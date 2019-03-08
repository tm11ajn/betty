package se.umu.cs.flp.aj.knuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.PriorityQueue;

import se.umu.cs.flp.aj.heap.BinaryHeap;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.WTA;

public class KnuthSmallestDerivations {

	private static WTA wta;
//	private static BinaryHeap<State, Weight> queue;
	private static PriorityQueue<StateWeight> queue;
	private static HashMap<State, Weight> defined;
	private static LinkedList<Rule> usableRules;
	private static HashMap<State, Weight> totalWeight;

	private static HashMap<State, Rule> bestRules = new HashMap<>();

	public static HashMap<State, Weight> getSmallestDerivations(WTA wta) {
		KnuthSmallestDerivations.wta = wta;
		HashMap<State, Weight> temp = computeCheapestTrees();
		return computeCheapestContexts(temp);
	}

	private static class StateWeight implements Comparable<StateWeight> {
		private State state;
		private Weight weight;
		public StateWeight(State state, Weight weight) {
			super();
			this.state = state;
			this.weight = weight;
		}
		public State getState() {
			return state;
		}
		public void setState(State state) {
			this.state = state;
		}
		public Weight getWeight() {
			return weight;
		}
		public void setWeight(Weight weight) {
			this.weight = weight;
		}
		@Override
		public int compareTo(StateWeight arg0) {

			int weightComparison = this.weight.compareTo(arg0.weight);

			if (weightComparison == 0) {
				return this.state.compareTo(arg0.state);
			}

			return weightComparison;
		}
		@Override
		public boolean equals(Object arg0) {

			if (!(arg0 instanceof StateWeight)) {
				return false;
			}

			return this.compareTo((StateWeight)arg0) == 0;
		}
	}

	private static HashMap<State, Weight> computeCheapestTrees() {
		//queue = new BinaryHeap<>();
		queue = new PriorityQueue<>();
		defined = new HashMap<>();
		usableRules = new LinkedList<>();
		totalWeight = new HashMap<>();
		HashMap<Rule, Integer> missingIndices = new HashMap<>();
		HashMap<Rule, HashMap<State, State>> seenStates = new HashMap<>();

//System.out.println("Init: ");
		for (Rule r : wta.getSourceRules()) {
			usableRules.add(r);
//System.out.println("Add " + r + " to usablerules init");
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

					queue.add(new StateWeight(resState, newWeight));

//					if (!queue.contains(resState)) {
//						queue.add(resState, newWeight);
//					} else {
//						queue.decreaseWeight(resState, newWeight);
//					}

					totalWeight.put(resState, newWeight);

//					if (resState.isFinal()) {
					bestRules.put(resState, r);
//					}
				}

				it.remove();
			}

//			BinaryHeap<State, Weight>.Node<State, Weight> element =
//					queue.dequeue();
			StateWeight element = null;
			boolean found = false;

			while (!found) {
				element = queue.poll();

				if (!defined.containsKey(element.getState())) {
					found= true;
				}
			}

//			State state = element.getObject();
			State state = element.getState();
			Weight weight = element.getWeight();
			defined.put(state, weight);
System.out.println("Defines treeweight of " + state + " as " + weight );

//			for (Rule r2 : wta.getRulesByState(state)) {
			for (Rule r2 : state.getOutgoing()) {
				if (missingIndices.get(r2) == null) {
//					missingIndices.put(r2, r2.getRank());
//					missingIndices.put(r2, r2.getStates().size());
					missingIndices.put(r2, r2.getNumberOfStates());
					seenStates.put(r2, new HashMap<>());
				}

				if (!seenStates.get(r2).containsKey(state)) {
					missingIndices.put(r2, missingIndices.get(r2) -
							r2.getIndexOfState(state).size());

					if (missingIndices.get(r2) == 0) {
						usableRules.addLast(r2);
//System.out.println("Adds " + r2 + " to usable rules");
					}

					seenStates.get(r2).put(state, state);
				}
			}
		}

//System.out.println("Best tree rules: ");
//for (Rule v : bestRules.values()) {
//System.out.println(v);
//}

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
//		queue = new BinaryHeap<>();
		queue = new PriorityQueue<>();
		defined = new HashMap<>();
		usableRules = new LinkedList<>();
		totalWeight = new HashMap<>();
//		HashMap<State, Boolean> wasUsed = new HashMap<>();

		for (State s : wta.getFinalStates()) {
			defined.put(s, wta.getSemiring().one());
			totalWeight.put(s, wta.getSemiring().one());

//			for (Rule r : wta.getRulesByResultingState(s)) {
			for (Rule r : s.getIncoming()) {
				usableRules.add(r);
			}
		}

//		int nOfStates = wta.getStates().size();
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

						queue.add(new StateWeight(s, newWeight));

//						if (!queue.contains(s)) {
//							queue.add(s, newWeight);
//						} else {
//							queue.decreaseWeight(s, newWeight);
//						}

						totalWeight.put(s, newWeight);
					}
				}

				it.remove();
			}

			StateWeight element = null;
			boolean found = false;

			while (!found) {

				if (queue.isEmpty()) {
					break;
				}

				element = queue.poll();

				if (!defined.containsKey(element.getState()) || element.getState().isFinal()) {
					found = true;
				}
			}

//			if (!queue.empty()) {
//			if (!queue.isEmpty()) {
			if (found) {
//				BinaryHeap<State, Weight>.Node<State, Weight> element = queue.dequeue();
//				StateWeight element = queue.poll();

//				State state = element.getObject();
				State state = element.getState();
				Weight weight = element.getWeight();
				defined.put(state, weight);
System.out.println("Defines weight of " + state + " as " + weight );

//				for (Rule r : wta.getRulesByResultingState(state)) {
				for (Rule r : state.getIncoming()) {
					usableRules.addLast(r);
				}

			} else {
				done = true;

				ArrayList<State> states = new ArrayList<>(cheapestTrees.keySet());

				for (State s : states) {
					if (!defined.containsKey(s)) {
//						defined.put(s, wta.getSemiring().zero());
						wta.removeState(s);
					}
				}
			}
		}

		return defined;
	}
}
