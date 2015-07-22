package se.umu.cs.nfl.aj.eppstein_k_best.graph;

public class Edge<T> {
	
	private Vertex<T> tail;
	private Vertex<T> head;
	private T label;
	private int weight;
	private String group;
	
	public Edge(Vertex<T> tail, Vertex<T> head, T label, int weight, String group) {
		this.tail = tail;
		this.head = head;
		this.label = label;
		this.weight = weight;
		this.group = group;
	}
	
	public Edge(Vertex<T> tail, Vertex<T> head, int weight, String group) {
		this.tail = tail;
		this.head = head;
		this.weight = weight;
		this.group = group;
	}
	
	public Vertex<T> getTail() {
		return this.tail;
	}

	public Vertex<T> getHead() {
		return this.head;
	}
	
	public T getLabel() {
		return label;
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
	
	public boolean isSidetrackOf(Vertex<T> v) {
		return (this.tail == v && this != v.getEdgeToPath() && this.weight >= 0); 
	}
	
	@Override
	public String toString() {
		return this.tail + "--" + this.weight + "-->" + this.head;
	}

}
