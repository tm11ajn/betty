package se.umu.cs.flp.aj.nbest.util;

import java.util.ArrayList;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.Configuration;
import se.umu.cs.flp.aj.nbest.treedata.TreeKeeper2;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.WTA;

public class ResultConnector {
	private TreeKeeper2[][] results;
	private int[] resCount;
	private int[][] connections;
	private ArrayList<ArrayList<Configuration<TreeKeeper2>>> configLists;
	private ArrayList<Rule> rules;
	private WTA wta;

	public ResultConnector(WTA wta, int maxResults) {
		int stateCount = wta.getStateCount();
		results = new TreeKeeper2[stateCount + 1][maxResults];
		resCount = new int[stateCount + 1];
		connections = new int[stateCount + 1][maxResults];
		configLists = new ArrayList<>();
		configLists.add(new ArrayList<>()); // Start from 1
		rules = wta.getRules();
		this.wta = wta;
	}

	public boolean makeConnections(Configuration<TreeKeeper2> config) {
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
				
				configLists.get(configIndex).add(config);
				missingElements++;
			}
		}

		config.decreaseLeftToValuesBy(size - missingElements);
		boolean activated = activateConfigIfReady(config);
		return activated;
	}

	/* Adds and propagates a result, and returns a list of ID's of rules 
	 * that have updated as a result of the propagation. */
	public ArrayList<Integer> addResult(int stateIndex, TreeKeeper2 result) {
		int resultIndex = resCount[stateIndex];
		results[stateIndex][resultIndex] = result;
		resCount[stateIndex] += 1;
		int configIndex = connections[stateIndex][resultIndex];
		ArrayList<Integer> needUpdate = new ArrayList<Integer>();

		for (Configuration<TreeKeeper2> config : configLists.get(configIndex)) {
			config.decreaseLeftToValuesBy(1);
			boolean wasReady = activateConfigIfReady(config);
			if (wasReady) {
				needUpdate.add(config.getOrigin().getID());
			}
		}
		
		return needUpdate;
	}
	
	/* Checks if the config is ready, that is, if all its required data is 
	 * in the results. Returns true if the input config triggers an update. */
	private boolean activateConfigIfReady(Configuration<TreeKeeper2> config) {
		boolean triggersUpdate = false;
		
		if (config.getLeftToValues() == 0) {
			TreeKeeper2[] result = extractResult(config);
			int size = config.getSize();
			Weight weight = wta.getSemiring().one(); // Default value for leaf rules
			
			for (int i = 0; i < size; i++) {
				TreeKeeper2 t1 = result[i];
				weight = weight.mult(t1.getRunWeight());
			}
			
			config.setValues(result);
			config.setWeight(weight);
			config.getOrigin().insert(config);
			triggersUpdate = true;
		}
		
		return triggersUpdate;
	}

	private TreeKeeper2[] extractResult(Configuration<TreeKeeper2> config) {
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
