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

package se.umu.cs.flp.aj.nbest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import se.umu.cs.flp.aj.knuth.BestContexts;
import se.umu.cs.flp.aj.nbest.helpers.RuleQueue;
import se.umu.cs.flp.aj.nbest.treedata.Context;
import se.umu.cs.flp.aj.nbest.treedata.Node;
import se.umu.cs.flp.aj.nbest.treedata.Tree;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.WTA;


public class BestTrees2 {
	private static WTA wta;
	private static int N;
	private static List<String> nBest;
	private static boolean derivations;
	private static int foundTrees;
	private static HashMap<Node, Tree> outputtedTrees;
	private static HashMap<State, HashMap<Node, Node>> seenTrees;
	private static RuleQueue ruleQueue;

//	public static List<String> run(WTA wta, int N, boolean derivations) {
	public static List<String> run(WTA wta, int N, BestContexts bestContexts, 
			boolean derivations, boolean trick) {
		BestTrees2.wta = wta;
		BestTrees2.N = N;
		BestTrees2.derivations = derivations;
		Tree.init(bestContexts.getBestContextsByState());

		/* For result. */
		nBest = new ArrayList<String>(N);

		// T <- empty. 
		outputtedTrees = new HashMap<>();
		seenTrees = new HashMap<>();

		// i <- 0
		foundTrees = 0;
		
		/* Considers the Knuth 1-best trees for output */
		for (Context context : bestContexts.getOrderedBestTreesList()) {
			State s = context.getBestTree().getResultingState();
			if (s.isFinal() //&& s.isInBestContext()
					) {
				considerForOutput(context.getBestTree(), false);
			}
		}
		
		// K <- empty
		/* Initialises implicitly by enqueuing all rules and initialising them to
		 * the start config, unless they were used in the 1-best tree and are in 
		 * that case initialised to the configurations following the first one. */
		ruleQueue = new RuleQueue(wta, N, bestContexts, trick);

		// while i < N and K nonempty do
		while (foundTrees < N && !ruleQueue.isEmpty()) {
			
			// t <- dequeue(K)
			Tree currentTree = ruleQueue.nextTree();
			
//System.out.println("Current tree: " + currentTree);

			/* If there are no more derivations with a non-infinity weight,
			 * we end the search. */
			if (currentTree.getDeltaWeight().compareTo(
					wta.getSemiring().zero()) >= 0) {
				break;
			}

			considerForOutput(currentTree, true);			
		}
//		ruleQueue.printFinalQueueSizes();
		return nBest;
	}
	
	private static void considerForOutput(Tree currentTree, boolean expand) {
		
		/* Blocks duplicates unless we are solving the N best derivations/runs 
		 * problem */
		if (derivations || !outputtedTrees.containsKey(currentTree.getNode())) {

			if (currentTree.getResultingState().isFinal()) {
				String outputString = "";
				if (wta.isGrammar()) {
					outputString = currentTree.getNode().toRTGString();
				} else {
					outputString = currentTree.getNode().toWTAString();
				}

				outputString += (" # " +
							currentTree.getRunWeight().toString());

//System.out.println("Outputting " + outputString);

				// output(t)
				nBest.add(outputString);
				currentTree.markAsOutputted();
				if (!derivations) {
					outputtedTrees.put(currentTree.getNode(), null);
				}
				foundTrees++;
			}
		}

		HashMap<Node, Node> temp = null;
		if (!derivations) {
			temp = seenTrees.get(currentTree.getResultingState());

			if (temp == null) {
				temp = new HashMap<>();
				seenTrees.put(currentTree.getResultingState(), temp);
			}
		}

		/* Blocks duplicates from being added to the state result lists 
		 * unless we are solving the best derivations/runs */
		if (derivations || !temp.containsKey(currentTree.getNode())) {

			// Expand search space with current tree
			if (expand && foundTrees < N && !currentTree.getResultingState().isSaturated()) {
				ruleQueue.expandWith(currentTree);
			}

			if (!derivations) {
				temp.put(currentTree.getNode(), null);
			}
		}
	}

}
