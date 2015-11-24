/*
 * Copyright 2015 Anna Jonsson for the research group Foundations of Language 
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

package se.umu.cs.flp.aj.nbest;

import java.util.HashMap;
import java.util.List;

import se.umu.cs.flp.aj.wta.State;
import se.umu.cs.flp.aj.wta.WTA;
import se.umu.cs.flp.aj.wta.Weight;
import se.umu.cs.flp.aj.wta_handlers.WTABuilder;
import se.umu.cs.flp.aj.wta_handlers.WTAParser;

public class NBest {
	
	private static final String NO_PRUNING_FLAG = "--no-pruning";
	private static final String BOTH_FLAG = "-both";
	private static final String TIMER_FLAG = "-timer";
	
	public static void main(String[] args) {
		
		checkArgs(args);
		
		String fileName = getFileName(args);
		int N = getN(args);
		
		WTAParser wtaParser = new WTAParser();
		WTA wta = wtaParser.parse(fileName);
		
		System.out.println("Pre-computing smallest completions...");
		HashMap<State, Weight> smallestCompletions = 
				getSmallestCompletions(wta);
		System.out.println("Smallest completions done.");

		long startTime;
		long endTime;
		long duration;
		
		if (usePruning(args) || runBoth(args)) {
			System.out.println("Running BestTrees...");
			BestTrees.setSmallestCompletions(smallestCompletions);
			
			startTime = System.nanoTime();
			List<String> result = BestTrees.run(wta, N);
			endTime = System.nanoTime();
			
			printResult(result);
			
			if (useTimer(args)) {
				duration = (endTime - startTime)/1000000;
				System.out.println("BestTrees took " + duration + 
						" milliseconds");
			}
		}
		
		if (!usePruning(args) || runBoth(args)) {
			System.out.println("Running BestTreesBasic...");
			BestTreesBasic.setSmallestCompletions(smallestCompletions);
			
			startTime = System.nanoTime();
			List<String> resultBasic = BestTreesBasic.run(wta, N);
			endTime = System.nanoTime();

			printResult(resultBasic);
			
			if (useTimer(args)) {
				duration = (endTime - startTime)/1000000;
				System.out.println("BestTreesBasic took " + duration + 
					" milliseconds");
			}
		}
		
	}
	
	public static HashMap<State, Weight> getSmallestCompletions(WTA wta) {
		WTABuilder b = new WTABuilder();
		return b.findSmallestCompletionWeights(wta);
	}
	
	private static void checkArgs(String[] args) {
		
		int nOfArgs = args.length;
		
		if (nOfArgs < 2 || nOfArgs > 4) {
			printUsageError();
		} else if (nOfArgs == 3) {
			String arg2 = args[2];
			
			if (!arg2.equals(NO_PRUNING_FLAG) && 
					!arg2.equals(BOTH_FLAG) &&
					!arg2.equals(TIMER_FLAG)) {
				printUsageError();
			}
			
		} else if (nOfArgs == 4) {
			String arg2 = args[2];
			String arg3 = args[3];
			
			if (!arg2.equals(NO_PRUNING_FLAG) && 
					!arg2.equals(BOTH_FLAG) &&
					!arg2.equals(TIMER_FLAG)) {
				printUsageError();
			} else if (!arg3.equals(NO_PRUNING_FLAG) && 
					!arg3.equals(BOTH_FLAG) &&
					!arg3.equals(TIMER_FLAG)) {
				printUsageError();
			} else if ((arg2.equals(NO_PRUNING_FLAG) && 
					arg3.equals(BOTH_FLAG))|| 
					(arg3.equals(NO_PRUNING_FLAG) && 
							arg2.equals(BOTH_FLAG))) {
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
	
	private static boolean usePruning(String[] args) {
		
		if ((args.length == 3 && args[2].equals(NO_PRUNING_FLAG)) ||
				(args.length == 4 && args[3].equals(NO_PRUNING_FLAG))) {
			return false;
		}
		
		return true;
	}
	
	private static boolean runBoth(String[] args) {
		
		if ((args.length == 3 && args[2].equals(BOTH_FLAG)) ||
				(args.length == 4 && args[3].equals(BOTH_FLAG))) {
			return true;
		}
		
		return false;
	}
	
	private static boolean useTimer(String[] args) {
		
		if ((args.length == 3 && args[2].equals(TIMER_FLAG)) ||
				(args.length == 4 && 
				(args[2].equals(TIMER_FLAG) || args[3].equals(TIMER_FLAG)))) {
			return true;
		}
		
		return false;
	}
	
	private static void printUsageError() {
		System.err.println("\n"
				+ "Usage: BestTrees <RTG file> N "
				+ "[ --no-pruning | -both ] [-timer] \n\n"
				+ "    N is an nonnegative integer \n"
				+ "    --no-pruning runs the BestTreesBasic algorithm\n"
				+ "    -both runs both BestTrees and BestTreesBasic\n"
				+ "    -timer measures the run-time for the algorithm(s)\n");
		System.exit(-1);
	}
	
	private static void printResult(List<String> result) {
		
		for (String treeString : result) {
			System.out.println(treeString);
		}
	}
	
}
