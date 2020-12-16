/*
 * Copyright 2015 Anna Jonsson for the research group Foundations of Language
 * Processing, Department of Computing Science, Umeå university
 *
 * This file is part of Betty.
 *
 * Betty is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Betty is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.umu.cs.flp.aj.nbest.wta.parsers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.Node;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.Symbol;
import se.umu.cs.flp.aj.nbest.wta.WTA;
import se.umu.cs.flp.aj.nbest.wta.exceptions.DuplicateRuleException;
import se.umu.cs.flp.aj.nbest.wta.exceptions.SymbolUsageException;


public class WTAParser implements Parser {

	public static final String EMPTY_LINE_REGEX = "^\\s*$";
	public static final String COMMENT_LINE_REGEX = "^//.*|^%.*";
	public static final String FINAL_REGEX =
			"^\\s*final\\s*([^,# \\[\\]]+\\s*,\\s*)*([^,# \\[\\]]+\\s*)+$";
	public static final String LEAF_RULE_REGEX =
			"^\\s*[^,# \\[\\]]+\\s*->\\s*[^,# \\[\\]]+" +
			"\\s*(\\#\\s*\\d+(\\.\\d+)?\\s*)?$";
	public static final String NON_LEAF_RULE_REGEX =
			"^\\s*[^,# \\[\\]]+\\[\\s*[^,# \\[\\]]+\\s*(,\\s*[^,# \\[\\]]+\\s*)*"
			+ "\\]\\s*->\\s*[^,# \\[\\]]+\\s*(\\#\\s*\\d+(\\.\\d+)?\\s*)*$";

	public static final String FINAL_SPLIT_REGEX = "\\s+|(\\s*,\\s*)";
	public static final String LEAF_RULE_SPLIT_REGEX = "\\s*((->)|#)\\s*";
	public static final String NON_LEAF_RULE_SPLIT_REGEX =
			"\\s*((\\]\\s*->)|#|\\[|,)\\s*";

	private Semiring semiring;

	private boolean forDerivations;
	private int ruleCounter;

	private WTA wta;

	public WTAParser(Semiring semiring) {
		this.semiring = semiring;
		this.ruleCounter = 0;
		this.forDerivations = false;
	}

	public WTA parseForBestTrees(String fileName) {
		this.forDerivations = false;
		return parse(fileName);
	}

	public WTA parseForBestDerivations(String fileName) {
		this.forDerivations = true;
		return parse(fileName);
	}

	private WTA parse(String fileName) {

		wta = new WTA(semiring);
		int rowCounter = 1;

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
			String line;

			try {

				while ((line = br.readLine()) != null) {
					parseLine(line);
					rowCounter++;
				}

			} catch (SymbolUsageException e) {
				System.err.println(e.getMessage());
				System.exit(-1);
			} catch (DuplicateRuleException e) {
				System.err.println(e.getMessage());
				System.exit(-1);
			} finally {
				br.close();
			}

		} catch (FileNotFoundException e) {
			System.err.println("File " + fileName + " not found.");
			System.exit(-1);
		} catch (IOException e) {
			System.err.println("IO error occured while reading " + fileName);
			System.exit(-1);
		} catch (IllegalArgumentException e) {
			System.err.println("Error found in line " + rowCounter +
					": " + e.getMessage());
			System.exit(-1);
		}

		return wta;
	}

	public void parseLine(String line)
			throws IllegalArgumentException, SymbolUsageException,
			DuplicateRuleException {

		if (line.matches(EMPTY_LINE_REGEX) ||
				line.matches(COMMENT_LINE_REGEX)) {
			// Ignore empty lines and comments.
		} else if (line.matches(FINAL_REGEX)) {
			parseFinal(line);
		} else if (line.matches(LEAF_RULE_REGEX)) {
			parseLeafRule(line);
		} else if (line.matches(NON_LEAF_RULE_REGEX)) {
			parseNonLeafRule(line);
		} else {
			throw new IllegalArgumentException("Line " + line +
					" was not correct.");
		}
	}

	private void parseFinal(String line) throws SymbolUsageException {
		String[] finals = line.trim().split(FINAL_SPLIT_REGEX);
		int size = finals.length;

		for (int i = 1; i < size; i++) {
			wta.setFinalState(finals[i]);
		}
	}

	private void parseLeafRule(String line)
			throws SymbolUsageException, DuplicateRuleException {

		String[] labels = line.trim().split(LEAF_RULE_SPLIT_REGEX);
		String symbolString = labels[0];

		if (forDerivations) {
			symbolString += "//rule" + ruleCounter;
		}

		Symbol symbol = wta.addSymbol(symbolString, 0);
		Node tree = new Node(symbol);
		State resultingState = wta.addState(labels[1]);

		Rule newRule;

		if (labels.length == 3) {
			double value = Double.parseDouble(labels[2]);
			Weight weight = semiring.createWeight(value);
			newRule = new Rule(tree, weight, resultingState);
//			newRule = new Rule<>(symbol, weight, resultingState);
		} else {
			Weight weight = semiring.one();
			newRule = new Rule(tree, weight, resultingState);
//			newRule = new Rule<>(symbol, weight, resultingState);
		}

		wta.addRule(newRule);
		ruleCounter++;
	}

	private void parseNonLeafRule(String line)
			throws SymbolUsageException, DuplicateRuleException {

		String[] labels = line.trim().split(NON_LEAF_RULE_SPLIT_REGEX);

		int numberOfLabels = labels.length;
		int numberOfLeftHandStates = numberOfLabels - 2;

		Weight weight = semiring.one();

		if (line.contains("#")) {
			double value = Double.parseDouble(labels[numberOfLabels - 1]);
			weight = semiring.createWeight(value);
			numberOfLeftHandStates -= 1;
		}

		String symbolString = labels[0];

		if (forDerivations) {
			symbolString += "//rule" + ruleCounter;
		}

		Symbol symbol = wta.addSymbol(symbolString, numberOfLeftHandStates);
		Node tree = new Node(symbol);
		State resultingState = wta.addState(labels[1 + numberOfLeftHandStates]);

		Rule newRule = new Rule(tree, weight, resultingState);

		for (int i = 1; i < numberOfLeftHandStates + 1; i++) {
			State state = wta.addState(labels[i]);
			state.getLabel().setNonterminal(true);
			newRule.addState(state);
			tree.addChild(new Node(state.getLabel()));
		}

		wta.addRule(newRule);
		ruleCounter++;
	}

}
