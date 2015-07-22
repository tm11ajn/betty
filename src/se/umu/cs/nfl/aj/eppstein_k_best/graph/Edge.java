package se.umu.cs.nfl.aj.eppstein_k_best.graph;

public class Edge {
	
	private Vertex tail;
	private Vertex head;
	private int weight;
	private String group;
	
	public Edge(Vertex tail, Vertex head, int weight, String group) {
		this.tail = tail;
		this.head = head;
		this.weight = weight;
		this.group = group;
	}
	
	public Vertex getTail() {
		return this.tail;
	}

	public Vertex getHead() {
		return this.head;
	}
	
	public int getWeight() {
		return this.weight;
	}
	
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public String getGroup() {
		return this.group;
	}
	
	public int getDelta() {
		return this.weight + this.head.getDistance() - this.tail.getDistance();
	}
	
	public boolean isSidetrackOf(Vertex v) {
		return (this.tail == v && this != v.getEdgeToPath() && this.weight >= 0); 
	}
	
	@Override
	public String toString() {
		return this.tail + "--" + this.weight + "-->" + this.head;
	}

}
