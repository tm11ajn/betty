/*
 * Copyright 2015 Anna Jonsson for the research group Foundations of Language
 * Processing, Department of Computing Science, Umeå university
 *
 * This file is part of BestTrees.
 *
 * BestTrees is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BestTrees is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BestTrees.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.umu.cs.flp.aj.nbest.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
			Iterator<TreeKeeper<LabelType>> descendingIterator) {
		
		boolean pruned = false;
		
		LinkedHashMap<State, State> optStates = insertedKey.getOptimalStates();

		for (State q : optStates.keySet()) {
			int qUsage = 0;

			if (optimalStatesUsage.get(q) != null) {
				qUsage = optimalStatesUsage.get(q);
			}

			optimalStatesUsage.put(q, qUsage + 1);

			if (qUsage + 1 > N) {
				TreeKeeper<LabelType> removeTree = removeKey(insertedKey, q, descendingIterator);
//System.out.println("Removing " + removeTree + " from treeQueue " + "because of state " + q + " which has usage " + qUsage + 1);
				
				LinkedHashMap<State, State> optStatesRemove = removeTree.getOptimalStates(); // no null check needed since condition checked above

				for (State optRemove : optStatesRemove.keySet()) {
					optimalStatesUsage.put(optRemove,
							optimalStatesUsage.get(optRemove) - 1);
				}

				pruned = true;
			}
		}
		
		return pruned;
	}
	
	private TreeKeeper<LabelType> removeKey(TreeKeeper<LabelType> tree, State q, 
			Iterator<TreeKeeper<LabelType>> descendingIterator) {
		
//System.out.println("IN GET REMOVE KEY");
		
		TreeKeeper<LabelType> removeKey = null;
		
		while (removeKey == null && descendingIterator.hasNext()) {
			
			TreeKeeper<LabelType> currentTree = descendingIterator.next();
			
//System.out.println("CURRENT TREE " + currentTree);
			
			if (currentTree.getOptimalStates().containsKey(q)) {
				removeKey = currentTree;
				descendingIterator.remove();
			}
		}
		
		return removeKey;
	}
	
}
