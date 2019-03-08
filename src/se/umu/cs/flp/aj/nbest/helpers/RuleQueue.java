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
import java.util.LinkedList;
import java.util.PriorityQueue;

import se.umu.cs.flp.aj.nbest.treedata.RuleKeeper;
import se.umu.cs.flp.aj.nbest.treedata.TreeKeeper2;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;

public class RuleQueue {

	private ArrayList<LinkedList<TreeKeeper2>> elements;
	private HashMap<State, Integer> stateElementIndex;

//	private WTA wta;
	private PriorityQueue<RuleKeeper> queue;
//	private HashMap<State, PriorityQueue<RuleKeeper>> stateQueues;
//	private HashMap<State, Boolean> stateQueuesUsage;
	private HashMap<Rule, RuleKeeper> ruleKeepers;
	private int size;

	private int limit;
	private HashMap<State, Integer> stateUsage;

//	public RuleQueue(WTA wta, int limit) {
//	public RuleQueue(int limit) {
	public RuleQueue(int limit, ArrayList<Rule> startRules) {
//		this.wta = wta;
		this.elements = new ArrayList<>();
		this.stateElementIndex = new HashMap<>();

		this.queue = new PriorityQueue<>();
//		this.stateQueues = new HashMap<>();
//		this.stateQueuesUsage = new HashMap<>();
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
//System.out.println("Start rule: " + r);
		}

//		for (State s : stateQueues.keySet()) {
//			queue.add(stateQueues.get(s).poll());
//			stateQueuesUsage.put(s, true);
//		}

		this.limit = limit;
		this.stateUsage = new HashMap<>();
	}

	private void addRule(Rule rule) {
//System.out.println("Add rule " + rule);

		ArrayList<State> states = rule.getStates();
		ArrayList<Integer> elementIndices = new ArrayList<>();

		for (State s : states) {
			controlInitState(s);
			elementIndices.add(stateElementIndex.get(s));
		}

		RuleKeeper keeper = new RuleKeeper(rule, limit,
				elements, elementIndices);
		ruleKeepers.put(rule, keeper);

		if (!keeper.isPaused()) {
//System.out.println("Adding to rule queue: " + rule);
			queue.add(keeper);
//System.out.println("Enqueues tree " + keeper.getSmallestTree());
//System.out.println("For rule " + keeper.getRule());
//			insertIntoStateQueues(keeper);
			keeper.setQueued(true);
		}

		size++;
	}

//	private void insertIntoStateQueues(RuleKeeper keeper) {
//		State resState = keeper.getRule().getResultingState();
//		if (!stateQueues.containsKey(resState)) {
//			stateQueues.put(resState, new PriorityQueue<>());
//		}
//
//		stateQueues.get(resState).add(keeper);
//	}

	private void controlInitState(State s) {
		if (!stateElementIndex.containsKey(s)) {
			elements.add(new LinkedList<>());
			stateElementIndex.put(s, elements.size() - 1);
		}
	}

	public void expandWith(TreeKeeper2 newTree) {
		State state = newTree.getResultingState();

System.out.println("Expand for " + state + " with " + newTree);

		if (!stateUsage.containsKey(state)) {
			stateUsage.put(state, 0);
		}

		controlInitState(state);
		int size = elements.get(stateElementIndex.get(state)).size();

//if (size > 0) {
//TreeKeeper2 old = elements.get(stateElementIndex.get(state)).getLast();
//if (old.getOptWeight().compareTo(newTree.getOptWeight()) == 1) {
//System.out.println("AJAJ: current weight=" + old.getOptWeight() + " new weight=" + newTree.getOptWeight());
////System.exit(1);
//}
//}

if (size > 0) {
TreeKeeper2 old = elements.get(stateElementIndex.get(state)).getLast();
if (old.getRunWeight().compareTo(newTree.getRunWeight()) == 1) {
System.out.println("AJAJ: current weight=" + old.getRunWeight() + " new weight=" + newTree.getRunWeight());
//System.exit(1);
}
}

		elements.get(stateElementIndex.get(state)).add(newTree);

		if (stateUsage.get(state) < limit) {
//			for (Rule rule : wta.getRulesByState(state)) {
			for (Rule rule : state.getOutgoing()) {
//System.out.println("For rule " + rule);
				if (!ruleKeepers.containsKey(rule)) {
					addRule(rule);
				}

				RuleKeeper currentKeeper = ruleKeepers.get(rule);
				ArrayList<Integer> stateIndices = rule.getIndexOfState(state);
//System.out.println("State indices for rule: " + rule.getIndexOfState(state));

				for (Integer index : stateIndices) {
//					currentKeeper.addTreeForStateIndex(newTree, index);
					currentKeeper.updateForStateIndex(index);
				}

				if (!currentKeeper.isQueued() && !currentKeeper.isPaused()) {
					currentKeeper.next();
					queue.add(currentKeeper);
//System.out.println("Enqueues tree " + currentKeeper.getSmallestTree());
//System.out.println("For rule " + currentKeeper.getRule());
//					insertIntoStateQueues(currentKeeper);
//					State resState = rule.getResultingState();
//					if (!stateQueuesUsage.containsKey(resState) || !stateQueuesUsage.get(resState)) {
//						queue.add(stateQueues.get(resState).poll());
//						stateQueuesUsage.put(resState, true);
//					}
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

System.out.println("Dequeues tree " + nextTree);
System.out.println("For rule " + ruleKeeper.getRule());

		if (!ruleKeeper.isPaused()) {
			queue.add(ruleKeeper);
//System.out.println("Enqueues tree " + ruleKeeper.getSmallestTree());
//System.out.println("For rule " + ruleKeeper.getRule());
//			insertIntoStateQueues(ruleKeeper);
			ruleKeeper.setQueued(true);
		}

//		State resState = ruleKeeper.getRule().getResultingState();
//		stateQueuesUsage.put(resState, false);
//
//		if (!stateQueues.get(resState).isEmpty()) {
//			queue.add(stateQueues.get(resState).poll());
//			stateQueuesUsage.put(resState, true);
//		}

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