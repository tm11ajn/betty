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
import java.util.LinkedList;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.Node;
import se.umu.cs.flp.aj.nbest.treedata.TreeKeeper2;
import se.umu.cs.flp.aj.nbest.util.Hypergraph;

public class Rule extends Hypergraph.Edge<State> {

//	private Symbol symbol;
	private Weight weight;
	private int rank = 0;
	private Node tree;

	private ArrayList<State> states = new ArrayList<>();
//	private HashMap<State, Integer> stateMap = new HashMap<>();
	private HashMap<State, ArrayList<Integer>> stateMap = new HashMap<>();

	private State resultingState;

	private int hashCode;
	private boolean validHash;

	public Rule(Node tree, Weight weight,
			State resultingState, State ... states) {
		super();

//		this.symbol = symbol;
		this.tree = tree;
		this.weight = weight;
		this.resultingState = resultingState;

		for (State state : states) {
			this.states.add(state);
			addToStateMap(state, rank);
			rank++;
		}

		validHash = false;
	}

//	public Rule(Symbol symbol, Weight weight, State resultingState,
//			State ... states) {
//
////		Rule(symbol, weight, resultingState, null, states);
//
//		this.symbol = symbol;
//		this.weight = weight;
//		this.tree = null;
//		this.resultingState = resultingState;
//
//		for (State state : states) {
//			this.states.add(state);
//			addToStateMap(state, rank);
//			rank++;
//		}
//	}

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

	public TreeKeeper2 apply(ArrayList<TreeKeeper2> tklist) {
		Node t = tree;
		Weight treeWeight = this.weight;

		ArrayList<Rule> usedRules = new ArrayList<>();

		for (TreeKeeper2 tk : tklist) {
			treeWeight = treeWeight.mult(tk.getRunWeight());
//			treeWeight = treeWeight.mult(tk.getOptWeight());

			usedRules.addAll(tk.getUsedRules());
		}

		usedRules.add(this);

		LinkedList<TreeKeeper2> copy = new LinkedList<>(tklist);

//		for (TreeKeeper2 tk : tklist) {
//			copy.addLast(tk);
//		}

		Node newTree = buildTree(t, copy);

		return new TreeKeeper2(newTree, treeWeight, resultingState, usedRules);
	}

	private Node buildTree(Node t, LinkedList<TreeKeeper2> tklist) {

		if (t.getChildCount() == 0) {
			Node node;

			if (t.getLabel().isNonterminal()) {
				node = tklist.poll().getTree();
			} else {
				node = new Node(t.getLabel());
			}

			return node;
		}

		Node newTree = new Node(t.getLabel());

		for (int i = 0; i < t.getChildCount(); i++) {
			Node tempTree = buildTree(t.getChildAt(i), tklist);
			newTree.addChild(tempTree);
		}

		return newTree;
	}

	public void addState(State state) {
		this.states.add(state);
		addToStateMap(state, rank);
		rank++;
		validHash = false;
	}

	private void addToStateMap(State state, int index) {
		if (!this.stateMap.containsKey(state)) {
			this.stateMap.put(state, new ArrayList<>());
		}

		this.stateMap.get(state).add(index);
	}

	public Node getTree() {
		return tree;
	}

	public Weight getWeight() {
		return weight;
	}

//	public int getRank() {
//		return rank;
//	}

	public int getNumberOfStates() {
		return states.size();
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

		if (obj instanceof Rule) {
			Rule rule = (Rule) obj;

			if (this.getID() == rule.getID()) {
				return true;
			}

//			if (rule.toString().equals(this.toString())) {
//				return true;
//			}

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

//		if (!validHash) {
//
////		int hash = 7*tree.getLabel().hashCode() + 11*resultingState.hashCode();
//			hashCode = 7*tree.hashCode() + 11*resultingState.hashCode();
//
//			for (State s : states) {
//				hashCode += s.hashCode();
//			}
//
//			// Added when using string for equal
//			hashCode += weight.hashCode();
//
//			validHash = true;
//		}
//
////System.out.println("Hash for " + this + " is " + hash);
//
//		return hashCode;
		return this.getID();
	}


	@Override
	public String toString() {

//		String stateString = "";
		String weightString = "";

//		if (!states.isEmpty()) {
//			stateString += "[";
//
//			int nOfStates = states.size();
//			stateString += states.get(0).toString();
//
//			for (int i = 1; i < nOfStates; i++) {
//				stateString += ", " + states.get(i);
//			}
//
//			stateString += "]";
//		}

		if (!weight.isOne()) {
			weightString = " # " + weight;
		}

		return resultingState + " -> " + tree + weightString;
//		return tree + " -> " + resultingState + weightString;
	}

//	private class Application {
//		Node tree;
//		Weight weight;
//
//		public Application(Node tree, Weight weight) {
//			this.tree = tree;
//			this.weight = weight;
//		}
//
//		public Node getTree() {
//			return tree;
//		}
//
//		public Weight getWeight() {
//			return weight;
//		}
//	}

}
