/*
 * Copyright 2015 Anna Jonsson for the research group Foundations of Language
 * Processing, Department of Computing Science, Umeå university
 *
 * This file is part of Betty.
 *
 * Betty is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Betty is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.umu.cs.flp.aj.nbest.util;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class NestedMap<KeyType0, KeyType1, ValueType> {

	private HashMap<KeyType0, HashMap<KeyType1, ValueType>> map =
			new HashMap<>();

	public NestedMap() {

	}

	public ValueType put(KeyType0 key0, KeyType1 key1, ValueType value) {

		HashMap<KeyType1, ValueType> currentMap = map.get(key0);

		if (currentMap == null) {
			HashMap<KeyType1, ValueType> newMap = new HashMap<>();
			newMap.put(key1, value);
			map.put(key0, newMap);
			return null;
		}

		return map.get(key0).put(key1, value);
	}

	public ValueType get(KeyType0 key0, KeyType1 key1) {

		HashMap<KeyType1, ValueType> currentMap = map.get(key0);

		if (currentMap == null) {
			return null;
		}

		return map.get(key0).get(key1);
	}
	
	public HashMap<KeyType1, ValueType> get(KeyType0 key0) {
		return map.get(key0);
	}
	
	public HashMap<KeyType1, ValueType> getAll(KeyType0 key0) {
		return map.get(key0);
	}

	public boolean containsKey(KeyType0 key) {
		return map.containsKey(key);
	}
	
	public Set<KeyType0> keySet() {
		return map.keySet();
	}
	
	@Override
	public String toString() {
		
		String s = "";
		
		for (Entry<KeyType0, HashMap<KeyType1, ValueType>> i : map.entrySet()) {
			
			for (Entry<KeyType1, ValueType> j : i.getValue().entrySet()) {
				s.concat(j.toString());
			}
			
			s.concat("\n");
		}
		
		return s;
	}

}
