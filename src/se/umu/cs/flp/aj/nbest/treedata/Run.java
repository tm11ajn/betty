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
 *
 * Created in 2015 by aj.
 * Modified in 2017 by aj.
 */

package se.umu.cs.flp.aj.nbest.treedata;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.Symbol;

public class Run implements Comparable<Run> {

	private TreeKeeper<Symbol> tree;

	private Weight weight;

	public Run(TreeKeeper<Symbol> tree, Weight weight) {
		this.tree = tree;
		this.weight = weight;
	}

	public TreeKeeper<Symbol> getTree() {
		return this.tree;
	}

	public Weight getWeight() {
		return this.weight;
	}

	@Override
	public int compareTo(Run o) {
		return this.getWeight().compareTo(o.getWeight());
	}

}
