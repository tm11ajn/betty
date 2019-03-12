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

package se.umu.cs.flp.aj.nbest.treedata;

import java.util.ArrayList;
import java.util.LinkedList;

import se.umu.cs.flp.aj.nbest.helpers.TreeConfigurationComparator;
import se.umu.cs.flp.aj.nbest.util.LazyLimitedLadderQueue;
import se.umu.cs.flp.aj.nbest.wta.Rule;

public class RuleKeeper implements Comparable<RuleKeeper> {

	private Rule rule;
	private LazyLimitedLadderQueue<TreeKeeper2> ladder;
	private TreeKeeper2 smallestTree;
	private boolean paused;
	private boolean queued;
	private boolean needsUpdate;

	public RuleKeeper(Rule rule, int limit,
			ArrayList<LinkedList<TreeKeeper2>> elements,
			ArrayList<Integer> elementIndices) {
		this.rule = rule;
		this.ladder = new LazyLimitedLadderQueue<>(rule.getNumberOfStates(),
				elements, elementIndices, new TreeConfigurationComparator(),
				limit);
		this.smallestTree = null;
		this.paused = true;
		this.needsUpdate = false;

		if (rule.getNumberOfStates() == 0) {
			this.paused = false;
			ArrayList<Rule> usedRules = new ArrayList<>();
			usedRules.add(rule);
			this.smallestTree = new TreeKeeper2(rule.getTree(),
					rule.getWeight(), rule.getResultingState(), usedRules);
		}
	}

	public TreeKeeper2 getSmallestTree() {

		if (smallestTree == null) {
			next();
		}

		return smallestTree;
	}

	public void updateForStateIndex(int stateIndex) {
		ladder.update(stateIndex);

		if (ladder.needsUpdate()) {
			this.needsUpdate = true;
			this.smallestTree = rule.apply(ladder.peek());
		}

		if (ladder.hasNext()) {
			paused = false;
		} else {
			paused = true;
		}
	}

	public void hasUpdated() {
		this.needsUpdate = false;
		ladder.hasUpdated();
	}

	public boolean needsUpdate() {
		return needsUpdate;
	}

	public void hasBeenDequeued() {
		ladder.dequeue();
	}

	public void next() {
		if (ladder.hasNext()) {
			ArrayList<TreeKeeper2> temp = ladder.peek();
			smallestTree = rule.apply(temp);
			paused = false;
		} else {
			paused = true;
		}
	}

	public boolean isPaused() {
		return paused;
	}

	public void setQueued(boolean queued) {
		this.queued = queued;
	}

	public boolean isQueued() {
		return queued;
	}

	public Rule getRule() {
		return rule;
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof RuleKeeper)) {
			return false;
		}

		RuleKeeper o = (RuleKeeper) obj;
		return this.compareTo(o) == 0;
	}

	@Override
	public int compareTo(RuleKeeper ruleKeeper) {
		return getSmallestTree().compareTo(ruleKeeper.getSmallestTree());
	}

	@Override
	public String toString() {
		return "Rule: " + rule + " smallest tree: " + smallestTree;
	}

}
