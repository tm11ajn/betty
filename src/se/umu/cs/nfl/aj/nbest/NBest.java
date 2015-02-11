package se.umu.cs.nfl.aj.nbest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class NBest {

	private ArrayList<Node> exploredTrees; // T
	private PriorityQueue<Node> treeQueue; // K
	private HashMap<Node, Double> treeValTable; // C

	public NBest() {
		reset();
	}

	public List<String> run(File automaton) {

		/* For result. */
		List<String> nBest = new ArrayList<String>();

		reset();

		return nBest;
	}

	private void reset() {
		exploredTrees = new ArrayList<Node>();
		treeQueue = new PriorityQueue<Node>(11, new TreeComparator());
		treeValTable = new HashMap<Node, Double>();
	}

}
