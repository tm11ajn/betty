package se.umu.cs.nfl.aj.nbest;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.umu.cs.nfl.aj.wta.State;
import se.umu.cs.nfl.aj.wta.Symbol;
import se.umu.cs.nfl.aj.wta.WTA;
import se.umu.cs.nfl.aj.wta.WTAParser;
import se.umu.cs.nfl.aj.wta.Weight;

public class NBestTest {
	
	private String fileName = "wta_examples/wta0.rtg";
	private WTA wta;

	@Before
	public void setUp() throws Exception {
		WTAParser parser = new WTAParser();
		wta = parser.parse(fileName);
	}

	@After
	public void tearDown() throws Exception {
		wta = null;
	}
	
	@Test
	public void shouldGetModifiedWTA() throws Exception {
		WTA modWTA = NBest.buildModifiedWTA(wta, new State("pa"));
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
	
	@Test
	public void shouldFindSmallestCompletionWeights() throws Exception {
		HashMap<State, Weight> smallestCompletionWeights = 
				NBest.findSmallestCompletionWeights(wta);
		
		HashMap<State, Weight> expected = new HashMap<>();
		
		expected.put(new State("pa"), new Weight(1));
		expected.put(new State("pb"), new Weight(1));
		expected.put(new State("qa"), new Weight(0));
		expected.put(new State("qb"), new Weight(0));
		
		assertEquals(expected, smallestCompletionWeights);
	}
	
	@Test
	public void shouldGetOptimalStateForLeafNode() throws Exception {
		Node<Symbol> leafNode = new Node<Symbol>(new Symbol("a", 0));
		State optimalState = NBest.getOptimalState(wta, leafNode);
		assertEquals(new State("pb"), optimalState);
	}
	
	@Test
	public void shouldGetOptimalStateForLeafNode2() throws Exception {
		Node<Symbol> leafNode = new Node<Symbol>(new Symbol("b", 0));
		State optimalState = NBest.getOptimalState(wta, leafNode);
		assertEquals(new State("pa"), optimalState);
	}
	
	@Test
	public void shouldGetOptimalStateForNonLeafNode() throws Exception {
		Node<Symbol> leafNodeA = new Node<Symbol>(new Symbol("a", 0));
		Node<Symbol> leafNodeB = new Node<Symbol>(new Symbol("b", 0));
		Node<Symbol> node = new Node<Symbol>(new Symbol("ball", 2));
		
		node.addChild(leafNodeA);
		node.addChild(leafNodeA);
		
		NBest.getOptimalState(wta, leafNodeA);
		NBest.getOptimalState(wta, leafNodeB);
		
		State optimalState = NBest.getOptimalState(wta, node);
		assertEquals(new State("qb"), optimalState);
	}
	
	@Test
	public void shouldGetOptimalStateForNonLeafNode2() throws Exception {
		Node<Symbol> leafNodeA = new Node<Symbol>(new Symbol("a", 0));
		Node<Symbol> leafNodeB = new Node<Symbol>(new Symbol("b", 0));
		Node<Symbol> node = new Node<Symbol>(new Symbol("ball", 2));
		
		node.addChild(leafNodeB);
		node.addChild(leafNodeB);
		
		NBest.getOptimalState(wta, leafNodeA);
		NBest.getOptimalState(wta, leafNodeB);
		
		State optimalState = NBest.getOptimalState(wta, node);
		assertEquals(new State("qa"), optimalState);
	}

}
