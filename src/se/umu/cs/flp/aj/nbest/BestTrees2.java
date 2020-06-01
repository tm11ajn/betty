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
import java.util.Map.Entry;

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

	public static List<String> run(WTA wta, int N, 
			Weight[] smallestCompletions) {
		TreeKeeper2.init(smallestCompletions);

		/* For result. */
		List<String> nBest = new ArrayList<String>();

		// T <- empty. 
		outputtedTrees = new HashMap<>();
		seenTrees = new HashMap<>();
		
		// K <- empty
		/* Initialises implicitly by enqueuing rules without states. */
		ruleQueue = new RuleQueue(N, wta);

		// i <- 0
		int foundTrees = 0;

		// while i < N and K nonempty do
		while (foundTrees < N && !ruleQueue.isEmpty()) {
			
			// t <- dequeue(K)
			TreeKeeper2 currentTree = ruleQueue.nextTree();

			/* If there are no more derivations with a non-infinity weight,
			 * we end the search. */
			if (currentTree.getDeltaWeight().compareTo(
					wta.getSemiring().zero()) >= 0) {
				break;
			}

			if (!outputtedTrees.containsKey(currentTree.getTree())) {

				if (currentTree.getResultingState().isFinal()) {
					String outputString = "";

					if (wta.isGrammar()) {
						outputString = currentTree.getTree().toRTGString();
					} else {
						outputString = currentTree.getTree().toWTAString();
					}

					outputString += (" # " +
								currentTree.getRunWeight().toString());

					// output(t)
					nBest.add(outputString);
					outputtedTrees.put(currentTree.getTree(), null);
					foundTrees++;
					
					
					for (Entry<State, Integer> entry : currentTree.getStateUsage().entrySet()) {
						int stateUsageCount = entry.getValue();
						State state = entry.getKey();
						int resSizeForState = ruleQueue.outputForStateSize(state);
						int coveredTrees = (int) Math.pow(resSizeForState, stateUsageCount);
						
						if (coveredTrees > N) {
							state.markAsSaturated(); 
						}
					}
				}
			}

			 HashMap<Node, Node> temp = seenTrees.get(currentTree.getResultingState());

			 if (temp == null) {
				 temp = new HashMap<>();
				 seenTrees.put(currentTree.getResultingState(), temp);
			 }

			if (!temp.containsKey(currentTree.getTree())) {

				// Expand search space with current tree
				if (foundTrees < N //&& !currentTree.getResultingState().isSaturated()
						) {
					ruleQueue.expandWith(currentTree);
				}

				temp.put(currentTree.getTree(), null);
			}
		}

		return nBest;
	}

}
