package edu.ufl.cise.bsmock.graph.util;

import java.util.*;
import edu.ufl.cise.bsmock.graph.*;
import se.umu.cs.flp.aj.wta.Weight;

/**
 * The Path class implements a path in a weighted, directed graph as a sequence of Edges.
 *
 * Created by Brandon Smock on 6/18/15.
 */
public class Path<T> implements Comparable<Path<T>> {
    private LinkedList<Edge<T>> edges;
    private Weight totalCost;

    public Path() {
        edges = new LinkedList<>();
        totalCost = new Weight(0);
    }

    public Path(Weight totalCost) {
        edges = new LinkedList<>();
        this.totalCost = totalCost;
    }

    public Path(LinkedList<Edge<T>> edges) {
        this.edges = edges;
        totalCost = new Weight(0);
        for (Edge<T> edge : edges) {
        	totalCost.add(edge.getWeight());
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
//        String firstNode = edges.getFirst().getFromNode();
//        edges.addFirst(new Edge(nodeLabel, firstNode,0));
    	Edge<T> firstEdge = edges.getFirst();
        String firstNode = firstEdge.getFromNode();
        edges.addFirst(new Edge<T>(nodeLabel, firstNode, new Weight(0), firstEdge.getLabel())); // TODO check
    }

    public void addFirst(Edge<T> edge) {
        edges.addFirst(edge);
        totalCost.add(edge.getWeight());
    }

    public void add(Edge<T> edge) {
        edges.add(edge);
        totalCost.add(edge.getWeight());
    }

    public void addLastNode(String nodeLabel) {
//        String lastNode = edges.getLast().getToNode();
//        edges.addLast(new Edge(lastNode, nodeLabel,0));
    	Edge<T> lastEdge = edges.getLast();
        String lastNode = lastEdge.getToNode();
        edges.addLast(new Edge<T>(lastNode, nodeLabel, new Weight(0), lastEdge.getLabel()));
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

/*    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Path path = (Path) o;

        if (Double.compare(path.totalCost, totalCost) != 0) return false;
        if (!edges.equals(path.edges)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = edges.hashCode();
        temp = Double.doubleToLongBits(totalCost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }*/

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
        
//        if (totalCost.equals(path2Cost)) {
//            return 0;
//        }
//        if (totalCost > path2Cost)
//            return 1;
//        return -1;
    }

//    public Path clone() {
//        LinkedList<Edge> edges = new LinkedList<Edge>();
//
//        for (Edge edge : this.edges) {
//            edges.add(edge.clone());
//        }
//
//        return new Path(edges);
//    }

//    public Path shallowClone() {
//        LinkedList<Edge> edges = new LinkedList<Edge>();
//
//        for (Edge edge : this.edges) {
//            edges.add(edge);
//        }
//
//        return new Path(edges,this.totalCost);
//    }

//    public Path cloneTo(int i) {
//        LinkedList<Edge> edges = new LinkedList<Edge>();
//        int l = this.edges.size();
//        if (i > l)
//            i = l;
//
//        //for (Edge edge : this.edges.subList(0,i)) {
//        for (int j = 0; j < i; j++) {
//            edges.add(this.edges.get(j).clone());
//        }
//
//        return new Path(edges);
//    }

//    public Path cloneFrom(int i) {
//        LinkedList<Edge> edges = new LinkedList<Edge>();
//
//        for (Edge edge : this.edges.subList(i,this.edges.size())) {
//            edges.add(edge.clone());
//        }
//
//        return new Path(edges);
//    }

//    public void addPath(Path p2) {
//        // ADD CHECK TO SEE THAT PATH P2'S FIRST NODE IS SAME AS THIS PATH'S LAST NODE
//
//        this.edges.addAll(p2.getEdges());
//        this.totalCost += p2.getTotalCost();
//    }
}
