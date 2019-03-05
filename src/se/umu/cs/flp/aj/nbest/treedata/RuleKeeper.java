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

	public RuleKeeper(Rule rule, int limit,
			ArrayList<LinkedList<TreeKeeper2>> elements,
			ArrayList<Integer> elementIndices) {
		this.rule = rule;
//System.out.println("Creating ladder queue for rule " + rule);
		this.ladder = new LazyLimitedLadderQueue<>(rule.getNumberOfStates(),
				elements, elementIndices, new TreeConfigurationComparator(),
				limit);
		this.smallestTree = null;
		this.paused = true;

		if (rule.getNumberOfStates() == 0) {
			this.paused = false;
			ArrayList<Rule> usedRules = new ArrayList<>();
			usedRules.add(rule);
			this.smallestTree = new TreeKeeper2(rule.getTree(),
					rule.getWeight(), rule.getResultingState(), usedRules); //Added
		}
	}

	public TreeKeeper2 getSmallestTree() {

		if (smallestTree == null) {
			next();
		}

		return smallestTree;
	}

//	public void addTreeForStateIndex(TreeKeeper2 tree, int stateIndex) {
	public void updateForStateIndex(int stateIndex) {
//		ladder.addLast(stateIndex, tree);
		ladder.update(stateIndex);
//System.out.println("Updates ladder with state index " + stateIndex + " for rule " + rule);

		if (ladder.hasNext()) {
			paused = false;
//System.out.println("After updating, ladder is paused");
		} else {
			paused = true;
//System.out.println("After updating, ladder is not paused");
		}
//System.out.println("Is empty ladder? " + ladder.isEmpty());
	}

	public void next() {

//System.out.println("For rule " + rule);
//System.out.println("ladder.hasNext=" + ladder.hasNext());
//System.out.println("Smallest tree: " + smallestTree);
		if (ladder.hasNext()) {
			ArrayList<TreeKeeper2> temp = ladder.dequeue();
//System.out.println("Dequeues " + temp + " from ladder and creates tree ...");
//System.out.println("temp=" + temp);
//			smallestTree = new TreeKeeper2(rule.getSymbol(),
//					rule.getWeight(), rule.getResultingState(), temp);
			smallestTree = rule.apply(temp);
//System.out.println(smallestTree);
//			TreeKeeper2 compTree = rule.apply(temp);
//			if (!compTree.equals(smallestTree)) {
//System.out.println(compTree);
//System.out.println(smallestTree);
//System.exit(1);
//			}
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
//		return getSmallestTree().compareTo(o.getSmallestTree()) == 0;
	}

	@Override
	public int compareTo(RuleKeeper ruleKeeper) {
//		TreeKeeper2 smallest1 = getSmallestTree();
//		TreeKeeper2 smallest2 = ruleKeeper.getSmallestTree();
//		int weightComparison = smallest1.getDeltaWeight().compareTo(smallest2.getDeltaWeight());
//
//		if (weightComparison == 0) {
//			Node n1 = smallest1.getTree();
//			Node n2 = smallest2.getTree();
//			return n1.compareTo(n2);
//		}
//
//		return weightComparison;

//		TreeKeeper2 smallest1 = getSmallestTree();
//		TreeKeeper2 smallest2 = ruleKeeper.getSmallestTree();
//		int weightComparison = smallest1.getDeltaWeight().compareTo(smallest2.getDeltaWeight());
//
//		if (weightComparison == 0) {
//			Node n1 = smallest1.getTree();
//			Node n2 = smallest2.getTree();
//			return n1.compareTo(n2);
//		}
//
//		return weightComparison;

		return getSmallestTree().compareTo(ruleKeeper.getSmallestTree());
	}



	@Override
	public String toString() {
		return "Rule: " + rule + " smallest tree: " + smallestTree;
	}

}
