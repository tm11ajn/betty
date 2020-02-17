package se.umu.cs.flp.aj.nbest.treedata;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.util.LadderQueue;

public class Configuration<T extends Comparable<T>> {
	private T[] values;
	private Weight weight;
	private int[] indices;
	private int size;
	private int leftToValues;
	private LadderQueue<T> origin;

	public Configuration(int[] indices, int size,
			LadderQueue<T> origin) {
		setIndices(indices, size);
		leftToValues = size;
		this.origin = origin;
	}

	public int getLeftToValues() {
		return this.leftToValues;
	}

	public void decreaseLeftToValuesBy(int dec) {
		this.leftToValues -= dec;
	}

	public void setValues(T[] values) {
		this.values = values;
	}

	public T[] getValues() {
		return values;
	}
	
	public void setWeight(Weight weight) {
		this.weight = weight;
	}
	
	public Weight getWeight() {
		return weight;
	}

	private void setIndices(int[] indices, int size) {
		this.indices = indices;
		this.size = size;
	}

	public int[] getIndices() {
		return indices;
	}

	public int getSize() {
		return size;
	}

	public LadderQueue<T> getOrigin() {
		return origin;
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
