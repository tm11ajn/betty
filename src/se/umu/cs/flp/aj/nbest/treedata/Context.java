/*
 * Copyright 2020 Anna Jonsson for the research group Foundations of Language
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

import java.util.ArrayList;
import java.util.HashMap;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;

public class Context implements Comparable<Context> {
//	private Tree bestTree;
//	private Rule bestRule;
	private Weight weight;
	private HashMap<State, Integer> occurrences;
	private int depth;
	
	private ArrayList<Integer> f;
	private ArrayList<HashMap<State, Integer>> P;
	
	public Context() {
		occurrences = new HashMap<>();
		depth = 0;
//		this.bestTree = null;
		this.weight = null;
		this.f = null;
		this.P = null;
	}
	
	public Context(Weight weight) {
		this();
		this.weight = weight;
	}
	
//	public Context(Weight weight, Tree bestTree, Rule bestRule) {
//		this(weight);
//		this.bestTree = bestTree;
//		this.bestRule = bestRule;
//	}
	
//	public Rule getBestRule() {
//		return bestRule;
//	}
//	
//	public void setBestRule(Rule bestRule) {
//		this.bestRule = bestRule;
//	}
//	
//	public Tree getBestTree() {
//		return bestTree;
//	}
//	
//	public void setBestTree(Tree bestTree) {
//		this.bestTree = bestTree;
//	}
	
	public int getDepth() {
		return depth;
	}
	
	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	public void setf(ArrayList<Integer> f) {
		this.f = f;
	}
	
	public void setP(ArrayList<HashMap<State, Integer>> P) {
		this.P = P;
	}
	
	public ArrayList<Integer> getf() {
		return f;
	}
	
	public ArrayList<HashMap<State, Integer>> getP() {
		return P;
	}
	
	public int getfValue() {
		return this.f.get(depth);
	}
	
	public int getPValue(State s) {
		return this.P.get(depth).get(s);
	}
	
	public void setStateOccurrence(State s, int occurrences) {
		this.occurrences.put(s, occurrences);
	}
	
	public void addStateOccurrence(State s, int occurrences) {
		if (this.occurrences.get(s) == null) {
			this.occurrences.put(s, 0);
		}
		this.occurrences.put(s, this.occurrences.get(s) + occurrences);
	}
	
	public int getStateOccurrence(State s) {
		if (this.occurrences.get(s) == null) {
			return 0;
		}
		return this.occurrences.get(s);
	}
	
	public HashMap<State, Integer> getStateOccurrences() {
		return this.occurrences;
	}
	
	public Weight getWeight() {
		return this.weight;
	}
	
	public void setWeight(Weight weight) {
		this.weight = weight;
	}

	@Override
	public int compareTo(Context o) {
		return this.weight.compareTo(o.weight);
	}

}
