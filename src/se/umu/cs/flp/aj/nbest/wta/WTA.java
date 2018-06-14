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

	private Hypergraph<State, Rule<Symbol>> transitionFunction;
	private State source = new State("DUMMY_SOURCE");

	public WTA(Semiring semiring) {
		this.semiring = semiring;
		this.transitionFunction = new Hypergraph<>();
		this.transitionFunction.addNode(source);
	}

	public State addState(String label) throws SymbolUsageException {

		if (rankedAlphabet.hasSymbol(label)) {
			throw new SymbolUsageException("The symbol " + label +
					" is used for both state and symbol.");
		}

		State newState = states.get(label);

		if (newState == null) {
			newState = new State(label);
			states.put(label, newState);
		}

		return newState;
	}

	public boolean setFinalState(String label) {

		State state = states.get(label);

		if (state == null) {
			state = new State(label);
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

		if (states.containsKey(symbol)) {
			throw new SymbolUsageException("The symbol " + symbol +
					" is used for both state and symbol.");
		}

		return rankedAlphabet.addSymbol(symbol, rank);
	}

	public ArrayList<Symbol> getSymbols() {
		return rankedAlphabet.getSymbols();
	}

	public Semiring getSemiring() {
		return semiring;
	}

	public ArrayList<Rule<Symbol>> getSourceRules() {
		return transitionFunction.getSourceEdges();
	}

	public ArrayList<Rule<Symbol>> getRulesByResultingState(
			State resultingState) {

		ArrayList<Rule<Symbol>> rules = transitionFunction.getIncoming(
				resultingState);

		if (rules == null) {
			return new ArrayList<>();
		}

		return rules;
	}

	public ArrayList<Rule<Symbol>> getRulesByState(State state) {

		ArrayList<Rule<Symbol>> rules = transitionFunction.getOutgoing(state);

		if (rules == null) {
			return new ArrayList<>();
		}

		return rules;
	}

	public ArrayList<Rule<Symbol>> getRules() {
		return transitionFunction.getEdges();
	}

	public void addRule(Rule<Symbol> rule) throws DuplicateRuleException {

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
		ArrayList<Rule<Symbol>> rules = getRules();
		String string = "";

		for (Rule<Symbol> r : rules) {
			string += r + "\n";
		}

		return string;
	}
}
