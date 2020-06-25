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
	private static ArrayList<Rule> usableRules;

	public static void computeBestContexts(WTA wta) {
		KnuthBestDerivations.wta = wta;
		computeBestTreeForEachState();
		computeBestContextForEachState();
	}
	
	/* Compute the tree with the best weight that can reach each state. */
	@SuppressWarnings("unchecked")
	private static void computeBestTreeForEachState() {
		int nOfStates = wta.getStateCount();
		int nOfRules = wta.getRuleCount();
		queue = new BinaryHeap<>();
		qElems = new Node[nOfStates + 1];
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
			state.setBestContext(element.getWeight());
			nOfDefined++;
			
System.out.println("state=" + state);

			/* Find new rules that can be used. */
			for (Rule r2 : state.getOutgoing()) {
				if (missingIndices[r2.getID()] == null) {
					missingIndices[r2.getID()] = r2.getNumberOfStates();
					Context newContext = new Context(r2.getWeight());
					newContext.addStateOccurrence(r2.getResultingState(), 1);
					ruleContexts[r2.getID()] = newContext;
				}
				
System.out.println("r2=" + r2);
				Context context = ruleContexts[r2.getID()];
				Context defContext = state.getBestContext();
				
				for (State s : r2.getStates()) {
System.out.println("s=" + s);


					if (s.getID() == state.getID()) {
						for (Entry<State, Integer> entry : defContext.getStateOccurrences().entrySet()) {
System.out.println("Occurrence for state " + entry.getKey() + " is " + entry.getValue());
							context.addStateOccurrence(entry.getKey(), entry.getValue());
						}
						missingIndices[r2.getID()]--;
						Weight currentWeight = context.getWeight();
						Weight newWeight = currentWeight.mult(s.getBestContext().getWeight());
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
		}
		
		/*Print cheapest context-trees*/
		for (State s : wta.getStates().values()) {
			Context c = s.getBestContext();
System.out.println(s + " : " + c.getWeight());
System.out.println("Depth: " + c.getDepth());
			for (Entry<State, Integer> e : c.getStateOccurrences().entrySet()) {
System.out.println(e.getKey() + " id: " + e.getKey().getID() + "| " + e.getValue() );
			}
		}
	}

	/* Search and combine the previously computed trees to achieve the contexts
	 * of best weights. */
	@SuppressWarnings("unchecked")
	private static void computeBestContextForEachState() {
		int nOfStates = wta.getStateCount();
		queue = new BinaryHeap<>();
//		defined = new Context[nOfStates + 1];
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
			s.setBestContext(newContext);
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

//					Weight newWeight = r.getWeight().mult(
//							defined[resState.getID()].getWeight());
					Weight newWeight = r.getWeight().mult(resState.getBestContext().getWeight());
					Context oldContext = element.getWeight();
					Context newContext = new Context();

					for (int j = 0; j < listSize; j++) {
						State s2 = stateList.get(j);
						Context cTemp = s2.getBestContext();
						
						if (i != j) {
							newWeight = newWeight.mult(cTemp.getWeight());
							int newStateOccurence = cTemp.getStateOccurrence(s) - 
									cTemp.getStateOccurrence(s2) + 1; // TODO
							newContext.addStateOccurrence(s2, newStateOccurence);
						}
					}
					
					newContext.addStateOccurrence(resState, 1);
					newContext.setDepth(resState.getBestContext().getDepth() + 1);
					newContext.setWeight(newWeight);

					if (oldContext == null) {
						queue.insert(element, newContext);
					} else if (newWeight.compareTo(oldContext.getWeight()) < 0) {
						queue.decreaseWeight(element, newContext);
					} //else {
//						for (int j = 0; j < listSize; j++) {
//							State s2 = stateList.get(j);
//							if (i != j) {
//								Context cTemp = s2.getBestContext();
//								int newStateOccurence = cTemp.getStateOccurrence(s) - 
//										cTemp.getStateOccurrence(s2);
//								oldContext.addStateOccurrence(s2, newStateOccurence);
//							}
//						}
//						oldContext.addStateOccurrence(resState, 1);
//						oldContext.setDepth(resState.getBestContext().getDepth() + 1);
//					}
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
				state.setBestContext(element.getWeight());
//				defined[state.getID()] = element.getWeight();
				nOfDefined++;

				for (Rule r : state.getIncoming()) {
					usableRules.add(r);
					usableSize++;
				}

			} else {
//				for (int i = 1; i < nOfStates + 1; i++) {
//					if (defined[i] == null) {
//						defined[i] = new Context(wta.getSemiring().zero());
//					}
//				}
				
//				for (State s : wta.getStates().values()) {
//					if (s.getBestContext() == null) {
//						s.setBestContext(new Context(wta.getSemiring().zero()));
//					}
//				}

				done = true;
			}
		}
		
		/*Print cheapest contexts*/
		for (State s : wta.getStates().values()) {
			Context c = s.getBestContext();
System.out.println(s + " : " + c.getWeight());
System.out.println("Depth: " + c.getDepth());
			for (Entry<State, Integer> e : c.getStateOccurrences().entrySet()) {
System.out.println(e.getKey() + " id: " + e.getKey().getID() + "| " + e.getValue() );
			}
		}

//		return defined;
	}
}
