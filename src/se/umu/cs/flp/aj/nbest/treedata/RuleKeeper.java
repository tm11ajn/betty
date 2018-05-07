package se.umu.cs.flp.aj.nbest.treedata;

import java.util.ArrayList;

import se.umu.cs.flp.aj.nbest.helpers.TreeConfigurationComparator;
import se.umu.cs.flp.aj.nbest.util.LadderQueue;
import se.umu.cs.flp.aj.nbest.wta.Rule;

public class RuleKeeper<LabelType extends Comparable<LabelType>> implements
			Comparable<RuleKeeper<LabelType>> {

//	private static HashMap<State, Weight> smallestCompletions;
//	private Node<LabelType> tree;
//	private LinkedHashMap<State,State> optimalStates;
//	private State optimalState;
//	private HashMap<State, Weight> optWeights;
//	private Weight smallestWeight;

	private Rule<LabelType> rule;
	private LadderQueue<TreeKeeper2<LabelType>> ladder;
	private TreeKeeper2<LabelType> smallestTree;
	private boolean paused;

	public RuleKeeper(Rule<LabelType> rule) {
		this.rule = rule;
		this.ladder = new LadderQueue<>(rule.getRank(),
				new TreeConfigurationComparator<LabelType>());
		this.smallestTree = null;
		this.paused = true;

		if (rule.getRank() == 0) {
			this.paused = false;
		}

//		if (rule.getRank() == 0) {
//			this.smallestTree = new TreeKeeper2<LabelType>(rule.getSymbol(),
//					rule.getWeight(), rule.getResultingState(),
//					new ArrayList<>());
//System.out.println("Adding tree for " + rule + " in rulekeeper constructor");
//		}
	}

//	public static void init(HashMap<State, Weight> smallestCompletionWeights) {
//		smallestCompletions = smallestCompletionWeights;
//	}

	public TreeKeeper2<LabelType> getSmallestTree() {
System.out.println("Rulekeeper returns smallesttree: " + smallestTree);
		if (smallestTree == null) {
			next();
		}

		return smallestTree;
	}

	public void addTreeForStateIndex(TreeKeeper2<LabelType> tree,
			int stateIndex) {
		ladder.addLast(stateIndex, tree);

		if (ladder.hasNext()) {
			paused = false;
		}

//		if (smallestTree == null) {
//			next();
//		}
	}

	public boolean paused() {
		return paused;
	}

	public void next() {

		if (ladder.hasNext()) {
			paused = false;
			ArrayList<TreeKeeper2<LabelType>> temp = ladder.dequeue();
			smallestTree = new TreeKeeper2<LabelType>(rule.getSymbol(),
					rule.getWeight(), rule.getResultingState(), temp);
System.out.println("New SmallestTree=" + smallestTree);
		} else {
			paused = true;
		}
	}

	public Rule<LabelType> getRule() {
		return rule;
	}

	@Override
	public int compareTo(RuleKeeper<LabelType> ruleKeeper) {
		return getSmallestTree().compareTo(ruleKeeper.getSmallestTree());
	}

	@Override
	public String toString() {
		return "Rule: " + rule + " smallest tree: " + smallestTree;
	}

}
