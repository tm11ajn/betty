package edu.ufl.cise.bsmock.graph.util;

/**
 * Created by brandonsmock on 6/1/15.
 * Modified for BestTrees by aj in 2017.
 */
import java.util.*;

import edu.ufl.cise.bsmock.graph.*;
import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.semiring.Weight;

public final class Dijkstra<T> {

	private Semiring semiring;

    public Dijkstra(Semiring semiring) {
    	this.semiring = semiring;
    }

    public ShortestPathTree<T> shortestPathTree(Graph<T> graph, String sourceLabel) throws Exception {

        HashMap<String,Node<T>> nodes = graph.getNodes();
        if (!nodes.containsKey(sourceLabel))
            throw new Exception("Source node not found in graph.");
        ShortestPathTree<T> predecessorTree =
        		new ShortestPathTree<T>(semiring, sourceLabel);
        Set<DijkstraNode<T>> visited = new HashSet<DijkstraNode<T>>();
        PriorityQueue<DijkstraNode<T>> pq = new PriorityQueue<DijkstraNode<T>>();
        for (String nodeLabel:nodes.keySet()) {
            DijkstraNode<T> newNode = new DijkstraNode<>(semiring, nodeLabel);
            newNode.setDist(semiring.zero());
            newNode.setDepth(Integer.MAX_VALUE);
            predecessorTree.add(newNode);
        }
        DijkstraNode<T> sourceNode = predecessorTree.getNodes().get(predecessorTree.getRoot());
        sourceNode.setDist(semiring.one());
        sourceNode.setDepth(0);
        pq.add(sourceNode);
        while (!pq.isEmpty()) {
        	DijkstraNode<T> current = pq.poll();
        	String currLabel = current.getLabel();
        	visited.add(current);
        	HashMap<String, Edge<T>> neighbors = nodes.get(currLabel).getNeighbors();
        	for (String currNeighborLabel:neighbors.keySet()) {
        		DijkstraNode<T> neighborNode = predecessorTree.getNodes().get(currNeighborLabel);
        		Weight currDistance = neighborNode.getDist();
        		Weight newDistance = current.getDist().mult(nodes.get(currLabel).getNeighbors().get(currNeighborLabel).getWeight());
        		if (newDistance.compareTo(currDistance) == -1) {
        			DijkstraNode<T> neighbor = predecessorTree.getNodes().get(currNeighborLabel);
        			pq.remove(neighbor);
        			neighbor.setDist(newDistance);
        			neighbor.setDepth(current.getDepth() + 1);
        			neighbor.setParent(currLabel);
        			pq.add(neighbor);
        		}
        	}
        }
        return predecessorTree;
    }

    public Path<T> shortestPath(Graph<T> graph, String sourceLabel, String targetLabel) {
    	HashMap<String, Node<T>> nodes = graph.getNodes();
        ShortestPathTree<T> predecessorTree =
        		new ShortestPathTree<>(semiring, sourceLabel);
        PriorityQueue<DijkstraNode<T>> pq = new PriorityQueue<>();
        for (String nodeLabel:nodes.keySet()) {
            DijkstraNode<T> newNode = new DijkstraNode<>(semiring, nodeLabel);
            newNode.setDist(semiring.zero());
            newNode.setDepth(Integer.MAX_VALUE);
            predecessorTree.add(newNode);
        }
        DijkstraNode<T> sourceNode = predecessorTree.getNodes().get(predecessorTree.getRoot());
//System.out.println(predecessorTree.getRoot());
//System.out.println(sourceNode);
        sourceNode.setDist(semiring.one());
        sourceNode.setDepth(0);
        pq.add(sourceNode);
        while (!pq.isEmpty()) {
            DijkstraNode<T> current = pq.poll();
            String currLabel = current.getLabel();
            if (currLabel.equals(targetLabel)) {

                Path<T> shortestPath = new Path<>(semiring);
                String currentN = targetLabel;
                String parentN = predecessorTree.getParentOf(currentN);

                while (parentN != null) {
                	Edge<T> currentE = nodes.get(parentN).getNeighbors().get(currentN);
                    shortestPath.addFirst(new Edge<T>(parentN, currentN, currentE.getWeight(), currentE.getLabel()));
                    currentN = parentN;
                    parentN = predecessorTree.getParentOf(currentN);
                }
                return shortestPath;
            }
            HashMap<String, Edge<T>> neighbors = nodes.get(currLabel).getNeighbors();
            for (String currNeighborLabel:neighbors.keySet()) {
                DijkstraNode<T> neighborNode = predecessorTree.getNodes().get(currNeighborLabel);
                Weight currDistance = neighborNode.getDist();
                Weight newDistance = current.getDist().mult(nodes.get(currLabel).getNeighbors().get(currNeighborLabel).getWeight());
                if (newDistance.compareTo(currDistance) == -1) {
                    DijkstraNode<T> neighbor = predecessorTree.getNodes().get(currNeighborLabel);
                    pq.remove(neighbor);
                    neighbor.setDist(newDistance);
                    neighbor.setDepth(current.getDepth() + 1);
                    neighbor.setParent(currLabel);
                    pq.add(neighbor);
                }
            }
        }
        return null;
    }
}
