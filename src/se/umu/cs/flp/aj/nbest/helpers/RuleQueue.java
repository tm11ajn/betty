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
	private ArrayList<BinaryHeap<RuleKeeper, Weight>.Node> queueElements;
	int limit;

	public RuleQueue(int limit, WTA wta) {
System.out.println("RuleQueue initialisation");
		this.queue = new BinaryHeap<>();
		this.queueElements = new ArrayList<>(wta.getRuleCount());
		this.limit = limit;
		this.resultConnector = new ResultConnector(wta.getStateCount(), 
				limit, wta.getRules());
		
		for (int i = 0; i < wta.getRuleCount(); i++) {
			queueElements.add(i, null);
		}

		for (Rule r : wta.getSourceRules()) {
			activateRule(r);

//			addRule(r);

//			RuleKeeper keeper = new RuleKeeper(r, r.getNumberOfStates());
//			LazyLimitedLadderQueue<TreeKeeper2> ladder = keeper.getLadderQueue();
//			Configuration<TreeKeeper2> startConfig = ladder.getStartConfig();
//			Weight weight = r.getWeight();
//			startConfig.setValues(new ArrayList<TreeKeeper2>());
//			startConfig.setWeight(weight);
//			ladder.insert(startConfig);
//			BinaryHeap<RuleKeeper, Weight>.Node n = queue.add(keeper, weight);
//			queueElements.add(r.getID(), n);
//			keeper.setSmallestTree(r.apply(ladder.peek()));
		}
		
System.out.println("Queue after init: ");
queue.printHeap();
	}
	
	private void activateRule(Rule r) {
System.out.println("RuleQueue: activateRule");
		RuleKeeper keeper = new RuleKeeper(r, limit);
		LazyLimitedLadderQueue<TreeKeeper2> ladder = keeper.getLadderQueue();
		Configuration<TreeKeeper2> startConfig = ladder.getStartConfig();
System.out.println("Startconfig=" + startConfig);
		
		if (r.getNumberOfStates() == 0) {
System.out.println("Adding rule with 0 states: " + r);
			Weight weight = r.getWeight();
//			startConfig.setValues(new ArrayList<TreeKeeper2>());
			startConfig.setWeight(weight.one());
		}

		resultConnector.makeConnections(startConfig);
		BinaryHeap<RuleKeeper, Weight>.Node elem = queue.createNode(keeper);
		queueElements.add(r.getID(), elem);
		
System.out.println("Should have next? " + (ladder.peek() != null));

		if (ladder.hasNext()) {
System.out.println("Has next");
System.out.println("Adding rule to rule queue: " + r);
			Configuration<TreeKeeper2> config = ladder.peek();
			keeper.setSmallestTree(r.apply(config));
			queue.insert(elem, keeper.getSmallestTree().getDeltaWeight());
		}
	}

//	private void addRule(Rule rule) {
//		int[] elementIndices = new int[rule.getNumberOfStates()];
//
////		for (State s : states) {
//////			controlInitState(s);
//////			elementIndices.add(stateElementIndex.get(s));
////		}
//
//		int counter = 0;
//		for (State s : rule.getStates()) {
//			elementIndices[counter] = s.getID();
//			counter++;
//		}
//
//		RuleKeeper keeper = new RuleKeeper(rule, limit);
////		ruleKeepers.put(rule, keeper);
//
//		if (!keeper.isPaused()) {
//			BinaryHeap<RuleKeeper, Weight>.Node n =
//					queue.add(keeper, keeper.getSmallestTree().getDeltaWeight());
//			queueElements.set(keeper.getRule().getID(), n);
//			keeper.setQueued(true);
//		}
//
//		size++;
//	}

//	private void controlInitState(State s) {
//		if (!stateElementIndex.containsKey(s)) {
//			elements.add(new LinkedList<>());
//			stateElementIndex.put(s, elements.size() - 1);
//		}
//	}

	public void expandWith(TreeKeeper2 newTree) {
System.out.println("RuleQueue: expandWith");
System.out.println("newTree=" + newTree);
		State resState = newTree.getResultingState();
		boolean unseenState = (resultConnector.getResult(resState.getID(),  0) == null);
		
		/* Create rulekeepers for the rules that we have not yet seen
		 * and add them to the queue as well. */
		if (unseenState) {
System.out.println("Based on state " + resState + " ... ");
			for (Rule r : resState.getOutgoing()) {
System.out.println("Activate rule: " + r);
				if (queueElements.get(r.getID()) == null) {
					activateRule(r);
//					RuleKeeper keeper = new RuleKeeper(r, r.getNumberOfStates());
//					LazyLimitedLadderQueue<TreeKeeper2> ladder = keeper.getLadderQueue();
//					Configuration<TreeKeeper2> startConfig = ladder.getStartConfig();
//					resultConnector.makeConnections(startConfig);
//					BinaryHeap<RuleKeeper, Weight>.Node n = queue.createNode(keeper);
//					queueElements.add(r.getID(), n);
//					keeper.setSmallestTree(r.apply(ladder.peek()));
//
//					if (ladder.hasNext()) {
//						Configuration<TreeKeeper2> config = ladder.peek();
//						keeper.setSmallestTree(r.apply(config));
//						queue.insert(n,
//								keeper.getSmallestTree().getDeltaWeight());
//					}
				}
			}
		}
		
		/* Have the result connector propagate the result to the correct configs,
		 * and get info on which rulekeepers should be updated based on this 
		 * propagation. */
		ArrayList<Integer> toUpdate = 
				resultConnector.addResult(resState.getID(), newTree);
		
		/* Then update the corresponding rulekeepers. */
		for (Integer index : toUpdate) {
			BinaryHeap<RuleKeeper, Weight>.Node elem = queueElements.get(index);
			RuleKeeper rk = elem.getObject();
			LazyLimitedLadderQueue<TreeKeeper2> ladder = rk.getLadderQueue();
			TreeKeeper2 newSmallest = rk.getRule().apply(ladder.peek());
			rk.setSmallestTree(newSmallest);
			
			if (elem.isEnqueued()) {
				queue.decreaseWeight(elem, newSmallest.getDeltaWeight());
			} else {
				queue.insert(elem, newSmallest.getDeltaWeight());
			}
System.out.println("UPDATE TRIGGERED for index=" + index);
System.out.println("Resulted in tree: " + newSmallest);
		}

//		int size = nOfElements[state.getID()];
//		elements[state.getID()][size] = newTree;
//		nOfElements[state.getID()] = size + 1;

//		if (stateUsage.get(state) < limit) {
//			for (Rule rule : state.getOutgoing()) {
////				if (!ruleKeepers.containsKey(rule)) {
////					addRule(rule);
////				}
//
////				RuleKeeper currentKeeper = ruleKeepers.get(rule);
//				ArrayList<Integer> stateIndices = rule.getIndexOfState(state);
//
//				for (Integer index : stateIndices) {
//					currentKeeper.updateForStateIndex(index);
//
//					if (currentKeeper.isQueued() &&
//							currentKeeper.needsUpdate()) {
//						queue.decreaseWeight(queueElements.get(currentKeeper.getRule().getID()),
//								currentKeeper.getSmallestTree().
//								getDeltaWeight());
//						currentKeeper.hasUpdated();
//					}
//				}
//
//				if (!currentKeeper.isQueued() && !currentKeeper.isPaused()) {
//					currentKeeper.next();
//					BinaryHeap<RuleKeeper, Weight>.Node n = queue.add(currentKeeper,
//							currentKeeper.getSmallestTree().getDeltaWeight());
//					queueElements.set(currentKeeper.getRule().getID(), n);
//					currentKeeper.setQueued(true);
//				}
//			}
//
//			stateUsage.put(state, stateUsage.get(state) + 1);
//		}

	}

	public TreeKeeper2 nextTree() {
System.out.println("RuleQueue: nextTree");
//		RuleKeeper ruleKeeper = queue.dequeue().getObject();
//		ruleKeeper.hasBeenDequeued();
//		TreeKeeper2 nextTree = ruleKeeper.getSmallestTree();
//		ruleKeeper.setQueued(false);
//		ruleKeeper.next();
//
//		if (!ruleKeeper.isPaused()) {
//			queue.add(ruleKeeper,
//					ruleKeeper.getSmallestTree().getDeltaWeight());
//			ruleKeeper.setQueued(true);
//		}
//
//		return nextTree;
		
		// Dequeue next tree.
		BinaryHeap<RuleKeeper, Weight>.Node elem = queue.dequeue();
System.out.println("Elem dequeued: ");
System.out.println(elem.getObject());
System.out.println(elem.getWeight());
		RuleKeeper ruleKeeper = elem.getObject();
		TreeKeeper2 nextTree = ruleKeeper.getSmallestTree();
System.out.println("Next tree: " + nextTree);
		LazyLimitedLadderQueue<TreeKeeper2> ladder = ruleKeeper.getLadderQueue();
		Configuration<TreeKeeper2> config = ladder.dequeue();
		ArrayList<Configuration<TreeKeeper2>> nextConfigs = 
				ladder.getNextConfigs(config);
		
		// Add the new configs to the process.
		if (!ladder.hasReachedLimit()) {
		for (Configuration<TreeKeeper2> next : nextConfigs) {
System.out.println("Next config: " + next);
			resultConnector.makeConnections(next);
		}
		}
		
System.out.println("The rulekeeper should be re-enqueued? " + (ladder.peek() != null));
		// Re-queue the rulekeeper if it has another element in its ladder.
		if (ladder.hasNext()) {
System.out.println("Re-queueing since the ladder has another element");
			Configuration<TreeKeeper2> c = ruleKeeper.getLadderQueue().peek();
			TreeKeeper2 newSmallest = ruleKeeper.getRule().apply(c);
			ruleKeeper.setSmallestTree(newSmallest);
			queue.insert(elem, newSmallest.getDeltaWeight());
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