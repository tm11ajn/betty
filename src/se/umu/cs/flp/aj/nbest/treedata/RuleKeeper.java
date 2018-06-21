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

import se.umu.cs.flp.aj.nbest.helpers.TreeConfigurationComparator;
import se.umu.cs.flp.aj.nbest.util.LazyLimitedLadderQueue;
import se.umu.cs.flp.aj.nbest.wta.Rule;

public class RuleKeeper<LabelType extends Comparable<LabelType>> implements
			Comparable<RuleKeeper<LabelType>> {

	private Rule<LabelType> rule;
	private LazyLimitedLadderQueue<TreeKeeper2<LabelType>> ladder;
	private TreeKeeper2<LabelType> smallestTree;
	private boolean paused;
	private boolean queued;

	public RuleKeeper(Rule<LabelType> rule, int limit) {
		this.rule = rule;
		this.ladder = new LazyLimitedLadderQueue<>(rule.getRank(),
				new TreeConfigurationComparator<>(), limit);
		this.smallestTree = null;
		this.paused = true;

		if (rule.getRank() == 0) {
			this.paused = false;
		}
	}

	public TreeKeeper2<LabelType> getSmallestTree() {

		if (smallestTree == null) {
			next();
		}

		return smallestTree;
	}

	public void addTreeForStateIndex(TreeKeeper2<LabelType> tree,
			int stateIndex) {
		ladder.addLast(stateIndex, tree);

		if (ladder.hasNext()) {
			paused = false;
		} else {
			paused = true;
		}
	}

	public void next() {

		if (ladder.hasNext()) {
			ArrayList<TreeKeeper2<LabelType>> temp = ladder.dequeue();
if (temp == null) {
System.out.println("Rule=" + rule);
System.out.println("Rank=" + rule.getRank());
System.out.println("Weight=" + rule.getWeight());
System.out.println("temp=" + temp);
}
			smallestTree = new TreeKeeper2<LabelType>(rule.getSymbol(),
					rule.getWeight(), rule.getResultingState(), temp);
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

	public Rule<LabelType> getRule() {
		return rule;
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof RuleKeeper<?>)) {
			return false;
		}

		RuleKeeper<?> o = (RuleKeeper<?>) obj;
		return getSmallestTree().equals(o.getSmallestTree());
	}

	@Override
	public int compareTo(RuleKeeper<LabelType> ruleKeeper) {
		return getSmallestTree().compareTo(ruleKeeper.getSmallestTree());
	}

	@Override
	public String toString() {
		return "Rule: " + rule + " smallest tree: " + smallestTree;
	}

}
