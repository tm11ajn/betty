package se.umu.cs.flp.aj.eppstein_k_best.runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.PriorityQueue;

import se.umu.cs.flp.aj.nbest.data.NestedMap;
import se.umu.cs.flp.aj.nbest.data.Node;
import se.umu.cs.flp.aj.nbest.data.Run;
import se.umu.cs.flp.aj.nbest.data.TreeKeeper;
import se.umu.cs.flp.aj.wta.Rule;
import se.umu.cs.flp.aj.wta.State;
import se.umu.cs.flp.aj.wta.Symbol;
import se.umu.cs.flp.aj.wta.WTA;
import se.umu.cs.flp.aj.wta.Weight;
import ude.graph.Edge;
import ude.graph.Graph;
import ude.graph.ksp.Eppstein;
import ude.graph.util.Path;

public class EppsteinRunner3 {
	private ArrayList<TreeKeeper<Symbol>> exploredTrees;
	private HashMap<String,Node<Symbol>> treeMapping = new HashMap<>();

	public EppsteinRunner3(ArrayList<TreeKeeper<Symbol>> exploredTrees) {		
		this.exploredTrees = exploredTrees;
	}
	
	public ArrayList<LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>>> runEppstein(WTA wta,
			int k, TreeKeeper<Symbol> tree, State q) {
		
		ArrayList<LinkedHashMap<Node<Symbol>,TreeKeeper<Symbol>>> kBestTreesForEachQRule =
				new ArrayList<>();

		Graph graph = new Graph();
		Eppstein epp = new Eppstein();

		ArrayList<Rule> rules = wta.getTransitionFunction().
				getRulesByResultingState(q);
		
		int c = 0;

		for (Rule r : rules) {
			
System.out.println("bf rule " + c);
			
			ArrayList<State> states = r.getStates();
			int nOfStates = states.size();

			addVertices(graph, nOfStates);

//			NestedMap<String, String, PriorityQueue<Run>> edgeMap =
//					buildEdgeMap(states, tree);
			
			NestedMap<String, String, PriorityQueue<Run>> edgeMap = buildEdges(states, tree);
			
			addKSmallestEdgesToGraph(graph, k, edgeMap, tree);
			
System.out.println("Graph before ksping: " + graph);

//			Path<Node<Symbol>> path =
//					graph.findShortestPath("u0", "v" + nOfStates);
			List<Path> pathList = epp.ksp(graph, "u0", "v" + nOfStates, k);
			
			
			LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>> treeList = new LinkedHashMap<>();
			
			for (Path path : pathList) {
				Node<Symbol> node = extractTreeFromPath(path, r);
				TreeKeeper<Symbol> keeper = new TreeKeeper<>(node);
				Weight w = new Weight(path.getTotalCost());
				w = w.add(r.getWeight());
				keeper.addWeight(q, w);
				treeList.put(node, keeper);
			}
		
//			LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>> treeList =
//					getKBestTreesForRule(graph, path, k, q, r);
			
			kBestTreesForEachQRule.add(treeList);
			
			c++;
		}

		return kBestTreesForEachQRule;
	}

	private void addVertices(Graph graph, int nOfStates) {
//		String vertices = "";

		for (int i = 0; i < nOfStates + 1; i++) {
			
			graph.addNode("u" + i);
			graph.addNode("v" + i);
			
//			vertices += "u" + i + "," + "v" + i;
		}
	}

	private NestedMap<String, String, PriorityQueue<Run>> buildEdges(
			ArrayList<State> states, TreeKeeper<Symbol> tree) {

		NestedMap<String, String, PriorityQueue<Run>> edgeMap =
				new NestedMap<>();
		int nOfStates = states.size();

		for (int i = 1; i < nOfStates + 1; i++) {
			State currentState = states.get(i-1);

			for (TreeKeeper<Symbol> n : exploredTrees) {
				Weight w = n.getWeight(currentState);

				if (w == null) {
					continue;
				}

				PriorityQueue<Run> pu = null;
				PriorityQueue<Run> pv = edgeMap.get("v" + (i - 1), "v" + i);
				String resultingNodeType = "";

				if (!n.getTree().equals(tree.getTree())) {
					resultingNodeType = "u";
				} else {
					resultingNodeType = "v";
				}

				pu = edgeMap.get("u" + (i - 1), resultingNodeType + i);
	
				if (pu == null) {
					pu = new PriorityQueue<>(nOfStates);
					edgeMap.put("u" + (i - 1), resultingNodeType + i, pu);
System.out.println("Adding u to x edge");
				}

				if (pv == null) {
					pv = new PriorityQueue<>(nOfStates);
					edgeMap.put("v" + (i - 1), "v" + i, pv);
System.out.println("Adding v to v edge");
				}

				pu.add(new Run(n, w));
				pv.add(new Run(n, w));
			}
		}

		return edgeMap;
	}

	private void addKSmallestEdgesToGraph(Graph graph,
			int k, NestedMap<String, String, PriorityQueue<Run>> edgeMap, 
			TreeKeeper<Symbol> tree) {

		for (String vertex1 : edgeMap.keySet()) {

			for (String vertex2 : edgeMap.get(vertex1).keySet()) {

				PriorityQueue<Run> p = edgeMap.get(vertex1, vertex2);
				int counter = 0;

				while (!p.isEmpty() && counter < k) {
System.out.println("Adding");
					Run run = p.poll();
//					graph.createEdge(vertex1, vertex2, run.getTree().getTree(),
//							Double.parseDouble(run.getWeight().toString()));
					graph.addEdge(vertex1, vertex2, Double.valueOf(run.getWeight().toString()));
					treeMapping.put(vertex1 + "," + vertex2, tree.getTree());
					counter++;
				}
			}
		}
	}

//	private LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>> getKBestTreesForRule(
//			Graph graph, Path<Edge> path,
//			int k, State q, Rule r) {
//		
////System.out.println(">>> Getting trees for rule " + r);
//		
//		LinkedHashMap<Node<Symbol>,TreeKeeper<Symbol>> treeList = new LinkedHashMap<>();
//
//		int counter = 0;
//
//		while (path.isValid() && counter < k) {
//			
//			Node<Symbol> pathTree = extractTreeFromPath(path, r);
//			Weight pathWeight = new Weight(path.getWeight());
//
////System.out.println(">>> Tree output: " + pathTree + " with weight " + pathWeight);
//			
//			if (path.getWeight() == Double.MAX_VALUE) {
//				pathWeight = new Weight(Weight.INF);
//			}
//
//			pathWeight = pathWeight.add(r.getWeight());
//			
//			TreeKeeper<Symbol> keeper = new TreeKeeper<>(pathTree); // Check if tree already in list and merge trees if that is the case?
//			keeper.addWeight(q, pathWeight);
//			
////			if (treeList.containsKey(pathTree)) {
////				System.out.println("HÄR ÄR JAG IGEN");
////			}
//
//			treeList.put(pathTree, keeper);
//			counter++;
//			
//			path = graph.findNextShortestPath();
//		}
//
//		return treeList;
//	}

	private Node<Symbol> extractTreeFromPath(Path path,
			Rule r) {

//		Node<Symbol> root = new Node<>(r.getSymbol());
//
//		for (Edge<Node<Symbol>> e : path) {
//			root.addChild(e.getLabel());
//		}
//
//		return root;
		
		Node<Symbol> root = new Node<>(r.getSymbol());

		for (Edge e : path.getEdges()) {
			String s = e.getFromNode() + "," + e.getToNode();
			root.addChild(treeMapping.get(s));
		}

		return root;
	}
}
