package se.umu.cs.flp.aj.nbest.treedata;

import java.util.HashMap;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.State;

public class Context {
	private Weight weight;
	private HashMap<State, Integer> occurrences;
	
	public Context() {
		occurrences = new HashMap<>();
		this.weight = null;
	}
	
	public Context(Weight weight) {
		this();
		this.weight = weight;
	}
	
	public void setStateOccurrences(State s, int occurrences) {
		this.occurrences.put(s, occurrences);
	}
	
	public int getStateOccurrences(State s) {
		return this.occurrences.get(s);
	}
	
	public Weight getWeight() {
		return this.weight;
	}

}
