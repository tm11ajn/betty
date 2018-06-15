package se.umu.cs.flp.aj.knuth;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.Symbol;
import se.umu.cs.flp.aj.nbest.wta.WTA;

public class KnuthSmallestDerivations {

	private static WTA wta;

	public static class QueueElement<V> implements Comparable<QueueElement<V>> {

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
		public int hashCode() {
			return value.hashCode();
		}

		@Override
		public int compareTo(QueueElement<V> arg0) {
			return this.weight.compareTo(arg0.weight);
		}
	}

	public static HashMap<State, Weight> getSmallestDerivations(WTA wta) {
		KnuthSmallestDerivations.wta = wta;
		return computeCheapestContexts(computeCheapestTrees());
	}

	private static HashMap<State, Weight> computeCheapestTrees() {
		PriorityQueue<QueueElement<State>> queue = new PriorityQueue<>();
		HashMap<State, Weight> defined = new HashMap<>();
		HashMap<Rule<Symbol>, Integer> missingIndices = new HashMap<>();
		LinkedList<Rule<Symbol>> usableRules = new LinkedList<>();
		HashMap<State, Weight> totalWeight = new HashMap<>();

		for (Rule<Symbol> r : wta.getSourceRules()) {
			usableRules.add(r);
		}

		int nOfStates = wta.getStates().size();

		while (defined.size() < nOfStates) {

			for (Rule<Symbol> r : usableRules) {
				State resState = r.getResultingState();

				Weight oldWeight = totalWeight.get(
						r.getResultingState());
				Weight newWeight = getWeight(r, defined);

				if (oldWeight == null ||
						newWeight.compareTo(oldWeight) < 0) {
					queue.add(new QueueElement<State>(resState, newWeight));
					totalWeight.put(resState, newWeight);
				}
			}

			QueueElement<State> element = queue.poll();
			State state = element.getValue();
			Weight weight = element.getWeight();
			defined.put(state, weight);

			for (Rule<Symbol> r2 : wta.getRulesByState(state)) {
				if (missingIndices.get(r2) == null) {
					missingIndices.put(r2, r2.getRank());
				}

				missingIndices.put(r2, missingIndices.get(r2) -
						r2.getIndexOfState(state).size());

				if (missingIndices.get(r2) == 0) {
					usableRules.addLast(r2);
				}
			}
		}

		return defined;
	}

	private static Weight getWeight(Rule<Symbol> rule,
			HashMap<State, Weight> defined) {
		Weight result = rule.getWeight();

		for (State s : rule.getStates()) {
			result.mult(defined.get(s));
		}

		return result;
	}

	private static HashMap<State, Weight> computeCheapestContexts(
			HashMap<State, Weight> cheapestTrees) {
		PriorityQueue<QueueElement<State>> queue = new PriorityQueue<>();
		HashMap<State, Weight> defined = new HashMap<>();

		LinkedList<Rule<Symbol>> usableRules = new LinkedList<>();
		HashMap<State, Weight> totalWeight = new HashMap<>();

		for (State s : wta.getFinalStates()) {
			defined.put(s, wta.getSemiring().one());

			for (Rule<Symbol> r : wta.getRulesByResultingState(s)) {
				usableRules.add(r);
			}
		}

		int nOfStates = wta.getStates().size();

		while (defined.size() < nOfStates) {

			for (Rule<Symbol> r : usableRules) {
				State resState = r.getResultingState();
				Weight newWeight = r.getWeight().mult(defined.get(resState));

				for (State s : r.getStates()) {
					Weight oldWeight = totalWeight.get(s);

					for (State s2 : r.getStates()) {
						if (!s2.equals(s)) {
							newWeight = newWeight.mult(cheapestTrees.get(s2));
						}
					}

					if (oldWeight == null ||
							newWeight.compareTo(oldWeight) < 0) {
						queue.add(new QueueElement<>(s, newWeight));
						totalWeight.put(s, newWeight);
					}
				}
			}

			QueueElement<State> element = queue.poll();
			State state = element.getValue();
			Weight weight = element.getWeight();
			defined.put(state, weight);

			for (Rule<Symbol> r : wta.getRulesByResultingState(state)) {
				usableRules.addLast(r);
			}
		}


		return defined;
	}

}
