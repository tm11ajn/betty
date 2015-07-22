package se.umu.cs.nfl.aj.eppstein_k_best.graph;

import java.util.ArrayList;

public class Path extends ArrayList<Edge> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public boolean isValid() {
		return (this.size() > 0);
	}
	
	public String getVertexNames() {
		
		if (!this.isValid()) {
			return "(empty)";
		}
		
		String string = "" + this.get(0).getTail();
		
		for (Edge e : this) {
			string += "" + e.getHead();
		}
		
		return string;
	}
	
	public int getWeight() {
		int weightSum = 0;
		
		for (Edge e : this) {
			weightSum += e.getWeight();
		}
		
		return weightSum;
	}
	
	public int getDeltaWeight() {
		int deltaWeightSum = 0;
		
		for (Edge e : this) {
			deltaWeightSum += e.getDelta();
		}
		
		return deltaWeightSum;
	}
	
	@Override
	public String toString() {
		
		if (!this.isValid()) {
			return "(empty)";
		}
		
		int size = this.size();
		
		String string = "" + this.get(0).getDelta();
		
		for (int i = 1; i < size; i++) {
			string += ", " + this.get(i).getDelta();
		}
		
		return string;
	}
}
