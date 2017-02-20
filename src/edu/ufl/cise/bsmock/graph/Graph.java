package edu.ufl.cise.bsmock.graph;

/**
 * The Graph class implements a weighted, directed graph using an adjacency list representation.
 *
 * Created by brandonsmock on 6/1/15.
 */

import java.util.*;

import se.umu.cs.flp.aj.wta.Weight;

public class Graph<T> {
    private HashMap<String,Node<T>> nodes;

    public Graph() {
        nodes = new HashMap<String,Node<T>>();
    }

    public Graph(HashMap<String,Node<T>> nodes) {
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
//        addEdge(edge.getFromNode(),edge.getToNode(),edge.getWeight());
    	nodes.get(edge.getFromNode()).addEdge(edge.getToNode(), edge);
    }

    public void addEdges(List<Edge<T>> edges) {
        for (Edge<T> edge : edges) {
            addEdge(edge);
        }
    }

//    public Edge removeEdge(String label1, String label2) {
//        if (nodes.containsKey(label1)) {
//            double weight = nodes.get(label1).removeEdge(label2);
//            if (weight != Double.MAX_VALUE) {
//                return new Edge(label1, label2, weight);
//            }
//        }
//
//        return null;
//    }

    public Weight getEdgeWeight(String label1, String label2) {
        if (nodes.containsKey(label1)) {
            Node<T> node1 = nodes.get(label1);
            if (node1.getNeighbors().containsKey(label2)) {
//                return node1.getNeighbors().get(label2);
                return node1.getNeighbors().get(label2).getWeight();
            }
        }

        return new Weight(Weight.INF);
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

//    public List<Edge> removeNode(String label) {
//        LinkedList<Edge> edges = new LinkedList<Edge>();
//        if (nodes.containsKey(label)) {
//            Node node = nodes.remove(label);
//            edges.addAll(node.getEdges());
//            edges.addAll(removeEdgesToNode(label));
//        }
//
//        return edges;
//    }

//    public List<Edge> removeEdgesToNode(String label) {
//        List<Edge> edges = new LinkedList<Edge>();
//        for (Node node : nodes.values()) {
//            if (node.getAdjacencyList().contains(label)) {
//                double weight = node.removeEdge(label);
//                edges.add(new Edge(node.getLabel(),label,weight));
//            }
//        }
//        return edges;
//    }



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

        return new Graph<T>(newNodes);
    }

    public void clear() {
        nodes = new HashMap<>();
    }

    public String toString() {
        StringBuilder graphStringB = new StringBuilder();
        Iterator<String> it = nodes.keySet().iterator();
        while (it.hasNext()) {
            String nodeLabel = it.next();
//            graphStringB.append(nodeLabel.toString());
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
