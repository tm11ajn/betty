/*
 * Copyright 2018 Anna Jonsson for the research group Foundations of Language
 * Processing, Department of Computing Science, Umeå university
 *
 * This file is part of Betty.
 *
 * Betty is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Betty is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.umu.cs.flp.aj.nbest.treedata;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.State;

public class TreeKeeper2 implements Comparable<TreeKeeper2> {

	private static Context[] bestContexts;

	private Node tree;
	private Weight runWeight;
	private State resultingState;
	private boolean outputted;

	public TreeKeeper2(Node tree, Weight treeWeight, State resultingState) {
		this.tree = tree;
		this.runWeight = treeWeight.duplicate();
		this.resultingState = resultingState;
		this.outputted = false;
	}

	public static void init(Context[] bestContexts2) {
		bestContexts = bestContexts2;
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
	
	public void markAsOutputted() {
		this.outputted = true;
	}
	
	public boolean hasBeenOutputted() {
		return this.outputted;
	}
	
	public Context getBestContext() {
		return bestContexts[resultingState.getID()];
	}

	public Weight getDeltaWeight() {		
		return runWeight.mult(bestContexts[resultingState.getID()].getWeight());
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
		
		int stateDepthComparison = bestContexts[resultingState.getID()].getDepth();

		if (stateDepthComparison != 0) {
			return false;
		}

		return true;
	}

	@Override
	public int compareTo(TreeKeeper2 o) {
		int weightComparison = this.getDeltaWeight().compareTo(o.getDeltaWeight());

		if (weightComparison != 0) {
			return weightComparison;
		}
		
		int stateDepthComparison = bestContexts[resultingState.getID()].getDepth();
		return stateDepthComparison;
	}
}

