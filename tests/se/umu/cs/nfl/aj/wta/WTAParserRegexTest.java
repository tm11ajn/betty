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
		assertEquals(4, WTAParserTest.finalLine.trim().split(
				WTAParser.FINAL_SPLIT_REGEX).length);
	}

	@Test
	public void shouldSplitLeafRuleLineRegex() throws Exception {
		assertEquals(2, WTAParserTest.leafRuleLine.trim().split(
				WTAParser.LEAF_RULE_SPLIT_REGEX).length);
	}

	@Test
	public void shouldSplitLeafRuleLineWithWeightRegex() throws Exception {
		assertEquals(3, WTAParserTest.leafRuleLineWithWeight.trim().split(
				WTAParser.LEAF_RULE_SPLIT_REGEX).length);
	}

	@Test
	public void shouldSplitNonLeafRuleLineRegex() throws Exception {
		assertEquals(4, WTAParserTest.nonLeafRuleLine.trim().split(
				WTAParser.NON_LEAF_RULE_SPLIT_REGEX).length);
	}

	@Test
	public void shouldSplitNonLeafRuleLineWithWeightRegex() throws Exception {
		assertEquals(5, WTAParserTest.nonLeafRuleLineWithWeight.trim().split(
				WTAParser.NON_LEAF_RULE_SPLIT_REGEX).length);
	}

}
