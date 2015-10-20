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

package se.umu.cs.flp.aj.wta_handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import se.umu.cs.flp.aj.wta.Rule;
import se.umu.cs.flp.aj.wta.State;
import se.umu.cs.flp.aj.wta.Symbol;
import se.umu.cs.flp.aj.wta.WTA;
import se.umu.cs.flp.aj.wta.Weight;
import se.umu.cs.flp.aj.wta.exceptions.DuplicateRuleException;
import se.umu.cs.flp.aj.wta.exceptions.SymbolUsageException;

public class WTABuilder {

	public WTABuilder() {
		
	}
	
	/**
	 * Using modification of WTA along with Knuth's generalization of Dijkstra's
	 * algorithm to get the weights of the smallest completions.
	 * @param wta
	 * @return
	 */
	public HashMap<State, Weight> findSmallestCompletionWeights(WTA wta) {
		HashMap<State, Weight> smallestCompletionWeights =
				new HashMap<State, Weight>();
		
		ArrayList<State> states = wta.getStates();
		
		/*
		 * Build a modified WTA for each state in the original WTA
		 * and get the weight of the smallest tree that is accepted
		 * by the new WTA, that is, the smallest context with the
		 * current state as the connector.
		 */
		for (State state : states) {
			
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
			ArrayList<Rule> modifiedRules = modifiedWTA.
					getTransitionFunction().getRules();
			
			int nOfModifiedStates = modifiedStates.size();
			
			HashMap<State, State> defined = new HashMap<>();
			HashMap<State, Weight> weights = new HashMap<>();
			
			for (State s : modifiedStates) {
				weights.put(s, new Weight(Weight.INF));
			}

			while (defined.size() < nOfModifiedStates) {
				
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
					
					if (allDefined) {
						Weight oldWeight = weights.get(resultingState);
						
						if (newWeight.compareTo(oldWeight) == 1) {
							newWeight = oldWeight;
						}
						
						weights.put(resultingState, newWeight);
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

			smallestCompletionWeights.put(state, smallestCompletionWeight);

		}

		return smallestCompletionWeights;
	}
	
	/* Build a new WTA for every state in this case and then use Dijkstras on
	 * each of them. Is there no better way to do it? TODO
	 */
	public WTA buildModifiedWTA(WTA wta, State state) 
			throws SymbolUsageException, DuplicateRuleException {

		WTA modWTA = new WTA();
		
		ArrayList<Symbol> symbols = wta.getSymbols();
		ArrayList<State> states = wta.getStates();
		ArrayList<State> finalStates = wta.getFinalStates();
		ArrayList<Rule> rules = wta.getTransitionFunction().getRules();
			
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
		modWTA.getTransitionFunction().addRule(reservedSymbolRule);
		
		for (Rule r : rules) {
			modWTA.getTransitionFunction().addRule(r);
			
			ArrayList<State> leftHandStates = r.getStates();
			int nOfLHStates = leftHandStates.size();
			
			for (int i = 0; i < nOfLHStates; i++) {
				State newResultingState = new State(r.getResultingState().
						getLabel().concat(
								State.RESERVED_LABEL_EXTENSION_STRING));
				Rule newRule = new Rule(r.getSymbol(), r.getWeight(), 
						newResultingState);
				
				for (int j = 0; j < nOfLHStates; j++) {
					
					if (i == j) {
						newRule.addState(new State(leftHandStates.get(i).
								getLabel().
								concat(State.RESERVED_LABEL_EXTENSION_STRING)));
					} else {
						newRule.addState(leftHandStates.get(j));
					}
				}
				
				modWTA.getTransitionFunction().addRule(newRule);
			}
		}

		return modWTA;
	}
	
}
