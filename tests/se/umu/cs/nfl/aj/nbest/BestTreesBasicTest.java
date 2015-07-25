package se.umu.cs.nfl.aj.nbest;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.umu.cs.nfl.aj.nbest.data.Node;
import se.umu.cs.nfl.aj.wta.State;
import se.umu.cs.nfl.aj.wta.Symbol;
import se.umu.cs.nfl.aj.wta.WTA;
import se.umu.cs.nfl.aj.wta_handlers.WTAParser;

public class BestTreesBasicTest {
	
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
	public void shouldGetOptimalStateForLeafNode() throws Exception {
		Node<Symbol> leafNode = new Node<Symbol>(new Symbol("a", 0));
		BestTreesBasic.init(wta);
		State optimalState = BestTreesBasic.
				getOptimalStates(wta, leafNode).get(0);
		assertEquals(new State("pb"), optimalState);
	}
	
	@Test
	public void shouldGetOptimalStateForLeafNode2() throws Exception {
		Node<Symbol> leafNode = new Node<Symbol>(new Symbol("b", 0));
		BestTreesBasic.init(wta);
		State optimalState = BestTreesBasic.
				getOptimalStates(wta, leafNode).get(0);
		assertEquals(new State("pa"), optimalState);
	}
	
	@Test
	public void shouldGetOptimalStatesForNonLeafNode() throws Exception {
		Node<Symbol> leafNodeA = new Node<Symbol>(new Symbol("a", 0));
		Node<Symbol> node = new Node<Symbol>(new Symbol("ball", 2));
		
		BestTreesBasic.init(wta);
		
		node.addChild(leafNodeA);
		node.addChild(leafNodeA);
		
		BestTreesBasic.getOptimalStates(wta, leafNodeA);
		
		ArrayList<State> expected = new ArrayList<>();
		expected.add(new State("qb"));
		
		assertEquals(expected, BestTreesBasic.getOptimalStates(wta, node));
	}
	
	@Test
	public void shouldGetOptimalStatesForNonLeafNode2() throws Exception {
		Node<Symbol> leafNodeA = new Node<Symbol>(new Symbol("a", 0));
		Node<Symbol> leafNodeB = new Node<Symbol>(new Symbol("b", 0));
		Node<Symbol> node = new Node<Symbol>(new Symbol("ball", 2));
		
		BestTreesBasic.init(wta);
		
		node.addChild(leafNodeB);
		node.addChild(leafNodeA);
		
		BestTreesBasic.getOptimalStates(wta, leafNodeA);
		BestTreesBasic.getOptimalStates(wta, leafNodeB);
		
		ArrayList<State> expected = new ArrayList<>();
		expected.add(new State("qa"));
		expected.add(new State("qb"));
		
		assertEquals(expected, BestTreesBasic.getOptimalStates(wta, node));
	}
	
	@Test
	public void shouldGetExpansion() throws Exception {
		Node<Symbol> leafNodeA = new Node<Symbol>(new Symbol("a", 0));
		ArrayList<Node<Symbol>> expansion = 
				BestTreesBasic.expandWith(wta, leafNodeA);
		
		ArrayList<Node<Symbol>> expected = new ArrayList<Node<Symbol>>();
		expected.add(leafNodeA); // TODO
		
		System.out.println("Expansion: ");
		for (Node<Symbol> n : expansion) {
			System.out.println(n);
		}
		
		assertEquals(expected, expansion);
	}

}
