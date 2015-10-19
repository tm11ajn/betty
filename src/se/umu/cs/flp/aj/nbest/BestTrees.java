package se.umu.cs.flp.aj.nbest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import se.umu.cs.flp.aj.eppstein_k_best.graph.Edge;
import se.umu.cs.flp.aj.eppstein_k_best.graph.Graph;
import se.umu.cs.flp.aj.eppstein_k_best.graph.Path;
import se.umu.cs.flp.aj.nbest.data.NestedMap;
import se.umu.cs.flp.aj.nbest.data.Node;
import se.umu.cs.flp.aj.nbest.data.Run;
import se.umu.cs.flp.aj.wta.Rule;
import se.umu.cs.flp.aj.wta.State;
import se.umu.cs.flp.aj.wta.Symbol;
import se.umu.cs.flp.aj.wta.WTA;
import se.umu.cs.flp.aj.wta.Weight;
import se.umu.cs.flp.aj.wta_handlers.WTABuilder;

import java.util.PriorityQueue;

public class BestTrees {

	private static NestedMap<Node<Symbol>, State, Weight> treeStateValTable =
			new NestedMap<>(); // C
	private static ArrayList<Node<Symbol>> exploredTrees =
			new ArrayList<Node<Symbol>>(); // T
	private static LinkedList<Node<Symbol>> treeQueue = new LinkedList<>(); // K

	private static HashMap<State, Weight> smallestCompletionWeights;
	private static HashMap<Node<Symbol>, ArrayList<State>> optimalStates =
			new HashMap<>();
	private static HashMap<State, Integer> optimalStatesUsage = new HashMap<>();

	public static List<String> run(WTA wta, int N) {

		/* For result. */
		List<String> nBest = new ArrayList<String>();

		init(wta);

		// T <- empty. K <- empty
		exploredTrees = new ArrayList<Node<Symbol>>();
		treeQueue = new LinkedList<Node<Symbol>>();

		// enqueue(K, Sigma_0) TODO
		enqueueRankZeroSymbols(wta, N);

		// i <- 0
		int counter = 0;

		// while i < N and K nonempty do
		while (counter < N && !treeQueue.isEmpty()) {

			// t <- dequeue(K)
			Node<Symbol> currentTree = treeQueue.poll();

			// T <- T u {t}
			exploredTrees.add(currentTree);

			// Get optimal state for current tree
			State optimalState = optimalStates.get(currentTree).get(0);

			// if M(t) = delta(t) then TODO
			// is this the same thing as smallestcompletion(optimalstate) = 0
			// and optimalstate in final states ?
			if (optimalState.isFinal()) {

				// output(t)
				nBest.add(currentTree.toString() + " " +
						treeStateValTable.get(currentTree, optimalState));

				// i <- i + 1
				counter++;
			}

			// prune(T, enqueue(K, expand(T, t)))
			enqueueWithExpansionAndPruning(wta, N, currentTree);
		}

		return nBest;
	}

	public static void init(WTA wta) {
		WTABuilder b = new WTABuilder();
		smallestCompletionWeights = b.findSmallestCompletionWeights(wta);
		treeStateValTable = new NestedMap<>();
	}

	private static void enqueueRankZeroSymbols(WTA wta, int N) {

		ArrayList<Symbol> symbols = wta.getSymbols();
		LinkedList<Node<Symbol>> tempQueue = new LinkedList<>();

		for (Symbol s : symbols) {

			if (s.getRank() == 0) {
				Node<Symbol> tree = new Node<Symbol>(s);

				ArrayList<Rule> rules = wta.getTransitionFunction().
						getRulesBySymbol(s);

				for (Rule r : rules) {
					State resState = r.getResultingState();
					Weight weight = r.getWeight();
					Weight oldWeight = treeStateValTable.get(tree, resState);

					if (oldWeight == null ||
							weight.compareTo(oldWeight) == -1) {
						treeStateValTable.put(tree, resState, weight);
					}
				}

				optimalStates.put(tree, getOptimalStates(tree));

				Weight treeDeltaWeight = getDeltaWeight(tree);

				int tempQueueSize = tempQueue.size();
				int insertIndex = 0;

				for (int i = 0; i < tempQueueSize; i++) {

					Node<Symbol> currentTree = tempQueue.get(i);
					Weight currentTreeDeltaWeight = getDeltaWeight(currentTree);

					int comparison = currentTreeDeltaWeight.compareTo(
							treeDeltaWeight);

					if (comparison == 1) {
						insertIndex = i;
						break;
					} else if (comparison == 0) {

						if (currentTree.compareTo(tree) == 1) {
							insertIndex = i;
							break;
						}
					}
				}

				tempQueue.add(insertIndex, tree);
			}
		}

		for (Node<Symbol> n : tempQueue) {
			insertIntoQueueUsingPruning(n, N);
		}

	}

	private static ArrayList<State> getOptimalStates(Node<Symbol> tree) {

		HashMap<State, Weight> stateValTable =
				treeStateValTable.getAll(tree);
		ArrayList<State> optStates = new ArrayList<>();
		Weight minWeight = new Weight(Weight.INF);

		for (Entry<State, Weight> e : stateValTable.entrySet()) {

			Weight currentWeight = e.getValue();
			int comparison = currentWeight.compareTo(minWeight);

			if (comparison == -1) {
				minWeight = currentWeight;
				optStates = new ArrayList<>();
				optStates.add(e.getKey());
			} else if (comparison == 0) {
				optStates.add(e.getKey());
			}
		}

		return optStates;
	}

	public static Weight getDeltaWeight(Node<Symbol> tree) {

		Weight delta = new Weight(0);

		State optimalState = optimalStates.get(tree).get(0);

		Weight minWeight = treeStateValTable.get(tree, optimalState);
		Weight smallestCompletionWeight = smallestCompletionWeights.
				get(optimalState);

		delta = minWeight.add(smallestCompletionWeight);

		return delta;
	}

	private static boolean insertIntoQueueUsingPruning(Node<Symbol> tree, int N) {

		ArrayList<State> optStates = optimalStates.get(tree);
		Weight deltaWeight = getDeltaWeight(tree);

		boolean canInsert = false;

		for (State q : optStates) {

			if (optimalStatesUsage.get(q) == null) {
				optimalStatesUsage.put(q, 0);
			}

			if (optimalStatesUsage.get(q) < N) {
				canInsert = true;
			}
		}

		int index = treeQueue.size() - 1;
		boolean isSmaller = true;

		while (!canInsert && isSmaller && index > -1) {
			Node<Symbol> last = treeQueue.get(index);
			Weight lastDeltaWeight = getDeltaWeight(last);

			if (lastDeltaWeight.compareTo(deltaWeight) == -1) {
				isSmaller = false;
			} else if (lastDeltaWeight.compareTo(deltaWeight) == 0) {

				if (last.compareTo(tree) != 1) {
					isSmaller = false;
				}
			}

			if (isSmaller) {
				ArrayList<State> optStatesLast = optimalStates.get(last);

				for (State optState : optStates) {

					if (optStatesLast.contains(optState)) {
						canInsert = true;
						treeQueue.remove(index);
						break;
					}
				}
			}

			index--;
		}

		if (canInsert) {

			for (State q : optStates) {
				optimalStatesUsage.put(q, optimalStatesUsage.get(q) + 1);
			}

			int queueSize = treeQueue.size();
			int insertIndex = 0;

			for (int i = 0; i < queueSize; i++) {
				Node<Symbol> n = treeQueue.get(i);

				Weight currentDeltaWeight = getDeltaWeight(n);

				if (currentDeltaWeight.compareTo(deltaWeight) == 1) {
					insertIndex = i;
					break; // TODO boolean and while instead
				} else if (currentDeltaWeight.compareTo(deltaWeight) == 0) {

					if (n.compareTo(tree) == 1) {
						insertIndex = i;
						break; // TODO boolean and while instead
					}

					insertIndex = i + 1;

				} else {
					insertIndex = i + 1;
				}
			}

			treeQueue.add(insertIndex, tree);
		}

		return canInsert;
	}

	public static void enqueueWithExpansionAndPruning(WTA wta, int N,
			Node<Symbol> tree) {

		HashMap<State, ArrayList<LinkedList<Node<Symbol>>>> allRuns = new HashMap<>();
		HashMap<State, LinkedList<Node<Symbol>>> nRuns = new HashMap<>();

		for (State q : wta.getStates()) {
			allRuns.put(q, useEppstein(wta, N, tree, q));
		}

		// Merge into one list for each Q
		for (Entry<State, ArrayList<LinkedList<Node<Symbol>>>> e :
			allRuns.entrySet()) {

			LinkedList<Node<Symbol>> mergedTreeList = new LinkedList<>();
			State q = e.getKey();

			for (LinkedList<Node<Symbol>> treeList : e.getValue()) {
				mergedTreeList = mergeTreeLists(q, N, treeList, mergedTreeList);
			}

			nRuns.put(q, mergedTreeList);
		}

		// merge with K, add all values to treeStateVal and get optstates for each tree added?

		// Merge all lists, remember to get the delta value!

		LinkedList<Node<Symbol>> mergedList = new LinkedList<>();
		int nOfStatesInWTA = wta.getStates().size();

		for (LinkedList<Node<Symbol>> currentList : nRuns.values()) {
			mergedList = mergeTreeListsByDeltaWeights(N*nOfStatesInWTA,
					currentList, mergedList);
		}

		// Merge sorted list with K
		for (Node<Symbol> n : mergedList) {
			insertIntoQueueUsingPruning(n, N);
		}

	}

	public static ArrayList<LinkedList<Node<Symbol>>> useEppstein(WTA wta,
			int k, Node<Symbol> tree, State q) {

		ArrayList<LinkedList<Node<Symbol>>> kBestTreesForEachQRule =
				new ArrayList<>();

		Graph<Node<Symbol>> graph = new Graph<>();

		ArrayList<Rule> rules = wta.getTransitionFunction().
				getRulesByResultingState(q);

		for (Rule r : rules) {
			LinkedList<Node<Symbol>> treeList = new LinkedList<>();

			ArrayList<State> states = r.getStates();
			int nOfStates = states.size();

			String vertices = "";

			for (int i = 0; i < nOfStates + 1; i++) {
				vertices += "u" + i + "," + "v" + i;

				if (i != nOfStates) {
					vertices += ",";
				}
			}

			graph.createVertices(vertices);

			Comparator<Run> smallestRunComparator = new Comparator<Run>() {

				@Override
				public int compare(Run o1, Run o2) {
					return o1.getWeight().compareTo(o2.getWeight());
				}
			};

			NestedMap<String, String, PriorityQueue<Run>> edgeMap =
					new NestedMap<>();

			for (int i = 1; i < nOfStates + 1; i++) {
				State currentState = states.get(i-1);

				for (Node<Symbol> n : exploredTrees) { // TODO include only N edges for each pair of nodes

					Weight w = treeStateValTable.get(n, currentState);
//					double weight = Double.MAX_VALUE;
//
//					if (w != null) {
//						weight = Double.parseDouble(w.toString());
//					} else {
//						continue;
//					}

					if (w == null) {
						continue;
					}

					boolean isT = n.equals(tree);

					PriorityQueue<Run> pu;
					PriorityQueue<Run> pv;

					if (!isT) {

						pu = edgeMap.get("u" + (i - 1), "u" + i);
						pv = edgeMap.get("v" + (i - 1), "v" + i);

						if (pu == null) {
							pu = new PriorityQueue<>(nOfStates, // TODO choose a better initial capacity?
									smallestRunComparator);
							edgeMap.put("u" + (i - 1), "u" + i, pu);
						}

						if (pv == null) {
							pv = new PriorityQueue<>(nOfStates, // TODO choose a better initial capacity?
									smallestRunComparator);
							edgeMap.put("v" + (i - 1), "v" + i, pv);
						}

						pu.add(new Run(n, w));
						pv.add(new Run(n, w));

//						graph.createEdge("u" + (i - 1), "u" + i, n, weight);
//						graph.createEdge("v" + (i - 1), "v" + i, n, weight);
					}

					if (isT) {

						pu = edgeMap.get("u" + (i - 1), "v" + i);
						pv = edgeMap.get("v" + (i - 1), "v" + i);

						if (pu == null) {
							pu = new PriorityQueue<>(nOfStates, // TODO choose a better initial capacity?
									smallestRunComparator);
							edgeMap.put("u" + (i - 1), "v" + i, pu);
						}

						if (pv == null) {
							pv = new PriorityQueue<>(nOfStates, // TODO choose a better initial capacity?
									smallestRunComparator);
							edgeMap.put("v" + (i - 1), "v" + i, pv);
						}

						pu.add(new Run(n, w));
						pv.add(new Run(n, w));

//						graph.createEdge("u" + (i - 1), "v" + i, n, weight);
//						graph.createEdge("v" + (i - 1), "v" + i, n, weight);
					}
				}
			}

			// Adds only the N smallest edges to Eppstein graph.
			for (String vertex1 : edgeMap.keySet()) {

				for (String vertex2 : edgeMap.get(vertex1).keySet()) {

					PriorityQueue<Run> p = edgeMap.get(vertex1, vertex2);

					int counter = 0;

					while (!p.isEmpty() && counter < k) {
						Run run = p.poll();
						graph.createEdge(vertex1, vertex2, run.getTree(),
								Double.parseDouble(run.getWeight().toString()));
						counter++;
					}
				}
			}

			int counter = 0;

			Path<Node<Symbol>> path =
					graph.findShortestPath("u0", "v" + nOfStates);

			while (path.isValid() && counter < k) {
				Node<Symbol> pathTree = extractTreeFromPath(path, r);
				Weight pathWeight = new Weight(path.getWeight());

				if (path.getWeight() == Double.MAX_VALUE) {
					pathWeight = new Weight(Weight.INF);
				}

				// TODO Might have to add the weight of the rule here
				pathWeight = pathWeight.add(r.getWeight());

				Weight oldWeight = treeStateValTable.get(pathTree, q);

				if (oldWeight == null
						|| oldWeight.compareTo(pathWeight) == 1) {
					treeStateValTable.put(pathTree, q, pathWeight);
				}

				treeList.add(pathTree);
				counter++;

				path = graph.findNextShortestPath();
			}

			kBestTreesForEachQRule.add(treeList);
		}

		return kBestTreesForEachQRule;
	}

	private static Node<Symbol> extractTreeFromPath(Path<Node<Symbol>> path,
			Rule r) {

		Node<Symbol> root = new Node<>(r.getSymbol());

		for (Edge<Node<Symbol>> e : path) {
			root.addChild(e.getLabel());
		}

		return root;
	}

	// Does not add same tree twice
	private static LinkedList<Node<Symbol>> mergeTreeLists(State q,
			int listSizeLimit, LinkedList<Node<Symbol>> list1,
			LinkedList<Node<Symbol>> list2) {

		LinkedList<Node<Symbol>> result = new LinkedList<>();

		int added = 0;

		while (added < listSizeLimit && !(list1.isEmpty() && list2.isEmpty())) {

			Node<Symbol> tree1 = list1.peek();
			Node<Symbol> tree2 = list2.peek();

			if (tree1 != null && result.contains(tree1)) {
				list1.poll();
			} else if (tree2 != null && result.contains(tree2)) {
				list2.poll();
			} else if (tree1 != null && tree2 != null) {

				Weight weight1 = treeStateValTable.get(tree1, q);
				Weight weight2 = treeStateValTable.get(tree2, q);

				if (weight1.compareTo(weight2) == -1) {
					result.addLast(list1.poll());
				} else if (weight1.compareTo(weight2) == 1) {
					result.addLast(list2.poll());
				} else {

					if (tree1.compareTo(tree2) == 0) {
						result.addLast(list1.poll());
						list2.poll();
					} else if (tree1.compareTo(tree2) == -1) {
						result.addLast(list1.poll());
					} else {
						result.addLast(list2.poll());
					}
				}

			} else if (tree1 != null) {
				result.addLast(list1.poll());
			} else {
				result.addLast(list2.poll());
			}

			added++;
		}

		return result;
	}

	// Does not add same tree twice
	private static LinkedList<Node<Symbol>> mergeTreeListsByDeltaWeights(
			int listSizeLimit, LinkedList<Node<Symbol>> list1,
			LinkedList<Node<Symbol>> list2) {

		LinkedList<Node<Symbol>> result = new LinkedList<>();

		int added = 0;

		while (added < listSizeLimit && !(list1.isEmpty() && list2.isEmpty())) {

			Node<Symbol> tree1 = list1.peek();
			Node<Symbol> tree2 = list2.peek();

			ArrayList<State> optStates1 = optimalStates.get(tree1);
			ArrayList<State> optStates2 = optimalStates.get(tree2);


			if (optStates1 == null) {
				optStates1 = getOptimalStates(tree1);
				optimalStates.put(tree1, optStates1);
			}

			if (optStates2 == null) {
				optStates2 = getOptimalStates(tree1);
				optimalStates.put(tree2, optStates2);
			}

			State opt1 = optStates1.get(0);
			State opt2 = optStates2.get(0);

			Weight compl1 = smallestCompletionWeights.get(optStates1.get(0));
			Weight compl2 = smallestCompletionWeights.get(optStates2.get(0));

			if (tree1 != null && result.contains(tree1)) {
				list1.poll();
			} else if (tree2 != null && result.contains(tree2)) {
				list2.poll();
			} else if (tree1 != null && tree2 != null) {

				Weight weight1 = treeStateValTable.get(tree1, opt1).add(compl1);
				Weight weight2 = treeStateValTable.get(tree2, opt2).add(compl2);

				if (weight1.compareTo(weight2) == -1) {
					result.addLast(list1.poll());
				} else if (weight1.compareTo(weight2) == 1) {
					result.addLast(list2.poll());
				} else {

					if (tree1.equals(tree2)) {
						result.addLast(list1.poll());
						list2.poll();
					} else if (tree1.compareTo(tree2) == -1) {
						result.addLast(list1.poll());
					} else {
						result.addLast(list2.poll());
					}
				}

			} else if (tree1 != null) {
				result.addLast(list1.poll());
			} else {
				result.addLast(list2.poll());
			}

			added++;
		}

		return result;
	}

}
