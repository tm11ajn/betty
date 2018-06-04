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
import se.umu.cs.flp.aj.nbest.wta.TransitionFunction;

public class RuleQueue<LabelType extends Comparable<LabelType>> {

	private TransitionFunction<LabelType> tf;
	private PriorityQueue<RuleKeeper<LabelType>> queue;
	private HashMap<Rule<LabelType>, RuleKeeper<LabelType>> ruleKeepers;

	public RuleQueue(TransitionFunction<LabelType> tf, int limit) {
		this.tf = tf;
		this.queue = new PriorityQueue<>();
		this.ruleKeepers = new HashMap<>();

		ArrayList<Rule<LabelType>> rules = tf.getRules();

		for (Rule<LabelType> r : rules) {
			RuleKeeper<LabelType> keeper = new RuleKeeper<>(r, limit);
			ruleKeepers.put(r, keeper);

			if (!keeper.isPaused()) {
				queue.add(keeper);
				keeper.setQueued(true);
			}
		}
	}

	public void expandWith(TreeKeeper2<LabelType> newTree) {
		State state = newTree.getResultingState();

		for (Rule<LabelType> rule : tf.getRulesByState(state)) {
			RuleKeeper<LabelType> currentKeeper = ruleKeepers.get(rule);

			boolean pausedBefore = currentKeeper.isPaused();

			ArrayList<Integer> stateIndices = rule.getIndexOfState(state);

			for (Integer index : stateIndices) {
				currentKeeper.addTreeForStateIndex(newTree, index);
			}

			if (!currentKeeper.isQueued() &&
					pausedBefore && !currentKeeper.isPaused()) {
				//currentKeeper.next();
				queue.add(currentKeeper);
				currentKeeper.setQueued(true);
			}

		}
	}

	public TreeKeeper2<LabelType> nextTree() {
		RuleKeeper<LabelType> ruleKeeper = queue.poll();
		TreeKeeper2<LabelType> nextTree = ruleKeeper.getSmallestTree();

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

}