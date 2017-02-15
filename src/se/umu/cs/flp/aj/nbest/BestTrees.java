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

package se.umu.cs.flp.aj.nbest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import se.umu.cs.flp.aj.eppstein_k_best.runner.EppsteinRunner;
import se.umu.cs.flp.aj.eppstein_k_best.runner.EppsteinRunner2;
import se.umu.cs.flp.aj.nbest.data.Node;
import se.umu.cs.flp.aj.nbest.data.PruneableQueue;
import se.umu.cs.flp.aj.nbest.data.TreeKeeper;
import se.umu.cs.flp.aj.nbest.data.TreePruner;
import se.umu.cs.flp.aj.wta.Rule;
import se.umu.cs.flp.aj.wta.State;
import se.umu.cs.flp.aj.wta.Symbol;
import se.umu.cs.flp.aj.wta.WTA;
import se.umu.cs.flp.aj.wta.Weight;


public class BestTrees {

	private static ArrayList<TreeKeeper<Symbol>> exploredTrees; // T
	private static PruneableQueue<TreeKeeper<Symbol>,Weight> treeQueue; // K
	
	private static int eppCounter = 0;

	public static void setSmallestCompletions(
			HashMap<State, Weight> smallestCompletions) {
		TreeKeeper.init(smallestCompletions);
	}

	public static List<String> run(WTA wta, int N) {

		/* For result. */
		List<String> nBest = new ArrayList<String>(); // Perhaps just output instead

		// T <- empty. K <- empty
		exploredTrees = new ArrayList<TreeKeeper<Symbol>>();
		treeQueue = new PruneableQueue<TreeKeeper<Symbol>,Weight>(new TreePruner<Symbol,Weight>(N));

		// enqueue(K, Sigma_0)
		enqueueRankZeroSymbols(wta, N);

//System.out.println("Queue after enqueing rank zero symbols: " + treeQueue);

		// i <- 0
		int counter = 0;

		// while i < N and K nonempty do
		while (counter < N && !treeQueue.isEmpty()) {

			// t <- dequeue(K)
			TreeKeeper<Symbol> currentTree = treeQueue.pollFirstEntry().getKey();

//System.out.println("Current tree: " + currentTree);

			// T <- T u {t}
			exploredTrees.add(currentTree);

//System.out.println("Explored trees: " + exploredTrees);

			// if M(t) = delta(t) then
			if (currentTree.getSmallestWeight().equals(currentTree.getDeltaWeight())) {

				// output(t)
				nBest.add(currentTree.getTree().toString() + " " +
						currentTree.getDeltaWeight().toString());
//System.out.println("Outputting " + currentTree + "with weight " + getDeltaWeight(currentTree).toString());

				// i <- i + 1
				counter++;
			}

			// prune(T, enqueue(K, expand(T, t)))
			enqueueWithExpansionAndPruning(wta, N, currentTree);

//System.out.println("Queue after expansion and pruning: " + treeQueue);
		}

		return nBest;
	}

	private static void enqueueRankZeroSymbols(WTA wta, int N) {

		ArrayList<Symbol> symbols = wta.getSymbols();

		for (Symbol s : symbols) {

			if (s.getRank() == 0) {
				Node<Symbol> node = new Node<Symbol>(s);
				TreeKeeper<Symbol> tree = new TreeKeeper<>(node);

				ArrayList<Rule> rules = wta.getTransitionFunction().
						getRulesBySymbol(s);

				for (Rule r : rules) {
					tree.addWeight(r.getResultingState(), r.getWeight());
				}

				treeQueue.put(tree, null);

//System.out.println(treeQueue);

			}
		}

//System.out.println("END enqueue rank zero symbols");
	}

	public static void enqueueWithExpansionAndPruning(WTA wta, int N,
			TreeKeeper<Symbol> tree) {

		HashMap<State, ArrayList<LinkedHashMap<Node<Symbol>,TreeKeeper<Symbol>>>> allRuns =
				new HashMap<>();
		HashMap<State, LinkedHashMap<Node<Symbol>,TreeKeeper<Symbol>>> nRuns = new HashMap<>();

		EppsteinRunner eRunner = new EppsteinRunner(exploredTrees);
		
		EppsteinRunner2 eRunner2 = new EppsteinRunner2(exploredTrees);


//		long startTime;
//		long endTime;
//		long duration;
//		
//		startTime = System.nanoTime();
		
		
		
		for (State q : wta.getStates()) {
			allRuns.put(q, eRunner.runEppstein(wta, N, tree, q));		
		}
		
		
		if (eppCounter > 100) {

System.out.println("epCounter=" + eppCounter);

		for (State q : wta.getStates()) {
q = wta.getStates().get(1); // TODO REMOVE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			allRuns.put(q, eRunner2.runEppstein(wta, N, tree, q));		
		}
		
		}
		
		eppCounter++;
		
//		endTime = System.nanoTime();
//		duration = (endTime - startTime)/1000000;
//		System.out.println("Eppstein took " + duration + 
//			" milliseconds for k=" + N);

		for (Entry<State, ArrayList<LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>>>> e :
			allRuns.entrySet()) {

			LinkedHashMap<Node<Symbol>,TreeKeeper<Symbol>> mergedTreeList = new LinkedHashMap<>();
			State q = e.getKey();

			for (LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>> treeList : e.getValue()) {
				mergedTreeList = SortedListMerger.mergeTreeListsForState(treeList,
						mergedTreeList, N, q);
			}

			nRuns.put(q, mergedTreeList);
		}

		LinkedHashMap<Node<Symbol>,TreeKeeper<Symbol>> mergedList = new LinkedHashMap<>();
		int nOfStatesInWTA = wta.getStates().size();

		for (LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>> currentList : nRuns.values()) {
			mergedList = SortedListMerger.mergeTreeListsByDeltaWeights(currentList, mergedList,
					N*nOfStatesInWTA); // Unnecessary? Just insert them all into K and prune after each insertion?
		}

		for (TreeKeeper<Symbol> n : mergedList.values()) {
			treeQueue.put(n, null);
		}
	}


}
