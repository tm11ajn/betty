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
import java.util.LinkedList;

import se.umu.cs.flp.aj.nbest.semiring.Weight;

public class LazyLadderQueue<V extends Weighted> {

	private int rank;
	private ArrayList<LinkedList<V>> elements;
//	private PriorityQueue<Configuration<V>> configQueue;
	private Configuration<V> currentConfig;
	private ArrayList<Integer> elementsSize;
	private int countFilled;
	private boolean empty;

	private int outputCount;
	private int maxOutputs;

	public LazyLadderQueue(int rank) {
		this.rank = rank;
		this.elements = new ArrayList<>();
//		this.configQueue = new PriorityQueue<>(comparator);
		this.currentConfig = null;
		this.empty = true;
		this.countFilled = 0;
		this.outputCount = 0;
		this.maxOutputs = 0;
		this.elementsSize = getZeroIndices();

		for (int i = 0; i < rank; i++) {
			elements.add(new LinkedList<>());
		}

		if (rank == 0) {
			Configuration<V> config = new Configuration<>();
			config.setValues(new ArrayList<>());
			currentConfig = config;
//			configQueue.add(config);
			empty = false;
		}
	}

	public static class Configuration<T> {
		private ArrayList<T> values;
		private ArrayList<Integer> indices;

		private int previouslyUpdatedIndex;

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

		public void setPreviouslyUpdatedIndex(int rankIndex) {
			this.previouslyUpdatedIndex = rankIndex;
		}

		public int getPreviouslyUpdatedIndex() {
			return previouslyUpdatedIndex;
		}
	}

	public void addLast(int rankIndex, V value) {
		updateEmptyStatus(rankIndex);
		elements.get(rankIndex).add(value);
		Integer oldSize = elementsSize.get(rankIndex);
		Integer newSize = oldSize + 1;
		elementsSize.set(rankIndex, newSize);
		maxOutputs = maxOutputs / oldSize * newSize;
	}

	private Configuration<V> getNextConfig() {

		int updatedIndex = currentConfig.getPreviouslyUpdatedIndex();
		ArrayList<Integer> currIndices = currentConfig.getIndices();
		V updatedVal = elements.get(updatedIndex).get(
				currIndices.get(updatedIndex));
		ArrayList<Integer> nextIndices = currentConfig.getIndices();
		Configuration<V> nextConfig = new Configuration<>();

		// Räkna ut skillnaden mellan varje "nästa" element och det föregående,
		// men för prevIndex, räkna ut skillnaden åt båda hållen.

		Weight undoDiff = null;

		if (currIndices.get(updatedIndex) != 0) {
			V val2 = elements.get(updatedIndex).get(
					currIndices.get(updatedIndex) - 1);
			undoDiff = val2.getWeight().div(updatedVal.getWeight());
		}

		Weight minDiff = null;
		Weight doDiff = null;
		int minDiffIndex = 0;

		for (int i = 0; i < rank; i++) {

			if (elements.get(i).size() > currIndices.get(i) + 1) {
				V val1 = elements.get(i).get(currIndices.get(i));
				V val2 = elements.get(i).get(currIndices.get(i) + 1);

				Weight w1 = val1.getWeight();
				Weight w2 = val2.getWeight();
				Weight diff = w2.div(w1);

				if (minDiff == null || diff.compareTo(minDiff) < 0) {
					minDiff = diff;
					minDiffIndex = i;
				}

				if (i == updatedIndex) {
					doDiff = diff;
				}
			}
		}

		if (minDiffIndex != updatedIndex && undoDiff != null) {
			Weight w1 = minDiff.div(undoDiff);
			Weight w2 = doDiff;

			if (w1.compareTo(w2) > 0) {
				minDiffIndex = updatedIndex;
			}
		}

//		nextIndices.set() // TODO update nextIndices properly.

		nextConfig.setValues(getValues(nextIndices));
		nextConfig.setPreviouslyUpdatedIndex(minDiffIndex); // TODO

		return nextConfig;
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

		if (isEmpty() || maxOutputs <= outputCount) {
			return false;
		}

		return true;
	}

	public ArrayList<V> dequeue() {

		if (!hasNext()) {
			return null;
		}

		ArrayList<V> output = currentConfig.getValues();
		currentConfig = getNextConfig();
		outputCount++;

		return output;
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
			countFilled++;

			if (countFilled >= rank) {
				empty = false;
			}
		}
	}

}
