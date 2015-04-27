package se.umu.cs.nfl.aj.wta;

import static org.junit.Assert.*;

import org.junit.Test;

public class WTAParserRegexTest {

	@Test
	public void emptyLineRegExpTest() {
		assertTrue(WTAParserTest.emptyLine.matches(WTAParser.EMPTY_LINE_REGEX));
	}

	@Test
	public void finalLineRegExpTest() {
		assertTrue(WTAParserTest.finalLine.matches(WTAParser.FINAL_REGEX));
	}

	@Test
	public void leafRuleLineRegExpTest() {
		assertTrue(WTAParserTest.leafRuleLine.
				matches(WTAParser.LEAF_RULE_REGEX));
	}

	@Test
	public void leafRuleLineWithWeightRegExpTest() {
		assertTrue(WTAParserTest.leafRuleLineWithWeight.
				matches(WTAParser.LEAF_RULE_REGEX));
	}

	@Test
	public void ruleLineRegExpTest() {
		assertTrue(WTAParserTest.nonLeafRuleLine.
				matches(WTAParser.NON_LEAF_RULE_REGEX));
	}

	@Test
	public void ruleLineWithWeightRegExpTest() {
		assertTrue(WTAParserTest.nonLeafRuleLineWithWeight.
				matches(WTAParser.NON_LEAF_RULE_REGEX));
	}

}
