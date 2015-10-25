/*
 * Copyright 2015 Anna Jonsson for the research group Foundations of Language 
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

package se.umu.cs.flp.aj.wta;

import java.util.ArrayList;
import java.util.HashMap;

import se.umu.cs.flp.aj.wta.exceptions.DuplicateRuleException;

public class TransitionFunction {

	private HashMap<Symbol, ArrayList<Rule>> rulesBySymbol = new HashMap<>();
	private HashMap<State, ArrayList<Rule>> rulesByResultingState =
			new HashMap<>();

	public TransitionFunction() {

	}

	public ArrayList<Rule> getRulesBySymbol(Symbol symbol) {
		
		ArrayList<Rule> rules = rulesBySymbol.get(symbol);
		
		if (rules == null) {
			return new ArrayList<Rule>();
		}
		
		return rules;
	}

	public ArrayList<Rule> getRulesByResultingState(State resultingState) {
		
		ArrayList<Rule> rules = rulesByResultingState.get(resultingState);
		
		if (rules == null) {
			return new ArrayList<Rule>();
		}
		
		return rules;
	}
	
	public ArrayList<Rule> getRules() {
		ArrayList<Rule> valueList = new ArrayList<>();
		
		for (ArrayList<Rule> ruleList : rulesBySymbol.values()) {
			for (Rule rule : ruleList) {
				valueList.add(rule);
			}
		}
		
		return valueList;
	}

	public boolean addRule(Rule rule) throws DuplicateRuleException {
		Symbol symbol = rule.getSymbol();
		State resState = rule.getResultingState();

		ArrayList<Rule> ruleListSym = rulesBySymbol.get(symbol);
		ArrayList<Rule> ruleListState = rulesByResultingState.get(resState);

		if (ruleListSym == null) {
			ruleListSym = new ArrayList<Rule>();
			rulesBySymbol.put(symbol, ruleListSym);
		}

		if (ruleListState == null) {
			ruleListState = new ArrayList<Rule>();
			rulesByResultingState.put(resState, ruleListState);
		}
		
		if (ruleListSym.contains(rule) || ruleListState.contains(rule)) {
			throw new DuplicateRuleException("The rule " + rule + 
					" is already defined by " + 
					ruleListSym.get(ruleListSym.indexOf(rule)));
		}

		return ruleListSym.add(rule) && ruleListState.add(rule);
	}
	
	@Override
	public String toString() {
		ArrayList<Rule> rules = getRules();
		String string = "";
		
		for (Rule r : rules) {
			string += r + "\n";
		}
		
		return string;
	}

}
