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

import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.Symbol;
import se.umu.cs.flp.aj.nbest.wta.WTA;
import se.umu.cs.flp.aj.nbest.wta.exceptions.DuplicateRuleException;
import se.umu.cs.flp.aj.nbest.wta.exceptions.SymbolUsageException;

public class WTABuilder {

	public WTABuilder() {

	}

	public WTA buildModifiedWTA(WTA wta, State state)
			throws SymbolUsageException, DuplicateRuleException {

		Semiring semiring = wta.getTransitionFunction().getSemiring();
		WTA modWTA = new WTA(semiring);

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

		Rule<Symbol> reservedSymbolRule = new Rule<>(reservedSymbol,
				semiring.one(), reservedSymbolState);
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
