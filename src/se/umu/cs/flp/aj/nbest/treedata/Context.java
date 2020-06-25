package se.umu.cs.flp.aj.nbest.treedata;

import java.util.HashMap;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.State;

public class Context implements Comparable<Context> {
	private Weight weight;
	private HashMap<State, Integer> occurrences;
//	private HashMap<State, Integer> depth;
	private int depth;
	
	public Context() {
		occurrences = new HashMap<>();
//		depth = new HashMap<>();
		depth = 0;
		this.weight = null;
	}
	
	public Context(Weight weight) {
		this();
		this.weight = weight;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public void setDepth(int depth) {
		this.depth = depth;
	}
	
//	public int getDepthOfState(State s) {
//		return depth;
//	}
	
//	public HashMap<State, Integer> getDepthOfStates() {
//		return depth;
//	}
	
//	public void addToDepthOfState(State s, int increase) {
//		if (this.depth.get(s) == null) {
//			this.depth.put(s, 0);
//		}
//		this.depth.put(s, this.depth.get(s) + 1);
//	}
	
	public void setStateOccurrence(State s, int occurrences) {
		this.occurrences.put(s, occurrences);
	}
	
	public void addStateOccurrence(State s, int occurrences) {
		if (this.occurrences.get(s) == null) {
			this.occurrences.put(s, 0);
		}
		this.occurrences.put(s, this.occurrences.get(s) + occurrences);
	}
	
	public int getStateOccurrence(State s) {
		if (this.occurrences.get(s) == null) {
			return 0;
		}
		return this.occurrences.get(s);
	}
	
	public HashMap<State, Integer> getStateOccurrences() {
		return this.occurrences;
	}
	
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
