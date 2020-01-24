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
//	private int maxResults;
	
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
//		this.maxResults = maxResults;
	}

	public boolean makeConnections(Configuration<TreeKeeper2> config) {
//System.out.println("ResultConnector: makeConnections");
		int[] indices = config.getIndices();
		int size = indices.length;
		int missingElements = 0;
		Rule rule = rules.get(config.getOrigin().getID());

		for (int i = 0; i < size; i++) {
			int currentState = rule.getStates().get(i).getID();

//System.out.println("indices[i]=" + indices[i]);
//System.out.println("resCount[currentState]=" + resCount[currentState]);
			if (indices[i] >= resCount[currentState]) {
				int configIndex = connections[currentState][indices[i]];
				
				if (configIndex == 0) {
					configIndex = configLists.size();
					connections[currentState][indices[i]] = configIndex;
					configLists.add(new ArrayList<>());
				}
				
				configLists.get(configIndex).add(
						new ConfigStateIndexPair(config, i));
				missingElements++;
			}
		}
//System.out.println("missing elements = " + missingElements);

		config.decreaseLeftToValuesBy(size - missingElements);
		boolean activated = activateConfigIfReady(config);
		return activated;
	}

	/*
	 * TODO: Return a list of ID's of rules that need updates in 
	 * the rule queue after this?
	 */
	public ArrayList<Integer> addResult(int stateIndex, TreeKeeper2 result) {
//System.out.println("ResultConnector: addResult");
		int resultIndex = resCount[stateIndex];
//		if (resultIndex >= maxResults) {
//			return new ArrayList<>();
//		}
		results[stateIndex][resultIndex] = result;
System.out.println("New result for state " + stateIndex + "; " + result.getResultingState() + " length: " + results[stateIndex].length + " for indices " + stateIndex + " and " + resultIndex);
		resCount[stateIndex] += 1;
		int configIndex = connections[stateIndex][resultIndex];
		ArrayList<Integer> needUpdate = new ArrayList<Integer>();

		for (ConfigStateIndexPair csi : configLists.get(configIndex)) {
			Configuration<TreeKeeper2> config = csi.getConfig();
//System.out.println("Current config");
//			if (config.getLeftToValues() != 0) {
//System.out.println("DecreaseLeftToValuesBy1");
				config.decreaseLeftToValuesBy(1);
				boolean wasReady = activateConfigIfReady(config);
				if (wasReady) {
					needUpdate.add(config.getOrigin().getID());
				}
//			}
		}
		
System.out.println("Need update count: " + needUpdate.size());
		return needUpdate;
	}
	
	/* Checks if the config is ready, that is, if all its required data is 
	 * in the results. Returns true if the input config triggers an update. */
	private boolean activateConfigIfReady(Configuration<TreeKeeper2> config) {
//System.out.println("ResultConnector: activateConfigIfReady");
		boolean triggersUpdate = false;
//System.out.println("Config: " + config);
//System.out.println("Haslefttovalues? " + config.getLeftToValues());
		
		if (config.getLeftToValues() == 0) {
//System.out.println("Activated");
			TreeKeeper2[] result = extractResult(config);
			int size = config.getSize();
			Weight weight = config.getWeight(); // Set if leaf, else null
			
			for (int i = 0; i < size; i++) {
				TreeKeeper2 t1 = result[i];
//System.out.println("t1=" + t1);

				if (i == 0) {
					weight = t1.getRunWeight();
				} else {
					weight = weight.mult(t1.getRunWeight());
				}
			}
			
			/* It can happen that a better config arrives late to the party. 
			 * This piece of code takes care of that. */
//			if ((config.getOrigin().isEmpty()) || 
//					(!config.getOrigin().isEmpty() &&
//					config.getOrigin().peek().getWeight().compareTo(weight) > 0)) {
////			if (!config.getOrigin().isEmpty() && 
////					config.getOrigin().peek().getWeight().compareTo(weight) > 0) {
////				config.getOrigin().setNeedsUpdate(true);
//				triggersUpdate = true;
//System.out.println("Triggers update (late to party)");
//			}
			
			triggersUpdate = true;
			config.setValues(result);
			config.setWeight(weight);
			config.getOrigin().insert(config);
		}
		
		return triggersUpdate;
	}

	private TreeKeeper2[] extractResult(Configuration<TreeKeeper2> config) {
//System.out.println("ResultConnector: extractResult");
		int[] indexList = config.getIndices();
		int ruleIndex = config.getOrigin().getID();
		Rule rule = rules.get(ruleIndex);
		int size = indexList.length;
		TreeKeeper2[] result = new TreeKeeper2[size];

		for (int i = 0; i < size; i++) {
			result[i] = results[rule.getStates().get(i).getID()][indexList[i]];
		}

		return result;
	}


	public TreeKeeper2 getResult(int stateIndex, int resultIndex) {
		return results[stateIndex][resultIndex];
	}
	
}
