package se.umu.cs.nfl.aj.nbest;


import java.util.List;

import se.umu.cs.nfl.aj.wta.WTA;
import se.umu.cs.nfl.aj.wta_handlers.WTAParser;

public class NBest {

	public static void main(String[] args) {

		String fileName = getFileName(args);
		int N = getN(args);
		WTAParser wtaParser = new WTAParser();
		WTA wta = wtaParser.parse(fileName);

		long startTime = System.nanoTime();
		List<String> resultBasic = BestTreesBasic.run(wta, N);
		long endTime = System.nanoTime();

		long duration = (endTime - startTime)/1000000;
		System.out.println("BestTreesBasic took " + duration + " milliseconds");
		
		for (String treeString : resultBasic) {
			System.out.println(treeString);
		}
		
		startTime = System.nanoTime();
		List<String> result = BestTrees.run(wta, N);
		endTime = System.nanoTime();
		
		duration = (endTime - startTime)/1000000;
		System.out.println("BestTrees took " + duration + " milliseconds");
		
		for (String treeString : result) {
			System.out.println(treeString);
		}
	}

	public static String getFileName(String[] args) {

		if (args.length != 2) {
			printUsageError();
		}

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
	
	private static void printUsageError() {
		System.err.println("Usage: java NBest <RTG file> <N> "
				+ "(where N is an nonnegative integer)");
		System.exit(-1);
	}
	
}
