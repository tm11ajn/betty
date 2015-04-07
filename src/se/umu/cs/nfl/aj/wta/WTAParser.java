package se.umu.cs.nfl.aj.wta;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class WTAParser {

	private static String emptyLineRegExp = "^\\s*$";
	private static String finalRegExp = "^\\s*final\\s*\\d+(\\.\\d+)?\\s*$";
	private static String leafRuleRegExp =
			"^\\s*[a-z0-9]+\\s*->\\s*[a-z0-9]+\\s*\\#\\s*\\d+(\\.\\d+)?\\s*$";
	private static String ruleRegexp =
			"^\\s*[a-z0-9]+\\[\\s*[a-z0-9]+\\s*(,\\s*[a-z0-9]+\\s*)*\\]\\s*->" +
			"\\s*[a-z0-9]+\\s*(\\#\\s*\\d+(\\.\\d+)?\\s*)*$";

	public static WTA parse(String fileName) {

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

	private static void parseLine(String line, WTA wta)
			throws IllegalArgumentException {

		if (line.matches(emptyLineRegExp)) {
			// Ignore empty lines.
		} else if (line.matches(finalRegExp)) {
			parseFinal(line, wta);
		} else if (line.matches(leafRuleRegExp)) {
			parseLeafRule(line, wta);
		} else if (line.matches(ruleRegexp)) {
			parseRule(line, wta);
		} else {
			throw new IllegalArgumentException("Line " + line +
					" was not correct.");
		}
	}

	private static void parseFinal(String line, WTA wta) {

	}

	private static void parseLeafRule(String line, WTA wta) {

	}

	private static void parseRule(String line, WTA wta) {

	}

}
