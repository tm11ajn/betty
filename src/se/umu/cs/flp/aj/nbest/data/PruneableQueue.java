/*
 * Copyright 2015 Anna Jonsson for the research group Foundations of Language
 * Processing, Department of Computing Science, Umeå university
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
 */

package se.umu.cs.flp.aj.nbest.data;

import java.util.TreeMap;

public class PruneableQueue<K,V> extends TreeMap<K,V> {
	
	private static final long serialVersionUID = 1L;
	
	private Pruner<K,V> pruner;
	
	public PruneableQueue(Pruner<K,V> p) {
		super();
		this.pruner = p;
	}

	@Override
	public V put(K key, V value) {
		
//System.out.println("Putting " + key + "=" + value );
		
System.out.println("Queue size before pruning: " + this.size());
		V returnVal = super.put(key, value);
		pruner.prune(key, this);
System.out.println("Queue size after pruning: " + this.size());
		
		return returnVal;
	}

	@Override
	public V remove(Object key) {
		V returnVal = super.remove(key);
		return returnVal;
	}

}
