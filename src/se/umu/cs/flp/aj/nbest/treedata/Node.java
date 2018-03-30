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
import java.util.List;

public class Node<LabelType extends Comparable<LabelType>> implements Comparable<Node<?>> {

	private LabelType label;
	private Node<LabelType> parent;
	private List<Node<LabelType>> children = new ArrayList<Node<LabelType>>();
	private int nOfChildren;

	private String treeString;
	private int size;
	private int hash;
	private boolean validString;
	private boolean validSize;
	private boolean validHash;

	public Node(LabelType label) {
		this.label = label;
		nOfChildren = 0;
		parent = null;

		treeString = "";
		size = 1;
		hash = 0;

		validString = false;
		validSize = true;
		validHash = false;
	}

	public Node(LabelType label, List<Node<LabelType>> children) {
		this(label);

		this.children = children;
		nOfChildren = children.size();

		for (Node<LabelType> n : children) {
			n.setParent(this);
		}
	}

	public LabelType getLabel() {
		return label;
	}

	public void addChild(Node<LabelType> child) {
		children.add(child);
		child.setParent(this);
		nOfChildren++;
		validString = false;
		validSize = false;
		this.invalidateHash();
	}

	public Node<LabelType> getChildAt(int childIndex) {
		return children.get(childIndex);
	}

	public int getChildCount() {
		return nOfChildren;
	}

	public void setParent(Node<LabelType> parent) {
		this.parent = parent;
//		validHash = false;
	}

	public Node<LabelType> getParent() {
		return parent;
	}

	public boolean isLeaf() {
		return nOfChildren == 0;
	}

	public int getSize() {

		if (validSize) {
			return size;
		}

		validSize = true;
		size = 1;

		for (Node<LabelType> child : children) {
			size += child.getSize();
		}

		return size;
	}

	private void invalidateHash() {
		this.validHash = false;

		if (this.parent != null) {
			this.parent.invalidateHash();
		}
	}

	@Override
	public int hashCode() {

		if (validHash) {
			return hash;
		}

		validHash = true;
		hash = this.label.hashCode() + this.getSize();

		int counter = 3;

		for (Node<LabelType> n : children) {
			hash += counter * n.hashCode();
			counter += 2;
		}

		hash = this.toString().hashCode();

		return hash;
	}

	@Override
	public String toString() {

		if (validString) {
			return treeString;
		}

		treeString = "" + label;

		if (!this.isLeaf()) {
			treeString += "[";

			for (int i = 0; i < nOfChildren; i++) {
				treeString += children.get(i);

				if (i != nOfChildren - 1) {
					treeString += ", ";
				}
			}

			treeString += "]";
		}

		validString = true;

		return treeString;
	}

	@Override
	public boolean equals(Object obj) {

		Node<?> n = null;

		if (obj instanceof Node<?>) {
			n = (Node<?>) obj;

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
	public int compareTo(Node<?> o) {

		int thisSize = this.getSize();
		int oSize = o.getSize();

		if (thisSize < oSize) {
			return -1;
		} else if (thisSize > oSize) {
			return 1;
		}

		String thisString = this.toString();
		String oString = o.toString();

		return thisString.compareTo(oString);
	}
}
