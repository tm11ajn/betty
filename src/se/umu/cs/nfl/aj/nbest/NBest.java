package se.umu.cs.nfl.aj.nbest;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import se.umu.cs.nfl.aj.wta.State;
import se.umu.cs.nfl.aj.wta.WTA;
import se.umu.cs.nfl.aj.wta.WTAParser;

public class NBest {

	private static int initialSize = 11;

	private static ArrayList<Node> exploredTrees = new ArrayList<Node>(); // T
	private static PriorityQueue<Node> treeQueue =
			new PriorityQueue<Node>(initialSize, new TreeComparator()); // K
	private static NestedMap<Node, State, Double> treeStateValTable =
			new NestedMap<>(); // C

	public static void main(String[] args) {

		String fileName = getFileName(args);
		WTA wta = WTAParser.parse(fileName);
		List<String> result = run(wta);

		for (String treeString : result) {
			System.out.println(treeString);
		}
	}

	public static String getFileName(String[] args) {

		if (args.length != 1) {
			System.err.println("Usage: java NBest <RTG file>");
			System.exit(-1);
		}

		return args[1];
	}

	public static List<String> run(WTA wta) {

		/* For result. */
		List<String> nBest = new ArrayList<String>();

		return nBest;
	}

//	private static void reset() {
//		exploredTrees = new ArrayList<Node>();
//		treeQueue = new PriorityQueue<Node>(initialSize, new TreeComparator());
//		treeStateValTable = new NestedMap<>();
//	}
}
