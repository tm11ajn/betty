package se.umu.cs.flp.aj.knuth;

import java.util.ArrayList;

import se.umu.cs.flp.aj.heap.BinaryHeap;
import se.umu.cs.flp.aj.heap.BinaryHeap.Node;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.WTA;

public class KnuthSmallestDerivations {
	private static WTA wta;
	private static BinaryHeap<State, Weight> queue;
	private static BinaryHeap<State, Weight>.Node[] qElems;
	private static Weight[] defined;
	private static ArrayList<Rule> usableRules;

	public static Weight[] getSmallestDerivations(WTA wta) {
		KnuthSmallestDerivations.wta = wta;
		return computeCheapestContexts(computeCheapestTrees());
	}

	@SuppressWarnings("unchecked")
	private static Weight[] computeCheapestTrees() {
		int nOfStates = wta.getStateCount();
		int nOfRules = wta.getRuleCount();
		queue = new BinaryHeap<>();
		qElems = new Node[nOfStates + 1];
		defined = new Weight[nOfStates + 1];
		usableRules = new ArrayList<>(nOfRules);
		Integer[] missingIndices = new Integer[nOfRules];
		int nOfDefined = 0;
		int usableStart = 0;
		int usableSize = 0;

		for (Rule r : wta.getSourceRules()) {
			usableRules.add(r);
			usableSize++;
		}

		while (nOfDefined < nOfStates) {

			for (int i = usableStart; i < usableSize; i++) {
				Rule r = usableRules.get(i);
				State resState = r.getResultingState();
				Weight oldWeight = null;
				Weight newWeight = getWeight(r);
				BinaryHeap<State, Weight>.Node element =
						qElems[resState.getID()];


				if (element != null) {
					oldWeight = element.getWeight();
				}

				if (oldWeight == null) {
					element = queue.add(resState, newWeight);
					qElems[resState.getID()] = element;
				} else if (newWeight.compareTo(oldWeight) < 0) {
					queue.decreaseWeight(qElems[resState.getID()],
							newWeight);
				}

				usableStart++;
			}

			BinaryHeap<State, Weight>.Node element = queue.dequeue();
			State state = element.getObject();
			Weight weight = element.getWeight();
			defined[state.getID()] = weight;
			nOfDefined++;

			for (Rule r2 : state.getOutgoing()) {
				if (missingIndices[r2.getID()] == null) {
					missingIndices[r2.getID()] = r2.getNumberOfStates();
				}

				if (defined[state.getID()] != null) {
					
//					// TODO: unnecessary and inefficient
//					int duplicateCounter = 0;
//					for (State s : r2.getStates()) {
//						if (s.getID() == state.getID()) {
//							duplicateCounter++;
//						}
//					}
//					missingIndices[r2.getID()] = missingIndices[r2.getID()] -
//							duplicateCounter;
					
					missingIndices[r2.getID()] = missingIndices[r2.getID()] -
							r2.getIndexOfState(state).size();
					
					if (missingIndices[r2.getID()] == 0) {
						usableRules.add(r2);
						usableSize++;
					}
				}
			}
		}

		return defined;
	}

	private static Weight getWeight(Rule rule) {
		Weight result = rule.getWeight();
		ArrayList<State> states = rule.getStates();

		if (states.size() == 0) {
			result = result.mult(wta.getSemiring().one());
		}

		for (State s : rule.getStates()) {
			result = result.mult(defined[s.getID()]);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private static Weight[] computeCheapestContexts(Weight[] cheapestTrees) {
		int nOfStates = wta.getStateCount();
		queue = new BinaryHeap<>();
		defined = new Weight[nOfStates + 1];
		qElems = new Node[nOfStates + 1];
		usableRules = new ArrayList<>();

		int nOfDefined = 0;
		boolean done = false;
		int usableStart = 0;
		int usableSize = 0;

		for (State s : wta.getFinalStates()) {
			defined[s.getID()] = wta.getSemiring().one();
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
					Weight newWeight = r.getWeight().mult(
							defined[resState.getID()]);
					Weight oldWeight = null;
					BinaryHeap<State, Weight>.Node element = qElems[s.getID()];

					if (element != null) {
						oldWeight = element.getWeight();
					}

					for (int j = 0; j < listSize; j++) {
						State s2 = stateList.get(j);
						if (i != j) {
							newWeight = newWeight.mult(
									cheapestTrees[s2.getID()]);
						}
					}

					if (oldWeight == null) {
						element = queue.add(s, newWeight);
						qElems[s.getID()] = element;

					} else if (newWeight.compareTo(oldWeight) < 0) {
						queue.decreaseWeight(qElems[s.getID()],
								newWeight);

					}
				}

				usableStart++;
			}

			if (!queue.empty()) {
				BinaryHeap<State, Weight>.Node element = queue.dequeue();
				State state = element.getObject();
				Weight weight = element.getWeight();
				defined[state.getID()] = weight;
				nOfDefined++;

				for (Rule r : state.getIncoming()) {
					usableRules.add(r);
					usableSize++;
				}

			} else {
				done = true;

				for (int i = 1; i < nOfStates + 1; i++) {
					if (defined[i] == null) {
						defined[i] = wta.getSemiring().zero();
					}
				}
			}
		}

		return defined;
	}
}
