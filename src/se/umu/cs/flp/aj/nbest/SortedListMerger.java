/*
 * Copyright 2017 Anna Jonsson for the research group Foundations of Language
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

package se.umu.cs.flp.aj.nbest;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import se.umu.cs.flp.aj.nbest.data.Node;
import se.umu.cs.flp.aj.nbest.data.TreeKeeper;
import se.umu.cs.flp.aj.wta.State;
import se.umu.cs.flp.aj.wta.Symbol;
import se.umu.cs.flp.aj.wta.Weight;

public class SortedListMerger {
	
	public static LinkedHashMap<Node<Symbol>,TreeKeeper<Symbol>> mergeTreeListsForState(
			LinkedHashMap<Node<Symbol>,TreeKeeper<Symbol>> list1, 
			LinkedHashMap<Node<Symbol>,TreeKeeper<Symbol>> list2,
			int listSizeLimit, State q) {

//System.out.println("MERGE FOR STATE " + q);
		
		if (list1.isEmpty()) {
//System.out.println("Merge for state: returning: " + list2);
			return list2;
		}
		
		if (list2.isEmpty()) {
//System.out.println("Merge for state: returning: " + list1);
			return list1;
		}
		
		LinkedHashMap<Node<Symbol>,TreeKeeper<Symbol>> result = new LinkedHashMap<>();

		int added = 0;
		int compResult;
		
		Iterator<Entry<Node<Symbol>, TreeKeeper<Symbol>>> iterator1 = list1.entrySet().iterator();
		Iterator<Entry<Node<Symbol>, TreeKeeper<Symbol>>> iterator2 = list2.entrySet().iterator();
		
		Entry<Node<Symbol>, TreeKeeper<Symbol>> currentEntry1 = null;
		Entry<Node<Symbol>, TreeKeeper<Symbol>> currentEntry2 = null;
		
		if (iterator1.hasNext()) {
			currentEntry1 = iterator1.next();
		} 
		
		if (iterator2.hasNext()) {
			currentEntry2 = iterator2.next();
		}

		while (added < listSizeLimit && !(currentEntry1 == null && currentEntry2 == null)) {
			
//System.out.println("CurrentEntry1 = " + currentEntry1 + " currentEntry2 = " + currentEntry2);
			
			if (currentEntry1 != null && result.containsKey(currentEntry1.getKey())) {
				result.get(currentEntry1.getKey()).addWeightsFrom(currentEntry1.getValue());
				iterator1.remove();
				currentEntry1 = null;
			} else if (currentEntry2 != null && result.containsKey(currentEntry2.getKey())) {
				result.get(currentEntry2.getKey()).addWeightsFrom(currentEntry2.getValue());
				iterator2.remove();
				currentEntry2 = null;
			} else if (currentEntry1 != null && currentEntry2 != null) {
				Weight weight1 = currentEntry1.getValue().getWeight(q);
				Weight weight2 = currentEntry2.getValue().getWeight(q);
				
				compResult = weight1.compareTo(weight2);
				
				if (compResult == -1) {
					result.put(currentEntry1.getKey(), currentEntry1.getValue());
					iterator1.remove();
					currentEntry1 = null;
				} else if (compResult == 1) {
					result.put(currentEntry2.getKey(), currentEntry2.getValue());
					iterator2.remove();
					currentEntry2 = null;
				} else {
					compResult = currentEntry1.getKey().compareTo(currentEntry2.getKey());
					
					if (compResult == 0) {
						currentEntry1.getValue().addWeightsFrom(currentEntry2.getValue());
						result.put(currentEntry1.getKey(), currentEntry1.getValue());
						iterator1.remove();
						iterator2.remove();
						currentEntry1 = null; 
						currentEntry2 = null;
					} else if (compResult == -1) {
						result.put(currentEntry1.getKey(), currentEntry1.getValue());
						iterator1.remove();
						currentEntry1 = null; 
					} else {
						result.put(currentEntry2.getKey(), currentEntry2.getValue());
						iterator2.remove();
						currentEntry2 = null; 
					}
				}
				
			} else if (currentEntry1 != null) {
				result.put(currentEntry1.getKey(), currentEntry1.getValue());
				iterator1.remove();
				currentEntry1 = null; 
			} else {
				result.put(currentEntry2.getKey(), currentEntry2.getValue());
				iterator2.remove();
				currentEntry2 = null; 
			}

			added++;
			
			if (currentEntry1 == null && iterator1.hasNext()) {
				currentEntry1 = iterator1.next();
			}
			
			if (currentEntry2 == null && iterator2.hasNext()) {
				currentEntry2 = iterator2.next();
			}
			
//System.out.println("Merge for state: current result: " + result);			
		}
//System.out.println("Merge for state: returning: " + result);
		return result;
	}

	public static LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>> mergeTreeListsByDeltaWeights(
			LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>> currentList, 
			LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>> mergedList,
			int listSizeLimit) {

//System.out.println("MERGE list by delta weights");		
		if (currentList.isEmpty()) {
//System.out.println("Merge: returning: " + mergedList);
			return mergedList;
		}
		
		if (mergedList.isEmpty()) {
//System.out.println("Merge: returning: " + currentList);
			return currentList;
		}

//		LinkedList<TreeKeeper<Symbol>> result = new LinkedList<>();
		LinkedHashMap<Node<Symbol>, TreeKeeper<Symbol>> result = new LinkedHashMap<>();

		int added = 0;
		int compResult;

		Iterator<Entry<Node<Symbol>, TreeKeeper<Symbol>>> iterator1 = currentList.entrySet().iterator();
		Iterator<Entry<Node<Symbol>, TreeKeeper<Symbol>>> iterator2 = mergedList.entrySet().iterator();
		
		Entry<Node<Symbol>, TreeKeeper<Symbol>> currentEntry1 = null;
		Entry<Node<Symbol>, TreeKeeper<Symbol>> currentEntry2 = null;
		
		if (iterator1.hasNext()) {
			currentEntry1 = iterator1.next();
		} 
		
		if (iterator2.hasNext()) {
			currentEntry2 = iterator2.next();
		}

		while (added < listSizeLimit && !(currentEntry1 == null && currentEntry2 == null)) {
//System.out.println("CurrentEntry1 = " + currentEntry1 + " currentEntry2 = " + currentEntry2);

			if (currentEntry1 != null && result.containsKey(currentEntry1.getKey())) {
				result.get(currentEntry1.getKey()).addWeightsFrom(currentEntry1.getValue());
				iterator1.remove();
				currentEntry1 = null; 
			} else if (currentEntry2 != null && result.containsKey(currentEntry2.getKey())) {
				result.get(currentEntry2.getKey()).addWeightsFrom(currentEntry2.getValue());
				iterator2.remove();
				currentEntry2 = null; 
			} else if (currentEntry1 != null && currentEntry2 != null) {				
				
				Weight weight1 = currentEntry1.getValue().getDeltaWeight();
				Weight weight2 = currentEntry2.getValue().getDeltaWeight();

				compResult = weight1.compareTo(weight2);
				
				if (compResult == -1) {
					result.put(currentEntry1.getKey(), currentEntry1.getValue());
					iterator1.remove();
					currentEntry1 = null;
				} else if (compResult == 1) {
					result.put(currentEntry2.getKey(), currentEntry2.getValue());
					iterator2.remove();
					currentEntry2 = null;
				} else {
					compResult = currentEntry1.getKey().compareTo(currentEntry2.getKey());
					
					if (compResult == 0) {
						currentEntry1.getValue().addWeightsFrom(currentEntry2.getValue());
						result.put(currentEntry1.getKey(), currentEntry1.getValue());
						iterator1.remove();
						iterator2.remove();
						currentEntry1 = null;
						currentEntry2 = null; 
					} else if (compResult == -1) {
						result.put(currentEntry1.getKey(), currentEntry1.getValue());
						iterator1.remove();
						currentEntry1 = null;
					} else {
						result.put(currentEntry2.getKey(), currentEntry2.getValue());
						iterator2.remove();
						currentEntry2 = null;
					}
				}
				
			} else if (currentEntry1 != null) {
				result.put(currentEntry1.getKey(), currentEntry1.getValue());
				iterator1.remove();
				currentEntry1 = null;
			} else {
				result.put(currentEntry2.getKey(), currentEntry2.getValue());
				iterator2.remove();
				currentEntry2 = null;
			}
			
			added++;
			
			if (currentEntry1 == null && iterator1.hasNext()) {
				currentEntry1 = iterator1.next();
			}
			
			if (currentEntry2 == null && iterator2.hasNext()) {
				currentEntry2 = iterator2.next();
			}
			
//System.out.println("Merge: current result: " + result);
		}
		
//System.out.println("Merge: returning: " + result);
		
		return result;
	}
}
