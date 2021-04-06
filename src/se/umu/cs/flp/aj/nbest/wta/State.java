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

package se.umu.cs.flp.aj.nbest.wta;

import se.umu.cs.flp.aj.nbest.util.Hypergraph;

public class State extends Hypergraph.Node<Rule> {

	private Symbol label;
	private boolean isFinal;
	private boolean saturated;
	private boolean inBestContext;
//	private Context bestContext;

	public State(Symbol label) {
		super();
		this.label = label;
		isFinal = false;
		saturated = false;
		inBestContext = false;
//		bestContext = null;
	}
	
//	public void setBestContext(Context context) {
//		this.bestContext = context;
//	}
	
//	public Context getBestContext() {
//		return bestContext;
//	}

	public Symbol getLabel() {
		return label;
	}

	protected void setFinal() {
		isFinal = true;
	}

	public boolean isFinal() {
		return isFinal;
	}
	
	public void markAsSaturated() {
		this.saturated = true;
	}
	
	public boolean isSaturated() {
		return saturated;
	}
	
	public void markAsFoundInBestContext() {
		this.inBestContext = true;
	}
	
	public boolean isInBestContext() {
		return inBestContext;
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
		return this.getID();
	}

	@Override
	public String toString() {
		return label.toString();
	}

}
