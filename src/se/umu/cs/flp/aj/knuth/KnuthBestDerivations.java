package se.umu.cs.flp.aj.knuth;

import java.util.ArrayList;

import se.umu.cs.flp.aj.heap.BinaryHeap;
import se.umu.cs.flp.aj.heap.BinaryHeap.Node;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.Context;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.WTA;

public class KnuthBestDerivations {
	private static WTA wta;
	private static BinaryHeap<State, Weight> queue;
	private static BinaryHeap<State, Weight>.Node[] qElems;
	private static Context[] defined;
	private static ArrayList<Rule> usableRules;

	public static Context[] getBestContexts(WTA wta) {
		KnuthBestDerivations.wta = wta;
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
		Weight[] ruleWeights = new Weight[nOfRules];
		Integer[] missingIndices = new Integer[nOfRules];
		int nOfDefined = 0;
		int usableStart = 0;
		int usableSize = 0;

		for (Rule r : wta.getSourceRules()) {
			ruleWeights[r.getID()] = r.getWeight();
			State resState = r.getResultingState();
			BinaryHeap<State, Weight>.Node element = qElems[resState.getID()];
			
			if (element == null) {
				element = queue.createNode(resState);
				qElems[resState.getID()] = element;
			}
			
			if (!element.isEnqueued()) {
				queue.insertUnordered(element, r.getWeight());
			} else if (element.getWeight().compareTo(r.getWeight()) > 0) {
				element.setWeight(r.getWeight());
			}
		}
		
		queue.makeHeap();

		while (nOfDefined < nOfStates) {
			for (int i = usableStart; i < usableSize; i++) {
				Rule r = usableRules.get(i);
				State resState = r.getResultingState();
				BinaryHeap<State, Weight>.Node element = qElems[resState.getID()];
				
				if (element == null) {
					element = queue.createNode(resState);
					qElems[resState.getID()] = element;
				}
				
				Weight oldWeight = element.getWeight();
				Weight newWeight = ruleWeights[r.getID()];

				if (oldWeight == null) {
					queue.insert(element, newWeight);
				} else if (newWeight.compareTo(oldWeight) < 0) {
					queue.decreaseWeight(element, newWeight);
				}

				usableStart++;
			}

			BinaryHeap<State, Weight>.Node element = queue.dequeue();
			State state = element.getObject();
			Context newContext = new Context(element.getWeight());
			defined[state.getID()] = newContext;
//			defined[state.getID()] = element.getWeight();
			nOfDefined++;

			for (Rule r2 : state.getOutgoing()) {
				if (missingIndices[r2.getID()] == null) {
					missingIndices[r2.getID()] = r2.getNumberOfStates();
					ruleWeights[r2.getID()] = r2.getWeight();
				}
				
				for (State s : r2.getStates()) {
					if (s.getID() == state.getID()) {
						missingIndices[r2.getID()]--;
						ruleWeights[r2.getID()] = ruleWeights[r2.getID()].mult(
								defined[s.getID()].getWeight());
					}
				}

				if (missingIndices[r2.getID()] == 0) {
					usableRules.add(r2);
					usableSize++;
				}
			}
		}

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

		for (State s : wta.getFinalStates()) {
			Context newContext = new Context(wta.getSemiring().one());
			defined[s.getID()] = newContext;
			nOfDefined++;
			
			for (Rule r : s.getIncoming()) {
				usableRules.add(r);
				usableSize++;
			}
		}

		while (!done && nOfDefined < nOfStates) {
			for (int k = usableStart; k < usableSize; k++) {
				Rule r = usableRules.get(k);
				State resState = r.getResultingState();
				ArrayList<State> stateList = r.getStates();
				int listSize = stateList.size();

				for (int i = 0; i < listSize; i++) {
					State s = stateList.get(i);
					BinaryHeap<State, Weight>.Node element = qElems[s.getID()];

					if (s.isFinal()) {
						continue;
					}
					
					if (element == null) {
						element = queue.createNode(s);
						qElems[s.getID()] = element;
					}

					Weight newWeight = r.getWeight().mult(
							defined[resState.getID()].getWeight());
					Weight oldWeight = element.getWeight();

					for (int j = 0; j < listSize; j++) {
						State s2 = stateList.get(j);
						if (i != j) {
							newWeight = newWeight.mult(
									bestTreeForState[s2.getID()].getWeight());
						}
					}

					if (oldWeight == null) {
						queue.insert(element, newWeight);
					} else if (newWeight.compareTo(oldWeight) < 0) {
						queue.decreaseWeight(element, newWeight);
					}
				}

				usableStart++;
			}

			if (!queue.empty()) {
				BinaryHeap<State, Weight>.Node element = queue.dequeue();
				State state = element.getObject();
				Weight weight = element.getWeight();
				Context newContext = new Context(weight);
				defined[state.getID()] = newContext;
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

		return defined;
	}
}
