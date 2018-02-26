package se.umu.cs.flp.aj.knuth;

import se.umu.cs.flp.aj.wta.State;
import se.umu.cs.flp.aj.wta.Weight;

public class StateHolder implements Comparable<StateHolder> {

	private State state;
	private Weight weight;

	public StateHolder() {

	}

	public StateHolder(State state, Weight weight) {
		this.state = state;
		this.weight = weight;
	}

	public void setState(State state) {
		this.state = state;
	}

	public State getState() {
		return state;
	}

	public void setWeight(Weight weight) {
		this.weight = weight;
	}

	public Weight getWeight() {
		return weight;
	}

	@Override
	public int compareTo(StateHolder sh) {
		return this.weight.compareTo(sh.weight);
	}

	@Override
	public String toString() {
		return "" + state + ": " + weight;
	}

	@Override
	public int hashCode() {
		return state.hashCode();
	}
}
