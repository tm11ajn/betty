package se.umu.cs.nfl.aj.eppstein_k_best.graph;

public class ST_Node implements Comparable<ST_Node> {

	private Path sidetracks;
	private int weight;
	
	public ST_Node(Path sidetracks) {
		this.sidetracks = sidetracks;
		this.weight = sidetracks.getDeltaWeight();
	}
	
	public Path getSidetracks() {
		return this.sidetracks;
	}
	
	public int getWeight() {
		return this.weight;
	}
	
	@Override
	public int compareTo(ST_Node o) {
		
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
