/*
 * Copyright 2018 Anna Jonsson for the research group Foundations of Language
 * Processing, Department of Computing Science, Umeå university
 *
 * This file is part of Betty.
 *
 * Betty is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Betty is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.umu.cs.flp.aj.knuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import se.umu.cs.flp.aj.heap.BinaryHeap;
import se.umu.cs.flp.aj.heap.BinaryHeap.Node;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.Context;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.WTA;

public class KnuthBestDerivations {
	private static WTA wta;
	private static BinaryHeap<State, Context> queue;
	private static BinaryHeap<State, Context>.Node[] qElems;
	private static Context[] defined;
	private static ArrayList<Rule> usableRules;
	private static boolean trick;

	public static Context[] computeBestContexts(WTA wta, boolean trickVal) {
		KnuthBestDerivations.wta = wta;
		trick = trickVal;
//		computeBestContextForEachState(computeBestTreeForEachState());
		return computeBestContextForEachState(computeBestTreeForEachState());
	}
	
	/* Compute the tree with the best weight that can reach each state. */
	@SuppressWarnings("unchecked")
	private static Context[] computeBestTreeForEachState() {
		int nOfStates = wta.getStateCount();
		int nOfRules = wta.getRuleCount();
		queue = new BinaryHeap<>();
		qElems = new Node[nOfStates + 1];
		defined = new Context[nOfStates + 1];
		usableRules = new ArrayList<>(nOfRules);
		Context[] ruleContexts = new Context[nOfRules];
		Integer[] missingIndices = new Integer[nOfRules];
		int nOfDefined = 0;
		int usableStart = 0;
		int usableSize = 0;
		boolean done = false;

		/* Initialise each state with the weight of the smallest source rule
		 * leading to that state. The initial tree then consists of a tree
		 * in which we have reached the resulting state of the current rule once.*/
		for (Rule r : wta.getSourceRules()) {
			ruleContexts[r.getID()] = new Context(r.getWeight());
			State resState = r.getResultingState();
			BinaryHeap<State, Context>.Node element = qElems[resState.getID()];
			
			if (element == null) {
				element = queue.createNode(resState);
				qElems[resState.getID()] = element;
			}
			
			if (!element.isEnqueued()) {
				Context context = new Context(r.getWeight());
				context.setStateOccurrence(resState, 1);
				queue.insertUnordered(element, context);
			} else if (element.getWeight().getWeight().compareTo(r.getWeight()) > 0) {
				Context context = new Context(r.getWeight());
				context.setStateOccurrence(resState, 1);
				element.setWeight(context);
			}
		}
		
		queue.makeHeap();

		/* Main loop that picks the best tree in the queue and defines it. 
		 * Based on what tree was previously defined, new rules can be used,
		 * and these are added to the usableRules. */
		while (nOfDefined < nOfStates && !done) {
			
			/* Go over the rules that are currently usable but not previously seen,
			 * and see if we can use them to get a better result. */ 
			for (int i = usableStart; i < usableSize; i++) {
				Rule r = usableRules.get(i);
				State resState = r.getResultingState();
				BinaryHeap<State, Context>.Node element = qElems[resState.getID()];
				
				if (element == null) {
					element = queue.createNode(resState);
					qElems[resState.getID()] = element;
				}
				
				Context oldContext = element.getWeight();
				Context newContext = ruleContexts[r.getID()];

				if (oldContext == null) {
					queue.insert(element, newContext);
				} else if (newContext.compareTo(oldContext) < 0) {
					queue.decreaseWeight(element, newContext);
				}

				usableStart++;
			}

			/* Pick the currently best tree and add it to output = define it. */
			BinaryHeap<State, Context>.Node element = queue.dequeue();
			State state = element.getObject();
			defined[state.getID()] = element.getWeight();
			nOfDefined++;

			/* Find new rules that can be used. */
			for (Rule r2 : state.getOutgoing()) {
				if (missingIndices[r2.getID()] == null) {
					missingIndices[r2.getID()] = r2.getNumberOfStates();
					Context newContext = new Context(r2.getWeight());
					newContext.addStateOccurrence(r2.getResultingState(), 1);
					ruleContexts[r2.getID()] = newContext;
				}
				
				Context context = ruleContexts[r2.getID()];
				Context defContext = defined[state.getID()];
				
				for (State s : r2.getStates()) {
					if (s.getID() == state.getID()) {
						if (trick) {
							// Compute P(t_q,p) for each q,p in Q so that P(t_q,p) is 
							// the number of p's in the best tree for q.
							for (Entry<State, Integer> entry : defContext.getStateOccurrences().entrySet()) {
								context.addStateOccurrence(entry.getKey(), entry.getValue());
							}
						}
						missingIndices[r2.getID()]--;
						Weight currentWeight = context.getWeight();
						Weight newWeight = currentWeight.mult(defined[s.getID()].getWeight());
						context.setWeight(newWeight);
					}
				}

				/* Mark new rules as usable if all the states used to apply 
				 * the rule are defined. */
				if (missingIndices[r2.getID()] == 0) {
					usableRules.add(r2);
					usableSize++;
				}
			}
			
			if (queue.empty()) {
				for (int i = 1; i < nOfStates + 1; i++) {
					if (defined[i] == null) {
						defined[i] = new Context(wta.getSemiring().zero());
					}
				}
				done = true;
			}
		}
		
		/*Print cheapest trees*/
//System.out.println("cheapest trees");
//		for (int i = 1; i < nOfStates + 1; i++) {
//			Context c = defined[i];
//			BinaryHeap<State, Context>.Node elem = qElems[i];
//			if (elem != null && elem.getObject().isFinal()
//					) {
//System.out.println(elem.getObject() + " : Weight=" + c.getWeight());
//				for (Entry<State, Integer> e : c.getStateOccurrences().entrySet()) {
//System.out.println(e.getKey() + " id: " + e.getKey().getID() + "| " + e.getValue() );
//				}
//			}
//		}

		return defined;
	}

	/* Search and combine the previously computed trees to achieve the contexts
	 * of best weights. */
	@SuppressWarnings("unchecked")
	private static Context[] computeBestContextForEachState(Context[] bestTreeForState) {
		int nOfStates = wta.getStateCount();
		queue = new BinaryHeap<>();
		defined = new Context[nOfStates + 1];
		qElems = new Node[nOfStates + 1];
		usableRules = new ArrayList<>();
		int nOfDefined = 0;
		boolean done = false;
		int usableStart = 0;
		int usableSize = 0;

		/* Initialise the contexts for the final states to be empty 
		 * and to have weight one (according to semiring). */		
		for (State s : wta.getFinalStates()) {
			Context newContext = new Context(wta.getSemiring().one());
			HashMap<State, Integer> map = new HashMap<State, Integer>();
			map.put(s, 0);
			newContext.setDepth(0);
			newContext.setP(new ArrayList<HashMap<State, Integer>>());
			newContext.getP().add(map);
			newContext.setf(new ArrayList<Integer>());
			newContext.getf().add(1);
			defined[s.getID()] = newContext;
			nOfDefined++;
			for (Rule r : s.getIncoming()) {
				usableRules.add(r);
				usableSize++;
			}
		}

		/* Iteratively define best context using the priority queue and update
		 * what contexts can be created based on the currently defined states. */
		while (!done && nOfDefined < nOfStates) {
			for (int k = usableStart; k < usableSize; k++) {
				Rule r = usableRules.get(k);
				State resState = r.getResultingState();
				ArrayList<State> stateList = r.getStates();
				int listSize = stateList.size();

				for (int i = 0; i < listSize; i++) {
					State s = stateList.get(i);
					BinaryHeap<State, Context>.Node element = qElems[s.getID()];

					if (s.isFinal()) {
						continue;
					}
					
					if (element == null) {
						element = queue.createNode(s);
						qElems[s.getID()] = element;
					}
					
					/* Create new context */
					Context oldContext = element.getWeight();
					Context newContext = new Context();
					newContext.setDepth(defined[resState.getID()].getDepth() + 1);
					newContext.setP(new ArrayList<>(defined[resState.getID()].getP()));
					newContext.setf(new ArrayList<>(defined[resState.getID()].getf()));

//System.out.println("Current blank state: " + s);
//System.out.println("P(" + resState + ")");
//for (HashMap<State, Integer> m : defined[resState.getID()].getP()) {
//	for (Entry<State, Integer> e : m.entrySet()) {
//System.out.println(e.getKey() + " : " + e.getValue());
//	}
//}
//
//System.out.println("f");
//for (Integer e : defined[resState.getID()].getf()) {
//System.out.println(e);
//}

					 /* Compute new weight and state occurrence in smallest context 
					  * for current rule application */ 
					Weight newWeight = r.getWeight().mult(
							defined[resState.getID()].getWeight());
					HashMap<State, Integer> stateCount = new HashMap<>();

					for (int j = 0; j < listSize; j++) {
						State s2 = stateList.get(j);
						if (i != j) {
							Context cTemp = bestTreeForState[s2.getID()];
							newWeight = newWeight.mult(cTemp.getWeight());
							
							if (trick) {
								for(Entry<State, Integer> entry : cTemp.getStateOccurrences().entrySet()) {
									State eState = entry.getKey();
									int eVal = entry.getValue();
									int val = (stateCount.get(eState) == null) ? 0 : stateCount.get(eState);
									stateCount.put(eState, val + eVal);
								}
							}
						}
					}

					newContext.getP().add(stateCount);
					newContext.setWeight(newWeight);
					
					//TODO: 
					// For p in Q, and i <= depth(newContext), let P(newContext,p)_i = P(d_0[...[d_i]...],p)
					// f(newContext) = sum_{i \in [depth(newContext)]} f(d_0[...[d_i]..]) * P(newContext,s2)_i
					
					// I kontext behöver vi en vektor som är lika stor som djupet på kontexten och som ärvs av 
					// barn-kontexterna och fylls på med värden
					// Tittar uppåt
					
					/*
					 * Sedan behöver vi en likadan struktur för att hålla reda på P-värdena
					 * under den i:te positionen, hur många av varje tillstånd har vi?
					 */
					
					if (trick) {
						int fNext = 1;
						for (int index = 0; index < newContext.getDepth(); index++) {
							int sumP = 1;
							for (int indexP = index; indexP < newContext.getDepth() + 1; indexP++) {
								HashMap<State, Integer> h = newContext.getP().get(indexP);
								sumP += (h.get(s) == null) ? 0 : h.get(s);
							}
							fNext = Math.max(fNext, newContext.getf().get(index) * sumP);
						}
						newContext.getf().add(fNext);
					}
					
//System.out.println("Next fValue: " + fNext);

					/* Add or update queue element */
					if (oldContext == null) {
						queue.insert(element, newContext);
					} else if (newWeight.compareTo(oldContext.getWeight()) < 0) {
						queue.decreaseWeight(element, newContext);
					}
				}

				usableStart++;
			}

			/* Define the currently best weighted context and based on that 
			 * find new rules that we can use. If the queue is empty, then
			 * not all states are reachable from the final states, and we 
			 * can set those contexts to empty ones with weight zero 
			 * (according to semiring, and that weight is the identity element
			 * for the semiring addition) */
			if (!queue.empty()) {
				BinaryHeap<State, Context>.Node element = queue.dequeue();
				State state = element.getObject();
				defined[state.getID()] = element.getWeight();
				nOfDefined++;
				for (Rule r : state.getIncoming()) {
					usableRules.add(r);
					usableSize++;
				}
			} else {
				for (int i = 1; i < nOfStates + 1; i++) {
					if (defined[i] == null) {
						defined[i] = new Context(wta.getSemiring().zero());
					}
				}
				done = true;
			}
		}
		
		/*Print best contexts*/
//System.out.println("best contexts");
//		for (int i = 1; i < nOfStates + 1; i++) {
//			Context c = defined[i];
//			BinaryHeap<State, Context>.Node elem = qElems[i];
//			if (elem != null) {
//System.out.println(elem.getObject() + " : Weight=" + c.getWeight());
//			} else {
//System.out.println("state with id=" + i + " : Weight=" + c.getWeight());
//			}
//			if (c.getP() != null) {
//				for (Entry<State, Integer> e : c.getP().get(c.getDepth()).entrySet()) {
//System.out.println("P(" + e.getKey() + ")=" + e.getValue());
//				}
//System.out.println("f=" + c.getfValue());
//			}
//		}

		return defined;
	}
}
