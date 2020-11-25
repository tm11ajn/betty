/*
 * Copyright 2015 Anna Jonsson for the research group Foundations of Language
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

package se.umu.cs.flp.aj.nbest.semiring;

public abstract class Semiring {

	public abstract Weight createWeight(double d);
	public abstract Weight zero();
	public abstract Weight one();
	public abstract boolean isOne(Weight w);
	public abstract boolean isZero(Weight w);

	public Weight add(Weight w1, Weight w2) {
		return w1.add(w2);
	}

	public Weight mult(Weight w1, Weight w2) {
		return w1.mult(w2);
	}

	public Weight div(Weight w1, Weight w2) {
		return w1.div(w2);
	}

}
