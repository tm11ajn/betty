package se.umu.cs.nfl.aj.wta;

import java.util.ArrayList;
import java.util.HashMap;

public class WTA {

	// TODO Bad idea or a 'must'?
	private HashMap<String, ArrayList<Rule>> rulesBySymbol = new HashMap<>();
	private HashMap<State, ArrayList<Rule>> rulesByResultingState =
			new HashMap<>();

//	private ArrayList<Rule> rules = new ArrayList<>();

//	private ArrayList<String> symbols = new ArrayList<>(); // or hashmap if we need hasSymbol

	private HashMap<String, String> symbols = new HashMap<>();

	/**
	 * Labels mapped to their corresponding states.
	 */
	private HashMap<String, State> states = new HashMap<>();

	public WTA() {

	}

	public boolean addRule(Rule rule) {

		ArrayList<Rule> ruleListSym = rulesBySymbol.get(rule.getSymbol());
		ArrayList<Rule> ruleListState = rulesByResultingState.get(
				rule.getResultingState());

		if (ruleListSym == null) {
			ruleListSym = new ArrayList<Rule>();
			rulesBySymbol.put(rule.getSymbol(), ruleListSym);
		}

		if (ruleListState == null) {
			ruleListState = new ArrayList<Rule>();
			rulesByResultingState.put(rule.getResultingState(), ruleListState);
		}

		return ruleListSym.add(rule) && ruleListState.add(rule);
	}

	public ArrayList<Rule> getRulesBySymbol(String symbol) {
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

	public String getSymbol(String symbol) {

		String s = symbols.get(symbol);

		if (s == null) {
			symbols.put(symbol, symbol);
		}

		return symbol;
	}

//	public String addSymbol(String symbol) {
//		return symbols.put(symbol, symbol);
//	}
//
//	public boolean hasSymbol(String symbol) {
//		return symbols.containsKey(symbol);
//	}

}
