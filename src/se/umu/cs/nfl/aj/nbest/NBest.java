package se.umu.cs.nfl.aj.nbest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

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

		return args[1];
	}
	
	private static int getN(String[] args) {
		
		int N = 0;
		
		try {
			N = Integer.parseInt(args[2]);
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
				
				State optimalState = getOptimalState(wta, tree);
				optimalStates.put(tree, optimalState);
				
//				ArrayList<Rule> rules = wta.getRulesBySymbol(s);
//				Weight minWeight = new Weight(Weight.INF);
//				State minState = null;
//				
//				for (Rule r : rules) {
//					
//					Weight currentWeight = treeStateValTable.get(tree, 
//							r.getResultingState());
//					
//					// Find smallest weight for each state
//					if (currentWeight == null || 
//							(currentWeight.compareTo(r.getWeight()) == 1)) {
//						treeStateValTable.put(tree, r.getResultingState(), 
//								r.getWeight());
//					}
//					
//					// Find optimal state
//					if (r.getWeight().compareTo(minWeight) < 1) {
//						minWeight = r.getWeight();
//						minState = r.getResultingState();
//						optimalStates.put(tree, r.getResultingState());
//					}
//				}
				
				insertTreeIntoQueueByTotalMinimumWeight(tree, 
						smallestCompletionWeights, optimalStates);
								
			}
		}
		
		// i <- 0
		int counter = 0;
		
		// while i < N and K nonempty do
		while (counter < N && !treeQueue.isEmpty()) {
			
			// t <- dequeue(K)
			Node<Symbol> currentTree = treeQueue.poll();
			
			// Get optimal state for current tree
			optimalStates.put(currentTree, getOptimalState(wta, currentTree));
			
			// T <- T u {t}
			exploredTrees.add(currentTree);
			
			// if M(t) = delta(t) then TODO
			// is this the same thing as smallestcompletion(state) = 0?
			if (currentTree.isLeaf()) {
				
				// output(t)
				nBest.add(currentTree.toString());
				
				// i <- i + 1
				counter++;
			}
			
			// enqueue(K, expand(T, t)) TODO
			
			expandWith(currentTree);
			
			for (Node<Symbol> t : exploredTrees) {
				
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
		
		int counter = 0;
		
		/*
		 * Build a modified WTA for each state in the original WTA
		 * and get the weight of the smallest tree that is accepted
		 * by the new WTA, that is, the smallest context with the
		 * current state as the connector.
		 */
		for (State state : states) {
			System.out.println("CURRENT STATE: " + state);
			WTA modifiedWTA = null;
			
			try {
				modifiedWTA = buildModifiedWTA(wta, state);
			} catch (SymbolUsageException e) {
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
				
				System.out.println("PRINTTEST");
				
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
					System.out.println("Rule: " + r);
					System.out.println("new weight=" + newWeight);
					System.out.println("allDefined=" + allDefined);
					
					if (allDefined) {
						Weight oldWeight = weights.get(resultingState);
						System.out.println("Oldweight=" + oldWeight);
						
						if (newWeight.compareTo(oldWeight) == 1) {
							newWeight = oldWeight;
						}
						
						weights.put(resultingState, newWeight);
						System.out.println("PUT");
						System.out.println(resultingState + " " + newWeight);
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
				
				System.out.println("Smallest weight: " + smallestWeight);
				System.out.println("Smallest state: " + smallestState);
				System.out.println("Size defined: " + defined.size());
				System.out.println("Size modstates: " + modifiedStates.size());
				
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

			System.out.println("SMALLESTCOMPLETIONWEIGHT FOR CURRENTSTATE: " + 
					smallestCompletionWeight);
			smallestCompletionWeights.put(state, smallestCompletionWeight);
			counter++;
			System.out.println(counter);
		}

		return smallestCompletionWeights;
	}

	/* Build a new WTA for every state in this case and then use Dijkstras on
	 * each of them. Is there no better way to do it? TODO
	 */
	public static WTA buildModifiedWTA(WTA wta, State state) 
			throws SymbolUsageException {

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
	
	public static State getOptimalState(WTA wta, Node<Symbol> tree) 
			throws SymbolUsageException {
		
		State optimalState = null;
		Weight minWeight = new Weight(Weight.INF);
		
		ArrayList<Rule> rules = wta.getRulesBySymbol(tree.getLabel());
		
		int nOfSubtrees = tree.getChildCount();
		
		for (Rule r : rules) {
			
			System.out.println("Rule: " + r);
			
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
			
			System.out.println("Current weight=" + r.getWeight());
			System.out.println("Weightsum=" + weightSum);
			
			if (canUseRule) {
				System.out.println("In canUseRule-if");
				
				Weight currentWeight = treeStateValTable.get(tree, 
						r.getResultingState());
				
				System.out.println("Current weight: " + currentWeight);
				
				// Find smallest weight for each state
				if (currentWeight == null || 
						(currentWeight.compareTo(weightSum) == 1)) {
					treeStateValTable.put(tree, r.getResultingState(), 
							weightSum);
					System.out.println("Putting " + tree + ", " + r.getResultingState() + ", " + weightSum);
				}
				
				// Save optimal state
				if (weightSum.compareTo(minWeight) < 1) {
					optimalState = r.getResultingState();
					minWeight = weightSum;
					System.out.println("New optimal state: " + optimalState);
					System.out.println("New min weight: " + minWeight);
				}
			}
		}
		
		return optimalState;
	}
	
	public static void insertTreeIntoQueueByTotalMinimumWeight(
			Node<Symbol> tree, HashMap<State, Weight> smallestCompletionWeights, 
			HashMap<Node<Symbol>, State> optimalStates) {
		
		State optimalState = optimalStates.get(tree);
		
		Weight minWeight = treeStateValTable.get(tree, optimalState);
		Weight wMinCurrent = minWeight.add(
				smallestCompletionWeights.get(optimalState));
		int queueSize = treeQueue.size();
		int queueIndex = 0;
		
		for (int i = 0; i < queueSize; i++) {
			Node<Symbol> t = treeQueue.get(i);
			State optState = optimalStates.get(t);
			Weight wMinRun = treeStateValTable.get(t, optState);
			Weight wMinContext = smallestCompletionWeights.get(optState);
			Weight wMin = wMinRun.add(wMinContext);
			
			if (wMin.compareTo(wMinCurrent) == -1) {
				queueIndex = i + 1;
			} else if (wMin.compareTo(wMinCurrent) == 0) {
				
				if (t.toString().compareTo(tree.toString()) < 1) {
					queueIndex = i + 1;
				}
			}
		}
				
		treeQueue.add(queueIndex, tree);
	}
	
	public static ArrayList<Node<Symbol>> expandWith(Node<Symbol> tree) {
		ArrayList<Node<Symbol>> expansion = new ArrayList<Node<Symbol>>();
		
		return expansion;
	}

//	private static void reset() {
//		exploredTrees = new ArrayList<Node>();
//		treeQueue = new PriorityQueue<Node>(initialSize, new TreeComparator());
//		treeStateValTable = new NestedMap<>();
//	}


}
