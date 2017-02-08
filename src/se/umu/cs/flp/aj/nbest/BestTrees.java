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
		
//System.out.println("Queue after enqueing rank zero symbols: " + treeQueue);

		// i <- 0
		int counter = 0;

		// while i < N and K nonempty do
		while (counter < N && !treeQueue.isEmpty()) {

			// t <- dequeue(K)
//			Node<Symbol> currentTree = treeQueue.poll();
			TreeKeeper<Symbol> currentTree = treeQueue.pollFirstEntry().getKey();
			
//System.out.println("Current tree: " + currentTree);

			// T <- T u {t}
//			exploredTrees.add(currentTree);
			exploredTrees.add(currentTree);
			
//System.out.println("Explored trees: " + exploredTrees);

			// Get optimal state for current tree
//			State optimalState = optimalStates.get(currentTree).get(0);
			State optimalState = currentTree.getOptimalStates().keySet().iterator().next();

			// if M(t) = delta(t) then
			if (optimalState.isFinal()) {

				// output(t)
//				nBest.add(currentTree.getTree().toString() + " " +
//						treeStateValTable.get(currentTree, optimalState));
				nBest.add(currentTree.getTree().toString() + " " +
						getDeltaWeight(currentTree).toString());
//System.out.println("Outputting " + currentTree + "with weight " + getDeltaWeight(currentTree).toString());

				// i <- i + 1
				counter++;
			}

			// prune(T, enqueue(K, expand(T, t)))
			enqueueWithExpansionAndPruning(wta, N, currentTree);
			
//System.out.println("Queue after expansion and pruning: " + treeQueue);
			
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
				tree.setSmallestWeight(tree.getOptWeights().get(tree.getOptimalStates().keySet().iterator().next()));
				
//				insertIntoQueueUsingPruning(tree, N);
//				treeQueue.put(tree, tree.getWeight());
				treeQueue.put(tree, null);
				
//				System.out.println(treeQueue);
				
			}
		}
		
//System.out.println("END enqueue rank zero symbols");
	}

//	private static ArrayList<State> getOptimalStates(TreeKeeper<Symbol> tree) {
	private static LinkedHashMap<State,State> getOptimalStates(TreeKeeper<Symbol> tree) {
//		HashMap<State, Weight> stateValTable =
//				treeStateValTable.getAll(tree);
		
//System.out.println("Tree to get opt states for: " + tree);
		
		HashMap<State, Weight> stateValTable = tree.getOptWeights();
		
//		ArrayList<State> optStates = new ArrayList<>();
		LinkedHashMap<State, State> optStates = new LinkedHashMap<>();
		
		Weight minWeight = new Weight(Weight.INF);

		for (Entry<State, Weight> e : stateValTable.entrySet()) {

			State currentState = e.getKey();
			Weight smallestCompletionWeight = smallestCompletionWeights.get(
					currentState);
			Weight currentWeight = e.getValue().add(smallestCompletionWeight);
			int comparison = currentWeight.compareTo(minWeight);

			if (comparison == -1) {
				minWeight = currentWeight;
//				optStates = new ArrayList<>();
				optStates.clear();
//				optStates.add(e.getKey());
				optStates.put(e.getKey(), e.getKey());
			} else if (comparison == 0) {
//				optStates.add(e.getKey());
				optStates.put(e.getKey(), e.getKey());
			}
		}
		
//System.out.println("And its optstates are: " + optStates);

		return optStates;
	}

	public static Weight getDeltaWeight(TreeKeeper<Symbol> tree) {

		Weight delta = new Weight(0);

//		State optimalState = optimalStates.get(tree).get(0);
		State optimalState = tree.getOptimalStates().keySet().iterator().next();

//		Weight minWeight = treeStateValTable.get(tree, optimalState);
		Weight minWeight = tree.getOptWeights().get(optimalState);

//System.out.println("In getDeltaWeight: minweight is " + minWeight + " for " + tree);
		
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

//		HashMap<State, ArrayList<LinkedList<TreeKeeper<Symbol>>>> allRuns =
//				new HashMap<>();
		HashMap<State, ArrayList<LinkedHashMap<Node<Symbol>,TreeKeeper<Symbol>>>> allRuns =
				new HashMap<>();
		
//		HashMap<State, LinkedList<TreeKeeper<Symbol>>> nRuns = new HashMap<>();
		HashMap<State, LinkedHashMap<Node<Symbol>,TreeKeeper<Symbol>>> nRuns = new HashMap<>();

//		EppsteinRunner eRunner = new EppsteinRunner(exploredTrees,
//				treeStateValTable);
		EppsteinRunner eRunner = new EppsteinRunner(exploredTrees);

		for (State q : wta.getStates()) {
			allRuns.put(q, eRunner.runEppstein(wta, N, tree, q));
		}

//		for (Entry<State, ArrayList<LinkedList<TreeKeeper<Symbol>>>> e :
//			allRuns.entrySet()) {
		for (Entry<State, ArrayList<LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>>>> e :
			allRuns.entrySet()) {

//			LinkedList<TreeKeeper<Symbol>> mergedTreeList = new LinkedList<>();
			LinkedHashMap<Node<Symbol>,TreeKeeper<Symbol>> mergedTreeList = new LinkedHashMap<>();
			
//			TreeMap<TreeKeeper<Symbol>,TreeKeeper<Symbol>> mergedTreeList2 = new TreeMap<>();
			
			State q = e.getKey();
			
			int counter = 0;
//			for (LinkedList<TreeKeeper<Symbol>> treeList : e.getValue()) {
			for (LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>> treeList : e.getValue()) {
				
				mergedTreeList = mergeTreeListsForState(treeList,
						mergedTreeList, N, q);

				/* New code */
//				for (Entry<Node<Symbol>, TreeKeeper<Symbol>> e1 : treeList.entrySet()) {
//					TreeKeeper<Symbol> t = e1.getValue();
//					
//					if (mergedTreeList2.containsKey(t)) {
//						TreeKeeper<Symbol> old = mergedTreeList2.get(t);
//						t.getOptimalStates().addAll(old.getOptimalStates());
//						t.getOptWeights().putAll(old.getOptWeights());
//					} else {
//						mergedTreeList2.put(t, t);
//					}
//				}
				
			}

//			nRuns.put(q, mergedTreeList);
			nRuns.put(q, mergedTreeList);
			
//			nRuns2.put(q, mergedTreeList2); // Continue this line of thought
		}

		/* New  */
//		for (Entry<State, TreeMap<TreeKeeper<Symbol>, TreeKeeper<Symbol>>> e : nRuns2.entrySet()) {
//			
//			for (Entry<TreeKeeper<Symbol>, TreeKeeper<Symbol>> e2 : e.getValue().entrySet()) {
//				treeQueue.put(e2.getKey(), null);
//			}
//		}
		
/* Old*/
//		LinkedList<TreeKeeper<Symbol>> mergedList = new LinkedList<>();
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

//	private static LinkedList<TreeKeeper<Symbol>> mergeTreeListsForState(
//			LinkedList<TreeKeeper<Symbol>> list1, LinkedList<TreeKeeper<Symbol>> list2,
//			int listSizeLimit, State q) {
//
//		LinkedList<TreeKeeper<Symbol>> result = new LinkedList<>();
//
//		int added = 0;
//		int compResult;
//
//		while (added < listSizeLimit && !(list1.isEmpty() && list2.isEmpty())) {
//			
//			TreeKeeper<Symbol> tree1 = list1.peek();
//			TreeKeeper<Symbol> tree2 = list2.peek();
//
//			if (tree1 != null && result.contains(tree1)) {
//				list1.poll();
//			} else if (tree2 != null && result.contains(tree2)) {
//				list2.poll();
//			} else if (tree1 != null && tree2 != null) {
//
////				Weight weight1 = treeStateValTable.get(tree1, q);
//				Weight weight1 = tree1.getOptWeights().get(q);
//				
////				Weight weight2 = treeStateValTable.get(tree2, q);
//				Weight weight2 = tree2.getOptWeights().get(q);
//
//				compResult = weight1.compareTo(weight2);
//
//				if (compResult == -1) {
//					result.addLast(list1.poll());
//				} else if (compResult == 1) {
//					result.addLast(list2.poll());
//				} else {
//
//					compResult = tree1.getTree().compareTo(tree2.getTree());
//
//					if (compResult == 0) {
////						tree1.getOptimalStates().addAll(tree2.getOptimalStates());
////						tree1.getOptWeights().putAll(tree2.getOptWeights());
//						result.addLast(list1.poll());
//						list2.poll();
//					} else if (compResult == -1) {
//						result.addLast(list1.poll());
//					} else {
//						result.addLast(list2.poll());
//					}
//				}
//
//			} else if (tree1 != null) {
//				result.addLast(list1.poll());
//			} else {
//				result.addLast(list2.poll());
//			}
//
//			added++;
//		}
//
//		return result;
//	}
	
//	private static LinkedList<TreeKeeper<Symbol>> mergeTreeListsForState(
//			LinkedList<TreeKeeper<Symbol>> list1, LinkedList<TreeKeeper<Symbol>> list2,
//			int listSizeLimit, State q) {
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
		


//		LinkedList<TreeKeeper<Symbol>> result = new LinkedList<>();
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

//		while (added < listSizeLimit && !(list1.isEmpty() && list2.isEmpty())) {
		while (added < listSizeLimit && !(currentEntry1 == null && currentEntry2 == null)) {
			
//System.out.println("CurrentEntry1 = " + currentEntry1 + " currentEntry2 = " + currentEntry2);
			
//			TreeKeeper<Symbol> tree1 = list1.peek();
//			TreeKeeper<Symbol> tree2 = list2.peek();
			
//			TreeKeeper<Symbol> tree1 = currentEntry1.getValue();
//			TreeKeeper<Symbol> tree2 = list2.peek();
			
			if (currentEntry1 != null && result.containsKey(currentEntry1.getKey())) {
//				result.get(currentEntry1.getKey()).getOptimalStates().putAll(currentEntry1.getValue().getOptimalStates());
				result.get(currentEntry1.getKey()).getOptWeights().putAll(currentEntry1.getValue().getOptWeights());
				iterator1.remove();
				currentEntry1 = null; // Do not know if necessary
			} else if (currentEntry2 != null && result.containsKey(currentEntry2.getKey())) {
//				result.get(currentEntry2.getKey()).getOptimalStates().putAll(currentEntry2.getValue().getOptimalStates());
				result.get(currentEntry2.getKey()).getOptWeights().putAll(currentEntry2.getValue().getOptWeights());
				iterator2.remove();
				currentEntry2 = null; // Do not know if necessary
			} else if (currentEntry1 != null && currentEntry2 != null) {
				Weight weight1 = currentEntry1.getValue().getOptWeights().get(q);
				Weight weight2 = currentEntry2.getValue().getOptWeights().get(q);
				
				compResult = weight1.compareTo(weight2);
				
				if (compResult == -1) {
					result.put(currentEntry1.getKey(), currentEntry1.getValue());
					iterator1.remove();
					currentEntry1 = null; // Do not know if necessary
				} else if (compResult == 1) {
					result.put(currentEntry2.getKey(), currentEntry2.getValue());
					iterator2.remove();
					currentEntry2 = null; // Do not know if necessary
				} else {
					compResult = currentEntry1.getKey().compareTo(currentEntry2.getKey());
					
					if (compResult == 0) {
						currentEntry1.getValue().getOptimalStates().putAll(currentEntry2.getValue().getOptimalStates());
						currentEntry1.getValue().getOptWeights().putAll(currentEntry2.getValue().getOptWeights());
						result.put(currentEntry1.getKey(), currentEntry1.getValue());
						iterator1.remove();
						iterator2.remove();
						currentEntry1 = null; // Do not know if necessary
						currentEntry2 = null; // Do not know if necessary
					} else if (compResult == -1) {
						result.put(currentEntry1.getKey(), currentEntry1.getValue());
						iterator1.remove();
						currentEntry1 = null; // Do not know if necessary
					} else {
						result.put(currentEntry2.getKey(), currentEntry2.getValue());
						iterator2.remove();
						currentEntry2 = null; // Do not know if necessary
					}
				}
				
			} else if (currentEntry1 != null) {
				result.put(currentEntry1.getKey(), currentEntry1.getValue());
				iterator1.remove();
				currentEntry1 = null; // Do not know if necessary
			} else {
				result.put(currentEntry2.getKey(), currentEntry2.getValue());
				iterator2.remove();
				currentEntry2 = null; // Do not know if necessary
			}

//			if (tree1 != null && result.contains(tree1)) {				
//				list1.poll();
//			} else if (tree2 != null && result.contains(tree2)) {
//				list2.poll();
//			} else if (tree1 != null && tree2 != null) {
//
////				Weight weight1 = treeStateValTable.get(tree1, q);
//				Weight weight1 = tree1.getOptWeights().get(q);
//				
////				Weight weight2 = treeStateValTable.get(tree2, q);
//				Weight weight2 = tree2.getOptWeights().get(q);
//
//				compResult = weight1.compareTo(weight2);
//
//				if (compResult == -1) {
//					result.addLast(list1.poll());
//				} else if (compResult == 1) {
//					result.addLast(list2.poll());
//				} else {
//
//					compResult = tree1.getTree().compareTo(tree2.getTree());
//
//					if (compResult == 0) {
////						tree1.getOptimalStates().addAll(tree2.getOptimalStates());
////						tree1.getOptWeights().putAll(tree2.getOptWeights());
//						result.addLast(list1.poll());
//						list2.poll();
//					} else if (compResult == -1) {
//						result.addLast(list1.poll());
//					} else {
//						result.addLast(list2.poll());
//					}
//				}
//
//			} else if (tree1 != null) {
//				result.addLast(list1.poll());
//			} else {
//				result.addLast(list2.poll());
//			}

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

//	private static LinkedList<TreeKeeper<Symbol>> mergeTreeListsByDeltaWeights(
//			LinkedList<TreeKeeper<Symbol>> list1, LinkedList<TreeKeeper<Symbol>> list2,
//			int listSizeLimit) {
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

			if (currentEntry1 != null && currentEntry1.getValue().getOptimalStates() == null) {
				currentEntry1.getValue().setOptimalStates(getOptimalStates(currentEntry1.getValue()));
			}

			if (currentEntry2 != null && currentEntry2.getValue().getOptimalStates() == null) {
				currentEntry2.getValue().setOptimalStates(getOptimalStates(currentEntry2.getValue()));
			}

			if (currentEntry1 != null && result.containsKey(currentEntry1.getKey())) {
//				result.get(currentEntry1.getKey()).getOptimalStates().putAll(currentEntry1.getValue().getOptimalStates());
				result.get(currentEntry1.getKey()).getOptWeights().putAll(currentEntry1.getValue().getOptWeights());
				iterator1.remove();
				currentEntry1 = null; // Do not know if necessary
			} else if (currentEntry2 != null && result.containsKey(currentEntry2.getKey())) {
//				result.get(currentEntry2.getKey()).getOptimalStates().putAll(currentEntry2.getValue().getOptimalStates());
				result.get(currentEntry2.getKey()).getOptWeights().putAll(currentEntry2.getValue().getOptWeights());
				iterator2.remove();
				currentEntry2 = null; // Do not know if necessary
			} else if (currentEntry1 != null && currentEntry2 != null) {				
				
//				State opt1 = optStates1.get(0);
				State opt1 = currentEntry1.getValue().getOptimalStates().keySet().iterator().next();
			
//				State opt2 = optStates2.get(0);
				State opt2 = currentEntry2.getValue().getOptimalStates().keySet().iterator().next();

//				Weight compl1 = smallestCompletionWeights.get(
//						optStates1.get(0));
				Weight compl1 = smallestCompletionWeights.get(opt1);
				
//				Weight compl2 = smallestCompletionWeights.get(
//						optStates2.get(0));
				Weight compl2 = smallestCompletionWeights.get(opt2);

//				Weight weight1 = treeStateValTable.get(tree1, opt1).add(compl1);
				Weight weight1 = currentEntry1.getValue().getOptWeights().get(opt1).add(compl1);
				
//				Weight weight2 = treeStateValTable.get(tree2, opt2).add(compl2);
				Weight weight2 = currentEntry2.getValue().getOptWeights().get(opt2).add(compl2);

				compResult = weight1.compareTo(weight2);
				
				compResult = weight1.compareTo(weight2);
				
				if (compResult == -1) {
					result.put(currentEntry1.getKey(), currentEntry1.getValue());
					iterator1.remove();
					currentEntry1 = null; // Do not know if necessary
				} else if (compResult == 1) {
					result.put(currentEntry2.getKey(), currentEntry2.getValue());
					iterator2.remove();
					currentEntry2 = null; // Do not know if necessary
				} else {
					compResult = currentEntry1.getKey().compareTo(currentEntry2.getKey());
					
					if (compResult == 0) {
//						currentEntry1.getValue().getOptimalStates().putAll(currentEntry2.getValue().getOptimalStates());
//						currentEntry1.getValue().getOptWeights().putAll(currentEntry2.getValue().getOptWeights());
						currentEntry1.getValue().getDataFrom(currentEntry2.getValue());
						result.put(currentEntry1.getKey(), currentEntry1.getValue());
						iterator1.remove();
						iterator2.remove();
						currentEntry1 = null; // Do not know if necessary
						currentEntry2 = null; // Do not know if necessary
					} else if (compResult == -1) {
						result.put(currentEntry1.getKey(), currentEntry1.getValue());
						iterator1.remove();
						currentEntry1 = null; // Do not know if necessary
					} else {
						result.put(currentEntry2.getKey(), currentEntry2.getValue());
						iterator2.remove();
						currentEntry2 = null; // Do not know if necessary
					}
				}
				
			} else if (currentEntry1 != null) {
				result.put(currentEntry1.getKey(), currentEntry1.getValue());
				iterator1.remove();
				currentEntry1 = null; // Do not know if necessary
			} else {
				result.put(currentEntry2.getKey(), currentEntry2.getValue());
				iterator2.remove();
				currentEntry2 = null; // Do not know if necessary
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
