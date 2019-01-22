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

public class TropicalSemiring extends Semiring {

	private static final double zero = Double.MAX_VALUE;
	private static final double one = 0;

	public class TropicalWeight extends Weight {

		private static final double NINF = Double.MIN_VALUE;

		public TropicalWeight() {
			this.value = zero;
		}

		public TropicalWeight(double value) {
			this.value = value;
		}

		@Override
		public boolean isOne() {
			return value == one;
		}

		@Override
		public boolean isZero() {
			return value == zero;
		}

//		public boolean isNegativeInfinity() {
//			return value == NINF;
//		}

		@Override
		public Weight mult(Weight w) {

			if ((value == zero && w.value == NINF) ||
					(value == NINF && w.value == zero)) {
				return null;
			}

			if (value == zero || w.value == zero) {
				return new TropicalWeight(zero);
			} else if (value == NINF || w.value == NINF) {
				return new TropicalWeight(NINF);
			}

			return new TropicalWeight(value + w.value);
		}

		@Override
		public Weight add(Weight w) {

			if (w.compareTo(this) < 0) {
				return w;
			}

			return this;
		}

		public Weight div(Weight w) {

			if ((value == zero && w.value == zero) ||
					(value == NINF && w.value == NINF)) {
				return new TropicalWeight(one);
			}

			if (value == zero || w.value == NINF) {
				return new TropicalWeight(zero);
			} else if (value == NINF || w.value == zero) {
				return new TropicalWeight(NINF);
			}

			return new TropicalWeight(value - w.value);
		}

		@Override
		public Weight zero() {
			return new TropicalWeight(zero);
		}

		@Override
		public Weight one() {
			return new TropicalWeight(one);
		}

		@Override
		public boolean equals(Object obj) {

			if (obj instanceof TropicalWeight) {
				return this.value == ((TropicalWeight) obj).value;
			}

			return false;
		}

		@Override
		public int hashCode() {
			return Double.valueOf(value).hashCode();
		}

		@Override
		public int compareTo(Weight o) {

			if (this.value == o.value) {
				return 0;
			} else if (this.value == zero || o.value == NINF) {
				return 1;
			} else if (this.value == NINF || o.value == zero) {
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

		@Override
		public Weight duplicate() {
			return new TropicalWeight(value);
		}

	}

	@Override
	public Weight createWeight(double d) {
		return new TropicalWeight(d);
	}

	@Override
	public Weight zero() {
		return new TropicalWeight(zero);
	}

	@Override
	public Weight one() {
		return new TropicalWeight(one);
	}

	@Override
	public boolean isOne(Weight w) {
		return w.value == one;
	}

	@Override
	public boolean isZero(Weight w) {
		return w.value == zero;
	}

}
