package se.umu.cs.nfl.aj.nbest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

	private static ArrayList<Node> exploredTrees = new ArrayList<Node>(); // T
	private static PriorityQueue<Node> treeQueue =
			new PriorityQueue<Node>(INITIAL_SIZE, new TreeComparator()); // K
	private static NestedMap<Node, State, Double> treeStateValTable =
			new NestedMap<>(); // C

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

		/* For result. */
		List<String> nBest = new ArrayList<String>();

		return nBest;
	}

	public static HashMap<State, Weight> findSmallestCompletionWeights(WTA wta) {
		HashMap<State, Weight> smallestCompletions =
				new HashMap<State, Weight>();
		
		ArrayList<State> states = wta.getStates();
		
		for (State state : states) {
			Weight smallestCompletionWeight = null;
			
			WTA modifiedWTA = null;
			
			try {
				modifiedWTA = buildModifiedWTA(wta, state);
			} catch (SymbolUsageException e) {
				System.err.println("Cannot build modified WTA: " + e.getMessage());
				System.exit(-1);
			}
			
			ArrayList<State> finalStates = modifiedWTA.getFinalStates();
			
			HashMap<State, State> visited;
			LinkedList<State> unvisited;
			HashMap<State, Double> distances;

			while (!finalStates.isEmpty()) {
				ArrayList<Rule> currentRules =
						wta.getRulesByResultingState(finalStates.get(finalStates.size() - 1)); // TODO
			}

			// TODO Dijkstras
			
			
			smallestCompletions.put(state, smallestCompletionWeight);
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
			
			for (State s : leftHandStates) {
				Rule newRule = new Rule(r.getSymbol(), r.getWeight(), 
						r.getResultingState());
				
				for (State s2 : leftHandStates) {
					if (s2.equals(s)) {
						newRule.addState(new State(s.getLabel().concat(
								State.RESERVED_LABEL_EXTENSION_STRING)));
					} else {
						newRule.addState(s2);
					}
				}
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
