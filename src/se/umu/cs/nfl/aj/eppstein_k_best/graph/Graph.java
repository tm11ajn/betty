package se.umu.cs.nfl.aj.eppstein_k_best.graph;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class Graph<T> {
	
	private ArrayList<Vertex<T>> vertices;
	private PriorityQueue<ST_Node<T>> pathsHeap;
	private boolean ready;
	private Vertex<T> sourceVertex;
	private Vertex<T> endVertex;
	
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
				this.vertices.add(new Vertex<T>(label));
			}
		}
		
		return true;
	}
	
	public boolean createEdge(String tail, String head, T label, double weight/*, String group*/) {
		
		Vertex<T> tailsVertex = getVertex(tail.toUpperCase());
		Vertex<T> headsVertex = getVertex(head.toUpperCase());
		
		Edge<T> e = new Edge<T>(tailsVertex, headsVertex, label, weight/*, group.toUpperCase()*/);
		tailsVertex.getRelatedEdges().add(e);

		if (!tailsVertex.equals(headsVertex)) {
			headsVertex.getRelatedEdges().add(e);
		}
		
		this.ready = false;
		
		return true;
	}
	
	public boolean createEdge(String tail, String head, double weight/*, String group*/) {
		
		Vertex<T> tailsVertex = getVertex(tail.toUpperCase());
		Vertex<T> headsVertex = getVertex(head.toUpperCase());
		
		Edge<T> e = new Edge<T>(tailsVertex, headsVertex, weight/*, group.toUpperCase()*/);
		tailsVertex.getRelatedEdges().add(e);

		if (!tailsVertex.equals(headsVertex)) {
			headsVertex.getRelatedEdges().add(e);
		}
		
		this.ready = false;
		
		return true;
	}
	
	public boolean createEdges(String tails, String heads, double weight/*, String group*/) {
		ArrayList<Vertex<T>> tailsList = this.parseVertices(tails.toUpperCase());
		ArrayList<Vertex<T>> headsList = this.parseVertices(heads.toUpperCase());
//		String groupUpper = group.toUpperCase();
		
		if (tailsList == null || headsList == null) {
			return false;
		}
		
		if (tailsList.size() != headsList.size()) {
			return false;
		}
		
		int tailsListSize = tailsList.size();
		
		for (int i = 0; i < tailsListSize; i++) {
			Vertex<T> tailsVertex = tailsList.get(i);
			Vertex<T> headsVertex = headsList.get(i);
			
			Edge<T> e = new Edge<T>(tailsVertex, headsVertex, weight/*, groupUpper*/);
			tailsVertex.getRelatedEdges().add(e);
			
			if (!tailsVertex.equals(headsVertex)) {
				headsVertex.getRelatedEdges().add(e);
			}
		}
		
		this.ready = false;
		
		return true;
	}
	
//	public void edgeGroupWeights(String group, double weight) {
//		String groupUpper = group.toUpperCase();
//		
//		for (Vertex<T> v : this.vertices) {
//			
//			for (Edge<T> e : v.getRelatedEdges()) {
//				
//				if (e.getGroup().equals(groupUpper)) {
//					e.setWeight(weight);
//				}
//			}
//		}
//		
//		this.ready = false;
//	}
	
	public Path<T> findShortestPath(String s, String t) {
		this.ready = false;
		
		this.sourceVertex = this.getVertex(s.toUpperCase());
		this.endVertex = this.getVertex(t.toUpperCase());
		
		if (sourceVertex == null || endVertex == null) {
			return new Path<T>(); // Invalid path
		}
		
		buildShortestPathTree();
		buildSidetracksHeap();
		ready = true;
		
		return findNextShortestPath();
	}
	
	public Path<T> findNextShortestPath() {
		
		if (!this.ready) {
			return new Path<T>(); // Invalid path
		}
		
		ST_Node<T> node = this.pathsHeap.poll();
		
		if (node == null) {
			return new Path<T>(); // Invalid path
		}
		
		return rebuildPath(node.getSidetracks());
	}
	
	private ArrayList<Vertex<T>> parseVertices(String labels) {
		String[] vertexList = labels.toUpperCase().split(",");
		
		if (vertexList.length == 0) {
			return null;
		}
		
		ArrayList<Vertex<T>> result = new ArrayList<>();
		
		for (String label : vertexList) {
			Vertex<T> v = this.getVertex(label);
			
			if (v == null) {
				return null;
			}
			
			result.add(v);
		}
		
		
		return result;
	}
	
	private Vertex<T> getVertex(String label) {
		
		if (label == null || label == "") {
			return null;
		}
		
		for (Vertex<T> v : this.vertices) {
			
			if (v.equals(label)) {
				return v;
			}
		}
		
		return null;
	}
	
	private void resetGraphState() {
		
		for (Vertex<T> v : this.vertices) {
			v.setEdgeToPath(null);
			v.setDistance(Double.MIN_VALUE);
		}
	}
	
	private void buildShortestPathTree() {
		resetGraphState();
		
		Vertex<T> v = this.endVertex;
		v.setDistance(0);
		
		PriorityQueue<SP_Node<T>> fringe = new PriorityQueue<>(this.vertices.size());
		
		do {
			
			if (v != null) {
				
				for (Edge<T> e : v.getRelatedEdges()) {
					
					if (e.getHead().equals(v) && e.getWeight() >= 0) {
						fringe.add(new SP_Node<T>(e, 
								e.getWeight() + e.getHead().getDistance()));
					}
				}
			}
			
			SP_Node<T> node = fringe.poll();
			
			if (node == null) {
				break;
			}
			
			Edge<T> e = node.getEdge();
			v = e.getTail();
			
			if (v.getDistance() == Double.MIN_VALUE) {
				v.setDistance(e.getWeight() + e.getHead().getDistance());
				v.setEdgeToPath(e);
			} else {
				v = null;
			}
			
		} while (true);
		
	}
	
	private void buildSidetracksHeap() {
		this.pathsHeap = new PriorityQueue<>(this.vertices.size());
		Path<T> empty = new Path<>();
		this.pathsHeap.add(new ST_Node<T>(empty));
		addSidetracks(empty, this.sourceVertex);
	}
	
	private void addSidetracks(Path<T> path, Vertex<T> vertex) {
		
		for (Edge<T> e : vertex.getRelatedEdges()) {
			
			if (e.isSidetrackOf(vertex) && 
					(e.getHead().getEdgeToPath() != null || e.getHead().equals(this.endVertex))) {
				
				Path<T> p = new Path<>();
				p.addAll(path);
				p.add(e);
				this.pathsHeap.add(new ST_Node<T>(p));
				
				if (!e.getHead().equals(vertex)) {
					addSidetracks(p, e.getHead());
				}
			}
		}
		
		if (vertex.next() != null) {
			addSidetracks(path, vertex.next());
		}
	}
	
	private Path<T> rebuildPath(Path<T> sidetracks) {
		Path<T> path = new Path<>();
		Vertex<T> v = this.sourceVertex;
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
