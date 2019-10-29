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
import java.util.ListIterator;
import java.util.PriorityQueue;

public class LazyLimitedLadderQueue<V extends Comparable<V>> {

//	static private Hypergraph<Configuration<?>, Pair> graph;
	static private Pair[][] pairs;
	static private boolean initialised = false;

	public static void init (int nOfStates, int N) {
		pairs = new Pair[nOfStates][N];
		initialised = true;
	}

	private int rank;
	private int limit;
	private V[][] elements;
	private int[] elementCount;
	private int[] elementIndices;
	private PriorityQueue<Configuration<V>> configQueue;
	private HashMap<Configuration<V>, Configuration<V>> usedConfigs;
//	private HashMap<Configuration<V>, Integer> missingDataCounter;
//	private HashMap<Integer, LinkedList<Configuration<V>>> pendingConfigs;
	private int nonEmptyListCounter;
	private int dequeueCounter;
	private boolean empty;
	private boolean needsUpdate;
	private Comparator<Configuration<V>> comparator;

	public LazyLimitedLadderQueue(int rank, V[][] elements, int[] elementCount,
			int[] elementIndices, Comparator<Configuration<V>> comparator,
			int limit) {
		if (!initialised) {
			throw new IllegalStateException("Class not initialised before use.");
		}

		this.rank = rank;
		this.elements = elements;
		this.elementCount = elementCount;
		this.elementIndices = elementIndices;
		this.configQueue = new PriorityQueue<>(comparator);
		this.usedConfigs = new HashMap<>();
//		this.missingDataCounter = new HashMap<>();
//		this.pendingConfigs = new HashMap<>();
		this.empty = true;
		this.needsUpdate = false;
		this.nonEmptyListCounter = 0;
		this.limit = limit;
		this.dequeueCounter = 0;
		this.comparator = comparator;

		if (rank == 0) {
			Configuration<V> config = new Configuration<>(new int[0], rank);
			configQueue.add(config);
			empty = false;
		}
	}

	static private class Pair extends Hypergraph.Edge<Configuration<?>> {
		private int left;
		private int right;

		Pair(int left, int right) {
			this.left = left;
			this.right = right;
		}

		public int getLeft() {
			return left;
		}

		public int getRight() {
			return right;
		}
	}

	static public class Configuration<T> extends Hypergraph.Node<Pair> {
		private ArrayList<T> values;
		private int[] indices;
		private int size;
		private int hash;
		private int leftToValues;

		public Configuration(int[] indices, int size) {
			setIndices(indices, size);
			values = new ArrayList<>();
			leftToValues = size;
		}

		public int getLeftToValues() {
			return this.leftToValues;
		}

		public void decreaseLeftToValuesBy(int dec) {
			this.leftToValues -= dec;
		}

		public void setValues(ArrayList<T> values) {
			this.values = values;
		}

		public ArrayList<T> getValues() {
			return values;
		}

		private void setIndices(int[] indices, int size) {
			hash = 0;
			this.indices = indices;
			this.size = size;
		}

		public int[] getIndices() {
			return indices;
		}

		public int getSize() {
			return size;
		}

		@Override
		public int hashCode() {

			if (hash == 0) {
//				hash = indices.hashCode();

				for (int i = 0; i < size; i++) {
					hash = (hash + indices[i]) << 1;
				}
			}

System.out.println("hashcode=" + hash + " for " + this.toString());
			return hash;
		}

		@Override
		public boolean equals(Object o) {

			if (!(o instanceof Configuration<?>)) {
				return false;
			}

			if (this.hash != o.hashCode()) {
				return false;
			}

			Configuration<?> c = (Configuration<?>) o;

			for (int i = 0; i < size; i++) {
				if (this.indices[i] != c.indices[i]) {
					return false;
				}
			}

//			return this.indices.equals(c.indices);
			return true;
		}

		@Override
		public String toString() {
			String s = "[";

			for (int i = 0; i < size; i++) {
				s += indices[i] + " ";
			}

			s += "]";

			return s;
		}
	}

	public boolean isEmpty() {
		return empty;
	}

	public boolean needsUpdate() {
		return needsUpdate;
	}

	public void hasUpdated() {
		needsUpdate = false;
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

	public ArrayList<V> dequeue() {
		dequeueCounter++;
		Configuration<V> config = configQueue.poll();
System.out.println("Config dequeued: " + config);
		enqueueNewConfigs(config);

		return config.getValues();
	}

	public ArrayList<V> peek() {
		return configQueue.peek().getValues();
	}

	public void update(int rankIndex) {
		Configuration<V> oldConfig = configQueue.peek();
		boolean wasEmpty = isEmpty();
		updateEmptyStatus(rankIndex);
		int stateIndex = elementIndices[rankIndex];
		int filledIndex = elementCount[stateIndex] - 1;
System.out.println(stateIndex);
System.out.println(filledIndex);

		if (filledIndex == -1) {
			filledIndex = 0;
		}

		if (pairs[stateIndex][filledIndex] == null) {
			pairs[stateIndex][filledIndex] = new Pair(stateIndex, filledIndex);
		}

		Pair currentPair = pairs[stateIndex][filledIndex];

System.out.println(wasEmpty);
System.out.println(isEmpty());

		if (wasEmpty && !isEmpty()) {
			Configuration<V> firstConfig = new Configuration<>(
					getZeroIndices(), rank);
			firstConfig.setValues(extractValues(firstConfig.getIndices()));
			configQueue.add(firstConfig);
System.out.println("add " + firstConfig.getValues());
System.out.println("and " + firstConfig.getIndices());
System.out.println(firstConfig);
System.out.println("hash: " + firstConfig.hashCode());
			usedConfigs.put(firstConfig, firstConfig);
		}

		ListIterator<Configuration<?>> it = currentPair.getFrom().listIterator();

		while (it.hasNext()) {
			Configuration<V> pending = (Configuration<V>) it.next();
System.out.println("pending=" + pending);
			pending.decreaseLeftToValuesBy(1);

			if (pending.getLeftToValues() == 0) {
				if (!usedConfigs.containsKey(pending)) {
					pending.setValues(extractValues(pending.getIndices()));
					configQueue.add(pending);
					usedConfigs.put(pending, pending);
System.out.println("add " + pending);
				}
			}

			it.remove();
		}

//			if (pendingConfigs.containsKey(rankIndex)) {
//				Iterator<Configuration<V>> iterator =
//						pendingConfigs.get(rankIndex).iterator();
//
//				while (iterator.hasNext()) {
//					Configuration<V> pending = iterator.next();
//					decreaseMissingDataCounter(pending);
//
//					if (isReady(pending)) {
//						if (!usedConfigs.containsKey(pending)) {
//							pending.setValues(extractValues(
//									pending.getIndices()));
//							configQueue.add(pending);
//							usedConfigs.put(pending, pending);
//						}
//
//						iterator.remove();
//					}
//				}
//			}

		if (oldConfig != null &&
				comparator.compare(configQueue.peek(), oldConfig) < 0) {
			needsUpdate = true;
		}

	}

	private void enqueueNewConfigs(Configuration<V> config) {
		int[] newIndices;
		int[] indices = config.getIndices();
		Configuration<V> newConfig;

		for (int i = 0; i < rank; i++) {
			newIndices = new int[rank];
			int nOfElements = elementCount[elementIndices[i]];

			for(int j = 0; j < rank; j++) {
				if (i == j) {
					newIndices[j] = indices[j] + 1;
				} else {
					newIndices[j] = indices[j];
				}
			}

			newConfig = new Configuration<>(newIndices, rank);

			if (nOfElements > newIndices[i]) {
				newConfig.setValues(extractValues(newIndices));

				if (!usedConfigs.containsKey(newConfig)) {
					configQueue.add(newConfig);
					usedConfigs.put(newConfig, newConfig);
System.out.println("enqueue " + newConfig);
				}
			} else {

				Pair currentPair = pairs[elementIndices[i]][newIndices[i]];

				if (currentPair == null) {
					currentPair = new Pair(elementIndices[i], newIndices[i]);
					pairs[elementIndices[i]][newIndices[i]] = currentPair;
				}

				currentPair.addFrom(newConfig);
				newConfig.leftToValues = countMissingElements(newConfig);

				for (int k = 0; k < rank; k++) {
					Pair pair = pairs[elementIndices[k]][newIndices[k]];
					pair.addFrom(newConfig);
				}

//				if (!pendingConfigs.containsKey(i)) {
//					pendingConfigs.put(i, new LinkedList<>());
//				}
//
//				pendingConfigs.get(i).add(newConfig);
//				missingDataCounter.put(newConfig,
//						countMissingElements(newConfig));
			}
		}
	}

//	private boolean isReady(Configuration<V> config) {
//		return missingDataCounter.get(config) == 0;
//	}

//	private void decreaseMissingDataCounter(Configuration<V> config) {
//		missingDataCounter.put(config, missingDataCounter.get(config) - 1);
//	}

	private int countMissingElements(Configuration<V> config) {
		int[] indices = config.getIndices();
		int missingElements = 0;

		for (int i = 0; i < rank; i++) {
			if (indices[i] >= elementCount[elementIndices[i]]) {
				missingElements++;
			}
		}

		return missingElements;
	}

	private ArrayList<V> extractValues(int[] indexList) {
		ArrayList<V> values = new ArrayList<>(rank);

		for (int i = 0; i < rank; i++) {
			values.add(elements[elementIndices[i]][indexList[i]]);
		}

		return values;
	}

	private int[] getZeroIndices() {
		int[] indexList = new int[rank];

		for (int i = 0; i < rank; i++) {
			indexList[i] = 0;
		}

		return indexList;
	}

	private void updateEmptyStatus(int rankIndex) {

		if (empty && elementCount[elementIndices[rankIndex]] == 1) {
			nonEmptyListCounter++;

			if (nonEmptyListCounter >= rank) {
				empty = false;
			}
		}
	}

}

