package se.umu.cs.flp.aj.nbest;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.umu.cs.flp.aj.nbest.data.Node;
import se.umu.cs.flp.aj.wta.State;
import se.umu.cs.flp.aj.wta.Symbol;
import se.umu.cs.flp.aj.wta.WTA;
import se.umu.cs.flp.aj.wta.Weight;
import se.umu.cs.flp.aj.wta_handlers.WTAParser;

public class BestTreesBasicTest {
	
	private String fileName = "wta_examples/wta0.rtg";
	private WTA wta;

	@Before
	public void setUp() throws Exception {
		WTAParser parser = new WTAParser();
		wta = parser.parse(fileName);
		
		HashMap<State, Weight> smallestCompletions = 
				NBest.getSmallestCompletions(wta);
		BestTreesBasic.setSmallestCompletions(smallestCompletions);
	}

	@After
	public void tearDown() throws Exception {
		wta = null;
	}
	
	@Test
	public void shouldGetOptimalStateForLeafNode() throws Exception {
		Node<Symbol> leafNode = new Node<Symbol>(new Symbol("a", 0));
		State optimalState = BestTreesBasic.
				getOptimalStates(leafNode).get(0);
		
		assertEquals(new State("pb"), optimalState);
	}
	
	@Test
	public void shouldGetOptimalStateForLeafNode2() throws Exception {
		Node<Symbol> leafNode = new Node<Symbol>(new Symbol("b", 0));
		State optimalState = BestTreesBasic.
				getOptimalStates(leafNode).get(0);
		
		assertEquals(new State("pa"), optimalState);
	}
	
	@Test
	public void shouldGetOptimalStatesForNonLeafNode() throws Exception {
		Node<Symbol> leafNodeA = new Node<Symbol>(new Symbol("a", 0));
		Node<Symbol> node = new Node<Symbol>(new Symbol("ball", 2));
		
		node.addChild(leafNodeA);
		node.addChild(leafNodeA);
		
		BestTreesBasic.computeCurrentWeight(wta, leafNodeA);
		BestTreesBasic.computeCurrentWeight(wta, node);
		
		ArrayList<State> expected = new ArrayList<>();
		expected.add(new State("qb"));
		
		assertEquals(expected, BestTreesBasic.getOptimalStates(node));
	}
	
	@Test
	public void shouldGetOptimalStatesForNonLeafNode2() throws Exception {
		Node<Symbol> leafNodeA = new Node<Symbol>(new Symbol("a", 0));
		Node<Symbol> leafNodeB = new Node<Symbol>(new Symbol("b", 0));
		Node<Symbol> node = new Node<Symbol>(new Symbol("ball", 2));
		
		node.addChild(leafNodeB);
		node.addChild(leafNodeA);
		
		BestTreesBasic.computeCurrentWeight(wta, leafNodeA);
		BestTreesBasic.computeCurrentWeight(wta, leafNodeB);
		BestTreesBasic.computeCurrentWeight(wta, node);
		
		ArrayList<State> expected = new ArrayList<>();
		expected.add(new State("qb"));
		expected.add(new State("qa"));
		
		assertEquals(expected, BestTreesBasic.getOptimalStates(node));
	}
	
//	@Test
//	public void shouldGetExpansion() throws Exception {
//		Node<Symbol> leafNodeA = new Node<Symbol>(new Symbol("a", 0));
//		ArrayList<Node<Symbol>> expansion = 
//				BestTreesBasic.expandWith(wta, leafNodeA);
//		
//		ArrayList<Node<Symbol>> expected = new ArrayList<Node<Symbol>>();
//		//expected.add(leafNodeA); // TODO
//		
//		System.out.println("Expansion: ");
//		for (Node<Symbol> n : expansion) {
//			System.out.println(n);
//		}
//		
//		assertEquals(expected, expansion);
//	}

}
