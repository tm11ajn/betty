package edu.ufl.cise.bsmock.graph;

import java.util.Collection;

/**
 * The Node class implements a node in a directed graph keyed on a label of type String, with adjacency lists for
 * representing edges.
 *
 * Created by brandonsmock on 5/31/15.
 * Modified for BestTrees by aj in 2017.
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import se.umu.cs.flp.aj.wta.Weight;

public class Node<T> {
    protected String label;
    protected HashMap<String,Edge<T>> neighbors; // adjacency list, with HashMap for each edge weight

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
        return neighbors;
    }

    public void setNeighbors(HashMap<String, Edge<T>> neighbors) {
        this.neighbors = neighbors;
    }

    public void addEdge(String toNodeLabel, Edge<T> edge) {
    	neighbors.put(toNodeLabel, edge);
    }

    public void addEdge(String toNodeLabel, Weight weight, T label) {
    	neighbors.put(toNodeLabel, new Edge<T>(this.label, toNodeLabel, weight, label));
    }

    public Set<String> getAdjacencyList() {
        return neighbors.keySet();
    }

    public Collection<Edge<T>> getEdges() {
    	return neighbors.values();
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
    }
}
