/*
 * Copyright 2018 Anna Jonsson for the research group Foundations of Language
 * Processing, Department of Computing Science, Umeï¿½ university
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

package se.umu.cs.flp.aj.nbest.util;

/*
 * Copyright 2020 Anna Jonsson for the research group Foundations of Language
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import se.umu.cs.flp.aj.nbest.treedata.Configuration;

public class LadderQueue<V extends Comparable<V>> {
	
	private int id;
	private int rank;
	private int limit;
	private PriorityQueue<Configuration<V>> configQueue;
	private ArrayList<Object> seenConfigurations;
	private int dequeueCounter;

	public LadderQueue(int id, int rank,
			Comparator<Configuration<V>> comparator,
			int limit) {
		this.id = id;
		this.rank = rank;
		this.configQueue = new PriorityQueue<>(comparator);
		this.seenConfigurations = new ArrayList<Object>();
		this.limit = limit;
		this.dequeueCounter = 0;
	}
	
	/* Uses a tree structure to check if a config has already been seen. */
	@SuppressWarnings("unchecked")
	private boolean isSeen(Configuration<V> config) {
		boolean answer = true;
		int[] indices = config.getIndices();
		ArrayList<Object> currentList = seenConfigurations;
		for (int i = 0; i < rank; i++) {
			Object currentObject = null;
			
			if (currentList.size() > indices[i]) {
				currentObject = currentList.get(indices[i]);
			}
			
			if (currentObject == null) {
				answer = false;
				ArrayList<Object> newList = new ArrayList<>();
				while (currentList.size() <= indices[i]) {
					currentList.add(null);
				}
				currentList.set(indices[i], newList);
				currentList = newList;
			} else {
				currentList = (ArrayList<Object>) currentObject;
			}
		}
		
		return answer;
	}

	public int getID() {
		return id;
	}

	public void insert(Configuration<V> config) {
		configQueue.add(config);
	}

	public boolean isEmpty() {
		return configQueue.size() == 0;
	}

	public boolean hasNext() {

		if (dequeueCounter >= limit) {
			return false;
		}

		if (configQueue.isEmpty()) {
			return false;
		}

		return true;
	}

	public Configuration<V> dequeue() {
		Configuration<V> config = configQueue.poll();
		dequeueCounter++;
		return config;
	}

	public Configuration<V> peek() {
		return configQueue.peek();
	}

	/* 'Counts up' the input config to achieve the configs that can follow 
	 * this one.  */
	public ArrayList<Configuration<V>> getNextConfigs(Configuration<V> config) {
		
		/* Return an empty list if we have already dequeued all we are allowed*/
		if (dequeueCounter >= limit) {
			return new ArrayList<>();
		}
		
		int[] newIndices;
		int[] indices = config.getIndices();
		Configuration<V> newConfig;
		ArrayList<Configuration<V>> newConfigs = new ArrayList<>();

		for (int i = 0; i < rank; i++) {
			newIndices = new int[rank];

			for (int j = 0; j < rank; j++) {
				if (i == j) {
					newIndices[j] = indices[j] + 1;
				} else {
					newIndices[j] = indices[j];
				}
			}

			newConfig = new Configuration<>(newIndices, rank, this);

			if (!isSeen(newConfig)) {
				newConfigs.add(newConfig);
			}
		}

		return newConfigs;
	}
	
	/* The starting config is always a zero valued array. */
	public Configuration<V> getStartConfig() {
		return new Configuration<>(new int[rank], rank, this); 
	}

	public int size() {
		return configQueue.size();
	}

	public boolean hasReachedLimit() {
		return dequeueCounter >= limit;
	}
	
	public boolean hasNotDequeuedYet() {
		return dequeueCounter == 0;
	}

}

