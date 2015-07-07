package se.umu.cs.nfl.aj.wta;

import java.util.ArrayList;
import java.util.HashMap;

public class TransitionFunction {

	// TODO Bad idea or a 'must'?
	private HashMap<Symbol, ArrayList<Rule>> rulesBySymbol = new HashMap<>();
	private HashMap<State, ArrayList<Rule>> rulesByResultingState =
			new HashMap<>();

	public TransitionFunction() {

	}

	public ArrayList<Rule> getRulesBySymbol(Symbol symbol) {
		return rulesBySymbol.get(symbol);
	}

	public ArrayList<Rule> getRulesByResultingState(State resultingState) {
		return rulesByResultingState.get(resultingState);
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
