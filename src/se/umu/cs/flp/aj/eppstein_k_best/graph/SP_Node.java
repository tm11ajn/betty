package se.umu.cs.flp.aj.eppstein_k_best.graph;

public class SP_Node<T> implements Comparable<SP_Node<T>> {

	private Edge<T> edge;
	private double weight;
	
	public SP_Node(Edge<T> edge, double weight) {
		this.edge = edge;
		this.weight = weight;
	}
	
	public Edge<T> getEdge() {
		return this.edge;
	}
	
	public double getWeight() {
		return this.weight;
	}
	
	@Override
	public int compareTo(SP_Node<T> o) {
		
		if (this.weight == o.weight) {
			return 0;
		} else if (this.weight < o.weight) {
			return -1;
		}
		
		return 1;
	}
	
	@Override
	public String toString() {
		return this.edge + " [" + this.weight + "]";
	}

}
