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
import se.umu.cs.flp.aj.nbest.util.LazyLimitedLadderQueue.Configuration;

public class TreeConfigurationComparator
			implements Comparator<Configuration<TreeKeeper2>> {

	@Override
	public int compare(Configuration<TreeKeeper2> config1,
			Configuration<TreeKeeper2> config2) {

		Weight weight1 = null;
		Weight weight2 = null;
//		Weight weight1 = config1.getWeight();
//		Weight weight2 = config2.getWeight();

		ArrayList<TreeKeeper2> list1 = config1.getValues();
		ArrayList<TreeKeeper2> list2 = config2.getValues();
		int size1 = list1.size();
		int size2 = list2.size();

		if (size1 < size2) {
			return -1;
		} else if (size1 > size2) {
			return 1;
		}

		for (int i = 0; i < size1; i++) {
			TreeKeeper2 t1 = list1.get(i);
			TreeKeeper2 t2 = list2.get(i);

			if (i == 0) {
				weight1 = t1.getRunWeight();
				weight2 = t2.getRunWeight();
//				weight1 = t1.getOptWeight();
//				weight2 = t2.getOptWeight();
			} else {
				weight1 = weight1.mult(t1.getRunWeight());
				weight2 = weight2.mult(t2.getRunWeight());
//				weight1 = weight1.mult(t1.getOptWeight());
//				weight2 = weight2.mult(t2.getOptWeight());
			}
		}

//		if (weight1 == null || weight2 == null) {
//
//			int counter = 0;
//
//			ArrayList<TreeKeeper2> list1 = config1.getValues();
//			ArrayList<TreeKeeper2> list2 = config2.getValues();
//			int size1 = list1.size();
//			int size2 = list2.size();
//
//			if (size1 < size2) {
//				return -1;
//			} else if (size1 > size2) {
//				return 1;
//			}
//
//			for (int i = 0; i < size1; i++) {
//				TreeKeeper2 t1 = list1.get(i);
//				TreeKeeper2 t2 = list2.get(i);
//
//				if (counter == 0) {
//					weight1 = t1.getRunWeight();
//					weight2 = t2.getRunWeight();
//				} else {
//					weight1 = weight1.mult(t1.getRunWeight());
//					weight2 = weight2.mult(t2.getRunWeight());
//				}
//
//				counter++;
//			}
//
//			config1.setWeight(weight1);
//			config2.setWeight(weight2);
//		}

		int comparison = weight1.compareTo(weight2);

		if (comparison !=  0) {
			return comparison;
		}

		for (int i = 0; i < list1.size(); i++) {
			TreeKeeper2 t1 = list1.get(i);
			TreeKeeper2 t2 = list2.get(i);
			int compare = t1.compareTo(t2);
			if (compare != 0) {
				return compare;
			}
		}

		return 0;

//		String string1 = "";
//		String string2 = "";
//
//		for (TreeKeeper2 t : list1) {
//			string1 += t.getTree() + ", ";
//		}
//
//		for (TreeKeeper2 t : list2) {
//			string2 += t.getTree() + ", ";
//		}
//
//		if (string1.length() < string2.length()) {
//			return -1;
//		} else if (string1.length() > string2.length()) {
//			return 1;
//		}
//
//		return string1.compareTo(string2);

//		return weight1.compareTo(weight2);

	}

}
