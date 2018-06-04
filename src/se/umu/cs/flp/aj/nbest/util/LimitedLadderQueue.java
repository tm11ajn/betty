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
import java.util.LinkedList;
import java.util.PriorityQueue;

public class LimitedLadderQueue<V extends Comparable<V>> {

	private int rank;
	private int limit;
	private ArrayList<LinkedList<V>> elements;
	private PriorityQueue<Configuration<V>> configQueue;
	private int nonEmptyListCounter;
	private int dequeueCounter;
	private boolean empty;

	public LimitedLadderQueue(int rank,
			Comparator<Configuration<V>> comparator, int limit) {
		this.rank = rank;
		this.elements = new ArrayList<>();
		this.configQueue = new PriorityQueue<>(comparator);
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
	}

	public void addLast(int rankIndex, V value) {

		if (elements.get(rankIndex).size() < limit) {
			updateEmptyStatus(rankIndex);
			elements.get(rankIndex).add(value);

			if (!isEmpty()) {
				enqueueNewConfigs(rankIndex);
			}
		}
	}

	private void enqueueNewConfigs(int rankIndex) {

		ArrayList<Integer> indexList = getZeroIndices();
		ArrayList<Integer> maxIndices = getZeroIndices();
		int rankSize = elements.get(rankIndex).size();
		indexList.set(rankIndex, rankSize - 1);
		int combinations = 1;

		for (int i = 0; i < rank; i++) {

			int currentSize = elements.get(i).size();

			if (i != rankIndex) {
				combinations = combinations * currentSize;
			}

			maxIndices.set(i, currentSize - 1);
		}

		for (int i = 0; i < combinations; i++) {

			boolean increased = false;
			int index = 0;

			Configuration<V> newConfig = new Configuration<>();
			newConfig.setIndices(indexList);
			newConfig.setValues(getValues(indexList));
			configQueue.add(newConfig);

			boolean done = (i == combinations - 1);

			while (!done && !increased && index < rank) {

				if (index == rankIndex) {
					index++;

					if (index >= rank) {
						index = 0;
					}
				}

				if (indexList.get(index) == maxIndices.get(index)) {
					indexList.set(index, 0);
				} else {
					indexList.set(index, indexList.get(index) + 1);
					increased = true;
				}

				index++;
			}
		}
	}

	private ArrayList<Integer> getZeroIndices() {
		ArrayList<Integer> indexList = new ArrayList<>();

		for (int i = 0; i < rank; i++) {
			indexList.add(0);
		}

		return indexList;
	}

	public boolean isEmpty() {
		return empty;
	}

	public boolean hasNext() {
		return !configQueue.isEmpty();
	}

	public ArrayList<V> dequeue() {

		if (dequeueCounter >= limit) {
			return null;
		}

		dequeueCounter++;

		return configQueue.poll().getValues();
	}


	private ArrayList<V> getValues(ArrayList<Integer> indexList) {
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

