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

public class Hypergraph<N extends Hypergraph.Node<E>,E extends Hypergraph.Edge<N>> {

////	private HashMap<N, Node> sourceNodes;
//	private HashMap<N, Node> nodes;
//	private HashMap<E, Edge> edges;

	private int nodeID;
	private int edgeID;

	private int nodeCount;
	private int edgeCount;

	public Hypergraph() {
////		this.sourceNodes = new HashMap<>();
//		this.nodes = new HashMap<>();
//		this.edges = new HashMap<>();
		this.nodeID = 0;
		this.edgeID = 0;
		this.nodeCount = 0;
		this.edgeCount = 0;
	}

//	public boolean hasNode(N node) {
//		return nodes.containsKey(node);
//	}

//	public boolean hasEdge(E edge) {
//		return edges.containsKey(edge);
//	}

//	public void addNode(N node) {
//		if (!nodes.containsKey(node)) {
//			Node newNode = new Node(node);
//			nodes.put(node, newNode);
////			sourceNodes.put(node, newNode);
//		}
//	}

	public void addNode(N node) {
		if (node.getID() == -1) {
			node.setID(nodeID);
			nodeID++;
			nodeCount++;
		}
	}

	public void removeNode(N node) {

		if (node.getID() != -1) {
			ArrayList<E> incoming = new ArrayList<>(node.getIncoming());
			ArrayList<E> outgoing = new ArrayList<>(node.getOutgoing());

			for (E edge : incoming) {
				removeEdge(edge);
			}

			for (E edge : outgoing) {
				removeEdge(edge);
			}

			node.setID(-1);
			nodeCount--;

//			if (sourceNodes.containsKey(node)) {
//				sourceNodes.remove(node);
//			}

//			nodes.remove(node);
		}
	}

//	public void removeNode(N node) {
//
//		if (nodes.containsKey(node)) {
//			Node n = nodes.get(node);
//			ArrayList<E> incoming = new ArrayList<>(n.getIncoming().keySet());
//			ArrayList<E> outgoing = new ArrayList<>(n.getOutgoing().keySet());
//
//			for (E edge : incoming) {
//				removeEdge(edge);
//			}
//
//			for (E edge : outgoing) {
//				removeEdge(edge);
//			}
//
////			if (sourceNodes.containsKey(node)) {
////				sourceNodes.remove(node);
////			}
//
//			nodes.remove(node);
//		}
//	}

//	/* Only for testing */
//	protected void addEdge(E edge, N toNode,
//			@SuppressWarnings("unchecked") N ... fromNodes)
//			throws DuplicateRuleException {
//		addEdge(edge, toNode, new ArrayList<N>(Arrays.asList(fromNodes)));
//	}

	/* Only for testing */
	protected void addEdge(E edge, N toNode,
			@SuppressWarnings("unchecked") N ... fromNodes) {
		addEdge(edge, toNode, new ArrayList<N>(Arrays.asList(fromNodes)));
	}

//	public void addEdge(E edge, Weight weight, N toNode, ArrayList<N> fromNodes)
//			throws DuplicateRuleException {
	public void addEdge(E edge, N toNode, ArrayList<N> fromNodes) {

		//		if (edges.containsKey(edge)) {
		//			throw new DuplicateRuleException("Duplicate rule " + edge + "\n and " + edges.get(edge));
		//		}


		//		Edge e = new Edge(edge, weight);
		//		Node to = nodes.get(toNode);

		//		if (to == null) {
		//			addNode(toNode);
		//			to = nodes.get(toNode);
		//		}

		if (edge.getID() == -1) {

			edge.setID(edgeID);
			edgeID++;
			edgeCount++;

			if (toNode.getID() == -1) {
				addNode(toNode);
			}

			//		if (sourceNodes.containsKey(toNode)) {
			//			sourceNodes.remove(toNode);
			//		}

			//		e.setTo(to);
			//		to.addIncoming(e);

			edge.setTo(toNode);
			toNode.addIncoming(edge);

			//		for (N from : fromNodes) {
			//			Node n = nodes.get(from);
			//
			//			if (n == null) {
			//				addNode(from);
			//				n = nodes.get(from);
			//			}
			//
			//			e.addFrom(n);
			//			n.addOutgoing(e);
			//		}

			for (N from : fromNodes) {

				if (from.getID() == -1) {
					addNode(from);
				}

				edge.addFrom(from);
				from.addOutgoing(edge);
			}
		}
//		edges.put(edge, e);
	}

//	public void addEdge(E edge, N toNode, ArrayList<N> fromNodes)
//			throws DuplicateRuleException {
//		addEdge(edge, null, toNode, fromNodes);
//	}

//	public void removeEdge(E edge) {
//
//		if (edges.containsKey(edge)) {
//			Edge e = edges.get(edge);
//			Node to = e.getTo();
//			to.removeIncoming(e);
//
////		if (to.getIncoming().isEmpty() && !to.getOutgoing().isEmpty()) {
////			sourceNodes.put(to.getElement(), to);
////		}
//
//			for (Node from : e.getFrom()) {
////if (from.getElement().toString().equals("DUMMY_SOURCE")) {
////System.out.println("removing " + e + " from dummy source");
////}
//				from.removeOutgoing(e);
//			}
//
//			edges.remove(edge);
//		}
//	}

	public void removeEdge(E edge) {

		if (edge.getID() != -1) {
			N to = edge.getTo();
			to.removeIncoming(edge);

			for (N from : edge.getFrom()) {
				from.removeOutgoing(edge);
			}

			edge.setID(-1);
			edgeCount--;
		}
	}

	public int getNodeCount() {
		return nodeCount;
	}

	public int getEdgeCount() {
		return edgeCount;
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

//	public ArrayList<N> getNodes() {
//		return new ArrayList<>(nodes.keySet());
//	}

//	public ArrayList<E> getEdges() {
//		return new ArrayList<>(edges.keySet());
//	}

//	public Weight getWeight(E edge) {
//		return edges.get(edge).getWeight();
//	}

//	public ArrayList<E> getOutgoing(N node) {
//		return new ArrayList<>(nodes.get(node).getOutgoing().keySet());
//	}
//
//	public ArrayList<E> getIncoming(N node) {
//		return new ArrayList<>(nodes.get(node).getIncoming().keySet());
//	}

	public static class Node<E> {
//		private N element;
//		private HashMap<E, Edge> in;
//		private HashMap<E, Edge> out;

		private int id;
		private ArrayList<E> in;
		private ArrayList<E> out;
//		private HashMap<E, E> in;
//		private HashMap<E, E> out;

//		public Node(N element) {
		public Node() {
//			this.element = element;
//			this.in = new HashMap<>();
//			this.out = new HashMap<>();
			this.id = -1;
			this.in = new ArrayList<>();
			this.out = new ArrayList<>();
//			this.in = new HashMap<>();
//			this.out = new HashMap<>();
		}

//		public Node() {
//			this.id = -1;
//		}

		void setID(int id) {
			this.id = id;
		}

		protected int getID() {
			return id;
		}

//		public N getElement() {
//			return element;
//		}

		public void addIncoming(E edge) {
//			in.put(edge.getElement(), edge);
			in.add(edge);
//			in.put(edge, null);
		}

		public void addOutgoing(E edge) {
//			out.put(edge.getElement(), edge);
			out.add(edge);
//			out.put(edge, null);
		}

		public void removeIncoming(E edge) {
//			in.remove(edge.getElement());
			in.remove(edge);
		}

		public void removeOutgoing(E edge) {
//			out.remove(edge.getElement());
			out.remove(edge);
		}

//		public HashMap<E, Edge> getIncoming() {
		public ArrayList<E> getIncoming() {
			return in;
//			return new ArrayList<>(in.keySet());
		}

//		public HashMap<E, Edge> getOutgoing() {
		public ArrayList<E> getOutgoing() {
			return out;
//			return new ArrayList<>(out.keySet());
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof Node &&
					((Node<?>) o).id == id;
		}

//		@Override
//		public boolean equals(Object arg0) {
//			if (arg0 instanceof Hypergraph.Node) {
//				return element.equals(((Hypergraph<?,?>.Node) arg0).element);
//			}
//			return super.equals(arg0);
//		}
//
//		@Override
//		public int hashCode() {
//			return element.hashCode();
//		}
//
//		@Override
//		public String toString() {
//			return element.toString();
//		}
	}

	public static class Edge<N> {
//		private E element;
//		private Weight weight;

		private int id;
		private ArrayList<N> from;
		N to;

//		public Edge(E element) {
		public Edge() {
//			this.element = element;
			this.id = -1;
			this.from = new ArrayList<>();
			this.to = null;
//			this.weight = null;
		}

		void setID(int id) {
			this.id = id;
		}

		protected int getID() {
			return id;
		}

//		public Edge(E element, Weight weight) {
//			this(element);
//			this.weight = weight;
//		}

//		public E getElement() {
//			return element;
//		}

//		public Weight getWeight() {
//			return weight;
//		}

		public void addFrom(N prev) {
			this.from.add(prev);
		}

		public void setTo(N next) {
			this.to = next;
		}

		public N getTo() {
			return to;
		}

		public ArrayList<N> getFrom() {
			return from;
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof Edge &&
					((Edge<?>) o).id == id;
		}

//		@Override
//		public boolean equals(Object arg0) {
//			if (arg0 instanceof Hypergraph.Edge) {
//				return element.equals(((Hypergraph<?,?>.Edge)arg0).element);
//			}
//			return false;
//		}
//
//		@Override
//		public int hashCode() {
//			return element.hashCode();
//		}
//
//		@Override
//		public String toString() {
//			return element.toString();
//		}
	}
}
