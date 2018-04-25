package se.umu.cs.flp.aj.heap;

import java.util.ArrayList;
import java.util.HashMap;

public class BinaryHeap<O, W extends Comparable<W>> {

	private ArrayList<Node<O, W>> nodes;
	private HashMap<O, Integer> positions;
	private boolean minHeap;


	public class Node<K, V> {
		K object;
		V weight;

		public Node(K object, V weight) {
			this.object = object;
			this.weight = weight;
		}
	}


	public BinaryHeap(boolean isMinHeap) {
		this.nodes = new ArrayList<>();
		this.positions = new HashMap<>();
		this.minHeap = isMinHeap;
	}

	public BinaryHeap() {
		this(true);
	}

	public int size () {
		return nodes.size();
	}

	public boolean empty() {
		return nodes.size() == 0;
	}

	public boolean contains(O object) {
		return positions.containsKey(object);
	}

	public O peek() {
		Node<O, W> min = nodes.get(0);
		return min.object;
	}

	public W getWeight(O object) {
		Integer position = positions.get(object);

		if (position == null) {
			return null;
		}

		return nodes.get(position).weight;
	}

	public void add(O object, W weight) {
		Node<O, W> newNode = new Node<>(object, weight);
		nodes.add(newNode);

		int size = nodes.size();
		int currentIndex = size - 1;

		positions.put(newNode.object, currentIndex);
		heapifyUp(currentIndex);
	}

	public void decreaseWeight(O object, W newWeight) {
		Integer currentIndex = positions.get(object);
		Node<O, W> currentNode = nodes.get(currentIndex);
		currentNode.weight = newWeight;
		heapifyUp(currentIndex);
	}

	public Node<O, W> dequeue() {
		int lastIndex = nodes.size() - 1;
		Node<O, W> first = nodes.get(0);
		Node<O, W> last = nodes.get(lastIndex);
		Node<O, W> min = new Node<>(first.object, first.weight);

		first.object = last.object;
		first.weight = last.weight;

		positions.remove(min.object);
		positions.put(first.object, 0);
		nodes.remove(lastIndex);

		int currentIndex = 0;
		heapifyDown(currentIndex);

		return min;
	}

	private void heapifyUp(int initialIndex) {
		int currentIndex = initialIndex;
		int parentIndex = (currentIndex - 1) / 2;
		boolean done = false;

		while (parentIndex > -1 && !done) {
			Node<O, W> parentNode = nodes.get(parentIndex);
			Node<O, W> currentNode = nodes.get(currentIndex);

			if (compare(currentNode.weight, parentNode.weight) < 0) {
				swap(parentNode, currentNode);
				updatePosition(nodes.get(parentIndex), parentIndex);
				updatePosition(nodes.get(currentIndex), currentIndex);
				currentIndex = parentIndex;
				parentIndex = (parentIndex - 1) / 2;
			} else {
				done = true;
			}
		}
	}

	private void heapifyDown(int initialIndex) {
		int currentIndex = initialIndex;
		int size = nodes.size();
		boolean done = false;

		while (!done) {
			int leftIndex = 2 * currentIndex + 1;
			int rightIndex = leftIndex + 1;

System.out.println("left=" + leftIndex);
System.out.println("right=" + rightIndex);

			if (leftIndex >= size) {
				done = true;
			} else {

				if (rightIndex >= size) {
					rightIndex = leftIndex;
System.out.println("HERE");
				}

				Node<O, W> left = nodes.get(leftIndex);
				Node<O, W> right = nodes.get(rightIndex);
				Node<O, W> current = nodes.get(currentIndex);

				int smallerIndex;
				Node<O, W> smaller;

				if (compare(left.weight, right.weight) <= 0) {
					smallerIndex = leftIndex;
					smaller = left;
				} else {
					smallerIndex = rightIndex;
					smaller = right;
				}

				if (compare(smaller.weight, current.weight) < 0) {
					swap(current, smaller);
					updatePosition(nodes.get(currentIndex), currentIndex);
					updatePosition(nodes.get(smallerIndex), smallerIndex);
				} else {
					done = true;
				}
			}
		}
	}

	private void updatePosition(Node<O, W> node, int index) {
		positions.put(node.object, index);
	}

	private void swap(Node<O, W> node1, Node<O, W> node2) {
		O object = node1.object;
		W weight = node1.weight;

		node1.object = node2.object;
		node1.weight = node2.weight;

		node2.object = object;
		node2.weight = weight;
	}

	private int compare(W weight1, W weight2) {

		if (minHeap) {
			return weight1.compareTo(weight2);
		}

		return weight2.compareTo(weight1);
	}

}
