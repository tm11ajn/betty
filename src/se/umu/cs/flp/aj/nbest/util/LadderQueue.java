package se.umu.cs.flp.aj.nbest.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import se.umu.cs.flp.aj.nbest.semiring.Semiring;

public class LadderQueue<V extends Comparable<V>> {

	private int rank;
	private ArrayList<LinkedList<V>> elements;
	private ArrayList<Integer> indices;
	private ArrayList<V> currentConfig;
//	private Comparator<ArrayList<V>> comparator;
//	private PriorityMap<ArrayList<V>, Semiring> queue;
	private PriorityQueue<ArrayList<V>> configQueue;

	public LadderQueue(int rank, Comparator<ArrayList<V>> comparator) {
		this.rank = rank;
		this.indices = new ArrayList<>();
		this.currentConfig = new ArrayList<>();
//		this.comparator = comparator;
		this.configQueue = new PriorityQueue<>(comparator);

		for (int i = 0; i < rank; i++) {
			elements.add(new LinkedList<>());
		}

		reset();
	}

	private void reset() {
		for (int i = 0; i < rank; i++) {
			indices.add(i, 0);
		}
	}

	public void add(int rankIndex, V value) {
		LinkedList<V> currentList = elements.get(rankIndex);
		int size = currentList.size();

		if (value.compareTo(currentList.get(size - 1)) >= 0) {
			addLast(rankIndex, value);
		}

		for (int i = 0; i < size; i++) {
			if (value.compareTo(currentList.get(i)) < 0) {
				currentList.add(i, value);
			}
		}
	}

	public void addLast(int rankIndex, V value) {
		elements.get(rankIndex).add(value);
	}

	public ArrayList<V> dequeue() {

		ArrayList<V> prev = peek();

		if (next()) {
			updateCurrentConfig();

			if (prev == null) {
				prev = dequeue();
			}

		} else {
			currentConfig = null;
		}

		return prev;
	}

	public ArrayList<V> peek() {
		return currentConfig;
	}

	private boolean atLimit() {

		for (int i = 0; i < rank; i++) {
			if (canIncreaseIndex(i)) {
				return false;
			}
		}

		return true;
	}

	private boolean next() {

		if (atLimit()) {
			return false;
		}

		ArrayList<Integer> indexList = null;
		ArrayList<V> minConfig = null;

		for (int i = 0; i < rank; i++) {
			indexList = new ArrayList<>(indices);

			if (canIncreaseIndex(i)) {
				indexList.set(i, indices.get(i) + 1);
				ArrayList<V> tempConfig = getConfig(indexList);

//				Semiring weight = getConfigWeight();

//				if (comparator.compare(tempConfig, minConfig) < 0) {
//					minConfig = tempConfig;
//				}

				configQueue.add(tempConfig);
			}
		}

//		currentConfig = minConfig;
//		currentConfig = configQueue.poll();

		return true;
	}

	private ArrayList<V> getConfig(ArrayList<Integer> indexList) {
		ArrayList<V> config = new ArrayList<>();

		for (int i = 0; i < rank; i++) {
			config.add(elements.get(i).get(indexList.get(i)));
		}

		return config;
	}

	private void updateCurrentConfig() {
		this.currentConfig = getConfig(indices);
	}

	private boolean canIncreaseIndex(int i) {
		return indices.get(i) + 1 < elements.get(i).size();
	}

	private void getConfigWeight(ArrayList<V> config) {

		Semiring weight = null;

		for (V current : config) {
//			weight = current.
		}

	}

}
