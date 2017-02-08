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

package se.umu.cs.flp.aj.nbest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import se.umu.cs.flp.aj.eppstein_k_best.runner.EppsteinRunner;
import se.umu.cs.flp.aj.nbest.data.NestedMap;
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

//	private static NestedMap<Node<Symbol>, State, Weight> treeStateValTable =
//			new NestedMap<>(); // C
	
//	private static ArrayList<Node<Symbol>> exploredTrees; // T
	private static ArrayList<TreeKeeper<Symbol>> exploredTrees; // T

	//	private static LinkedList<Node<Symbol>> treeQueue; // K
	private static PrunedQueue<TreeKeeper<Symbol>,Weight> treeQueue; // K

	private static HashMap<State, Weight> smallestCompletionWeights;
	
//	private static HashMap<Node<Symbol>, ArrayList<State>> optimalStates =
//			new HashMap<>();
	
//	private static HashMap<State, Integer> optimalStatesUsage = new HashMap<>();

	public static void setSmallestCompletions(
			HashMap<State, Weight> smallestCompletions) {
		smallestCompletionWeights = smallestCompletions;
	}

	public static List<String> run(WTA wta, int N) {

		/* For result. */
		List<String> nBest = new ArrayList<String>(); // Perhaps just output instead

		// T <- empty. K <- empty
//		exploredTrees = new ArrayList<Node<Symbol>>();
		exploredTrees = new ArrayList<TreeKeeper<Symbol>>();
		
//		treeQueue = new LinkedList<Node<Symbol>>();
		treeQueue = new PrunedQueue<TreeKeeper<Symbol>,Weight>(new TreeComparator<Symbol>(smallestCompletionWeights), 
				new TreePruner<Symbol,Weight>(N));
//		treeQueue = new PrunedQueue<TreeKeeper<Symbol>,Weight>(
//				new TreePruner<Symbol,Weight>(N));

		// enqueue(K, Sigma_0)
		enqueueRankZeroSymbols(wta, N);
		
System.out.println("Queue after enqueing rank zero symbols: " + treeQueue);

		// i <- 0
		int counter = 0;

		// while i < N and K nonempty do
		while (counter < N && !treeQueue.isEmpty()) {

			// t <- dequeue(K)
//			Node<Symbol> currentTree = treeQueue.poll();
			TreeKeeper<Symbol> currentTree = treeQueue.pollFirstEntry().getKey();
			
System.out.println("Current tree: " + currentTree);

			// T <- T u {t}
//			exploredTrees.add(currentTree);
			exploredTrees.add(currentTree);
			
System.out.println("Explored trees: " + exploredTrees);

			// Get optimal state for current tree
//			State optimalState = optimalStates.get(currentTree).get(0);
			State optimalState = currentTree.getOptimalStates().get(0);

			// if M(t) = delta(t) then
			if (optimalState.isFinal()) {

				// output(t)
//				nBest.add(currentTree.getTree().toString() + " " +
//						treeStateValTable.get(currentTree, optimalState));
				nBest.add(currentTree.getTree().toString() + " " +
						getDeltaWeight(currentTree).toString());
System.out.println("Outputting " + currentTree + "with weight " + getDeltaWeight(currentTree).toString());

				// i <- i + 1
				counter++;
			}

			// prune(T, enqueue(K, expand(T, t)))
			enqueueWithExpansionAndPruning(wta, N, currentTree);
			
System.out.println("Queue after expansion and pruning: " + treeQueue);
			
			// Typ getExpansion och sen adda allt till kön en efter en?

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
					State resState = r.getResultingState();
					Weight weight = r.getWeight();
//					Weight oldWeight = treeStateValTable.get(tree, resState);
					Weight oldWeight = tree.getOptWeights().get(resState);

					if (oldWeight == null ||
							weight.compareTo(oldWeight) == -1) {
//						treeStateValTable.put(tree, resState, weight);
						tree.getOptWeights().put(resState, weight);
					}
				}
				
//				optimalStates.put(tree, getOptimalStates(tree));
				tree.setOptimalStates(getOptimalStates(tree));
				
//				insertIntoQueueUsingPruning(tree, N);
//				treeQueue.put(tree, tree.getWeight());
				treeQueue.put(tree, null);
				
//				System.out.println(treeQueue);
				
			}
		}
	}

	private static ArrayList<State> getOptimalStates(TreeKeeper<Symbol> tree) {
//		HashMap<State, Weight> stateValTable =
//				treeStateValTable.getAll(tree);
		
System.out.println("Tree to get opt states for: " + tree);
		
		HashMap<State, Weight> stateValTable = tree.getOptWeights();
		
		ArrayList<State> optStates = new ArrayList<>();
		Weight minWeight = new Weight(Weight.INF);

		for (Entry<State, Weight> e : stateValTable.entrySet()) {

			State currentState = e.getKey();
			Weight smallestCompletionWeight = smallestCompletionWeights.get(
					currentState);
			Weight currentWeight = e.getValue().add(smallestCompletionWeight);
			int comparison = currentWeight.compareTo(minWeight);

			if (comparison == -1) {
				minWeight = currentWeight;
				optStates = new ArrayList<>();
				optStates.add(e.getKey());
			} else if (comparison == 0) {
				optStates.add(e.getKey());
			}
		}
		
System.out.println("And its optstates are: " + optStates);

		return optStates;
	}

	public static Weight getDeltaWeight(TreeKeeper<Symbol> tree) {

		Weight delta = new Weight(0);

//		State optimalState = optimalStates.get(tree).get(0);
		State optimalState = tree.getOptimalStates().get(0);

//		Weight minWeight = treeStateValTable.get(tree, optimalState);
		Weight minWeight = tree.getOptWeights().get(optimalState);

System.out.println("In getDeltaWeight: minweight is " + minWeight + " for " + tree);
		
		Weight smallestCompletionWeight = smallestCompletionWeights.
				get(optimalState);

		delta = minWeight.add(smallestCompletionWeight);

		return delta;
	}

//	private static boolean insertIntoQueueUsingPruning(TreeKeeper<Symbol> tree,
//			int N) {
//		boolean inserted = false;
//		int insertIndex = getInsertIndex(tree);
//		treeQueue.add(insertIndex, tree);		
//		inserted = true; //TODO what?
//		prune(tree, N);
//		return true; // TODO
//	}

//	private static int getInsertIndex(Node<Symbol> tree) {
//		Weight deltaWeight = getDeltaWeight(tree);
//
//		int queueSize = treeQueue.size();
//		int insertIndex = 0;
//		int currentIndex = queueSize - 1;
//
//		while (insertIndex == 0 && currentIndex > -1) {
//
//			Node<Symbol> currentTree = treeQueue.get(currentIndex);
//			Weight currentDeltaWeight = getDeltaWeight(currentTree);
//
//			if (currentDeltaWeight.compareTo(deltaWeight) == -1) {
//				insertIndex = currentIndex + 1;
//			} else if (currentDeltaWeight.compareTo(deltaWeight) == 0) {
//
//				if (currentTree.compareTo(tree) == -1) {
//					insertIndex = currentIndex + 1;
//				}
//			}
//
//			currentIndex--;
//		}
//
//		return insertIndex;
//	}

//	private static void prune(Node<Symbol> tree, int N) {
//		ArrayList<State> optStates = optimalStates.get(tree);
//
//		for (State q : optStates) {
//			int qUsage = 0;
//
//			if (optimalStatesUsage.get(q) != null) {
//				qUsage = optimalStatesUsage.get(q);
//			}
//
//			optimalStatesUsage.put(q, qUsage + 1);
//
//			if (qUsage + 1 > N) {
//				int removeIndex = getRemoveIndex(tree, q);
//				
//				if (removeIndex < treeQueue.size()) { // Remove check?
//					Node<Symbol> removeTree = treeQueue.get(removeIndex);
//					ArrayList<State> optStatesRemove =
//							optimalStates.get(removeTree);
//					treeQueue.remove(removeIndex);
//
//					for (State optRemove : optStatesRemove) {
//						optimalStatesUsage.put(optRemove,
//								optimalStatesUsage.get(optRemove) - 1);
//					}
//				}
//
//			}
//		}
//	}

//	private static int getRemoveIndex(Node<Symbol> tree, State q) {
//		int queueSize = treeQueue.size();
//		int currentIndex = queueSize - 1;
//		int removeIndex = queueSize;
//
//		while (removeIndex == queueSize && currentIndex > -1) {
//			Node<Symbol> currentTree = treeQueue.get(currentIndex);
//			ArrayList<State> optStatesCurrent =
//					optimalStates.get(currentTree);
//
//			if (optStatesCurrent.contains(q)) {
//				removeIndex = currentIndex;
//			}
//
//			currentIndex--;
//		}
//
//		return removeIndex;
//	}

	public static void enqueueWithExpansionAndPruning(WTA wta, int N,
			TreeKeeper<Symbol> tree) {

		HashMap<State, ArrayList<LinkedList<TreeKeeper<Symbol>>>> allRuns =
				new HashMap<>();
		HashMap<State, LinkedList<TreeKeeper<Symbol>>> nRuns = new HashMap<>();
		
		HashMap<State, TreeMap<TreeKeeper<Symbol>,TreeKeeper<Symbol>>> nRuns2 = new HashMap<>();

//		EppsteinRunner eRunner = new EppsteinRunner(exploredTrees,
//				treeStateValTable);
		EppsteinRunner eRunner = new EppsteinRunner(exploredTrees);

		for (State q : wta.getStates()) {
			allRuns.put(q, eRunner.runEppstein(wta, N, tree, q));
		}

		for (Entry<State, ArrayList<LinkedList<TreeKeeper<Symbol>>>> e :
			allRuns.entrySet()) {

			LinkedList<TreeKeeper<Symbol>> mergedTreeList = new LinkedList<>();
			
			TreeMap<TreeKeeper<Symbol>,TreeKeeper<Symbol>> mergedTreeList2 = new TreeMap<>();
			
			State q = e.getKey();
			
			int counter = 0;

			for (LinkedList<TreeKeeper<Symbol>> treeList : e.getValue()) {
				mergedTreeList = mergeTreeListsForState(treeList,
						mergedTreeList, N, q);
				
				for (TreeKeeper<Symbol> t : treeList) {
					if (mergedTreeList2.containsKey(t)) {
						TreeKeeper<Symbol> old = mergedTreeList2.get(t);
						t.getOptimalStates().addAll(old.getOptimalStates());
						t.getOptWeights().putAll(old.getOptWeights());
					}
					
					mergedTreeList2.put(t, t);
				}
				
			}

			nRuns.put(q, mergedTreeList);
			
			nRuns2.put(q, mergedTreeList2); // Continue this line of though
		}

		LinkedList<TreeKeeper<Symbol>> mergedList = new LinkedList<>();
		int nOfStatesInWTA = wta.getStates().size();

		for (LinkedList<TreeKeeper<Symbol>> currentList : nRuns.values()) {
			mergedList = mergeTreeListsByDeltaWeights(currentList, mergedList, 
					N*nOfStatesInWTA); // Unnecessary? Just insert them all into K and prune after each insertion?
		}

		for (TreeKeeper<Symbol> n : mergedList) {
			treeQueue.put(n, null);
//			insertIntoQueueUsingPruning(n, N);
		}
	}

	private static LinkedList<TreeKeeper<Symbol>> mergeTreeListsForState(
			LinkedList<TreeKeeper<Symbol>> list1, LinkedList<TreeKeeper<Symbol>> list2,
			int listSizeLimit, State q) {

		LinkedList<TreeKeeper<Symbol>> result = new LinkedList<>();

		int added = 0;
		int compResult;

		while (added < listSizeLimit && !(list1.isEmpty() && list2.isEmpty())) {
			
			TreeKeeper<Symbol> tree1 = list1.peek();
			TreeKeeper<Symbol> tree2 = list2.peek();

			if (tree1 != null && result.contains(tree1)) {
				list1.poll();
			} else if (tree2 != null && result.contains(tree2)) {
				list2.poll();
			} else if (tree1 != null && tree2 != null) {

//				Weight weight1 = treeStateValTable.get(tree1, q);
				Weight weight1 = tree1.getOptWeights().get(q);
				
//				Weight weight2 = treeStateValTable.get(tree2, q);
				Weight weight2 = tree2.getOptWeights().get(q);

				compResult = weight1.compareTo(weight2);

				if (compResult == -1) {
					result.addLast(list1.poll());
				} else if (compResult == 1) {
					result.addLast(list2.poll());
				} else {

					compResult = tree1.getTree().compareTo(tree2.getTree());

					if (compResult == 0) {
						tree1.getOptimalStates().addAll(tree2.getOptimalStates());
						tree1.getOptWeights().putAll(tree2.getOptWeights());
						result.addLast(list1.poll());
						list2.poll();
					} else if (compResult == -1) {
						result.addLast(list1.poll());
					} else {
						result.addLast(list2.poll());
					}
				}

			} else if (tree1 != null) {
				result.addLast(list1.poll());
			} else {
				result.addLast(list2.poll());
			}

			added++;
		}

		return result;
	}

	private static LinkedList<TreeKeeper<Symbol>> mergeTreeListsByDeltaWeights(
			LinkedList<TreeKeeper<Symbol>> list1, LinkedList<TreeKeeper<Symbol>> list2,
			int listSizeLimit) {

		LinkedList<TreeKeeper<Symbol>> result = new LinkedList<>();

		int added = 0;

		while (added < listSizeLimit && !(list1.isEmpty() && list2.isEmpty())) {

			int compResult;
			
			TreeKeeper<Symbol> tree1 = list1.peek();
			TreeKeeper<Symbol> tree2 = list2.peek();
			
//			System.out.println("tree1: " + tree1);
//			System.out.println("tree2: " + tree2);

//			ArrayList<State> optStates1 = optimalStates.get(tree1);

			if (tree1 != null && tree1.getOptimalStates() == null) {
//				optStates1 = getOptimalStates(tree1);
//				optimalStates.put(tree1, optStates1);
				tree1.setOptimalStates(getOptimalStates(tree1));
			}

//			ArrayList<State> optStates2 = optimalStates.get(tree2);

			if (tree2 != null && tree2.getOptimalStates() == null) {
//				optStates2 = getOptimalStates(tree2);
//				optimalStates.put(tree2, optStates2);
				tree2.setOptimalStates(getOptimalStates(tree2));
			}
			
//			System.out.println("After null check");
//			System.out.println("tree1: " + tree1);
//			System.out.println("tree2: " + tree2);

			if (tree1 != null && result.contains(tree1)) {
				list1.poll();
			} else if (tree2 != null && result.contains(tree2)) {
				list2.poll();
			} else if (tree1 != null && tree2 != null) {

//				State opt1 = optStates1.get(0);
				State opt1 = tree1.getOptimalStates().get(0);
				
//				State opt2 = optStates2.get(0);
				State opt2 = tree2.getOptimalStates().get(0);

//				Weight compl1 = smallestCompletionWeights.get(
//						optStates1.get(0));
				Weight compl1 = smallestCompletionWeights.get(opt1);
				
//				Weight compl2 = smallestCompletionWeights.get(
//						optStates2.get(0));
				Weight compl2 = smallestCompletionWeights.get(opt2);

//				Weight weight1 = treeStateValTable.get(tree1, opt1).add(compl1);
				Weight weight1 = tree1.getOptWeights().get(opt1).add(compl1);
				
//				Weight weight2 = treeStateValTable.get(tree2, opt2).add(compl2);
				Weight weight2 = tree2.getOptWeights().get(opt2).add(compl2);

				compResult = weight1.compareTo(weight2);

				if (compResult == -1) {
					result.addLast(list1.poll());
				} else if (compResult == 1) {
					result.addLast(list2.poll());
				} else {

					compResult = tree1.getTree().compareTo(tree2.getTree());

					if (compResult == 0) {
						tree1.getOptimalStates().addAll(tree2.getOptimalStates());
						tree1.getOptWeights().putAll(tree2.getOptWeights());
						result.addLast(list1.poll());
						list2.poll();
					} else if (compResult == -1) {
						result.addLast(list1.poll());
					} else {
						result.addLast(list2.poll());
					}
				}

			} else if (tree1 != null) {
				result.addLast(list1.poll());
			} else {
				result.addLast(list2.poll());
			}

			added++;
		}

		return result;
	}
}
