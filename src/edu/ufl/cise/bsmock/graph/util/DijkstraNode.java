package edu.ufl.cise.bsmock.graph.util;

import java.util.HashMap;
import java.util.Set;
import edu.ufl.cise.bsmock.graph.*;
import se.umu.cs.flp.aj.wta.Weight;

/**
 * Created by brandonsmock on 6/6/15.
 */
public class DijkstraNode<T> extends Node<T> implements Comparable<DijkstraNode<T>> {
    private Weight dist = new Weight(Weight.INF);
    private int depth;

    public DijkstraNode(Weight dist) {
        super();
        this.dist = dist;
    }

    public DijkstraNode(String label) {
        super(label);
        this.dist = new Weight(0);
    }

    public DijkstraNode(String label, Weight dist) {
        super(label);
        this.dist = dist;
    }

//    public DijkstraNode(String label, double dist, int depth, String parent) {
//        super(label);
//        this.dist = dist;
//        this.depth = depth;
//        super.addEdge(parent,0.0);
//    }
    
    public DijkstraNode(String label, Edge<T> edge, int depth, String parent) {
        super(label);
        this.dist = edge.getWeight();
        this.depth = depth;
        super.addEdge(parent, edge);
    }

    public Weight getDist() {
        return dist;
    }

    public void setDist(Weight dist) {
        this.dist = dist;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setParent(String parent) {
        super.neighbors = new HashMap<>();
        super.neighbors.put(parent,null);
    }

    public String getParent() {
        Set<String> neighborLabels = super.neighbors.keySet();
        if (neighborLabels.size() > 1) {
            return null;
        }
        if (neighborLabels.size() < 1) {
            return null;
        }
        return super.neighbors.keySet().iterator().next();
    }

    public int compareTo(DijkstraNode<T> comparedNode) {
        Weight distance1 = this.dist;
        Weight distance2 = comparedNode.getDist();
        return distance1.compareTo(distance2);
    }

    public boolean equals(DijkstraNode<T> comparedNode) {
        return this.getLabel().equals(comparedNode.getLabel());
    }
}
