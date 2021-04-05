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

public class Tree implements Comparable<Tree> {

	private static Context[] bestContexts;

	private Node node;
	private Weight runWeight;
	private State resultingState;
	private boolean outputted;

	public Tree(Node node, Weight treeWeight, State resultingState) {
		this.node = node;
		this.runWeight = treeWeight.duplicate();
		this.resultingState = resultingState;
		this.outputted = false;
	}

	public static void init(Context[] bestContexts2) {
		bestContexts = bestContexts2;
	}

	public Node getNode() {
		return node;
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
		return this.node.hashCode();
	}

	@Override
	public String toString() {
		String s =  "Tree: " + node + " RunWeight: " + runWeight;
		if (bestContexts != null) {
			s += " Delta weight: " + getDeltaWeight();
		}
		s += " Resulting state: " + resultingState;
		return s;
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof Tree)) {
			return false;
		}

		Tree o = (Tree) obj;
		
		int weightComparison = this.getDeltaWeight().compareTo(o.getDeltaWeight());

		if (weightComparison != 0) {
			return false;
		}
		
		int thisDepth = bestContexts[this.resultingState.getID()].getDepth();
		int oDepth = bestContexts[o.resultingState.getID()].getDepth();
		
		if (thisDepth != oDepth) {
			return false;
		}

		return true;
	}

	@Override
	public int compareTo(Tree o) {
		int weightComparison = this.getDeltaWeight().compareTo(o.getDeltaWeight());

		if (weightComparison != 0) {
			return weightComparison;
		}
		
		int thisDepth = bestContexts[this.resultingState.getID()].getDepth();
		int oDepth = bestContexts[o.resultingState.getID()].getDepth();
		
		if (thisDepth < oDepth) {
			return -1;
		} else if (thisDepth > oDepth) {
			return 1;
		}
		return 0;
	}
}

