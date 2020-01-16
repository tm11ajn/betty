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
import java.util.LinkedList;

public class Hypergraph<N extends Hypergraph.Node<E>,E extends Hypergraph.Edge<N>> {

//	private static int nodeID = 0;
//	private static int edgeID = 0;

	private int nodeCount;
	private int edgeCount;

	public Hypergraph() {
		this.nodeCount = 0;
		this.edgeCount = 0;
	}

//	public static void resetClass() {
//		nodeID = 0;
//		edgeID = 0;
//	}

//	private static int getNodeID() {
//		return nodeID++;
//	}
//
//	private static int getEdgeID() {
//		return edgeID++;
//	}

//	public void addNode(N node) {
////		if (node.getID() == -1) {
////			node.setID(nodeID);
////			nodeID++;
////			nodeCount++;
////		}
//
//		nodeCount++;
//	}

//	public void removeNode(N node) {
//
//		if (node.getID() != -1) {
//			ArrayList<E> incoming = new ArrayList<>(node.getIncoming());
//			ArrayList<E> outgoing = new ArrayList<>(node.getOutgoing());
//
//			for (E edge : incoming) {
//				removeEdge(edge);
//			}
//
//			for (E edge : outgoing) {
//				removeEdge(edge);
//			}
//
//			node.setID(-1);
//			nodeCount--;
//		}
//	}

	/* Only for testing */
	protected void addEdge(E edge, N toNode,
			@SuppressWarnings("unchecked") N ... fromNodes) {
		addEdge(edge, toNode, new ArrayList<N>(Arrays.asList(fromNodes)));
	}
	
	public void addNode(N node) {
		node.unused = false;
		node.setID(nodeCount);
		nodeCount++;
	}

	public void addEdge(E edge, N toNode, ArrayList<N> fromNodes) {

//		if (edge.getID() == -1) {
//			edge.setID(edgeID);
//			edgeID++;
//			edgeCount++;
//
//			if (toNode.getID() == -1) {
//				addNode(toNode);
//			}
//
//			edge.setTo(toNode);
//			toNode.addIncoming(edge);
//
//			for (N from : fromNodes) {
//
//				if (from.getID() == -1) {
//					addNode(from);
//				}
//
//				edge.addFrom(from);
//				from.addOutgoing(edge);
//			}
//		}

		if (toNode.unused) {
			addNode(toNode);
		}

		edge.setTo(toNode);
		toNode.addIncoming(edge);

		for (N from : fromNodes) {

			if (from.unused) {
				addNode(from);
			}

			edge.addFrom(from);
			from.addOutgoing(edge);
		}

		edge.setID(edgeCount);
		edgeCount++;
	}

//	public void removeEdge(E edge) {
//
//		if (edge.getID() != -1) {
//			N to = edge.getTo();
//			to.removeIncoming(edge);
//
//			for (N from : edge.getFrom()) {
//				from.removeOutgoing(edge);
//			}
//
//			edge.setID(-1);
//			edgeCount--;
//		}
//	}

	public int getNodeCount() {
		return nodeCount;
	}

	public int getEdgeCount() {
		return edgeCount;
	}

	public static class Node<E> {
		private int id;
		private LinkedList<E> in;
		private LinkedList<E> out;
		boolean unused;

		public Node() {
			this.id = -1;
			unused = true;
//			this.id = getNodeID();
			this.in = new LinkedList<>();
			this.out = new LinkedList<>();
		}

		void setID(int id) {
			this.id = id;
		}

		public int getID() {
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

		public LinkedList<E> getIncoming() {
			return in;
		}

		public LinkedList<E> getOutgoing() {
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
		private LinkedList<N> from;
		N to;

		public Edge() {
			this.id = -1;
//			this.id = getEdgeID();
			this.from = new LinkedList<>();
			this.to = null;
		}

		void setID(int id) {
			this.id = id;
		}

		public int getID() {
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

		public LinkedList<N> getFrom() {
			return from;
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof Edge &&
					((Edge<?>) o).id == id;
		}
	}
}
