package se.umu.cs.nfl.aj.nbest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import se.umu.cs.nfl.aj.wta.Rule;
import se.umu.cs.nfl.aj.wta.State;
import se.umu.cs.nfl.aj.wta.Symbol;
import se.umu.cs.nfl.aj.wta.SymbolUsageException;
import se.umu.cs.nfl.aj.wta.WTA;
import se.umu.cs.nfl.aj.wta.WTAParser;
import se.umu.cs.nfl.aj.wta.Weight;

public class NBest {

	private static final int INITIAL_SIZE = 11;

	public static void main(String[] args) {

		String fileName = getFileName(args);
		int N = getN(args);
		WTAParser wtaParser = new WTAParser();
		WTA wta = wtaParser.parse(fileName);

		List<String> result = run(wta, N);

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
	
	public static List<String> run(WTA wta, int N) {
		
		HashMap<State, Weight> smallestCompletionWeights = 
				findSmallestCompletionWeights(wta);
		
		ArrayList<Node> exploredTrees = new ArrayList<Node>(); // T
		PriorityQueue<Node> treeQueue =
				new PriorityQueue<Node>(INITIAL_SIZE, new TreeComparator(smallestCompletionWeights)); // K
		NestedMap<Node, State, Weight> treeStateValTable = new NestedMap<>(); // C
		
		ArrayList<Symbol> symbols = wta.getSymbols();
		
		for (Symbol s : symbols) {
			if (s.getRank() == 0) {
				treeQueue.add(new Node(s.getLabel()));
			}
		}

		/* For result. */
		List<String> nBest = new ArrayList<String>();
		
		
		
		

		return nBest;
	}

	/**
	 * Using modification of WTA along with Knuth's generalization of Dijkstra's
	 * algorithm to get the weights of the smallest completions.
	 * @param wta
	 * @return
	 */
	public static HashMap<State, Weight> findSmallestCompletionWeights(WTA wta) {
		HashMap<State, Weight> smallestCompletions =
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
			smallestCompletions.put(state, smallestCompletionWeight);
			counter++;
			System.out.println(counter);
		}

		return smallestCompletions;
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

//	private static void reset() {
//		exploredTrees = new ArrayList<Node>();
//		treeQueue = new PriorityQueue<Node>(initialSize, new TreeComparator());
//		treeStateValTable = new NestedMap<>();
//	}


}
