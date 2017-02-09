/*
 * Copyright 2015 Anna Jonsson for the research group Foundations of Language
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import se.umu.cs.flp.aj.eppstein_k_best.runner.EppsteinRunner;
import se.umu.cs.flp.aj.nbest.data.Node;
import se.umu.cs.flp.aj.nbest.data.PrunedQueue;
import se.umu.cs.flp.aj.nbest.data.TreeComparator;
import se.umu.cs.flp.aj.nbest.data.TreeKeeper;
import se.umu.cs.flp.aj.nbest.data.TreePruner;
import se.umu.cs.flp.aj.wta.Rule;
import se.umu.cs.flp.aj.wta.State;
import se.umu.cs.flp.aj.wta.Symbol;
import se.umu.cs.flp.aj.wta.WTA;
import se.umu.cs.flp.aj.wta.Weight;


public class BestTrees {

	private static ArrayList<TreeKeeper<Symbol>> exploredTrees; // T
	private static PrunedQueue<TreeKeeper<Symbol>,Weight> treeQueue; // K

	public static void setSmallestCompletions(
			HashMap<State, Weight> smallestCompletions) {
//		smallestCompletionWeights = smallestCompletions;
		TreeKeeper.init(smallestCompletions);
	}

	public static List<String> run(WTA wta, int N) {

		/* For result. */
		List<String> nBest = new ArrayList<String>(); // Perhaps just output instead

		// T <- empty. K <- empty
		exploredTrees = new ArrayList<TreeKeeper<Symbol>>();
		treeQueue = new PrunedQueue<TreeKeeper<Symbol>,Weight>(new TreeComparator<Symbol>(), 
				new TreePruner<Symbol,Weight>(N));

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

			// Get optimal state for current tree
			State optimalState = currentTree.getOptimalStates().keySet().iterator().next();

			// if M(t) = delta(t) then
			if (optimalState.isFinal()) {

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

		for (State q : wta.getStates()) {
			allRuns.put(q, eRunner.runEppstein(wta, N, tree, q));
		}

		for (Entry<State, ArrayList<LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>>>> e :
			allRuns.entrySet()) {

			LinkedHashMap<Node<Symbol>,TreeKeeper<Symbol>> mergedTreeList = new LinkedHashMap<>();
			State q = e.getKey();
			
			for (LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>> treeList : e.getValue()) {
				mergedTreeList = mergeTreeListsForState(treeList,
						mergedTreeList, N, q);
			}

			nRuns.put(q, mergedTreeList);
		}

		LinkedHashMap<Node<Symbol>,TreeKeeper<Symbol>> mergedList = new LinkedHashMap<>();
		int nOfStatesInWTA = wta.getStates().size();

		for (LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>> currentList : nRuns.values()) {
			mergedList = mergeTreeListsByDeltaWeights(currentList, mergedList, 
					N*nOfStatesInWTA); // Unnecessary? Just insert them all into K and prune after each insertion?
		}

		for (TreeKeeper<Symbol> n : mergedList.values()) {
			treeQueue.put(n, null);
//			insertIntoQueueUsingPruning(n, N);
		}
	}
	
	private static LinkedHashMap<Node<Symbol>,TreeKeeper<Symbol>> mergeTreeListsForState(
			LinkedHashMap<Node<Symbol>,TreeKeeper<Symbol>> list1, 
			LinkedHashMap<Node<Symbol>,TreeKeeper<Symbol>> list2,
			int listSizeLimit, State q) {

//System.out.println("MERGE FOR STATE " + q);
		
		if (list1.isEmpty()) {
//System.out.println("Merge for state: returning: " + list2);
			return list2;
		}
		
		if (list2.isEmpty()) {
//System.out.println("Merge for state: returning: " + list1);
			return list1;
		}
		
		LinkedHashMap<Node<Symbol>,TreeKeeper<Symbol>> result = new LinkedHashMap<>();

		int added = 0;
		int compResult;
		
		Iterator<Entry<Node<Symbol>, TreeKeeper<Symbol>>> iterator1 = list1.entrySet().iterator();
		Iterator<Entry<Node<Symbol>, TreeKeeper<Symbol>>> iterator2 = list2.entrySet().iterator();
		
		Entry<Node<Symbol>, TreeKeeper<Symbol>> currentEntry1 = null;
		Entry<Node<Symbol>, TreeKeeper<Symbol>> currentEntry2 = null;
		
		if (iterator1.hasNext()) {
			currentEntry1 = iterator1.next();
		} 
		
		if (iterator2.hasNext()) {
			currentEntry2 = iterator2.next();
		}

		while (added < listSizeLimit && !(currentEntry1 == null && currentEntry2 == null)) {
			
//System.out.println("CurrentEntry1 = " + currentEntry1 + " currentEntry2 = " + currentEntry2);
			
			if (currentEntry1 != null && result.containsKey(currentEntry1.getKey())) {
				result.get(currentEntry1.getKey()).addWeightsFrom(currentEntry1.getValue());
				iterator1.remove();
				currentEntry1 = null;
			} else if (currentEntry2 != null && result.containsKey(currentEntry2.getKey())) {
				result.get(currentEntry2.getKey()).addWeightsFrom(currentEntry2.getValue());
				iterator2.remove();
				currentEntry2 = null;
			} else if (currentEntry1 != null && currentEntry2 != null) {
				Weight weight1 = currentEntry1.getValue().getWeight(q);
				Weight weight2 = currentEntry2.getValue().getWeight(q);
				
				compResult = weight1.compareTo(weight2);
				
				if (compResult == -1) {
					result.put(currentEntry1.getKey(), currentEntry1.getValue());
					iterator1.remove();
					currentEntry1 = null;
				} else if (compResult == 1) {
					result.put(currentEntry2.getKey(), currentEntry2.getValue());
					iterator2.remove();
					currentEntry2 = null;
				} else {
					compResult = currentEntry1.getKey().compareTo(currentEntry2.getKey());
					
					if (compResult == 0) {
						currentEntry1.getValue().addWeightsFrom(currentEntry2.getValue());
						result.put(currentEntry1.getKey(), currentEntry1.getValue());
						iterator1.remove();
						iterator2.remove();
						currentEntry1 = null; 
						currentEntry2 = null;
					} else if (compResult == -1) {
						result.put(currentEntry1.getKey(), currentEntry1.getValue());
						iterator1.remove();
						currentEntry1 = null; 
					} else {
						result.put(currentEntry2.getKey(), currentEntry2.getValue());
						iterator2.remove();
						currentEntry2 = null; 
					}
				}
				
			} else if (currentEntry1 != null) {
				result.put(currentEntry1.getKey(), currentEntry1.getValue());
				iterator1.remove();
				currentEntry1 = null; 
			} else {
				result.put(currentEntry2.getKey(), currentEntry2.getValue());
				iterator2.remove();
				currentEntry2 = null; 
			}

			added++;
			
			if (currentEntry1 == null && iterator1.hasNext()) {
				currentEntry1 = iterator1.next();
			}
			
			if (currentEntry2 == null && iterator2.hasNext()) {
				currentEntry2 = iterator2.next();
			}
			
//System.out.println("Merge for state: current result: " + result);			
		}
//System.out.println("Merge for state: returning: " + result);
		return result;
	}

	private static LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>> mergeTreeListsByDeltaWeights(
			LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>> currentList, 
			LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>> mergedList,
			int listSizeLimit) {

//System.out.println("MERGE list by delta weights");		
		if (currentList.isEmpty()) {
//System.out.println("Merge: returning: " + mergedList);
			return mergedList;
		}
		
		if (mergedList.isEmpty()) {
//System.out.println("Merge: returning: " + currentList);
			return currentList;
		}

//		LinkedList<TreeKeeper<Symbol>> result = new LinkedList<>();
		LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>> result = new LinkedHashMap<>();

		int added = 0;
		int compResult;

		Iterator<Entry<Node<Symbol>, TreeKeeper<Symbol>>> iterator1 = currentList.entrySet().iterator();
		Iterator<Entry<Node<Symbol>, TreeKeeper<Symbol>>> iterator2 = mergedList.entrySet().iterator();
		
		Entry<Node<Symbol>, TreeKeeper<Symbol>> currentEntry1 = null;
		Entry<Node<Symbol>, TreeKeeper<Symbol>> currentEntry2 = null;
		
		if (iterator1.hasNext()) {
			currentEntry1 = iterator1.next();
		} 
		
		if (iterator2.hasNext()) {
			currentEntry2 = iterator2.next();
		}

		while (added < listSizeLimit && !(currentEntry1 == null && currentEntry2 == null)) {
//System.out.println("CurrentEntry1 = " + currentEntry1 + " currentEntry2 = " + currentEntry2);

			if (currentEntry1 != null && result.containsKey(currentEntry1.getKey())) {
				result.get(currentEntry1.getKey()).addWeightsFrom(currentEntry1.getValue());
				iterator1.remove();
				currentEntry1 = null; 
			} else if (currentEntry2 != null && result.containsKey(currentEntry2.getKey())) {
				result.get(currentEntry2.getKey()).addWeightsFrom(currentEntry2.getValue());
				iterator2.remove();
				currentEntry2 = null; 
			} else if (currentEntry1 != null && currentEntry2 != null) {				
				
				Weight weight1 = currentEntry1.getValue().getDeltaWeight();
				Weight weight2 = currentEntry2.getValue().getDeltaWeight();

				compResult = weight1.compareTo(weight2);
				
				if (compResult == -1) {
					result.put(currentEntry1.getKey(), currentEntry1.getValue());
					iterator1.remove();
					currentEntry1 = null;
				} else if (compResult == 1) {
					result.put(currentEntry2.getKey(), currentEntry2.getValue());
					iterator2.remove();
					currentEntry2 = null;
				} else {
					compResult = currentEntry1.getKey().compareTo(currentEntry2.getKey());
					
					if (compResult == 0) {
						currentEntry1.getValue().addWeightsFrom(currentEntry2.getValue());
						result.put(currentEntry1.getKey(), currentEntry1.getValue());
						iterator1.remove();
						iterator2.remove();
						currentEntry1 = null;
						currentEntry2 = null; 
					} else if (compResult == -1) {
						result.put(currentEntry1.getKey(), currentEntry1.getValue());
						iterator1.remove();
						currentEntry1 = null;
					} else {
						result.put(currentEntry2.getKey(), currentEntry2.getValue());
						iterator2.remove();
						currentEntry2 = null;
					}
				}
				
			} else if (currentEntry1 != null) {
				result.put(currentEntry1.getKey(), currentEntry1.getValue());
				iterator1.remove();
				currentEntry1 = null;
			} else {
				result.put(currentEntry2.getKey(), currentEntry2.getValue());
				iterator2.remove();
				currentEntry2 = null;
			}
			
			added++;
			
			if (currentEntry1 == null && iterator1.hasNext()) {
				currentEntry1 = iterator1.next();
			}
			
			if (currentEntry2 == null && iterator2.hasNext()) {
				currentEntry2 = iterator2.next();
			}
			
//System.out.println("Merge: current result: " + result);
		}
		
//System.out.println("Merge: returning: " + result);
		
		return result;
	}
}
