/*
 * Copyright 2017 Anna Jonsson for the research group Foundations of Language
 * Processing, Department of Computing Science, Ume� university
 *
 * This file is part of Betty.
 *
 * Betty is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Betty is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.umu.cs.flp.aj.nbest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import se.umu.cs.flp.aj.eppstein_k_best.runner.EppsteinRunner;
import se.umu.cs.flp.aj.nbest.helpers.SortedListMerger;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.Context;
import se.umu.cs.flp.aj.nbest.treedata.Node;
import se.umu.cs.flp.aj.nbest.treedata.Tree1;
import se.umu.cs.flp.aj.nbest.treedata.TreePruner;
import se.umu.cs.flp.aj.nbest.util.PruneableQueue;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.Symbol;
import se.umu.cs.flp.aj.nbest.wta.WTA;


public class BestTrees {

	private static ArrayList<Tree1> exploredTrees; // T
	private static PruneableQueue<Tree1,Weight> treeQueue; // K


//	public static void setSmallestCompletions(
//			HashMap<State, Weight> smallestCompletions) {
//	public static void setSmallestCompletions(Context[] smallestCompletions) {
//		TreeKeeper.init(smallestCompletions);
//	}

	public static List<String> run(WTA wta, Context[] bestContexts, int N) {
		Tree1.init(bestContexts);

		/* For result. */
		List<String> nBest = new ArrayList<String>();

		// T <- empty. K <- empty
		exploredTrees = new ArrayList<Tree1>();
		treeQueue = new PruneableQueue<Tree1,Weight>(new TreePruner<Weight>(N));

		// enqueue(K, Sigma_0)
		enqueueRankZeroSymbols(wta, N);

		// i <- 0
		int counter = 0;

		// while i < N and K nonempty do
		while (counter < N && !treeQueue.isEmpty()) {

			// t <- dequeue(K)
			Tree1 currentTree = treeQueue.pollFirstEntry().getKey();

			// T <- T u {t}
			exploredTrees.add(currentTree);

			// if M(t) = delta(t) then
			if (exploredTrees.contains(currentTree) && currentTree.getSmallestWeight().equals(currentTree.getDeltaWeight())) {

				// output(t)
				String outputString;
				
				if (wta.isGrammar()) {
					outputString = currentTree.getNode().toRTGString();
				} else {
					outputString = currentTree.getNode().toWTAString();
				}
				
				outputString = outputString + " # " +
						currentTree.getDeltaWeight().toString();

//				if (forDerivations) {
//					outputString = outputString.replaceAll("//rule[0-9]*", "");
//				}
				
				nBest.add(outputString);

				// i <- i + 1
				counter++;
			}

			// prune(T, enqueue(K, expand(T, t)))
			// problem reduced to N - i best trees
			if (counter < N) {
				enqueueWithExpansionAndPruning(wta, N - counter, currentTree);
			}
		}

		return nBest;
	}

	private static void enqueueRankZeroSymbols(WTA wta, int N) {
		HashMap<Symbol, Tree1> trees = new HashMap<>();

		/* TODO: Här köar vi på även de regler som inte leder någonstans,
		 * vilket senare ger problem i Eppstein. */
		for (Rule r : wta.getSourceRules()) {
			Symbol symbol = r.getTree().getLabel();
			Tree1 tree = null;

			if (trees.containsKey(symbol)) {
				tree = trees.get(symbol);
			} else {
				tree = new Tree1(r.getTree(), wta.getSemiring());
				trees.put(symbol, tree);
			}
//System.out.println(r.getResultingState().toString());
//if (r.getResultingState().toString().equals("in[6,6]_in_piat-hd-dat.pl.fem")) {
//System.out.println("NOOOO");
//System.exit(1);
//}

			tree.addStateWeight(r.getResultingState(),
					r.getWeight().duplicate());
		}

		for (Tree1 t : trees.values()) {
			treeQueue.put(t, null);
		}
	}

	public static void enqueueWithExpansionAndPruning(WTA wta, int N,
			Tree1 tree) {

		HashMap<State, ArrayList<LinkedHashMap<Node,Tree1>>> allRuns =
				new HashMap<>();
		HashMap<State, LinkedHashMap<Node,Tree1>> nRuns = new HashMap<>();

		EppsteinRunner eRunner = new EppsteinRunner(exploredTrees);

		for (State q : wta.getStates().values()) {
//System.out.println("Current state: " + q);
			allRuns.put(q, eRunner.runEppstein(wta, N, tree, q));
		}

		for (Entry<State, ArrayList<LinkedHashMap<Node,
				Tree1>>> e : allRuns.entrySet()) {

			LinkedHashMap<Node,Tree1> mergedTreeList = new LinkedHashMap<>();
			State q = e.getKey();

			for (LinkedHashMap<Node, Tree1> treeList : e.getValue()) {
				mergedTreeList = SortedListMerger.mergeTreeListsForState(treeList,
						mergedTreeList, N, q);
			}

			nRuns.put(q, mergedTreeList);
		}

		LinkedHashMap<Node,Tree1> mergedList = new LinkedHashMap<>();
		int nOfStatesInWTA = wta.getStates().size();

		for (LinkedHashMap<Node, Tree1> currentList : nRuns.values()) {
			mergedList = SortedListMerger.mergeTreeListsByDeltaWeights(currentList, mergedList,
					N*nOfStatesInWTA);
		}

		for (Tree1 n : mergedList.values()) {
			treeQueue.put(n, null);
		}
	}
}
