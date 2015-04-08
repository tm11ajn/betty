package se.umu.cs.nfl.aj.wta;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WTAParserTest {

	private String emptyLine = "		    			";
	private String finalLine = "	  final    q0   q1 q2	";
	private String leafRuleLine = " a   -> q0 ";
	private String ruleLine = "  a[q0, q1] ->   qf ";

	private WTA wta = new WTA();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void emptyLineRegExpTest() {
		assertTrue(emptyLine.matches(WTAParser.emptyLineRegExp));
	}

	@Test
	public void finalLineRegExpTest() {
		assertTrue(finalLine.matches(WTAParser.finalRegExp));
	}

	@Test
	public void leafRuleLineRegExpTest() {
		assertTrue(leafRuleLine.matches(WTAParser.leafRuleRegExp));
	}

	@Test
	public void ruleLineRegExpTest() {
		assertTrue(ruleLine.matches(WTAParser.ruleRegexp));
	}

	/**
	 * Tests that states are properly set to final when parsing a
	 * final rule.
	 */
	@Test
	public void parseFinalLineTest() {
		WTAParser.parseLine(finalLine, wta);
		assertTrue(wta.getState("q0").isFinal());
	}

	/**
	 * Tests that a leaf rule is parsed properly.
	 */
	@Test
	public void parseLeafRuleLineTest() {
		WTAParser.parseLine(leafRuleLine, wta);
		assertEquals("q0", wta.getRulesBySymbol("a").
				get(0).getResultingState().getLabel());
	}

	/**
	 * Tests that a leaf rule is added properly to an empty wta.
	 */
	@Test
	public void parseLeafRuleLineLengthTest() {
		WTAParser.parseLine(leafRuleLine, wta);
		assertEquals(1, wta.getRulesBySymbol("a").size());
	}

	/**
	 * Tests that multiple rules are not added.
	 */
	@Test
	public void parseLeafRuleLineLengthTest2() {
		WTAParser.parseLine(leafRuleLine, wta);
		WTAParser.parseLine(leafRuleLine, wta);
		assertEquals(1, wta.getRulesBySymbol("a").size());
	}

}
