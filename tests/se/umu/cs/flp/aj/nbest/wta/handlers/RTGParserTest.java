package se.umu.cs.flp.aj.nbest.wta.handlers;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.semiring.TropicalSemiring;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.WTA;
import se.umu.cs.flp.aj.nbest.wta.parsers.RTGParser;

public class RTGParserTest {
	public static final String fileName = "rtg_examples/rtg_for_wta_conversion.rtg";
	public static final String fileName2 = "rtg_examples/tiburon_wta-gen-1.rtg";

	private Semiring semiring = new TropicalSemiring();

	private RTGParser rtgParser = new RTGParser(semiring);
	private WTA wta;

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
		HashMap<String, State> states = wta.getStates();
		State state = states.get("q");
		assertThat(state.getIncoming().size(), is(1));
	}

	@Test
	public void shouldParseCorrectFile4() throws Exception {
		wta = rtgParser.parseForBestTrees(fileName);
		HashMap<String, State> states = wta.getStates();
		State state = states.get("q1");
		assertThat(state.getIncoming().size(), is(1));
	}

	@Test
	public void shouldParseCorrectFile5() throws Exception {
		wta = rtgParser.parseForBestTrees(fileName);
		HashMap<String, State> states = wta.getStates();
		State state = states.get("q2");
		assertThat(state.getIncoming().size(), is(1));
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
		HashMap<String, State> states = wta.getStates();
		State state = states.get("qq");
		assertThat(state.getIncoming().size(), is(4));
	}

	@Test
	public void shouldParseCorrectSecondFile4() throws Exception {
		wta = rtgParser.parseForBestTrees(fileName2);
		HashMap<String, State> states = wta.getStates();
		State state = states.get("qa");
		assertThat(state.getIncoming().size(), is(2));
	}

	@Test
	public void shouldParseCorrectSecondFile5() throws Exception {
		wta = rtgParser.parseForBestTrees(fileName2);
		HashMap<String, State> states = wta.getStates();
		State state = states.get("qa1");
		assertThat(state.getIncoming().size(), is(1));
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
