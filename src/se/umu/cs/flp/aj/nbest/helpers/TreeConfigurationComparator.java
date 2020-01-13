/*
 * Copyright 2018 Anna Jonsson for the research group Foundations of Language
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

package se.umu.cs.flp.aj.nbest.helpers;

import java.util.ArrayList;
import java.util.Comparator;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.TreeKeeper2;
import se.umu.cs.flp.aj.nbest.treedata.Configuration;

public class TreeConfigurationComparator
			implements Comparator<Configuration<TreeKeeper2>> {


	@Override
	public int compare(
			Configuration<TreeKeeper2> config1,
			Configuration<TreeKeeper2> config2) {

		Weight weight1 = null;
		Weight weight2 = null;

		ArrayList<TreeKeeper2> list1 = config1.getValues();
		ArrayList<TreeKeeper2> list2 = config2.getValues();
		int size1 = config1.getSize();
		int size2 = config2.getSize();

		if (size1 < size2) {
			return -1;
		} else if (size1 > size2) {
			return 1;
		}
		
		weight1 = config1.getWeight();
		weight2 = config2.getWeight();

		int comparison = weight1.compareTo(weight2);

		if (comparison !=  0) {
			return comparison;
		}

		for (int i = 0; i < size1; i++) {
			TreeKeeper2 t1 = list1.get(i);
			TreeKeeper2 t2 = list2.get(i);
			int compare = t1.compareTo(t2);
			if (compare != 0) {
				return compare;
			}
		}

		return 0;
	}

}
