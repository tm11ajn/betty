package edu.ufl.cise.bsmock.graph.util;

import java.util.HashMap;

import se.umu.cs.flp.aj.nbest.semiring.Semiring;

/**
 * Created by brandonsmock on 6/8/15.
 * Modified for BestTrees by aj in 2017.
 */
public class ShortestPathTree<T> {
    private HashMap<String,DijkstraNode<T>> nodes;
    private final String root;
    private Semiring semiring;

    public ShortestPathTree(Semiring semiring) {
    	this.semiring = semiring;
        this.nodes = new HashMap<String, DijkstraNode<T>>();
        this.root = "";
    }

    public ShortestPathTree(Semiring semiring, String root) {
    	this.semiring = semiring;
        this.nodes = new HashMap<String, DijkstraNode<T>>();
        this.root = root;
    }

    public HashMap<String, DijkstraNode<T>> getNodes() {
        return nodes;
    }

    public void setNodes(HashMap<String, DijkstraNode<T>> nodes) {
        this.nodes = nodes;
    }

    public String getRoot() {
        return root;
    }

    public void add(DijkstraNode<T> newNode) {
        nodes.put(newNode.getLabel(),newNode);
    }

    public void setParentOf(String node, String parent) {
        if (!nodes.containsKey(node))
            nodes.put(node,new DijkstraNode<T>(semiring, node));
        nodes.get(node).setParent(parent);
    }

    public String getParentOf(String node) {
        if (nodes.containsKey(node))
            return nodes.get(node).getParent();
        else
            return null;
    }
}
