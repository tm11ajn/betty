package se.umu.cs.nfl.aj.nbest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import se.umu.cs.nfl.aj.nbest.data.NestedMap;
import se.umu.cs.nfl.aj.nbest.data.Node;
import se.umu.cs.nfl.aj.wta.State;
import se.umu.cs.nfl.aj.wta.Symbol;
import se.umu.cs.nfl.aj.wta.Weight;

public class BestTreesBasic {
	
	private static NestedMap<Node<Symbol>, State, Weight> treeStateValTable = 
			new NestedMap<>(); // C
	private static ArrayList<Node<Symbol>> exploredTrees = 
			new ArrayList<Node<Symbol>>(); // T
	private static LinkedList<Node<Symbol>> treeQueue = new LinkedList<>(); // K
	
	private static HashMap<State, Weight> smallestCompletionWeights;
	private static HashMap<Node<Symbol>, ArrayList<State>> optimalStates = new HashMap<>();
	private static HashMap<State, Integer> optimalStatesUsage = new HashMap<>();
	
	

}
