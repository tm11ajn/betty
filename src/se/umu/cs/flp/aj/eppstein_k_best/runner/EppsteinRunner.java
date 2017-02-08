/*
 * Copyright 2015 Anna Jonsson for the research group Foundations of Language
 * Processing, Department of Computing Science, Umeï¿½ university
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

package se.umu.cs.flp.aj.eppstein_k_best.runner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.PriorityQueue;

import se.umu.cs.flp.aj.eppstein_k_best.graph.Edge;
import se.umu.cs.flp.aj.eppstein_k_best.graph.Graph;
import se.umu.cs.flp.aj.eppstein_k_best.graph.Path;
import se.umu.cs.flp.aj.nbest.data.NestedMap;
import se.umu.cs.flp.aj.nbest.data.Node;
import se.umu.cs.flp.aj.nbest.data.Run;
import se.umu.cs.flp.aj.nbest.data.TreeKeeper;
import se.umu.cs.flp.aj.wta.Rule;
import se.umu.cs.flp.aj.wta.State;
import se.umu.cs.flp.aj.wta.Symbol;
import se.umu.cs.flp.aj.wta.WTA;
import se.umu.cs.flp.aj.wta.Weight;

public class EppsteinRunner {

//	private NestedMap<Node<Symbol>, State, Weight> treeStateValTable;
	private ArrayList<TreeKeeper<Symbol>> exploredTrees;

//	public EppsteinRunner(ArrayList<Node<Symbol>> exploredTrees,
//			NestedMap<Node<Symbol>, State, Weight> treeStateValTable) {
	public EppsteinRunner(ArrayList<TreeKeeper<Symbol>> exploredTrees) {		
		this.exploredTrees = exploredTrees;
//		this.treeStateValTable = treeStateValTable;
	}

	// memoisation here?

	
//	public ArrayList<LinkedList<TreeKeeper<Symbol>>> runEppstein(WTA wta,
//			int k, TreeKeeper<Symbol> tree, State q) {
	public ArrayList<LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>>> runEppstein(WTA wta,
			int k, TreeKeeper<Symbol> tree, State q) {
		

//		ArrayList<LinkedList<TreeKeeper<Symbol>>> kBestTreesForEachQRule =
//				new ArrayList<>();
		ArrayList<LinkedHashMap<Node<Symbol>,TreeKeeper<Symbol>>> kBestTreesForEachQRule =
				new ArrayList<>();

		Graph<Node<Symbol>> graph = new Graph<>();

		ArrayList<Rule> rules = wta.getTransitionFunction().
				getRulesByResultingState(q);

		for (Rule r : rules) {

			ArrayList<State> states = r.getStates();
			int nOfStates = states.size();

			String vertices = buildVertexString(nOfStates);
			graph.createVertices(vertices);

			NestedMap<String, String, PriorityQueue<Run>> edgeMap =
					buildEdgeMap(states, tree);
			addKSmallestEdgesToGraph(graph, k, edgeMap);

			Path<Node<Symbol>> path =
					graph.findShortestPath("u0", "v" + nOfStates);

//			LinkedList<TreeKeeper<Symbol>> treeList =
//					getKBestTreesForRule(graph, path, k, q, r);			
			LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>> treeList =
					getKBestTreesForRule(graph, path, k, q, r);

			kBestTreesForEachQRule.add(treeList);
		}

		return kBestTreesForEachQRule;
	}

	private String buildVertexString(int nOfStates) {
		String vertices = "";

		for (int i = 0; i < nOfStates + 1; i++) {
			vertices += "u" + i + "," + "v" + i;

			if (i != nOfStates) {
				vertices += ",";
			}
		}
		return vertices;
	}

	private NestedMap<String, String, PriorityQueue<Run>> buildEdgeMap(
			ArrayList<State> states, TreeKeeper<Symbol> tree) {

		NestedMap<String, String, PriorityQueue<Run>> edgeMap =
				new NestedMap<>();
		int nOfStates = states.size();

		for (int i = 1; i < nOfStates + 1; i++) {
			State currentState = states.get(i-1);

			for (TreeKeeper<Symbol> n : exploredTrees) {

//				Weight w = treeStateValTable.get(n, currentState);
				Weight w = n.getOptWeights().get(currentState);

				if (w == null) {
					continue;
				}

				PriorityQueue<Run> pu = null;
				PriorityQueue<Run> pv = edgeMap.get("v" + (i - 1), "v" + i);
				String resultingNodeType = "";

				if (!n.getTree().equals(tree.getTree())) {
					resultingNodeType = "u";
				} else {
					resultingNodeType = "v";
				}

				pu = edgeMap.get("u" + (i - 1), resultingNodeType + i);

				if (pu == null) {
					pu = new PriorityQueue<>(nOfStates);
					edgeMap.put("u" + (i - 1), resultingNodeType + i, pu);
				}

				if (pv == null) {
					pv = new PriorityQueue<>(nOfStates);
					edgeMap.put("v" + (i - 1), "v" + i, pv);
				}

				pu.add(new Run(n, w));
				pv.add(new Run(n, w));
			}
		}

		return edgeMap;
	}

	private void addKSmallestEdgesToGraph(Graph<Node<Symbol>> graph,
			int k, NestedMap<String, String, PriorityQueue<Run>> edgeMap) {

		for (String vertex1 : edgeMap.keySet()) {

			for (String vertex2 : edgeMap.get(vertex1).keySet()) {

				PriorityQueue<Run> p = edgeMap.get(vertex1, vertex2);
				int counter = 0;

				while (!p.isEmpty() && counter < k) {
					Run run = p.poll();
					graph.createEdge(vertex1, vertex2, run.getTree().getTree(),
							Double.parseDouble(run.getWeight().toString()));
					counter++;
				}
			}
		}
	}

	private LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>> getKBestTreesForRule(
			Graph<Node<Symbol>> graph, Path<Node<Symbol>> path,
			int k, State q, Rule r) {
//	private LinkedList<TreeKeeper<Symbol>> getKBestTreesForRule(
//			Graph<Node<Symbol>> graph, Path<Node<Symbol>> path,
//			int k, State q, Rule r) {

//		LinkedList<TreeKeeper<Symbol>> treeList = new LinkedList<>();
		
//System.out.println(">>> Getting trees for rule " + r);
		
		LinkedHashMap<Node<Symbol>,TreeKeeper<Symbol>> treeList = new LinkedHashMap<>();

		int counter = 0;

		while (path.isValid() && counter < k) {
			Node<Symbol> pathTree = extractTreeFromPath(path, r);
			Weight pathWeight = new Weight(path.getWeight());

//System.out.println(">>> Tree output: " + pathTree + " with weight " + pathWeight);
			
			if (path.getWeight() == Double.MAX_VALUE) {
				pathWeight = new Weight(Weight.INF);
			}

			pathWeight = pathWeight.add(r.getWeight());
			
////			Weight oldWeight = treeStateValTable.get(pathTree, q);
//			Weight oldWeight = pathTree.getOptWeights().get(q);
//
//			if (oldWeight == null
//					|| oldWeight.compareTo(pathWeight) == 1) {
////				treeStateValTable.put(pathTree, q, pathWeight);
//				pathTree.getOptWeights().put(q, pathWeight);
//			}
			
//			TreeKeeper<Symbol> keeper = null;
			TreeKeeper<Symbol> keeper = new TreeKeeper<>(pathTree); // Check if tree already in list and merge trees if that is the case?
			keeper.getOptWeights().put(q, pathWeight);
			
//			if (treeList.containsKey(pathTree)) {
//				System.out.println("HÄR ÄR JAG IGEN");
//			}
			
			/* New part*/
//			if (treeList.containsKey(pathTree)) {
//				keeper = treeList.get(pathTree);
//				
//				if (keeper.getWeight().compareTo(pathWeight) == 1) { // We do not use getWeight
//					keeper.getOptWeights().put(q, pathWeight);
//				}
//				
//			} else {
//				keeper = new TreeKeeper<>(pathTree);
//				keeper.getOptWeights().put(q, pathWeight);
//			}

//			treeList.add(pathTree);
//			treeList.add(keeper);
			treeList.put(pathTree, keeper);
			counter++;

			path = graph.findNextShortestPath();
		}

		return treeList;
	}

	private Node<Symbol> extractTreeFromPath(Path<Node<Symbol>> path,
			Rule r) {

		Node<Symbol> root = new Node<>(r.getSymbol());

		for (Edge<Node<Symbol>> e : path) {
			root.addChild(e.getLabel());
		}

		return root;
	}
}
