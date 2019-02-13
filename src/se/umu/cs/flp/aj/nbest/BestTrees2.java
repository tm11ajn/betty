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

package se.umu.cs.flp.aj.nbest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import se.umu.cs.flp.aj.nbest.helpers.RuleQueue;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.Node;
import se.umu.cs.flp.aj.nbest.treedata.TreeKeeper2;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.WTA;


public class BestTrees2 {

	private static HashMap<Node, TreeKeeper2>
			outputtedTrees;
	private static RuleQueue ruleQueue;

	public static void setSmallestCompletions(
			HashMap<State, Weight> smallestCompletions) {
		TreeKeeper2.init(smallestCompletions);
	}


	public static List<String> run(WTA wta, int N) {
//	public static List<TreeKeeper2> run(WTA wta, int N) {

		/* For result. */
		List<String> nBest = new ArrayList<String>();
//		List<TreeKeeper2> nBest = new ArrayList<>();

		// T <- empty. K <- empty
		outputtedTrees = new HashMap<>();
		ruleQueue = new RuleQueue(N, wta.getSourceRules());

//		for (Rule r : wta.getSourceRules()) {
//			ruleQueue.addRule(r);
//		}

		// i <- 0
		int foundTrees = 0;

		// while i < N and K nonempty do
		while (foundTrees < N && !ruleQueue.isEmpty()) {

			// t <- dequeue(K)
			TreeKeeper2 currentTree = ruleQueue.nextTree();

			if (!outputtedTrees.containsKey(currentTree.getTree())) {

				if (//currentTree.getRunWeight().equals(
						//currentTree.getDeltaWeight()) &&
						currentTree.getResultingState().isFinal()) {

					// output(t)
					nBest.add(currentTree.getTree().toString() + " # " +
							currentTree.getDeltaWeight().toString());
//					nBest.add(currentTree);
					outputtedTrees.put(currentTree.getTree(), null);

					foundTrees++;
				}
			}

//			if (ruleQueue.size() < wta.getRuleCount()) {
//				for (Rule r : currentTree.getResultingState().getOutgoing()) {
//					ruleQueue.addRule(r);
//				}
//			}

			// expand
			if (foundTrees < N //&& currentTree.isQueueable()
					) {
				ruleQueue.expandWith(currentTree);
			}
		}

		return nBest;
	}

}
