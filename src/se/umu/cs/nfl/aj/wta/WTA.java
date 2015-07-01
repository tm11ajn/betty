package se.umu.cs.nfl.aj.wta;

import java.util.ArrayList;
import java.util.HashMap;

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

	public boolean addRule(Rule rule) {
		return transitionFunction.addRule(rule);
	}

	public ArrayList<Rule> getRulesBySymbol(Symbol symbol) {
		return transitionFunction.getRulesBySymbol(symbol);
	}

	public ArrayList<Rule> getRulesByResultingState(State resultingState) {
		return transitionFunction.getRulesByResultingState(resultingState);
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
			state.setFinal();
			states.put(label, state);
		} else {
			state.setFinal();
		}

		return finalStates.add(state);
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

}
