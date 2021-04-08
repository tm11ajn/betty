/*
 * Copyright 2020 Anna Jonsson for the research group Foundations of Language
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

import java.util.ArrayList;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.Configuration;
import se.umu.cs.flp.aj.nbest.treedata.Tree;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.WTA;

public class ResultConnector {
	private ArrayList<Tree>[] results;
	private int[] resCount;
	private int[] connections;
//	private ArrayList<ArrayList<Configuration<Tree>>> configLists;
	private ArrayList<ConfigLists> configLists;
	private ArrayList<Rule> rules;
	private WTA wta;
	
	private class ConfigLists {
		private ArrayList<Configuration<Tree>> current;
		private ArrayList<Configuration<Tree>> next;
		
		public ConfigLists() {
			this.current = new ArrayList<>();
			this.next = new ArrayList<>();
		}
		
		public ArrayList<Configuration<Tree>> getCurrent() {
			return current;
		}
		public void setCurrent(ArrayList<Configuration<Tree>> current) {
			this.current = current;
		}
		public ArrayList<Configuration<Tree>> getNext() {
			return next;
		}
		public void setNext(ArrayList<Configuration<Tree>> next) {
			this.next = next;
		}
	}

	@SuppressWarnings("unchecked")
	public ResultConnector(WTA wta, int maxResults) {
		int stateCount = wta.getStateCount();
		results = new ArrayList[stateCount + 1];
		resCount = new int[stateCount + 1];
		connections = new int[stateCount + 1];
		configLists = new ArrayList<>();
		configLists.add(new ConfigLists()); // Start from 1
//		configLists.add(new ArrayList<>()); // Start from 1
		rules = wta.getRules();
		this.wta = wta;
	}

	/* Connects a config to the elements that it is waiting for. Activates
	 * the config if it is not waiting for any elements. */
	public boolean makeConnections(Configuration<Tree> config) {
		int[] indices = config.getIndices();
		int size = indices.length;
		int missingElements = 0;
		Rule rule = rules.get(config.getOrigin().getID());

		for (int i = 0; i < size; i++) {
			int currentState = rule.getStates().get(i).getID(); 

//			if (indices[i] >= resCount[currentState]) {
			if (indices[i] == resCount[currentState]) {
				int configIndex = connections[currentState];
				
				if (configIndex == 0) {
					configIndex = configLists.size();
					connections[currentState] = configIndex;
//					configLists.add(new ArrayList<>());
					configLists.add(new ConfigLists());
				}
				
//				configLists.get(configIndex).add(config);
				configLists.get(configIndex).getCurrent().add(config);
				missingElements++;
			} else if (indices[i] > resCount[currentState]) {
				int configIndex = connections[currentState];
				
				if (configIndex == 0) {
					configIndex = configLists.size();
					connections[currentState] = configIndex;
					configLists.add(new ConfigLists());
				}
				
				configLists.get(configIndex).getNext().add(config);
				missingElements++;
			}
		}

		config.decreaseLeftToValuesBy(size - missingElements);
		boolean activated = activateConfigIfReady(config);
		return activated;
	}

	/* Adds and propagates a result, and returns a list of ID's of rules 
	 * that have updated as a result of the propagation. */
	public ArrayList<Integer> addResult(Tree result) {
		int stateIndex = result.getResultingState().getID();
//System.out.println("Add result " + result);
		ArrayList<Integer> needUpdate = new ArrayList<Integer>();
		
		if (results[stateIndex] == null) {
			results[stateIndex] = new ArrayList<>();
		}

		results[stateIndex].add(result);
		resCount[stateIndex] += 1;

		int configIndex = connections[stateIndex];

//		for (Configuration<Tree> config : configLists.get(configIndex)) {
		for (Configuration<Tree> config : configLists.get(configIndex).getCurrent()) {
//System.out.println("Rescount for stateindex " + resCount[stateIndex]);
//System.out.println("Config decreaselefttovalue " + config + "has left " + config.getLeftToValues());
			config.decreaseLeftToValuesBy(1);
			boolean wasReady = activateConfigIfReady(config);
			if (wasReady) {
//System.out.println("Needs update");
				needUpdate.add(config.getOrigin().getID());
			}
		}
		
//		configLists.set(configIndex, new ArrayList<>());
		configLists.get(configIndex).setCurrent(configLists.get(configIndex).getNext());
		configLists.get(configIndex).setNext(new ArrayList<>());

		return needUpdate;
	}
	
	/* Checks if the config is ready, that is, if all its required data is 
	 * in the results. Returns true if the input config triggers an update. */
	private boolean activateConfigIfReady(Configuration<Tree> config) {
		boolean triggersUpdate = false;
		
		if (config.getLeftToValues() == 0) {
//System.out.println("Config: " + config);
			Tree[] result = extractResult(config);
			int size = config.getSize();
			Weight weight = wta.getSemiring().one(); // Default value for leaf rules
			
			for (int i = 0; i < size; i++) {
				Tree t1 = result[i];
				weight = weight.mult(t1.getRunWeight());
			}
			
			config.setValues(result);
			config.setWeight(weight);
			config.getOrigin().insert(config);
			triggersUpdate = true;
		}
		
		return triggersUpdate;
	}

	/* Returns the tree that corresponds to the input configuration. */
	private Tree[] extractResult(Configuration<Tree> config) {
		int[] indexList = config.getIndices();
		int ruleIndex = config.getOrigin().getID();
		Rule rule = rules.get(ruleIndex);
		int size = indexList.length;
		Tree[] result = new Tree[size];

		for (int i = 0; i < size; i++) {
			result[i] = results[rule.getStates().get(i).getID()].get(indexList[i]);
		}

		return result;
	}


	public Tree getResult(int stateIndex, int resultIndex) {
		return results[stateIndex].get(resultIndex);
	}
	
	public int getResultSize(int stateIndex) {
		
		if (results[stateIndex] == null) {
			return 0;
		}
		
		return resCount[stateIndex];
//		return results[stateIndex].size();
	}
	
	public boolean isUnseen(int stateIndex) {
		return results[stateIndex] == null;
	}
	
}
