package se.umu.cs.flp.aj.nbest.wta.parsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

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
	public static final String FINAL_REGEX = "^\\s*[^,# \\[\\]]+\\s*$";

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
//	private HashMap<State, State> finalStates;
//	private State finalState;
	private boolean hasFinalState;

	private boolean forDerivations;
	private int ruleCounter;

	public RTGParser(Semiring semiring) {
		this.semiring = semiring;
//		this.finalStates = new HashMap<>();
//		this.finalState = null;
		this.hasFinalState = false;
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

		WTA wta = new WTA(semiring);
		int rowCounter = 1;

		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line;

			while ((line = br.readLine()) != null) {
				parseLine(line, wta);
				rowCounter++;
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
		} catch (SymbolUsageException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		} catch (DuplicateRuleException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}

		return wta;
	}



	public void parseLine(String line, WTA wta)
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

			wta.setFinalState(line.trim());
			hasFinalState = true;

		} else {
			String[] sides = line.trim().split("(->)|#");
			Weight weight = semiring.one();

			if (sides.length < 2 || sides.length > 3) {
				throw new IllegalArgumentException("Line " + line +
						" contains too many or too few -> and/or #." );
			} else if (sides.length == 3) {
				double value = Double.parseDouble(sides[2]);
				weight = semiring.createWeight(value);
			}

			String lhs = sides[0].trim();
			String rhs = sides[1].trim();

			if (containsParsingCharacters(lhs)) {
				throw new IllegalArgumentException("Line " + line +
						" has parentheses in its left-hand side.");
			}

			Node tree = buildTree(wta, rhs);
			State resultingState = wta.addState(lhs);
			resultingState.getLabel().setNonterminal(true);

			Rule newRule = new Rule(tree, weight,
					resultingState);

//			if (!containsParsingCharacters(lhs)) { // Leaf rule
//				String symbolString = lhs;
//
//				if (forDerivations) {
//					symbolString += "//rule" + ruleCounter;
//				}
//
//				Symbol symbol = wta.addSymbol(symbolString, 0);
//				State resultingState = wta.addState(rhs);
//				Node tree = new Node(symbol);
//
//				newRule = new Rule<>(symbol, weight, resultingState, tree);
//
//			} else { // Non-leaf rule
//
//				if (!containsParsingCharacters(lhs)) {
//					Node tree = new Node(wta.addSymbol(lhs, 0));
//				} else {
//
//					Node tree = new Node(getSymbol(wta, lhs));
//
//					tree.addChild();
//
//					String[] parts = lhs.split(" ");
//					int numberOfLeftHandStates = parts.length;
//
//					String symbolString = parts[0];
//
//					if (forDerivations) {
//						symbolString += "//rule" + ruleCounter;
//					}
//
//					Symbol symbol = wta.addSymbol(symbolString, numberOfLeftHandStates);
//					State resultingState = wta.addState(parts[1 + numberOfLeftHandStates]);
//
//					newRule = new Rule<>(symbol, weight, resultingState);
//
//					for (int i = 1; i < numberOfLeftHandStates + 1; i++) {
//						newRule.addState(wta.addState(parts[i]));
//					}
//
//				}
//			}

			wta.addRule(newRule);
			ruleCounter++;
		}


//		if (line.matches(EMPTY_LINE_REGEX) ||
//				line.matches(COMMENT_LINE_REGEX)) {
//			// Ignore empty lines and comments.
//		} else if (line.matches(FINAL_REGEX)) {
//			parseFinal(line, wta);
//		} else if (line.matches(LEAF_RULE_REGEX)) {
//			parseLeafRule(line, wta);
//		} else if (line.matches(NON_LEAF_RULE_REGEX)) {
//			parseNonLeafRule(line, wta);
//		} else {
//			throw new IllegalArgumentException("Line " + line +
//					" was not correct.");
//		}
	}

	private Node buildTree(WTA wta, String lhs)
			throws SymbolUsageException {

		if (!containsParsingCharacters(lhs)) { // Leaf rule
			String symbolString = lhs;

			if (forDerivations) {
				symbolString += "//rule" + ruleCounter;
			}

			Symbol symbol = wta.addSymbol(symbolString, 0);
			Node tree = new Node(symbol);

			return tree;

		} else { // Non-leaf rule

			String[] parts = lhs.split(" ");
			int numberOfLeftHandStates = parts.length;

			Node tree = new Node(wta.addSymbol(lhs,
					numberOfLeftHandStates));

			String symbolString = parts[0];

			if (forDerivations) {
				symbolString += "//rule" + ruleCounter;
			}

			Symbol symbol = wta.addSymbol(symbolString, numberOfLeftHandStates);
			State resultingState = wta.addState(parts[1 + numberOfLeftHandStates]);

//			newRule = new Rule<>(symbol, weight, resultingState);
			// How to handle states? Add in tree and to wta but just mark them as states?
			// they are states if leaves is not true.

			for (int i = 1; i < numberOfLeftHandStates + 1; i++) {
//				newRule.addState(wta.addState(parts[i]));
				tree.addChild(buildTree(wta, parts[i]));
			}

			return tree;
		}
	}

//	private Symbol getSymbol(WTA wta, String lhs) {
//
//		String[] parts = lhs.split(" ");
//		int numberOfLeftHandStates = parts.length;
//
//		String symbolString = parts[0];
//
//		if (forDerivations) {
//			symbolString += "//rule" + ruleCounter;
//		}
//
//		Symbol symbol = wta.addSymbol(symbolString, numberOfLeftHandStates);
//		State resultingState = wta.addState(parts[1 + numberOfLeftHandStates]);
//
//		newRule = new Rule<>(symbol, weight, resultingState);
//
//		for (int i = 1; i < numberOfLeftHandStates + 1; i++) {
//			newRule.addState(wta.addState(parts[i]));
//		}
//	}

	private boolean containsParsingCharacters(String line) {

		if (line.matches(".*\\(.*|.*\\).*|.*#.*|.*->.*")) {
			return true;
		}

		return false;
	}

	private void parseFinal(String line, WTA wta) {
		String[] finals = line.trim().split(FINAL_SPLIT_REGEX);
		int size = finals.length;

//		for (int i = 1; i < size; i++) {
//			if (!finalStates.containsKey(finals[i])) {
//				wta.setFinalState(finals[i]);
//			}
//		}
	}

//	private void parseLeafRule(String line, WTA wta)
//			throws SymbolUsageException, DuplicateRuleException {
//
//		String[] labels = line.trim().split(LEAF_RULE_SPLIT_REGEX);
//
//		String symbolString = labels[0];
//
//		if (forDerivations) {
//			symbolString += "//rule" + ruleCounter;
//		}
//
//		Symbol symbol = wta.addSymbol(symbolString, 0);
//		State resultingState = wta.addState(labels[1]);
//
//		Rule newRule;
//
//		if (labels.length == 3) {
//			double value = Double.parseDouble(labels[2]);
//			Weight weight = semiring.createWeight(value);
//			newRule = new Rule(symbol, weight, resultingState);
//		} else {
//			Weight weight = semiring.one();
//			newRule = new Rule(symbol, weight, resultingState);
//		}
//
//		wta.addRule(newRule);
//		ruleCounter++;
//	}

//	private void parseNonLeafRule(String line, WTA wta)
//			throws SymbolUsageException, DuplicateRuleException {
//
//		String[] labels = line.trim().split(NON_LEAF_RULE_SPLIT_REGEX);
//
//		int numberOfLabels = labels.length;
//		int numberOfLeftHandStates = numberOfLabels - 2;
//
//		Weight weight = semiring.one();
//
//		if (line.contains("#")) {
//			double value = Double.parseDouble(labels[numberOfLabels - 1]);
//			weight = semiring.createWeight(value);
//			numberOfLeftHandStates -= 1;
//		}
//
//		String symbolString = labels[0];
//
//		if (forDerivations) {
//			symbolString += "//rule" + ruleCounter;
//		}
//
//		Symbol symbol = wta.addSymbol(symbolString, numberOfLeftHandStates);
//		State resultingState = wta.addState(labels[1 + numberOfLeftHandStates]);
//
//		Rule newRule = new Rule(symbol, weight, resultingState);
//
//		for (int i = 1; i < numberOfLeftHandStates + 1; i++) {
//			newRule.addState(wta.addState(labels[i]));
//		}
//		wta.addRule(newRule);
//		ruleCounter++;
//	}

}
