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
		}

		return wta;
	}

	public void parseLine(String line, WTA wta)
			throws IllegalArgumentException {

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

	private void parseLeafRule(String line, WTA wta) {

		String[] labels = line.trim().split(LEAF_RULE_SPLIT_REGEX);

		Symbol symbol = wta.getSymbol(labels[0], 0);
		State resultingState = wta.getState(labels[1]);

		Rule newRule;

//		if (resultingState == null) {
//			resultingState = new State(labels[1]);
//			wta.addState(resultingState);
//		}

		if (labels.length == 3) {
			double weight = Double.parseDouble(labels[2]); // TODO handle exception maybe NOT (Trust your input!)
			newRule = new Rule(symbol, weight, resultingState);
		} else {
			newRule = new Rule(symbol, resultingState);
			System.out.println("H�R �R JAG");
			System.out.println("Nya regeln:");
			System.out.println("Symbol=" + newRule.getSymbol().getLabel());
			System.out.println("Resulting state (label)=" + newRule.getResultingState().getLabel());
		}

		wta.addRule(newRule);
		//wta.getRulesBySymbol(new Symbol("a", 0)).isEmpty();

		//boolean b = wta.getRulesBySymbol(new Symbol("a", 0)).isEmpty();

		//System.out.println(b);
	}

	private Rule parseNonLeafRule(String line, WTA wta) {

		String[] labels = line.trim().split(NON_LEAF_RULE_SPLIT_REGEX);

		int numberOfLabels = labels.length;
		int numberOfLeftHandStates = numberOfLabels - 1;

		double weight = 0;

		if (line.contains("#")) {
			weight = Double.parseDouble(labels[numberOfLabels - 1]);
			numberOfLeftHandStates -= 1;
		}

		Symbol symbol = wta.getSymbol(labels[0], numberOfLeftHandStates);
		State resultingState = wta.getState(labels[numberOfLabels - 1]);

		Rule newRule = new Rule(symbol, weight, resultingState);

		for (int i = 1; i < numberOfLeftHandStates + 1; i++) { // TODO FINISH!
			newRule.addState(wta.getState(labels[i]));
		}

		return newRule;
	}

}
