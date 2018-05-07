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

package se.umu.cs.flp.aj.nbest.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import se.umu.cs.flp.aj.nbest.treedata.RuleKeeper;
import se.umu.cs.flp.aj.nbest.treedata.TreeKeeper2;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.TransitionFunction;

public class RuleQueue<LabelType extends Comparable<LabelType>> {

	private TransitionFunction<LabelType> tf;
	private LinkedList<RuleKeeper<LabelType>> queue;
	private HashMap<Rule<LabelType>, RuleKeeper<LabelType>> ruleKeepers;

	public RuleQueue(TransitionFunction<LabelType> tf) {
		this.tf = tf;
		this.queue = new LinkedList<>();
		this.ruleKeepers = new HashMap<>();

		ArrayList<Rule<LabelType>> rules = tf.getRules();

		for (Rule<LabelType> r : rules) {
			RuleKeeper<LabelType> keeper = new RuleKeeper<>(r);
			ruleKeepers.put(r, keeper);

			if (r.getRank() == 0) {
				queue.add(keeper);
			}

//			queue.add(keeper);
		}

		addRankZeroTrees();
	}

	private void addRankZeroTrees() {
		ArrayList<Rule<LabelType>> rules = tf.getRules();

		for (Rule<LabelType> r : rules) {

			if (r.getRank() == 0) {
				TreeKeeper2<LabelType> tempTree = new TreeKeeper2<LabelType>(
						r.getSymbol(), r.getWeight(),
						r.getResultingState(), new ArrayList<>());
				//queue.add(ruleKeepers.get(r));
				addTree(tempTree);
			}
		}

System.out.println("Rulequeue after enqueuing rank 0 symbols: ");
for (RuleKeeper<LabelType> q : queue) {
System.out.println("" + q);
}
	}

	public void addTree(TreeKeeper2<LabelType> newTree) {
System.out.println("IN ADDTREE");
System.out.println("adding tree " + newTree + " to rulequeue");
		for (State state : newTree.getOptimalStates().keySet()) {
System.out.println("Current state is " + state);
			for (Rule<LabelType> rule : tf.getRulesByState(state)) {
				RuleKeeper<LabelType> currentKeeper = ruleKeepers.get(rule);
System.out.println("Current rule is " + rule);

				boolean pausedBefore = currentKeeper.paused();

				ArrayList<Integer> stateIndices = rule.getIndexOfState(state);

				for (Integer index : stateIndices) {
					currentKeeper.addTreeForStateIndex(newTree, index);
				}

//				currentKeeper.addTreeForStateIndex(newTree,
//						rule.getIndexOfState(state));

				if (pausedBefore && !currentKeeper.paused()) {
					queue.add(currentKeeper);
System.out.println("Adds RULE " + currentKeeper.getRule() + " to queue");
				}

System.out.println("Adding state " + state + " of rule " + rule +
		" with tree " + newTree);
			}
		}

System.out.println("After addtree: Now rulequeue is: ");
for (RuleKeeper<LabelType> q : queue) {
System.out.println("" + q);
}
	}

	public TreeKeeper2<LabelType> nextTree() {
		RuleKeeper<LabelType> ruleKeeper = queue.pop();
		ruleKeeper.next();
		TreeKeeper2<LabelType> nextTree = ruleKeeper.getSmallestTree();

		if (!ruleKeeper.paused()) {
			queue.add(ruleKeeper);
		}
System.out.println("Ruleorganiser gets " + nextTree + " from rulekeeper");
		return nextTree;
	}

	public boolean isEmpty() {

		if (queue.peek() == null) {
			return true;
		}

		return false;
	}

}
