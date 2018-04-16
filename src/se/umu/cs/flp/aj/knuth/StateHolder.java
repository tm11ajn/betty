package se.umu.cs.flp.aj.knuth;

import se.umu.cs.flp.aj.nbest.semiring.TropicalWeight;
import se.umu.cs.flp.aj.nbest.wta.State;

public class StateHolder implements Comparable<StateHolder> {

	private State state;
	private TropicalWeight weight;

	public StateHolder() {

	}

	public StateHolder(State state, TropicalWeight weight) {
		this.state = state;
		this.weight = weight;
	}

	public void setState(State state) {
		this.state = state;
	}

	public State getState() {
		return state;
	}

	public void setWeight(TropicalWeight weight) {
		this.weight = weight;
	}

	public TropicalWeight getWeight() {
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
