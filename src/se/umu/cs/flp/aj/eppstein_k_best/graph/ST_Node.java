package se.umu.cs.flp.aj.eppstein_k_best.graph;

public class ST_Node<T> implements Comparable<ST_Node<T>> {

	private Path<T> sidetracks;
	private double weight;
	
	public ST_Node(Path<T> sidetracks) {
		this.sidetracks = sidetracks;
		this.weight = sidetracks.getDeltaWeight();
	}
	
	public Path<T> getSidetracks() {
		return this.sidetracks;
	}
	
	public double getWeight() {
		return this.weight;
	}
	
	@Override
	public int compareTo(ST_Node<T> o) {
		
		if (this.weight == o.weight) {
			return 0;
		} else if (this.weight < o.weight) {
			return -1;
		}
		
		return 1;
	}
	
	@Override
	public String toString() {
		return this.sidetracks + " [" + this.weight + "]";
	}

}
