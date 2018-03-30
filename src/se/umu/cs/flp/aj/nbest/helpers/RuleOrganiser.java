package se.umu.cs.flp.aj.nbest.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import se.umu.cs.flp.aj.nbest.treedata.TreeKeeper;
import se.umu.cs.flp.aj.nbest.util.LadderQueue;
import se.umu.cs.flp.aj.nbest.wta.Rule;

public class RuleOrganiser {

	private ArrayList<Rule> rules;
//	private BinaryHeap<Rule,Semiring> weights;
	private HashMap<Rule,LadderQueue<TreeKeeper<?>>> ladders;
	private LinkedList<TreeKeeper<?>> queue;

	public RuleOrganiser(ArrayList<Rule> rules) {
		this.rules = rules;
//		this.weights = new BinaryHeap<>();
		this.ladders = new HashMap<>();
		initQueues();
	}

	private void initQueues() {

		for (Rule r : rules) {
			ladders.put(r, new LadderQueue<>(r.getRank(),
					new TreeConfigurationComparator()));
		}
	}

	public void update() {

	}

	public TreeKeeper<?> nextTree() {
		return queue.poll();
	}

}
