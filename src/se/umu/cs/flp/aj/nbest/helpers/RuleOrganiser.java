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

import se.umu.cs.flp.aj.nbest.treedata.TreeKeeper;
import se.umu.cs.flp.aj.nbest.util.LadderQueue;
import se.umu.cs.flp.aj.nbest.wta.Rule;

public class RuleOrganiser {

	private ArrayList<Rule> rules;
//	private BinaryHeap<Rule,Semiring> weights;
	private HashMap<Rule,LadderQueue<TreeKeeper<?>>> ladders;
	private LinkedList<TreeKeeper<?>> queue;

	public RuleOrganiser(ArrayList<Rule> rules) {
		this.rules = rules;
//		this.weights = new BinaryHeap<>();
		this.ladders = new HashMap<>();
		initQueues();
	}

	private void initQueues() {

		for (Rule r : rules) {
			ladders.put(r, new LadderQueue<>(r.getRank(),
					new TreeConfigurationComparator()));
		}
	}

	public void update() {

	}

	public TreeKeeper<?> nextTree() {
		return queue.poll();
	}

}
