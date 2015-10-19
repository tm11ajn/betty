/*
 * Copyright 2015 Anna Jonsson for the research group Foundations of Language 
 * Processing, Department of Computing Science, Umeå university
 * 
 * This file is part of BestTrees.
 * 
 * BestTrees is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * BestTrees is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with BestTrees.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.umu.cs.flp.aj.nbest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import se.umu.cs.flp.aj.nbest.data.NestedMap;
import se.umu.cs.flp.aj.nbest.data.Node;
import se.umu.cs.flp.aj.wta.Rule;
import se.umu.cs.flp.aj.wta.State;
import se.umu.cs.flp.aj.wta.Symbol;
import se.umu.cs.flp.aj.wta.WTA;
import se.umu.cs.flp.aj.wta.Weight;
import se.umu.cs.flp.aj.wta_handlers.WTABuilder;

public class BestTreesBasic {
	
	private static NestedMap<Node<Symbol>, State, Weight> treeStateValTable = 
			new NestedMap<>(); // C
	private static ArrayList<Node<Symbol>> exploredTrees = 
			new ArrayList<Node<Symbol>>(); // T
	private static LinkedList<Node<Symbol>> treeQueue = new LinkedList<>(); // K
	
	private static HashMap<State, Weight> smallestCompletionWeights;
	private static HashMap<Node<Symbol>, ArrayList<State>> optimalStates = 
			new HashMap<>();
			
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
			State optimalState = optimalStates.get(currentTree).get(0);
			
			
			// T <- T u {t}
			exploredTrees.add(currentTree);
			
			// if M(t) = delta(t)
			// (this the same thing as the optimal state being a final state)
			// then
			if (optimalState.isFinal()) {
				
				// output(t)
				nBest.add(currentTree.toString() + " " + 
						treeStateValTable.get(currentTree, 
								optimalState).toString());

				// i <- i + 1
				counter++;
			}
			
			// enqueue(K, expand(T, t))
			ArrayList<Node<Symbol>> expansion = expandWith(wta, currentTree);
			
			for (Node<Symbol> t : expansion) {
				optimalStates.put(t, getOptimalStates(wta, t));
				insertTreeIntoQueueByTotalMinimumWeight(t);
			}
			
		}

		return nBest;
	}
	
	
	
	
	
	// TODO
	// Eventually divide into two methods, one that calculates the M^q's 
	// and one that gets the optimal state using the M^q's.
	public static ArrayList<State> getOptimalStates(WTA wta, Node<Symbol> tree) {
		
		ArrayList<State> optStatesList = new ArrayList<>();
		State optimalState = null;
		Weight minWeight = new Weight(Weight.INF);
		
		ArrayList<Rule> rules = wta.getTransitionFunction().
				getRulesBySymbol(tree.getLabel());
		
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
				
				if (t.compareTo(tree) < 1) {
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
	
	public static ArrayList<Node<Symbol>> expandWith(WTA wta, 
			Node<Symbol> tree) {
		
		ArrayList<Node<Symbol>> expansion = new ArrayList<Node<Symbol>>();
		ArrayList<Rule> rules = wta.getTransitionFunction().getRules();
		
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
					break; // TODO while instead
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

}
