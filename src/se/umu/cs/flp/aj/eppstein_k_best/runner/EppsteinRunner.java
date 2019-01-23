/*
 * Copyright 2017 Anna Jonsson for the research group Foundations of Language
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

package se.umu.cs.flp.aj.eppstein_k_best.runner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import edu.ufl.cise.bsmock.graph.Edge;
import edu.ufl.cise.bsmock.graph.Graph;
import edu.ufl.cise.bsmock.graph.ksp.Eppstein;
import edu.ufl.cise.bsmock.graph.util.Path;
import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.Node;
import se.umu.cs.flp.aj.nbest.treedata.Run;
import se.umu.cs.flp.aj.nbest.treedata.TreeKeeper;
import se.umu.cs.flp.aj.nbest.util.NestedMap;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.WTA;

public class EppsteinRunner {

	private ArrayList<TreeKeeper> exploredTrees;


	public EppsteinRunner(ArrayList<TreeKeeper> exploredTrees) {
		this.exploredTrees = exploredTrees;
	}

	public ArrayList<LinkedHashMap<Node, TreeKeeper>>
			runEppstein(WTA wta, int k, TreeKeeper tree, State q) {

		ArrayList<LinkedHashMap<Node,TreeKeeper>>
				kBestTreesForEachQRule = new ArrayList<>();
		ArrayList<Rule> rules = wta.getRulesByResultingState(q);

		for (Rule r : rules) {
			Graph<Node> graph = new Graph<>(wta.getSemiring());
			Eppstein<Node> epp = new Eppstein<>(wta.getSemiring());

			ArrayList<State> states = r.getStates();
			int nOfStates = states.size();

			NestedMap<String, String, PriorityQueue<Run>> edgeMap =
					buildEdges(states, tree);

			if (edgeMap.keySet().size() > nOfStates) {
				addKSmallestEdgesToGraph(graph, k, edgeMap, tree,
						wta.getSemiring());

				List<Path<Node>> pathList = epp.ksp(graph, "u0",
						"v" + nOfStates, k);
				LinkedHashMap<Node, TreeKeeper> treeList =
						new LinkedHashMap<>();

				for (Path<Node> path : pathList) {
					Node node = extractTreeFromPath(path, r);
					TreeKeeper keeper = new TreeKeeper(node,
							wta.getSemiring());
					Weight w = path.getTotalCost();
					w = w.mult(r.getWeight());
					keeper.addStateWeight(q, w);
					treeList.put(keeper.getTree(), keeper);
				}

				kBestTreesForEachQRule.add(treeList);

			}
		}

		return kBestTreesForEachQRule;
	}

	private NestedMap<String, String, PriorityQueue<Run>> buildEdges(
			ArrayList<State> states, TreeKeeper tree) {

		NestedMap<String, String, PriorityQueue<Run>> edgeMap =
				new NestedMap<>();
		int nOfStates = states.size();

		for (int i = 1; i < nOfStates + 1; i++) {
			State currentState = states.get(i-1);

			for (TreeKeeper n : exploredTrees) {
				Weight w = n.getOptimalWeight(currentState);

				if (w != null) {
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
		}

		return edgeMap;
	}

	private void addKSmallestEdgesToGraph(Graph<Node> graph,
			int k, NestedMap<String, String, PriorityQueue<Run>> edgeMap,
			TreeKeeper tree, Semiring semiring) {

		int dummyCounter = 0;

		for (String vertex1 : edgeMap.keySet()) {

			for (String vertex2 : edgeMap.get(vertex1).keySet()) {
				PriorityQueue<Run> p = edgeMap.get(vertex1, vertex2);
				int counter = 0;

				while (!p.isEmpty() && counter < k) {
					Run run = p.poll();
					String dummyNode = "d" + dummyCounter;
					graph.addEdge(vertex1, dummyNode, semiring.one(), null);
					graph.addEdge(dummyNode, vertex2, run.getWeight(),
							run.getTree().getTree());
					dummyCounter++;
					counter++;
				}
			}
		}
	}

	private Node extractTreeFromPath(Path<Node> path,
			Rule r) {

		Node ruleTree = r.getTree();
//		Node root = new Node(ruleTree.getLabel());
		int counter = 0;

		LinkedList<Node> inputList = new LinkedList<>();

		for (Edge<Node> e : path.getEdges()) {

			if (counter%2 == 1) {
//				root.addChild(e.getLabel());
				inputList.add(e.getLabel());
			}

			counter++;
		}

		Node root = buildTree(ruleTree, inputList);

		return root;
	}

	private Node buildTree(Node t, LinkedList<Node> tklist) {

		if (t.getChildCount() == 0) {
			Node node;

			if (t.getLabel().isNonterminal()) {
				node = tklist.poll();
			} else {
				node = new Node(t.getLabel());
			}

			return node;
		}

		Node newTree = new Node(t.getLabel());

		for (int i = 0; i < t.getChildCount(); i++) {
			Node tempTree = buildTree(t.getChildAt(i), tklist);
			newTree.addChild(tempTree);
		}

		return newTree;
	}




}
