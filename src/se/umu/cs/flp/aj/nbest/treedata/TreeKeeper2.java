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

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.State;

public class TreeKeeper2 implements Comparable<TreeKeeper2> {

//	private static Context[] smallestCompletions;
//	private static HashMap<Node, HashMap<State, Weight>> optWeights =
//			new HashMap<>();

	private Node tree;
	private Weight runWeight;
	private State resultingState;
//	private HashMap<State, Integer> stateUsage;
	private boolean outputted;

	public TreeKeeper2(Node tree, Weight treeWeight, State resultingState) {
		this.tree = tree;
		this.runWeight = treeWeight.duplicate();
		this.resultingState = resultingState;
		this.outputted = false;
//		this.stateUsage = new HashMap<State, Integer>();
//		addStateWeight(resultingState, treeWeight);
	}

//	public static void init(Context[] smallestCompletions2) {
//		smallestCompletions = smallestCompletions2;
//	}

	public Node getTree() {
		return tree;
	}

	public Weight getRunWeight() {
		return this.runWeight;
	}

	public State getResultingState() {
		return resultingState;
	}
	
//	public HashMap<State, Integer> getStateUsage() {
//		return stateUsage;
//	}
//	
//	public void setStateUsage(HashMap<State, Integer> stateUsage) {
//		this.stateUsage = stateUsage;
//	}
	
	public void markAsOutputted() {
		this.outputted = true;
	}
	
	public boolean hasBeenOutputted() {
		return this.outputted;
	}

//	private void addStateWeight(State s, Weight w) {
//
//		if (!optWeights.containsKey(tree)) {
//			optWeights.put(tree, new HashMap<>());
//		}
//
//		HashMap<State, Weight> treeWeights = optWeights.get(tree);
//
//		if (!treeWeights.containsKey(s) || treeWeights.get(s).compareTo(w) > 0) {
//			treeWeights.put(s, w);
//		}
//	}
	
//	public int getStateUsageInBestContext(State s) {
//		return smallestCompletions[resultingState.getID()].getStateOccurrence(s);
//	}
	
	public Context getBestContext() {
		return resultingState.getBestContext();
//		return smallestCompletions[resultingState.getID()];
	}

	public Weight getDeltaWeight() {
		return runWeight.mult(resultingState.getBestContext().getWeight());
//		return runWeight.mult(smallestCompletions[resultingState.getID()].getWeight());
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
		
		int weightComparison = this.getDeltaWeight().compareTo(o.getDeltaWeight());

		if (weightComparison != 0) {
			return false;
		}
		
		int runWeightComparison = this.getRunWeight().compareTo(o.getRunWeight());
		
		if (runWeightComparison != 0) {
			return false;
		}
		
		if (!this.getTree().equals(o.getTree())) {
			return false;
		}

//		if (this.compareTo(o) == 0) {
//			return true;
//		}

		return true;
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
		
		return treeComparison;

//		int stateComparison = this.resultingState.compareTo(o.resultingState);

//		return stateComparison;
	}
}

