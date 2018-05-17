package se.umu.cs.flp.aj.knuth;

import java.util.HashMap;

import se.umu.cs.flp.aj.heap.BinaryHeap;
import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.Symbol;
import se.umu.cs.flp.aj.nbest.wta.WTA;

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

	public Weight getSmallestDerivation() {
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

				for (Rule<Symbol> r : wta.getTransitionFunction().getRulesBySymbol(s)) {
					updateQueue(r);
				}
			}
		}
	}

	private void enqueueNextLayer() {

		for (Rule<Symbol> r : wta.getTransitionFunction().getRulesByState(
				smallest)) {
			updateQueue(r);
		}
	}

	private void updateQueue(Rule<Symbol> r) {
		State resultingState = r.getResultingState();
		Weight currentWeight = queue.getWeight(resultingState);
		Weight newWeight = getNewWeight(r);
		Semiring semiring = wta.getTransitionFunction().getSemiring();

		if (currentWeight == null &&
				newWeight.compareTo(semiring.zero()) < 0) {
			queue.add(resultingState, newWeight);
		} else if (currentWeight != null &&
				newWeight.compareTo(currentWeight) < 0) {
			queue.decreaseWeight(resultingState, newWeight);
		}
	}

	private Weight getNewWeight(Rule<Symbol> r) {
		Semiring semiring = wta.getTransitionFunction().getSemiring();
		Weight newWeight = semiring.one();

		for (State stateInRule : r.getStates()) {

			if (defined.containsKey(stateInRule)) {
				newWeight = newWeight.mult(defined.
						get(stateInRule));
			} else {
				newWeight = newWeight.mult(
						semiring.zero());
			}
		}

		newWeight = newWeight.mult(r.getWeight());
		return newWeight;
	}

	private boolean update(HashMap<String, State> states) {
		smallest = queue.peek();

		if (smallest.isFinal()) {
			return true;
		}

		defined.put(smallest, queue.getWeight(smallest));
		states.remove(smallest.getLabel());
		queue.dequeue();

		return false;
	}

}
