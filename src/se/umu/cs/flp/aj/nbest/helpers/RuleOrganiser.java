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
import se.umu.cs.flp.aj.nbest.treedata.TreeKeeper;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.TransitionFunction;

public class RuleOrganiser<LabelType extends Comparable<LabelType>> {

	private TransitionFunction<LabelType> tf;
	private LinkedList<RuleKeeper<LabelType>> queue;
	private HashMap<Rule<LabelType>, RuleKeeper<LabelType>> ruleKeepers;

	public RuleOrganiser(TransitionFunction<LabelType> tf) {
		this.tf = tf;
		this.queue = new LinkedList<>();
		this.ruleKeepers = new HashMap<>();

		ArrayList<Rule<LabelType>> rules = tf.getRules();

		for (Rule<LabelType> r : rules) {
			RuleKeeper<LabelType> keeper = new RuleKeeper<>(r);
			ruleKeepers.put(r, keeper);
			queue.add(keeper);
		}
	}

	public void update(TreeKeeper<LabelType> newTree) {
		for (State state : newTree.getOptimalStates().keySet()) {
			for (Rule<LabelType> rule : tf.getRulesByState(state)) {
				ruleKeepers.get(rule).addLast(rule.getIndexOfState(state),
						newTree);
			}
		}
	}

	public TreeKeeper<LabelType> nextTree() {
		RuleKeeper<LabelType> ruleKeeper = queue.pop();
		TreeKeeper<LabelType> nextTree = ruleKeeper.getSmallestTree();
		ruleKeeper.next();
		queue.add(ruleKeeper);
		return nextTree;
	}

	public boolean isEmpty() {

		if (queue.peek() == null) {
			return true;
		}

		return false;
	}

}
