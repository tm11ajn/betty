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
// TODO: bygg klart
//	@Override
//	public boolean equals(Object obj) {
//		boolean isEqual = true;
//
//		if (obj instanceof Rule) {
//			Rule rule = (Rule) obj;
//
//			if (rule.getSymbol().equals(symbol) &&
//					rule.getResultingState().equals(resultingState)) {
//
//				for (State s : states) {
//
//					if (s.equals(obj)) {
//						return false;
//					}
//				}
//			}
//		}
//
//
//		return false;
//	}
//
//	@Override
//	public int hashCode() {
//		return super.hashCode();
//	}

}
