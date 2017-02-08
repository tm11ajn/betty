package se.umu.cs.flp.aj.nbest.data;

import java.util.Comparator;
import java.util.TreeMap;

public class PrunedQueue<K,V> extends TreeMap<K,V> {
	
	private static final long serialVersionUID = 1L;
	
	private Pruner<K,V> pruner;
//	private int maxSize;
	
	public PrunedQueue(Pruner<K,V> p) {
		super();
		this.pruner = p;
	}
	
	public PrunedQueue(Comparator<K> c, Pruner<K,V> p) {
		super(c);		
		this.pruner = p;
//		this.maxSize = maxSize;
	}

	@Override
	public V put(K key, V value) {
		
//System.out.println("Putting " + key + "=" + value );
		
		V returnVal = super.put(key, value);
		pruner.prune(key, this);
		
		return returnVal;
	}

	@Override
	public V remove(Object key) {
		V returnVal = super.remove(key);
		return returnVal;
	}

}
