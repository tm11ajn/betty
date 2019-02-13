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

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.NoFixedFacet;

import se.umu.cs.flp.aj.nbest.wta.Symbol;

public class Node implements Comparable<Node> {

	private Symbol label;
//	private Node parent;
	private ArrayList<Node> children = new ArrayList<>();
//	private int nOfChildren;

	private ArrayList<Node> leaves = new ArrayList<>();
	private boolean validLeaves;

	private String treeString;
	private int size;
	private int hash;
	private boolean validString;
	private boolean validSize;
	private boolean validHash;


	public Node(Symbol label) {
		this.label = label;
//		nOfChildren = 0;
//		parent = null;

		treeString = "";
		size = 1;
		hash = 0;

		validLeaves = false;
		validString = false;
		validSize = true;
		validHash = false;
	}

//	public Node(Symbol label, ArrayList<Node> children) {
//		this(label);
//
//		this.children = children;
//		nOfChildren = children.size();
//
//		for (Node n : children) {
//			n.setParent(this);
//		}
//	}

	public Symbol getLabel() {
		return label;
	}

	public Node addChild(Node child) {
		children.add(child);
//		child.setParent(this);
//		nOfChildren++;
		validLeaves = false;
		validString = false;
		validSize = false;
		this.invalidateHash();
		return child;
	}

	public Node getChildAt(int childIndex) {
		return children.get(childIndex);
	}

	public int getChildCount() {
//		return nOfChildren;
		return children.size();
	}

//	public void setParent(Node parent) {
//		this.parent = parent;
//	}
//
//	public Node getParent() {
//		return parent;
//	}

	public boolean isLeaf() {
//		return nOfChildren == 0;
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

//		if (this.parent != null) {
//			this.parent.invalidateHash();
//		}
	}

	@Override
	public int hashCode() {

		if (validHash) {
//System.out.println("Hashcode for " + this + " is " + hash);
			return hash;
		}

		validHash = true;
		hash = this.label.hashCode() + this.getSize();

//		int counter = 3;
		int counter = 1;

		for (Node n : children) {
			hash += counter * n.hashCode();
//			counter += 2;
		}

//		hash = this.toString().hashCode();

//System.out.println("Hashcode for " + this + " is " + hash);

		return hash;
	}

//	@Override
//	public String toString() {
//
//		if (validString) {
//			return treeString;
//		}
//
//		treeString = "" + label;
//
//		if (!this.isLeaf()) {
//			treeString += "[";
//
//			int nOfChildren = children.size();
//			for (int i = 0; i < nOfChildren; i++) {
//				treeString += children.get(i);
//
//				if (i != nOfChildren - 1) {
//					treeString += ", ";
//				}
//			}
//
//			treeString += "]";
//		}
//
//		validString = true;
//
//		return treeString;
//	}

	@Override
	public String toString() {

		if (validString) {
			return treeString;
		}

		treeString = "" + label;

		if (!this.isLeaf()) {
			treeString += "(";

			int nOfChildren = children.size();
			for (int i = 0; i < nOfChildren; i++) {
				treeString += children.get(i);

				if (i != nOfChildren - 1) {
					treeString += " ";
				}
			}

			treeString += ")";
		}

		validString = true;

		return treeString;
	}

	@Override
	public boolean equals(Object obj) {

		Node n = null;

		if (obj instanceof Node) {
			n = (Node) obj;

			if (this.hashCode() != obj.hashCode()) {
				return false;
			}

			if (this.compareTo(n) == 0) {
				return true;
			}
		}

		return false;
	}

	@Override
	public int compareTo(Node o) {

		int thisSize = this.getSize();
		int oSize = o.getSize();

		if (thisSize < oSize) {
			return -1;
		} else if (thisSize > oSize) {
			return 1;
		}

		if (this.children.size() < o.children.size()) {
			return -1;
		} else if (o.children.size() > this.children.size()) {
			return 1;
		}
//
//		for (int i = 0; i < children.size(); i++) {
//			int comparison = this.children.get(i).compareTo(o.children.get(i));
//
//			if (comparison != 0) {
//				return comparison;
//			}
//		}

		String thisString = this.toString();
		String oString = o.toString();

		int comparison = thisString.compareTo(oString);

		if (comparison > 0) {
			return 1;
		} else if (comparison < 0) {
			return -1;
		}

		return 0;
	}
}
