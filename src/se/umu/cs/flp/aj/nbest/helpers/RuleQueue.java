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

import se.umu.cs.flp.aj.heap.BinaryHeap;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.RuleKeeper;
import se.umu.cs.flp.aj.nbest.treedata.TreeKeeper2;
import se.umu.cs.flp.aj.nbest.util.LazyLimitedLadderQueue;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.WTA;

public class RuleQueue {

//	private ArrayList<LinkedList<TreeKeeper2>> elements;
	private TreeKeeper2[][] elements;
	private int[] nOfElements;
//	private HashMap<State, Integer> stateElementIndex;
	private BinaryHeap<RuleKeeper, Weight> queue;
	private ArrayList<BinaryHeap<RuleKeeper, Weight>.Node> queueElements;
	private HashMap<Rule, RuleKeeper> ruleKeepers;
	private int size;

	private int limit;
	private HashMap<State, Integer> stateUsage;

//	public RuleQueue(int limit, ArrayList<Rule> startRules, int nOfRules) {
	public RuleQueue(int limit, WTA wta) {
//		this.elements = new ArrayList<>();
		this.elements = new TreeKeeper2[wta.getStateCount() + 1][limit];
		this.nOfElements = new int[wta.getStateCount() + 1];
//		this.stateElementIndex = new HashMap<>();
		this.queue = new BinaryHeap<>();
		this.queueElements = new ArrayList<>();
		this.ruleKeepers = new HashMap<>();
		this.size = 0;

		LazyLimitedLadderQueue.init(wta.getStateCount(), limit);

		for (int i = 0; i < wta.getRuleCount(); i++) {
			queueElements.add(i, null);
		}

		for (Rule r : wta.getSourceRules()) {
			addRule(r);
		}

		this.limit = limit;
		this.stateUsage = new HashMap<>();
	}

	private void addRule(Rule rule) {
		int[] elementIndices = new int[rule.getNumberOfStates()];

//		for (State s : states) {
////			controlInitState(s);
////			elementIndices.add(stateElementIndex.get(s));
//		}

		int counter = 0;
		for (State s : rule.getStates()) {
			elementIndices[counter] = s.getID();
			counter++;
		}

		RuleKeeper keeper = new RuleKeeper(rule, limit, elements,
				elementIndices, nOfElements);
		ruleKeepers.put(rule, keeper);

		if (!keeper.isPaused()) {
			BinaryHeap<RuleKeeper, Weight>.Node n =
					queue.add(keeper, keeper.getSmallestTree().getDeltaWeight());
			queueElements.set(keeper.getRule().getID(), n);
			keeper.setQueued(true);
		}

		size++;
	}

//	private void controlInitState(State s) {
//		if (!stateElementIndex.containsKey(s)) {
//			elements.add(new LinkedList<>());
//			stateElementIndex.put(s, elements.size() - 1);
//		}
//	}

	public void expandWith(TreeKeeper2 newTree) {
		State state = newTree.getResultingState();

		if (!stateUsage.containsKey(state)) {
			stateUsage.put(state, 0);
		}

//		controlInitState(state);
//		elements.get(stateElementIndex.get(state)).add(newTree);
		int size = nOfElements[state.getID()];
		elements[state.getID()][size] = newTree;
		nOfElements[state.getID()] = size + 1;

		if (stateUsage.get(state) < limit) {
			for (Rule rule : state.getOutgoing()) {
				if (!ruleKeepers.containsKey(rule)) {
					addRule(rule);
				}

				RuleKeeper currentKeeper = ruleKeepers.get(rule);
				ArrayList<Integer> stateIndices = rule.getIndexOfState(state);

				for (Integer index : stateIndices) {
					currentKeeper.updateForStateIndex(index);

					if (currentKeeper.isQueued() &&
							currentKeeper.needsUpdate()) {
						queue.decreaseWeight(queueElements.get(currentKeeper.getRule().getID()),
								currentKeeper.getSmallestTree().
								getDeltaWeight());
						currentKeeper.hasUpdated();
					}
				}

				if (!currentKeeper.isQueued() && !currentKeeper.isPaused()) {
					currentKeeper.next();
					BinaryHeap<RuleKeeper, Weight>.Node n = queue.add(currentKeeper,
							currentKeeper.getSmallestTree().getDeltaWeight());
					queueElements.set(currentKeeper.getRule().getID(), n);
					currentKeeper.setQueued(true);
				}
			}

			stateUsage.put(state, stateUsage.get(state) + 1);
		}

	}

	public TreeKeeper2 nextTree() {
		RuleKeeper ruleKeeper = queue.dequeue().getObject();
		ruleKeeper.hasBeenDequeued();
		TreeKeeper2 nextTree = ruleKeeper.getSmallestTree();
		ruleKeeper.setQueued(false);
		ruleKeeper.next();

		if (!ruleKeeper.isPaused()) {
			queue.add(ruleKeeper,
					ruleKeeper.getSmallestTree().getDeltaWeight());
			ruleKeeper.setQueued(true);
		}

		return nextTree;
	}

	public boolean isEmpty() {

		if (queue.empty()) {
			return true;
		}

		return false;
	}

	public int size() {
		return size;
	}

}