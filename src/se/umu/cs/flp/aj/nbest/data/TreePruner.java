/*
 * Copyright 2015 Anna Jonsson for the research group Foundations of Language
 * Processing, Department of Computing Science, Ume� university
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

//System.out.println("IN PRUNING");
//System.out.println("Previously inserted tree: " + insertedKey);
//System.out.println("Tree queue: " + map);
//System.out.println("optimalStatesUsage=" + optimalStatesUsage);

		boolean pruned = false;

		LinkedHashMap<State, State> optStates = insertedKey.getOptimalStates();

		for (State q : optStates.keySet()) {

			int qUsage = 0;

			if (optimalStatesUsage.get(q) != null) {
				qUsage = optimalStatesUsage.get(q);
			}

			optimalStatesUsage.put(q, qUsage + 1);
		}

		for (State q : optStates.keySet()) {

			if (optimalStatesUsage.get(q) > N) {
				TreeKeeper<LabelType> removeTree = getRemoveKey(insertedKey, q, map);
//System.out.println("Removing " + removeTree + " from treeQueue " + "because of state " + q + " which has usage " + qUsage + 1);

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

//System.out.println("IN GET REMOVE KEY");

		TreeKeeper<LabelType> removeKey = null;
		Iterator<TreeKeeper<LabelType>> iterator = map.descendingKeySet().iterator();

		while (removeKey == null && iterator.hasNext()) {
			TreeKeeper<LabelType> currentTree = iterator.next();
//System.out.println("CURRENT TREE " + currentTree);

			if (currentTree.getOptimalStates().containsKey(q)) {
				removeKey = currentTree;
			}
		}

		return removeKey;
	}

}
