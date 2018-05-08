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
		if (smallestTree == null) {
			next();
		}

System.out.println("Rulekeeper returns smallesttree: " + smallestTree);
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
System.out.println("next for rulekeeper " + rule + " with tree " + smallestTree);

		if (ladder.hasNext()) {
System.out.println("HASNEXT");
			paused = false;
			ArrayList<TreeKeeper2<LabelType>> temp = ladder.dequeue();
			smallestTree = new TreeKeeper2<LabelType>(rule.getSymbol(),
					rule.getWeight(), rule.getResultingState(), temp);

			if (!ladder.hasNext()) {
				paused = true;
			}
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

		int comparison =
				getSmallestTree().compareTo(ruleKeeper.getSmallestTree());
System.out.println("HEEEEEEEEEEEEEEEEEEEEEEEEEERE 11111111111111");
System.out.println("Compares " + this.rule + " with " + ruleKeeper.rule);
System.out.println("Result: " + comparison);

		if (comparison == 0) {
System.out.println("HEEEEEEEEEEEEEEEEEEEEEEEEEERE 22222222222");
System.out.println("Compares " + this.rule + " with " + ruleKeeper.rule);
System.out.println("Result: " + this.rule.getSymbol().compareTo(ruleKeeper.rule.getSymbol()));
			return this.rule.getSymbol().compareTo(ruleKeeper.rule.getSymbol());
		}

		return comparison;
	}

	@Override
	public String toString() {
		return "Rule: " + rule + " smallest tree: " + smallestTree;
	}

}
