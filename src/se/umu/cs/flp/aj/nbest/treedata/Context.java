package se.umu.cs.flp.aj.nbest.treedata;

import java.util.HashMap;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.State;

public class Context implements Comparable<Context> {
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
	
//	public void setStateOccurrence(State s, int occurrences) {
//		this.occurrences.put(s, occurrences);
//	}
//	
//	public void addStateOccurrence(State s, int occurrences) {
//		if (this.occurrences.get(s) == null) {
//			this.occurrences.put(s, 0);
//		}
//		this.occurrences.put(s, this.occurrences.get(s) + 1);
//	}
//	
//	public int getStateOccurrence(State s) {
//		if (this.occurrences.get(s) == null) {
//			return 0;
//		}
//		return this.occurrences.get(s);
//	}
//	
//	public HashMap<State, Integer> getStateOccurrences() {
//		return this.occurrences;
//	}
	
	public Weight getWeight() {
		return this.weight;
	}
	
	public void setWeight(Weight weight) {
		this.weight = weight;
	}

	@Override
	public int compareTo(Context o) {
		return this.weight.compareTo(o.weight);
	}

}
