package se.umu.cs.flp.aj.nbest.data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import se.umu.cs.flp.aj.wta.State;
import se.umu.cs.flp.aj.wta.Weight;

public class TreeKeeper<LabelType extends Comparable<LabelType>> 
		implements Comparable<TreeKeeper<?>> {
	
	// Keep and update optimal states here instead of computing it outside the class?
	// Can use TreeMap if we use a class containing a state and a weight 
	
	private static HashMap<State, Weight> smallestCompletions;
	
	private Node<LabelType> tree;
//	private ArrayList<State> optimalStates;
	
	private LinkedHashMap<State,State> optimalStates;
	private State optimalState;
	
	private HashMap<State, Weight> optWeights;
//	private TreeMap<State, Weight> bestWeights; Continue. 
	private Weight smallestWeight;
	
	public TreeKeeper(Node<LabelType> tree) {
		this.tree = tree;
		this.optimalStates = null;
		this.optimalState = null;
		this.optWeights = new HashMap<>();
//		this.bestWeights = new TreeMap<>();
		this.smallestWeight = new Weight(Weight.INF); // or just set from outside?
	}
	
	public static void init(HashMap<State, Weight> smallestCompletionWeights) {
		smallestCompletions = smallestCompletionWeights;
	}
	
	public Node<LabelType> getTree() {
		return tree;
	}
	
	public LinkedHashMap<State, State> getOptimalStates() {
		return optimalStates;
	}
	
//	public boolean addState(State s) {
//		return optimalStates.add(s);
//	}
	
//	public void setOptimalStates(LinkedHashMap<State, State> optimalStates) {
//		this.optimalStates = optimalStates;
//	}
	
//	public HashMap<State, Weight> getOptWeights() {
//		return optWeights;
//	}
	
	public Weight getWeight(State s) {
		return optWeights.get(s);
	}
	
	public void addWeight(State s, Weight w) {
		
		if (this.optWeights.containsKey(s)) {				
			if (this.optWeights.get(s).compareTo(w) == 1) {
				this.optWeights.put(s, w);
			}
		} else {
			this.optWeights.put(s, w);
		}
		
		if (w.compareTo(smallestWeight) == -1) {
			optimalStates = new LinkedHashMap<>();
			optimalStates.put(s, s);
			optimalState = s;
			smallestWeight = w;
		} else if (w.compareTo(smallestWeight) == 0) {
			optimalStates.put(s, s);
		}
	}
	
	public void addWeightsFrom(TreeKeeper<LabelType> t) { // USE INSTEAD OF MERGE
		HashMap<State, Weight> map = t.optWeights;
				
		for (Entry<State, Weight> e : map.entrySet()) {
			addWeight(e.getKey(), e.getValue());
		}
	}
	
//	public void getBestWeight() {
//		return bestWeights.firstEntry();
//	}
	
	public Weight getSmallestWeight() {
		return smallestWeight;
	}
	
//	private void setSmallestWeight(Weight weight) {
//		this.smallestWeight = weight;
//	}
	
	public Weight getDeltaWeight() {
		return smallestWeight.add(smallestCompletions.get(optimalState)); 
	}
	
//	public void getDataFrom(TreeKeeper<LabelType> t) {
//		
//		int comparison = this.smallestWeight.compareTo(t.smallestWeight);
//		
//		if (comparison == 1) {
//			this.smallestWeight = t.smallestWeight;
//			this.optimalStates = t.optimalStates;
//		} else if (comparison == 0) {
//			this.optimalStates.putAll(t.optimalStates);
//		}
//		
//		for (Entry<State, Weight> e : t.optWeights.entrySet()) {
//			
//			if (this.optWeights.containsKey(e.getKey())) {				
//				if (this.optWeights.get(e.getKey()).compareTo(e.getValue()) == 1) {
//					this.optWeights.put(e.getKey(), e.getValue());
//				}
//			} else {
//				this.optWeights.put(e.getKey(), e.getValue());
//			}
//		}
//	}

	@Override
	public int compareTo(TreeKeeper<?> o) { // TODO compare to uses W^q also (means a reinstantiation of the weight field)
		
//		int weightComparison = this.smallestWeight.compareTo(o.smallestWeight);
//		
//		if (weightComparison == 0) {
//			return this.tree.compareTo(o.tree);
//		}
//		
//		return weightComparison;
		
		return this.tree.compareTo(o.tree);
	}
	
	@Override
	public int hashCode() {
//		return this.tree.hashCode() + 3 * this.weight.hashCode();
		return this.tree.hashCode();
	}
	
	@Override
	public String toString() { // TODO Make a tostring including the 
		
		String optStatesString = "";
		
		if (optimalStates != null) {
			optStatesString = optimalStates.toString();
		}
		
		return tree.toString() + optStatesString + optWeights.toString();
//		throw new RuntimeException("Don't use this toString");
		// TODO Auto-generated method stub
		//return super.toString();
	}
	
	@Override
	public boolean equals(Object obj) { // TODO fix according to compareTo
		
		if (!(obj instanceof TreeKeeper<?>)) {
			return false;
		}
		
		return this.compareTo((TreeKeeper<?>) obj) == 0;
		
		//throw new RuntimeException("Don't use this equals");
		// TODO Auto-generated method stub
		//return super.equals(obj);
	}
}
