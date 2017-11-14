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
import java.util.List;
import java.util.PriorityQueue;

import edu.ufl.cise.bsmock.graph.Edge;
import edu.ufl.cise.bsmock.graph.Graph;
import edu.ufl.cise.bsmock.graph.ksp.Eppstein;
import edu.ufl.cise.bsmock.graph.util.Path;
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

	private ArrayList<TreeKeeper<Symbol>> exploredTrees;

	public EppsteinRunner(ArrayList<TreeKeeper<Symbol>> exploredTrees) {
		this.exploredTrees = exploredTrees;
	}

	public ArrayList<LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>>>
			runEppstein(WTA wta, int k, TreeKeeper<Symbol> tree, State q) {

		ArrayList<LinkedHashMap<Node<Symbol>,TreeKeeper<Symbol>>>
				kBestTreesForEachQRule = new ArrayList<>();
		ArrayList<Rule> rules = wta.getTransitionFunction().
				getRulesByResultingState(q);

		for (Rule r : rules) {
			Graph<Node<Symbol>> graph = new Graph<>();
			Eppstein<Node<Symbol>> epp = new Eppstein<>();

			ArrayList<State> states = r.getStates();
			int nOfStates = states.size();

			NestedMap<String, String, PriorityQueue<Run>> edgeMap =
					buildEdges(states, tree);

			if (edgeMap.keySet().size() > nOfStates) {
				addKSmallestEdgesToGraph(graph, k, edgeMap, tree);

				List<Path<Node<Symbol>>> pathList = epp.ksp(graph, "u0",
						"v" + nOfStates, k);
				LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>> treeList =
						new LinkedHashMap<>();

				for (Path<Node<Symbol>> path : pathList) {
					Node<Symbol> node = extractTreeFromPath(path, r);
					TreeKeeper<Symbol> keeper = new TreeKeeper<>(node);
					Weight w = path.getTotalCost();
					w = w.add(r.getWeight());
					keeper.addWeight(q, w);
					treeList.put(keeper.getTree(), keeper);
				}

				kBestTreesForEachQRule.add(treeList);

			}
		}

		return kBestTreesForEachQRule;
	}

	private NestedMap<String, String, PriorityQueue<Run>> buildEdges(
			ArrayList<State> states, TreeKeeper<Symbol> tree) {

		NestedMap<String, String, PriorityQueue<Run>> edgeMap =
				new NestedMap<>();
		int nOfStates = states.size();

		for (int i = 1; i < nOfStates + 1; i++) {
			State currentState = states.get(i-1);

			for (TreeKeeper<Symbol> n : exploredTrees) {
				Weight w = n.getWeight(currentState);

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

	private void addKSmallestEdgesToGraph(Graph<Node<Symbol>> graph,
			int k, NestedMap<String, String, PriorityQueue<Run>> edgeMap,
			TreeKeeper<Symbol> tree) {

		int dummyCounter = 0;

		for (String vertex1 : edgeMap.keySet()) {

			for (String vertex2 : edgeMap.get(vertex1).keySet()) {
				PriorityQueue<Run> p = edgeMap.get(vertex1, vertex2);
				int counter = 0;

				while (!p.isEmpty() && counter < k) {
					Run run = p.poll();
					String dummyNode = "d" + dummyCounter;
					graph.addEdge(vertex1, dummyNode, new Weight(0), null);
					graph.addEdge(dummyNode, vertex2, run.getWeight(), run.getTree().getTree());
					dummyCounter++;
					counter++;
				}
			}
		}
	}

	private Node<Symbol> extractTreeFromPath(Path<Node<Symbol>> path,
			Rule r) {

		Node<Symbol> root = new Node<>(r.getSymbol());
		int counter = 0;

		for (Edge<Node<Symbol>> e : path.getEdges()) {

			if (counter%2 == 1) {
				root.addChild(e.getLabel());
			}

			counter++;
		}

		return root;
	}
}
