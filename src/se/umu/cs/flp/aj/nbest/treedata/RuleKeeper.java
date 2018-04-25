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

	public RuleKeeper(Rule<LabelType> rule) {
		this.rule = rule;
		this.ladder = new LadderQueue<>(rule.getRank(),
				new TreeConfigurationComparator<LabelType>());
		this.smallestTree = null;

		if (rule.getRank() == 0) {
			this.smallestTree = new TreeKeeper2<LabelType>(rule.getSymbol(),
					rule.getWeight(), rule.getResultingState(),
					new ArrayList<>());
System.out.println("Adding tree for " + rule + " in rulekeeper constructor");
		}
	}

//	public static void init(HashMap<State, Weight> smallestCompletionWeights) {
//		smallestCompletions = smallestCompletionWeights;
//	}

	public TreeKeeper2<LabelType> getSmallestTree() {
System.out.println("Rulekeeper returns smallesttree: " + smallestTree);
		return smallestTree;
	}

	public void addTreeForStateIndex(TreeKeeper2<LabelType> tree,
			int stateIndex) {
		ladder.addLast(stateIndex, tree);

if (ladder.isReady()) {
System.out.println("ladder for rule " + rule + " is ready");
}

		if (!ladder.isReady() || (ladder.hasConfig() && smallestTree == null)) {
			ArrayList<TreeKeeper2<LabelType>> tk = ladder.peek();

			if (tk != null) {
				smallestTree = new TreeKeeper2<LabelType>(rule.getSymbol(),
						rule.getWeight(), rule.getResultingState(), tk);
			}
		}
	}

	public boolean next() {

		if (!ladder.hasConfig()) {
			return false;
		} else {

			ArrayList<TreeKeeper2<LabelType>> temp = ladder.dequeue();

			System.out.println("In next: ");
			System.out.println("Rule = " + rule);
			System.out.println("temp = " + temp);

//			if (temp == null) {
//				return false;
//			} else {
//				ladder.dequeue();
//			}

			smallestTree = new TreeKeeper2<LabelType>(rule.getSymbol(),
					rule.getWeight(), rule.getResultingState(), temp);

System.out.println("New SmallestTree=" + smallestTree);

		}

		return true;
	}

	public Rule<LabelType> getRule() {
		return rule;
	}

	@Override
	public int compareTo(RuleKeeper<LabelType> ruleKeeper) {
		return smallestTree.compareTo(ruleKeeper.smallestTree);
	}

	@Override
	public String toString() {
		return "Rule: " + rule + " smallest tree: " + smallestTree;
	}

}
