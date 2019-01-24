package se.umu.cs.flp.aj.nbest.wta.handlers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.semiring.TropicalSemiring;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.Symbol;
import se.umu.cs.flp.aj.nbest.wta.WTA;
import se.umu.cs.flp.aj.nbest.wta.parsers.RTGParser;

public class RTGParserTest {
	public static final String fileName = "rtg_examples/rtg_for_wta_conversion.rtg";
	public static final String fileName2 = "rtg_examples/tiburon_wta-gen-1.rtg";

//	public static final String emptyLine = "		    			";
//	public static final String commentLine = "//a -> q1";
//	public static final String finalLine = "q0";
//	public static final String finalLine2 = "	      qf 	";
//	public static final String leafRuleLine = " q0   -> A ";
//	public static final String leafRuleLineWithWeight = "qf\n q0   -> a  #  2 ";
//	public static final String nonLeafRuleLine = "  qf ->   f(q0 q1) ";
//	public static final String nonLeafRuleLineWithWeight =
//			"  qf ->   f(q0 q1)  #  0.2 ";
//	public static final String nonLeafRuleLine2 = "  qf ->   g(q0) ";
//	public static final String nonLeafRuleLineWithWeight2 =
//			"  qf ->   g(q0)  #  0.2 ";
//	public static final String mtDataTestRuleLine = "qnn-hd-dat.sg.masc -> nn-hd-dat.sg.masc(qtunfischfang)";

	private Semiring semiring = new TropicalSemiring();

	private RTGParser rtgParser = new RTGParser(semiring);
	private WTA wta = new WTA(semiring);

//	private Symbol ASymb = new Symbol("A", 0);
//	private Symbol aSymb = new Symbol("a", 0);
//	private Symbol bSymb = new Symbol("b", 0);
//	private Symbol gSymb = new Symbol("g", 1);
//	private Symbol fSymb = new Symbol("f", 2);
//	private Symbol mtSymb = new Symbol("nn-hd-dat.sg.masc", 1);
//
//	private State q0 = new State(new Symbol("q0", 0));
//	private State q1 = new State(new Symbol("q1", 0));
//	private State qf = new State(new Symbol("qf", 0));
//
//	private State pa = new State(new Symbol("pa", 0));
//	private State pb = new State(new Symbol("pb", 0));
//
//	private State mtq0 = new State(new Symbol("qtunfischfang", 0));
//	private State mtq1 = new State(new Symbol("qnn-hd-dat.sg.masc", 0));
//
//	private Weight w0 = new TropicalSemiring().one();
//	private Weight w1 = new TropicalSemiring().createWeight(1);
//	private Weight w2 = new TropicalSemiring().createWeight(2);
//	private Weight w02 = new TropicalSemiring().createWeight(0.2);
//
//	Rule leafRule = new Rule(new Node(ASymb), w0, q0);
//	Rule leafRuleWithWeight = new Rule(new Node(aSymb), w2, q0);
//	Rule nonLeafRule = new Rule(new Node(fSymb), w0, qf, q0, q1);
//	Rule nonLeafRuleWithWeight = new Rule(new Node(fSymb), w02,
//			qf, q0, q1);
//	Rule nonLeafRule2 = new Rule(new Node(gSymb), w0, qf, q0);
//	Rule nonLeafRuleWithWeight2 = new Rule(new Node(gSymb), w02,
//			qf, q0);
//	Rule mtDataTestRule = new Rule(new Node(mtSymb), w0, mtq1, mtq0);

	@Test
	public void shouldParseCorrectFile0() throws Exception {
		wta = rtgParser.parseForBestTrees(fileName);
		assertThat(wta.getStates().size(), is(3));
	}

	@Test
	public void shouldParseCorrectFile1() throws Exception {
		wta = rtgParser.parseForBestTrees(fileName);
		assertThat(wta.getFinalStates().size(), is(1));
	}

	@Test
	public void shouldParseCorrectFile2() throws Exception {
		wta = rtgParser.parseForBestTrees(fileName);
		assertThat(wta.getSymbols().size(), is(9));
	}

	@Test
	public void shouldParseCorrectFile3() throws Exception {
		wta = rtgParser.parseForBestTrees(fileName);
		assertThat(wta.getRulesByResultingState(
				new State(new Symbol("q", 0))).size(), is(1));
	}

	@Test
	public void shouldParseCorrectFile4() throws Exception {
		wta = rtgParser.parseForBestTrees(fileName);
		assertThat(wta.getRulesByResultingState(
				new State(new Symbol("q1", 0))).size(), is(1));
	}

	@Test
	public void shouldParseCorrectFile5() throws Exception {
		wta = rtgParser.parseForBestTrees(fileName);
		assertThat(wta.getRulesByResultingState(
				new State(new Symbol("q2", 0))).size(), is(1));
	}

	/* Tests for second test file */

	@Test
	public void shouldParseCorrectSecondFile0() throws Exception {
		wta = rtgParser.parseForBestTrees(fileName2);
		assertThat(wta.getStates().size(), is(9));
	}

	@Test
	public void shouldParseCorrectSecondFile1() throws Exception {
		wta = rtgParser.parseForBestTrees(fileName2);
		assertThat(wta.getFinalStates().size(), is(1));
	}

	@Test
	public void shouldParseCorrectSecondFile2() throws Exception {
		wta = rtgParser.parseForBestTrees(fileName2);
		assertThat(wta.getSymbols().size(), is(12));
	}

	@Test
	public void shouldParseCorrectSecondFile3() throws Exception {
		wta = rtgParser.parseForBestTrees(fileName2);
		assertThat(wta.getRulesByResultingState(
				new State(new Symbol("qq", 0))).size(), is(4));
	}

	@Test
	public void shouldParseCorrectSecondFile4() throws Exception {
		wta = rtgParser.parseForBestTrees(fileName2);
		assertThat(wta.getRulesByResultingState(
				new State(new Symbol("qa", 0))).size(), is(2));
	}

	@Test
	public void shouldParseCorrectSecondFile5() throws Exception {
		wta = rtgParser.parseForBestTrees(fileName2);
		assertThat(wta.getRulesByResultingState(
				new State(new Symbol("qa1", 0))).size(), is(1));
	}

//	@Test
//	public void shouldParseCorrectFile8() throws Exception {
//		wta = rtgParser.parseForBestTrees(fileName);
//		ArrayList<Rule> list = new ArrayList<>();
//		list.add(new Rule(new Node(aSymb), w1, pb));
//		list.add(new Rule(new Node(aSymb), w2, pa));
//		list.add(new Rule(new Node(bSymb), w2, pb));
//		list.add(new Rule(new Node(bSymb), w1, pa));
//		assertThat(wta.getSourceRules().size(), is(4));
//	}

}
