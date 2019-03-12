package se.umu.cs.flp.aj.knuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import se.umu.cs.flp.aj.heap.BinaryHeap;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.WTA;

public class KnuthSmallestDerivations {

	private static WTA wta;
	private static BinaryHeap<State, Weight> queue;
	private static HashMap<State, Weight> defined;
	private static LinkedList<Rule> usableRules;
	private static HashMap<State, Weight> totalWeight;

	private static HashMap<State, Rule> bestRules = new HashMap<>();

	public static HashMap<State, Weight> getSmallestDerivations(WTA wta) {
		KnuthSmallestDerivations.wta = wta;
		HashMap<State, Weight> temp = computeCheapestTrees();
		return computeCheapestContexts(temp);
	}

	private static HashMap<State, Weight> computeCheapestTrees() {
		queue = new BinaryHeap<>();
		defined = new HashMap<>();
		usableRules = new LinkedList<>();
		totalWeight = new HashMap<>();
		HashMap<Rule, Integer> missingIndices = new HashMap<>();
		HashMap<Rule, HashMap<State, State>> seenStates = new HashMap<>();

		for (Rule r : wta.getSourceRules()) {
			usableRules.add(r);
		}

		int nOfStates = wta.getStateCount();

		while (defined.size() < nOfStates) {
			ListIterator<Rule> it = usableRules.listIterator();

			while (it.hasNext()) {
				Rule r = it.next();
				State resState = r.getResultingState();
				Weight oldWeight = totalWeight.get(resState);
				Weight newWeight = getWeight(r);

				if (oldWeight == null ||
						newWeight.compareTo(oldWeight) < 0) {

					if (!queue.contains(resState)) {
						queue.add(resState, newWeight);
					} else {
						queue.decreaseWeight(resState, newWeight);
					}

					totalWeight.put(resState, newWeight);
					bestRules.put(resState, r);
				}

				it.remove();
			}

			BinaryHeap<State, Weight>.Node element =
					queue.dequeue();

			State state = element.getObject();
			Weight weight = element.getWeight();
			defined.put(state, weight);

			for (Rule r2 : state.getOutgoing()) {
				if (missingIndices.get(r2) == null) {
					missingIndices.put(r2, r2.getNumberOfStates());
					seenStates.put(r2, new HashMap<>());
				}

				if (!seenStates.get(r2).containsKey(state)) {
					missingIndices.put(r2, missingIndices.get(r2) -
							r2.getIndexOfState(state).size());

					if (missingIndices.get(r2) == 0) {
						usableRules.addLast(r2);
					}

					seenStates.get(r2).put(state, state);
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
			result = result.mult(defined.get(s));
		}

		return result;
	}

	private static HashMap<State, Weight> computeCheapestContexts(
			HashMap<State, Weight> cheapestTrees) {
		queue = new BinaryHeap<>();
		defined = new HashMap<>();
		usableRules = new LinkedList<>();
		totalWeight = new HashMap<>();

		for (State s : wta.getFinalStates()) {
			defined.put(s, wta.getSemiring().one());
			totalWeight.put(s, wta.getSemiring().one());

			for (Rule r : s.getIncoming()) {
				usableRules.add(r);
			}
		}

		int nOfStates = wta.getStateCount();
		boolean done = false;

		while (!done && defined.size() < nOfStates) {
			ListIterator<Rule> it = usableRules.listIterator();

			while (it.hasNext()) {
				Rule r = it.next();
				State resState = r.getResultingState();
				ArrayList<State> stateList = r.getStates();
				int listSize = stateList.size();

				for (int i = 0; i < listSize; i++) {
					State s = stateList.get(i);
					Weight newWeight = r.getWeight().mult(defined.get(resState));
					Weight oldWeight = totalWeight.get(s);

					for (int j = 0; j < listSize; j++) {
						State s2 = stateList.get(j);
						if (i != j) {
							newWeight = newWeight.mult(cheapestTrees.get(s2));
						}
					}

					if (oldWeight == null ||
							newWeight.compareTo(oldWeight) < 0) {

						if (!queue.contains(s)) {
							queue.add(s, newWeight);
						} else {
							queue.decreaseWeight(s, newWeight);
						}

						totalWeight.put(s, newWeight);
					}
				}

				it.remove();
			}

			if (!queue.empty()) {
				BinaryHeap<State, Weight>.Node element = queue.dequeue();
				State state = element.getObject();
				Weight weight = element.getWeight();
				defined.put(state, weight);

				for (Rule r : state.getIncoming()) {
					usableRules.addLast(r);
				}

			} else {
				done = true;

				ArrayList<State> states = new ArrayList<>(cheapestTrees.keySet());

				for (State s : states) {
					if (!defined.containsKey(s)) {
						wta.removeState(s);
					}
				}
			}
		}

		return defined;
	}
}
