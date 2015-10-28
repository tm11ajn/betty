package se.umu.cs.flp.aj.eppstein_k_best.runner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

import se.umu.cs.flp.aj.eppstein_k_best.graph.Edge;
import se.umu.cs.flp.aj.eppstein_k_best.graph.Graph;
import se.umu.cs.flp.aj.eppstein_k_best.graph.Path;
import se.umu.cs.flp.aj.nbest.data.NestedMap;
import se.umu.cs.flp.aj.nbest.data.Node;
import se.umu.cs.flp.aj.nbest.data.Run;
import se.umu.cs.flp.aj.wta.Rule;
import se.umu.cs.flp.aj.wta.State;
import se.umu.cs.flp.aj.wta.Symbol;
import se.umu.cs.flp.aj.wta.WTA;
import se.umu.cs.flp.aj.wta.Weight;



public class EppsteinRunner {
	
	private NestedMap<Node<Symbol>, State, Weight> treeStateValTable;
	private ArrayList<Node<Symbol>> exploredTrees;
	
	public EppsteinRunner(ArrayList<Node<Symbol>> exploredTrees, 
			NestedMap<Node<Symbol>, State, Weight> treeStateValTable) {
		this.exploredTrees = exploredTrees;
		this.treeStateValTable = treeStateValTable;
	}
	
	public ArrayList<LinkedList<Node<Symbol>>> runEppstein(WTA wta,
			int k, Node<Symbol> tree, State q) {

		ArrayList<LinkedList<Node<Symbol>>> kBestTreesForEachQRule =
				new ArrayList<>();

		Graph<Node<Symbol>> graph = new Graph<>();

		ArrayList<Rule> rules = wta.getTransitionFunction().
				getRulesByResultingState(q);

		for (Rule r : rules) {
			
			ArrayList<State> states = r.getStates();
			int nOfStates = states.size();
			
			String vertices = buildVertexString(nOfStates);
			graph.createVertices(vertices);

			NestedMap<String, String, PriorityQueue<Run>> edgeMap = 
					buildEdgeMap(states, tree);
			addKSmallestEdgesToGraph(graph, k, edgeMap);

			Path<Node<Symbol>> path =
					graph.findShortestPath("u0", "v" + nOfStates);

			LinkedList<Node<Symbol>> treeList = 
					getKBestTreesForRule(graph, path, k, q, r);

			kBestTreesForEachQRule.add(treeList);
		}

		return kBestTreesForEachQRule;
	}

	private String buildVertexString(int nOfStates) {
		String vertices = "";

		for (int i = 0; i < nOfStates + 1; i++) {
			vertices += "u" + i + "," + "v" + i;

			if (i != nOfStates) {
				vertices += ",";
			}
		}
		return vertices;
	}

	private NestedMap<String, String, PriorityQueue<Run>> buildEdgeMap(
			ArrayList<State> states, Node<Symbol> tree) {

		NestedMap<String, String, PriorityQueue<Run>> edgeMap =
				new NestedMap<>();
		int nOfStates = states.size();

		for (int i = 1; i < nOfStates + 1; i++) {
			State currentState = states.get(i-1);

			for (Node<Symbol> n : exploredTrees) {

				Weight w = treeStateValTable.get(n, currentState);

				if (w == null) {
					continue;
				}
				
				PriorityQueue<Run> pu = null;
				PriorityQueue<Run> pv = edgeMap.get("v" + (i - 1), "v" + i);
				String resultingNodeType = "";

				if (!n.equals(tree)) {
					resultingNodeType = "u";
				} else {
					resultingNodeType = "v";
				}
				
				pu = edgeMap.get("u" + (i - 1), resultingNodeType + i);

				if (pu == null) {
					pu = new PriorityQueue<>(nOfStates);
					edgeMap.put("u" + (i - 1), resultingNodeType + i, pu);
				}
				
				if (pv == null) {
					pv = new PriorityQueue<>(nOfStates);
					edgeMap.put("v" + (i - 1), "v" + i, pv);
				}
				
				pu.add(new Run(n, w));
				pv.add(new Run(n, w));
			}
		}
		
		return edgeMap;
	}
	
	private void addKSmallestEdgesToGraph(Graph<Node<Symbol>> graph, 
			int k, NestedMap<String, String, PriorityQueue<Run>> edgeMap) {
		
		for (String vertex1 : edgeMap.keySet()) {

			for (String vertex2 : edgeMap.get(vertex1).keySet()) {

				PriorityQueue<Run> p = edgeMap.get(vertex1, vertex2);
				int counter = 0;

				while (!p.isEmpty() && counter < k) {
					Run run = p.poll();
					graph.createEdge(vertex1, vertex2, run.getTree(),
							Double.parseDouble(run.getWeight().toString()));
					counter++;
				}
			}
		}
	}
	
	private LinkedList<Node<Symbol>> getKBestTreesForRule(
			Graph<Node<Symbol>> graph, Path<Node<Symbol>> path, 
			int k, State q, Rule r) {
		
		LinkedList<Node<Symbol>> treeList = new LinkedList<>();
		int counter = 0;
		
		while (path.isValid() && counter < k) {
			Node<Symbol> pathTree = extractTreeFromPath(path, r);
			Weight pathWeight = new Weight(path.getWeight());

			if (path.getWeight() == Double.MAX_VALUE) {
				pathWeight = new Weight(Weight.INF);
			}

			pathWeight = pathWeight.add(r.getWeight());
			Weight oldWeight = treeStateValTable.get(pathTree, q);

			if (oldWeight == null
					|| oldWeight.compareTo(pathWeight) == 1) {
				treeStateValTable.put(pathTree, q, pathWeight);
			}

			treeList.add(pathTree);
			counter++;

			path = graph.findNextShortestPath();
		}
		
		return treeList;
	}

	private Node<Symbol> extractTreeFromPath(Path<Node<Symbol>> path,
			Rule r) {

		Node<Symbol> root = new Node<>(r.getSymbol());

		for (Edge<Node<Symbol>> e : path) {
			root.addChild(e.getLabel());
		}

		return root;
	}
}
