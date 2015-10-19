package se.umu.cs.flp.aj.nbest;

import java.util.List;

import se.umu.cs.flp.aj.wta.WTA;
import se.umu.cs.flp.aj.wta_handlers.WTAParser;

public class NBest {
	
	private static final String NO_PRUNING_FLAG = "--no-pruning";
	private static final String TIMER_FLAG = "-timer";
	
	public static void main(String[] args) {
		
		checkArgs(args);
		
		String fileName = getFileName(args);
		int N = getN(args);
		
		WTAParser wtaParser = new WTAParser();
		WTA wta = wtaParser.parse(fileName);

		if (useTimer(args)) {
			long startTime = System.nanoTime();
			List<String> resultBasic = BestTreesBasic.run(wta, N);
			long endTime = System.nanoTime();

			long duration = (endTime - startTime)/1000000;
			System.out.println("BestTreesBasic took " + duration + 
					" milliseconds");

			printResult(resultBasic);
			
			startTime = System.nanoTime();
			List<String> result = BestTrees.run(wta, N);
			endTime = System.nanoTime();

			duration = (endTime - startTime)/1000000;
			System.out.println("BestTrees took " + duration + " milliseconds");

			printResult(result);
		
		} else if (!usePruning(args)) {
			System.out.println("BestTreesBasic");
			List<String> resultBasic = BestTreesBasic.run(wta, N);
			printResult(resultBasic);
		} else {
			System.out.println("BestTrees");
			List<String> result = BestTrees.run(wta, N);
			printResult(result);
		}
		
	}
	
	private static void checkArgs(String[] args) {
		
		int nOfArgs = args.length;
		
		if (nOfArgs < 2 || nOfArgs > 3) {
			printUsageError();
		} else if (nOfArgs == 3) {
			
			if (!args[2].equals(NO_PRUNING_FLAG) && 
					!args[2].equals(TIMER_FLAG)) {
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
		
		if (args.length == 3 && args[2].equals(NO_PRUNING_FLAG)) {
			return false;
		}
		
		return true;
	}
	
	private static boolean useTimer(String[] args) {
		
		if (args.length == 3 && args[2].equals(TIMER_FLAG)) {
			return true;
		}
		
		return false;
	}
	
	private static void printUsageError() {
		System.err.println("\n"
				+ "Usage: BestTrees <RTG file> N "
				+ "[ --no-pruning | -timer ] \n\n"
				+ "    N is an nonnegative integer \n"
				+ "    --no-pruning runs the BestTreesBasic algorithm \n"
				+ "    -timer compares BestTrees and BestTreesBasic\n");
		System.exit(-1);
	}
	
	private static void printResult(List<String> result) {
		
		for (String treeString : result) {
			System.out.println(treeString);
		}
	}
	
}
