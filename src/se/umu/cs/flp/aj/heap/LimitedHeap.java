package se.umu.cs.flp.aj.heap;

public class LimitedHeap<O, W extends Comparable<W>> {
	private BinaryHeap<O, W> minHeap;
	private BinaryHeap<O, W> maxHeap;
	private int limit;

	public LimitedHeap(int limit) {
		this.minHeap = new BinaryHeap<>();
		this.maxHeap = new BinaryHeap<>(false);
		this.limit = limit;
	}

	public boolean add(O object, W weight) {

		W maxWeight = maxHeap.getWeight(maxHeap.peek());

		if (maxWeight.compareTo(weight) <= 0) {
			return false;
		}

		minHeap.add(object, weight);
		maxHeap.add(object, weight);

		if (minHeap.size() > limit) {
			O max = maxHeap.peek();
//			minHeap.decreaseWeight(max); // Implement remove
		}

		return true;
	}

}
