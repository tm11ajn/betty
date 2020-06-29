package se.umu.cs.flp.aj.knuth;

import java.util.ArrayList;
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

	public static Context[] computeBestContexts(WTA wta) {
		KnuthBestDerivations.wta = wta;
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
		while (nOfDefined < nOfStates) {
			
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
//                Context defContext = state.getBestContext();
				Context defContext = defined[state.getID()];
                
                for (State s : r2.getStates()) {
                    if (s.getID() == state.getID()) {
                        for (Entry<State, Integer> entry : defContext.getStateOccurrences().entrySet()) {
                            context.addStateOccurrence(entry.getKey(), entry.getValue());
                        }
                        missingIndices[r2.getID()]--;
                        Weight currentWeight = context.getWeight();
//                        Weight newWeight = currentWeight.mult(s.getBestContext().getWeight());
                        Weight newWeight = currentWeight.mult(defined[s.getID()].getWeight());
                        context.setWeight(newWeight);
                    }
                }
				
//				for (State s : r2.getStates()) {
//					if (s.getID() == state.getID()) {
//						missingIndices[r2.getID()]--;
//						Context context = ruleContexts[r2.getID()];
//						Weight currentWeight = context.getWeight();
//						Weight newWeight = currentWeight.mult(defined[s.getID()].getWeight());
//						context.setWeight(newWeight);
//						Context defContext = defined[s.getID()];
//						
////						for (Entry<State, Integer> entry : defContext.getStateOccurrences().entrySet()) {
////							context.addStateOccurrence(entry.getKey(), entry.getValue());
////						}
//					}
//				}

				/* Mark new rules as usable if all the states used to apply 
				 * the rule are defined. */
				if (missingIndices[r2.getID()] == 0) {
					usableRules.add(r2);
					usableSize++;
				}
			}
		}
		
		/*Print cheapest trees*/
//System.out.println("cheapest trees");
//		for (int i = 1; i < nOfStates + 1; i++) {
//			Context c = defined[i];
//			BinaryHeap<State, Context>.Node elem = qElems[i];
//			if (elem != null) {
//System.out.println(elem.getObject() + " : " + c.getWeight());
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
            newContext.setDepth(0);
            newContext.setStateOccurrence(s, 1);
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

					 /* Compute new weight */ 
					Weight newWeight = r.getWeight().mult(
							defined[resState.getID()].getWeight());

					for (int j = 0; j < listSize; j++) {
						State s2 = stateList.get(j);
						if (i != j) {
							Context cTemp = bestTreeForState[s2.getID()];
							newWeight = newWeight.mult(cTemp.getWeight());
						}
					}
					
					/* Create new context with new weight and depth */
					Context oldContext = element.getWeight();
					Context newContext = new Context();
//					Context prevContext = bestTreeForState[s.getID()];
                    newContext.setDepth(defined[resState.getID()].getDepth() + 1);
                    newContext.setWeight(newWeight);
                    
                    /* Compute state usage in best context */
                    ArrayList<State> relevantStates = new ArrayList<State>(bestTreeForState[resState.getID()].
                    		getStateOccurrences().keySet());
                    relevantStates.addAll(defined[resState.getID()].getStateOccurrences().keySet());

                    for (State s2 : relevantStates) {

                        if (newContext.getStateOccurrence(s2) > 0) {
                            continue;
                        }

                        int newStateOccurrence = defined[resState.getID()].getStateOccurrence(s2) +
                                bestTreeForState[resState.getID()].getStateOccurrence(s2) - 
                                bestTreeForState[s.getID()].getStateOccurrence(s2);
                        
                        if (s2.getID() == s.getID()) {
                            newStateOccurrence++;
                        }

                        if (resState.getID() == s2.getID()) {
                            newStateOccurrence--;
                        }
                        newContext.addStateOccurrence(s2, newStateOccurrence);
                    }

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
//					BinaryHeap<State, Context>.Node elem = qElems[i];
					if (defined[i] == null) {
						defined[i] = new Context(wta.getSemiring().zero());
					}
//					elem.getObject().setBestContext(defined[i]);
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
//System.out.println(elem.getObject() + " : " + c.getWeight());
//				for (Entry<State, Integer> e : c.getStateOccurrences().entrySet()) {
//System.out.println(e.getKey() + " id: " + e.getKey().getID() + "| " + e.getValue() );
//				}
//			}
//		}

		return defined;
	}
}
