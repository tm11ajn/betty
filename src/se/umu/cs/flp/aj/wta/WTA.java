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
	
	public ArrayList<State> getStates() {
		return new ArrayList<State>(states.values());
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
	
	public WTA getModifiedWTAWithLeafOnState() {
		return null;
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
