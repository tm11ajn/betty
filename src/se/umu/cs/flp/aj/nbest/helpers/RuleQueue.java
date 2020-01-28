/*
 * Copyright 2018 Anna Jonsson for the research group Foundations of Language
 * Processing, Department of Computing Science, Umeå university
 *
 * This file is part of BestTrees.
 *
 * BestTrees is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BestTrees is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BestTrees.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.umu.cs.flp.aj.nbest.helpers;

import java.util.ArrayList;

import se.umu.cs.flp.aj.heap.BinaryHeap;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.Configuration;
import se.umu.cs.flp.aj.nbest.treedata.RuleKeeper;
import se.umu.cs.flp.aj.nbest.treedata.TreeKeeper2;
import se.umu.cs.flp.aj.nbest.util.LazyLimitedLadderQueue;
import se.umu.cs.flp.aj.nbest.util.ResultConnector;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.WTA;

public class RuleQueue {
	
	private ResultConnector resultConnector; // Måste innehålla rätt index också, antingen en post per index eller en lista per post
	private BinaryHeap<RuleKeeper, Weight> queue;
//	private ArrayList<BinaryHeap<RuleKeeper, Weight>.Node> queueElements;
	private BinaryHeap<RuleKeeper, Weight>.Node[] queueElems;
	int limit;

	@SuppressWarnings("unchecked")
	public RuleQueue(int limit, WTA wta) {
		this.queue = new BinaryHeap<>();
//		this.queueElements = new ArrayList<>(wta.getRuleCount());
		this.queueElems = new BinaryHeap.Node[wta.getRuleCount()];
		this.limit = limit;
		this.resultConnector = new ResultConnector(wta.getStateCount(), 
				limit, wta.getRules());

		for (Rule r : wta.getSourceRules()) {
			initialiseRuleElement(r);
		}
	}
	
	private void initialiseRuleElement(Rule r) {
		RuleKeeper keeper = new RuleKeeper(r, limit);
		LazyLimitedLadderQueue<TreeKeeper2> ladder = keeper.getLadderQueue();
		Configuration<TreeKeeper2> startConfig = ladder.getStartConfig();
		resultConnector.makeConnections(startConfig);
		BinaryHeap<RuleKeeper, Weight>.Node elem = queue.createNode(keeper);
//		queueElements.set(r.getID(), elem);
		queueElems[r.getID()] = elem;

		if (ladder.hasNext()) {
			Configuration<TreeKeeper2> config = ladder.peek();
			keeper.setBestTree(r.apply(config));
			queue.insert(elem, keeper.getBestTree().getDeltaWeight());
		}
	}

	public void expandWith(TreeKeeper2 newTree) {
		State resState = newTree.getResultingState();
		boolean unseenState = 
				(resultConnector.getResult(resState.getID(),  0) == null);
		
		/* Create rulekeepers for the rules that we have not yet seen
		 * and add them to the queue as well. */
		if (unseenState) {
			for (Rule r : resState.getOutgoing()) {
//				if (queueElements.get(r.getID()) == null) {
				if (queueElems[r.getID()] == null) {
					initialiseRuleElement(r);
				} 
			}
		}
				
		/* Have the result connector propagate the result to the connected 
		 * configs, and get info on which rulekeepers should be updated 
		 * based on this propagation. */
		ArrayList<Integer> toUpdate = 
				resultConnector.addResult(resState.getID(), newTree);
		
		/* Then update the corresponding rulekeepers. */
		for (Integer index : toUpdate) {
//			BinaryHeap<RuleKeeper, Weight>.Node elem = queueElements.get(index);
			BinaryHeap<RuleKeeper, Weight>.Node elem = queueElems[index];
			RuleKeeper rk = elem.getObject();
			Rule rule = rk.getRule();
			TreeKeeper2 currentBest = rk.getBestTree();
			LazyLimitedLadderQueue<TreeKeeper2> ladder = rk.getLadderQueue();
			Configuration<TreeKeeper2> config = ladder.peek();
			
			if (elem.isEnqueued()) {
				Weight currentWeight = currentBest.getRunWeight();
				Weight newWeight = config.getWeight().mult(rule.getWeight());
				
				if (currentWeight.compareTo(newWeight) > 0) {
					TreeKeeper2 newBest = rule.apply(config);
					rk.setBestTree(newBest);
					queue.decreaseWeight(elem, newBest.getDeltaWeight());
				}
			} else {
				TreeKeeper2 newBest = rule.apply(config);
				rk.setBestTree(newBest);
				queue.insert(elem, newBest.getDeltaWeight());
			}
		}
	}

	public TreeKeeper2 nextTree() {
		
		/* Dequeue the next tree. */
		BinaryHeap<RuleKeeper, Weight>.Node elem = queue.dequeue();
		RuleKeeper ruleKeeper = elem.getObject();
		TreeKeeper2 nextTree = ruleKeeper.getBestTree();
		ruleKeeper.setBestTree(null);
		LazyLimitedLadderQueue<TreeKeeper2> ladder = ruleKeeper.getLadderQueue();
		Configuration<TreeKeeper2> config = ladder.dequeue();
		
		/* Add the new configs to the process. */
		ArrayList<Configuration<TreeKeeper2>> nextConfigs = 
				ladder.getNextConfigs(config);
		for (Configuration<TreeKeeper2> next : nextConfigs) {
			resultConnector.makeConnections(next);
		}
		
		/* Re-queue the rulekeeper if it has another element in its ladder. */
		if (ladder.hasNext()) {
			Configuration<TreeKeeper2> c = ruleKeeper.getLadderQueue().peek();
			TreeKeeper2 newBest = ruleKeeper.getRule().apply(c);
			ruleKeeper.setBestTree(newBest);
			queue.insert(elem, newBest.getDeltaWeight());
		}
		
		return nextTree;
	}

	public boolean isEmpty() {
		return queue.empty();
	}

	public int size() {
		return queue.size();
	}

}