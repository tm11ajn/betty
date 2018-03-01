package se.umu.cs.flp.aj.fibonacci;

public class FibonacciNode<V, P extends Comparable<P>> {
	protected V value;
	protected P priority;

	protected FibonacciNode<V, P> parent;
	protected FibonacciNode<V, P> prev;
	protected FibonacciNode<V, P> next;
	protected FibonacciNode<V, P> child;

	protected int degree;
	protected boolean marked;

	public FibonacciNode(V value, P priority) {
		this.value = value;
		this.priority = priority;

		this.parent = null;
		this.prev = this;
		this.next = this;
		this.child = null;

		this.degree = 0;
		this.marked = false;
	}

	public V getValue() {
		return value;
	}

	public P getPriority() {
		return priority;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof FibonacciNode<?, ?>) {
			FibonacciNode<?, ?> n = (FibonacciNode<?, ?>) o;

			if (n.value.equals(this.value) &&
					n.priority.equals(this.priority)) {
				return true;
			}
		}

		return false;
	}
}
