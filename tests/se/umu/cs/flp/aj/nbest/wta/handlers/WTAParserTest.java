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
		assertThat(wta.getSymbols().size(), is(7));
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

}
