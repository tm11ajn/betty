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

	private Symbol aSymb = new Symbol("a", 0);
	private Symbol fSymb = new Symbol("f", 2);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests that states are properly set to final when parsing a final rule.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 */
	@Test
	public void shouldParseFinalLine()
			throws IllegalArgumentException, SymbolUsageException {
		wtaParser.parseLine(finalLine, wta);
		assertTrue(wta.addState("q0").isFinal());
	}

	/**
	 * Tests that a leaf rule is parsed properly.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 */
	@Test
	public void shouldParseLeafRuleLine()
			throws IllegalArgumentException, SymbolUsageException {
		wtaParser.parseLine(leafRuleLine, wta);
		assertEquals("q0", wta.getRulesBySymbol(aSymb).
				get(0).getResultingState().getLabel());
	}

	/**
	 * Tests that a leaf rule is added properly to an empty wta.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 */
	@Test
	public void shouldParseLeafRuleLineLength()
			throws IllegalArgumentException, SymbolUsageException {
		wtaParser.parseLine(leafRuleLine, wta);
		assertEquals(1, wta.getRulesBySymbol(aSymb).size());
	}

	/**
	 * Tests that multiple rules are not added.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 */
	@Test
	public void shouldParseLeafRuleLineLengthNoMultiple()
			throws IllegalArgumentException, SymbolUsageException {
		wtaParser.parseLine(leafRuleLine, wta);
		wtaParser.parseLine(leafRuleLine, wta);
		assertEquals(1, wta.getRulesBySymbol(aSymb).size());
	}

	/**
	 * Tests that a leaf rule with weight is parsed properly.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 */
	@Test
	public void shouldParseLeafRuleLineWithWeight()
			throws IllegalArgumentException, SymbolUsageException {
		wtaParser.parseLine(leafRuleLineWithWeight, wta);
		assertEquals(2, wta.getRulesBySymbol(aSymb).
				get(0).getWeight(), 10e-5);
	}

	/**
	 * Tests that a leaf rule with weight is added properly to an empty wta.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 */
	@Test
	public void shouldParseLeafRuleWithWeightLineLength()
			throws IllegalArgumentException, SymbolUsageException {
		wtaParser.parseLine(leafRuleLineWithWeight, wta);
		assertEquals(1, wta.getRulesBySymbol(aSymb).size());
	}

	/**
	 * Tests that a non-leaf rule is parsed properly.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 */
	@Test
	public void shouldParseNonLeafRuleLine()
			throws IllegalArgumentException, SymbolUsageException {
		wtaParser.parseLine(nonLeafRuleLine, wta);
		assertEquals("qf", wta.getRulesBySymbol(fSymb).
				get(0).getResultingState().getLabel());
	}

	/**
	 * Tests that a non-leaf rule with weight is parsed properly.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 */
	@Test
	public void shouldParseNonLeafRuleLineWithWeight()
			throws IllegalArgumentException, SymbolUsageException {
		wtaParser.parseLine(nonLeafRuleLineWithWeight, wta);
		assertEquals(0.2, wta.getRulesBySymbol(fSymb).get(0).getWeight(), 10e-5);
	}


	/**
	 * Tests that a non-leaf rule is added properly to an empty wta.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 */
	@Test
	public void shouldParseNonLeafRuleLineLength()
			throws IllegalArgumentException, SymbolUsageException {
		wtaParser.parseLine(nonLeafRuleLine, wta);
		assertEquals(1, wta.getRulesBySymbol(fSymb).size());
	}

	/**
	 * Tests that a non-leaf rule with weight is added properly to an empty wta.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 */
	@Test
	public void shouldParseNonLeafRuleLineWithWeightLength()
			throws IllegalArgumentException, SymbolUsageException {
		wtaParser.parseLine(nonLeafRuleLineWithWeight, wta);
		assertEquals(1, wta.getRulesBySymbol(fSymb).size());
	}

	/**
	 * Tests that a symbol can be used with same rank as previously.
	 * @throws Exception
	 */
	@Test
	public void shouldAllowForSymbolToBeUsedWithSameRank()
			throws Exception {
		wtaParser.parseLine(nonLeafRuleLineWithWeight, wta);
		wtaParser.parseLine(nonLeafRuleLine, wta);
		assertEquals(2, wta.getRulesBySymbol(fSymb).size());
	}

	/**
	 * Tests that a symbol cannot be used with different ranks.
	 * @throws Exception
	 */
	@Test(expected=SymbolUsageException.class)
	public void shouldNotAllowForSymbolToBeUsedWithDifferentRanks()
			throws Exception {
		wtaParser.parseLine(nonLeafRuleLineWithWeight, wta);
		wtaParser.parseLine("f[q0, q1, q2] -> qf", wta);
	}

	/**
	 * Test for a bug that occurred the rules to be null.
	 * @throws Exception
	 */
	@Test
	public void shouldNotBeNullWhenCollectingParsed() throws Exception {
		wtaParser.parseLine(nonLeafRuleLineWithWeight, wta);
		assertNotNull(wta.getRulesBySymbol(fSymb));
	}

}
