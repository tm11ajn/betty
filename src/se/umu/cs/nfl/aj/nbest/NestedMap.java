package se.umu.cs.nfl.aj.nbest;

import java.util.HashMap;

public class NestedMap<KeyType0, KeyType1, ValueType> {

//	private Map<KeyType0, Map<KeyType1, ValueType>> map = new HashMap<>();
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
		} else {
			return map.get(key0).put(key1, value);
		}
	}

	public ValueType get(KeyType0 key0, KeyType1 key1) {

		HashMap<KeyType1, ValueType> currentMap = map.get(key0);

		if (currentMap == null) {
			return null;
		} else {
			return map.get(key0).get(key1);
		}
	}

	// public ValueType remove(KeyType0 key0, KeyType1 key1)  {}

//	public boolean containsKey(KeyType0 key) {
//		return map.get(key) != null;
//	}

}
