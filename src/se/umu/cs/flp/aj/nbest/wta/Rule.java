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
import java.util.Map.Entry;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.Configuration;
import se.umu.cs.flp.aj.nbest.treedata.Node;
import se.umu.cs.flp.aj.nbest.treedata.TreeKeeper2;
import se.umu.cs.flp.aj.nbest.util.Hypergraph;

public class Rule extends Hypergraph.Edge<State> {

	private Weight weight;
	private int rank = 0;
	private Node tree;

	private ArrayList<State> states = new ArrayList<>();
	private HashMap<State, ArrayList<Integer>> stateMap = new HashMap<>();

	private State resultingState;

	public Rule(Node tree, Weight weight,
			State resultingState, State ... states) {
		super();

		this.tree = tree;
		this.weight = weight;
		this.resultingState = resultingState;

		for (State state : states) {
			addState(state);
		}
	}

//	public TreeKeeper2 apply(ArrayList<TreeKeeper2> tklist) {
//		Node t = tree;
//		Weight treeWeight = this.weight;
//		Node newTree = buildTree(t, tklist, 0);
//
//		for (int i = 0; i < rank; i++) {
//			treeWeight = treeWeight.mult(tklist.get(i).getRunWeight());
//		}
//
//		return new TreeKeeper2(newTree, treeWeight, resultingState);
//	}
	
	public TreeKeeper2 apply(Configuration<TreeKeeper2> config) {
System.out.println("Rule: apply");
System.out.println(config);
System.out.println(config.getValues());
		Node t = tree;
		Weight treeWeight = this.weight;
		ArrayList<TreeKeeper2> tklist = config.getValues();
		Node newTree = buildTree(t, tklist, 0);
		treeWeight = treeWeight.mult(config.getWeight());
		return new TreeKeeper2(newTree, treeWeight, resultingState);
	}

	private Node buildTree(Node t, ArrayList<TreeKeeper2> tklist,
			int nonTermIndex) {

		if (t.getChildCount() == 0) {
			Node node;

			if (t.getLabel().isNonterminal()) {
				node = tklist.get(nonTermIndex).getTree();
			} else {
				node = t;
			}

			return node;
		}

		Node newTree = new Node(t.getLabel());

		for (int i = 0; i < t.getChildCount(); i++) {
			Node tempTree = buildTree(t.getChildAt(i), tklist, nonTermIndex);
			if (t.getChildAt(i).getLabel().isNonterminal()) {
				nonTermIndex++;
			}
			newTree.addChild(tempTree);
		}

		return newTree;
	}

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
		
		if (state.getID() == 157) {
//System.out.println("WOOOOOOOOOW");
//System.out.println(index);
//System.out.println(this.stateMap.get(state));
//System.exit(1);
		}
	}

	public Node getTree() {
		return tree;
	}

	public Weight getWeight() {
		return weight;
	}

	public int getNumberOfStates() {
//		return states.size();
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

		if (obj instanceof Rule) {
			Rule rule = (Rule) obj;

			if (this.getID() == rule.getID()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public int hashCode() {
		return this.getID();
	}

	public String toWTAString() {

		String weightString = "";

		if (!weight.isOne()) {
			weightString = " # " + weight;
		}

		return tree.toWTAString() + " -> " + resultingState + weightString;
	}

	public String toRTGString() {

		String weightString = "";

		if (!weight.isOne()) {
			weightString = " # " + weight;
		}

		return resultingState + " -> " + tree.toRTGString() + weightString;
	}


	@Override
	public String toString() {
		return toWTAString();
	}

}
