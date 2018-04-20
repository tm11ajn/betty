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

import se.umu.cs.flp.aj.nbest.helpers.RuleOrganiser;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.Node;
import se.umu.cs.flp.aj.nbest.treedata.TreeKeeper;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.Symbol;
import se.umu.cs.flp.aj.nbest.wta.WTA;


public class BestTrees2 {

	private static ArrayList<TreeKeeper<Symbol>> exploredTrees; // T
//	private static PruneableQueue<TreeKeeper<Symbol>,Weight> treeQueue; // K

//	private static PriorityQueue<Rule> ruleQueue;

	private static RuleOrganiser<Symbol> ruleOrganiser;

//	private static LinkedList<TreeKeeper<Symbol>> queue;


	public static void setSmallestCompletions(
			HashMap<State, Weight> smallestCompletions) {
		TreeKeeper.init(smallestCompletions);
	}

	public static List<String> run(WTA wta, int N) {

		/* For result. */
		List<String> nBest = new ArrayList<String>();

		// T <- empty. K <- empty
		exploredTrees = new ArrayList<TreeKeeper<Symbol>>();
		ruleOrganiser = new RuleOrganiser<>(wta.getTransitionFunction());

		// enqueue(K, Sigma_0)
		enqueueRankZeroSymbols(wta, N);

		// i <- 0
		int counter = 0;

		// while i < N and K nonempty do
		while (counter < N && !ruleOrganiser.isEmpty()) {

			// t <- dequeue(K)
			TreeKeeper<Symbol> currentTree = ruleOrganiser.nextTree();

System.out.println("Current tree = " + currentTree);

			// T <- T u {t}
			exploredTrees.add(currentTree);

System.out.println("Smallest weight = " + currentTree.getSmallestWeight());
System.out.println("Delta weight = " + currentTree.getDeltaWeight());

// TODO: make sure that optimal states and optimal weights are updated properly.

			// if M(t) = delta(t) then
			if (currentTree.getSmallestWeight().equals(
					currentTree.getDeltaWeight())) {

				// output(t)
				nBest.add(currentTree.getTree().toString() + " " +
						currentTree.getDeltaWeight().toString());

				// i <- i + 1
				counter++;
			}

			// prune(T, enqueue(K, expand(T, t)))
			if (counter < N) {
				ruleOrganiser.update(currentTree);
			}
		}

		return nBest;
	}

	private static void enqueueRankZeroSymbols(WTA wta, int N) {

		ArrayList<Symbol> symbols = wta.getSymbols();

		for (Symbol s : symbols) {

			if (s.getRank() == 0) {

System.out.println("Current symbol: " + s);
				Node<Symbol> node = new Node<Symbol>(s);
				TreeKeeper<Symbol> tree = new TreeKeeper<>(node,
						wta.getTransitionFunction().getSemiring());

				ArrayList<Rule<Symbol>> rules = wta.getTransitionFunction().
						getRulesBySymbol(s);

				for (Rule<Symbol> r : rules) {
					tree.addStateWeight(r.getResultingState(), r.getWeight());
				}

System.out.println("Updating rule organiser with " + tree);

				ruleOrganiser.update(tree);
			}
		}
	}

}
