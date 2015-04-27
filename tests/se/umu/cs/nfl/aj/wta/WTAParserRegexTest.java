package se.umu.cs.nfl.aj.wta;

import static org.junit.Assert.*;

import org.junit.Test;

public class WTAParserRegexTest {

	@Test
	public void shouldFindEmptyLineRegexTest() {
		assertTrue(WTAParserTest.emptyLine.matches(WTAParser.EMPTY_LINE_REGEX));
	}

	@Test
	public void shouldFindFinalLineRegexTest() {
		assertTrue(WTAParserTest.finalLine.matches(WTAParser.FINAL_REGEX));
	}

	@Test
	public void shouldFindLeafRuleLineRegexTest() {
		assertTrue(WTAParserTest.leafRuleLine.
				matches(WTAParser.LEAF_RULE_REGEX));
	}

	@Test
	public void shouldFindLeafRuleLineWithWeightRegexTest() {
		assertTrue(WTAParserTest.leafRuleLineWithWeight.
				matches(WTAParser.LEAF_RULE_REGEX));
	}

	@Test
	public void shouldFindRuleLineRegexTest() {
		assertTrue(WTAParserTest.nonLeafRuleLine.
				matches(WTAParser.NON_LEAF_RULE_REGEX));
	}

	@Test
	public void shouldFindRuleLineWithWeightRegexTest() {
		assertTrue(WTAParserTest.nonLeafRuleLineWithWeight.
				matches(WTAParser.NON_LEAF_RULE_REGEX));
	}

	@Test
	public void shouldSplitFinalLineRegexTest() throws Exception {
		//assertTrue(WTAParser.FINAL_SPLIT_REGEX);
		fail("Not implemented");
	}

}
