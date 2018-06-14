package se.umu.cs.flp.aj.nbest.wta.handlers;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;

import org.junit.Test;

import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.semiring.TropicalSemiring;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
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
	public static final String finalLine2 = "	  final    qf 	";
	public static final String leafRuleLine = " A   -> q0 ";
	public static final String leafRuleLineWithWeight = " a   -> q0  #  2 ";
	public static final String nonLeafRuleLine = "  f[q0, q1] ->   qf ";
	public static final String nonLeafRuleLineWithWeight =
			"  f[q0, q1] ->   qf  #  0.2 ";
	private Semiring semiring = new TropicalSemiring();

	private WTAParser wtaParser = new WTAParser(semiring);
	private WTA wta = new WTA(semiring);

	private Symbol ASymb = new Symbol("A", 0);
	private Symbol aSymb = new Symbol("a", 0);
	private Symbol bSymb = new Symbol("b", 0);
	private Symbol fSymb = new Symbol("f", 2);

	private State q0 = new State("q0");
	private State q1 = new State("q1");
	private State qf = new State("qf");

	private State pa = new State("pa");
	private State pb = new State("pb");

	private Weight w0 = new TropicalSemiring().one();
	private Weight w1 = new TropicalSemiring().createWeight(1);
	private Weight w2 = new TropicalSemiring().createWeight(2);
	private Weight w02 = new TropicalSemiring().createWeight(0.2);

	Rule<Symbol> leafRule = new Rule<Symbol>(ASymb, w0, q0);
	Rule<Symbol> leafRuleWithWeight = new Rule<Symbol>(aSymb, w2, q0);
	Rule<Symbol> nonLeafRule = new Rule<Symbol>(fSymb, w0, qf, q0, q1);
	Rule<Symbol> nonLeafRuleWithWeight = new Rule<Symbol>(fSymb, w02,
			qf, q0, q1);

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
		System.out.println(wta);
		assertTrue(wta.addState("q0").isFinal()
				&& wta.addState("q1").isFinal()
				&& wta.addState("q2").isFinal());
	}

	/**
	 * Tests that the same final state is not added multiple times.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 * @throws DuplicateRuleException
	 */
	public void shouldNotParseFinalLineTwice()
			throws IllegalArgumentException, SymbolUsageException,
			DuplicateRuleException {
		wtaParser.parseLine(finalLine, wta);
		wtaParser.parseLine(finalLine, wta);
		assertThat(wta.getFinalStates().size(), is(3));
	}

	/**
	 * Tests that states are properly set to final when parsing a final rule.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 * @throws DuplicateRuleException
	 */
	@Test
	public void shouldParseFinalLineLength()
			throws IllegalArgumentException, SymbolUsageException,
			DuplicateRuleException {
		wtaParser.parseLine(finalLine, wta);
		wtaParser.parseLine(leafRuleLine, wta);
		System.out.println(wta);
		assertThat(wta.getFinalStates().size(), is(3));
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
		assertThat(wta.getRulesByResultingState(q0).get(0), is(leafRule));
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
		assertThat(wta.getRulesByResultingState(q0).size(), is(1));
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
		assertThat(wta.getRulesByResultingState(q0).get(0),
				is(leafRuleWithWeight));
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
		assertThat(wta.getRulesByResultingState(q0).size(), is(1));
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
		assertThat(wta.getRulesByResultingState(qf).get(0), is(nonLeafRule));
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
		assertThat(wta.getRulesByResultingState(qf).get(0),
				is(nonLeafRuleWithWeight));
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
		assertThat(wta.getRulesByResultingState(qf).size(), is(1));
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
		assertThat(wta.getRulesByResultingState(qf).size(), is(1));
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
		assertThat(wta.getRulesByResultingState(qf).size(), is(2));
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
		assertNotNull(wta.getRulesByResultingState(qf));
	}

	@Test
	public void shouldParseCorrectFile0() throws Exception {
		wta = wtaParser.parse(fileName);
		assertThat(wta.getStates().size(), is(4));
	}

	@Test
	public void shouldParseCorrectFile1() throws Exception {
		wta = wtaParser.parse(fileName);
		assertThat(wta.getFinalStates().size(), is(2));
	}

	@Test
	public void shouldParseCorrectFile2() throws Exception {
		wta = wtaParser.parse(fileName);
		assertThat(wta.getSymbols().size(), is(3));
	}

	@Test
	public void shouldParseCorrectFile3() throws Exception {
		wta = wtaParser.parse(fileName);
		assertThat(wta.getRulesByResultingState(new State("pa")).size(), is(4));
	}

	@Test
	public void shouldParseCorrectFile4() throws Exception {
		wta = wtaParser.parse(fileName);
		assertThat(wta.getRulesByResultingState(new State("pb")).size(), is(4));
	}

	@Test
	public void shouldParseCorrectFile5() throws Exception {
		wta = wtaParser.parse(fileName);
		assertThat(wta.getRulesByResultingState(new State("qa")).size(), is(2));
	}

	@Test
	public void shouldParseCorrectFile6() throws Exception {
		wta = wtaParser.parse(fileName);
		assertThat(wta.getRulesByResultingState(new State("qb")).size(), is(2));
	}

	@Test
	public void shouldParseCorrectFile7() throws Exception {
		wta = wtaParser.parse(fileName);
		assertThat(wta.getStates().size(), is(4));
	}

	@Test
	public void shouldParseCorrectFile8() throws Exception {
		wta = wtaParser.parse(fileName);
		ArrayList<Rule<Symbol>> list = new ArrayList<>();
		list.add(new Rule<Symbol>(aSymb, w1, pb));
		list.add(new Rule<Symbol>(aSymb, w2, pa));
		list.add(new Rule<Symbol>(bSymb, w2, pb));
		list.add(new Rule<Symbol>(bSymb, w1, pa));
		assertThat(wta.getSourceRules().size(), is(4));
	}

//	@Test
//	public void shouldParseCorrectFile() throws Exception {
//		wta = wtaParser.parse(fileName);
//		assertEquals(wta.toString(),
//				"States: pa pb qb qa \n"
//				+ "Ranked alphabet: ball(2) b(0) a(0) \n"
//				+ "Transition function: \n"
//				+ "a -> pa # 2.0\n"
//				+ "a -> pb # 1.0\n"
//				+ "ball[pa, pa] -> qa\n"
//				+ "ball[qa, qa] -> qa\n"
//				+ "ball[pb, pb] -> qb\n"
//				+ "ball[qb, qb] -> qb\n"
//				+ "ball[pa, qa] -> pa\n"
//				+ "ball[qa, pa] -> pa\n"
//				+ "ball[pb, qb] -> pb\n"
//				+ "ball[qb, pb] -> pb\n"
//				+ "b -> pa # 1.0\n"
//				+ "b -> pb # 2.0\n"
//				+ "Final states: qa qb ");
//	}

}
