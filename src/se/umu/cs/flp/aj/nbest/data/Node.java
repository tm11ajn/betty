package se.umu.cs.flp.aj.nbest.data;

import java.util.ArrayList;
import java.util.List;

public class Node<LabelType> implements Comparable<Node<LabelType>> {

	private LabelType label;
	private Node<LabelType> parent;
	private List<Node<LabelType>> children = new ArrayList<Node<LabelType>>();
	private int nOfChildren;

	public Node(LabelType label) {
		this.label = label;
		nOfChildren = 0;
		parent = null;
	}

	public Node(LabelType label, List<Node<LabelType>> children) {
		this.label = label;
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
	}

	public Node<LabelType> getChildAt(int childIndex) {
		return children.get(childIndex);
	}

	public int getChildCount() {
		return nOfChildren;
	}

	public void setParent(Node<LabelType> parent) {
		this.parent = parent;
	}

	public Node<LabelType> getParent() {
		return parent;
	}

	public boolean isLeaf() {
		return nOfChildren == 0;
	}
	
	public int getSize() {
		int size = 0;
		
		for (Node<LabelType> child : children) {
			size += child.getSize();
		}
		
		return size + 1;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Node<?>) {
			Node<?> n = (Node<?>) obj;

			if (n.nOfChildren != this.nOfChildren) {
				return false;
			}

			if (n.isLeaf()) {
				return n.label.equals(this.label);
			}

			boolean allEqual = true;

			for (int i = 0; i < nOfChildren; i++) {

				if (!n.children.get(i).equals(this.children.get(i))) {
					allEqual = false;
				}
			}

			return allEqual && n.label.equals(this.label);
		}

		return false;
	}

	@Override
	public int hashCode() {

		int hash = 19 * this.label.hashCode();

		for (Node<LabelType> n : children) {
			hash += 11 *  n.hashCode();
		}

		return hash;
	}

	@Override
	public String toString() {

		String treeString = "" + label;

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

		return treeString;
	}

	@Override
	public int compareTo(Node<LabelType> o) {
		
		int thisSize = this.getSize();
		int oSize = o.getSize();
		
		if (thisSize < oSize) {
			return -1;
		} else if (thisSize > oSize) {
			return 1;
		}
		
		String thisString = this.toString();
		String oString = o.toString();
		
		if (thisString.compareTo(oString) < 0) {
			return -1;
		} else if (thisString.compareTo(oString) > 0) {
			return 1;
		}
		
		return 0;
	}
}
