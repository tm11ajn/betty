package se.umu.cs.flp.aj.nbest.treedata;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.util.LazyLimitedLadderQueue;

public class Configuration<T extends Comparable<T>> {
	private T[] values;
	private Weight weight;
	private int[] indices;
	private int size;
	private int hash;
	private int leftToValues;
	private LazyLimitedLadderQueue<T> origin;

	public Configuration(int[] indices, int size,
			LazyLimitedLadderQueue<T> origin) {
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

	public LazyLimitedLadderQueue<T> getOrigin() {
		return origin;
	}

	@Override
	public int hashCode() {

		if (hash == 0) {
//			hash = indices.hashCode();

			for (int i = 0; i < size; i++) {
				hash = (hash + indices[i]) << 1;
			}
		}

//System.out.println("hashcode=" + hash + " for " + this.toString());
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

//		return this.indices.equals(c.indices);
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
