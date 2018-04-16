package edu.ufl.cise.bsmock.graph.util;

import java.util.*;

import edu.ufl.cise.bsmock.graph.*;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.semiring.TropicalWeight;

/**
 * The Path class implements a path in a weighted, directed graph as a sequence of Edges.
 *
 * Created by Brandon Smock on 6/18/15.
 * Modified for BestTrees by aj in 2017.
 */
public class Path<T> implements Comparable<Path<T>> {
    private LinkedList<Edge<T>> edges;
    private Weight totalCost;

    public Path() {
        edges = new LinkedList<>();
        totalCost = (new TropicalWeight()).one();
    }

    public Path(Weight totalCost) {
        edges = new LinkedList<>();
        this.totalCost = totalCost;
    }

    public Path(LinkedList<Edge<T>> edges) {
        this.edges = edges;
        totalCost = (new TropicalWeight()).one();
        for (Edge<T> edge : edges) {
        	totalCost.mult(edge.getWeight());
        }
    }

    public Path(LinkedList<Edge<T>> edges, Weight totalCost) {
        this.edges = edges;
        this.totalCost = totalCost;
    }

    public LinkedList<Edge<T>> getEdges() {
        return edges;
    }

    public void setEdges(LinkedList<Edge<T>> edges) {
        this.edges = edges;
    }

    public List<String> getNodes() {
        LinkedList<String> nodes = new LinkedList<String>();

        for (Edge<T> edge : edges) {
            nodes.add(edge.getFromNode());
        }

        Edge<T> lastEdge = edges.getLast();
        if (lastEdge != null) {
            nodes.add(lastEdge.getToNode());
        }

        return nodes;
    }

    public Weight getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Weight totalCost) {
        this.totalCost = totalCost;
    }

    public void addFirstNode(String nodeLabel) {
    	Edge<T> firstEdge = edges.getFirst();
        String firstNode = firstEdge.getFromNode();
        edges.addFirst(new Edge<T>(nodeLabel, firstNode, (new TropicalWeight()).one(),
        		firstEdge.getLabel()));
    }

    public void addFirst(Edge<T> edge) {
        edges.addFirst(edge);
        totalCost = totalCost.mult(edge.getWeight());
    }

    public void add(Edge<T> edge) {
        edges.add(edge);
        totalCost = totalCost.mult(edge.getWeight());
    }

    public void addLastNode(String nodeLabel) {
    	Edge<T> lastEdge = edges.getLast();
        String lastNode = lastEdge.getToNode();
        edges.addLast(new Edge<T>(lastNode, nodeLabel, (new TropicalWeight()).one(),
        		lastEdge.getLabel()));
    }

    public int size() {
        return edges.size();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        int numEdges = edges.size();
        sb.append(totalCost);
        sb.append(": [");
        if (numEdges > 0) {
            for (int i = 0; i < edges.size(); i++) {
                sb.append(edges.get(i).getFromNode().toString());
                sb.append("-");
            }

            sb.append(edges.getLast().getToNode().toString());
        }
        sb.append("]");
        return sb.toString();
    }

    public boolean equals(Path<?> path2) {
        if (path2 == null)
            return false;

        LinkedList<?> edges2 = path2.getEdges();

        int numEdges1 = edges.size();
        int numEdges2 = edges2.size();

        if (numEdges1 != numEdges2) {
            return false;
        }

        for (int i = 0; i < numEdges1; i++) {
            Edge<?> edge1 = edges.get(i);
            Edge<?> edge2 = (Edge<?>) edges2.get(i);
            if (!edge1.getFromNode().equals(edge2.getFromNode()))
                return false;
            if (!edge1.getToNode().equals(edge2.getToNode()))
                return false;
        }

        return true;
    }

    public int compareTo(Path<T> path2) {
        Weight path2Cost = path2.getTotalCost();
        return totalCost.compareTo(path2Cost);
    }
}
