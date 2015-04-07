package se.umu.cs.nfl.aj.wta;

import java.util.ArrayList;
import java.util.HashMap;

public class WTA {

	private ArrayList<Rule> rules = new ArrayList<>();
	private ArrayList<String> symbols = new ArrayList<>(); // or hashmap if we need hasSymbol

	private HashMap<String, State> states = new HashMap<>();

	public WTA() {

	}

	public boolean addRule(Rule rule) {
		return rules.add(rule);
	}

	public State addState(String label) {
		return states.put(label, new State(label));
	}

	public State getState(String label) {
		return states.get(label);
	}


	public boolean addSymbol(String symbol) {
		return symbols.add(symbol);
	}

}
