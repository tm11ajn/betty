/*
 * Copyright 2015 Anna Jonsson for the research group Foundations of Language
 * Processing, Department of Computing Science, Umeå university
 *
 * This file is part of Betty.
 *
 * Betty is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Betty is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.umu.cs.flp.aj.nbest.treedata;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import se.umu.cs.flp.aj.nbest.util.Pruner;
import se.umu.cs.flp.aj.nbest.wta.State;

public class TreePruner<V>
		implements Pruner<TreeKeeper,V> {

	private static HashMap<State, Integer> optimalStatesUsage;
	private static int N;

	public TreePruner(int maxStateUsage) {
		super();
		optimalStatesUsage = new HashMap<>();
		N = maxStateUsage;
	}

	@Override
	public boolean prune(TreeKeeper insertedKey,
			TreeMap<TreeKeeper, V> map) {

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
				TreeKeeper removeTree = getRemoveKey(insertedKey,
						q, map);
				LinkedHashMap<State, State> optStatesRemove =
						removeTree.getOptimalStates();

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

	private TreeKeeper getRemoveKey(TreeKeeper tree,
			State q, TreeMap<TreeKeeper, V> map) {

		TreeKeeper removeKey = null;
		Iterator<TreeKeeper> iterator =
				map.descendingKeySet().iterator();

		while (removeKey == null && iterator.hasNext()) {
			TreeKeeper currentTree = iterator.next();

			if (currentTree.getOptimalStates().containsKey(q)) {
				removeKey = currentTree;
			}
		}

		return removeKey;
	}

}
