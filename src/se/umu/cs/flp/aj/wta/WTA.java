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

package se.umu.cs.flp.aj.wta;

import java.util.ArrayList;
import java.util.HashMap;

import se.umu.cs.flp.aj.wta.exceptions.SymbolUsageException;

public class WTA {

	/**
	 * Labels mapped to their corresponding states.
	 */
	private HashMap<String, State> states = new HashMap<>();
	private ArrayList<State> finalStates = new ArrayList<>();

	private RankedAlphabet rankedAlphabet = new RankedAlphabet();

	private TransitionFunction transitionFunction = new TransitionFunction();

	public WTA() {

	}

	public TransitionFunction getTransitionFunction() {
		return transitionFunction;
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

//	public ArrayList<State> getStates() {
//		return new ArrayList<State>(states.values());
//	}

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

	@Override
	public String toString() {

		String string = "States: ";

		for (State s : states.values()) {
			string += s + " ";
		}

		string += "\n";
		string += "Ranked alphabet: " + rankedAlphabet + "\n";
		string += "Transition function: \n" + transitionFunction;
		string += "Final states: ";

		for (State s : finalStates) {
			string += s + " ";
		}

		return string;
	}

}
