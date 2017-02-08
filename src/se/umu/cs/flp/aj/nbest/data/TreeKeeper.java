package se.umu.cs.flp.aj.nbest.data;

import java.util.HashMap;
import java.util.LinkedHashMap;

import se.umu.cs.flp.aj.wta.State;
import se.umu.cs.flp.aj.wta.Weight;

public class TreeKeeper<LabelType extends Comparable<LabelType>> 
		implements Comparable<TreeKeeper<?>> {
	
	private Node<LabelType> tree;
//	private ArrayList<State> optimalStates;
	private LinkedHashMap<State,State> optimalStates;
	private HashMap<State, Weight> optWeights;
	private Weight weight;
	
	public TreeKeeper(Node<LabelType> tree) {
		this.tree = tree;
		this.optimalStates = null;
		this.optWeights = new HashMap<>();
		this.weight = new Weight(Weight.INF); // or just set from outside?
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
	
	public void setOptimalStates(LinkedHashMap<State, State> optimalStates) {
		this.optimalStates = optimalStates;
	}
	
	public HashMap<State, Weight> getOptWeights() {
		return optWeights;
	}
	
	public Weight getWeight() {
		return weight;
	}
	
	public void setWeight(Weight weight) {
		this.weight = weight;
	}

	@Override
	public int compareTo(TreeKeeper<?> o) { // TODO compare to uses W^q also (means a reinstantiation of the weight field)
		
//		int weightComparison = this.weight.compareTo(o.weight);
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
