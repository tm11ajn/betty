package se.umu.cs.flp.aj.nbest.treedata;

import java.util.ArrayList;

import se.umu.cs.flp.aj.nbest.helpers.TreeConfigurationComparator;
import se.umu.cs.flp.aj.nbest.util.LadderQueue;
import se.umu.cs.flp.aj.nbest.wta.Rule;

public class RuleKeeper<LabelType extends Comparable<LabelType>> implements
			Comparable<RuleKeeper<LabelType>> {

	private Rule<LabelType> rule;
	private LadderQueue<TreeKeeper<LabelType>> ladder;
	private TreeKeeper<LabelType> smallestTree;

	public RuleKeeper(Rule<LabelType> rule) {
		this.rule = rule;
		this.ladder = new LadderQueue<>(rule.getRank(),
				new TreeConfigurationComparator<LabelType>());
	}

	public TreeKeeper<LabelType> getSmallestTree() {
		return smallestTree;
	}

	public void addLast(int rankIndex, TreeKeeper<LabelType> tree) {
		ladder.addLast(rankIndex, tree);
	}

	public boolean next() {

		ArrayList<TreeKeeper<LabelType>> temp = ladder.dequeue();

System.out.println("In next: ");
System.out.println("Rule = " + rule);
System.out.println("temp = " + temp);

		if (temp == null) {
			return false;
		}

		smallestTree = new TreeKeeper<LabelType>(rule.getSymbol(),
				rule.getWeight(), temp);

System.out.println("New SmallestTree=" + smallestTree);

		return true;
	}

	@Override
	public int compareTo(RuleKeeper<LabelType> ruleKeeper) {
		return smallestTree.compareTo(ruleKeeper.smallestTree);
	}

}
