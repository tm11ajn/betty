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
import java.util.Map;

import se.umu.cs.flp.aj.nbest.helpers.RuleQueue;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.Node;
import se.umu.cs.flp.aj.nbest.treedata.TreeKeeper2;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.WTA;


public class BestTrees2 {

	private static HashMap<Node, TreeKeeper2>
			outputtedTrees;
	private static HashMap<TreeKeeper2, TreeKeeper2> seenTrees;
	private static RuleQueue ruleQueue;

	public static void setSmallestCompletions(
			HashMap<State, Weight> smallestCompletions) {
		TreeKeeper2.init(smallestCompletions);

//for ( Map.Entry<State, Weight> s: smallestCompletions.entrySet()) {
//System.out.println(s.getKey() + " # " + s.getValue());
//}
	}


	public static List<String> run(WTA wta, int N) {
//	public static List<TreeKeeper2> run(WTA wta, int N) {

		/* For result. */
		List<String> nBest = new ArrayList<String>();
//		List<TreeKeeper2> nBest = new ArrayList<>();

//System.out.println("Before creating rulequeue");
		// T <- empty. K <- empty
		outputtedTrees = new HashMap<>();
		seenTrees = new HashMap<>();
		ruleQueue = new RuleQueue(N, wta.getSourceRules());

//System.out.println("After creating rulequeue");

//		for (Rule r : wta.getSourceRules()) {
//			ruleQueue.addRule(r);
//		}

		// i <- 0
		int foundTrees = 0;

		// while i < N and K nonempty do
		while (foundTrees < N && !ruleQueue.isEmpty()) {

			// t <- dequeue(K)
			TreeKeeper2 currentTree = ruleQueue.nextTree();
//System.out.println("Current tree=" + currentTree);

			if (!outputtedTrees.containsKey(currentTree.getTree())) {

				if (//currentTree.getRunWeight().equals(
						//currentTree.getDeltaWeight()) &&
						currentTree.getResultingState().isFinal()) {

//					String usedRuleString = "";
//					for (Rule r : currentTree.getUsedRules()) {
//						usedRuleString += r + "\n";
//					}

					// output(t)
					nBest.add(currentTree.getTree().toString() + " # " +
//							currentTree.getDeltaWeight().toString());
//							currentTree.getRunWeight().toString());
							currentTree.getOptWeight().toString()); //+ " Used rules: "
//							+ usedRuleString);
//					nBest.add(currentTree);
					outputtedTrees.put(currentTree.getTree(), null);
System.out.println("OUTPUT " + currentTree);

					foundTrees++;
				}

//				// expand
//				if (foundTrees < N //&& currentTree.isQueueable()
//						) {
////System.out.println("Expand with " + currentTree);
//					ruleQueue.expandWith(currentTree);
//				}
			}

			if (!seenTrees.containsKey(currentTree)) {

				// expand
				if (foundTrees < N //&& currentTree.isQueueable()
						) {
//System.out.println("Expand with " + currentTree);
					ruleQueue.expandWith(currentTree);
				}

				seenTrees.put(currentTree, null);
			}
		}

		return nBest;
	}

}
