package se.umu.cs.nfl.aj.eppstein_k_best.graph;

import java.util.ArrayList;

public class Vertex {
	
	private String label;
	
	private Edge edgeToPath = null;
	private int distance = Integer.MIN_VALUE;
	private ArrayList<Edge> relatedEdges = new ArrayList<>();
	
	public Vertex(String label) {
		this.label = label;
	}
	
	public Vertex next() {
		
		if (edgeToPath == null) {
			return null;
		}
		
		return edgeToPath.getHead();
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public Edge getEdgeToPath() {
		return this.edgeToPath;
	}
	
	public void setEdgeToPath(Edge edgeToPath) {
		this.edgeToPath = edgeToPath;
	}

	public int getDistance() {
		return this.distance;
	}
	
	public void setDistance(int distance) {
		this.distance = distance;
	}

	public ArrayList<Edge> getRelatedEdges() {
		return this.relatedEdges;
	}
	
	@Override
	public String toString() {
		return this.label;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof Vertex) {
			return this.label.equals(((Vertex) obj).label);
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
