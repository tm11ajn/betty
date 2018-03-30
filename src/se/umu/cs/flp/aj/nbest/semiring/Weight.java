/*
 * Copyright 2015 Anna Jonsson for the research group Foundations of Language
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

package se.umu.cs.flp.aj.nbest.semiring;


public class Weight extends Semiring {

	private static final double INF = Double.MAX_VALUE;
	private static final double NINF = Double.MIN_VALUE;

	//private double value;

	public Weight() {
		this.value = INF;
	}

	public Weight(double value) {
		this.value = value;
	}

	@Override
	public boolean isOne() {
		return value == 0;
	}

	@Override
	public boolean isZero() {
		return value == INF;
	}

	public boolean isNegativeInfinity() {
		return value == NINF;
	}

	@Override
	public Semiring mult(Semiring w) {

		if ((value == INF && w.value == NINF) ||
				(value == NINF && w.value == INF)) {
			return null;
		}

		if (value == INF || w.value == INF) {
			return new Weight(INF);
		} else if (value == NINF || w.value == NINF) {
			return new Weight(NINF);
		}

		return new Weight(value + w.value);
	}

	@Override
	public Semiring add(Semiring w) {

		if (w.compareTo(this) < 0) {
			return w;
		}

		return this;
	}

	public Semiring div(Semiring w) {

		if ((value == INF && w.value == INF) ||
				(value == NINF && w.value == NINF)) {
			return new Weight(0);
		}

		if (value == INF || w.value == NINF) {
			return new Weight(INF);
		} else if (value == NINF || w.value == INF) {
			return new Weight(NINF);
		}

		return new Weight(value - w.value);
	}

	@Override
	public Semiring zero() {
		return new Weight(INF);
	}

	@Override
	public Semiring one() {
		return new Weight(0);
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Weight) {
			return this.value == ((Weight) obj).value;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Double.valueOf(value).hashCode();
	}

	@Override
	public int compareTo(Semiring o) {

		if (this.value == o.value) {
			return 0;
		} else if (this.value == INF || o.value == NINF) {
			return 1;
		} else if (this.value == NINF || o.value == INF) {
			return -1;
		}

		if (this.value < o.value) {
			return -1;
		}

		return 1;
	}

	@Override
	public String toString() {
		return "" + value;
	}

}
