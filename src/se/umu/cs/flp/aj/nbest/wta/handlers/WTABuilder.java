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

package se.umu.cs.flp.aj.nbest.wta.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.Symbol;
import se.umu.cs.flp.aj.nbest.wta.WTA;
import se.umu.cs.flp.aj.nbest.wta.exceptions.DuplicateRuleException;
import se.umu.cs.flp.aj.nbest.wta.exceptions.SymbolUsageException;

public class WTABuilder {

	public WTABuilder() {

	}

	/**
	 * Using modification of WTA along with Knuth's generalization of Dijkstra's
	 * algorithm to get the weights of the smallest completions.
	 * @param wta
	 * @return
	 */
	public HashMap<State, Semiring> findSmallestCompletionWeights(WTA wta) {
		HashMap<State, Semiring> smallestCompletionWeights =
				new HashMap<State, Semiring>();

		Collection<State> states = wta.getStates().values();

		/*
		 * Build a modified WTA for each state in the original WTA
		 * and get the weight of the smallest tree that is accepted
		 * by the new WTA, that is, the smallest context with the
		 * current state as the connector.
		 */
		for (State state : states) {

			WTA modifiedWTA = null;

			try {
				modifiedWTA = buildModifiedWTA(wta, state);
			} catch (SymbolUsageException e) {
				System.err.println("Cannot build modified WTA: " + e.getMessage());
				System.exit(-1);
			} catch (DuplicateRuleException e) {
				System.err.println("Cannot build modified WTA: " + e.getMessage());
				System.exit(-1);
			}

			Collection<State> modifiedStates = modifiedWTA.getStates().values();
			ArrayList<State> modifiedFinalStates = modifiedWTA.getFinalStates();
			ArrayList<Rule<Symbol>> modifiedRules = modifiedWTA.
					getTransitionFunction().getRules();

			int nOfModifiedStates = modifiedStates.size();

			HashMap<State, State> defined = new HashMap<>();
			HashMap<State, Semiring> weights = new HashMap<>();

			for (State s : modifiedStates) {
				weights.put(s, (new Weight()).zero());
			}

			while (defined.size() < nOfModifiedStates) {

				for (Rule<Symbol> r : modifiedRules) {
					ArrayList<State> leftHandStates = r.getStates();
					State resultingState = r.getResultingState();
					Semiring newWeight = (new Weight()).one();
					boolean allDefined = true;

					for (State s : leftHandStates) {

						if (!defined.containsKey(s)) {
							allDefined = false;
							break;
						}

						newWeight = newWeight.mult(weights.get(s));
					}

					newWeight = newWeight.mult(r.getWeight());

					if (allDefined) {
						Semiring oldWeight = weights.get(resultingState);

						if (newWeight.compareTo(oldWeight) == 1) {
							newWeight = oldWeight;
						}

						weights.put(resultingState, newWeight);
					}
				}

				Semiring smallestWeight = (new Weight()).zero();
				State smallestState = null;

				for (Entry<State, Semiring> e : weights.entrySet()) {
					Semiring tempWeight = e.getValue();
					State tempState = e.getKey();

					if (!defined.containsKey(tempState) &&
							smallestWeight.compareTo(tempWeight) > -1) {
						smallestWeight = tempWeight;
						smallestState = tempState;
					}
				}

				defined.put(smallestState, smallestState);
				modifiedStates.remove(smallestState);

			}

			Semiring smallestCompletionWeight = (new Weight()).zero();

			for (State s : modifiedFinalStates) {
				Semiring tempWeight = weights.get(s);

				if (tempWeight == null) {
					System.err.println("In getting the smallest completion, "
							+ "the final state " + s + "did not have any weight"
							+ "assigned to it");
					System.exit(-1);
				}

				if (tempWeight.compareTo(smallestCompletionWeight) == -1) {
					smallestCompletionWeight = tempWeight;
				}
			}

			smallestCompletionWeights.put(state, smallestCompletionWeight);

		}

		return smallestCompletionWeights;
	}


	public WTA buildModifiedWTA(WTA wta, State state)
			throws SymbolUsageException, DuplicateRuleException {

		WTA modWTA = new WTA();

		ArrayList<Symbol> symbols = wta.getSymbols();
		Collection<State> states = wta.getStates().values();
		ArrayList<State> finalStates = wta.getFinalStates();
		ArrayList<Rule<Symbol>> rules = wta.getTransitionFunction().getRules();

		for (Symbol s : symbols) {
			modWTA.addSymbol(s.getLabel(), s.getRank());
		}

		Symbol reservedSymbol = modWTA.addSymbol(
				Symbol.RESERVED_SYMBOL_STRING, 0);
		State reservedSymbolState = null;

		for (State s : states) {
			modWTA.addState(s.getLabel());
			State temp = modWTA.addState(s.getLabel().concat(
					State.RESERVED_LABEL_EXTENSION_STRING));

			if (s.equals(state)) {
				reservedSymbolState = temp;
			}
		}

		if (reservedSymbolState == null) {
			throw new SymbolUsageException("The state " + state +
					"is not in the WTA.");
		}

		for (State s : finalStates) {
			modWTA.setFinalState(s.getLabel().concat(
					State.RESERVED_LABEL_EXTENSION_STRING));
		}

		Rule<Symbol> reservedSymbolRule = new Rule<>(reservedSymbol, new Weight(0),
				reservedSymbolState);
		modWTA.getTransitionFunction().addRule(reservedSymbolRule);

		for (Rule<Symbol> r : rules) {
			modWTA.getTransitionFunction().addRule(r);

			ArrayList<State> leftHandStates = r.getStates();
			int nOfLHStates = leftHandStates.size();

			for (int i = 0; i < nOfLHStates; i++) {
				State newResultingState = new State(r.getResultingState().
						getLabel().concat(
								State.RESERVED_LABEL_EXTENSION_STRING));
				Rule<Symbol> newRule = new Rule<>(r.getSymbol(), r.getWeight(),
						newResultingState);

				for (int j = 0; j < nOfLHStates; j++) {

					if (i == j) {
						newRule.addState(new State(leftHandStates.get(i).
								getLabel().
								concat(State.RESERVED_LABEL_EXTENSION_STRING)));
					} else {
						newRule.addState(leftHandStates.get(j));
					}
				}

				modWTA.getTransitionFunction().addRule(newRule);
			}
		}

		return modWTA;
	}

}
