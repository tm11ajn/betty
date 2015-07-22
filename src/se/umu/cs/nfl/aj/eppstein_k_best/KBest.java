package se.umu.cs.nfl.aj.eppstein_k_best;

import se.umu.cs.nfl.aj.eppstein_k_best.graph.Edge;
import se.umu.cs.nfl.aj.eppstein_k_best.graph.Graph;
import se.umu.cs.nfl.aj.eppstein_k_best.graph.Path;

public class KBest {

	public static void main(String[] args) {
		Graph<String> g = new Graph<>();
		g.createVertices("S,A,B,C,D,E,F,G,H,I,J,K,T");
		
		g.createEdge("S", "A", "2", 2);
		g.createEdge("A", "B", "20", 20);
		g.createEdge("E", "I", "20", 20);
        g.createEdge("B", "C", "14", 14);
        g.createEdge("B", "F", "14", 14);
        g.createEdge("D", "E", "9", 9);
        g.createEdge("E", "F", "10", 10);
        g.createEdge("F", "G", "25", 25);
        g.createEdge("H", "I", "18", 18);
        g.createEdge("I", "J", "8", 8);
        g.createEdge("J", "T", "11", 11);
        g.createEdge("S", "D", "13", 13);
        g.createEdge("S", "D", "12", 12);
        g.createEdge("A", "E", "27", 27);
        g.createEdge("C", "G", "15", 15);
        g.createEdge("D", "H", "15", 15);
        g.createEdge("F", "J", "12", 12);
        g.createEdge("G", "T", "7", 7);
        
        Path<String> p = g.findShortestPath("S", "T");
        
        while (p.isValid()) {
        	
        	System.out.println("Labels: ");
        	for (Edge<String> e : p) {
        		System.out.println(e.getLabel());
        	}
        	
        	System.out.println(p.getVertexNames() + " (" + p.getWeight() + ")");
        	p = g.findNextShortestPath();
        }
        
	}

}
