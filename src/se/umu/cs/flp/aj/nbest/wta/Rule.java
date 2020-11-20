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
	private State resultingState;
	private int nonTermIndex = 0;

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
	
	/* Applies this rule to the input list of trees. */
	public TreeKeeper2 apply(Configuration<TreeKeeper2> config) {
		TreeKeeper2 result = null; 
		Node t = tree;
		Weight treeWeight = this.weight;
		TreeKeeper2[] tklist = config.getValues();
		nonTermIndex = 0;
		Node newTree = buildTree(t, tklist);
		treeWeight = treeWeight.mult(config.getWeight());
		result = new TreeKeeper2(newTree, treeWeight, resultingState);
//		result.setStateUsage(computeStateUsage(tklist));
		return result;
	}

	/* Recursively builds the tree by combining the input list of trees
	 * in the by this rule specified manner. 
	 * Requires nonTermIndex to be set to zero beforehand. */
	private Node buildTree(Node t, TreeKeeper2[] tklist) {

		if (t.getChildCount() == 0) {
			Node node;

			if (t.getLabel().isNonterminal()) {
				node = tklist[nonTermIndex].getTree();
			} else {
				node = t;
			}

			return node;
		}

//		Node newTree = new Node(t.getLabel());
//
//		for (int i = 0; i < t.getChildCount(); i++) {
//			Node tempTree = buildTree(t.getChildAt(i), tklist);
//			if (t.getChildAt(i).getLabel().isNonterminal()) {
//				nonTermIndex++;
//			}
//			newTree.addChild(tempTree);
//		}

//		return newTree;
		return new Node();
	}
	
//	private HashMap<State, Integer> computeStateUsage(TreeKeeper2[] tklist) {
//		HashMap<State, Integer> stateUsage = new HashMap<State, Integer>();
//		
//		for (TreeKeeper2 tk: tklist) {
//			HashMap<State, Integer> currentSU = tk.getStateUsage();
//			for (Entry<State, Integer> entry : currentSU.entrySet()) {
//				if (stateUsage.containsKey(entry.getKey())) {
//					int oldValue = stateUsage.get(entry.getKey());
//					int newValue = oldValue + entry.getValue();
//					stateUsage.put(entry.getKey(), newValue);
//				} else {
//					stateUsage.put(entry.getKey(), entry.getValue());
//				}
//			}
//		}
//		
//		if (!stateUsage.containsKey(resultingState)) {
//			stateUsage.put(resultingState, 1);
//		}
//		
//		return stateUsage;
//	}

	public void addState(State state) {
		this.states.add(state);
		rank++;
	}

	public Node getTree() {
		return tree;
	}

	public Weight getWeight() {
		return weight;
	}

	public int getNumberOfStates() {
		return rank;
	}

	public State getResultingState() {
		return resultingState;
	}

	public ArrayList<State> getStates() {
		return states;
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
