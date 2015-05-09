package se.umu.cs.nfl.aj.wta;

import static org.junit.Assert.*;

import org.junit.Test;

public class WTAParserRegexTest {

	@Test
	public void shouldFindEmptyLineRegex() {
		assertTrue(WTAParserTest.emptyLine.matches(WTAParser.EMPTY_LINE_REGEX));
	}

	@Test
	public void shouldFindFinalLineRegex() {
		assertTrue(WTAParserTest.finalLine.matches(WTAParser.FINAL_REGEX));
	}

	@Test
	public void shouldFindLeafRuleLineRegex() {
		assertTrue(WTAParserTest.leafRuleLine.
				matches(WTAParser.LEAF_RULE_REGEX));
	}

	@Test
	public void shouldFindLeafRuleLineWithWeightRegex() {
		assertTrue(WTAParserTest.leafRuleLineWithWeight.
				matches(WTAParser.LEAF_RULE_REGEX));
	}

	@Test
	public void shouldFindRuleLineRegex() {
		assertTrue(WTAParserTest.nonLeafRuleLine.
				matches(WTAParser.NON_LEAF_RULE_REGEX));
	}

	@Test
	public void shouldFindRuleLineWithWeightRegex() {
		assertTrue(WTAParserTest.nonLeafRuleLineWithWeight.
				matches(WTAParser.NON_LEAF_RULE_REGEX));
	}

	@Test
	public void shouldSplitFinalLineRegex() throws Exception {
		//assertTrue(WTAParser.FINAL_SPLIT_REGEX);
		fail("Not implemented");
	}

}
