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

public class Symbol implements Comparable<Symbol> {
	
	public static final String RESERVED_SYMBOL_STRING = 
			"context_symbol_with_rank_zero";

	private String label;
	private int rank;

	public Symbol(String symbol, int rank) {
		this.label = symbol;
		this.rank = rank;
	}

	public String getLabel() {
		return label;
	}

	public int getRank() {
		return rank;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Symbol &&
				((Symbol) obj).getLabel().equals(label) &&
				((Symbol) obj).getRank() == rank) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return label.hashCode() * 11 + rank * 17;
	}

	@Override
	public String toString() {
		return label;
	}

	@Override
	public int compareTo(Symbol o) {
		return o.label.compareTo(this.label);
	}
}
