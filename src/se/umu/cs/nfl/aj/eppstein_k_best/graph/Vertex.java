package se.umu.cs.nfl.aj.eppstein_k_best.graph;

import java.util.ArrayList;

public class Vertex<T> {
	
	private String label;
	
	private Edge<T> edgeToPath = null;
	private double distance = Double.MIN_VALUE;
	private ArrayList<Edge<T>> relatedEdges = new ArrayList<>();
	
	public Vertex(String label) {
		this.label = label;
	}
	
	public Vertex<T> next() {
		
		if (edgeToPath == null) {
			return null;
		}
		
		return edgeToPath.getHead();
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public Edge<T> getEdgeToPath() {
		return this.edgeToPath;
	}
	
	public void setEdgeToPath(Edge<T> edgeToPath) {
		this.edgeToPath = edgeToPath;
	}

	public double getDistance() {
		return this.distance;
	}
	
	public void setDistance(double distance) {
		this.distance = distance;
	}

	public ArrayList<Edge<T>> getRelatedEdges() {
		return this.relatedEdges;
	}
	
	@Override
	public String toString() {
		return this.label;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof Vertex) {
			return this.label.equals(((Vertex<?>) obj).label);
		} else if (obj instanceof String) {
			return this.label.equals(((String) obj));
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.label.hashCode();
	}
}
