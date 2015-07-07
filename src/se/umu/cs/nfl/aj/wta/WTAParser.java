package se.umu.cs.nfl.aj.wta;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class WTAParser {

	public static final String EMPTY_LINE_REGEX = "^\\s*$";
	public static final String FINAL_REGEX = "^\\s*final\\s*([a-z0-9]\\s*)+$";
	public static final String LEAF_RULE_REGEX =
			"^\\s*[a-z0-9]+\\s*->\\s*[a-z0-9]+" +
			"\\s*(\\#\\s*\\d+(\\.\\d+)?\\s*)?$";
	public static final String NON_LEAF_RULE_REGEX =
			"^\\s*[a-z0-9]+\\[\\s*[a-z0-9]+\\s*(,\\s*[a-z0-9]+\\s*)*\\]\\s*->" +
			"\\s*[a-z0-9]+\\s*(\\#\\s*\\d+(\\.\\d+)?\\s*)*$";

	public static final String FINAL_SPLIT_REGEX = "\\s+";
	public static final String LEAF_RULE_SPLIT_REGEX = "\\s*((->)|#)\\s*";
	public static final String NON_LEAF_RULE_SPLIT_REGEX =
			"\\s*((\\]\\s*->)|#|\\[|,)\\s*";

	public WTA parse(String fileName) {

		WTA wta = new WTA();
		int rowCounter = 1;

		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

			String line;

			while ((line = br.readLine()) != null) {
				parseLine(line, wta);
				line = br.readLine();
				rowCounter++;
			}

		} catch (FileNotFoundException e) {
			System.err.println("File " + fileName + " not found.");
			e.printStackTrace();
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
		}

		return wta;
	}

	public void parseLine(String line, WTA wta)
			throws IllegalArgumentException, SymbolUsageException {

		if (line.matches(EMPTY_LINE_REGEX)) {
			// Ignore empty lines.
		} else if (line.matches(FINAL_REGEX)) {
			parseFinal(line, wta);
		} else if (line.matches(LEAF_RULE_REGEX)) {
			parseLeafRule(line, wta);
		} else if (line.matches(NON_LEAF_RULE_REGEX)) {
			parseNonLeafRule(line, wta);
		} else {
			throw new IllegalArgumentException("Line " + line +
					" was not correct.");
		}
	}

	private void parseFinal(String line, WTA wta) {

		String[] finals = line.trim().split(FINAL_SPLIT_REGEX);
		int size = finals.length;

		for (int i = 1; i < size; i++) {
			wta.setFinalState(finals[i]);
		}
	}

	private void parseLeafRule(String line, WTA wta)
			throws SymbolUsageException {

		String[] labels = line.trim().split(LEAF_RULE_SPLIT_REGEX);
		
		checkSymbol(labels[0]);

		Symbol symbol = wta.addSymbol(labels[0], 0);
		State resultingState = wta.addState(labels[1]);

		Rule newRule;

		if (labels.length == 3) {
			double value = Double.parseDouble(labels[2]); // TODO handle exception maybe NOT (Trust your input!)
			Weight weight = new Weight(value);
			newRule = new Rule(symbol, weight, resultingState);
		} else {
			newRule = new Rule(symbol, resultingState);
		}

		wta.addRule(newRule);
	}

	private void parseNonLeafRule(String line, WTA wta)
			throws SymbolUsageException {

		String[] labels = line.trim().split(NON_LEAF_RULE_SPLIT_REGEX);

		int numberOfLabels = labels.length;
		int numberOfLeftHandStates = numberOfLabels - 2;

		Weight weight = new Weight(0);

		if (line.contains("#")) {
			double value = Double.parseDouble(labels[numberOfLabels - 1]);
			weight = new Weight(value);
			numberOfLeftHandStates -= 1;
		}
		
		checkSymbol(labels[0]);

		Symbol symbol = wta.addSymbol(labels[0], numberOfLeftHandStates);
		State resultingState = wta.addState(labels[1 + numberOfLeftHandStates]);

		Rule newRule = new Rule(symbol, weight, resultingState);

		for (int i = 1; i < numberOfLeftHandStates + 1; i++) {
			newRule.addState(wta.addState(labels[i]));
		}

		wta.addRule(newRule);
	}
	
	private void checkSymbol(String label) throws SymbolUsageException {
		
		if (label.equals(Symbol.RESERVED_SYMBOL_STRING)) {
			throw new SymbolUsageException("The symbol " + label + 
					" is reserved and cannot be used in the input WTA.");
		}
	}

}
