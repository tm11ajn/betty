package se.umu.cs.nfl.aj.wta;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WTAParserTest {

	public static final String emptyLine = "		    			";
	public static final String finalLine = "	  final    q0   q1 q2	";
	public static final String leafRuleLine = " a   -> q0 ";
	public static final String leafRuleLineWithWeight = " a   -> q0  #  2 ";
	public static final String nonLeafRuleLine = "  f[q0, q1] ->   qf ";
	public static final String nonLeafRuleLineWithWeight =
			"  f[q0, q1] ->   qf  #  0.2 ";

	private WTAParser wtaParser = new WTAParser();
	private WTA wta = new WTA();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests that states are properly set to final when parsing a final rule.
	 */
	@Test
	public void parseFinalLineTest() {
		wtaParser.parseLine(finalLine, wta);
		assertTrue(wta.getState("q0").isFinal());
	}

	/**
	 * Tests that a leaf rule is parsed properly.
	 */
	@Test
	public void parseLeafRuleLineTest() {
		wtaParser.parseLine(leafRuleLine, wta);
		assertEquals("q0", wta.getRulesBySymbol("a").
				get(0).getResultingState().getLabel());
	}

	/**
	 * Tests that a leaf rule is added properly to an empty wta.
	 */
	@Test
	public void parseLeafRuleLineLengthTest() {
		wtaParser.parseLine(leafRuleLine, wta);
		assertEquals(1, wta.getRulesBySymbol("a").size());
	}

	/**
	 * Tests that multiple rules are not added.
	 */
	@Test
	public void parseLeafRuleLineLengthTest2() {
		wtaParser.parseLine(leafRuleLine, wta);
		wtaParser.parseLine(leafRuleLine, wta);
		assertEquals(1, wta.getRulesBySymbol("a").size());
	}

	/**
	 * Tests that a leaf rule with weight is parsed properly.
	 */
	@Test
	public void parseLeafRuleLineWithWeightTest() {
		wtaParser.parseLine(leafRuleLineWithWeight, wta);
		assertEquals(2, wta.getRulesBySymbol("a").
				get(0).getWeight(), 10e-5);
	}

	/**
	 * Tests that a leaf rule with weight is added properly to an empty wta.
	 */
	@Test
	public void parseLeafRuleWithWeightLineLengthTest() {
		wtaParser.parseLine(leafRuleLineWithWeight, wta);
		assertEquals(1, wta.getRulesBySymbol("a").size());
	}

	/**
	 * Tests that a non-leaf rule is parsed properly.
	 */
	@Test
	public void parseNonLeafRuleLineTest() {
		wtaParser.parseLine(nonLeafRuleLine, wta);
		assertEquals("q0", wta.getRulesBySymbol("f").
				get(0).getResultingState().getLabel());
	}

	/**
	 * Tests that a non-leaf rule with weight is parsed properly.
	 */
	@Test
	public void parseNonLeafRuleLineWithWeightTest() {
		wtaParser.parseLine(nonLeafRuleLineWithWeight, wta);
		assertEquals(0.2, wta.getRulesBySymbol("f").get(0).getWeight(), 10e-5);
	}

	/**
	 * Tests that a non-leaf rule is added properly to an empty wta.
	 */
	@Test
	public void parseNonLeafRuleLineLengthTest() {
		wtaParser.parseLine(nonLeafRuleLine, wta);
		assertEquals(1, wta.getRulesBySymbol("f").size());
	}

	/**
	 * Tests that a non-leaf rule with weight is added properly to an empty wta.
	 */
	@Test
	public void parseNonLeafRuleLineWithWeightLengthTest() {
		wtaParser.parseLine(nonLeafRuleLineWithWeight, wta);
		assertEquals(1, wta.getRulesBySymbol("f").size());
	}

}
