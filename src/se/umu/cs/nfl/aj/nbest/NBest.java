package se.umu.cs.nfl.aj.nbest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import se.umu.cs.nfl.aj.eppstein_k_best.graph.Edge;
import se.umu.cs.nfl.aj.eppstein_k_best.graph.Graph;
import se.umu.cs.nfl.aj.eppstein_k_best.graph.Path;
import se.umu.cs.nfl.aj.wta.DuplicateRuleException;
import se.umu.cs.nfl.aj.wta.Rule;
import se.umu.cs.nfl.aj.wta.State;
import se.umu.cs.nfl.aj.wta.Symbol;
import se.umu.cs.nfl.aj.wta.SymbolUsageException;
import se.umu.cs.nfl.aj.wta.WTA;
import se.umu.cs.nfl.aj.wta.WTAParser;
import se.umu.cs.nfl.aj.wta.Weight;

public class NBest {

	private static NestedMap<Node<Symbol>, State, Weight> treeStateValTable = new NestedMap<>(); // C
	
	private static ArrayList<Node<Symbol>> exploredTrees = new ArrayList<Node<Symbol>>(); // T
	
	private static LinkedList<Node<Symbol>> treeQueue = new LinkedList<>(); // K

	public static void main(String[] args) {

		String fileName = getFileName(args);
		int N = getN(args);
		WTAParser wtaParser = new WTAParser();
		WTA wta = wtaParser.parse(fileName);

		List<String> result = null;
		try {
			result = run(wta, N);
		} catch (SymbolUsageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
	
	public static List<String> run(WTA wta, int N) throws SymbolUsageException {
		
		/* For result. */
		List<String> nBest = new ArrayList<String>();
		
		HashMap<State, Weight> smallestCompletionWeights = 
				findSmallestCompletionWeights(wta);
		
		treeStateValTable = new NestedMap<>(); // C
		HashMap<Node<Symbol>, State> optimalStates = new HashMap<>();
		
		// T <- empty
		exploredTrees = new ArrayList<Node<Symbol>>(); // T
		
		// K <- empty
		treeQueue = new LinkedList<Node<Symbol>>(); // K
		
		// enqueue(K, Sigma_0) TODO
		
		ArrayList<Symbol> symbols = wta.getSymbols();
		
		for (Symbol s : symbols) {
			
			if (s.getRank() == 0) {	
				Node<Symbol> tree = new Node<Symbol>(s);
				
				State optimalState = getOptimalState(wta, tree, smallestCompletionWeights);
				optimalStates.put(tree, optimalState);
				
				insertTreeIntoQueueByTotalMinimumWeight(tree, 
						smallestCompletionWeights, optimalStates);
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
			State optimalState = optimalStates.get(currentTree);
			
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
				optimalStates.put(t, getOptimalState(wta, t, 
						smallestCompletionWeights));
				insertTreeIntoQueueByTotalMinimumWeight(t, 
						smallestCompletionWeights, optimalStates);
			}
			
		}

		return nBest;
	}

	/**
	 * Using modification of WTA along with Knuth's generalization of Dijkstra's
	 * algorithm to get the weights of the smallest completions.
	 * @param wta
	 * @return
	 */
	public static HashMap<State, Weight> findSmallestCompletionWeights(WTA wta) {
		HashMap<State, Weight> smallestCompletionWeights =
				new HashMap<State, Weight>();
		
		ArrayList<State> states = wta.getStates();
		
//		int counter = 0;
		
		/*
		 * Build a modified WTA for each state in the original WTA
		 * and get the weight of the smallest tree that is accepted
		 * by the new WTA, that is, the smallest context with the
		 * current state as the connector.
		 */
		for (State state : states) {
//			System.out.println("CURRENT STATE: " + state);
			WTA modifiedWTA = null;
			
			try {
				modifiedWTA = buildModifiedWTA(wta, state);
			} catch (SymbolUsageException e) {
				System.err.println("Cannot build modified WTA: " + e.getMessage());
				System.exit(-1);
			} catch (DuplicateRuleException e) {
				System.err.println("Cannot build modified WTA: " + e.getMessage());
				System.exit(-1);
			}
			
			ArrayList<State> modifiedStates = modifiedWTA.getStates();
			ArrayList<State> modifiedFinalStates = modifiedWTA.getFinalStates();
			ArrayList<Rule> modifiedRules = modifiedWTA.getRules();
			
			int nOfModifiedStates = modifiedStates.size();
			
			HashMap<State, State> defined = new HashMap<>();
			HashMap<State, Weight> weights = new HashMap<>();
			
			for (State s : modifiedStates) {
				weights.put(s, new Weight(Weight.INF));
			}

			while (defined.size() < nOfModifiedStates) {
				
//				System.out.println("PRINTTEST");
				
				for (Rule r : modifiedRules) {
					ArrayList<State> leftHandStates = r.getStates();
					State resultingState = r.getResultingState();
					Weight newWeight = new Weight(0);
					boolean allDefined = true;
					
					for (State s : leftHandStates) {
						
						if (!defined.containsKey(s)) {
							allDefined = false;
							break;
						}
						
						newWeight = newWeight.add(weights.get(s));
					}
					
					newWeight = newWeight.add(r.getWeight());
//					System.out.println("Rule: " + r);
//					System.out.println("new weight=" + newWeight);
//					System.out.println("allDefined=" + allDefined);
					
					if (allDefined) {
						Weight oldWeight = weights.get(resultingState);
//						System.out.println("Oldweight=" + oldWeight);
						
						if (newWeight.compareTo(oldWeight) == 1) {
							newWeight = oldWeight;
						}
						
						weights.put(resultingState, newWeight);
//						System.out.println("PUT");
//						System.out.println(resultingState + " " + newWeight);
					}
				}
				
				Weight smallestWeight = new Weight(Weight.INF);
				State smallestState = null;
				
				for (Entry<State, Weight> e : weights.entrySet()) {
					Weight tempWeight = e.getValue();
					State tempState = e.getKey();
					
					if (!defined.containsKey(tempState) && 
							smallestWeight.compareTo(tempWeight) > -1) {
						smallestWeight = tempWeight;
						smallestState = tempState;
					}
				}
				
				defined.put(smallestState, smallestState);
				modifiedStates.remove(smallestState);
				
//				System.out.println("Smallest weight: " + smallestWeight);
//				System.out.println("Smallest state: " + smallestState);
//				System.out.println("Size defined: " + defined.size());
//				System.out.println("Size modstates: " + modifiedStates.size());
				
				// Add corresponding state to defined and 
				// remove it from modifiedStates
			}
			
			Weight smallestCompletionWeight = new Weight(Weight.INF);
			
			for (State s : modifiedFinalStates) {
				Weight tempWeight = weights.get(s);
				
				if (tempWeight == null) {
					System.err.println("In getting the smallest completion, "
							+ "the final state " + s + "did not have any weight"
							+ "assigned to it");
					System.exit(-1);
				}
				
				if (tempWeight.compareTo(smallestCompletionWeight) == -1) {
					smallestCompletionWeight = tempWeight; 
				}
			}

//			System.out.println("SMALLESTCOMPLETIONWEIGHT FOR CURRENTSTATE: " + 
//					smallestCompletionWeight);
			smallestCompletionWeights.put(state, smallestCompletionWeight);
//			counter++;
//			System.out.println(counter);
		}

		return smallestCompletionWeights;
	}

	/* Build a new WTA for every state in this case and then use Dijkstras on
	 * each of them. Is there no better way to do it? TODO
	 */
	public static WTA buildModifiedWTA(WTA wta, State state) 
			throws SymbolUsageException, DuplicateRuleException {

		WTA modWTA = new WTA();
		
		ArrayList<Symbol> symbols = wta.getSymbols();
		ArrayList<State> states = wta.getStates();
		ArrayList<State> finalStates = wta.getFinalStates();
		ArrayList<Rule> rules = wta.getRules();
			
		for (Symbol s : symbols) {
			modWTA.addSymbol(s.getLabel(), s.getRank());
		}

		Symbol reservedSymbol = modWTA.addSymbol(
				Symbol.RESERVED_SYMBOL_STRING, 0);
		State reservedSymbolState = null;
		
		for (State s : states) {
			modWTA.addState(s.getLabel());
			State temp = modWTA.addState(s.getLabel().concat(
					State.RESERVED_LABEL_EXTENSION_STRING));
			
			if (s.equals(state)) {
				reservedSymbolState = temp;
			}
		}
		
		if (reservedSymbolState == null) {
			throw new SymbolUsageException("The state " + state + 
					"is not in the WTA.");
		}
		
		for (State s : finalStates) {
			modWTA.setFinalState(s.getLabel().concat(
					State.RESERVED_LABEL_EXTENSION_STRING));
		}
		
		Rule reservedSymbolRule = new Rule(reservedSymbol, new Weight(0), 
				reservedSymbolState); 
		modWTA.addRule(reservedSymbolRule);
		
		for (Rule r : rules) {
			modWTA.addRule(r);
			
			ArrayList<State> leftHandStates = r.getStates();
			int nOfLHStates = leftHandStates.size();
			
			for (int i = 0; i < nOfLHStates; i++) {
				State newResultingState = new State(r.getResultingState().
						getLabel().concat(State.RESERVED_LABEL_EXTENSION_STRING));
				Rule newRule = new Rule(r.getSymbol(), r.getWeight(), 
						newResultingState);
				
				for (int j = 0; j < nOfLHStates; j++) {
					
					if (i == j) {
						newRule.addState(new State(leftHandStates.get(i).getLabel().concat(
								State.RESERVED_LABEL_EXTENSION_STRING)));
					} else {
						newRule.addState(leftHandStates.get(j));
					}
				}
				
				modWTA.addRule(newRule);
			}
		}

		return modWTA;
	}
	
	// TODO
	// Eventually divide into two methods, one that calculates the M^q's 
	// and one that gets the optimal state using the M^q's.
	public static State getOptimalState(WTA wta, Node<Symbol> tree, 
			HashMap<State, Weight> smallestCompletionWeights) 
			throws SymbolUsageException {
		
		State optimalState = null;
		Weight minWeight = new Weight(Weight.INF);
		
		ArrayList<Rule> rules = wta.getRulesBySymbol(tree.getLabel());
		
		int nOfSubtrees = tree.getChildCount();
		
		for (Rule r : rules) {
			
//			System.out.println("Rule: " + r);
			
			ArrayList<State> states = r.getStates();
			int nOfStates = states.size();
			
			// TODO this is already checked in parsing, right?
			if (nOfStates != nOfSubtrees) {
				throw new SymbolUsageException("The symbol " + r.getSymbol() + 
						" with rank " + r.getSymbol().getRank() + "" +
						" is used in a rule " + r + " with rank " + nOfStates);
			}
			
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
				if (totalWeight.compareTo(minWeight) < 1) {
					optimalState = r.getResultingState();
					minWeight = totalWeight;
//					System.out.println("New optimal state: " + optimalState);
//					System.out.println("New min weight: " + minWeight);
				}
			}
		}
		
		return optimalState;
	}
	
	public static void insertTreeIntoQueueByTotalMinimumWeight(
			Node<Symbol> tree, HashMap<State, Weight> smallestCompletionWeights, 
			HashMap<Node<Symbol>, State> optimalStates) {
		
		Weight wMinCurrent = getDeltaWeight(tree, optimalStates, 
				smallestCompletionWeights);
		
		int queueSize = treeQueue.size();
		int queueIndex = 0;
		
		for (int i = 0; i < queueSize; i++) {
			Node<Symbol> t = treeQueue.get(i);
			
			Weight wMinTemp = getDeltaWeight(t, optimalStates, 
					smallestCompletionWeights);
			
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
	
	public static Weight getDeltaWeight(Node<Symbol> tree, 
			HashMap<Node<Symbol>, State> optimalStates,
			HashMap<State, Weight> smallestCompletionWeights) {
		
		Weight delta = new Weight(0);
		
		State optimalState = optimalStates.get(tree);
		
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
	
	public static ArrayList<Node<Symbol>> expandUsingPruning(WTA wta, int N, Node<Symbol> tree) {
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
					
					Run<Symbol> run = new Run<>(pathTree, pathWeight);
					runList.add(run);
					
					path = graph.findNextShortestPath();
					counter++;
				}
				
				runs.put(q, mergeRunLists(N, runList, runs.get(q)));
			}
			
			// merge all lists with K, remember to get the delta value!
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
			
		}
		
		return result;
	}

//	private static void reset() {
//		exploredTrees = new ArrayList<Node>();
//		treeQueue = new PriorityQueue<Node>(initialSize, new TreeComparator());
//		treeStateValTable = new NestedMap<>();
//	}

}
