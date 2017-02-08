package se.umu.cs.flp.aj.nbest.data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import se.umu.cs.flp.aj.wta.State;

public class TreePruner<LabelType extends Comparable<LabelType>,V> 
		implements Pruner<TreeKeeper<LabelType>,V> {
	
	private static HashMap<State, Integer> optimalStatesUsage;
	private static int N;
	
	public TreePruner(int maxStateUsage) {
		super();
		optimalStatesUsage = new HashMap<>();
		N = maxStateUsage;
	}

	@Override
	public boolean prune(TreeKeeper<LabelType> insertedKey,
			TreeMap<TreeKeeper<LabelType>, V> map) {
		
		boolean pruned = false;
		
//		ArrayList<State> optStates = optimalStates.get(tree);
		LinkedHashMap<State, State> optStates = insertedKey.getOptimalStates();
		

		for (State q : optStates.keySet()) {
			int qUsage = 0;

			if (optimalStatesUsage.get(q) != null) {
				qUsage = optimalStatesUsage.get(q);
			}

			optimalStatesUsage.put(q, qUsage + 1);

			if (qUsage + 1 > N) {
				TreeKeeper<LabelType> removeTree = getRemoveKey(insertedKey, q, map);
//System.out.println("Removing " + removeTree + " from treeQueue " + "because of state " + q + " which has usage " + qUsage + 1);

//				if (removeKey < map.size()) { // Remove check?
//				TreeKeeper<LabelType> removeTree = map.get(removeKey);

//					ArrayList<State> optStatesRemove =
//							optimalStates.get(removeTree);
				LinkedHashMap<State, State> optStatesRemove = removeTree.getOptimalStates();

				for (State optRemove : optStatesRemove.keySet()) {
					optimalStatesUsage.put(optRemove,
							optimalStatesUsage.get(optRemove) - 1);
				}

				map.remove(removeTree);
				pruned = true;
			}
		}
		
		return pruned;
	}
	
	private TreeKeeper<LabelType> getRemoveKey(TreeKeeper<LabelType> tree, State q, 
			TreeMap<TreeKeeper<LabelType>, V> map) {

//		int queueSize = map.size();
//		while (removeIndex == queueSize && currentIndex > -1) {
//			TreeKeeper<LabelType> currentTree = map.get(currentIndex);
//			ArrayList<State> optStatesCurrent =
//					optimalStates.get(currentTree);
//
//			if (optStatesCurrent.contains(q)) {
//				removeIndex = currentIndex;
//			}
//
//			currentIndex--;
//		}
//
//		return removeIndex;
		
//System.out.println("IN GET REMOVE KEY");
		
		TreeKeeper<LabelType> removeKey = null;
		
		for (TreeKeeper<LabelType> currentTree : map.descendingKeySet()) {
			
//System.out.println("CURRENT TREE " + currentTree);
			
			if (currentTree.getOptimalStates().containsKey(q)) {
				removeKey = currentTree;
				break;
			}
		}
		
		return removeKey;
	}
	
}
