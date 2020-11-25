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
