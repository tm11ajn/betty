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

	private int nodeID;
	private int edgeID;

	private int nodeCount;
	private int edgeCount;

	public Hypergraph() {
		this.nodeID = 0;
		this.edgeID = 0;
		this.nodeCount = 0;
		this.edgeCount = 0;
	}

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
		}
	}

	/* Only for testing */
	protected void addEdge(E edge, N toNode,
			@SuppressWarnings("unchecked") N ... fromNodes) {
		addEdge(edge, toNode, new ArrayList<N>(Arrays.asList(fromNodes)));
	}

	public void addEdge(E edge, N toNode, ArrayList<N> fromNodes) {

		if (edge.getID() == -1) {
			edge.setID(edgeID);
			edgeID++;
			edgeCount++;

			if (toNode.getID() == -1) {
				addNode(toNode);
			}

			edge.setTo(toNode);
			toNode.addIncoming(edge);

			for (N from : fromNodes) {

				if (from.getID() == -1) {
					addNode(from);
				}

				edge.addFrom(from);
				from.addOutgoing(edge);
			}
		}
	}

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

	public static class Node<E> {
		private int id;
		private ArrayList<E> in;
		private ArrayList<E> out;

		public Node() {
			this.id = -1;
			this.in = new ArrayList<>();
			this.out = new ArrayList<>();
		}

		void setID(int id) {
			this.id = id;
		}

		protected int getID() {
			return id;
		}

		public void addIncoming(E edge) {
			in.add(edge);
		}

		public void addOutgoing(E edge) {
			out.add(edge);
		}

		public void removeIncoming(E edge) {
			in.remove(edge);
		}

		public void removeOutgoing(E edge) {
			out.remove(edge);
		}

		public ArrayList<E> getIncoming() {
			return in;
		}

		public ArrayList<E> getOutgoing() {
			return out;
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof Node &&
					((Node<?>) o).id == id;
		}
	}

	public static class Edge<N> {
		private int id;
		private ArrayList<N> from;
		N to;

		public Edge() {
			this.id = -1;
			this.from = new ArrayList<>();
			this.to = null;
		}

		void setID(int id) {
			this.id = id;
		}

		protected int getID() {
			return id;
		}

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
	}
}
