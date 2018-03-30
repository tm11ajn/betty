package se.umu.cs.flp.aj.nbest.treedata;

import se.umu.cs.flp.aj.nbest.helpers.TreeConfigurationComparator;
import se.umu.cs.flp.aj.nbest.util.LadderQueue;
import se.umu.cs.flp.aj.nbest.wta.Rule;

public class RuleKeeper<LabelType extends Comparable<LabelType>> {

	private Rule<LabelType> rule;
	private LadderQueue<TreeKeeper<LabelType>> ladder;
	private TreeKeeper<LabelType> smallestTree;

	public RuleKeeper(Rule<LabelType> rule) {
		this.rule = rule;
		this.ladder = new LadderQueue<>(rule.getRank(),
				new TreeConfigurationComparator<LabelType>());
	}

	public TreeKeeper<?> getSmallestTree() {
		return smallestTree;
	}

	public void next() {
		smallestTree = new TreeKeeper<LabelType>(rule.getSymbol(),
				rule.getWeight(), ladder.dequeue());
	}

}
