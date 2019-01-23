/*
 * Copyright 2018 Anna Jonsson for the research group Foundations of Language
 * Processing, Department of Computing Science, Umeå university
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

package se.umu.cs.flp.aj.nbest.treedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.Symbol;

public class TreeKeeper2 implements Comparable<TreeKeeper2> {

	private static HashMap<State, Weight> smallestCompletions;

	private static HashMap<Node, LinkedHashMap<State, State>> optimalStates =
			new HashMap<>();
	private static HashMap<Node, State> optimalState = new HashMap<>();
	private static HashMap<Node, HashMap<State, Weight>> optWeights =
			new HashMap<>();
	private static HashMap<Node, Weight> smallestWeight =
			new HashMap<>();

	private Node tree;
	private Weight runWeight;
	private State resultingState;
	private boolean queueable;

	// Remove this constructor in the end
	public TreeKeeper2(Symbol ruleLabel, Weight ruleWeight,
			State resultingState,
			ArrayList<TreeKeeper2> trees) {

		this.tree = new Node(ruleLabel);
		this.resultingState = resultingState;
		this.queueable = false;

		Weight temp = ruleWeight.mult(ruleWeight.one());

		for (TreeKeeper2 currentTree : trees) {
			tree.addChild(currentTree.getTree());
			temp = temp.mult(currentTree.getRunWeight());
		}

		this.runWeight = temp;
		addStateWeight(resultingState, temp);

//System.out.println(tree);
//System.out.println(resultingState);
//System.out.println(resultingState.isFinal());
//System.out.println(runWeight);
//System.exit(1);
	}

	public TreeKeeper2(Node tree, Weight treeWeight,
			State resultingState) {
		this.tree = tree;
		this.runWeight = treeWeight.duplicate();
		this.resultingState = resultingState;
		this.queueable = false;
		addStateWeight(resultingState, treeWeight);
//System.out.println(tree);
//System.out.println(resultingState);
//System.out.println(resultingState.isFinal());
//System.out.println(runWeight);
//System.exit(1);
	}

	public static void init(HashMap<State, Weight> smallestCompletionWeights) {
		smallestCompletions = smallestCompletionWeights;
	}

	public Node getTree() {
		return tree;
	}

	public Weight getRunWeight() {
		return this.runWeight;
	}

	public State getResultingState() {
		return resultingState;
	}

	public LinkedHashMap<State, State> getOptimalStates() {
		return optimalStates.get(tree);
	}

	public Weight getOptimalWeight(State s) {
		return optWeights.get(tree).get(s);
	}

	private void addStateWeight(State s, Weight w) {

		if (!optWeights.containsKey(tree)) {
			optWeights.put(tree, new HashMap<>());
		}

		HashMap<State, Weight> treeWeights = optWeights.get(tree);

		if (!treeWeights.containsKey(s)) {
			this.queueable = true;
			treeWeights.put(s, w);

			Weight newDeltaWeight = w.mult(smallestCompletions.get(s));

			if (optimalState.get(tree) == null) {
				LinkedHashMap<State, State> newMap = new LinkedHashMap<>();
				newMap.put(s, s);
				optimalStates.put(tree, newMap);
				optimalState.put(tree, s);
				smallestWeight.put(tree, w);
			} else {
				Weight oldDeltaWeight = getDeltaWeight();

				if (newDeltaWeight.compareTo(oldDeltaWeight) == -1) {
					LinkedHashMap<State, State> newMap = new LinkedHashMap<>();
					newMap.put(s, s);
					optimalStates.put(tree, newMap);
					optimalState.put(tree, s);
					smallestWeight.put(tree, w);
				} else if (newDeltaWeight.compareTo(oldDeltaWeight) == 0) {
					optimalStates.get(tree).put(s, s);
				}
			}
		}
	}

	public boolean isQueueable() {
		return queueable;
	}

	public Weight getSmallestWeight() {
		return smallestWeight.get(tree);
	}

	public Weight getDeltaWeight() {
		return smallestWeight.get(tree).mult(
				smallestCompletions.get(optimalState.get(tree)));
	}

	@Override
	public int hashCode() {
		return this.tree.hashCode();
	}

	@Override
	public String toString() {

		String optStatesString = "";

		if (optimalStates != null) {
			optStatesString = optimalStates.toString();
		}

		return "Tree: " + tree + " RunWeight: " + runWeight +
				" Optstates: " + optStatesString +
				" Optstate: " + optimalState +
				" Optweights: " + optWeights;
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof TreeKeeper2)) {
			return false;
		}

		TreeKeeper2 o = (TreeKeeper2) obj;
		int weightComparison = runWeight.compareTo(o.runWeight);
		int treeComparison = this.tree.compareTo(o.tree);

		if (weightComparison == 0 && treeComparison == 0) {
			return true;
		}

		return false;
	}

	@Override
	public int compareTo(TreeKeeper2 o) {
		int weightComparison = runWeight.compareTo(o.runWeight);

		if (weightComparison == 0) {
			return this.tree.compareTo(o.tree);
		}

		return weightComparison;
	}
}

