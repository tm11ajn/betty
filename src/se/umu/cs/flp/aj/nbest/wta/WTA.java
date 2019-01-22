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

import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.util.Hypergraph;
import se.umu.cs.flp.aj.nbest.wta.exceptions.DuplicateRuleException;
import se.umu.cs.flp.aj.nbest.wta.exceptions.SymbolUsageException;

public class WTA {

	/**
	 * Labels mapped to their corresponding states.
	 */
	private HashMap<String, State> states = new HashMap<>();
	private ArrayList<State> finalStates = new ArrayList<>();
	private RankedAlphabet rankedAlphabet = new RankedAlphabet();
	private Semiring semiring;

	private Hypergraph<State, Rule> transitionFunction;
	private State source = new State(new Symbol("DUMMY_SOURCE", 0));

	public WTA(Semiring semiring) {
		this.semiring = semiring;
		this.transitionFunction = new Hypergraph<>();
		this.transitionFunction.addNode(source);
	}

	public State addState(String label) throws SymbolUsageException {

//		if (rankedAlphabet.hasSymbol(label)) {
//			throw new SymbolUsageException("The symbol " + label +
//					" is used for both state and symbol.");
//		}

		State newState = states.get(label);

		if (newState == null) {
			Symbol symbol = rankedAlphabet.addSymbol(label, 0);
			symbol.setNonterminal(true);
			newState = new State(symbol);
			states.put(label, newState);
		}

		return newState;
	}

	public boolean setFinalState(String label) throws SymbolUsageException {

		State state = states.get(label);

		if (state == null) {
			Symbol symbol = rankedAlphabet.addSymbol(label, 0);
			symbol.setNonterminal(true);
			state = new State(symbol);
			states.put(label, state);
		}

		state.setFinal();

		return finalStates.add(state);
	}

	public HashMap<String, State> getStates() {
		return states;
	}

	public ArrayList<State> getFinalStates() {
		return finalStates;
	}

	public Symbol addSymbol(String symbol, int rank)
			throws SymbolUsageException {

//		if (states.containsKey(symbol)) {
//			throw new SymbolUsageException("The symbol " + symbol +
//					" is used for both state and symbol.");
//		}

		return rankedAlphabet.addSymbol(symbol, rank);
	}

	public ArrayList<Symbol> getSymbols() {
		return rankedAlphabet.getSymbols();
	}

	public Semiring getSemiring() {
		return semiring;
	}

	public ArrayList<State> getSourceNodes() {
		return transitionFunction.getSourceNodes();
	}

	public ArrayList<Rule> getSourceRules() {
		return transitionFunction.getSourceEdges();
	}

	public ArrayList<Rule> getRulesByResultingState(
			State resultingState) {

		ArrayList<Rule> rules = transitionFunction.getIncoming(
				resultingState);

		if (rules == null) {
			return new ArrayList<>();
		}

		return rules;
	}

	public ArrayList<Rule> getRulesByState(State state) {

		ArrayList<Rule> rules = transitionFunction.getOutgoing(state);

		if (rules == null) {
			return new ArrayList<>();
		}

		return rules;
	}

	public ArrayList<Rule> getRules() {
		return transitionFunction.getEdges();
	}

	public void addRule(Rule rule) throws DuplicateRuleException {

		ArrayList<State> states = rule.getStates();

		if (states.isEmpty()) {
			states = new ArrayList<>();
			states.add(source);
		}

		transitionFunction.addEdge(rule, rule.getWeight(),
				rule.getResultingState(), states);
	}

	@Override
	public String toString() {
		String string = "States: ";

		for (State s : states.values()) {
			string += s + " ";
		}

		string += "\n";
		string += "Ranked alphabet: " + rankedAlphabet + "\n";
		string += "Transition function: \n" + printTransitionFunction();
		string += "Final states: ";

		for (State s : finalStates) {
			string += s + " ";
		}

		return string;
	}

	public String printTransitionFunction() {
		ArrayList<Rule> rules = getRules();
		String string = "";

		for (Rule r : rules) {
			string += r + "\n";
		}

		return string;
	}
}
