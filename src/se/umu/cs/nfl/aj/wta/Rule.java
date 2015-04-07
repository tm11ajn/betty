package se.umu.cs.nfl.aj.wta;

import java.util.ArrayList;


public class Rule {

	private String symbol;
	private double weight;

	private ArrayList<State> states = new ArrayList<>();

	private State resultingState;

	public Rule(String symbol, double weight, String resultingStateLabel,
			String ... stateLabels) {

		this.symbol = symbol;
		this.weight = weight;
		this.resultingState = new State(resultingStateLabel);

		for (String state : stateLabels) {
			this.states.add(new State(state));
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
