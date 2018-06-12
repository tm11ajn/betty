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
import java.util.LinkedList;
import java.util.PriorityQueue;

public class OneStepLazyLimitedLadderQueue<V extends Comparable<V>> {

	private int rank;
	private int limit;
	private ArrayList<LinkedList<V>> elements;
	private PriorityQueue<Configuration<V>> configQueue;
	private HashMap<Configuration<V>, Configuration<V>> usedConfigs;
	private HashMap<Configuration<V>, ArrayList<Integer>> expandedIndicesForConfig;
	private HashMap<Configuration<V>, Integer> timesUsedForEnqueue;
	private LinkedList<Configuration<V>> dequeuedButNotExpandedConfigs;
	private Configuration<V> frontConfig;
//	private LinkedList<Configuration<V>> pendingConfigs;
	private int nonEmptyListCounter;
	private int dequeueCounter;
	private boolean empty;

	public OneStepLazyLimitedLadderQueue(int rank,
			Comparator<Configuration<V>> comparator, int limit) {
		this.rank = rank;
		this.elements = new ArrayList<>();
		this.configQueue = new PriorityQueue<>(comparator);
		this.usedConfigs = new HashMap<>();
		this.expandedIndicesForConfig = new HashMap<>();
		this.timesUsedForEnqueue = new HashMap<>();
		this.dequeuedButNotExpandedConfigs = new LinkedList<>();
		this.frontConfig = null;
//		this.pendingConfigs = new LinkedList<>();
		this.empty = true;
		this.nonEmptyListCounter = 0;
		this.limit = limit;
		this.dequeueCounter = 0;

		for (int i = 0; i < rank; i++) {
			elements.add(new LinkedList<>());
		}

		if (rank == 0) {
			Configuration<V> config = new Configuration<>();
			config.setIndices(new ArrayList<>());
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
		timesUsedForEnqueue.put(config, 0);
		expandedIndicesForConfig.put(config, getZeroIndices());
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

			if (frontConfig != null) {
				if (isUsable(frontConfig) &&
						!usedConfigs.containsKey(frontConfig)) {
					frontConfig.setValues(extractValues(
							frontConfig.getIndices()));
					frontConfig = null;
				}
			}

			if (!dequeuedButNotExpandedConfigs.isEmpty()) {
				updateQueueBasedOnDequeuedConfigs();
			}
		}

//		if (elements.get(rankIndex).size() < limit) {
//			boolean wasEmpty = isEmpty();
//			updateEmptyStatus(rankIndex);
//			elements.get(rankIndex).add(value);
//
//			if (wasEmpty && !isEmpty()) {
//				Configuration<V> firstConfig = new Configuration<>();
//				firstConfig.setIndices(getZeroIndices());
//				firstConfig.setValues(extractValues(firstConfig.getIndices()));
//				configQueue.add(firstConfig);
//				usedConfigs.put(firstConfig, firstConfig);
//			}
//
//			Iterator<Configuration<V>> iterator = pendingConfigs.iterator();
//
//			while (iterator.hasNext()) {
//				Configuration<V> pending = iterator.next();
//
//				if (isUsable(pending)) {
//
//					if (!usedConfigs.containsKey(pending)) {
//						pending.setValues(extractValues(pending.getIndices()));
//						configQueue.add(pending);
//						usedConfigs.put(pending, pending);
//					}
//
//					iterator.remove();
//				}
//			}
//		}
	}

	private boolean isUsable(Configuration<V> config) {
		ArrayList<Integer> indices = config.getIndices();

		for (int i = 0; i < rank; i++) {
			if (indices.get(i) >= elements.get(i).size()) {
				return false;
			}
		}

		return true;
	}

	private void enqueueNewConfigs(Configuration<V> config) {

		if (!dequeuedButNotExpandedConfigs.isEmpty()) {
			updateFrontConfig(config);
//			updateQueueBasedOnDequeuedConfigs();
			dequeuedButNotExpandedConfigs.addLast(config);
		} else {
			updateQueueBasedOnCurrentConfig(config);
		}

	}

	private void updateFrontConfig(Configuration<V> config) {
		Configuration<V> prev = dequeuedButNotExpandedConfigs.getLast();
		int diffIndex = getDiffIndex(config, prev);
		ArrayList<Integer> indexList = new ArrayList<>(config.getIndices());
		indexList.set(diffIndex, indexList.get(diffIndex) + 1);
		Configuration<V> newConfig = new Configuration<>();
		newConfig.setIndices(indexList);

		if (isUsable(newConfig)) {
			timesUsedForEnqueue.put(config, 1);
			expandedIndicesForConfig.get(config).set(diffIndex, 0);

			if (!usedConfigs.containsKey(newConfig)) {
				newConfig.setValues(extractValues(indexList));
				configQueue.add(newConfig);
				usedConfigs.put(newConfig, newConfig);
			}
		} else {
			frontConfig = newConfig;
		}
	}

	private void updateQueueBasedOnDequeuedConfigs() {
		Configuration<V> base = dequeuedButNotExpandedConfigs.getFirst();
		ArrayList<Integer> usedIndices = expandedIndicesForConfig.get(base);
		ArrayList<Integer> indexList;
		Configuration<V> newConfig;
		boolean done = false;

		while (!done && !dequeuedButNotExpandedConfigs.isEmpty()) {

			for (int i = 0; i < rank; i++) {

				if (usedIndices.get(i) == 0) {
					indexList = new ArrayList<>(base.getIndices());
					indexList.set(i, indexList.get(i) + 1);
					newConfig = new Configuration<>();
					newConfig.setIndices(indexList);

					if (isUsable(newConfig)) {
						expandedIndicesForConfig.get(base).set(i, 1);
						timesUsedForEnqueue.put(base,
								timesUsedForEnqueue.get(base) + 1);

						if (!usedConfigs.containsKey(newConfig)) {
							newConfig.setValues(extractValues(indexList));
							configQueue.add(newConfig);
							usedConfigs.put(newConfig, newConfig);
						}
					}
				}
			}

			if (timesUsedForEnqueue.get(base) == rank) {
				dequeuedButNotExpandedConfigs.removeFirst();
			} else {
				done = true;
			}
		}
	}

	private void updateQueueBasedOnCurrentConfig(Configuration<V> config) {
		int nOfInputtedConfigs = 0;
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
				expandedIndicesForConfig.get(config).set(i, 1);
				nOfInputtedConfigs++;

				if (!usedConfigs.containsKey(newConfig)) {
					newConfig.setValues(extractValues(indexList));
					configQueue.add(newConfig);
					usedConfigs.put(newConfig, newConfig);
				}
			}
		}

		if (nOfInputtedConfigs < rank) {
			dequeuedButNotExpandedConfigs.addLast(config);
			timesUsedForEnqueue.put(config, nOfInputtedConfigs);
		}
	}

	private int getDiffIndex(Configuration<V> current, Configuration<V> prev) {

		for (int i = 0; i < rank; i++) {
			if (current.getIndices().get(i) > prev.getIndices().get(i)) {
				return i;
			}
		}

		return -1;
	}

	private ArrayList<Integer> getZeroIndices() {
		ArrayList<Integer> indexList = new ArrayList<>();

		for (int i = 0; i < rank; i++) {
			indexList.add(0);
		}

		return indexList;
	}

	private ArrayList<V> extractValues(ArrayList<Integer> indexList) {
		ArrayList<V> values = new ArrayList<>();

		for (int i = 0; i < rank; i++) {
			values.add(elements.get(i).get(indexList.get(i)));
		}

		return values;
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

