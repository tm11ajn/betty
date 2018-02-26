package se.umu.cs.flp.aj.knuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import se.umu.cs.flp.aj.wta.Rule;
import se.umu.cs.flp.aj.wta.State;
import se.umu.cs.flp.aj.wta.TransitionFunction;
import se.umu.cs.flp.aj.wta.WTA;
import se.umu.cs.flp.aj.wta.Weight;

public class KnuthSmallestDerivations {

	private WTA wta;

	public KnuthSmallestDerivations(WTA wta) {
		this.wta = wta;
	}

	public HashMap<State, StateHolder> getSmallestDerivation() {
		PriorityQueue<StateHolder> my = new PriorityQueue<>();
		HashMap<State, Weight> v = new HashMap<>();
		HashMap<State, StateHolder> defined = new HashMap<>();
		HashMap<String, State> states = wta.getStates();
		TransitionFunction transitionFunc = wta.getTransitionFunction();
		int nOfStates = states.size();
		State mostRecentlyAddedState = null;

//System.out.println("Defined: " + defined);
//System.out.println("States: " + states);

		while (defined.size() < nOfStates) {

			for (State state : states.values()) {
				ArrayList<Rule> transitions =
						transitionFunc.getRulesByResultingState(state);
				Weight currentWeight = v.get(state);

				if (currentWeight == null) {
					currentWeight = new Weight(Weight.INF);
				}

//System.out.println("Current weight: " + currentWeight);
//System.out.println("Most recently added state: " + mostRecentlyAddedState);

				for (Rule transition : transitions) {

//System.out.println("Transition: " + transition);

					if (mostRecentlyAddedState == null ||
							transition.getStates().contains(
									mostRecentlyAddedState)) {
						Weight newWeight = new Weight(0);

						for (State stateInRule : transition.getStates()) {

//System.out.println("State in rule: " + stateInRule);

							if (defined.containsKey(stateInRule)) {
								newWeight = newWeight.add(defined.
										get(stateInRule).getWeight());
							} else {
								newWeight = newWeight.add(new Weight(Weight.INF));
							}
						}

						newWeight = newWeight.add(transition.getWeight());

//System.out.println("New weight: " + newWeight);

						if (newWeight.compareTo(currentWeight) < 0) {
							v.replace(state, newWeight);
							my.add(new StateHolder(state, newWeight));
//System.out.println("Adding state to MY: " + state + ": " + newWeight);
						}
					}
				}
			}

			StateHolder smallest = my.poll();
//System.out.println("SMALLEST = " + smallest);
			while (!my.isEmpty() && defined.containsKey(smallest.getState())) {
				smallest = my.poll();
			}

			if (smallest == null) {
				return defined;
			}

			mostRecentlyAddedState = smallest.getState();
			defined.put(smallest.getState(), smallest);
			states.remove(smallest.getState().getLabel());
//System.out.println("Adding state to D: " + mostRecentlyAddedState + " with weight " + smallest.getWeight());
//System.out.println("Defined now: " + defined);
//System.out.println("States now: " + states);

		}

		return defined;
	}
}
