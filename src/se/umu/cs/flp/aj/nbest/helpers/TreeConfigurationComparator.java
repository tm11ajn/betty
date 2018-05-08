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

System.out.println("LIST 1 SIZE: " + list1.size());
System.out.println("LIST 2 SIZE: " + list2.size());

		for (TreeKeeper2<LabelType> t : list1) {

			if (counter == 0) {
//				weight1 = t.getSmallestWeight();
				weight1 = t.getRunWeight();
			} else {
//				weight1 = weight1.mult(t.getSmallestWeight());
				weight1 = weight1.mult(t.getRunWeight());
			}

			counter++;
		}

		counter = 0;

		for (TreeKeeper2<LabelType> t : list2) {

			if (counter == 0) {
//				weight2 = t.getSmallestWeight();
				weight2 = t.getRunWeight();
			} else {
//				weight2 = (Weight) weight2.mult(t.getSmallestWeight());
				weight2 = weight2.mult(t.getRunWeight());
			}

			counter++;
		}

//		if (weight1 == null && weight2 == null) {
//			return 0;
//		} else if (weight1 == null) {
//			return 1;
//		} else if (weight2 == null) {
//			return -1;
//		}

		int comparison = weight1.compareTo(weight2);

		if (comparison !=  0) {
			return comparison;
		}

		String string1 = "";
		String string2 = "";

System.out.println("COMPARING STRINGS ");

		for (TreeKeeper2<LabelType> t : list1) {
			string1 += t.getTree();
		}

		for (TreeKeeper2<LabelType> t : list2) {
			string2 += t.getTree();
		}

System.out.println("STRING 1: " + string1);
System.out.println("STRING 2: " + string2);

		return string1.compareTo(string2);
	}

}
