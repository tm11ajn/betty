package edu.ufl.cise.bsmock.graph;

import se.umu.cs.flp.aj.wta.Weight;

/**
 * The Edge class implements standard properties and methods for a weighted edge in a directed graph.
 *
 * Created by Brandon Smock on 6/19/15.
 */
//public class Edge<T> implements Cloneable {
public class Edge<T> implements Cloneable, Comparable<Edge<?>> {
    private String fromNode;
    private String toNode;
    private Weight weight;
    private T label;

    public Edge() {
        this.fromNode = null;
        this.toNode = null;
        this.weight = new Weight(Double.MAX_VALUE);
        this.label = null;
    }

    public Edge(String fromNode, String toNode, Weight weight, T label) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.weight = weight;
        this.label = label;
    }

    public String getFromNode() {
        return fromNode;
    }

    public void setFromNode(String fromNode) {
        this.fromNode = fromNode;
    }

    public String getToNode() {
        return toNode;
    }

    public void setToNode(String toNode) {
        this.toNode = toNode;
    }

    public Weight getWeight() {
        return weight;
    }

    public void setWeight(Weight weight) {
        this.weight = weight;
    }
    
    public T getLabel() {
		return label;
	}

    public Edge<T> clone() {
        return new Edge<T>(fromNode, toNode, weight, label);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(fromNode);
        sb.append(",");
        sb.append(toNode);
        sb.append("){");
        sb.append(weight);
        sb.append("}");
        sb.append("{");
        sb.append(label);
        sb.append("}");

        return sb.toString();
    }

    public boolean equals(Edge<?> edge2) {
//        if (hasSameEndpoints(edge2) && weight == edge2.getWeight())
//            return true;
//
//        return false;
    	
    	if (!(edge2 instanceof Edge<?>)) {
    		return false;
    	}
    	
    	return this.compareTo((Edge<?>) edge2) == 0;
    }

    public boolean hasSameEndpoints(Edge<?> edge2) {
        if (fromNode.equals(edge2.getFromNode()) && toNode.equals(edge2.getToNode()))
            return true;

        return false;
    }

	@Override
	public int compareTo(Edge<?> o) {
		
		int comp = this.weight.compareTo(o.getWeight());
		
		if (comp != 0) {
			return comp;
		}
		
		comp = this.label.toString().compareTo(o.getLabel().toString());
		
		if (comp != 0) {
			return comp;
		}
		
		comp = fromNode.compareTo(o.getFromNode());
		
		if (comp != 0) {
			return comp;
			
		} else {
			comp = toNode.compareTo(o.toNode);
					
			if (comp != 0) {
				return comp;
			}
		}
		
		return 0;
	}
}
