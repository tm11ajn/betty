/*
 * Copyright 2018 Anna Jonsson for the research group Foundations of Language
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

package se.umu.cs.flp.aj.knuth;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.State;

public class StateHolder implements Comparable<StateHolder> {

	private State state;
	private Weight weight;

	public StateHolder() {

	}

	public StateHolder(State state, Weight weight) {
		this.state = state;
		this.weight = weight;
	}

	public void setState(State state) {
		this.state = state;
	}

	public State getState() {
		return state;
	}

	public void setWeight(Weight weight) {
		this.weight = weight;
	}

	public Weight getWeight() {
		return weight;
	}

	@Override
	public int compareTo(StateHolder sh) {
		return this.weight.compareTo(sh.weight);
	}

	@Override
	public String toString() {
		return "" + state + ": " + weight;
	}

	@Override
	public int hashCode() {
		return state.hashCode();
	}
}
