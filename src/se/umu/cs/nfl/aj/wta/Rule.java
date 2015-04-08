package se.umu.cs.nfl.aj.wta;

import java.util.ArrayList;


public class Rule {

	private String symbol;
	private double weight;

	private ArrayList<State> states = new ArrayList<>();

	private State resultingState;

	public Rule(String symbol, double weight, State resultingState,
			State ... states) {

		this.symbol = symbol;
		this.weight = weight;
		this.resultingState = resultingState;

		for (State state : states) {
			this.states.add(state);
		}

	}

	public Rule(String symbol, State resultingState, State ... states) {

		this.symbol = symbol;
		weight = 0;
		this.resultingState = resultingState;

		for (State state : states) {
			this.states.add(state);
		}

	}

	public String getSymbol() {
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

}
