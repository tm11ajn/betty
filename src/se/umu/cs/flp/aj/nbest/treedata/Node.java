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

import java.util.ArrayList;
import se.umu.cs.flp.aj.nbest.wta.Symbol;
import se.umu.cs.flp.aj.nbest.Timer;

public class Node implements Comparable<Node> {
	static public int N_EQUALS = 0;
	static public long TIME_EQUALS = 0;
	static public int N_COMPARE_TO = 0;
	static public long TIME_COMPARE_TO = 0;

	private Symbol label;
	private ArrayList<Node> children = new ArrayList<>();
	private ArrayList<Node> leaves = new ArrayList<>();
	private boolean validLeaves;

	private String treeString;
	private String treeRTGString;
	private int size;
	private int hash;
	private boolean validString;
	private boolean validRTGString;
	private boolean validSize;
	private boolean validHash;
	

	public Node(Symbol label) {
		this.label = label;

		treeString = "";
		size = 1;
		hash = 0;

		validLeaves = false;
		validString = false;
		validRTGString = false;
		validSize = true;
		validHash = false;
	}

	public Symbol getLabel() {
		return label;
	}

	public Node addChild(Node child) {
		children.add(child);
		validLeaves = false;
		validString = false;
		validRTGString = false;
		validSize = false;
		this.invalidateHash();
		return child;
	}

	public Node getChildAt(int childIndex) {
		return children.get(childIndex);
	}

	public int getChildCount() {
		return children.size();
	}

	public boolean isLeaf() {
		return children.size() == 0;
	}

	public ArrayList<Node> getLeaves() {

		if (validLeaves) {
			return this.leaves;
		}

		leaves = new ArrayList<>();
		validLeaves = true;

		if (this.isLeaf()) {
			leaves.add(this);
		} else {

			for (int i = 0; i < children.size(); i++) {
				ArrayList<Node> temp = this.getChildAt(i).getLeaves();
				leaves.addAll(temp);
			}
		}

		return leaves;
	}

	public int getNumberOfLeaves() {
		return getLeaves().size();
	}

	public int getSize() {

		if (validSize) {
			return size;
		}

		validSize = true;
		size = 1;

		for (Node child : children) {
			size += child.getSize();
		}

		return size;
	}

	private void invalidateHash() {
		this.validHash = false;
	}

	@Override
	public int hashCode() {

		if (validHash) {
			return hash;
		}

		validHash = true;
		hash = this.label.hashCode() + this.getSize();

		int counter = 1;

		for (Node n : children) {
			hash += counter * n.hashCode();
			counter += 2;
		}

		return hash;
	}

	public String toWTAString() {

		if (validString) {
			return treeString;
		}

		treeString = "" + label;

		if (!this.isLeaf()) {
			treeString += "[";

			int nOfChildren = children.size();
			for (int i = 0; i < nOfChildren; i++) {
				treeString += children.get(i);

				if (i != nOfChildren - 1) {
					treeString += ", ";
				}
			}

			treeString += "]";
		}
		
		treeString = treeString.intern();
		validString = true;

		return treeString;
	}

	public String toRTGString() {

		if (validRTGString) {
			return treeRTGString;
		}

		treeRTGString = "" + label;

		if (!this.isLeaf()) {
			treeRTGString += "(";

			int nOfChildren = children.size();
			for (int i = 0; i < nOfChildren; i++) {
				treeRTGString += children.get(i).toRTGString();

				if (i != nOfChildren - 1) {
					treeRTGString += " ";
				}
			}

			treeRTGString += ")";
		}

		treeString = treeString.intern();
		validRTGString = true;

		return treeRTGString;
	}

	@Override
	public String toString() {
		return toRTGString();
	}

	@Override
	public boolean equals(Object obj) {
		N_EQUALS++;
		Timer t = new Timer();
		t.start();
		
		boolean retVal = false;
		boolean decided = false;
		Node n = null;
		if (obj instanceof Node) {
			n = (Node) obj;

			if (this.hashCode() != obj.hashCode()) {
//				return false;
				retVal = false;
				decided = true;
			}
			
			if (!decided) {
				if (this.toString() == n.toString()) {
	//				return true;
					retVal = true;
					decided = true;
				}
			}
		}
		
		TIME_EQUALS += t.elapsed();

//		return false;
		return retVal;
	}

	@Override
	public int compareTo(Node o) {
		N_COMPARE_TO++;
		Timer t = new Timer();
		t.start();
		
		int thisSize = this.getSize();
		int oSize = o.getSize();
		int retVal = 0;
		boolean decided = false;

		if (thisSize < oSize) {
//			return -1;
			retVal = -1;
			decided = true;
		} else if (thisSize > oSize) {
//			return 1;
			retVal = 1;
			decided = true;
		}

		if (!decided) {
			if (this.children.size() < o.children.size()) {
//				return -1;
				retVal = -1;
				decided = true;
			} else if (o.children.size() > this.children.size()) {
//				return 1;
				retVal = 1;
				decided = true;
			}
		}
		
		String thisString = null;
		String oString = null;

		if (!decided) {
			thisString = this.toString();
			oString = o.toString();

			if (thisString.length() < oString.length()) {
//				return -1;
				retVal = -1;
				decided = true;
			} else if (thisString.length() > oString.length()) {
//				return 1;
				retVal = 1;
				decided = true;
			}
		}
		
		if (!decided) {
			if (thisString == oString) {
//				return 0;
				retVal = 0;
				decided = true;
			} else {
				retVal = thisString.compareTo(oString);
			}
		}

		TIME_COMPARE_TO += t.elapsed();
		return retVal;
	}
}
