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

package se.umu.cs.flp.aj.nbest;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import se.umu.cs.flp.aj.knuth.KnuthBestDerivations;
import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.semiring.SemiringFactory;
import se.umu.cs.flp.aj.nbest.treedata.Context;
import se.umu.cs.flp.aj.nbest.wta.WTA;
import se.umu.cs.flp.aj.nbest.wta.parsers.Parser;
import se.umu.cs.flp.aj.nbest.wta.parsers.RTGParser;
import se.umu.cs.flp.aj.nbest.wta.parsers.WTAParser;


public class NBest {

	private static final String FILE_FLAG = "f";
	private static final String FILE_FLAG_LONG = "file";

	private static final String FILE_TYPE_FLAG = "t";
	private static final String FILE_TYPE_LONG = "type";
	private static final String DEFAULT_FILE_TYPE = "wta";

	private static final String N_FLAG = "N";

	private static final String VERSION_FLAG = "v";
	private static final String VERSION_FLAG_LONG = "version";
	private static final String RULE_QUEUE_ARG = "2";
	private static final String TREE_QUEUE_ARG = "1";
	private static final String ALL_ARG = "all";
	private static final String DEFAULT_VERSION = RULE_QUEUE_ARG;

	private static final String TIMER_FLAG = "timer";
	private static final boolean DEFAULT_TIMER_VAL = false;

	private static final String SEMIRING_FLAG = "s";
	private static final String SEMIRING_FLAG_LONG = "semiring";
	private static final String TROPICAL_SEMIRING = "tropical";
	private static final String DEFAULT_SEMIRING = TROPICAL_SEMIRING;

	private static final String DERIVATION_FLAG = "r";
	private static final String DERIVATION_FLAG_LONG = "runs";
	
	private static final String TRICK_FLAG_LONG = "trick";
	
	

	public static void main(String[] args) {

		Options options = createOptions();
		CommandLineParser clParser = new DefaultParser();

		String fileName = "";
		int N = 0;
		String version = DEFAULT_VERSION;
		boolean timer = DEFAULT_TIMER_VAL;
		String semiringType = DEFAULT_SEMIRING;
		boolean derivations = false;
		String fileType = DEFAULT_FILE_TYPE;
		boolean trick = false;

		try {
			CommandLine cmd = clParser.parse(options, args);

			fileName = cmd.getOptionValue(FILE_FLAG);
			N = Integer.parseInt(cmd.getOptionValue(N_FLAG));

			if (cmd.hasOption(FILE_TYPE_FLAG)) {
				fileType = cmd.getOptionValue(FILE_TYPE_FLAG);
			}

			if (N < 0) {
				throw new NumberFormatException();
			}

			if (cmd.hasOption(VERSION_FLAG)) {
				version = cmd.getOptionValue(VERSION_FLAG);
			}

			if (cmd.hasOption(TIMER_FLAG)) {
				timer = true;
			}

			if (cmd.hasOption(SEMIRING_FLAG)) {
				semiringType = cmd.getOptionValue(SEMIRING_FLAG);
			}

			if (cmd.hasOption(DERIVATION_FLAG)) {
				derivations = true;
			}
			
			if (cmd.hasOption(TRICK_FLAG_LONG)) {
				trick = true;
			}
			
			if (trick && !derivations) {
				throw new ParseException("Trick only valid for best runs");
			}

		} catch (ParseException e) {
			System.out.println(e.getMessage());
			HelpFormatter help = new HelpFormatter();
			System.out.println();
			help.printHelp("BestTrees", options);
			System.exit(1);
		} catch (NumberFormatException e) {
			System.out.println("N takes a nonnegative integer.");
			System.exit(1);
		}

		SemiringFactory semFac = new SemiringFactory();
		Semiring semiring = semFac.getSemiring(semiringType);

		Parser parser = null;
		WTA wta = null;
		
		if (fileType.equals("wta")) {
			parser = new WTAParser(semiring);
		} else if (fileType.equals("rtg")) {
			parser = new RTGParser(semiring);
		}
		
		// TOOD: fix so that derivations still works for the old version, or not?
		wta = parser.parseForBestTrees(fileName);
		
//System.out.println(wta);
		
//		if (derivations) {
//			wta = parser.parseForBestDerivations(fileName);
//		} else {
//			wta = parser.parseForBestTrees(fileName);
//		}

		long duration;
		long accumulativeTime;
		
		Timer threadTimer = new Timer();

		System.out.println("Pre-computing best contexts...");
		threadTimer.start();
		Context[] bestContexts = KnuthBestDerivations.computeBestContexts(wta, trick);
		List<String> result = new LinkedList<>();
		duration = threadTimer.elapsed();
		accumulativeTime = duration;
		System.out.println("Best contexts done (took "
				+ duration + " milliseconds).");

		if (version.equals(RULE_QUEUE_ARG) || version.equals(ALL_ARG)) {
			System.out.println("Running BestTrees version 2...");
			threadTimer.start();
			result = BestTrees2.run(wta, N, bestContexts, derivations, trick);
			duration = threadTimer.elapsed();
			printResult(result, derivations);

			if (timer) {
				accumulativeTime += duration;
				System.out.println("BestTrees version 2 took " + duration +
						" milliseconds");
			}
		}

		if (version.equals(TREE_QUEUE_ARG) || version.equals(ALL_ARG)) {
			System.out.println("Running BestTrees version 1...");
//			BestTrees.setSmallestCompletions(bestContexts);
			
			threadTimer.start();
			result = BestTrees.run(wta, bestContexts, N);
			duration = threadTimer.elapsed();

			printResult(result, derivations);

			if (timer) {
				accumulativeTime += duration;
				System.out.println("BestTrees version 1 took " + duration +
						" milliseconds");
			}
		}

		if (result.size() < N) {
			String outputType;

			if (derivations) {
				outputType = "runs";
			} else {
				outputType = "trees";
			}

			System.out.println("BestTrees only found " + result.size() +
					" of the " + N + " requested " + outputType);
		}

		if (timer) {
			System.out.println("Total time for BestTrees including "
					+ "the preprocessing of best contexts: " +
					accumulativeTime + " ms");
		}
	}

	private static Options createOptions() {

		Options options = new Options();
		Option wtaFileOpt = new Option(FILE_FLAG, FILE_FLAG_LONG,
				true, "file containing the input wta");
		Option fileTypeOpt = new Option(FILE_TYPE_FLAG, FILE_TYPE_LONG,
				true, "file type (detault is "+ DEFAULT_FILE_TYPE + ")");
		Option nOpt = new Option(N_FLAG, true, "number of trees wanted");
		Option versionOpt = new Option(VERSION_FLAG, VERSION_FLAG_LONG,
				true, "version of BestTrees; arg can be\n" + RULE_QUEUE_ARG +
				" (uses a pruned rule queue), \n" +
						TREE_QUEUE_ARG +
				" (uses a pruned tree queue) or \n" +
						ALL_ARG +
				" (runs all versions).\n The default version is " +
				DEFAULT_VERSION);
		Option timerOpt = new Option(TIMER_FLAG, false,
				"measures running time(s)");
		Option semiringOpt = new Option(SEMIRING_FLAG, SEMIRING_FLAG_LONG, true,
				"semiring used for BestTrees (the default semiring is the "
						+ DEFAULT_SEMIRING + " semiring)");
		Option derivationsOpt = new Option(DERIVATION_FLAG,
				DERIVATION_FLAG_LONG, false,
				"finds the best runs instead of the best trees");
		Option trickOpt = new Option(TRICK_FLAG_LONG, false, 
				"uses trick that increases pruning for best runs; not valid for best trees");

		wtaFileOpt.setArgName("wta file");
		fileTypeOpt.setArgName("file type ('wta' or 'rtg')");
		nOpt.setArgName("nonnegative integer");
		semiringOpt.setArgName("semiring");

		wtaFileOpt.setRequired(true);
		fileTypeOpt.setRequired(false);
		nOpt.setRequired(true);
		versionOpt.setRequired(false);
		timerOpt.setRequired(false);
		semiringOpt.setRequired(false);
		derivationsOpt.setRequired(false);
		trickOpt.setRequired(false);

		options.addOption(wtaFileOpt);
		options.addOption(fileTypeOpt);
		options.addOption(nOpt);
		options.addOption(versionOpt);
		options.addOption(timerOpt);
		options.addOption(semiringOpt);
		options.addOption(derivationsOpt);
		options.addOption(trickOpt);

		return options;
	}

	private static void printResult(List<String> result, boolean derivations) {

		for (String treeString : result) {

//			if (derivations) {
//				treeString = treeString.replaceAll("//rule[0-9]*", "");
//			}

			System.out.println(treeString);
		}
	}

//	private static void printResult2(List<TreeKeeper2> result, boolean derivations) {
//
//		for (TreeKeeper2 tk : result) {
//
//			String treeString = tk.getTree() + " " + tk.getRunWeight();
//
//			if (derivations) {
//				treeString = treeString.replaceAll("//rule[0-9]*", "");
//			}
//
//			System.out.println(treeString);
//		}
//	}

}
