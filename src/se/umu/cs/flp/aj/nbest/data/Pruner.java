package se.umu.cs.flp.aj.nbest.data;

import java.util.TreeMap;

public interface Pruner<K,V> {
	
	public boolean prune(K insertedKey, TreeMap<K,V> map);
	
}
