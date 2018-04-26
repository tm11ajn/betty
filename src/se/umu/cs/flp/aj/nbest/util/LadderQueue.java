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

public class LadderQueue<V extends Comparable<V>> {

	private int rank;
	private ArrayList<LinkedList<V>> elements;
	private ArrayList<Integer> currentIndices;
	private Configuration<V> currentConfig;
	private PriorityQueue<Configuration<V>> configQueue;
	private int countFilled;
	private boolean empty;
	private boolean isZero;

	public LadderQueue(int rank, Comparator<Configuration<V>> comparator) {
System.out.println("Creating ladder queue with rank " + rank);
		this.rank = rank;
		this.elements = new ArrayList<>();
		this.currentIndices = new ArrayList<>();
		this.currentConfig = new Configuration<>();
		this.configQueue = new PriorityQueue<>(comparator);
		this.empty = true;
		this.countFilled = 0;
		this.isZero = true;

		for (int i = 0; i < rank; i++) {
			elements.add(new LinkedList<>());
			currentIndices.add(0);
		}

		currentConfig.setIndices(currentIndices);

		if (rank == 0) {
			currentConfig.setValues(new ArrayList<>());
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

//	public void add(int rankIndex, V value) {
//		updateEmptyStatus(rankIndex);
//
//		LinkedList<V> currentList = elements.get(rankIndex);
//		int size = currentList.size();
//
//		if (value.compareTo(currentList.get(size - 1)) >= 0) {
//			addLast(rankIndex, value);
//		}
//
//		for (int i = 0; i < size; i++) {
//			if (value.compareTo(currentList.get(i)) < 0) {
//				currentList.add(i, value);
//			}
//		}
//	}

	public void addLast(int rankIndex, V value) {

		updateEmptyStatus(rankIndex);

System.out.println("Adding " + value + " to ladderqueue");

		elements.get(rankIndex).add(value);
	}

	public boolean isEmpty() {
		return empty;
	}

	public boolean hasNext() {
		return (isZero && !isEmpty()) || !atLimit();
	}

	public ArrayList<V> dequeue() {

		ArrayList<V> prev = currentConfig.getValues();

		if (hasNext()) {
			enqueueNextConfigurations();

			if (isZero) {
				currentConfig.setValues(getValues(currentIndices));
				isZero = false;
			} else {
				currentConfig = configQueue.poll();
				currentIndices = currentConfig.getIndices();
			}
		}

System.out.println("Ladderqueue dequeueing " + prev);
		return currentConfig.getValues();
	}

	public ArrayList<V> peek() {
		return currentConfig.getValues();
	}

	private void enqueueNextConfigurations() {

		ArrayList<Integer> indexList = null;
		Configuration<V> newConfig = new Configuration<>();

		for (int i = 0; i < rank; i++) {
			indexList = new ArrayList<>(currentIndices);
System.out.println("indexlist before=" + indexList);
System.out.println("elements(i)=" + elements.get(i) );
			if (!empty && canIncreaseIndex(i)) {
				indexList.set(i, currentIndices.get(i) + 1);
System.out.println("indexlist after=" + indexList);
System.out.println("i=" + i);
System.out.println("" + elements.size());
System.out.println("" + elements.get(i));
				ArrayList<V> tempValues = getValues(indexList);
				newConfig.setIndices(indexList);
				newConfig.setValues(tempValues);
				configQueue.add(newConfig);
				//indexMap.put(tempConfig, indexList);
System.out.println("adds config " + tempValues + " to ladderqueue");
			}
		}
	}

	private ArrayList<V> getValues(ArrayList<Integer> indexList) {
		ArrayList<V> values = new ArrayList<>();

		for (int i = 0; i < rank; i++) {
System.out.println("indexlist.get(i)=" + indexList.get(i) + "for i=" + i);
System.out.println("elements.get(i)=" + elements.get(i));
			values.add(elements.get(i).get(indexList.get(i)));
		}

		return values;
	}

	private boolean atLimit() {

		for (int i = 0; i < rank; i++) {
			if (canIncreaseIndex(i)) {
				return false;
			}
		}

		return true;
	}

	private boolean canIncreaseIndex(int i) {
		int size = elements.get(i).size();


System.out.println("size=" + size);

		if (size == 0) {
			size--;
		}

System.out.println("index=" + currentIndices.get(i));

		return currentIndices.get(i) + 1 < size;
	}

	private void updateEmptyStatus(int rankIndex) {

		if (empty && elements.get(rankIndex).size() == 0) {
			countFilled++;
System.out.println("countFilled=" + countFilled);
System.out.println("rank=" + rank);
System.out.println("rankIndex=" + rankIndex);
			if (countFilled >= rank) {
				empty = false;
			}
		}
	}

}
