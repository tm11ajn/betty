package edu.ufl.cise.bsmock.graph;

import java.util.Collection;
import java.util.Collections;

/**
 * The Node class implements a node in a directed graph keyed on a label of type String, with adjacency lists for
 * representing edges.
 *
 * Created by brandonsmock on 5/31/15.
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import se.umu.cs.flp.aj.wta.Weight;

public class Node<T> {
    protected String label;
    protected HashMap<String,Edge<T>> neighbors; // adjacency list, with HashMap for each edge weight
//    protected HashMap<String,HashMap<T,Edge<T>>> neighbors; // adjacency list, with HashMap for each edge weight

    public Node() {
        neighbors = new HashMap<>();
    }

    public Node(String label) {
        this.label = label;
        neighbors = new HashMap<>();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public HashMap<String, Edge<T>> getNeighbors() {
//    public HashMap<String, HashMap<T, Edge<T>>> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(HashMap<String, Edge<T>> neighbors) {
//    public void setNeighbors(HashMap<String, HashMap<T, Edge<T>>> neighbors) {
        this.neighbors = neighbors;
    }
    
    public void addEdge(String toNodeLabel, Edge<T> edge) {
    	neighbors.put(toNodeLabel, edge);
    	
//    	if (neighbors.get(toNodeLabel) == null) {
//    		neighbors.put(toNodeLabel, new HashMap<>());
//    	}
//    	
//        neighbors.get(toNodeLabel).put(edge.getLabel(), edge);
    }

    public void addEdge(String toNodeLabel, Weight weight, T label) {
//    public void addEdge(String toNodeLabel, Weight weight, T edgeLabel) {
    	
    	neighbors.put(toNodeLabel, new Edge<T>(this.label, toNodeLabel, weight, label));
    	
//    	if (neighbors.get(toNodeLabel) == null) {
//    		neighbors.put(toNodeLabel, new HashMap<>());
//    	} 
//    	
//        neighbors.get(toNodeLabel).put(edgeLabel, new Edge<T>(this.label, toNodeLabel, weight, edgeLabel));
    }

//    public double removeEdge(String toNodeLabel) {
//        if (neighbors.containsKey(toNodeLabel)) {
//            double weight = neighbors.get(toNodeLabel);
//            neighbors.remove(toNodeLabel);
//            return weight;
//        }
//
//        return Double.MAX_VALUE;
//    }

    public Set<String> getAdjacencyList() {
        return neighbors.keySet();
    }

//    public LinkedList<Edge> getEdges() {
//        LinkedList<Edge> edges = new LinkedList<Edge>();
//        for (String toNodeLabel : neighbors.keySet()) {
//            edges.add(new Edge(label,toNodeLabel,neighbors.get(toNodeLabel)));
//        }
//
//        return edges;
//    }
    
    public Collection<Edge<T>> getEdges() {
    	
    	return neighbors.values();
    	
//    	Collection<Edge<T>> c = Collections.emptyList();
//    	
//    	for (HashMap<T, Edge<T>> val : neighbors.values()) {
//    		c.addAll(val.values());
//    	}
//    	
//    	return c;
    }
    
    public String toString() {
        StringBuilder nodeStringB = new StringBuilder();
        nodeStringB.append(label);
        nodeStringB.append(": {");
        Set<String> adjacencyList = this.getAdjacencyList();
        Iterator<String> alIt = adjacencyList.iterator();
        HashMap<String, Edge<T>> neighbors = this.getNeighbors();
        while (alIt.hasNext()) {
            String neighborLabel = alIt.next();
            nodeStringB.append(neighborLabel.toString());
            nodeStringB.append(": ");
            nodeStringB.append(neighbors.get(neighborLabel));
            if (alIt.hasNext())
                nodeStringB.append(", ");
        }
        nodeStringB.append("}");
        nodeStringB.append("\n");

        return nodeStringB.toString();
        
//        StringBuilder nodeStringB = new StringBuilder();
//        nodeStringB.append(label);
//        nodeStringB.append(": {");
//        Set<String> adjacencyList = this.getAdjacencyList();
//        Iterator<String> alIt = adjacencyList.iterator();
//        HashMap<String, HashMap<T, Edge<T>>> neighbors = this.getNeighbors();
//        while (alIt.hasNext()) {
//            String neighborLabel = alIt.next();
//            nodeStringB.append(neighborLabel.toString());
//            nodeStringB.append(": ");
//            nodeStringB.append(neighbors.get(neighborLabel));
//            if (alIt.hasNext())
//                nodeStringB.append(", ");
//        }
//        nodeStringB.append("}");
//        nodeStringB.append("\n");
//
//        return nodeStringB.toString();
    }
}
