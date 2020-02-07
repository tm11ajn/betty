package se.umu.cs.flp.aj.nbest.wta.parsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

	private Semiring semiring;
	private boolean hasFinalState;

	private boolean forDerivations;
	private int ruleCounter;
//	private ArrayList<Rule> tempRules;
	private HashMap<State, ArrayList<Rule>> tempRules;

	private WTA wta;

	public RTGParser(Semiring semiring) {
		this.semiring = semiring;
		this.hasFinalState = false;
		this.ruleCounter = 0;
		this.forDerivations = false;
//		tempRules = new ArrayList<>();
		tempRules = new HashMap<>();
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
//		tempRules = new ArrayList<>();
		tempRules = new HashMap<>();
		wta = new WTA(semiring, true);
		int rowCounter = 1;

		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line;

			try {
				
				// TODO: change this so that we only collect nonterminals the first round and save
				// the lines in a hashmap in which the key is the resulting state, and then 
				// we traverse them and create the elements the second time around

				while ((line = br.readLine()) != null) {
					parseLine(line);
					rowCounter++;
				}

//				for (Rule r : tempRules) {
//					ArrayList<Node> leaves = r.getTree().getLeaves();
//
//					for (Node leaf : leaves) {
//						if (leaf.getLabel().isNonterminal()) {
//							r.addState(wta.addState(leaf.getLabel().getLabel()));
//						}
//					}
//					wta.addRule(r);
//				}
				
				for (ArrayList<Rule> list : tempRules.values()) {
					for (Rule r : list) {
						ArrayList<Node> leaves = r.getTree().getLeaves();

						for (Node leaf : leaves) {
							if (leaf.getLabel().isNonterminal()) {
								r.addState(wta.addState(leaf.getLabel().getLabel()));
							}
						}
						wta.addRule(r);
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



	public void parseLine(String line)
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
			Symbol stateSymb = wta.addSymbol(stateLabel, 0);
			stateSymb.setNonterminal(true);
			wta.addState(stateLabel);
			wta.setFinalState(stateLabel);
			hasFinalState = true;

		} else {
			String[] sides = line.trim().split("(->)|[^\\\\]#");
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

			Node tree = buildTree(rhs, 0);
			State resultingState = wta.addState(lhs);
			resultingState.getLabel().setNonterminal(true);

			Rule newRule = new Rule(tree, weight,
					resultingState);

			
//			tempRules.add(newRule);
			
			if (tempRules.get(resultingState) == null) {
				tempRules.put(resultingState, new ArrayList<>());
			}
			
			tempRules.get(resultingState).add(newRule);
			
			ruleCounter++;
		}
	}

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

	private boolean containsParsingCharacters(String line) {

		if (line.matches(".*\\(.*|.*\\).*|.*[^\\\\]#.*|.*->.*")) {
			return true;
		}

		return false;
	}

}
