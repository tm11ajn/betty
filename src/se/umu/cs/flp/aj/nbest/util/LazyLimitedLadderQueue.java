/*
 * Copyright 2018 Anna Jonsson for the research group Foundations of Language
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

package se.umu.cs.flp.aj.nbest.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class LazyLimitedLadderQueue<V extends Comparable<V>> {

	private int rank;
	private int limit;
	private ArrayList<LinkedList<V>> elements;
	private PriorityQueue<Configuration<V>> configQueue;
	private HashMap<Configuration<V>, Configuration<V>> usedConfigs;
	private HashMap<Configuration<V>, Integer> missingDataCounter;
	private HashMap<Integer, LinkedList<Configuration<V>>> pendingConfigs;
	private int nonEmptyListCounter;
	private int dequeueCounter;
	private boolean empty;

	public LazyLimitedLadderQueue(int rank,
			Comparator<Configuration<V>> comparator, int limit) {
		this.rank = rank;
		this.elements = new ArrayList<>();
		this.configQueue = new PriorityQueue<>(comparator);
		this.usedConfigs = new HashMap<>();
		this.missingDataCounter = new HashMap<>();
		this.pendingConfigs = new HashMap<>();
		this.empty = true;
		this.nonEmptyListCounter = 0;
		this.limit = limit;
		this.dequeueCounter = 0;

		for (int i = 0; i < rank; i++) {
			elements.add(new LinkedList<>());
		}

		if (rank == 0) {
			Configuration<V> config = new Configuration<>();
			config.setValues(new ArrayList<>());
			configQueue.add(config);
			empty = false;
		}
	}

	public static class Configuration<T> {
		private ArrayList<T> values;
		private ArrayList<Integer> indices;

		public Configuration() {
		}

		public void setValues(ArrayList<T> values) {
			this.values = values;
		}

		public ArrayList<T> getValues() {
			return values;
		}

		public void setIndices(ArrayList<Integer> indices) {
			this.indices = indices;
		}

		public ArrayList<Integer> getIndices() {
			return indices;
		}

		@Override
		public int hashCode() {
			return indices.hashCode();
		}

		@Override
		public boolean equals(Object o) {

			if (!(o instanceof Configuration<?>)) {
				return false;
			}

			Configuration<?> c = (Configuration<?>) o;

			return this.indices.equals(c.indices);
		}

		@Override
		public String toString() {
			return indices.toString(); //+ "/" + values;
		}
	}



	public boolean isEmpty() {
		return empty;
	}

	public boolean hasNext() {

		if (configQueue.isEmpty()) {
			return false;
		}

		return true;
	}

	public ArrayList<V> dequeue() {

		if (dequeueCounter >= limit) {
			return null;
		}

		dequeueCounter++;
		Configuration<V> config = configQueue.poll();
		enqueueNewConfigs(config);

		return config.getValues();
	}

	public void addLast(int rankIndex, V value) {

		if (elements.get(rankIndex).size() < limit) {
			boolean wasEmpty = isEmpty();
			updateEmptyStatus(rankIndex);
			elements.get(rankIndex).add(value);

			if (wasEmpty && !isEmpty()) {
				Configuration<V> firstConfig = new Configuration<>();
				firstConfig.setIndices(getZeroIndices());
				firstConfig.setValues(extractValues(firstConfig.getIndices()));
				configQueue.add(firstConfig);
				usedConfigs.put(firstConfig, firstConfig);
			}

			if (pendingConfigs.containsKey(rankIndex)) {
				Iterator<Configuration<V>> iterator =
						pendingConfigs.get(rankIndex).iterator();

				while (iterator.hasNext()) {
					Configuration<V> pending = iterator.next();
					decreaseMissingDataCounter(pending);

					if (isReady(pending)) {

						if (!usedConfigs.containsKey(pending)) {
							pending.setValues(extractValues(
									pending.getIndices()));
							configQueue.add(pending);
							usedConfigs.put(pending, pending);
						}

						iterator.remove();
					}
				}
			}
		}
	}

	private void enqueueNewConfigs(Configuration<V> config) {

		ArrayList<Integer> indexList;
		Configuration<V> newConfig;

		for (int i = 0; i < rank; i++) {
			indexList = new ArrayList<>(config.getIndices());
			int prevIndex = indexList.get(i);
			int nOfElements = elements.get(i).size();

			indexList.set(i, prevIndex + 1);
			newConfig = new Configuration<>();
			newConfig.setIndices(indexList);

			if (nOfElements <= limit && nOfElements > prevIndex + 1) {
				newConfig.setValues(extractValues(indexList));

				if (!usedConfigs.containsKey(newConfig)) {
					configQueue.add(newConfig);
					usedConfigs.put(newConfig, newConfig);
				}
			} else if (nOfElements <= limit) {

				if (!pendingConfigs.containsKey(i)) {
					pendingConfigs.put(i, new LinkedList<>());
				}

				pendingConfigs.get(i).add(newConfig);
				missingDataCounter.put(newConfig,
						countMissingElements(newConfig));
			}
		}
	}

	private boolean isReady(Configuration<V> config) {
		return missingDataCounter.get(config) == 0;
	}

	private void decreaseMissingDataCounter(Configuration<V> config) {
		missingDataCounter.put(config, missingDataCounter.get(config) - 1);
	}

	private int countMissingElements(Configuration<V> config) {
		ArrayList<Integer> indices = config.getIndices();
		int missingElements = 0;

		for (int i = 0; i < rank; i++) {
			if (indices.get(i) >= elements.get(i).size()) {
				missingElements++;
			}
		}

		return missingElements;
	}

	private ArrayList<V> extractValues(ArrayList<Integer> indexList) {
		ArrayList<V> values = new ArrayList<>();

		for (int i = 0; i < rank; i++) {
			values.add(elements.get(i).get(indexList.get(i)));
		}

		return values;
	}

	private ArrayList<Integer> getZeroIndices() {
		ArrayList<Integer> indexList = new ArrayList<>();

		for (int i = 0; i < rank; i++) {
			indexList.add(0);
		}

		return indexList;
	}

	private void updateEmptyStatus(int rankIndex) {

		if (empty && elements.get(rankIndex).size() == 0) {
			nonEmptyListCounter++;

			if (nonEmptyListCounter >= rank) {
				empty = false;
			}
		}
	}

}

