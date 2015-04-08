package se.umu.cs.nfl.aj.wta;

import java.util.ArrayList;
import java.util.HashMap;

public class WTA {

	// TODO Bad idea or a 'must'?
	private HashMap<String, ArrayList<Rule>> rulesBySymbol = new HashMap<>();
	private HashMap<State, ArrayList<Rule>> rulesByResultingState =
			new HashMap<>();

	private ArrayList<Rule> rules = new ArrayList<>();

	private ArrayList<String> symbols = new ArrayList<>(); // or hashmap if we need hasSymbol

	/**
	 * Labels mapped to their corresponding states.
	 */
	private HashMap<String, State> states = new HashMap<>();

	public WTA() {

	}

	public boolean addRule(Rule rule) {
		return rules.add(rule);
	}

	public Rule[] getRulesBySymbol() {
		return null;
	}

	public Rule[] getRulesByResultingState() {
		return null;
	}

	public State addState(String label) {
		return states.put(label, new State(label));
	}

	public State getState(String label) {
		return states.get(label);
	}

	public void setFinalState(String label) {

		State state;

		if ((state = states.get(label)) == null) {
			state = new State(label);
			state.setFinal();
			states.put(label, state);
		} else {
			state.setFinal();
		}
	}

	public boolean addSymbol(String symbol) {
		return symbols.add(symbol);
	}

}
