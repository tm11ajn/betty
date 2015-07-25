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

		List<String> result = run2(wta, N);	

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
		
		// enqueue(K, Sigma_0)
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
		
		// while i < N and K nonempty do
		while (counter < N && !treeQueue.isEmpty()) {
			
			// t <- dequeue(K)
			Node<Symbol> currentTree = treeQueue.poll();
			
			// Get optimal state for current tree
//			State optimalState = getOptimalState(wta, currentTree, 
//					smallestCompletionWeights);
//			optimalStates.put(currentTree, optimalState);
			State optimalState = optimalStates.get(currentTree).get(0);
			
			
			// T <- T u {t}
			exploredTrees.add(currentTree);
			
			// if M(t) = delta(t) then TODO
			// is this the same thing as smallestcompletion(optimalstate) = 0 
			// and optimalstate in final states ?
			if (optimalState.isFinal()) {
				
				// output(t)
				//nBest.add(currentTree.toString());
				nBest.add(currentTree.toString() + " " + treeStateValTable.get(currentTree, optimalState).toString());
				
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
				nBest.add(currentTree.toString() + " " + 
						treeStateValTable.get(currentTree, optimalState));
				
				// i <- i + 1
				counter++;
			}
			
			// prune(T, enqueue(K, expand(T, t)))
			enqueueWithExpansionAndPruning(wta, N, currentTree);			
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
				
				optimalStates.put(tree, getOptimalStates2(tree));
				
				Weight treeDeltaWeight = getDeltaWeight(tree);
				
				int tempQueueSize = tempQueue.size();
				int insertIndex = 0;
				
				for (int i = 0; i < tempQueueSize; i++) {
					
					Node<Symbol> currentTree = tempQueue.get(i);
					Weight currentTreeDeltaWeight = getDeltaWeight(currentTree);
					
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
			}
		}
		
		for (Node<Symbol> n : tempQueue) {
			insertIntoQueueUsingPruning(n, N);
		}
		
	}
	
	private static ArrayList<State> getOptimalStates2(Node<Symbol> tree) {
		
		HashMap<State, Weight> stateValTable = 
				treeStateValTable.getAll(tree);
		ArrayList<State> optStates = new ArrayList<>();
		Weight minWeight = new Weight(Weight.INF);
		
		for (Entry<State, Weight> e : stateValTable.entrySet()) {
			
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
		
		return optStates;
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
			
			if (canUseRule) {
				Weight currentWeight = treeStateValTable.get(tree, 
						r.getResultingState());
				
				// Find smallest weight for each state
				if (currentWeight == null || 
						(currentWeight.compareTo(weightSum) == 1)) {
					treeStateValTable.put(tree, r.getResultingState(), 
							weightSum);
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
		Weight smallestCompletionWeight = smallestCompletionWeights.
				get(optimalState);
		
		delta = minWeight.add(smallestCompletionWeight);
		
		return delta;
	}
	
	public static ArrayList<Node<Symbol>> expandWith(WTA wta, Node<Symbol> tree) {
		ArrayList<Node<Symbol>> expansion = new ArrayList<Node<Symbol>>();
		ArrayList<Rule> rules = wta.getRules();
		
		for (Rule r : rules) {
			ArrayList<State> states = r.getStates();
			HashMap<State, ArrayList<Node<Symbol>>> producibleTrees = 
					new HashMap<>();
					
			boolean canUseAllStates = true;
			boolean usesT = false;
			
			for (State s : states) {
				
				for (Node<Symbol> n : exploredTrees) {
					
					if (treeStateValTable.get(n, s) != null) {
						
						if (!producibleTrees.containsKey(s)) {
							producibleTrees.put(s, 
									new ArrayList<Node<Symbol>>());
						}
						
						ArrayList<Node<Symbol>> treesForState = 
								producibleTrees.get(s);
						
						if (!treesForState.contains(n)) {
							treesForState.add(n);
						
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
					}

					boolean increased = false;
					int index = 0;

					while (!increased && index < nOfStates) {

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
		
		return expansion;
	}
	
	public static void enqueueWithExpansionAndPruning(WTA wta, int N, 
			Node<Symbol> tree) {
		
		HashMap<State, ArrayList<LinkedList<Node<Symbol>>>> allRuns = new HashMap<>();
		HashMap<State, LinkedList<Node<Symbol>>> nRuns = new HashMap<>();
		
		for (State q : wta.getStates()) {
			ArrayList<LinkedList<Node<Symbol>>> hej;
			allRuns.put(q, (hej = useEppstein(wta, N, tree, q)));
		}
		
		// Merge into one list for each Q
		for (Entry<State, ArrayList<LinkedList<Node<Symbol>>>> e : 
			allRuns.entrySet()) {
			
			LinkedList<Node<Symbol>> mergedTreeList = new LinkedList<>();
			State q = e.getKey();
			
			for (LinkedList<Node<Symbol>> treeList : e.getValue()) {
				mergedTreeList = mergeTreeLists(q, N, treeList, mergedTreeList);
			}
			
			nRuns.put(q, mergedTreeList);
		}
		
		// merge with K, add all values to treeStateVal and get optstates for each tree added?
		
		// Merge all lists, remember to get the delta value!
		
		LinkedList<Node<Symbol>> mergedList = new LinkedList<>();
		int nOfStatesInWTA = wta.getStates().size();
		
		for (LinkedList<Node<Symbol>> currentList : nRuns.values()) {
			mergedList = mergeTreeListsByDeltaWeights(N*nOfStatesInWTA, 
					currentList, mergedList);
		}
		
		// Merge sorted list with K
		for (Node<Symbol> n : mergedList) {
			insertIntoQueueUsingPruning(n, N);
		}
		
	}
	
	public static ArrayList<LinkedList<Node<Symbol>>> useEppstein(WTA wta, int k, 
			Node<Symbol> tree, State q) {
		
		ArrayList<LinkedList<Node<Symbol>>> kBestTreesForEachQRule = 
				new ArrayList<>();

		Graph<Node<Symbol>> graph = new Graph<>();

		ArrayList<Rule> rules = wta.getRulesByResultingState(q);
		

		for (Rule r : rules) {
			LinkedList<Node<Symbol>> treeList = new LinkedList<>();
			
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

			for (int i = 1; i < nOfStates + 1; i++) {
				State currentState = states.get(i-1);

				for (Node<Symbol> n : exploredTrees) { // TODO include only N edges for each pair of nodes

					Weight w = treeStateValTable.get(n, currentState);
					double weight = Double.MAX_VALUE;
					
					if (w != null) {
						weight = Double.parseDouble(w.toString());
					} else {
						continue;
					}

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
					graph.findShortestPath("u0", "v" + nOfStates);
			
			for (Edge<Node<Symbol>> e : path) {
				System.out.println(e.getLabel() + " : " + e.getWeight());
			}
			
			while (path.isValid() && counter < k) {
				Node<Symbol> pathTree = extractTreeFromPath(path, r);
				Weight pathWeight = new Weight(path.getWeight());
				
				if (path.getWeight() == Double.MAX_VALUE) {
					pathWeight = new Weight(Weight.INF);
				}
				
				// TODO Might have to add the weight of the rule here
				//pathWeight = pathWeight.add(r.getWeight());

				Weight oldWeight = treeStateValTable.get(pathTree, q);

				if (oldWeight == null 
						|| oldWeight.compareTo(pathWeight) == 1) {
					treeStateValTable.put(pathTree, q, pathWeight);
				}

				treeList.add(pathTree);
				counter++;
				
				path = graph.findNextShortestPath();
			}

			kBestTreesForEachQRule.add(treeList);
		}

		return kBestTreesForEachQRule;
	}
	
	private static Node<Symbol> extractTreeFromPath(Path<Node<Symbol>> path, 
			Rule r) {
		
		Node<Symbol> root = new Node<>(r.getSymbol());
		
		for (Edge<Node<Symbol>> e : path) {
			root.addChild(e.getLabel());
		}
		
		return root;
	}
	
	// Does not add same tree twice
	private static LinkedList<Node<Symbol>> mergeTreeLists(State q, 
			int listSizeLimit, LinkedList<Node<Symbol>> list1, 
			LinkedList<Node<Symbol>> list2) {
		
		LinkedList<Node<Symbol>> result = new LinkedList<>();
		
		int added = 0;
		
		while (added < listSizeLimit && !(list1.isEmpty() && list2.isEmpty())) {
			
			Node<Symbol> tree1 = list1.peek();
			Node<Symbol> tree2 = list2.peek();
			
			if (tree1 != null && result.contains(tree1)) {
				list1.poll();
			} else if (tree2 != null && result.contains(tree2)) {
				list2.poll();
			} else if (tree1 != null && tree2 != null) {

				Weight weight1 = treeStateValTable.get(tree1, q);
				Weight weight2 = treeStateValTable.get(tree2, q);

				if (weight1.compareTo(weight2) == -1) {
					result.addLast(list1.poll());
				} else if (weight1.compareTo(weight2) == 1) {
					result.addLast(list2.poll());
				} else {
					
					if (tree1.compareTo(tree2) == 0) {
						result.addLast(list1.poll());
						list2.poll();
					} else if (tree1.compareTo(tree2) == -1) {
						result.addLast(list1.poll());
					} else {
						result.addLast(list2.poll());
					}
				}
				
			} else if (tree1 != null) {
				result.addLast(list1.poll());
			} else {
				result.addLast(list2.poll());
			}
			
			added++;
		}
		
		return result;
	}
	
	// Does not add same tree twice
	private static LinkedList<Node<Symbol>> mergeTreeListsByDeltaWeights( 
			int listSizeLimit, LinkedList<Node<Symbol>> list1, 
			LinkedList<Node<Symbol>> list2) {

		LinkedList<Node<Symbol>> result = new LinkedList<>();

		int added = 0;

		while (added < listSizeLimit && !(list1.isEmpty() && list2.isEmpty())) {

			Node<Symbol> tree1 = list1.peek();
			Node<Symbol> tree2 = list2.peek();

			ArrayList<State> optStates1 = optimalStates.get(tree1);
			ArrayList<State> optStates2 = optimalStates.get(tree2);


			if (optStates1 == null) {
				optStates1 = getOptimalStates2(tree1);
				optimalStates.put(tree1, optStates1);
			} 
			
			if (optStates2 == null) {
				optStates2 = getOptimalStates2(tree1);
				optimalStates.put(tree2, optStates2);
			}
			
			State opt1 = optStates1.get(0);
			State opt2 = optStates2.get(0);
			
			Weight compl1 = smallestCompletionWeights.get(optStates1.get(0));
			Weight compl2 = smallestCompletionWeights.get(optStates2.get(0));

			if (tree1 != null && result.contains(tree1)) {
				list1.poll();
			} else if (tree2 != null && result.contains(tree2)) {
				list2.poll();
			} else if (tree1 != null && tree2 != null) {

				Weight weight1 = treeStateValTable.get(tree1, opt1).add(compl1);
				Weight weight2 = treeStateValTable.get(tree2, opt2).add(compl2);

				if (weight1.compareTo(weight2) == -1) {
					result.addLast(list1.poll());
				} else if (weight1.compareTo(weight2) == 1) {
					result.addLast(list2.poll());
				} else {

					if (tree1.equals(tree2)) {
						result.addLast(list1.poll());
						list2.poll();
					} else if (tree1.compareTo(tree2) == -1) {
						result.addLast(list1.poll());
					} else {
						result.addLast(list2.poll());
					}
				}

			} else if (tree1 != null) {
				result.addLast(list1.poll());
			} else {
				result.addLast(list2.poll());
			}

			added++;
		}

		return result;
	}
	
	private static boolean insertIntoQueueUsingPruning(Node<Symbol> tree, int N) {
		
		ArrayList<State> optStates = optimalStates.get(tree);
		Weight deltaWeight = getDeltaWeight(tree);
		
		boolean canInsert = false;
		
		for (State q : optStates) {
			
			if (optimalStatesUsage.get(q) == null) {
				optimalStatesUsage.put(q, 0);
			}
			
			if (optimalStatesUsage.get(q) < N) {
				canInsert = true;
			}
		}
		
		int index = treeQueue.size() - 1;
		boolean isSmaller = true;
		
		while (!canInsert && isSmaller && index > -1) {
			Node<Symbol> last = treeQueue.get(index);
			Weight lastDeltaWeight = getDeltaWeight(last);
			
			if (lastDeltaWeight.compareTo(deltaWeight) == -1) {
				isSmaller = false;
			} else if (lastDeltaWeight.compareTo(deltaWeight) == 0) {
				
				if (last.toString().compareTo(tree.toString()) != 1) {
					isSmaller = false;
				}
			}
			
			if (isSmaller) {
				ArrayList<State> optStatesLast = optimalStates.get(last);
				
				for (State optState : optStates) {
					
					if (optStatesLast.contains(optState)) {
						canInsert = true;
						treeQueue.remove(index);
						break;
					}
				}
			}
			
			index--;
		}
		
		if (canInsert) {
			
			for (State q : optStates) {
				optimalStatesUsage.put(q, optimalStatesUsage.get(q) + 1);
			}
			
			int queueSize = treeQueue.size();
			int insertIndex = 0;
			
			for (int i = 0; i < queueSize; i++) {
				Node<Symbol> n = treeQueue.get(i);
				
				Weight currentDeltaWeight = getDeltaWeight(n);
				
				if (currentDeltaWeight.compareTo(deltaWeight) == 1) {
					insertIndex = i;
					break; // TODO boolean and while instead
				} else if (currentDeltaWeight.compareTo(deltaWeight) == 0) {
					
					if (n.toString().compareTo(tree.toString()) == 1) {
						insertIndex = i;
						break; // TODO boolean and while instead
					} else {
						insertIndex = i + 1;
					}
				} else {
					insertIndex = i + 1;
				}
			}
					
			treeQueue.add(insertIndex, tree);
		}
		
		return canInsert;
	}

}
