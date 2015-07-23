package se.umu.cs.nfl.aj.nbest;

import se.umu.cs.nfl.aj.wta.Weight;

public class Run<LabelType> {

	private Node<LabelType> tree;
	private Weight weight;
	
	public Run(Node<LabelType> tree, Weight weight) {
		this.tree = tree;
		this.weight = weight;
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
	
}
