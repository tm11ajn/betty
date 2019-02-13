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

package se.umu.cs.flp.aj.nbest.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import se.umu.cs.flp.aj.nbest.treedata.RuleKeeper;
import se.umu.cs.flp.aj.nbest.treedata.TreeKeeper2;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;

public class RuleQueue {

//	private WTA wta;
	private PriorityQueue<RuleKeeper> queue;
	private HashMap<Rule, RuleKeeper> ruleKeepers;
	private int size;

	private int limit;
	private HashMap<State, Integer> stateUsage;

//	public RuleQueue(WTA wta, int limit) {
//	public RuleQueue(int limit) {
	public RuleQueue(int limit, ArrayList<Rule> startRules) {
//		this.wta = wta;
		this.queue = new PriorityQueue<>();
		this.ruleKeepers = new HashMap<>();
		this.size = 0;

//		ArrayList<Rule> rules = wta.getRules();
//
//		for (Rule r : rules) {
//			RuleKeeper keeper = new RuleKeeper(r, limit);
//			ruleKeepers.put(r, keeper);
//
//			if (!keeper.isPaused()) {
//				queue.add(keeper);
//				keeper.setQueued(true);
//			}
//		}

		for (Rule r : startRules) {
			addRule(r);
		}

		this.limit = limit;
		this.stateUsage = new HashMap<>();
	}

	private void addRule(Rule rule) {
		RuleKeeper keeper = new RuleKeeper(rule, limit);
		ruleKeepers.put(rule, keeper);

		if (!keeper.isPaused()) {
//System.out.println("Adding to rule queue: " + rule);
			queue.add(keeper);
			keeper.setQueued(true);
		}

		size++;
	}

	public void expandWith(TreeKeeper2 newTree) {
		State state = newTree.getResultingState();

		if (!stateUsage.containsKey(state)) {
			stateUsage.put(state, 0);
		}

		if (stateUsage.get(state) < limit) {
//			for (Rule rule : wta.getRulesByState(state)) {
			for (Rule rule : state.getOutgoing()) {

				if (!ruleKeepers.containsKey(rule)) {
					addRule(rule);
				}

				RuleKeeper currentKeeper = ruleKeepers.get(rule);
				ArrayList<Integer> stateIndices = rule.getIndexOfState(state);

				for (Integer index : stateIndices) {
					currentKeeper.addTreeForStateIndex(newTree, index);
				}

				if (!currentKeeper.isQueued() && !currentKeeper.isPaused()) {
					currentKeeper.next();
					queue.add(currentKeeper);
					currentKeeper.setQueued(true);
				}
			}

			stateUsage.put(state, stateUsage.get(state) + 1);
		}
	}

	public TreeKeeper2 nextTree() {
		RuleKeeper ruleKeeper = queue.poll();
		TreeKeeper2 nextTree = ruleKeeper.getSmallestTree();
		ruleKeeper.setQueued(false);
		ruleKeeper.next();

		if (!ruleKeeper.isPaused()) {
			queue.add(ruleKeeper);
			ruleKeeper.setQueued(true);
		}

		return nextTree;
	}

	public boolean isEmpty() {

		if (queue.peek() == null) {
			return true;
		}

		return false;
	}

	public int size() {
		return size;
	}

	@Override
	public String toString() {
		String string = "\n";

		for (RuleKeeper r : queue) {
			string = string + r + "\n";
		}

		return string;
	}

}