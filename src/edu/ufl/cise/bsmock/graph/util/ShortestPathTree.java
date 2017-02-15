package edu.ufl.cise.bsmock.graph.util;

import java.util.HashMap;

/**
 * Created by brandonsmock on 6/8/15.
 */
public class ShortestPathTree<T> {
    private HashMap<String,DijkstraNode<T>> nodes;
    private final String root;

    public ShortestPathTree() {
        this.nodes = new HashMap<String, DijkstraNode<T>>();
        this.root = "";
    }

    public ShortestPathTree(String root) {
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
//        if (parent != null && !nodes.containsKey(parent)) {
//            System.out.println("Warning: parent node not present in tree.");
//        }
        if (!nodes.containsKey(node))
            nodes.put(node,new DijkstraNode<T>(node));

        nodes.get(node).setParent(parent);

    }

    public String getParentOf(String node) {
        if (nodes.containsKey(node))
            return nodes.get(node).getParent();
        else
            return null;
    }
}
