package se.umu.cs.flp.aj.knuth;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import se.umu.cs.flp.aj.heap.BinaryHeap;
import se.umu.cs.flp.aj.heap.BinaryHeap.Node;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.WTA;

public class KnuthBestDerivations {
	private static WTA wta;
	private static BinaryHeap<State, Weight> queue;
	private static BinaryHeap<State, Weight>.Node[] qElems;
	private static Weight[] defined;
	private static ArrayList<Rule> usableRules;
//	private static LinkedList<Rule> usable;

	public static Weight[] getBestDerivations(WTA wta) {
		KnuthBestDerivations.wta = wta;
		return computeBestContexts(computeBestTrees());
	}
	
	@SuppressWarnings("unchecked")
	private static Weight[] computeBestTrees() {
		int nOfStates = wta.getStateCount();
		int nOfRules = wta.getRuleCount();
		queue = new BinaryHeap<>();
		qElems = new Node[nOfStates + 1];
		defined = new Weight[nOfStates + 1];
		usableRules = new ArrayList<>(nOfRules);
//		usable = new LinkedList<>();
		Weight[] ruleWeights = new Weight[nOfRules];
		Integer[] missingIndices = new Integer[nOfRules];
		int nOfDefined = 0;
		int usableStart = 0;
		int usableSize = 0;

		for (Rule r : wta.getSourceRules()) {
			usableRules.add(r);
//			usable.add(r);
			ruleWeights[r.getID()] = r.getWeight();
			usableSize++;
		}

		while (nOfDefined < nOfStates) {
			for (int i = usableStart; i < usableSize; i++) {
//			ListIterator<Rule> it = usable.listIterator();
//			while (it.hasNext()) {
				Rule r = usableRules.get(i);
//				Rule r = it.next();
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

//				it.remove();
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
					ruleWeights[r2.getID()] = r2.getWeight();
				}
				
				for (State s : r2.getStates()) {
					if (s.getID() == state.getID()) {
						missingIndices[r2.getID()]--;
						ruleWeights[r2.getID()] = ruleWeights[r2.getID()].mult(
								defined[s.getID()]);
					}
				}

				if (missingIndices[r2.getID()] == 0) {
					usableRules.add(r2);
//					usable.add(r2);
					usableSize++;
				}
			}
		}

		return defined;
	}

//	@SuppressWarnings("unchecked")
//	private static Weight[] computeBestTrees() {
//		int nOfStates = wta.getStateCount();
//		int nOfRules = wta.getRuleCount();
//		queue = new BinaryHeap<>();
//		qElems = new Node[nOfStates + 1];
//		defined = new Weight[nOfStates + 1];
//		usableRules = new ArrayList<>(nOfRules);
//		Weight[] ruleWeights = new Weight[nOfRules];
//		Integer[] missingIndices = new Integer[nOfRules];
//		int nOfDefined = 0;
//		int usableStart = 0;
//		int usableSize = 0;
//
//		for (Rule r : wta.getSourceRules()) {
//			usableRules.add(r);
//			usableSize++;
//		}
//
//		while (nOfDefined < nOfStates) {
//			for (int i = usableStart; i < usableSize; i++) {
//				Rule r = usableRules.get(i);
//				State resState = r.getResultingState();
//				Weight oldWeight = null;
//				Weight newWeight = getWeight(r);
//				BinaryHeap<State, Weight>.Node element = qElems[resState.getID()];
//				
//				if (element == null) {
//					element = queue.createNode(resState);
//					qElems[resState.getID()] = element;
//				}
//
//				oldWeight = element.getWeight();
//
//				if (oldWeight == null) {
//					queue.insert(element, newWeight);
//				} else if (newWeight.compareTo(oldWeight) < 0) {
//					queue.decreaseWeight(element, newWeight);
//				}
//
//				usableStart++;
//			}
//
//			BinaryHeap<State, Weight>.Node element = queue.dequeue();
//			State state = element.getObject();
//			Weight weight = element.getWeight();
//			defined[state.getID()] = weight;
//			nOfDefined++;
//
//			for (Rule r2 : state.getOutgoing()) {
//				if (missingIndices[r2.getID()] == null) {
//					missingIndices[r2.getID()] = r2.getNumberOfStates();
//				}
//
//				if (defined[state.getID()] != null) {
//					
//					// TODO: unnecessary and inefficient
////					missingIndices[r2.getID()] = missingIndices[r2.getID()] -
////							r2.getIndexOfState(state).size();
//					
//					for (State s : r2.getStates()) {
//						if (s.equals(state)) {
//							missingIndices[r2.getID()]--;
//						}
//					}
//					
//					if (missingIndices[r2.getID()] == 0) {
//						usableRules.add(r2);
//						usableSize++;
//					}
//				}
//			}
//		}
//
//		return defined;
//	}

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
	private static Weight[] computeBestContexts(Weight[] cheapestTrees) {
		int nOfStates = wta.getStateCount();
		queue = new BinaryHeap<>();
		defined = new Weight[nOfStates + 1];
		qElems = new Node[nOfStates + 1];
		usableRules = new ArrayList<>();
//		usable = new LinkedList<Rule>();

		int nOfDefined = 0;
		boolean done = false;
		int usableStart = 0;
		int usableSize = 0;

		for (State s : wta.getFinalStates()) {
			defined[s.getID()] = wta.getSemiring().one();
			nOfDefined++;
			
			for (Rule r : s.getIncoming()) {
				usableRules.add(r);
//				usable.add(r);
				usableSize++;
			}
		}

		while (!done && nOfDefined < nOfStates) {
			for (int k = usableStart; k < usableSize; k++) {
//			ListIterator<Rule> it = usable.listIterator();
//			while (it.hasNext()) {
				Rule r = usableRules.get(k);
//				Rule r = it.next();
				State resState = r.getResultingState();
				ArrayList<State> stateList = r.getStates();
				int listSize = stateList.size();

				for (int i = 0; i < listSize; i++) {
					State s = stateList.get(i);
					BinaryHeap<State, Weight>.Node element = qElems[s.getID()];
					
					if (element == null) {
						element = queue.createNode(s);
						qElems[s.getID()] = element;
					}

					Weight newWeight = r.getWeight().mult(
							defined[resState.getID()]);
					Weight oldWeight = element.getWeight();

					for (int j = 0; j < listSize; j++) {
						State s2 = stateList.get(j);
						if (i != j) {
							newWeight = newWeight.mult(
									cheapestTrees[s2.getID()]);
						}
					}

					if (oldWeight == null) {
						queue.insert(element, newWeight);
					} else if (newWeight.compareTo(oldWeight) < 0) {
						queue.decreaseWeight(element, newWeight);
					}
				}

//				it.remove();
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
//					usable.add(r);
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
