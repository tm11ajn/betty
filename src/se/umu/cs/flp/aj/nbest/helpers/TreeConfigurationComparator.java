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
import se.umu.cs.flp.aj.nbest.util.LadderQueue.Configuration;

public class TreeConfigurationComparator<LabelType extends Comparable<LabelType>>
			implements Comparator<Configuration<TreeKeeper2<LabelType>>> {

	@Override
	public int compare(Configuration<TreeKeeper2<LabelType>> config1,
			Configuration<TreeKeeper2<LabelType>> config2) {

		Weight weight1 = null;
		Weight weight2 = null;

		int counter = 0;

		ArrayList<TreeKeeper2<LabelType>> list1 = config1.getValues();
		ArrayList<TreeKeeper2<LabelType>> list2 = config2.getValues();

		for (TreeKeeper2<LabelType> t : list1) {

			if (counter == 0) {
				weight1 = t.getRunWeight();
			} else {
				weight1 = weight1.mult(t.getRunWeight());
			}

			counter++;
		}

		counter = 0;

		for (TreeKeeper2<LabelType> t : list2) {

			if (counter == 0) {
				weight2 = t.getRunWeight();
			} else {
				weight2 = weight2.mult(t.getRunWeight());
			}

			counter++;
		}

		int comparison = weight1.compareTo(weight2);

		if (comparison !=  0) {
			return comparison;
		}

		String string1 = "";
		String string2 = "";

		for (TreeKeeper2<LabelType> t : list1) {
			string1 += t.getTree() + ", ";
		}

		for (TreeKeeper2<LabelType> t : list2) {
			string2 += t.getTree() + ", ";
		}

		return string1.compareTo(string2);
	}

}
