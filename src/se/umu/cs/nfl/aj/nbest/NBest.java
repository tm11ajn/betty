package se.umu.cs.nfl.aj.nbest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import se.umu.cs.nfl.aj.eppstein_k_best.graph.Edge;
import se.umu.cs.nfl.aj.eppstein_k_best.graph.Graph;
import se.umu.cs.nfl.aj.eppstein_k_best.graph.Path;
import se.umu.cs.nfl.aj.nbest.data.NestedMap;
import se.umu.cs.nfl.aj.nbest.data.Node;
import se.umu.cs.nfl.aj.nbest.data.Run;
import se.umu.cs.nfl.aj.wta.Rule;
import se.umu.cs.nfl.aj.wta.State;
import se.umu.cs.nfl.aj.wta.Symbol;
import se.umu.cs.nfl.aj.wta.WTA;
import se.umu.cs.nfl.aj.wta.Weight;
import se.umu.cs.nfl.aj.wta_handlers.WTABuilder;
import se.umu.cs.nfl.aj.wta_handlers.WTAParser;

public class NBest {

	private static NestedMap<Node<Symbol>, State, Weight> treeStateValTable = 
			new NestedMap<>(); // C
	private static ArrayList<Node<Symbol>> exploredTrees = 
			new ArrayList<Node<Symbol>>(); // T
	private static LinkedList<Node<Symbol>> treeQueue = new LinkedList<>(); // K
	
	private static HashMap<State, Weight> smallestCompletionWeights;
	private static HashMap<Node<Symbol>, ArrayList<State>> optimalStates = new HashMap<>();
	private static HashMap<State, Integer> optimalStatesUsage = new HashMap<>();

	public static void main(String[] args) {

		String fileName = getFileName(args);
		int N = getN(args);
		WTAParser wtaParser = new WTAParser();
		WTA wta = wtaParser.parse(fileName);

		List<String> result = null;
		
		System.out.println("START");
		result = run(wta, N);		

		for (String treeString : result) {
			System.out.println(treeString);
		}
	}

	public static String getFileName(String[] args) {

		if (args.length != 2) {
			printUsageError();
		}

		return args[0];
	}
	
	private static int getN(String[] args) {
		
		int N = 0;
		
		try {
			N = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			printUsageError();
		}
		
		return N;
	}
	
	private static void printUsageError() {
		System.err.println("Usage: java NBest <RTG file> <N> "
				+ "(where N is an nonnegative integer)");
		System.exit(-1);
	}
	
	public static void init(WTA wta) {
		WTABuilder b = new WTABuilder();
		smallestCompletionWeights = b.findSmallestCompletionWeights(wta);
		treeStateValTable = new NestedMap<>();
	}
	
	public static List<String> run(WTA wta, int N) {
		
		/* For result. */
		List<String> nBest = new ArrayList<String>();
		
		init(wta);
		
		// T <- empty
		exploredTrees = new ArrayList<Node<Symbol>>(); 
		
		// K <- empty
		treeQueue = new LinkedList<Node<Symbol>>(); 
		
		// enqueue(K, Sigma_0) TODO
		
		ArrayList<Symbol> symbols = wta.getSymbols();
		
		for (Symbol s : symbols) {
			
			if (s.getRank() == 0) {	
				Node<Symbol> tree = new Node<Symbol>(s);
				
				//State optimalState = getOptimalStates(wta, tree).get(0);
				optimalStates.put(tree, getOptimalStates(wta, tree));
				
				insertTreeIntoQueueByTotalMinimumWeight(tree);
			}
		}
		
		// i <- 0
		int counter = 0;
		
//		System.out.println("After initialization: ");
//		System.out.println("T:");
//		
//		for (Node<Symbol> n : exploredTrees) {
//			System.out.println(n);
//		}
//		
//		System.out.println("K:");
//		
//		for (Node<Symbol> n : treeQueue) {
//			System.out.println(n);
//		}
		
		// while i < N and K nonempty do
		while (counter < N && !treeQueue.isEmpty()) {
			
			// t <- dequeue(K)
			Node<Symbol> currentTree = treeQueue.poll();
			
//			System.out.println("Dequeue " + currentTree);
			
			// Get optimal state for current tree
//			State optimalState = getOptimalState(wta, currentTree, 
//					smallestCompletionWeights);
//			optimalStates.put(currentTree, optimalState);
			State optimalState = optimalStates.get(currentTree).get(0);
			
//			System.out.println("Optimal state is " + optimalState);
			
			// T <- T u {t}
			exploredTrees.add(currentTree);
			
//			System.out.println("Before checking/expansion");
//			
//			System.out.println("T:");
//			
//			for (Node<Symbol> n : exploredTrees) {
//				System.out.println(n);
//			}
//			
//			System.out.println("K:");
//			
//			for (Node<Symbol> n : treeQueue) {
//				System.out.println(n);
//			}
			
			// if M(t) = delta(t) then TODO
			// is this the same thing as smallestcompletion(optimalstate) = 0 
			// and optimalstate in final states ?
			if (optimalState.isFinal()) {
				
				// output(t)
				nBest.add(currentTree.toString());
//				System.out.println("Adds " + currentTree + " to output");
				
				// i <- i + 1
				counter++;
			}
			
			// enqueue(K, expand(T, t)) TODO
			
			ArrayList<Node<Symbol>> expansion = expandWith(wta, currentTree);
			
			for (Node<Symbol> t : expansion) {
				// Need to get the optimal state for each new tree before putting it into the queue?
				// In that case, this does not need to be done earlier in the while loop.
				optimalStates.put(t, getOptimalStates(wta, t));
				insertTreeIntoQueueByTotalMinimumWeight(t);
			}
			
		}

		return nBest;
	}
	
	public static List<String> run2(WTA wta, int N) {
		
		/* For result. */
		List<String> nBest = new ArrayList<String>();
		
		init(wta);
		
		// T <- empty. K <- empty
		exploredTrees = new ArrayList<Node<Symbol>>(); 
		treeQueue = new LinkedList<Node<Symbol>>(); 
		
		// enqueue(K, Sigma_0) TODO
		enqueueRankZeroSymbols(wta, N);
		
		// i <- 0
		int counter = 0;
		
		// while i < N and K nonempty do
		while (counter < N && !treeQueue.isEmpty()) {
			
			// t <- dequeue(K)
			Node<Symbol> currentTree = treeQueue.poll();
						
			// T <- T u {t}
			exploredTrees.add(currentTree);
			
			// Get optimal state for current tree
			State optimalState = optimalStates.get(currentTree).get(0);
			
			// if M(t) = delta(t) then TODO
			// is this the same thing as smallestcompletion(optimalstate) = 0 
			// and optimalstate in final states ?
			if (optimalState.isFinal()) {
				
				// output(t)
				nBest.add(currentTree.toString());
				
				// i <- i + 1
				counter++;
			}
			
			// prune(T, enqueue(K, expand(T, t))) TODO
			
			enqueueWithExpansionAndPruning(wta, currentTree);
			
		}

		return nBest;
	}
	
	private static void enqueueRankZeroSymbols(WTA wta, int N) {
		
		ArrayList<Symbol> symbols = wta.getSymbols();
		LinkedList<Node<Symbol>> tempQueue = new LinkedList<>();
		
		for (Symbol s : symbols) {
			
			if (s.getRank() == 0) {	
				Node<Symbol> tree = new Node<Symbol>(s);
				
				ArrayList<Rule> rules = wta.getRulesBySymbol(s);
				
				for (Rule r : rules) {
					State resState = r.getResultingState();
					Weight weight = r.getWeight();
					Weight oldWeight = treeStateValTable.get(tree, resState);
					
					if (oldWeight == null || 
							weight.compareTo(oldWeight) == -1) {
						treeStateValTable.put(tree, resState, weight);
					}
				}
				
				HashMap<State, Weight> stateValTable = 
						treeStateValTable.getAll(tree);
				ArrayList<State> optStates = new ArrayList<>();
				
				for (Entry<State, Weight> e : stateValTable.entrySet()) {
					
					Weight minWeight = new Weight(Weight.INF);
					Weight currentWeight = e.getValue();
					
					int comparison = currentWeight.compareTo(minWeight);
					
					if (comparison == -1) {
						minWeight = currentWeight;
						optStates = new ArrayList<>();
						optStates.add(e.getKey());
					} else if (comparison == 0) {
						optStates.add(e.getKey());
					}
					
				}
				
				optimalStates.put(tree, optStates);
				
				State treeOptState = optimalStates.get(tree).get(0);
				Weight treeDeltaWeight = smallestCompletionWeights.
						get(treeOptState).add(treeStateValTable.
								get(tree, treeOptState));
				
				int tempQueueSize = tempQueue.size();
				int insertIndex = 0;
				
				for (int i = 0; i < tempQueueSize; i++) {
					
					Node<Symbol> currentTree = tempQueue.get(i);
					State currentTreeOptState = optimalStates.get(currentTree).
							get(0);
					Weight currentTreeDeltaWeight = smallestCompletionWeights.
							get(currentTreeOptState).add(treeStateValTable.
									get(currentTree, currentTreeOptState));
					
					int comparison = currentTreeDeltaWeight.compareTo(
							treeDeltaWeight);
					
					if (comparison == 1) {
						insertIndex = i;
						break;
					} else if (comparison == 0) {
						
						if (currentTree.compareTo(tree) == 1) {
							insertIndex = i;
							break;
						}
					}
				}
				
				tempQueue.add(insertIndex, tree);
				
				for (Node<Symbol> n : tempQueue) {
					boolean shallPrune = true;
					
					for (State optState : optimalStates.get(n)) {
						int usage = optimalStatesUsage.get(optState);
						
						if (usage >= N) {
							shallPrune = false;
						}
					}
					
					if (!shallPrune) {
						
						for (State optState : optimalStates.get(n)) {
							int usage = optimalStatesUsage.get(optState);
							usage++;
							optimalStatesUsage.put(optState, usage);
						}
						
						insertTreeIntoQueueByTotalMinimumWeight(n);
					}
					
				}
				
			}
		}
	}
	
	private static void enqueueWithExpansionAndPruning(WTA wta, Node<Symbol> tree) {
		
	}
	
	// TODO
	// Eventually divide into two methods, one that calculates the M^q's 
	// and one that gets the optimal state using the M^q's.
	public static ArrayList<State> getOptimalStates(WTA wta, Node<Symbol> tree) {
		
		ArrayList<State> optStatesList = new ArrayList<>();
		State optimalState = null;
		Weight minWeight = new Weight(Weight.INF);
		
		ArrayList<Rule> rules = wta.getRulesBySymbol(tree.getLabel());
		
		int nOfSubtrees = tree.getChildCount();
		
		for (Rule r : rules) {
			
//			System.out.println("Rule: " + r);
			
			ArrayList<State> states = r.getStates();
			
			boolean canUseRule = true;
			Weight weightSum = new Weight(0);
			
			for (int i = 0; i < nOfSubtrees; i++) {
				Node<Symbol> subTree = tree.getChildAt(i);
				State s = states.get(i);
				Weight wTemp = treeStateValTable.get(subTree, s);
				
				if (wTemp == null) {
					canUseRule = false; // TODO throw exception instead?
				} else {
					weightSum = weightSum.add(wTemp);
				}
			}
			
			weightSum = weightSum.add(r.getWeight());
			
//			System.out.println("Current weight=" + r.getWeight());
//			System.out.println("Weightsum=" + weightSum);
			
			if (canUseRule) {
//				System.out.println("In canUseRule-if");
				
				Weight currentWeight = treeStateValTable.get(tree, 
						r.getResultingState());
				
				System.out.println(currentWeight);
				
//				System.out.println("Current weight: " + currentWeight);
				
				// Find smallest weight for each state
				if (currentWeight == null || 
						(currentWeight.compareTo(weightSum) == 1)) {
					treeStateValTable.put(tree, r.getResultingState(), 
							weightSum);
//					System.out.println("Putting " + tree + ", " + r.getResultingState() + ", " + weightSum);
				}
				
				// M((c_q)[t]) TODO use deltaWeight-function instead
				Weight totalWeight = weightSum.add(
						smallestCompletionWeights.get(r.getResultingState()));
				
				// Save optimal state
				if (totalWeight.compareTo(minWeight) == -1) {
					optimalState = r.getResultingState();
					minWeight = totalWeight;
					
					optStatesList = new ArrayList<>();
					optStatesList.add(optimalState);
					
//					System.out.println("New optimal state: " + optimalState);
//					System.out.println("New min weight: " + minWeight);
				} else if (totalWeight.compareTo(minWeight) == 0) {
					optimalState = r.getResultingState();
					optStatesList.add(optimalState);
				}
			}
		}
		
		return optStatesList;
	}
	
	public static void insertTreeIntoQueueByTotalMinimumWeight(
			Node<Symbol> tree) {
		
		Weight wMinCurrent = getDeltaWeight(tree);
		
		int queueSize = treeQueue.size();
		int queueIndex = 0;
		
		for (int i = 0; i < queueSize; i++) {
			Node<Symbol> t = treeQueue.get(i);
			
			Weight wMinTemp = getDeltaWeight(t);
			
			if (wMinTemp.compareTo(wMinCurrent) == -1) {
				queueIndex = i + 1;
			} else if (wMinTemp.compareTo(wMinCurrent) == 0) {
				
				if (t.toString().compareTo(tree.toString()) < 1) {
					queueIndex = i + 1;
				}
			}
		}
				
		treeQueue.add(queueIndex, tree);
	}
	
	public static Weight getDeltaWeight(Node<Symbol> tree) {
		
		Weight delta = new Weight(0);
		
		State optimalState = optimalStates.get(tree).get(0);
		
		Weight minWeight = treeStateValTable.get(tree, optimalState);
		Weight smallestCompletionWeight = smallestCompletionWeights.get(optimalState);
		
		delta = minWeight.add(smallestCompletionWeight);
		
		return delta;
	}
	
	public static ArrayList<Node<Symbol>> expandWith(WTA wta, Node<Symbol> tree) {
		ArrayList<Node<Symbol>> expansion = new ArrayList<Node<Symbol>>();
		ArrayList<Rule> rules = wta.getRules();
		
//		System.out.println("EXPLORED TREES:");
//		
//		for (Node<Symbol> t : exploredTrees) {
//			System.out.println(t);
//		}
		
//		System.out.println("ENTERING EXPANSION");
		
		for (Rule r : rules) {
			ArrayList<State> states = r.getStates();
			HashMap<State, ArrayList<Node<Symbol>>> producibleTrees = 
					new HashMap<>();
					
//			System.out.println("Current rule: " + r);
					
			boolean canUseAllStates = true;
			boolean usesT = false;
			
			for (State s : states) {
				
//				System.out.println("Current state:" + s);
				
//				System.out.println("Explored trees are: ");
//				for (Node<Symbol> n : exploredTrees) {
//					System.out.println(n);
//				}
				
				for (Node<Symbol> n : exploredTrees) {
					
					//System.out.println("Current explored tree: " + n);
					
					if (treeStateValTable.get(n, s) != null) {
						
						if (!producibleTrees.containsKey(s)) {
							producibleTrees.put(s, 
									new ArrayList<Node<Symbol>>());
						}
						
						ArrayList<Node<Symbol>> treesForState = 
								producibleTrees.get(s);
						
						if (!treesForState.contains(n)) {
							treesForState.add(n);
//							System.out.println("Sets " + s + ":" + n);
						
							if (n.equals(tree)) {
								usesT = true;
							}
						}
					}
				}
				
				if (!producibleTrees.containsKey(s)) {
					canUseAllStates = false;
					break; // TODO while instead?
				}
			}
			
			
//			System.out.println("PRODUCIBLE TREES:");
//			
//			for (Entry<State, ArrayList<Node<Symbol>>> e : producibleTrees.entrySet()) {
//				System.out.println(e.getKey());
//				
//				for (Node<Symbol> v : e.getValue()) {
//					System.out.println(v);
//				}
//			}
			
//			System.out.println("canUseAllStates=" + canUseAllStates);
//			System.out.println("usesT=" + usesT);
			
			if (canUseAllStates && usesT) {
				
				int nOfStates = states.size();

				int[] indices = new int[nOfStates];
				int[] maxIndices = new int[nOfStates];

				int combinations = 1;

				for (int i = 0; i < nOfStates; i++) {
					int nOfTreesForState = 
							producibleTrees.get(states.get(i)).size();
					indices[i] = 0;
					maxIndices[i] = nOfTreesForState - 1;
					combinations *= nOfTreesForState;
				}
				
//				System.out.println("combinations=" + combinations);

				for (int i = 0; i < combinations; i++ ) {
					
					Node<Symbol> expTree = new Node<Symbol>(r.getSymbol());
					boolean hasT = false;

					for (int j = 0; j < nOfStates; j++) {
						State currentState = states.get(j);
						ArrayList<Node<Symbol>> trees = 
								producibleTrees.get(currentState);
						Node<Symbol> currentTree = trees.get(indices[j]);
						expTree.addChild(currentTree);
						
						if (currentTree.equals(tree)) {
							hasT = true;
						}
					}
					
					if (hasT && !expansion.contains(expTree)) {
						expansion.add(expTree);
//						System.out.println("Adds " + expTree + " to expansion");
					}

					boolean increased = false;
					int index = 0;

					while (!increased && index < nOfStates) {
						
//						System.out.println("stuck?");

						if (indices[index] == maxIndices[index]) {
							indices[index] = 0;
						} else {
							indices[index]++;
							increased = true;
						}
						
						index++;
					}
				}
			}
		}
		
//		System.out.println("RETURNING EXPANSION");
		
//		for (Node<Symbol> t : expansion) {
//			System.out.println(t);
//		}
		
		return expansion;
	}
	
	public static ArrayList<Node<Symbol>> expandUsingPruning(WTA wta, int N, 
			Node<Symbol> tree) {
		
		ArrayList<Node<Symbol>> expansion = new ArrayList<>();
		
		useEppstein(wta, N, tree);
		
		return expansion;
	}
	
	public static void useEppstein(WTA wta, int N, Node<Symbol> tree) {
		
		Graph<Node<Symbol>> graph = new Graph<>();
		
		HashMap<State, LinkedList<Run<Symbol>>> runs = new HashMap<>();
		
		for (State q : wta.getStates()) {
			ArrayList<Rule> rules = wta.getRulesByResultingState(q);
			LinkedList<Run<Symbol>> runList = new LinkedList<>();
			Weight qSmallestCompletionWeight = smallestCompletionWeights.get(q);
			
			for (Rule r : rules) {
				ArrayList<State> states = r.getStates();
				int nOfStates = states.size();
				
				String vertices = "";
				
				for (int i = 0; i < nOfStates + 1; i++) {
					vertices += "u" + i + "," + "v" + i;
					
					if (i != nOfStates) {
						vertices += ",";
					}
				}
				
				graph.createVertices(vertices);
				
				for (int i = 1; i < nOfStates; i++) {
					State currentState = states.get(i-1);
					
					for (Node<Symbol> n : exploredTrees) { // TODO include only N edges for each pair of nodes
						
						double weight = Double.parseDouble(
								treeStateValTable.get(n, currentState).
								toString());
						
						boolean isT = n.equals(tree);
						
						if (!isT) {
							graph.createEdge("u" + (i - 1), "u" + i, n, weight);
							graph.createEdge("v" + (i - 1), "v" + i, n, weight);
						}
						
						if (isT) {
							graph.createEdge("u" + (i - 1), "v" + i, n, weight);
							graph.createEdge("v" + (i - 1), "v" + i, n, weight);
						}
					}
				}		
				
				int counter = 0;
				
				Path<Node<Symbol>> path = 
						graph.findShortestPath("u0", "v" + (nOfStates + 1));
				
				while (path.isValid() && counter < N) {
					Node<Symbol> pathTree = extractTreeFromPath(path, r);
					Weight pathWeight = new Weight(path.getWeight());
					
					// CHEATING
					pathWeight = pathWeight.add(qSmallestCompletionWeight);
					
					Run<Symbol> run = new Run<>(pathTree, pathWeight, q);
					runList.add(run);
					
					path = graph.findNextShortestPath();
					counter++;
				}
				
				runs.put(q, mergeRunLists(N, runList, runs.get(q)));
			}
		}
		
		// merge all lists with K, remember to get the delta value!
		
		LinkedList<Run<Symbol>> mergedList = new LinkedList<>();
		int nOfStatesInWTA = wta.getStates().size();
		
		for (LinkedList<Run<Symbol>> currentList : runs.values()) {
			mergedList = mergeRunLists(N*nOfStatesInWTA, currentList, 
					mergedList);
		}
		
		// merge with K, add all values to treeStateVal and get optstates for each tree added?
		for (Run<Symbol> r : mergedList) {
		
		
		}
	}
	
	private static Node<Symbol> extractTreeFromPath(Path<Node<Symbol>> path, 
			Rule r) {
		
		Node<Symbol> root = new Node<>(r.getSymbol());
		
		for (Edge<Node<Symbol>> e : path) {
			root.addChild(e.getLabel());
		}
		
		return root;
	}
	
	private static LinkedList<Run<Symbol>> mergeRunLists(int N,
			LinkedList<Run<Symbol>> list1, LinkedList<Run<Symbol>> list2) {
		
		LinkedList<Run<Symbol>> result = new LinkedList<>();
		
		int added = 0;
		
		while (added < N || (list1.isEmpty() && list2.isEmpty())) {
			
			Run<Symbol> run1 = list1.peek();
			Run<Symbol> run2 = list2.peek();
			
			if (run1 != null && run2 != null) {

				Weight weight1 = list1.peek().getWeight();
				Weight weight2 = list2.peek().getWeight();

				if (weight1.compareTo(weight2) < 1) {
					result.addLast(list1.poll());
				} else {
					result.addLast(list2.poll());
				}
				
			} else if (run1 != null) {
				result.addLast(list1.poll());
			} else {
				result.addLast(list2.poll());
			}
			
			added++;
		}
		
		return result;
	}

}
