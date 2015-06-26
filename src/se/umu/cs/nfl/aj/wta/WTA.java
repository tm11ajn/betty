package se.umu.cs.nfl.aj.wta;

import java.util.ArrayList;
import java.util.HashMap;

public class WTA {

	// TODO Bad idea or a 'must'?
	private HashMap<Symbol, ArrayList<Rule>> rulesBySymbol = new HashMap<>();
	private HashMap<State, ArrayList<Rule>> rulesByResultingState =
			new HashMap<>();

//	private ArrayList<Rule> rules = new ArrayList<>();
//	private ArrayList<String> symbols = new ArrayList<>(); // or hashmap if we need hasSymbol

	private HashMap<String, Symbol> symbols = new HashMap<>();
	private RankedAlphabet ranked = new RankedAlphabet();

	/**
	 * Labels mapped to their corresponding states.
	 */
	private HashMap<String, State> states = new HashMap<>();

	public WTA() {

	}

	public boolean addRule(Rule rule) {

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

		return ruleListSym.add(rule) && ruleListState.add(rule);
	}

	public ArrayList<Rule> getRulesBySymbol(Symbol symbol) {
		return rulesBySymbol.get(symbol);
	}

	public ArrayList<Rule> getRulesByResultingState(State resultingState) {
		return rulesByResultingState.get(resultingState);
	}

//	public State addState(State state) {
//		return states.put(state.getLabel(), state);
//	}

	public State getState(String label) {

		State state = states.get(label);

		if (state == null) {
			state = new State(label);
			states.put(label, state);
		}

		return state;
	}

	public void setFinalState(String label) { // TODO input State or String?

		State state;

		if ((state = states.get(label)) == null) {
			state = new State(label);
			state.setFinal();
			states.put(label, state);
		} else {
			state.setFinal();
		}
	}

	public Symbol getSymbol(String symbol, int rank) {

		Symbol s = symbols.get(symbol);

		if (s == null) {
			s = new Symbol(symbol, rank);
			ranked.addSymbol(s);
			symbols.put(symbol, s);
		} else if (s.getRank() != rank) {
			System.err.println("Rank error: The symbol " + symbol +
					" cannot be of two different ranks ");
			System.exit(-1);
			// TODO throw exception instead
		}

		return s;
	}

//	public String addSymbol(String symbol) {
//		return symbols.put(symbol, symbol);
//	}
//
//	public boolean hasSymbol(String symbol) {
//		return symbols.containsKey(symbol);
//	}

}
