package se.umu.cs.flp.aj.nbest.util;

import java.util.Iterator;

public class AppendList<T> implements Iterable<T> {
	
	private Element<T> head;
	private Element<T> foot;
	private int size;;
	
	static class Element<T> {
		T data;
		Element<T> prev;
		Element<T> next;
		
		Element(T data) {
			this.data = data;
			this.prev = null;
			this.next = null;
		}
	}
	
	static class AppendListIterator<T> implements Iterator<T> {
		Element<T> current; 
		
		AppendListIterator (AppendList<T> list) {
			this.current = list.head;
		}
		
		@Override
		public boolean hasNext() {
			return current.next.data != null;
		}

		@Override
		public T next() {
			this.current = this.current.next;
			return this.current.data;
		}
	}
	
	public AppendList() {
		this.head = new Element<T>(null);
		this.foot = new Element<T>(null);
		this.head.next = this.foot;
		this.foot.prev = this.head;
		this.size = 0;
	}

	public void appendLast(T data) {
		Element<T> newElem = new Element<T>(data);
		this.foot.prev.next = newElem;
		newElem.prev = this.foot.prev;
		newElem.next = this.foot;
		this.foot.prev = newElem;
		this.size++;
	}
	
	public AppendList<T> concatenate(AppendList<T> list) {
		this.foot.prev.next = list.head.next;
		list.head.next.prev = this.foot.prev;
		this.foot = list.foot;
		this.size = this.size + list.size;
		return this;
	}
	
	public T getFirst() {
		return this.head.next.data;
	}
	
	public T getLast() {
		return this.foot.prev.data;
	}
	
	public int size() {
		return this.size;
	}

	@Override
	public Iterator<T> iterator() {
		return new AppendListIterator<T>(this);
	}
}
