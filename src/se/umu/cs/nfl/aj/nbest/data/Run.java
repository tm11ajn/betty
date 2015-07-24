package se.umu.cs.nfl.aj.nbest.data;

import se.umu.cs.nfl.aj.wta.State;
import se.umu.cs.nfl.aj.wta.Weight;

public class Run<LabelType> {

	private Node<LabelType> tree;
	private Weight weight;
	private State state;
	
	public Run(Node<LabelType> tree, Weight weight, State state) {
		this.tree = tree;
		this.weight = weight;
		this.state = state;
	}
	
	public Node<LabelType> getTree() {
		return this.tree;
	}
	
	public Weight getWeight() {
		return this.weight;
	}
	
	public void setWeight(Weight weight) {
		this.weight = weight;
	}
	
	public State getState() {
		return this.state;
	}
	
}
