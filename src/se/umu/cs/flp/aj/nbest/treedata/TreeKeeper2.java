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
 *
 * Created in 2018 by aj.
 * Modified in 2018 by aj.
 */

package se.umu.cs.flp.aj.nbest.treedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.State;

public class TreeKeeper2<LabelType extends Comparable<LabelType>>
		implements Comparable<TreeKeeper2<LabelType>> {

	private static HashMap<State, Weight> smallestCompletions;

	private static HashMap<Node<?>, LinkedHashMap<State, State>> optimalStates =
			new HashMap<>();
	private static HashMap<Node<?>, State> optimalState = new HashMap<>();
//	private static LinkedHashMap<Node<LabelType>, State> optimalStates;
	private static HashMap<Node<?>, HashMap<State, Weight>> optWeights =
			new HashMap<>();
	private static HashMap<Node<?>, Weight> smallestWeight =
			new HashMap<>();

	private Node<LabelType> tree;
	private Weight runWeight;

//	private LinkedHashMap<State,State> optimalStates;
//	private State optimalState;
//	private HashMap<State, Weight> optWeights;
//	private Weight smallestWeight;

//	public TreeKeeper2(Node<LabelType> tree, Semiring semiring) {
//		this.tree = tree;
//
//		if (!smallestWeight.containsKey(tree)) {
//			smallestWeight.put(tree, semiring.zero());
//		}
//
//		if (!optWeights.containsKey(tree)) {
//			optWeights.put(tree, new LinkedHashMap<>());
//		}
//
//		if (!optimalStates.containsKey(tree)) {
//			optimalStates.put(tree, new LinkedHashMap<>());
//		}
////		this.optWeights = new HashMap<>();
////		this.smallestWeight = semiring.zero();
//	}

	public TreeKeeper2(LabelType ruleLabel, Weight ruleWeight,
			State resultingState,
			ArrayList<TreeKeeper2<LabelType>> trees) {

		this.tree = new Node<LabelType>(ruleLabel);

		Weight temp = ruleWeight;

		for (TreeKeeper2<LabelType> currentTree : trees) {
			tree.addChild(currentTree.getTree());

			//temp = temp.mult(currentTree.getSmallestWeight());
			temp = temp.mult(currentTree.getRunWeight());

		}

		this.runWeight = temp;

//		if (!optWeights.containsKey(tree)) {
//			optWeights.put(tree, new HashMap<>());
//			smallestWeight.put(tree, temp);
//		} else if (smallestWeight.get(tree).compareTo(temp) == 1) {
//			smallestWeight.put(tree, temp);
//		}
//
//		if (!optimalStates.containsKey(tree)) {
//			optimalStates.put(tree, new LinkedHashMap<>());
//		}

System.out.println("Creating new tree: " + tree);

		addStateWeight(resultingState, temp);
	}

	public static void init(HashMap<State, Weight> smallestCompletionWeights) {
		smallestCompletions = smallestCompletionWeights;
	}

	public Node<LabelType> getTree() {
		return tree;
	}

	public void setRunWeight(Weight weight) {
		this.runWeight = weight;
	}

	public Weight getRunWeight() {
		return this.runWeight;
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

		if (!optimalStates.containsKey(tree)) {
			optimalStates.put(tree, new LinkedHashMap<>());
		}

		HashMap<State, Weight> treeWeights = optWeights.get(tree);

		if (treeWeights.containsKey(s)) {
			if (w.compareTo(treeWeights.get(s)) == -1) {
				treeWeights.put(s, w);
			}
		} else {
			treeWeights.put(s, w);
		}

		Weight currentSmallestWeight = smallestWeight.get(tree);

		if (currentSmallestWeight == null ||
				w.compareTo(currentSmallestWeight) == -1) {
			LinkedHashMap<State, State> newMap = new LinkedHashMap<>();
			newMap.put(s, s);
			optimalStates.put(tree, newMap);
			optimalState.put(tree, s);
			smallestWeight.put(tree, w);
		} else if (w.compareTo(currentSmallestWeight) == 0) {
			optimalStates.get(tree).put(s, s);
			optimalState.put(tree, s);
		}
	}

//	public void addWeightsFrom(TreeKeeper<LabelType> t) {
//		HashMap<State, Weight> map = t.optWeights;
//
//		for (Entry<State, Weight> e : map.entrySet()) {
//			addStateWeight(e.getKey(), e.getValue());
//		}
//	}

	public Weight getSmallestWeight() {
		return smallestWeight.get(tree);
	}

	public Weight getDeltaWeight() {
//		return smallestWeight.get(tree).mult(
//				smallestCompletions.get(optimalState.get(tree)));
		return runWeight.mult(
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

		if (!(obj instanceof TreeKeeper2<?>)) {
			return false;
		}

		TreeKeeper2<?> o = (TreeKeeper2<?>) obj;

//		int weightComparison = smallestWeight.get(tree).compareTo(
//				smallestWeight.get(o.tree));
		int weightComparison = runWeight.compareTo(o.runWeight);
		int treeComparison = this.tree.compareTo(o.tree);

		if (weightComparison == 0 && treeComparison == 0) {
			return true;
		}

		return false;
	}

	@Override
	public int compareTo(TreeKeeper2<LabelType> o) {
//		int weightComparison = smallestWeight.get(tree).compareTo(
//				smallestWeight.get(o.tree));
		int weightComparison = runWeight.compareTo(o.runWeight);

		if (weightComparison == 0) {
			return this.tree.compareTo(o.tree);
		}

		return weightComparison;
	}
}

