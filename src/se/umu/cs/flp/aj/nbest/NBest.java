/*
 * Copyright 2015 Anna Jonsson for the research group Foundations of Language
 * Processing, Department of Computing Science, Ume� university
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

package se.umu.cs.flp.aj.nbest;

import java.util.HashMap;
import java.util.List;

import se.umu.cs.flp.aj.knuth.SmallestCompletionsFinder;
import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.WTA;
//import se.umu.cs.flp.aj.wta_handlers.WTABuilder;
import se.umu.cs.flp.aj.nbest.wta.handlers.WTAParser;

public class NBest {

	private static final String VERSION_FLAG = "-v";
	private static final String RULE_QUEUE_ARG = "2";
	private static final String TREE_QUEUE_ARG = "1";
	private static final String BASIC_ARG = "basic";
	private static final String ALL_ARG = "all";
	private static final String TIMER_FLAG = "-timer";

	public static void main(String[] args) {

		checkArgs(args);

		String fileName = getFileName(args);
		int N = getN(args);

		WTAParser wtaParser = new WTAParser();
		WTA wta = wtaParser.parse(fileName);

		long startTime;
		long endTime;
		long duration;

		boolean all = runAll(args);
		boolean timer = useTimer(args);

		System.out.println("Pre-computing smallest completions...");
		startTime = System.nanoTime();
		HashMap<State, Semiring> smallestCompletions =
				getSmallestCompletions(wta);
		endTime = System.nanoTime();
		duration = (endTime - startTime)/1000000;
		System.out.println("Smallest completions done (took "
				+ duration + " milliseconds).");

		if (runVersion2(args) || all) {
			System.out.println("Running BestTrees version 2...");
			BestTrees2.setSmallestCompletions(smallestCompletions);

			startTime = System.nanoTime();
//			List<String> result = BestTrees2.run(wta, N);
			endTime = System.nanoTime();

//			printResult(result);

			if (timer) {
				duration = (endTime - startTime)/1000000;
				System.out.println("BestTrees version 2 took " + duration +
						" milliseconds");
			}
		}

		if (runVersion1(args) || all) {
			System.out.println("Running BestTrees version 1...");
			BestTrees.setSmallestCompletions(smallestCompletions);

			startTime = System.nanoTime();
			List<String> result = BestTrees.run(wta, N);
			endTime = System.nanoTime();

			printResult(result);

			if (timer) {
				duration = (endTime - startTime)/1000000;
				System.out.println("BestTrees version 1 took " + duration +
						" milliseconds");
			}
		}

		if (runBasic(args) || all) {
			System.out.println("Running BestTreesBasic...");
			BestTreesBasic.setSmallestCompletions(smallestCompletions);

			startTime = System.nanoTime();
			List<String> resultBasic = BestTreesBasic.run(wta, N);
			endTime = System.nanoTime();

			printResult(resultBasic);

			if (timer) {
				duration = (endTime - startTime)/1000000;
				System.out.println("BestTreesBasic took " + duration +
					" milliseconds");
			}
		}

	}

	public static HashMap<State, Semiring> getSmallestCompletions(WTA wta) {
//		WTABuilder b = new WTABuilder();
//		return b.findSmallestCompletionWeights(wta);
		return SmallestCompletionsFinder.findSmallestCompletionWeights(wta);
	}

	private static void checkArgs(String[] args) {

		int nOfArgs = args.length;

		if (nOfArgs < 2 || nOfArgs > 5) {
			printUsageError();
		} else if (nOfArgs == 3) {
			String arg2 = args[2];

			if (!arg2.equals(TIMER_FLAG)) {
				printUsageError();
			}

		} else if (nOfArgs == 4) {
			String arg2 = args[2];
			String arg3 = args[3];

			if (!arg2.equals(VERSION_FLAG)) {
				printUsageError();
			} else if (!arg3.equals(ALL_ARG) &&
					!arg3.equals(BASIC_ARG) &&
					!arg3.equals(TREE_QUEUE_ARG) &&
					!arg3.equals(RULE_QUEUE_ARG)) {
				printUsageError();
			}

		} else if (nOfArgs == 5) {
			String arg2 = args[2];
			String arg3 = args[3];
			String arg4 = args[4];

			if (!arg2.equals(TIMER_FLAG) &&
					!arg2.equals(VERSION_FLAG)) {
				printUsageError();
			} else if (!arg4.equals(TIMER_FLAG) &&
					!arg3.equals(VERSION_FLAG)) {
				printUsageError();
			} else if (arg2.equals(VERSION_FLAG) &&
					(!arg3.equals(ALL_ARG) &&
							!arg3.equals(BASIC_ARG) &&
							!arg3.equals(TREE_QUEUE_ARG) &&
							!arg3.equals(RULE_QUEUE_ARG))) {
				printUsageError();
			} else if (arg3.equals(VERSION_FLAG) &&
					(!arg4.equals(ALL_ARG) &&
							!arg4.equals(BASIC_ARG) &&
							!arg4.equals(TREE_QUEUE_ARG) &&
							!arg4.equals(RULE_QUEUE_ARG))) {
				printUsageError();
			}
		}
	}

	private static String getFileName(String[] args) {
		return args[0];
	}

	private static int getN(String[] args) {

		int N = 0;

		try {
			N = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			printUsageError();
		}

		return N;
	}

	private static boolean runVersion2(String[] args) {

		if (args.length < 4) {
			return true;
		}

		if ((args.length == 4 && args[3].equals(RULE_QUEUE_ARG)) ||
				(args.length == 5 && (args[3].equals(RULE_QUEUE_ARG) ||
						args[4].equals(RULE_QUEUE_ARG)))) {
			return true;
		}

		return false;
	}


	private static boolean runVersion1(String[] args) {

		if ((args.length == 4 && args[3].equals(TREE_QUEUE_ARG)) ||
				(args.length == 5 && (args[3].equals(TREE_QUEUE_ARG) ||
						args[4].equals(TREE_QUEUE_ARG)))) {
			return true;
		}

		return false;
	}

	private static boolean runBasic(String[] args) {

		if ((args.length == 4 && args[3].equals(BASIC_ARG)) ||
				(args.length == 5 && (args[3].equals(BASIC_ARG) ||
						args[4].equals(BASIC_ARG)))) {
			return true;
		}

		return false;
	}

	private static boolean runAll(String[] args) {

		if ((args.length == 4 && args[3].equals(ALL_ARG)) ||
				(args.length == 5 && (args[3].equals(ALL_ARG) ||
						args[4].equals(ALL_ARG)))) {
			return true;
		}

		return false;
	}

	private static boolean useTimer(String[] args) {

		if ((args.length == 3 && args[2].equals(TIMER_FLAG)) ||
				(args.length == 5 &&
				(args[2].equals(TIMER_FLAG) || args[4].equals(TIMER_FLAG)))) {
			return true;
		}

		return false;
	}

	private static void printUsageError() {
		System.err.println("\n"
				+ "Usage: BestTrees <RTG file> N "
				+ "["+ VERSION_FLAG + " VERSION] [" + TIMER_FLAG + "] \n\n"
				+ "    N is an nonnegative integer \n"
				+ "    -v allows the user to select which version to run\n"
				+ "    -timer measures the run-time for the algorithm(s)\n"
				+ "    VERSION can be: \n"
				+ "    " + RULE_QUEUE_ARG + " - "
				+ "runs BestTrees version 2 (pruned rule queue)\n"
				+ "    " + TREE_QUEUE_ARG + " - "
				+ "runs BestTrees version 1 (pruned tree queue)\n"
				+ "    " + BASIC_ARG + " - runs the BestTreesBasic algorithm\n"
				+ "    " + ALL_ARG + " - runs all versions\n");
		System.exit(-1);
	}

	private static void printResult(List<String> result) {

		for (String treeString : result) {
			System.out.println(treeString);
		}
	}

}
