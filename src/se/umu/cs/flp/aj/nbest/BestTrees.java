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
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import se.umu.cs.flp.aj.eppstein_k_best.runner.EppsteinRunner;
import se.umu.cs.flp.aj.nbest.data.NestedMap;
import se.umu.cs.flp.aj.nbest.data.Node;
import se.umu.cs.flp.aj.wta.Rule;
import se.umu.cs.flp.aj.wta.State;
import se.umu.cs.flp.aj.wta.Symbol;
import se.umu.cs.flp.aj.wta.WTA;
import se.umu.cs.flp.aj.wta.Weight;


public class BestTrees {

	private static NestedMap<Node<Symbol>, State, Weight> treeStateValTable =
			new NestedMap<>(); // C
	private static ArrayList<Node<Symbol>> exploredTrees =
			new ArrayList<Node<Symbol>>(); // T
	private static LinkedList<Node<Symbol>> treeQueue = new LinkedList<>(); // K

	private static HashMap<State, Weight> smallestCompletionWeights;
	private static HashMap<Node<Symbol>, ArrayList<State>> optimalStates =
			new HashMap<>();
	private static HashMap<State, Integer> optimalStatesUsage = new HashMap<>();

	public static void setSmallestCompletions(
			HashMap<State, Weight> smallestCompletions) {
		smallestCompletionWeights = smallestCompletions;
	}

	public static List<String> run(WTA wta, int N) {

		/* For result. */
		List<String> nBest = new ArrayList<String>();

		// T <- empty. K <- empty
		exploredTrees = new ArrayList<Node<Symbol>>();
		treeQueue = new LinkedList<Node<Symbol>>();

		// enqueue(K, Sigma_0)
		enqueueRankZeroSymbols(wta, N);

		// i <- 0
		int counter = 0;

		// while i < N and K nonempty do
		while (counter < N && !treeQueue.isEmpty()) {

			// t <- dequeue(K)
			Node<Symbol> currentTree = treeQueue.poll();

			// T <- T u {t}
			exploredTrees.add(currentTree);

			// Get optimal state for current tree
			State optimalState = optimalStates.get(currentTree).get(0);

			// if M(t) = delta(t) then
			if (optimalState.isFinal()) {

				// output(t)
				nBest.add(currentTree.toString() + " " +
						treeStateValTable.get(currentTree, optimalState));

				// i <- i + 1
				counter++;
			}

			// prune(T, enqueue(K, expand(T, t)))
			enqueueWithExpansionAndPruning(wta, N, currentTree);

		}

		return nBest;
	}

	private static void enqueueRankZeroSymbols(WTA wta, int N) {

		ArrayList<Symbol> symbols = wta.getSymbols();

		for (Symbol s : symbols) {

			if (s.getRank() == 0) {
				Node<Symbol> tree = new Node<Symbol>(s);

				ArrayList<Rule> rules = wta.getTransitionFunction().
						getRulesBySymbol(s);

				for (Rule r : rules) {
					State resState = r.getResultingState();
					Weight weight = r.getWeight();
					Weight oldWeight = treeStateValTable.get(tree, resState);

					if (oldWeight == null ||
							weight.compareTo(oldWeight) == -1) {
						treeStateValTable.put(tree, resState, weight);
					}
				}

				optimalStates.put(tree, getOptimalStates(tree));
				insertIntoQueueUsingPruning(tree, N);
			}
		}
	}

	private static ArrayList<State> getOptimalStates(Node<Symbol> tree) {
		HashMap<State, Weight> stateValTable =
				treeStateValTable.getAll(tree);
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

		return optStates;
	}

	public static Weight getDeltaWeight(Node<Symbol> tree) {

		Weight delta = new Weight(0);

		State optimalState = optimalStates.get(tree).get(0);

		Weight minWeight = treeStateValTable.get(tree, optimalState);
		Weight smallestCompletionWeight = smallestCompletionWeights.
				get(optimalState);

		delta = minWeight.add(smallestCompletionWeight);

		return delta;
	}

	private static boolean insertIntoQueueUsingPruning(Node<Symbol> tree,
			int N) {

		boolean inserted = false;
		int insertIndex = getInsertIndex(tree);

		treeQueue.add(insertIndex, tree);
		inserted = true; //TODO what?

		prune(tree, N);

		return inserted;
	}

	private static int getInsertIndex(Node<Symbol> tree) {
		Weight deltaWeight = getDeltaWeight(tree);

		int queueSize = treeQueue.size();
		int insertIndex = 0;
		int currentIndex = queueSize - 1;

		while (insertIndex == 0 && currentIndex > -1) {

			Node<Symbol> currentTree = treeQueue.get(currentIndex);
			Weight currentDeltaWeight = getDeltaWeight(currentTree);

			if (currentDeltaWeight.compareTo(deltaWeight) == -1) {
				insertIndex = currentIndex + 1;
			} else if (currentDeltaWeight.compareTo(deltaWeight) == 0) {

				if (currentTree.compareTo(tree) == -1) {
					insertIndex = currentIndex + 1;
				}
			}

			currentIndex--;
		}

		return insertIndex;
	}

	private static void prune(Node<Symbol> tree, int N) {
		ArrayList<State> optStates = optimalStates.get(tree);

		for (State q : optStates) {
			int qUsage = 0;

			if (optimalStatesUsage.get(q) != null) {
				qUsage = optimalStatesUsage.get(q);
			}

			optimalStatesUsage.put(q, qUsage + 1);

			if (qUsage + 1 > N) {
				int removeIndex = getRemoveIndex(tree, q);
				
				if (removeIndex < treeQueue.size()) { // Remove check?
					Node<Symbol> removeTree = treeQueue.get(removeIndex);
					ArrayList<State> optStatesRemove =
							optimalStates.get(removeTree);
					treeQueue.remove(removeIndex);

					for (State optRemove : optStatesRemove) {
						optimalStatesUsage.put(optRemove,
								optimalStatesUsage.get(optRemove) - 1);
					}
				}

			}
		}
	}

	private static int getRemoveIndex(Node<Symbol> tree, State q) {
		int queueSize = treeQueue.size();
		int currentIndex = queueSize - 1;
		int removeIndex = queueSize;

		while (removeIndex == queueSize && currentIndex > -1) {
			Node<Symbol> currentTree = treeQueue.get(currentIndex);
			ArrayList<State> optStatesCurrent =
					optimalStates.get(currentTree);

			if (optStatesCurrent.contains(q)) {
				removeIndex = currentIndex;
			}

			currentIndex--;
		}

		return removeIndex;
	}

	public static void enqueueWithExpansionAndPruning(WTA wta, int N,
			Node<Symbol> tree) {

		HashMap<State, ArrayList<LinkedList<Node<Symbol>>>> allRuns =
				new HashMap<>();
		HashMap<State, LinkedList<Node<Symbol>>> nRuns = new HashMap<>();

		EppsteinRunner eRunner = new EppsteinRunner(exploredTrees,
				treeStateValTable);

		for (State q : wta.getStates()) {
			allRuns.put(q, eRunner.runEppstein(wta, N, tree, q));
		}

		for (Entry<State, ArrayList<LinkedList<Node<Symbol>>>> e :
			allRuns.entrySet()) {

			LinkedList<Node<Symbol>> mergedTreeList = new LinkedList<>();
			State q = e.getKey();

			for (LinkedList<Node<Symbol>> treeList : e.getValue()) {
				mergedTreeList = mergeTreeListsForState(treeList,
						mergedTreeList, N, q);
			}

			nRuns.put(q, mergedTreeList);
		}

		LinkedList<Node<Symbol>> mergedList = new LinkedList<>();
		int nOfStatesInWTA = wta.getStates().size();

		for (LinkedList<Node<Symbol>> currentList : nRuns.values()) {
			mergedList = mergeTreeListsByDeltaWeights(currentList, mergedList, 
					N*nOfStatesInWTA); // Unnecessary? Just insert them all into K and prune after each insertion?
		}

		for (Node<Symbol> n : mergedList) {
			insertIntoQueueUsingPruning(n, N);
		}
	}

	private static LinkedList<Node<Symbol>> mergeTreeListsForState(
			LinkedList<Node<Symbol>> list1, LinkedList<Node<Symbol>> list2,
			int listSizeLimit, State q) {

		LinkedList<Node<Symbol>> result = new LinkedList<>();

		int added = 0;
		int compResult;

		while (added < listSizeLimit && !(list1.isEmpty() && list2.isEmpty())) {

			Node<Symbol> tree1 = list1.peek();
			Node<Symbol> tree2 = list2.peek();

			if (tree1 != null && result.contains(tree1)) {
				list1.poll();
			} else if (tree2 != null && result.contains(tree2)) {
				list2.poll();
			} else if (tree1 != null && tree2 != null) {

				Weight weight1 = treeStateValTable.get(tree1, q);
				Weight weight2 = treeStateValTable.get(tree2, q);

				compResult = weight1.compareTo(weight2);

				if (compResult == -1) {
					result.addLast(list1.poll());
				} else if (compResult == 1) {
					result.addLast(list2.poll());
				} else {

					compResult = tree1.compareTo(tree2);

					if (compResult == 0) {
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

	private static LinkedList<Node<Symbol>> mergeTreeListsByDeltaWeights(
			LinkedList<Node<Symbol>> list1, LinkedList<Node<Symbol>> list2,
			int listSizeLimit) {

		LinkedList<Node<Symbol>> result = new LinkedList<>();

		int added = 0;

		while (added < listSizeLimit && !(list1.isEmpty() && list2.isEmpty())) {

			int compResult;

			Node<Symbol> tree1 = list1.peek();
			Node<Symbol> tree2 = list2.peek();

			ArrayList<State> optStates1 = optimalStates.get(tree1);

			if (optStates1 == null && tree1 != null) {
				optStates1 = getOptimalStates(tree1);
				optimalStates.put(tree1, optStates1);
			}

			ArrayList<State> optStates2 = optimalStates.get(tree2);

			if (optStates2 == null && tree2 != null) {
				optStates2 = getOptimalStates(tree2);
				optimalStates.put(tree2, optStates2);
			}

			if (tree1 != null && result.contains(tree1)) {
				list1.poll();
			} else if (tree2 != null && result.contains(tree2)) {
				list2.poll();
			} else if (tree1 != null && tree2 != null) {

				State opt1 = optStates1.get(0);
				State opt2 = optStates2.get(0);

				Weight compl1 = smallestCompletionWeights.get(
						optStates1.get(0));
				Weight compl2 = smallestCompletionWeights.get(
						optStates2.get(0));

				Weight weight1 = treeStateValTable.get(tree1, opt1).add(compl1);
				Weight weight2 = treeStateValTable.get(tree2, opt2).add(compl2);

				compResult = weight1.compareTo(weight2);

				if (compResult == -1) {
					result.addLast(list1.poll());
				} else if (compResult == 1) {
					result.addLast(list2.poll());
				} else {

					compResult = tree1.compareTo(tree2);

					if (compResult == 0) {
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
