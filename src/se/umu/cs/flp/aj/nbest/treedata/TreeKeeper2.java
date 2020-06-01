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

import java.util.HashMap;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.State;

public class TreeKeeper2 implements Comparable<TreeKeeper2> {

	private static Weight[] smallestCompletions;
	private static HashMap<Node, HashMap<State, Weight>> optWeights =
			new HashMap<>();

	private Node tree;
	private Weight runWeight;
	private State resultingState;
	private HashMap<State, Integer> stateUsage;

	public TreeKeeper2(Node tree, Weight treeWeight, State resultingState) {
		this.tree = tree;
		this.runWeight = treeWeight.duplicate();
		this.resultingState = resultingState;
//		this.stateUsage = new HashMap<State, Integer>();
		addStateWeight(resultingState, treeWeight);
	}

	public static void init(Weight[] smallestCompletionWeights) {
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
	
	public HashMap<State, Integer> getStateUsage() {
		return stateUsage;
	}
	
	public void setStateUsage(HashMap<State, Integer> stateUsage) {
		this.stateUsage = stateUsage;
	}

	private void addStateWeight(State s, Weight w) {

		if (!optWeights.containsKey(tree)) {
			optWeights.put(tree, new HashMap<>());
		}

		HashMap<State, Weight> treeWeights = optWeights.get(tree);

		if (!treeWeights.containsKey(s) || treeWeights.get(s).compareTo(w) > 0) {
			treeWeights.put(s, w);
		}
	}

	public Weight getDeltaWeight() {
		return runWeight.mult(smallestCompletions[resultingState.getID()]);
	}

	@Override
	public int hashCode() {
		return this.tree.hashCode();
	}

	@Override
	public String toString() {
		return "Tree: " + tree + " RunWeight: " + runWeight +
				" Delta weight: " + getDeltaWeight() +
				" Resulting state: " + resultingState;
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof TreeKeeper2)) {
			return false;
		}

		TreeKeeper2 o = (TreeKeeper2) obj;

		if (this.compareTo(o) == 0) {
			return true;
		}

		return false;
	}

	@Override
	public int compareTo(TreeKeeper2 o) {
		int weightComparison = this.getDeltaWeight().compareTo(o.getDeltaWeight());

		if (weightComparison != 0) {
			return weightComparison;
		}
		
		int runWeightComparison = this.getRunWeight().compareTo(o.getRunWeight());
		
		if (runWeightComparison != 0) {
			return runWeightComparison;
		}

		int treeComparison = this.tree.compareTo(o.tree);

		if (treeComparison != 0) {
			return treeComparison;
		}

		int stateComparison = this.resultingState.compareTo(o.resultingState);

		return stateComparison;
	}
}

