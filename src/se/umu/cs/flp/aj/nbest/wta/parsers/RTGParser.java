/*
 * Copyright 2018 Anna Jonsson for the research group Foundations of Language
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.Node;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.Symbol;
import se.umu.cs.flp.aj.nbest.wta.WTA;
import se.umu.cs.flp.aj.nbest.wta.exceptions.DuplicateRuleException;
import se.umu.cs.flp.aj.nbest.wta.exceptions.SymbolUsageException;

public class RTGParser implements Parser {

	public static final String EMPTY_LINE_REGEX = "^\\s*$";
	public static final String COMMENT_LINE_REGEX = "^//.*|^%.*";
	public static final String SPLIT_REGEX = "(->)|[^\\\\]#";

	private Semiring semiring;
	private boolean hasFinalState;

	private boolean forDerivations;
	private int ruleCounter;
	private HashMap<String, ArrayList<String>> sortedInput;
	private ArrayList<String> stateNames;

	private WTA wta;

	public RTGParser(Semiring semiring) {
		this.semiring = semiring;
		this.hasFinalState = false;
		this.ruleCounter = 0;
		this.forDerivations = false;
		this.sortedInput = new HashMap<>();
		this.stateNames = new ArrayList<>();
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
		wta = new WTA(semiring, true);
		int rowCounter = 1;

//		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),  StandardCharsets.UTF_8))) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
			String line;

			try {
				
				/* Read and preprocess the file, saving the data in a hashmap
				 * for another round of processing. The preprocessing checks
				 * which of the strings occur in the left-hand-side and that 
				 * are thereby states; here we also check for all errors that
				 * can occur in the input wrt format. */
				while ((line = br.readLine()) != null) {
					preprocessLine(line);
					rowCounter++;
				}
				
				/* Add the final state to the wta. */
				String stateLabel = stateNames.get(0);
				Symbol stateSymb = wta.addSymbol(stateLabel, 0);
				stateSymb.setNonterminal(true);
				wta.addState(stateLabel);
				wta.setFinalState(stateLabel);
				
				/* Re-process the lines of the file and create wta. */
				for (String stateName : stateNames) {
					for (String inputLine : sortedInput.get(stateName)) {
						parseLine(inputLine);
					}
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
	
	/* Find which strings correspond to states. Moreover: catches errors. */
	public void preprocessLine(String line)
			throws IllegalArgumentException, SymbolUsageException,
			DuplicateRuleException {

		if (line.matches(EMPTY_LINE_REGEX) ||
				line.matches(COMMENT_LINE_REGEX)) {
			// Ignore empty lines and comments.
		} else if (!hasFinalState) {
			if (containsParsingCharacters(line)) {
				throw new IllegalArgumentException("Line " + line +
						" should specify the final state but contains "
						+ "illegal characters.");
			}

			String stateLabel = line.trim();
			hasFinalState = true;
			stateNames.add(stateLabel);
			sortedInput.put(stateLabel, new ArrayList<>());
		} else {
			String s = line.trim();
			String[] sides = s.split(SPLIT_REGEX);
			
			if (sides.length < 2 || sides.length > 3) {
				throw new IllegalArgumentException("Line " + line +
						" contains too many or too few -> and/or #." );
			}

			String lhs = sides[0].trim();

			if (containsParsingCharacters(lhs)) {
				throw new IllegalArgumentException("Line " + line +
						" has parentheses in its left-hand side.");
			}
			
			if (!sortedInput.containsKey(lhs)) {
				sortedInput.put(lhs, new ArrayList<>());
				stateNames.add(lhs);
			}
			
			sortedInput.get(lhs).add(line);
		}
	}

	public void parseLine(String line)
			throws IllegalArgumentException, SymbolUsageException,
			DuplicateRuleException {
		String[] sides = line.split(SPLIT_REGEX);
		Weight weight = semiring.one();
		if (sides.length > 2) {
			double value = Double.parseDouble(sides[2]);
			weight = semiring.createWeight(value);
		}
		String lhs = sides[0].trim();
		String rhs = sides[1].trim();
		Node tree = buildTree(rhs, 0);
		State resultingState = wta.addState(lhs);
		resultingState.getLabel().setNonterminal(true);
		Rule newRule = new Rule(tree, weight, resultingState);
		ArrayList<Node> leaves = tree.getLeaves();
		
		for (Node leaf : leaves) {
			String label = leaf.getLabel().getLabel();
			if (sortedInput.containsKey(label)) {
				leaf.getLabel().setNonterminal(true);
				newRule.addState(wta.addState(label));
			}
		}
		
		wta.addRule(newRule);
		ruleCounter++;
	}

	/* Given a right-hand side, build the tree that the string represents. 
	 * This process is done recursively, which is why we need the children
	 * counter. */
	private Node buildTree(String rhs, int nOfChildren)
			throws SymbolUsageException {

		if (!containsParsingCharacters(rhs)) {
			String symbolString = rhs.trim();

			if (forDerivations && nOfChildren > 0) {
				symbolString += "//rule" + ruleCounter;
			}

			Symbol symbol = wta.addSymbol(symbolString, nOfChildren);
			Node tree = new Node(symbol);

			return tree;

		} else {
			int length = rhs.length();
			Stack<Character> parStack = new Stack<>();
			String currentString = "";

			Node tree = null;
			String treeString = null;
			LinkedList<Node> children = new LinkedList<>();

			for (int i = 0; i < length; i++) {
				char c = rhs.charAt(i);

				if (c == '(') {
					treeString = rhs.substring(0, i);
					boolean done = false;
					parStack.push(c);

					while (!done) {
						i++;
						c = rhs.charAt(i);

						if (c == '(') {
							parStack.push(c);
						} else if (c == ')') {
							parStack.pop();
						}

						if (parStack.empty()) {
							done = true;
						}

						if (done || (c == ' ' &&
								!currentString.isEmpty() &&
								parStack.size() == 1)) {
							Node tempTree = buildTree(currentString, 0);
							children.addLast(tempTree);
							currentString = "";
						} else {
							currentString += c;
						}
					}
				}
			}

			int size = children.size();
			tree = buildTree(treeString, size);

			while (!children.isEmpty()) {
				tree.addChild(children.pollFirst());
			}

			return tree;
		}
	}

	/* Checks if the line contains illegal charachters, in this case
	 * the right-hand side should not contain any characters related
	 * to parsing. */
	private boolean containsParsingCharacters(String line) {

		if (line.matches(".*\\(.*|.*\\).*|.*[^\\\\]#.*|.*->.*")) {
			return true;
		}

		return false;
	}

}
