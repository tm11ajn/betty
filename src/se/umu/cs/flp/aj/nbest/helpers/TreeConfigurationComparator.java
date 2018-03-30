package se.umu.cs.flp.aj.nbest.helpers;

import java.util.ArrayList;
import java.util.Comparator;

import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.treedata.TreeKeeper;

public class TreeConfigurationComparator<LabelType extends Comparable<LabelType>> implements Comparator<ArrayList<TreeKeeper<LabelType>>> {

	@Override
	public int compare(ArrayList<TreeKeeper<LabelType>> list1,
			ArrayList<TreeKeeper<LabelType>> list2) {

		Semiring weight1 = null;
		Semiring weight2 = null;

		int counter = 0;

		for (TreeKeeper<?> t : list1) {

			if (counter == 0) {
				weight1 = t.getSmallestWeight();
			} else {
				weight1 = (Semiring) weight1.mult(t.getSmallestWeight());
			}

			counter++;
		}

		counter = 0;

		for (TreeKeeper<?> t : list2) {

			if (counter == 0) {
				weight2 = t.getSmallestWeight();
			} else {
				weight2 = (Semiring) weight2.mult(t.getSmallestWeight());
			}
		}

		if (weight1 == null && weight2 == null) {
			return 0;
		} else if (weight1 == null) {
			return 1;
		} else if (weight2 == null) {
			return -1;
		}

		return weight1.compareTo(weight2);
	}

}
