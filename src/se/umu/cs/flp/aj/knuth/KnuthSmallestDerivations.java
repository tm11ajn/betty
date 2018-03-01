package se.umu.cs.flp.aj.knuth;

import java.util.ArrayList;
import java.util.HashMap;

import se.umu.cs.flp.aj.heap.BinaryHeap;
import se.umu.cs.flp.aj.wta.Rule;
import se.umu.cs.flp.aj.wta.State;
import se.umu.cs.flp.aj.wta.Symbol;
import se.umu.cs.flp.aj.wta.TransitionFunction;
import se.umu.cs.flp.aj.wta.WTA;
import se.umu.cs.flp.aj.wta.Weight;

public class KnuthSmallestDerivations {

	private WTA wta;
//	private PriorityMap<State, Weight> my;
//	private FibonacciHeap<State, Weight> my;
	private BinaryHeap<State, Weight> my;
	private HashMap<State, Weight> defined;

	public KnuthSmallestDerivations(WTA wta) {
		this.wta = wta;
//		this.my = new PriorityMap<>();
//		this.my = new FibonacciHeap<>();
		this.my = new BinaryHeap<>();
		this.defined = new HashMap<>();
	}

	public Weight getSmallestDerivation() {
		HashMap<String, State> states = wta.getStates();
		TransitionFunction transitionFunc = wta.getTransitionFunction();
		int nOfStates = states.size();
		State mostRecentlyAddedState = null;

		while (defined.size() < nOfStates) {

			for (State state : states.values()) {
				ArrayList<Rule> transitions =
						transitionFunc.getRulesByResultingState(state);
				Weight currentWeight = my.getWeight(state);

				if (currentWeight == null) {
					currentWeight = new Weight(Weight.INF);

					my.add(state, currentWeight);
				}

				for (Rule transition : transitions) {

					if (mostRecentlyAddedState == null ||
							transition.hasState(
									mostRecentlyAddedState)) {
						Weight newWeight = new Weight(0);

						for (State stateInRule : transition.getStates()) {

							if (defined.containsKey(stateInRule)) {
								newWeight = newWeight.add(defined.
										get(stateInRule));
							} else {
								newWeight = newWeight.add(
										new Weight(Weight.INF));
							}
						}

						newWeight = newWeight.add(transition.getWeight());

						if (newWeight.compareTo(currentWeight) < 0) {
							my.decreaseWeight(state, newWeight);
							currentWeight = newWeight;
						}
					}
				}
			}

			State smallest = my.min();

			if (smallest.isFinal()) {
				return my.getWeight(smallest);
			}

			mostRecentlyAddedState = smallest;
			defined.put(smallest, my.getWeight(smallest));
			my.dequeue();
			states.remove(smallest.getLabel());

		}

		return null;
	}


	public Weight getSmallestDerivation2() {
		HashMap<String, State> states = wta.getStates();
		State mostRecentlyAddedState = null;
		State smallest = null;
		boolean first = true;

		while (states.size() > 0) {

			if (first) {
				enqueueFirstLayer();
				first = false;
			} else {
				enqueueNextLayer(mostRecentlyAddedState);
			}

			smallest = my.min();

			if (smallest.isFinal()) {
				return my.getWeight(smallest);
			}

			mostRecentlyAddedState = smallest;
			defined.put(smallest, my.getWeight(smallest));
//			my.poll();
			my.dequeue();
			states.remove(smallest.getLabel());
//System.out.println("Defined: " + defined);
//System.out.println("States: " + states);
		}

		return null;
	}

	private void enqueueFirstLayer() {

		for (Symbol s : wta.getSymbols()) {

			if (s.getRank() == 0) {

				for (Rule r : wta.getTransitionFunction().getRulesBySymbol(s)) {
					State resultingState = r.getResultingState();
					Weight currentWeight = my.getWeight(resultingState);
					Weight newWeight = r.getWeight();

					if (currentWeight == null &&
							newWeight.compareTo(new Weight(Weight.INF)) < 0) {
						my.add(resultingState, newWeight);
					} else if (currentWeight != null &&
							newWeight.compareTo(currentWeight) < 0) {
						my.decreaseWeight(resultingState, newWeight);
					}
				}
			}
		}
	}

	private void enqueueNextLayer(State mostRecentlyAddedState) {

		for (Rule r : wta.getTransitionFunction().getRulesByState(
				mostRecentlyAddedState)) {
			State resultingState = r.getResultingState();
			Weight currentWeight = my.getWeight(resultingState);
			Weight newWeight = new Weight(0);

			for (State stateInRule : r.getStates()) {

				if (defined.containsKey(stateInRule)) {
					newWeight = newWeight.add(defined.
							get(stateInRule));
				} else {
					newWeight = newWeight.add(
							new Weight(Weight.INF));
				}
			}

			newWeight = newWeight.add(r.getWeight());

			if (currentWeight == null &&
					newWeight.compareTo(new Weight(Weight.INF)) < 0) {
				my.add(resultingState, newWeight);
			} else if (currentWeight != null &&
					newWeight.compareTo(currentWeight) < 0) {
				my.decreaseWeight(resultingState, newWeight);
			}
		}
	}
}
