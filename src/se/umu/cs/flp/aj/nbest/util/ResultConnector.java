package se.umu.cs.flp.aj.nbest.util;

import java.util.ArrayList;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.Configuration;
import se.umu.cs.flp.aj.nbest.treedata.TreeKeeper2;
import se.umu.cs.flp.aj.nbest.wta.Rule;

public class ResultConnector {
	private TreeKeeper2[][] results;
	private int[] resCount;
	private int[][] connections;
	private ArrayList<ArrayList<ConfigStateIndexPair>> configLists;
	private ArrayList<Rule> rules;
	
	/*
	 * TODO: Enqueue only configurations if it turns out that the 
	 * state index is never needed.
	 */
	private class ConfigStateIndexPair {
		Configuration<TreeKeeper2> config;
		int state;
		
		public ConfigStateIndexPair(Configuration<TreeKeeper2> config, 
				int state) {
			this.config = config;
			this.state = state;
		}
		
		public Configuration<TreeKeeper2> getConfig() {
			return config;
		}
		
		public int getStateIndex() {
			return state;
		}	
	}

	public ResultConnector(int stateCount, int maxResults, 
			ArrayList<Rule> rules) {
		results = new TreeKeeper2[stateCount + 1][maxResults];
		resCount = new int[stateCount + 1];
		connections = new int[stateCount + 1][maxResults];
		configLists = new ArrayList<>((stateCount + 1)*maxResults);
		configLists.add(new ArrayList<>()); // Start from 1
		this.rules = rules;
	}
	
	public void step() {
		
	}

	public void makeConnections(Configuration<TreeKeeper2> config) {
		int[] indices = config.getIndices();
		int size = indices.length;
		int missingElements = 0;
		Rule rule = rules.get(config.getOrigin().getID());

		for (int i = 0; i < size; i++) {
			int currentState = rule.getStates().get(i).getID();

			if (indices[i] >= resCount[currentState]) {
				int configIndex = connections[currentState][indices[i]];
				if (configIndex == 0) {
					configIndex = configLists.size();
					connections[currentState][indices[i]] = configIndex;
					configLists.add(new ArrayList<>());
				}
				configLists.get(currentState).add(
						new ConfigStateIndexPair(config, i));
				missingElements++;
			}
		}

		config.decreaseLeftToValuesBy(size - missingElements);
		activateConfigIfReady(config);
	}

	/*
	 * TODO: Return a list of ID's of rules that need updates in 
	 * the rule queue after this?
	 */
	public void addResult(int stateIndex, TreeKeeper2 result) {
		int resultIndex = resCount[stateIndex];
		results[stateIndex][resultIndex] = result;
		resCount[stateIndex] += 1;
		int configIndex = connections[stateIndex][resultIndex];

		for (ConfigStateIndexPair csi : configLists.get(configIndex)) {
			Configuration<TreeKeeper2> config = csi.getConfig();
			if (config.getLeftToValues() != 0) {
				config.decreaseLeftToValuesBy(1);
				activateConfigIfReady(config);
			}
		}
	}
	
	/* TODO: Return true if the input config triggers an update? */
	private void activateConfigIfReady(Configuration<TreeKeeper2> config) {
		
		if (config.getLeftToValues() == 0) {
			ArrayList<TreeKeeper2> result = extractResult(config);
			int size = config.getSize();
			Weight weight = null;
			
			for (int i = 0; i < size; i++) {
				TreeKeeper2 t1 = result.get(i);

				if (i == 0) {
					weight = t1.getRunWeight();
				} else {
					weight = weight.mult(t1.getRunWeight());
				}
			}
			
			/* It can happen that a better config arrives late to the party. 
			 * This piece of code takes care of that. */
			if (config.getOrigin().peek().getWeight().compareTo(weight) > 0) {
				config.getOrigin().setNeedsUpdate(true);
			}
			
			config.setValues(result);
			config.setWeight(weight);
			config.getOrigin().insert(config);
		}
	}

	private ArrayList<TreeKeeper2> extractResult(Configuration<TreeKeeper2> config) {
		int[] indexList = config.getIndices();
		int ruleIndex = config.getOrigin().getID();
		Rule rule = rules.get(ruleIndex);
		int size = indexList.length;
		ArrayList<TreeKeeper2> result = new ArrayList<>(size);

		for (int i = 0; i < size; i++) {
			result.add(results[rule.getStates().get(i).getID()][indexList[i]]);
		}

		return result;
	}


	public TreeKeeper2 getResult(int stateIndex, int resultIndex) {
		return results[stateIndex][resultIndex];
	}

}
