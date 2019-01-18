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

package se.umu.cs.flp.aj.nbest.wta;

public class State implements Comparable<State> {

//	public static final String RESERVED_LABEL_EXTENSION_STRING = "_extension";

	private String label;
	private boolean isFinal;

	public State(String label) {
		this.label = label;
		isFinal = false;
	}

	public String getLabel() {
		return label;
	}

	protected void setFinal() {
		isFinal = true;
	}

	public boolean isFinal() {
		return isFinal;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof State) {
			State s = (State) obj;

			if (s.getLabel().equals(this.label)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public int hashCode() {
		return label.hashCode();
	}

	@Override
	public String toString() {
		return label;
	}

	@Override
	public int compareTo(State arg0) {
		return this.getLabel().compareTo(arg0.getLabel());
	}

}
