package se.umu.cs.nfl.aj.nbest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import se.umu.cs.nfl.aj.nbest.wta.State;

public class NBest {

	private ArrayList<Node> exploredTrees; // T
	private PriorityQueue<Node> treeQueue; // K
	private NestedMap<Node, State, Double> treeStateValTable; // C

	public NBest() {
		reset();
	}

	public List<String> run(File automaton) {

		/* For result. */
		List<String> nBest = new ArrayList<String>();

		reset();

		return nBest;
	}

	private void readFile() {
		
	}

	private void reset() {
		exploredTrees = new ArrayList<Node>();
		treeQueue = new PriorityQueue<Node>(11, new TreeComparator());
		treeStateValTable = new NestedMap<>();
	}

}
