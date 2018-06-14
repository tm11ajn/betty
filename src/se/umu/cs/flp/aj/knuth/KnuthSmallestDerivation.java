package se.umu.cs.flp.aj.knuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

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

	private PriorityQueue<QueueElement<Rule<Symbol>>> ruleQueue;
	private HashMap<Rule<Symbol>, Integer> coveredStates;
	private HashMap<Rule<Symbol>, Weight> totalWeight;
	private HashMap<Rule<Symbol>, Boolean> seen;

	public KnuthSmallestDerivation(WTA wta) {
		this.wta = wta;
		this.queue = new BinaryHeap<>();
		this.defined = new HashMap<>();
		this.smallest = null;

		this.ruleQueue = new PriorityQueue<>();
		this.coveredStates = new HashMap<>();
		this.totalWeight = new HashMap<>();
		this.seen = new HashMap<>();
	}

	public class QueueElement<V> implements Comparable<QueueElement<V>> {

		private V value;
		private Weight weight;

		public QueueElement(V value, Weight weight) {
			this.value = value;
			this.weight = weight;
		}

		public V getValue() {
			return value;
		}

		public Weight getWeight() {
			return weight;
		}

		@Override
		public int compareTo(QueueElement<V> arg0) {
			return this.weight.compareTo(arg0.weight);
		}
	}

	public Weight getSmallestDerivation2() {

		for (Rule<Symbol> r : wta.getSourceRules()) {
			ruleQueue.add(new QueueElement<Rule<Symbol>>(r, r.getWeight()));
			totalWeight.put(r, r.getWeight());
			seen.put(r, false);
		}

		boolean found = false;

		while (!found) {

			QueueElement<Rule<Symbol>> currentElement = ruleQueue.poll();
			Rule<Symbol> currentRule = currentElement.getValue();
			Weight currentWeight = currentElement.getWeight();
			State resultingState = currentRule.getResultingState();
			seen.put(currentRule, true);

			if (resultingState.isFinal()) {
				return totalWeight.get(currentRule);
			}

			ArrayList<Rule<Symbol>> nextRules =
					wta.getRulesByState(resultingState);

			for (Rule<Symbol> r : nextRules) {

				if (seen.get(r) == null || !seen.get(r)) {
					ArrayList<Integer> indices =
							r.getIndexOfState(resultingState);
					int nOfIndices = indices.size();
					int counter = 0;

					while (counter < nOfIndices) {

						if (!coveredStates.containsKey(r)) {
							coveredStates.put(r, 1);
						} else {
							coveredStates.put(r, coveredStates.get(r) + 1);
						}

						if (!totalWeight.containsKey(r)) {
							totalWeight.put(r, currentWeight);
						} else {
							totalWeight.put(r, totalWeight.get(r).mult(currentWeight));
						}

						counter++;
					}

					if (coveredStates.get(r) == r.getRank()) {
						ruleQueue.add(new QueueElement<>(r, r.getWeight()));
						seen.put(r, false);
					}
				}
			}

		}

		return null;
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
		for (Rule<Symbol> r : wta.getSourceRules()) {
			updateQueue(r);
		}
	}

	private void enqueueNextLayer() {

		for (Rule<Symbol> r : wta.getRulesByState(smallest)) {
			updateQueue(r);
		}
	}

	private void updateQueue(Rule<Symbol> r) {
		State resultingState = r.getResultingState();
		Weight currentWeight = queue.getWeight(resultingState);
		Weight newWeight = getNewWeight(r);
		Semiring semiring = wta.getSemiring();

		if (currentWeight == null &&
				newWeight.compareTo(semiring.zero()) < 0) {
			queue.add(resultingState, newWeight);
		} else if (currentWeight != null &&
				newWeight.compareTo(currentWeight) < 0) {
			queue.decreaseWeight(resultingState, newWeight);
		}
	}

	private Weight getNewWeight(Rule<Symbol> r) {
		Semiring semiring = wta.getSemiring();
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
