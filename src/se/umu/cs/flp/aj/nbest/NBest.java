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

import java.util.HashMap;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import se.umu.cs.flp.aj.knuth.SmallestCompletionsFinder;
import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.semiring.SemiringFactory;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.WTA;
import se.umu.cs.flp.aj.nbest.wta.handlers.WTAParser;


public class NBest {

	private static final String FILE_FLAG = "f";
	private static final String FILE_FLAG_LONG = "file";

	private static final String N_FLAG = "N";

	private static final String VERSION_FLAG = "v";
	private static final String VERSION_FLAG_LONG = "version";
	private static final String RULE_QUEUE_ARG = "2";
	private static final String TREE_QUEUE_ARG = "1";
	private static final String BASIC_ARG = "basic";
	private static final String ALL_ARG = "all";
	private static final String DEFAULT_VERSION = RULE_QUEUE_ARG;

	private static final String TIMER_FLAG = "timer";
	private static final boolean DEFAULT_TIMER_VAL = false;

	private static final String SEMIRING_FLAG = "s";
	private static final String SEMIRING_FLAG_LONG = "semiring";
	private static final String TROPICAL_SEMIRING = "tropical";
	private static final String DEFAULT_SEMIRING = TROPICAL_SEMIRING;


	public static void main(String[] args) {

		Options options = createOptions();
		CommandLineParser clParser = new DefaultParser();

		String fileName = "";
		int N = 0;
		String version = DEFAULT_VERSION;
		boolean timer = DEFAULT_TIMER_VAL;
		String semiringType = DEFAULT_SEMIRING;

		try {
			CommandLine cmd = clParser.parse(options, args);

			fileName = cmd.getOptionValue(FILE_FLAG);
			N = Integer.parseInt(cmd.getOptionValue(N_FLAG));

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

		WTAParser wtaParser = new WTAParser(semiring);
		WTA wta = wtaParser.parse(fileName);

		long startTime;
		long endTime;
		long duration;

		System.out.println("Pre-computing smallest completions...");
		startTime = System.nanoTime();
		HashMap<State, Weight> smallestCompletions =
				SmallestCompletionsFinder.findSmallestCompletionWeights(wta);
		endTime = System.nanoTime();
		duration = (endTime - startTime)/1000000;
		System.out.println("Smallest completions done (took "
				+ duration + " milliseconds).");

		if (version.equals(RULE_QUEUE_ARG) || version.equals(ALL_ARG)) {
			System.out.println("Running BestTrees version 2...");
			BestTrees2.setSmallestCompletions(smallestCompletions);

			startTime = System.nanoTime();
			List<String> result = BestTrees2.run(wta, N);
			endTime = System.nanoTime();

			printResult(result);

			if (timer) {
				duration = (endTime - startTime)/1000000;
				System.out.println("BestTrees version 2 took " + duration +
						" milliseconds");
			}
		}

		if (version.equals(TREE_QUEUE_ARG) || version.equals(ALL_ARG)) {
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

		if (version.equals(BASIC_ARG) || version.equals(ALL_ARG)) {
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

	private static Options createOptions() {

		Options options = new Options();
		Option wtaFileOpt = new Option(FILE_FLAG, FILE_FLAG_LONG,
				true, "file containing the input wta");
		Option nOpt = new Option(N_FLAG, true, "number of trees wanted");
		Option versionOpt = new Option(VERSION_FLAG, VERSION_FLAG_LONG,
				true, "version of BestTrees; arg can be\n" + RULE_QUEUE_ARG +
				" (uses a pruned rule queue), \n" +
						TREE_QUEUE_ARG +
				" (uses a pruned tree queue), \n" +
						BASIC_ARG +
				" (basic version) or \n" + ALL_ARG +
				" (runs all versions).\n The default version is " +
				DEFAULT_VERSION);
		Option timerOpt = new Option(TIMER_FLAG, false,
				"measures running time(s)");
		Option semiringOpt = new Option(SEMIRING_FLAG, SEMIRING_FLAG_LONG, true,
				"semiring used for BestTrees (the default semiring is the "
						+ DEFAULT_SEMIRING + " semiring)");

		wtaFileOpt.setArgName("wta file");
		nOpt.setArgName("nonnegative integer");
		semiringOpt.setArgName("semiring");

		wtaFileOpt.setRequired(true);
		nOpt.setRequired(true);
		versionOpt.setRequired(false);
		timerOpt.setRequired(false);
		semiringOpt.setRequired(false);

		options.addOption(wtaFileOpt);
		options.addOption(nOpt);
		options.addOption(versionOpt);
		options.addOption(timerOpt);
		options.addOption(semiringOpt);

		return options;
	}

	private static void printResult(List<String> result) {

		for (String treeString : result) {
			System.out.println(treeString);
		}
	}

}
