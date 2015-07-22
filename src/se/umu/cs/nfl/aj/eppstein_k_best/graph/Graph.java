package se.umu.cs.nfl.aj.eppstein_k_best.graph;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class Graph {
	
	private ArrayList<Vertex> vertices;
	private PriorityQueue<ST_Node> pathsHeap;
	private boolean ready;
	private Vertex sourceVertex;
	private Vertex endVertex;
	
	public Graph() {
		this.vertices = new ArrayList<>();
		this.ready = false;
	}
	
	public boolean createVertices(String vertices) {
		this.ready = false;
		this.vertices = new ArrayList<>();
		
		String[] vertexList = vertices.toUpperCase().split(",");
		
		for (String label : vertexList) {
			
			if (label.length() == 0) {
				continue;
			}
			
			if (!label.matches("^[a-zA-Z][a-zA-Z0-9]*")) {
				return false;
			}
			
			if (this.getVertex(label) == null) {
				this.vertices.add(new Vertex(label));
			}
		}
		
		return true;
	}
	
	public boolean createEdges(String tails, String heads, int weight, String group) {
		ArrayList<Vertex> tailsList = this.parseVertices(tails.toUpperCase());
		ArrayList<Vertex> headsList = this.parseVertices(heads.toUpperCase());
		String groupUpper = group.toUpperCase();
		
		if (tailsList == null || headsList == null) {
			return false;
		}
		
		if (tailsList.size() != headsList.size()) {
			return false;
		}
		
		int tailsListSize = tailsList.size();
		
		for (int i = 0; i < tailsListSize; i++) {
			Vertex tailsVertex = tailsList.get(i);
			Vertex headsVertex = headsList.get(i);
			
			Edge e = new Edge(tailsVertex, headsVertex, weight, groupUpper);
			tailsVertex.getRelatedEdges().add(e);
			
			if (!tailsVertex.equals(headsVertex)) {
				headsVertex.getRelatedEdges().add(e);
			}
		}
		
		this.ready = false;
		
		return true;
	}
	
	public void edgeGroupWeights(String group, int weight) {
		String groupUpper = group.toUpperCase();
		
		for (Vertex v : this.vertices) {
			
			for (Edge e : v.getRelatedEdges()) {
				
				if (e.getGroup().equals(groupUpper)) {
					e.setWeight(weight);
				}
			}
		}
		
		this.ready = false;
	}
	
	public Path findShortestPath(String s, String t) {
		this.ready = false;
		
		this.sourceVertex = this.getVertex(s.toUpperCase());
		this.endVertex = this.getVertex(t.toUpperCase());
		
		if (sourceVertex == null || endVertex == null) {
			return new Path(); // Invalid path
		}
		
		buildShortestPathTree();
		buildSidetracksHeap();
		ready = true;
		
		return findNextShortestPath();
	}
	
	public Path findNextShortestPath() {
		
		if (!this.ready) {
			return new Path(); // Invalid path
		}
		
		ST_Node node = this.pathsHeap.poll();
		
		if (node == null) {
			return new Path(); // Invalid path
		}
		
		return rebuildPath(node.getSidetracks());
	}
	
	private ArrayList<Vertex> parseVertices(String labels) {
		String[] vertexList = labels.toUpperCase().split(",");
		
		if (vertexList.length == 0) {
			return null;
		}
		
		ArrayList<Vertex> result = new ArrayList<>();
		
		for (String label : vertexList) {
			Vertex v = this.getVertex(label);
			
			if (v == null) {
				return null;
			}
			
			result.add(v);
		}
		
		
		return result;
	}
	
	private Vertex getVertex(String label) {
		
		if (label == null || label == "") {
			return null;
		}
		
		for (Vertex v : this.vertices) {
			
			if (v.equals(label)) {
				return v;
			}
		}
		
		return null;
	}
	
	private void resetGraphState() {
		
		for (Vertex v : this.vertices) {
			v.setEdgeToPath(null);
			v.setDistance(Integer.MIN_VALUE);
		}
	}
	
	private void buildShortestPathTree() {
		resetGraphState();
		
		Vertex v = this.endVertex;
		v.setDistance(0);
		
		PriorityQueue<SP_Node> fringe = new PriorityQueue<>(this.vertices.size());
		
		do {
			
			if (v != null) {
				
				for (Edge e : v.getRelatedEdges()) {
					
					if (e.getHead().equals(v) && e.getWeight() >= 0) {
						fringe.add(new SP_Node(e, 
								e.getWeight() + e.getHead().getDistance()));
					}
				}
			}
			
			SP_Node node = fringe.poll();
			
			if (node == null) {
				break;
			}
			
			Edge e = node.getEdge();
			v = e.getTail();
			
			if (v.getDistance() == Integer.MIN_VALUE) {
				v.setDistance(e.getWeight() + e.getHead().getDistance());
				v.setEdgeToPath(e);
			} else {
				v = null;
			}
			
		} while (true);
		
	}
	
	private void buildSidetracksHeap() {
		this.pathsHeap = new PriorityQueue<>(this.vertices.size());
		Path empty = new Path();
		this.pathsHeap.add(new ST_Node(empty));
		addSidetracks(empty, this.sourceVertex);
	}
	
	private void addSidetracks(Path path, Vertex vertex) {
		
		for (Edge e : vertex.getRelatedEdges()) {
			
			if (e.isSidetrackOf(vertex) && 
					(e.getHead().getEdgeToPath() != null || e.getHead().equals(this.endVertex))) {
				
				Path p = new Path();
				p.addAll(path);
				p.add(e);
				this.pathsHeap.add(new ST_Node(p));
				
				if (!e.getHead().equals(vertex)) {
					addSidetracks(p, e.getHead());
				}
			}
		}
		
		if (vertex.next() != null) {
			addSidetracks(path, vertex.next());
		}
	}
	
	private Path rebuildPath(Path sidetracks) {
		Path path = new Path();
		Vertex v = this.sourceVertex;
		int i = 0;
		
		while (v != null) {
			
			if (i < sidetracks.size() && sidetracks.get(i).getTail().equals(v)) {
				path.add(sidetracks.get(i));
				v = sidetracks.get(i++).getHead();
			} else {
				
				if (v.getEdgeToPath() == null) {
					break;
				}
				
				path.add(v.getEdgeToPath());
				v = v.next();
			}
		}
		
		return path;
	}

}
