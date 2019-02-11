/*
 * Copyright 2018 Anna Jonsson for the research group Foundations of Language
 * Processing, Department of Computing Science, Ume� university
 *
 * This file is part of BestTrees.
 *
 * BestTrees is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BestTrees is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BestTrees.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.umu.cs.flp.aj.nbest.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.Symbol;
import se.umu.cs.flp.aj.nbest.wta.exceptions.DuplicateRuleException;

public class Hypergraph<N, E> {

//	private HashMap<N, Node> sourceNodes;
	private HashMap<N, Node> nodes;
	private HashMap<E, Edge> edges;


	public Hypergraph() {
//		this.sourceNodes = new HashMap<>();
		this.nodes = new HashMap<>();
		this.edges = new HashMap<>();
	}

	public boolean hasNode(N node) {
		return nodes.containsKey(node);
	}

	public boolean hasEdge(E edge) {
		return edges.containsKey(edge);
	}

	public void addNode(N node) {
		if (!nodes.containsKey(node)) {
			Node newNode = new Node(node);
			nodes.put(node, newNode);
//			sourceNodes.put(node, newNode);
		}
	}

	public void removeNode(N node) {

		if (nodes.containsKey(node)) {
			Node n = nodes.get(node);
			ArrayList<E> incoming = new ArrayList<>(n.getIncoming().keySet());
			ArrayList<E> outgoing = new ArrayList<>(n.getOutgoing().keySet());

			for (E edge : incoming) {
				removeEdge(edge);
			}

			for (E edge : outgoing) {
				removeEdge(edge);
			}

//			if (sourceNodes.containsKey(node)) {
//				sourceNodes.remove(node);
//			}

			nodes.remove(node);
		}
	}

	/* Only for testing */
	protected void addEdge(E edge, N toNode,
			@SuppressWarnings("unchecked") N ... fromNodes)
			throws DuplicateRuleException {
		addEdge(edge, toNode, new ArrayList<N>(Arrays.asList(fromNodes)));
	}

	public void addEdge(E edge, Weight weight, N toNode, ArrayList<N> fromNodes)
			throws DuplicateRuleException {

		if (edges.containsKey(edge)) {
			throw new DuplicateRuleException("Duplicate rule " + edge + "\n and " + edges.get(edge));
		}

		Edge e = new Edge(edge, weight);
		Node to = nodes.get(toNode);

		if (to == null) {
			addNode(toNode);
			to = nodes.get(toNode);
		}

//		if (sourceNodes.containsKey(toNode)) {
//			sourceNodes.remove(toNode);
//		}

		e.setTo(to);
		to.addIncoming(e);

		for (N from : fromNodes) {
			Node n = nodes.get(from);

			if (n == null) {
				addNode(from);
				n = nodes.get(from);
			}

			e.addFrom(n);
			n.addOutgoing(e);
		}

		edges.put(edge, e);
	}

	public void addEdge(E edge, N toNode, ArrayList<N> fromNodes)
			throws DuplicateRuleException {
		addEdge(edge, null, toNode, fromNodes);
	}

	public void removeEdge(E edge) {

		if (edges.containsKey(edge)) {
			Edge e = edges.get(edge);
			Node to = e.getTo();
			to.removeIncoming(e);

//		if (to.getIncoming().isEmpty() && !to.getOutgoing().isEmpty()) {
//			sourceNodes.put(to.getElement(), to);
//		}

			for (Node from : e.getFrom()) {
//if (from.getElement().toString().equals("DUMMY_SOURCE")) {
//System.out.println("removing " + e + " from dummy source");
//}
				from.removeOutgoing(e);
			}

			edges.remove(edge);
		}
	}

//	public ArrayList<N> getSourceNodes() {
//		return new ArrayList<>(sourceNodes.keySet());
//	}

//	public ArrayList<E> getSourceEdges() {
//		ArrayList<E> sourceEdges = new ArrayList<>();
//
//		for (Node n : sourceNodes.values()) {
//			HashMap<E, Edge> edgeList = n.getOutgoing();
//			sourceEdges.addAll(edgeList.keySet());
//		}
//
//		return sourceEdges;
//	}

	public ArrayList<N> getNodes() {
		return new ArrayList<>(nodes.keySet());
	}

	public ArrayList<E> getEdges() {
		return new ArrayList<>(edges.keySet());
	}

	public Weight getWeight(E edge) {
		return edges.get(edge).getWeight();
	}

	public ArrayList<E> getOutgoing(N node) {
		return new ArrayList<>(nodes.get(node).getOutgoing().keySet());
	}

	public ArrayList<E> getIncoming(N node) {
		return new ArrayList<>(nodes.get(node).getIncoming().keySet());
	}

	private class Node {
		private N element;
		private HashMap<E, Edge> in;
		private HashMap<E, Edge> out;

		public Node(N element) {
			this.element = element;
			this.in = new HashMap<>();
			this.out = new HashMap<>();
		}

		public N getElement() {
			return element;
		}

		public void addIncoming(Edge edge) {
			in.put(edge.getElement(), edge);
		}

		public void addOutgoing(Edge edge) {
			out.put(edge.getElement(), edge);
		}

		public void removeIncoming(Edge edge) {
			in.remove(edge.getElement());
		}

		public void removeOutgoing(Edge edge) {
			out.remove(edge.getElement());
		}

		public HashMap<E, Edge> getIncoming() {
			return in;
		}

		public HashMap<E, Edge> getOutgoing() {
			return out;
		}

		@Override
		public boolean equals(Object arg0) {
			if (arg0 instanceof Hypergraph.Node) {
				return element.equals(((Hypergraph<?,?>.Node) arg0).element);
			}
			return super.equals(arg0);
		}

		@Override
		public int hashCode() {
			return element.hashCode();
		}

		@Override
		public String toString() {
			return element.toString();
		}
	}

	private class Edge {
		private E element;
		private Weight weight;
		private ArrayList<Node> from;
		Node to;

		public Edge(E element) {
			this.element = element;
			this.from = new ArrayList<>();
			this.to = null;
			this.weight = null;
		}

		public Edge(E element, Weight weight) {
			this(element);
			this.weight = weight;
		}

		public E getElement() {
			return element;
		}

		public Weight getWeight() {
			return weight;
		}

		public void addFrom(Node prev) {
			this.from.add(prev);
		}

		public void setTo(Node next) {
			this.to = next;
		}

		public Node getTo() {
			return to;
		}

		public ArrayList<Node> getFrom() {
			return from;
		}

		@Override
		public boolean equals(Object arg0) {
			if (arg0 instanceof Hypergraph.Edge) {
				return element.equals(((Hypergraph<?,?>.Edge)arg0).element);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return element.hashCode();
		}

		@Override
		public String toString() {
			return element.toString();
		}
	}
}
