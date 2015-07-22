package se.umu.cs.nfl.aj.eppstein_k_best.graph;

public class SP_Node<T> implements Comparable<SP_Node<T>> {

	private Edge<T> edge;
	private int weight;
	
	public SP_Node(Edge<T> edge, int weight) {
		this.edge = edge;
		this.weight = weight;
	}
	
	public Edge<T> getEdge() {
		return this.edge;
	}
	
	public int getWeight() {
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
