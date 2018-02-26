package se.umu.cs.flp.aj.knuth;

import java.util.ArrayList;
import java.util.HashMap;

import se.umu.cs.flp.aj.wta.State;
import se.umu.cs.flp.aj.wta.WTA;
import se.umu.cs.flp.aj.wta.Weight;
import se.umu.cs.flp.aj.wta.exceptions.DuplicateRuleException;
import se.umu.cs.flp.aj.wta.exceptions.SymbolUsageException;
import se.umu.cs.flp.aj.wta_handlers.WTABuilder;

public class SmallestCompletionsFinder {

	/**
	 * Using modification of WTA along with Knuth's generalization of Dijkstra's
	 * algorithm to get the weights of the smallest completions.
	 * @param wta
	 * @return
	 */
	public static HashMap<State, Weight> findSmallestCompletionWeights(WTA wta) {
		HashMap<State, Weight> smallestCompletionWeights =
				new HashMap<State, Weight>();

		HashMap<String, State> states = wta.getStates();

		/*
		 * Build a modified WTA for each state in the original WTA
		 * and get the weight of the smallest tree that is accepted
		 * by the new WTA, that is, the smallest context with the
		 * current state as the connector.
		 */
		for (State state : states.values()) {

			WTA modifiedWTA = null;
			WTABuilder builder = new WTABuilder();

			try {
				modifiedWTA = builder.buildModifiedWTA(wta, state);
			} catch (SymbolUsageException e) {
				System.err.println("Cannot build modified WTA: " + e.getMessage());
				System.exit(-1);
			} catch (DuplicateRuleException e) {
				System.err.println("Cannot build modified WTA: " + e.getMessage());
				System.exit(-1);
			}

			ArrayList<State> modifiedFinalStates = modifiedWTA.getFinalStates();
			KnuthSmallestDerivations ksd =
					new KnuthSmallestDerivations(modifiedWTA);
			HashMap<State, StateHolder> smallestDerivations =
					ksd.getSmallestDerivation();

			Weight smallestCompletionWeight = new Weight(Weight.INF);

			for (State s : modifiedFinalStates) {

				if (smallestDerivations.containsKey(s)) {
				Weight tempWeight = smallestDerivations.get(s).getWeight();

				if (tempWeight == null) {
					System.err.println("In getting the smallest completion, "
							+ "the final state " + s + "did not have any weight"
							+ "assigned to it");
					System.exit(-1);
				}

				if (tempWeight.compareTo(smallestCompletionWeight) == -1) {
					smallestCompletionWeight = tempWeight;
				}
				}
			}

			smallestCompletionWeights.put(state, smallestCompletionWeight);

		}

		return smallestCompletionWeights;
	}
}
