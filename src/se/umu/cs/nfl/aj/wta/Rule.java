package se.umu.cs.nfl.aj.wta;

import java.util.ArrayList;


public class Rule {

	private Symbol symbol;
	private double weight;

	private ArrayList<State> states = new ArrayList<>();

	private State resultingState;

	public Rule(Symbol symbol, double weight, State resultingState,
			State ... states) {

		this.symbol = symbol;
		this.weight = weight;
		this.resultingState = resultingState;

		for (State state : states) {
			this.states.add(state);
		}

	}

	public Rule(Symbol symbol, State resultingState, State ... states) {

		this.symbol = symbol;
		weight = 0;
		this.resultingState = resultingState;

		for (State state : states) {
			this.states.add(state);
		}

	}

	public void addState(State state) {
		states.add(state);
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public double getWeight() {
		return weight;
	}

	public State getResultingState() {
		return resultingState;
	}

	public ArrayList<State> getStates() {
		return states;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Rule) {
			Rule rule = (Rule) obj;

			if (rule.symbol.equals(this.symbol)
					&& rule.resultingState.equals(this.resultingState)
					&& rule.states.size() == this.states.size()) {

				int statesSize = this.states.size();

				for (int i = 0; i < statesSize; i++) {

					if (!rule.states.get(i).equals(this.states.get(i))) {
						return false;
					}
				}

				return true;
			}
		}

		return false;
	}

	// TODO implement!
	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
