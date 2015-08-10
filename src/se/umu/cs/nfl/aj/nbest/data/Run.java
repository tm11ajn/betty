package se.umu.cs.nfl.aj.nbest.data;

import se.umu.cs.nfl.aj.wta.Symbol;
import se.umu.cs.nfl.aj.wta.Weight;

public class Run {
	
	private Node<Symbol> tree;
	private Weight weight;
	
	public Run(Node<Symbol> tree, Weight weight) {
		this.tree = tree;
		this.weight = weight;
	}
	
	public Node<Symbol> getTree() {
		return this.tree;
	}
	
	public Weight getWeight() {
		return this.weight;
	}
	
	
}
