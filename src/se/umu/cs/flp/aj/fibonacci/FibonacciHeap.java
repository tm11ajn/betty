package se.umu.cs.flp.aj.fibonacci;

import java.util.ArrayList;
import java.util.HashMap;

public class FibonacciHeap<V, P extends Comparable<P>> {
	private FibonacciNode<V, P> min;
	private HashMap<V, FibonacciNode<V, P>> valuesToNodes;

	public FibonacciHeap() {
		this.min = null;
		this.valuesToNodes = new HashMap<>();
	}

	public boolean add(V value, P priority) {

//System.out.println("Adding value=" + value + ", priority=" + priority + " to heap");

		if (valuesToNodes.containsKey(value)) {
//System.out.println("ALREADY CONTAINED KEY");
			return false;
		}

		FibonacciNode<V, P> newNode = new FibonacciNode<>(value, priority);
		valuesToNodes.put(value, newNode);

		if (min == null) {
			min = newNode;
		} else {
			concatenateSiblings(newNode, min);

			if (newNode.priority.compareTo(min.priority) < 0) {
				min = newNode;
			}
		}

		return true;
	}

	public V peek() {

		if (min == null) {
			return null;
		}

		return min.value;
	}

	public V dequeue() {

		if (min == null) {
			return null;
		}

		if (min.child != null) {
			FibonacciNode<V, P> temp = min.child;

			while (temp.parent != null) {
				temp.parent = null;
				temp = temp.next;
			}

			concatenateSiblings(temp, min);
		}

		FibonacciNode<V, P> oldMin = min;

		if (min.next.equals(min)) {
			min = null;
		} else {
			min = min.next;
			removeSibling(oldMin);
			merge();
		}

		valuesToNodes.remove(oldMin.value);

		return oldMin.value;
	}

	public boolean decreasePriority(V value, P priority) {
		FibonacciNode<V, P> node = valuesToNodes.get(value);

//System.out.println("Decreasing priority for " + value + " from " + node.priority + " to " + priority);

		if (node == null) {
			return false;
		} else if (node.priority.compareTo(priority) < 0) {
			return false;
		}

		node.priority = priority;
		FibonacciNode<V, P> parent = node.parent;

		if (parent != null && node.priority.compareTo(parent.priority) < 0) {
			cut(node, parent);
			cascadingCut(parent);
		}

		if (node.priority.compareTo(min.priority) < 0) {
			min = node;
		}

		return true;
	}

	public int size() {
		return valuesToNodes.size();
	}

	public boolean contains(V value) {
		return valuesToNodes.containsKey(value);
	}

	public P getPriority(V value) {

		if (valuesToNodes.get(value) == null) {
			return null;
		}

		return valuesToNodes.get(value).priority;
	}

	private void concatenateSiblings(FibonacciNode<V, P> node1,
			FibonacciNode<V, P> node2) {
		node1.next.prev = node2;
		node2.next.prev = node1;

		FibonacciNode<V, P> node1Next = node1.next;

		node1.next = node2.next;
		node2.next = node1Next;
	}

	private void removeSibling(FibonacciNode<V, P> node) {

		if (node.next.equals(node)) {
			return;
		}

		node.next.prev = node.prev;
		node.prev.next = node.next;
		node.next = node;
		node.prev = node;
	}

	private void merge() {
		int size = size();
//System.out.println(size);
		ArrayList<FibonacciNode<V, P>> newRoots = new ArrayList<>(size);

		for (int i = 0; i < size; i++) {
			newRoots.add(i, null);
		}

		FibonacciNode<V, P> current = min;
		FibonacciNode<V, P> start = min;

		do {
			FibonacciNode<V, P> node1 = current;
			int currentDegree = current.degree;

			while (newRoots.get(currentDegree) != null) {
				FibonacciNode<V, P> node2 = newRoots.get(currentDegree);

				if (node2.priority.compareTo(node1.priority) < 0) {
					FibonacciNode<V, P> temp = node1;
					node1 = node2;
					node2 = temp;
				}

				if (node2.equals(start)) {
					start = start.next;
				}

				if (node2.equals(current)) {
					current = current.prev;
				}

				link(node2, node1);

				newRoots.set(currentDegree, null);
				currentDegree++;
			}

			newRoots.add(currentDegree, node1);
			current = current.next;

		} while (!current.equals(start));

		min = null;

		for (FibonacciNode<V, P> n : newRoots) {
			if (n != null && (min == null ||
					n.priority.compareTo(min.priority) < 0)) {
				min = n;
			}
		}
	}

	private void link(FibonacciNode<V, P> node1, FibonacciNode<V, P> node2) {
		removeSibling(node1);
		node1.parent = node2;

		if (node2.child == null) {
			node2.child = node1;
		} else {
			concatenateSiblings(node2.child, node1);
		}

		node2.degree++;
		node1.marked = false;
	}

	private void cut(FibonacciNode<V, P> node1, FibonacciNode<V, P> node2) {

		if (node2.child.equals(node1)) {
			node2.child = node1.next;
		}

		if (node2.child.equals(node1)) {
			node2.child = null;
		}

		node2.degree--;
		removeSibling(node1);
		concatenateSiblings(node1, min);
		node1.parent = null;
		node1.marked = false;
	}

	private void cascadingCut(FibonacciNode<V, P> node) {
		FibonacciNode<V, P> parent = node.parent;

		if (parent != null) {

			if (!node.marked) {
				node.marked = true;
			} else {
				cut(node, parent);
				cascadingCut(parent);
			}
		}
	}

}
