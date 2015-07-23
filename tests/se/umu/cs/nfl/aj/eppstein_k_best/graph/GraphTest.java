package se.umu.cs.nfl.aj.eppstein_k_best.graph;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GraphTest {
	
	private Graph<String> g = new Graph<>();

	@Before
	public void setUp() throws Exception {
		buildPaperExample();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	private void buildPaperExample() {
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
        g.createEdge("A", "E", "27", 27);
        g.createEdge("C", "G", "15", 15);
        g.createEdge("D", "H", "15", 15);
        g.createEdge("F", "J", "12", 12);
        g.createEdge("G", "T", "7", 7);
	}
	
	@Test
	public void shouldGetShortestPathForPaperExample() throws Exception {
		
        Path<String> p = g.findShortestPath("S", "T");
        
        if (!p.isValid()) {
        	fail("Path is not valid.");
        }
        
        assertEquals("SDEFJT55.0", p.getVertexNames() + p.getWeight());
	}
	
	@Test
	public void shouldGetShortestPathForPaperExampleWithAdditionalEdge() 
			throws Exception {
		
		g.createEdge("S", "D", "12", 12);
        Path<String> p = g.findShortestPath("S", "T");
        
        if (!p.isValid()) {
        	fail("Path is not valid.");
        }
        
//        while (p.isValid()) {
//        	
//        	System.out.println("Labels: ");
//        	for (Edge<String> e : p) {
//        		System.out.println(e.getLabel());
//        	}
//        	
//        	System.out.println(p.getVertexNames() + " (" + p.getWeight() + ")");
//        	p = g.findNextShortestPath();
//        }
        
        assertEquals("SDEFJT54.0", p.getVertexNames() + p.getWeight());
	}

}
