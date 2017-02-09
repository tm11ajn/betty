/*
 * Copyright 2015 Anna Jonsson for the research group Foundations of Language
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

package se.umu.cs.flp.aj.nbest.data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import se.umu.cs.flp.aj.wta.State;
import se.umu.cs.flp.aj.wta.Weight;

public class TreeKeeper<LabelType extends Comparable<LabelType>> 
		implements Comparable<TreeKeeper<?>> {
	
	private static HashMap<State, Weight> smallestCompletions;
	
	private Node<LabelType> tree;
	private LinkedHashMap<State,State> optimalStates;
	private State optimalState;
	private HashMap<State, Weight> optWeights; 
	private Weight smallestWeight;
	
	public TreeKeeper(Node<LabelType> tree) {
		this.tree = tree;
		this.optWeights = new HashMap<>();
		this.smallestWeight = new Weight(Weight.INF);
	}
	
	public static void init(HashMap<State, Weight> smallestCompletionWeights) {
		smallestCompletions = smallestCompletionWeights;
	}
	
	public Node<LabelType> getTree() {
		return tree;
	}
	
	public LinkedHashMap<State, State> getOptimalStates() {
		return optimalStates;
	}
	
	public Weight getWeight(State s) {
		return optWeights.get(s);
	}
	
	public void addWeight(State s, Weight w) {
		
		if (this.optWeights.containsKey(s)) {				
			if (this.optWeights.get(s).compareTo(w) == 1) {
				this.optWeights.put(s, w);
			}
		} else {
			this.optWeights.put(s, w);
		}
		
		if (w.compareTo(smallestWeight) == -1) {
			optimalStates = new LinkedHashMap<>();
			optimalStates.put(s, s);
			optimalState = s;
			smallestWeight = w;
		} else if (w.compareTo(smallestWeight) == 0) {
			optimalStates.put(s, s);
		}
	}
	
	public void addWeightsFrom(TreeKeeper<LabelType> t) {
		HashMap<State, Weight> map = t.optWeights;
				
		for (Entry<State, Weight> e : map.entrySet()) {
			addWeight(e.getKey(), e.getValue());
		}
	}
	
	public Weight getSmallestWeight() {
		return smallestWeight;
	}
	
	public Weight getDeltaWeight() {
		return smallestWeight.add(smallestCompletions.get(optimalState)); 
	}

	@Override
	public int compareTo(TreeKeeper<?> o) { 		
		int weightComparison = this.smallestWeight.compareTo(o.smallestWeight);
		
		if (weightComparison == 0) {
			return this.tree.compareTo(o.tree);
		}
		
		return weightComparison;
	}
	
	@Override
	public int hashCode() {
//		return this.tree.hashCode() + 3 * this.smallestWeight.hashCode();
		return this.tree.hashCode();
	}
	
	@Override
	public String toString() { 
		
		String optStatesString = "";
		
		if (optimalStates != null) {
			optStatesString = optimalStates.toString();
		}
		
		return tree.toString() + optStatesString + optWeights.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (!(obj instanceof TreeKeeper<?>)) {
			return false;
		}
		
		return this.compareTo((TreeKeeper<?>) obj) == 0;
	}
}
