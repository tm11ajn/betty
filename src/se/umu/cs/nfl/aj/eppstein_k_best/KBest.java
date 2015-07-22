package se.umu.cs.nfl.aj.eppstein_k_best;

import se.umu.cs.nfl.aj.eppstein_k_best.graph.Graph;
import se.umu.cs.nfl.aj.eppstein_k_best.graph.Path;

public class KBest {

	public static void main(String[] args) {
		Graph g = new Graph();
		g.createVertices("S,A,B,C,D,E,F,G,H,I,J,K,T");
		
		g.createEdges("S", 	 "A",   2,  "alpha");
		g.createEdges("A,E", "B,I", 20, "alpha");
        g.createEdges("B,B", "C,F", 14, "alpha");
        g.createEdges("D",   "E",   9,  "alpha");
        g.createEdges("E",   "F",   10, "alpha");
        g.createEdges("F",   "G",   25, "alpha");
        g.createEdges("H",   "I",   18, "alpha");
        g.createEdges("I",   "J",   8,  "alpha");
        g.createEdges("J",   "T",   11, "alpha");
        g.createEdges("S",   "D",   13, "alpha");
        g.createEdges("S",   "D",   12, "alpha");
        g.createEdges("A",   "E",   27, "alpha");
        g.createEdges("C,D", "G,H", 15, "alpha");
        g.createEdges("F",   "J",   12, "alpha");
        g.createEdges("G",   "T",   7,  "alpha");
        
        Path p = g.findShortestPath("S", "T");
        
        while (p.isValid()) {
        	System.out.println(p.getVertexNames() + " (" + p.getWeight() + ")");
        	p = g.findNextShortestPath();
        }
        
	}

}
