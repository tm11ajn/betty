package se.umu.cs.flp.aj.nbest.wta.handlers;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.semiring.TropicalSemiring;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.WTA;
import se.umu.cs.flp.aj.nbest.wta.handlers.WTABuilder;
import se.umu.cs.flp.aj.nbest.wta.handlers.WTAParser;

public class WTABuilderTest {

	private Semiring semiring = new TropicalSemiring();
	private String fileName = "wta_examples/wta0.rtg";
	private WTA wta;
	private WTABuilder wtaBuilder;

	@Before
	public void setUp() throws Exception {
		WTAParser parser = new WTAParser(semiring);
		wta = parser.parse(fileName);
		wtaBuilder = new WTABuilder();
	}

	@After
	public void tearDown() throws Exception {
		wta = null;
	}
	@Test
	public void shouldGetModifiedWTA() throws Exception {
		WTA modWTA = wtaBuilder.buildModifiedWTA(wta, new State("pa"));
		System.out.println(modWTA.getSourceRules());
		System.out.println(modWTA);
		assertEquals(
				"States: qa_extension qb_extension pa pb qb pa_extension qa pb_extension \n"
				+ "Ranked alphabet: ball(2) b(0) a(0) context_symbol_with_rank_zero(0) \n"
				+ "Transition function: \n"
				+ "a -> pa # 2.0\n"
				+ "a -> pb # 1.0\n"
				+ "context_symbol_with_rank_zero -> pa_extension\n"
				+ "b -> pa # 1.0\n"
				+ "b -> pb # 2.0\n"
				+ "ball[pa, pa] -> qa\n"
				+ "ball[pa_extension, pa] -> qa_extension\n"
				+ "ball[pa, pa_extension] -> qa_extension\n"
				+ "ball[qa, qa] -> qa\n"
				+ "ball[qa_extension, qa] -> qa_extension\n"
				+ "ball[qa, qa_extension] -> qa_extension\n"
				+ "ball[pb, pb] -> qb\n"
				+ "ball[pb_extension, pb] -> qb_extension\n"
				+ "ball[pb, pb_extension] -> qb_extension\n"
				+ "ball[qb, qb] -> qb\n"
				+ "ball[qb_extension, qb] -> qb_extension\n"
				+ "ball[qb, qb_extension] -> qb_extension\n"
				+ "ball[pa, qa] -> pa\n"
				+ "ball[pa_extension, qa] -> pa_extension\n"
				+ "ball[pa, qa_extension] -> pa_extension\n"
				+ "ball[qa, pa] -> pa\n"
				+ "ball[qa_extension, pa] -> pa_extension\n"
				+ "ball[qa, pa_extension] -> pa_extension\n"
				+ "ball[pb, qb] -> pb\n"
				+ "ball[pb_extension, qb] -> pb_extension\n"
				+ "ball[pb, qb_extension] -> pb_extension\n"
				+ "ball[qb, pb] -> pb\n"
				+ "ball[qb_extension, pb] -> pb_extension\n"
				+ "ball[qb, pb_extension] -> pb_extension\n"
				+ "Final states: qa_extension qb_extension ",
				modWTA.toString());
	}

	// TODO make a smallestcompletionsfindertest instead
//	@Test
//	public void shouldFindSmallestCompletionWeights() throws Exception {
//		HashMap<State, Weight> smallestCompletionWeights =
//				wtaBuilder.findSmallestCompletionWeights(wta);
//
//		HashMap<State, Weight> expected = new HashMap<>();
//
//		expected.put(new State("pa"), new TropicalWeight(1));
//		expected.put(new State("pb"), new TropicalWeight(1));
//		expected.put(new State("qa"), new TropicalWeight(0));
//		expected.put(new State("qb"), new TropicalWeight(0));
//
//		assertEquals(expected, smallestCompletionWeights);
//	}

}
