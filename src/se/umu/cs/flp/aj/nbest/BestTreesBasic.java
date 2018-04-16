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

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.semiring.TropicalWeight;
import se.umu.cs.flp.aj.nbest.treedata.Node;
import se.umu.cs.flp.aj.nbest.util.NestedMap;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.Symbol;
import se.umu.cs.flp.aj.nbest.wta.WTA;

public class BestTreesBasic {

	private static NestedMap<Node<Symbol>, State, Weight> treeStateValTable =
			new NestedMap<>(); // C
	private static ArrayList<Node<Symbol>> exploredTrees =
			new ArrayList<Node<Symbol>>(); // T
	private static LinkedList<Node<Symbol>> treeQueue = new LinkedList<>(); // K

	private static HashMap<State, Weight> smallestCompletionWeights;
	private static HashMap<Node<Symbol>, ArrayList<State>> optimalStates =
			new HashMap<>();

	public static void setSmallestCompletions(
			HashMap<State, Weight> smallestCompletions) {
		smallestCompletionWeights = smallestCompletions;
	}

	public static List<String> run(WTA wta, int N) {

		/* For result. */
		List<String> nBest = new ArrayList<String>();

		// T <- empty
		exploredTrees = new ArrayList<Node<Symbol>>();

		// K <- empty
		treeQueue = new LinkedList<Node<Symbol>>();

		// enqueue(K, Sigma_0)
		enqueueRankZeroSymbols(wta);

		// i <- 0
		int counter = 0;

		// while i < N and K nonempty do
		while (counter < N && !treeQueue.isEmpty()) {

			// t <- dequeue(K)
			Node<Symbol> currentTree = treeQueue.poll();

			// Get optimal state for current tree
			State optimalState = optimalStates.get(currentTree).get(0);

			// T <- T u {t}
			exploredTrees.add(currentTree);

			// if M(t) = delta(t)
			// (this the same thing as the optimal state being a final state)
			if (optimalState.isFinal()) {

				// output(t)
				nBest.add(currentTree.toString() + " " +
						treeStateValTable.get(currentTree, optimalState));

				// i <- i + 1
				counter++;
			}

			// enqueue(K, expand(T, t))
			ArrayList<Node<Symbol>> expansion = expandWith(wta, currentTree);

			for (Node<Symbol> t : expansion) {
				computeCurrentWeight(wta, t);
				optimalStates.put(t, getOptimalStates(t));
				insertTreeIntoQueueByTotalMinimumWeight(t);
			}
		}

		return nBest;
	}

	private static void enqueueRankZeroSymbols(WTA wta) {

		ArrayList<Symbol> symbols = wta.getSymbols();

		for (Symbol s : symbols) {

			if (s.getRank() == 0) {
				Node<Symbol> tree = new Node<Symbol>(s);

				ArrayList<Rule<Symbol>> rules = wta.getTransitionFunction().
						getRulesBySymbol(s);

				for (Rule<Symbol> r : rules) {
					State resState = r.getResultingState();
					Weight weight = r.getWeight();
					Weight oldWeight = treeStateValTable.get(tree, resState);

					if (oldWeight == null ||
							weight.compareTo(oldWeight) == -1) {
						treeStateValTable.put(tree, resState, weight);
					}
				}

				optimalStates.put(tree, getOptimalStates(tree));
				insertTreeIntoQueueByTotalMinimumWeight(tree);
			}
		}
	}

	public static void computeCurrentWeight(WTA wta, Node<Symbol> tree) {

		ArrayList<Rule<Symbol>> rules = wta.getTransitionFunction().
				getRulesBySymbol(tree.getLabel());

		int nOfSubtrees = tree.getChildCount();

		for (Rule<Symbol> r : rules) {
			ArrayList<State> states = r.getStates();

			boolean canUseRule = true;
			Weight weightSum = (new TropicalWeight()).one();

			for (int i = 0; i < nOfSubtrees; i++) {
				Node<Symbol> subTree = tree.getChildAt(i);
				State s = states.get(i);
				Weight wTemp = treeStateValTable.get(subTree, s);

				if (wTemp == null) {
					canUseRule = false;
				} else {
					weightSum = weightSum.mult(wTemp);
				}
			}

			weightSum = weightSum.mult(r.getWeight());

			if (canUseRule) {
				Weight currentWeight = treeStateValTable.get(tree,
						r.getResultingState());

				if (currentWeight == null ||
						(currentWeight.compareTo(weightSum) == 1)) {
					treeStateValTable.put(tree, r.getResultingState(),
							weightSum);
				}
			}
		}
	}

	public static ArrayList<State> getOptimalStates(Node<Symbol> tree) {
		HashMap<State, Weight> stateValTable =
				treeStateValTable.getAll(tree);
		ArrayList<State> optStates = new ArrayList<>();
		Weight minWeight = (new TropicalWeight()).zero();

		for (Entry<State, Weight> e : stateValTable.entrySet()) {

			State currentState = e.getKey();
			Weight smallestCompletionWeight = smallestCompletionWeights.get(
					currentState);
			Weight currentWeight = e.getValue().mult(smallestCompletionWeight);
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

	public static void insertTreeIntoQueueByTotalMinimumWeight(
			Node<Symbol> tree) {

		Weight wMinCurrent = getDeltaWeight(tree);

		int queueSize = treeQueue.size();
		int queueIndex = 0;

		for (int i = 0; i < queueSize; i++) {
			Node<Symbol> t = treeQueue.get(i);

			Weight wMinTemp = getDeltaWeight(t);

			if (wMinTemp.compareTo(wMinCurrent) == -1) {
				queueIndex = i + 1;
			} else if (wMinTemp.compareTo(wMinCurrent) == 0) {

				if (t.compareTo(tree) < 1) {
					queueIndex = i + 1;
				}
			}
		}

		treeQueue.add(queueIndex, tree);
	}

	public static Weight getDeltaWeight(Node<Symbol> tree) {

		Weight delta = (new TropicalWeight()).one();

		State optimalState = optimalStates.get(tree).get(0);

		Weight minWeight = treeStateValTable.get(tree, optimalState);
		Weight smallestCompletionWeight = smallestCompletionWeights.
				get(optimalState);

		delta = minWeight.mult(smallestCompletionWeight);

		return delta;
	}

	public static ArrayList<Node<Symbol>> expandWith(WTA wta,
			Node<Symbol> tree) {

		ArrayList<Node<Symbol>> expansion = new ArrayList<Node<Symbol>>();
		ArrayList<Rule<Symbol>> rules = wta.getTransitionFunction().getRules();

		for (Rule<Symbol> r : rules) {
			ArrayList<State> states = r.getStates();
			HashMap<State, ArrayList<Node<Symbol>>> producibleTrees =
					new HashMap<>();

			boolean canUseAllStates = true;
			boolean usesT = false;

			for (State s : states) {

				for (Node<Symbol> n : exploredTrees) {

					if (treeStateValTable.get(n, s) != null) {

						if (!producibleTrees.containsKey(s)) {
							producibleTrees.put(s,
									new ArrayList<Node<Symbol>>());
						}

						ArrayList<Node<Symbol>> treesForState =
								producibleTrees.get(s);

						if (!treesForState.contains(n)) {
							treesForState.add(n);

							if (n.equals(tree)) {
								usesT = true;
							}
						}
					}
				}

				if (!producibleTrees.containsKey(s)) {
					canUseAllStates = false;
					break; // ugly, fix if time
				}
			}

			if (canUseAllStates && usesT) {

				int nOfStates = states.size();

				int[] indices = new int[nOfStates];
				int[] maxIndices = new int[nOfStates];

				int combinations = 1;

				for (int i = 0; i < nOfStates; i++) {
					int nOfTreesForState =
							producibleTrees.get(states.get(i)).size();
					indices[i] = 0;
					maxIndices[i] = nOfTreesForState - 1;
					combinations *= nOfTreesForState;
				}

				for (int i = 0; i < combinations; i++ ) {

					Node<Symbol> expTree = new Node<Symbol>(r.getSymbol());
					boolean hasT = false;

					for (int j = 0; j < nOfStates; j++) {
						State currentState = states.get(j);
						ArrayList<Node<Symbol>> trees =
								producibleTrees.get(currentState);
						Node<Symbol> currentTree = trees.get(indices[j]);
						expTree.addChild(currentTree);

						if (currentTree.equals(tree)) {
							hasT = true;
						}
					}

					if (hasT && !expansion.contains(expTree)) {
						expansion.add(expTree);
					}

					boolean increased = false;
					int index = 0;

					while (!increased && index < nOfStates) {

						if (indices[index] == maxIndices[index]) {
							indices[index] = 0;
						} else {
							indices[index]++;
							increased = true;
						}

						index++;
					}
				}
			}
		}

		return expansion;
	}

}
