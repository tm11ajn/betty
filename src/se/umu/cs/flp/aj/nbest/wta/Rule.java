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

package se.umu.cs.flp.aj.nbest.wta;

import java.util.ArrayList;
import java.util.HashMap;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.Node;

public class Rule<LabelType extends Comparable<LabelType>> {

	private LabelType symbol;
	private Weight weight;
	private int rank = 0;
	private Node<LabelType> tree;

	private ArrayList<State> states = new ArrayList<>();
//	private HashMap<State, Integer> stateMap = new HashMap<>();
	private HashMap<State, ArrayList<Integer>> stateMap = new HashMap<>();

	private State resultingState;

	public Rule(LabelType symbol, Weight weight, State resultingState,
			Node<LabelType> tree, State ... states) {

		this.symbol = symbol;
		this.weight = weight;
		this.tree = tree;
		this.resultingState = resultingState;

		for (State state : states) {
			this.states.add(state);
			addToStateMap(state, rank);
			rank++;
		}
	}

	public Rule(LabelType symbol, Weight weight, State resultingState,
			State ... states) {

//		Rule(symbol, weight, resultingState, null, states);

		this.symbol = symbol;
		this.weight = weight;
		this.tree = null;
		this.resultingState = resultingState;

		for (State state : states) {
			this.states.add(state);
			addToStateMap(state, rank);
			rank++;
		}
	}

//	public Rule(LabelType symbol, State resultingState, State ... states) {
//
//		this.symbol = symbol;
//		weight = new TropicalWeight(0);
//		this.resultingState = resultingState;
//
//		for (State state : states) {
//			this.states.add(state);
//			this.stateMap.put(state, state);
//			rank++;
//		}
//
//	}

	public void addState(State state) {
		this.states.add(state);
		addToStateMap(state, rank);
		rank++;
	}

	private void addToStateMap(State state, int index) {
		if (!this.stateMap.containsKey(state)) {
			this.stateMap.put(state, new ArrayList<>());
		}

		this.stateMap.get(state).add(index);
	}

	public LabelType getSymbol() {
		return symbol;
	}

	public Weight getWeight() {
		return weight;
	}

	public int getRank() {
		return rank;
	}

	public boolean hasState(State state) {
		return stateMap.containsKey(state);
	}

	public State getResultingState() {
		return resultingState;
	}

	public ArrayList<State> getStates() {
		return states;
	}

	public ArrayList<Integer> getIndexOfState(State state) {
		return stateMap.get(state);
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Rule<?>) {
			Rule<?> rule = (Rule<?>) obj;

			if (rule.toString().equals(this.toString())) {
				return true;
			}

//			if (rule.symbol.equals(this.symbol)
//					&& rule.resultingState.equals(this.resultingState)
//					&& rule.states.size() == this.states.size()) {
//
//				int statesSize = this.states.size();
//
//				for (int i = 0; i < statesSize; i++) {
//
//					if (!rule.states.get(i).equals(this.states.get(i))) {
//						return false;
//					}
//				}
//
//				return true;
//			}
		}

		return false;
	}

	@Override
	public int hashCode() {

		int hash = 7*symbol.hashCode() + 11*resultingState.hashCode();

		for (State s : states) {
			hash += s.hashCode();
		}

		// Added when using string for equal
		hash += weight.hashCode();

		return hash;
	}


	@Override
	public String toString() {

		String stateString = "";
		String weightString = "";

		if (!states.isEmpty()) {
			stateString += "[";

			int nOfStates = states.size();
			stateString += states.get(0).toString();

			for (int i = 1; i < nOfStates; i++) {
				stateString += ", " + states.get(i);
			}

			stateString += "]";
		}

		if (!weight.isOne()) {
			weightString = " # " + weight;
		}

		return symbol + stateString + " -> " + resultingState + weightString;
	}

}
