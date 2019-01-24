package se.umu.cs.flp.aj.nbest.wta.handlers;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;

import org.junit.Test;

import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.semiring.TropicalSemiring;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.Node;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.Symbol;
import se.umu.cs.flp.aj.nbest.wta.WTA;
import se.umu.cs.flp.aj.nbest.wta.exceptions.DuplicateRuleException;
import se.umu.cs.flp.aj.nbest.wta.exceptions.SymbolUsageException;
import se.umu.cs.flp.aj.nbest.wta.parsers.WTAParser;

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
	public static final String nonLeafRuleLine2 = "  g[q0] ->   qf ";
	public static final String nonLeafRuleLineWithWeight2 =
			"  g[q0] ->   qf  #  0.2 ";
	public static final String mtDataTestRuleLine = "nn-hd-dat.sg.masc[qtunfischfang] -> qnn-hd-dat.sg.masc";

	private Semiring semiring = new TropicalSemiring();

	private WTAParser wtaParser = new WTAParser(semiring);
	private WTA wta = new WTA(semiring);

	private Symbol ASymb = new Symbol("A", 0);
	private Symbol aSymb = new Symbol("a", 0);
	private Symbol bSymb = new Symbol("b", 0);
	private Symbol gSymb = new Symbol("g", 1);
	private Symbol fSymb = new Symbol("f", 2);
	private Symbol mtSymb = new Symbol("nn-hd-dat.sg.masc", 1);

	private State q0 = new State(new Symbol("q0", 0));
	private State q1 = new State(new Symbol("q1", 0));
	private State qf = new State(new Symbol("qf", 0));

	private State pa = new State(new Symbol("pa", 0));
	private State pb = new State(new Symbol("pb", 0));

	private State mtq0 = new State(new Symbol("qtunfischfang", 0));
	private State mtq1 = new State(new Symbol("qnn-hd-dat.sg.masc", 0));

	private Weight w0 = new TropicalSemiring().one();
	private Weight w1 = new TropicalSemiring().createWeight(1);
	private Weight w2 = new TropicalSemiring().createWeight(2);
	private Weight w02 = new TropicalSemiring().createWeight(0.2);

	Rule leafRule = new Rule(new Node(ASymb), w0, q0);
	Rule leafRuleWithWeight = new Rule(new Node(aSymb), w2, q0);
	Rule nonLeafRule = new Rule(new Node(fSymb), w0, qf, q0, q1);
	Rule nonLeafRuleWithWeight = new Rule(new Node(fSymb), w02,
			qf, q0, q1);
	Rule nonLeafRule2 = new Rule(new Node(gSymb), w0, qf, q0);
	Rule nonLeafRuleWithWeight2 = new Rule(new Node(gSymb), w02,
			qf, q0);
	Rule mtDataTestRule = new Rule(new Node(mtSymb), w0, mtq1, mtq0);

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
		wtaParser.parseLine(finalLine);
		wtaParser.parseLine(leafRuleLine);
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
		wtaParser.parseLine(finalLine);
		wtaParser.parseLine(finalLine);
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
		wtaParser.parseLine(finalLine);
		wtaParser.parseLine(leafRuleLine);
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
		wtaParser.parseLine(leafRuleLine);
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
		wtaParser.parseLine(leafRuleLine);
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
		wtaParser.parseLine(leafRuleLine);
		wtaParser.parseLine(leafRuleLine);
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
		wtaParser.parseLine(leafRuleLineWithWeight);
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
		wtaParser.parseLine(leafRuleLineWithWeight);
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
		wtaParser.parseLine(nonLeafRuleLine);
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
		wtaParser.parseLine(nonLeafRuleLineWithWeight);
		assertThat(wta.getRulesByResultingState(qf).get(0),
				is(nonLeafRuleWithWeight));
	}

	/**
	 * Tests that a non-leaf rule with weight is parsed properly.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 * @throws DuplicateRuleException
	 */
	@Test
	public void shouldParseNonLeafRuleLineWithWeightWithCorrectRank()
			throws IllegalArgumentException, SymbolUsageException,
			DuplicateRuleException {
		wtaParser.parseLine(nonLeafRuleLineWithWeight);
		assertThat(wta.getRulesByResultingState(qf).get(0).getRank(),
				is(2));
	}

	/**
	 * Tests that a non-leaf rule is parsed properly.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 * @throws DuplicateRuleException
	 */
	@Test
	public void shouldParseNonLeafRuleLine2()
			throws IllegalArgumentException, SymbolUsageException,
			DuplicateRuleException {
		wtaParser.parseLine(nonLeafRuleLine2);
		assertThat(wta.getRulesByResultingState(qf).get(0), is(nonLeafRule2));
	}

	/**
	 * Tests that a non-leaf rule with weight is parsed properly.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 * @throws DuplicateRuleException
	 */
	@Test
	public void shouldParseNonLeafRuleLineWithWeight2()
			throws IllegalArgumentException, SymbolUsageException,
			DuplicateRuleException {
		wtaParser.parseLine(nonLeafRuleLineWithWeight2);
		assertThat(wta.getRulesByResultingState(qf).get(0),
				is(nonLeafRuleWithWeight2));
	}

	/**
	 * Tests that a non-leaf rule with weight is parsed properly.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 * @throws DuplicateRuleException
	 */
	@Test
	public void shouldParseNonLeafRuleLineWithWeightWithCorrectRank2()
			throws IllegalArgumentException, SymbolUsageException,
			DuplicateRuleException {
		wtaParser.parseLine(nonLeafRuleLineWithWeight2);
		assertThat(wta.getRulesByResultingState(qf).get(0).getRank(),
				is(1));
	}

	/**
	 * Tests that a non-leaf rule is parsed properly.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 * @throws DuplicateRuleException
	 */
	@Test
	public void shouldParseMTDataRuleLine()
			throws IllegalArgumentException, SymbolUsageException,
			DuplicateRuleException {
		wtaParser.parseLine(mtDataTestRuleLine);
		assertThat(wta.getRulesByResultingState(mtq1).get(0), is(mtDataTestRule));
	}

	/**
	 * Tests that a non-leaf rule is parsed properly.
	 * @throws SymbolUsageException
	 * @throws IllegalArgumentException
	 * @throws DuplicateRuleException
	 */
	@Test
	public void shouldParseMTDataRuleLineWithCorrectRank()
			throws IllegalArgumentException, SymbolUsageException,
			DuplicateRuleException {
		wtaParser.parseLine(mtDataTestRuleLine);
		assertThat(wta.getRulesByResultingState(mtq1).get(0).getRank(), is(1));
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
		wtaParser.parseLine(nonLeafRuleLine);
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
		wtaParser.parseLine(nonLeafRuleLineWithWeight);
		assertThat(wta.getRulesByResultingState(qf).size(), is(1));
	}

	/**
	 * Tests that a symbol can be used with same rank as previously.
	 * @throws Exception
	 */
	@Test
	public void shouldAllowForSymbolToBeUsedWithSameRank()
			throws Exception {
		wtaParser.parseLine(nonLeafRuleLineWithWeight);
		wtaParser.parseLine("f[q1, q0] -> qf");
		assertThat(wta.getRulesByResultingState(qf).size(), is(2));
	}

	/**
	 * Tests that a symbol cannot be used with different ranks.
	 * @throws Exception
	 */
	@Test(expected=SymbolUsageException.class)
	public void shouldNotAllowForSymbolToBeUsedWithDifferentRanks()
			throws Exception {
		wtaParser.parseLine(nonLeafRuleLineWithWeight);
		wtaParser.parseLine("f[q0, q1, q2] -> qf");
	}

	/**
	 * Test for a bug that occurred the rules to be null.
	 * @throws Exception
	 */
	@Test
	public void shouldNotBeNullWhenCollectingParsed() throws Exception {
		wtaParser.parseLine(nonLeafRuleLineWithWeight);
		assertNotNull(wta.getRulesByResultingState(qf));
	}

	@Test
	public void shouldParseCorrectFile0() throws Exception {
		wta = wtaParser.parseForBestTrees(fileName);
		assertThat(wta.getStates().size(), is(4));
	}

	@Test
	public void shouldParseCorrectFile1() throws Exception {
		wta = wtaParser.parseForBestTrees(fileName);
		assertThat(wta.getFinalStates().size(), is(2));
	}

	@Test
	public void shouldParseCorrectFile2() throws Exception {
		wta = wtaParser.parseForBestTrees(fileName);
		assertThat(wta.getSymbols().size(), is(3));
	}

	@Test
	public void shouldParseCorrectFile3() throws Exception {
		wta = wtaParser.parseForBestTrees(fileName);
		assertThat(wta.getRulesByResultingState(
				new State(new Symbol("pa", 0))).size(), is(4));
	}

	@Test
	public void shouldParseCorrectFile4() throws Exception {
		wta = wtaParser.parseForBestTrees(fileName);
		assertThat(wta.getRulesByResultingState(
				new State(new Symbol("pb", 0))).size(), is(4));
	}

	@Test
	public void shouldParseCorrectFile5() throws Exception {
		wta = wtaParser.parseForBestTrees(fileName);
		assertThat(wta.getRulesByResultingState(
				new State(new Symbol("qa", 0))).size(), is(2));
	}

	@Test
	public void shouldParseCorrectFile6() throws Exception {
		wta = wtaParser.parseForBestTrees(fileName);
		assertThat(wta.getRulesByResultingState(
				new State(new Symbol("qb", 0))).size(), is(2));
	}

	@Test
	public void shouldParseCorrectFile7() throws Exception {
		wta = wtaParser.parseForBestTrees(fileName);
		assertThat(wta.getStates().size(), is(4));
	}

	@Test
	public void shouldParseCorrectFile8() throws Exception {
		wta = wtaParser.parseForBestTrees(fileName);
		ArrayList<Rule> list = new ArrayList<>();
		list.add(new Rule(new Node(aSymb), w1, pb));
		list.add(new Rule(new Node(aSymb), w2, pa));
		list.add(new Rule(new Node(bSymb), w2, pb));
		list.add(new Rule(new Node(bSymb), w1, pa));
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
