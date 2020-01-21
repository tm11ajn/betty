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
	private static HashMap<State, HashMap<Node, Node>> seenTrees;
	private static RuleQueue ruleQueue;

//	public static void setSmallestCompletions(
//			HashMap<State, Weight> smallestCompletions) {
//	public static void setSmallestCompletions(Weight[] smallestCompletions) {
//		TreeKeeper2.init(smallestCompletions);
//	}


	public static List<String> run(WTA wta, int N, 
			Weight[] smallestCompletions) {
		TreeKeeper2.init(smallestCompletions);

		/* For result. */
		List<String> nBest = new ArrayList<String>();

		// T <- empty. K <- empty
		outputtedTrees = new HashMap<>();
		seenTrees = new HashMap<>();
//		ruleQueue = new RuleQueue(N, wta.getSourceRules(), wta.getRuleCount());
		ruleQueue = new RuleQueue(N, wta);

		// i <- 0
		int foundTrees = 0;

//System.out.println("Main: START OF MAIN LOOP");
		// while i < N and K nonempty do
		while (foundTrees < N && !ruleQueue.isEmpty()) {
			
			
			// t <- dequeue(K)
			TreeKeeper2 currentTree = ruleQueue.nextTree();
//System.out.println("Main: Current tree: " + currentTree);

			// If there are no more derivations with a non-infinity weight,
			// we end the search.
			if (currentTree.getDeltaWeight().compareTo(
					wta.getSemiring().zero()) >= 0) {
				break;
			}

			if (!outputtedTrees.containsKey(currentTree.getTree())) {

				if (currentTree.getResultingState().isFinal()) {
//System.out.println("Main: resState final");
					String outputString = "";

					if (wta.isGrammar()) {
						outputString = currentTree.getTree().toRTGString();
					} else {
						outputString = currentTree.getTree().toWTAString();
					}

//					if (forDerivations) {
//						outputString = outputString.replaceAll("//rule[0-9]*", "");
//					}

					outputString += (" # " +
								currentTree.getRunWeight().toString());

					// output(t)
					nBest.add(outputString);
//					nBest.add(currentTree);

//System.out.println("OUTPUT " + currentTree);
					outputtedTrees.put(currentTree.getTree(), null);
					foundTrees++;
				}
			}

			 HashMap<Node, Node> temp = seenTrees.get(currentTree.getResultingState());

			 if (temp == null) {
				 temp = new HashMap<>();
				 seenTrees.put(currentTree.getResultingState(), temp);
			 }


			if (!temp.containsKey(currentTree.getTree())) {

				// expand
				if (foundTrees < N) {
System.out.println("Main: expand with: " + currentTree);
System.out.println("Found trees: " + foundTrees);
System.out.println("ruleQueue size before: " + ruleQueue.size());
					ruleQueue.expandWith(currentTree);
System.out.println("ruleQueue size after: " + ruleQueue.size()); 
// TODO: same as before for Main: expand with: Tree: \$.[.] RunWeight: .065691 Delta weight: 78.086873 Resulting state: .[21,21]_._\$., debug
				}

				temp.put(currentTree.getTree(), null);
			}
		}

		return nBest;
	}

}
