package edu.ufl.cise.bsmock.graph;

/**
 * The Graph class implements a weighted, directed graph using an adjacency list representation.
 *
 * Created by brandonsmock on 6/1/15.
 * Modified for BestTrees by aj in 2017.
 */

import java.util.*;

import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.semiring.Weight;

public class Graph<T> {
    private HashMap<String,Node<T>> nodes;
    private Semiring semiring;

    public Graph(Semiring semiring) {
    	this.semiring = semiring;
        nodes = new HashMap<String,Node<T>>();
    }

    public Graph(Semiring semiring, HashMap<String,Node<T>> nodes) {
    	this.semiring = semiring;
        this.nodes = nodes;
    }

    public int numNodes() {
        return nodes.size();
    }

    public int numEdges() {
        int edgeCount = 0;
        for (Node<T> node : nodes.values()) {
            edgeCount += node.getEdges().size();
        }
        return edgeCount;
    }

    public void addNode(String label) {
        if (!nodes.containsKey(label))
            nodes.put(label,new Node<T>(label));
    }

    public void addNode(Node<T> node) {
        String label = node.getLabel();
        if (!nodes.containsKey(label))
            nodes.put(label,node);
    }

    public void addEdge(String label1, String label2, Edge<T> edge) {
        if (!nodes.containsKey(label1))
            addNode(label1);
        if (!nodes.containsKey(label2))
            addNode(label2);
        nodes.get(label1).addEdge(label2, edge);
    }

    public void addEdge(String label1, String label2, Weight weight, T label) {
        if (!nodes.containsKey(label1))
            addNode(label1);
        if (!nodes.containsKey(label2))
            addNode(label2);
        nodes.get(label1).addEdge(label2, weight, label);
    }

    public void addEdge(Edge<T> edge) {
    	nodes.get(edge.getFromNode()).addEdge(edge.getToNode(), edge);
    }

    public void addEdges(List<Edge<T>> edges) {
        for (Edge<T> edge : edges) {
            addEdge(edge);
        }
    }

    public Weight getEdgeWeight(String label1, String label2) {
        if (nodes.containsKey(label1)) {
            Node<T> node1 = nodes.get(label1);
            if (node1.getNeighbors().containsKey(label2)) {
                return node1.getNeighbors().get(label2).getWeight();
            }
        }

        return semiring.zero();
    }

    public HashMap<String,Node<T>> getNodes() {
        return nodes;
    }

    public List<Edge<T>> getEdgeList() {
        List<Edge<T>> edgeList = new LinkedList<>();

        for (Node<T> node : nodes.values()) {
            edgeList.addAll(node.getEdges());
        }

        return edgeList;
    }

    public Set<String> getNodeLabels() {
        return nodes.keySet();
    }

    public Node<T> getNode(String label) {
        return nodes.get(label);
    }

    public Graph<T> transpose() {
        HashMap<String,Node<T>> newNodes = new HashMap<>();

        Iterator<String> it = nodes.keySet().iterator();
        while (it.hasNext()) {
            String nodeLabel = it.next();
            newNodes.put(nodeLabel,new Node<T>(nodeLabel));
        }

        it = nodes.keySet().iterator();
        while (it.hasNext()) {
            String nodeLabel = it.next();
            Node<T> node = nodes.get(nodeLabel);
            Set<String> adjacencyList = node.getAdjacencyList();
            Iterator<String> alIt = adjacencyList.iterator();
            HashMap<String, Edge<T>> neighbors = node.getNeighbors();
            while (alIt.hasNext()) {
                String neighborLabel = alIt.next();
                newNodes.get(neighborLabel).addEdge(nodeLabel,neighbors.get(neighborLabel));
            }
        }

        return new Graph<T>(semiring, newNodes);
    }

    public void clear() {
        nodes = new HashMap<>();
    }

    public String toString() {
        StringBuilder graphStringB = new StringBuilder();
        Iterator<String> it = nodes.keySet().iterator();
        while (it.hasNext()) {
            String nodeLabel = it.next();
            graphStringB.append(nodeLabel);
            graphStringB.append(": {");
            Node<T> node = nodes.get(nodeLabel);
            Set<String> adjacencyList = node.getAdjacencyList();
            Iterator<String> alIt = adjacencyList.iterator();
            HashMap<String, Edge<T>> neighbors = node.getNeighbors();
            while (alIt.hasNext()) {
                String neighborLabel = alIt.next();
                graphStringB.append(neighborLabel.toString());
                graphStringB.append(": ");
                graphStringB.append(neighbors.get(neighborLabel));
                if (alIt.hasNext())
                    graphStringB.append(", ");
            }
            graphStringB.append("}");
            graphStringB.append("\n");
        }

        return graphStringB.toString();
    }

}
