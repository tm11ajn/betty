package se.umu.cs.flp.aj.knuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import se.umu.cs.flp.aj.heap.BinaryHeap;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.Symbol;
import se.umu.cs.flp.aj.nbest.wta.WTA;

public class KnuthSmallestDerivations {

	private static WTA wta;
	private static BinaryHeap<State, Weight> queue;
	private static HashMap<State, Weight> defined;
	private static LinkedList<Rule<Symbol>> usableRules;
	private static HashMap<State, Weight> totalWeight;

//	public static class QueueElement<V extends Comparable<V>> implements Comparable<QueueElement<V>> {
//
//		private V value;
//		private Weight weight;
//
//		public QueueElement(V value, Weight weight) {
//			this.value = value;
//			this.weight = weight;
//		}
//
//		public V getValue() {
//			return value;
//		}
//
//		public Weight getWeight() {
//			return weight;
//		}
//
//		@Override
//		public int hashCode() {
//			return value.hashCode();
//		}
//
//		@Override
//		public int compareTo(QueueElement<V> arg0) {
//			int weightComparison = this.weight.compareTo(arg0.weight);
//
//			if (weightComparison == 0) {
//				return this.value.compareTo(arg0.value);
//			}
//
//			return weightComparison;
//		}
//
//		@Override
//		public boolean equals(Object arg0) {
//
//			if (!(arg0 instanceof QueueElement<?>)) {
//				return false;
//			}
//
//			QueueElement<?> o = (QueueElement<?>) arg0;
//
//			return value.equals(o.value) && weight.equals(o.weight);
//		}
//
//		@Override
//		public String toString() {
//			return value.toString() + " # " +  weight.toString();
//		}
//	}

	public static HashMap<State, Weight> getSmallestDerivations(WTA wta) {
		KnuthSmallestDerivations.wta = wta;
		return computeCheapestContexts(computeCheapestTrees());
	}

	private static HashMap<State, Weight> computeCheapestTrees() {
		queue = new BinaryHeap<>();
		defined = new HashMap<>();
		usableRules = new LinkedList<>();
		totalWeight = new HashMap<>();
		HashMap<Rule<Symbol>, Integer> missingIndices = new HashMap<>();
		HashMap<Rule<Symbol>, HashMap<State, State>> seenStates = new HashMap<>();

		for (Rule<Symbol> r : wta.getSourceRules()) {
			usableRules.add(r);
		}

		int nOfStates = wta.getStates().size();

		while (defined.size() < nOfStates) {
			ListIterator<Rule<Symbol>> it = usableRules.listIterator();

			while (it.hasNext()) {
				Rule<Symbol> r = it.next();
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
				}

				it.remove();
			}

			BinaryHeap<State, Weight>.Node<State, Weight> element =
					queue.dequeue();
			State state = element.getObject();
			Weight weight = element.getWeight();
			defined.put(state, weight);

			for (Rule<Symbol> r2 : wta.getRulesByState(state)) {
				if (missingIndices.get(r2) == null) {
					missingIndices.put(r2, r2.getRank());
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

	private static Weight getWeight(Rule<Symbol> rule) {
		Weight result = rule.getWeight();
		ArrayList<State> states = rule.getStates();

		if (states.size() == 0) {
			result = result.mult(wta.getSemiring().one());
		}

		for (State s : rule.getStates()) {
			result.mult(defined.get(s));
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

			for (Rule<Symbol> r : wta.getRulesByResultingState(s)) {
				usableRules.add(r);
			}
		}

		int nOfStates = wta.getStates().size();

		while (defined.size() < nOfStates) {
			ListIterator<Rule<Symbol>> it = usableRules.listIterator();

			while (it.hasNext()) {
				Rule<Symbol> r = it.next();
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

			BinaryHeap<State, Weight>.Node<State, Weight> element = queue.dequeue();
			State state = element.getObject();
			Weight weight = element.getWeight();
			defined.put(state, weight);

			for (Rule<Symbol> r : wta.getRulesByResultingState(state)) {
				usableRules.addLast(r);
			}
		}

		return defined;
	}
}
