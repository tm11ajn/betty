package se.umu.cs.flp.aj.knuth;

import java.util.HashMap;

import se.umu.cs.flp.aj.heap.BinaryHeap;
import se.umu.cs.flp.aj.wta.Rule;
import se.umu.cs.flp.aj.wta.State;
import se.umu.cs.flp.aj.wta.Symbol;
import se.umu.cs.flp.aj.wta.WTA;
import se.umu.cs.flp.aj.wta.Weight;

public class KnuthSmallestDerivation {

	private WTA wta;
	private BinaryHeap<State, Weight> queue;
	private HashMap<State, Weight> defined;
	private State smallest;

	public KnuthSmallestDerivation(WTA wta) {
		this.wta = wta;
		this.queue = new BinaryHeap<>();
		this.defined = new HashMap<>();
		this.smallest = null;
	}

	public Weight getSmallestDerivation2() {
		HashMap<String, State> states = wta.getStates();
		boolean isFound = false;

		enqueueFirstLayer();
		isFound = update(states);

		while (states.size() > 0 && !isFound) {

			enqueueNextLayer();
			isFound = update(states);
		}

		return queue.getWeight(smallest);
	}

	private void enqueueFirstLayer() {

		for (Symbol s : wta.getSymbols()) {

			if (s.getRank() == 0) {

				for (Rule r : wta.getTransitionFunction().getRulesBySymbol(s)) {
					updateQueue(r);
				}
			}
		}
	}

	private void enqueueNextLayer() {

		for (Rule r : wta.getTransitionFunction().getRulesByState(
				smallest)) {
			updateQueue(r);
		}
	}

	private void updateQueue(Rule r) {
		State resultingState = r.getResultingState();
		Weight currentWeight = queue.getWeight(resultingState);
		Weight newWeight = getNewWeight(r);

		if (currentWeight == null &&
				newWeight.compareTo(new Weight(Weight.INF)) < 0) {
			queue.add(resultingState, newWeight);
		} else if (currentWeight != null &&
				newWeight.compareTo(currentWeight) < 0) {
			queue.decreaseWeight(resultingState, newWeight);
		}
	}

	private Weight getNewWeight(Rule r) {
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
		return newWeight;
	}

	private boolean update(HashMap<String, State> states) {
		smallest = queue.min();

		if (smallest.isFinal()) {
			return true;
		}

		defined.put(smallest, queue.getWeight(smallest));
		states.remove(smallest.getLabel());
		queue.dequeue();

		return false;
	}

}
