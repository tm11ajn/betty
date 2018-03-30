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

package se.umu.cs.flp.aj.nbest.wta;

import java.util.ArrayList;
import java.util.HashMap;

import se.umu.cs.flp.aj.nbest.wta.exceptions.DuplicateRuleException;

public class TransitionFunction<LabelType> {

	private HashMap<LabelType, ArrayList<Rule<LabelType>>> rulesBySymbol = new HashMap<>();
	private HashMap<State, ArrayList<Rule<LabelType>>> rulesByResultingState =
			new HashMap<>();
	private HashMap<State, ArrayList<Rule<LabelType>>> rulesByState = new HashMap<>();

	public TransitionFunction() {

	}

	public ArrayList<Rule<LabelType>> getRulesBySymbol(Symbol symbol) {

		ArrayList<Rule<LabelType>> rules = rulesBySymbol.get(symbol);

		if (rules == null) {
			return new ArrayList<>();
		}

		return rules;
	}

	public ArrayList<Rule<LabelType>> getRulesByResultingState(State resultingState) {

		ArrayList<Rule<LabelType>> rules = rulesByResultingState.get(resultingState);

		if (rules == null) {
			return new ArrayList<>();
		}

		return rules;
	}

	public ArrayList<Rule<LabelType>> getRulesByState(State state) {

		ArrayList<Rule<LabelType>> rules = rulesByState.get(state);

		if (rules == null) {
			return new ArrayList<>();
		}

		return rules;
	}

	public ArrayList<Rule<LabelType>> getRules() {
		ArrayList<Rule<LabelType>> valueList = new ArrayList<>();

		for (ArrayList<Rule<LabelType>> ruleList : rulesBySymbol.values()) {
			for (Rule<LabelType> rule : ruleList) {
				valueList.add(rule);
			}
		}

		return valueList;
	}

	public boolean addRule(Rule<LabelType> rule) throws DuplicateRuleException {
		LabelType symbol = rule.getSymbol();
		State resState = rule.getResultingState();

		ArrayList<Rule<LabelType>> ruleListSym = rulesBySymbol.get(symbol);
		ArrayList<Rule<LabelType>> ruleResListState = rulesByResultingState.get(resState);

		if (ruleListSym == null) {
			ruleListSym = new ArrayList<Rule<LabelType>>();
			rulesBySymbol.put(symbol, ruleListSym);
		}

		if (ruleResListState == null) {
			ruleResListState = new ArrayList<Rule<LabelType>>();
			rulesByResultingState.put(resState, ruleResListState);
		}

		if (ruleListSym.contains(rule) || ruleResListState.contains(rule)) {
			throw new DuplicateRuleException("The rule " + rule +
					" is already defined by " +
					ruleListSym.get(ruleListSym.indexOf(rule)));
		}

		boolean acc = true;

		for (State state : rule.getStates()) {
			ArrayList<Rule<LabelType>> ruleListState = rulesByState.get(state);

			if (ruleListState == null) {
				ruleListState = new ArrayList<>();
				rulesByState.put(state, ruleListState);
			}

			acc = ruleListState.add(rule) && acc;
		}

		return ruleListSym.add(rule) && ruleResListState.add(rule) && acc;
	}

	@Override
	public String toString() {
		ArrayList<Rule<LabelType>> rules = getRules();
		String string = "";

		for (Rule<LabelType> r : rules) {
			string += r + "\n";
		}

		return string;
	}

}
