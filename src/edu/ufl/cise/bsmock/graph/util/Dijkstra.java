package edu.ufl.cise.bsmock.graph.util;

/**
 * Created by brandonsmock on 6/1/15.
 */
import java.util.*;
import edu.ufl.cise.bsmock.graph.*;
import se.umu.cs.flp.aj.wta.Weight;

public final class Dijkstra<T> {

    public Dijkstra() {}

    public ShortestPathTree<T> shortestPathTree(Graph<T> graph, String sourceLabel) throws Exception {
System.out.println("SHORTEST PATH TREE");
        HashMap<String,Node<T>> nodes = graph.getNodes();
//System.out.println("nodes=" + nodes);
        if (!nodes.containsKey(sourceLabel))
            throw new Exception("Source node not found in graph.");
        ShortestPathTree<T> predecessorTree = new ShortestPathTree<T>(sourceLabel);
        Set<DijkstraNode<T>> visited = new HashSet<DijkstraNode<T>>();
        PriorityQueue<DijkstraNode<T>> pq = new PriorityQueue<DijkstraNode<T>>();
        for (String nodeLabel:nodes.keySet()) {
            DijkstraNode<T> newNode = new DijkstraNode<>(nodeLabel);
            newNode.setDist(new Weight(Weight.INF));
            newNode.setDepth(Integer.MAX_VALUE);
            predecessorTree.add(newNode);
        }
        DijkstraNode<T> sourceNode = predecessorTree.getNodes().get(predecessorTree.getRoot());
        sourceNode.setDist(new Weight(0));
        sourceNode.setDepth(0);
        pq.add(sourceNode);

//        int count = 0;
        while (!pq.isEmpty()) {
            DijkstraNode<T> current = pq.poll();
            String currLabel = current.getLabel();
            visited.add(current);
//            count++;
//            HashMap<String, Double> neighbors = nodes.get(currLabel).getNeighbors();
            HashMap<String, Edge<T>> neighbors = nodes.get(currLabel).getNeighbors();
            for (String currNeighborLabel:neighbors.keySet()) {
                DijkstraNode<T> neighborNode = predecessorTree.getNodes().get(currNeighborLabel);
                Weight currDistance = neighborNode.getDist();
System.out.println("currDistance=" + currDistance);
                Weight newDistance = current.getDist().add(nodes.get(currLabel).getNeighbors().get(currNeighborLabel).getWeight());
System.out.println("newDistance=" + newDistance);
                if (newDistance.compareTo(currDistance) == -1) {
                    DijkstraNode<T> neighbor = predecessorTree.getNodes().get(currNeighborLabel);
System.out.println("Update " + currNeighborLabel + " with " + newDistance);
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

    public Path<T> shortestPath(Graph<T> graph, String sourceLabel, String targetLabel) throws Exception {
        //if (!nodes.containsKey(sourceLabel))
        //    throw new Exception("Source node not found in graph.");
    	HashMap<String, Node<T>> nodes = graph.getNodes();
    	
System.out.println("SHORTEST PATH");
System.out.println("source=" + sourceLabel + ", target=" + targetLabel);
System.out.println("nodes=" + nodes);
        ShortestPathTree<T> predecessorTree = new ShortestPathTree<>(sourceLabel);
        PriorityQueue<DijkstraNode<T>> pq = new PriorityQueue<>();
        for (String nodeLabel:nodes.keySet()) {
            DijkstraNode<T> newNode = new DijkstraNode<>(nodeLabel);
            newNode.setDist(new Weight(Weight.INF));
            newNode.setDepth(Integer.MAX_VALUE);
            predecessorTree.add(newNode);
        }
        DijkstraNode<T> sourceNode = predecessorTree.getNodes().get(predecessorTree.getRoot());
        sourceNode.setDist(new Weight(0));
        sourceNode.setDepth(0);
        pq.add(sourceNode);
System.out.println("Adds source node " + sourceNode);

 //       int count = 0;
        while (!pq.isEmpty()) {
            DijkstraNode<T> current = pq.poll();
System.out.println("current dijkstra node = " + current + " (target label is " + targetLabel + ")");
            String currLabel = current.getLabel();
            if (currLabel.equals(targetLabel)) {
            	
                Path<T> shortestPath = new Path<>();
                String currentN = targetLabel;
                String parentN = predecessorTree.getParentOf(currentN);

                while (parentN != null) {
System.out.println("currentN=" + currentN);
System.out.println("parentN=" + parentN);
                	Edge<T> currentE = nodes.get(parentN).getNeighbors().get(currentN);
System.out.println("Adds currentE=" + currentE);
                    shortestPath.addFirst(new Edge<T>(parentN, currentN, currentE.getWeight(), currentE.getLabel()));
//                	shortestPath.addFirst(currentE);
System.out.println("... yielding shortest path: " + shortestPath);
                    currentN = parentN;
                    parentN = predecessorTree.getParentOf(currentN);
                }
                
System.out.println("shortestPath=" + shortestPath);
                return shortestPath;
            }
//            count++;
//            HashMap<String, Double> neighbors = nodes.get(currLabel).getNeighbors();
            HashMap<String, Edge<T>> neighbors = nodes.get(currLabel).getNeighbors();
            for (String currNeighborLabel:neighbors.keySet()) {
                DijkstraNode<T> neighborNode = predecessorTree.getNodes().get(currNeighborLabel);
System.out.println("current neighbor node = " + neighborNode);
                Weight currDistance = neighborNode.getDist();
                Weight newDistance = current.getDist().add(nodes.get(currLabel).getNeighbors().get(currNeighborLabel).getWeight());
System.out.println("currDistance=" + currDistance);
System.out.println("newDistance=" + newDistance);
                if (newDistance.compareTo(currDistance) == -1) {
                    DijkstraNode<T> neighbor = predecessorTree.getNodes().get(currNeighborLabel);
System.out.println("Update " + currNeighborLabel + " with " + newDistance);
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
