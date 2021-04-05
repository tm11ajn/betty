/*
 * Copyright 2018 Anna Jonsson for the research group Foundations of Language
 * Processing, Department of Computing Science, Umeå university
 *
 * This file is part of Betty.
 *
 * Betty is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Betty is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.umu.cs.flp.aj.nbest.treedata;

import se.umu.cs.flp.aj.nbest.helpers.TreeConfigurationComparator;
import se.umu.cs.flp.aj.nbest.util.LadderQueue;
import se.umu.cs.flp.aj.nbest.wta.Rule;

public class RuleKeeper implements Comparable<RuleKeeper> {

	private Rule rule;
	private LadderQueue<Tree> ladder;
	private Tree bestTree;

	public RuleKeeper(Rule rule, int limit) {
		this.rule = rule;
		this.ladder = new LadderQueue<>(rule.getID(), 
				rule.getNumberOfStates(), 
				new TreeConfigurationComparator(), limit);
		this.bestTree = null;
	}
	
	public Tree getBestTree() {
		return bestTree;
	}
	
	public void setBestTree(Tree smallestTree) {
		this.bestTree = smallestTree;
	}
	
	public LadderQueue<Tree> getLadderQueue() {
		return ladder;
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
		return getBestTree().compareTo(ruleKeeper.getBestTree());
	}

	@Override
	public String toString() {
		return "Rule: " + rule + " smallest tree: " + bestTree;
	}

}
