package se.umu.cs.flp.aj.nbest.data;

import java.util.Comparator;
import java.util.HashMap;

import se.umu.cs.flp.aj.wta.State;
import se.umu.cs.flp.aj.wta.Weight;

public class TreeComparator<LabelType extends Comparable<LabelType>> 
		implements Comparator<TreeKeeper<LabelType>> {
	
	private static HashMap<State, Weight> smallestCompletions;
	
	public TreeComparator(HashMap<State, Weight> smallestCompletionWeights) {
		smallestCompletions = smallestCompletionWeights;
	}

	@Override
	public int compare(TreeKeeper<LabelType> o1, TreeKeeper<LabelType> o2) {
		
		int result;
		
		State optState1 = o1.getOptimalStates().keySet().iterator().next();
		State optState2 = o2.getOptimalStates().keySet().iterator().next();
		
		Weight weight1 = o1.getOptWeights().get(optState1).
				add(smallestCompletions.get(optState1));
		Weight weight2 = o2.getOptWeights().get(optState2).
				add(smallestCompletions.get(optState2));
		
//		int weightComparison = o1.getWeight().compareTo(o2.getWeight());
		int weightComparison = weight1.compareTo(weight2);
		
		if (weightComparison != 0 ) {
//			return weightComparison;
			result = weightComparison;
		} else {
			result = o1.getTree().compareTo(o2.getTree());
		}

//System.out.println("Comparing " + o1 + " with " + o2 + " with result " + result);


		return result;
//		return o1.getTree().compareTo(o2.getTree())
	}

}
