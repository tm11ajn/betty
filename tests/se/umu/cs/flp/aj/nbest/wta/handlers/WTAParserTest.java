package se.umu.cs.flp.aj.nbest.wta.handlers;

import static org.junit.Assert.*;

import org.junit.Test;

import se.umu.cs.flp.aj.nbest.semiring.TropicalWeight;
import se.umu.cs.flp.aj.nbest.wta.Symbol;
import se.umu.cs.flp.aj.nbest.wta.WTA;
import se.umu.cs.flp.aj.nbest.wta.exceptions.DuplicateRuleException;
import se.umu.cs.flp.aj.nbest.wta.exceptions.SymbolUsageException;
import se.umu.cs.flp.aj.nbest.wta.handlers.WTAParser;

public class WTAParserTest {
	public static final String fileName = "wta_examples/wta0.rtg";

	public static final String emptyLine = "		    			";
	public static final String commentLine = "//a -> q1";
	public static final String finalLine = "	  final    q0,   q1, q2	";
	public static final String leafRuleLine = " A   -> q0 ";
	public static final String leafRuleLineWithWeight = " a   -> q0  #  2 ";
	public static final String nonLeafRuleLine = "  f[q0, q1] ->   qf ";
	public static final String nonLeafRuleLineWithWeight =
			"  f[q0, q1] ->   qf  #  0.2 ";

	private WTAParser wtaParser = new WTAParser();
	private WTA wta = new WTA();

	private Symbol ASymb = new Symbol("A", 0);
	private Symbol aSymb = new Symbol("a", 0);
	private Symbol fSymb = new Symbol("f", 2);

	/**
	 * Tests that states are properly set to final when parsing a final rule.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 * @throws DuplicateRuleException 
	 */
	@Test
	public void shouldParseFinalLine()
			throws IllegalArgumentException, SymbolUsageException, 
			DuplicateRuleException {
		wtaParser.parseLine(finalLine, wta);
		wtaParser.parseLine(leafRuleLine, wta);
		wtaParser.parseLine(finalLine, wta);
		System.out.println(wta);
		assertTrue(wta.addState("q0").isFinal() 
				&& wta.addState("q1").isFinal() 
				&& wta.addState("q2").isFinal());
	}

	/**
	 * Tests that a leaf rule is parsed properly.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 * @throws DuplicateRuleException 
	 */
	@Test
	public void shouldParseLeafRuleLine()
			throws IllegalArgumentException, SymbolUsageException, 
			DuplicateRuleException {
		wtaParser.parseLine(leafRuleLine, wta);
		assertEquals("q0", wta.getTransitionFunction().getRulesBySymbol(ASymb).
				get(0).getResultingState().getLabel());
	}

	/**
	 * Tests that a leaf rule is added properly to an empty wta.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 * @throws DuplicateRuleException 
	 */
	@Test
	public void shouldParseLeafRuleLineLength()
			throws IllegalArgumentException, SymbolUsageException, 
			DuplicateRuleException {
		wtaParser.parseLine(leafRuleLine, wta);
		assertEquals(1, wta.getTransitionFunction().
				getRulesBySymbol(ASymb).size());
	}

	/**
	 * Tests that multiple rules are not added.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 * @throws DuplicateRuleException 
	 */
	@Test(expected=DuplicateRuleException.class)
	public void shouldParseLeafRuleLineLengthNoMultiple()
			throws IllegalArgumentException, SymbolUsageException, 
			DuplicateRuleException {
		wtaParser.parseLine(leafRuleLine, wta);
		wtaParser.parseLine(leafRuleLine, wta);
		assertEquals(1, wta.getTransitionFunction().
				getRulesBySymbol(aSymb).size());
	}

	/**
	 * Tests that a leaf rule with weight is parsed properly.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 * @throws DuplicateRuleException 
	 */
	@Test
	public void shouldParseLeafRuleLineWithWeight()
			throws IllegalArgumentException, SymbolUsageException, 
			DuplicateRuleException {
		wtaParser.parseLine(leafRuleLineWithWeight, wta);
		assertEquals(0, wta.getTransitionFunction().getRulesBySymbol(aSymb).
				get(0).getWeight().compareTo(new TropicalWeight(2)));
	}

	/**
	 * Tests that a leaf rule with weight is added properly to an empty wta.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 * @throws DuplicateRuleException 
	 */
	@Test
	public void shouldParseLeafRuleWithWeightLineLength()
			throws IllegalArgumentException, SymbolUsageException, 
			DuplicateRuleException {
		wtaParser.parseLine(leafRuleLineWithWeight, wta);
		assertEquals(1, wta.getTransitionFunction().
				getRulesBySymbol(aSymb).size());
	}

	/**
	 * Tests that a non-leaf rule is parsed properly.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 * @throws DuplicateRuleException 
	 */
	@Test
	public void shouldParseNonLeafRuleLine()
			throws IllegalArgumentException, SymbolUsageException, 
			DuplicateRuleException {
		wtaParser.parseLine(nonLeafRuleLine, wta);
		assertEquals("qf", wta.getTransitionFunction().getRulesBySymbol(fSymb).
				get(0).getResultingState().getLabel());
	}

	/**
	 * Tests that a non-leaf rule with weight is parsed properly.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 * @throws DuplicateRuleException 
	 */
	@Test
	public void shouldParseNonLeafRuleLineWithWeight()
			throws IllegalArgumentException, SymbolUsageException, 
			DuplicateRuleException {
		wtaParser.parseLine(nonLeafRuleLineWithWeight, wta);
		assertEquals(0, wta.getTransitionFunction().getRulesBySymbol(fSymb).
				get(0).getWeight().compareTo(new TropicalWeight(0.2)));
	}


	/**
	 * Tests that a non-leaf rule is added properly to an empty wta.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 * @throws DuplicateRuleException 
	 */
	@Test
	public void shouldParseNonLeafRuleLineLength()
			throws IllegalArgumentException, SymbolUsageException, 
			DuplicateRuleException {
		wtaParser.parseLine(nonLeafRuleLine, wta);
		assertEquals(1, wta.getTransitionFunction().
				getRulesBySymbol(fSymb).size());
	}

	/**
	 * Tests that a non-leaf rule with weight is added properly to an empty wta.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 * @throws DuplicateRuleException 
	 */
	@Test
	public void shouldParseNonLeafRuleLineWithWeightLength()
			throws IllegalArgumentException, SymbolUsageException, 
			DuplicateRuleException {
		wtaParser.parseLine(nonLeafRuleLineWithWeight, wta);
		assertEquals(1, wta.getTransitionFunction().
				getRulesBySymbol(fSymb).size());
	}

	/**
	 * Tests that a symbol can be used with same rank as previously.
	 * @throws Exception
	 */
	@Test
	public void shouldAllowForSymbolToBeUsedWithSameRank()
			throws Exception {
		wtaParser.parseLine(nonLeafRuleLineWithWeight, wta);
		wtaParser.parseLine("f[q1, q0] -> qf", wta);
		assertEquals(2, wta.getTransitionFunction().
				getRulesBySymbol(fSymb).size());
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
		assertNotNull(wta.getTransitionFunction().getRulesBySymbol(fSymb));
	}
	
	@Test
	public void shouldParseCorrectFile() throws Exception {
		wta = wtaParser.parse(fileName);
		assertEquals(wta.toString(), 
				"States: pa pb qb qa \n"
				+ "Ranked alphabet: ball(2) b(0) a(0) \n"
				+ "Transition function: \n"
				+ "a -> pa # 2.0\n"
				+ "a -> pb # 1.0\n"
				+ "ball[pa, pa] -> qa\n"
				+ "ball[qa, qa] -> qa\n"
				+ "ball[pb, pb] -> qb\n"
				+ "ball[qb, qb] -> qb\n"
				+ "ball[pa, qa] -> pa\n"
				+ "ball[qa, pa] -> pa\n"
				+ "ball[pb, qb] -> pb\n"
				+ "ball[qb, pb] -> pb\n"
				+ "b -> pa # 1.0\n"
				+ "b -> pb # 2.0\n"
				+ "Final states: qa qb ");
	}

}
