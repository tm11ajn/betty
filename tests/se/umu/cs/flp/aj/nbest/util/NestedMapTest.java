package se.umu.cs.flp.aj.nbest.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.umu.cs.flp.aj.nbest.treedata.Node;
import se.umu.cs.flp.aj.nbest.util.NestedMap;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.Symbol;

public class NestedMapTest {

	NestedMap<Node, State, Double> map = new NestedMap<>();

	Node tree = new Node(new Symbol("root", 3));
	Node child3 = new Node(new Symbol("child3", 1));

	State s0 = new State(new Symbol("s0", 0));
	State s1 = new State(new Symbol("s1", 0));
	State s2 = new State(new Symbol("s2", 0));
	State s3 = new State(new Symbol("s3", 0));

	int initialTreeHash;

	@Before
	public void setUp() throws Exception {
		initialTreeHash = tree.hashCode();

		tree.addChild(new Node(new Symbol("child0", 0)));
		tree.addChild(new Node(new Symbol("child1", 0)));
		tree.addChild(new Node(new Symbol("child2", 0)));

		child3.addChild(new Node(new Symbol("grandchild30", 0)));
		child3.addChild(new Node(new Symbol("grandchild31", 0)));

		tree.addChild(child3);
	}

	@After
	public void tearDown() throws Exception {
		map = null;
		tree = null;
	}

	@Test
	public void shouldReturnNullOnNewTreeInsert() {
		assertNull(map.put(tree, s0, 0.1));
	}

	@Test
	public void shouldHaveValueForInsertedTree() {
		map.put(tree, s0, 0.1);
		assertEquals(0.1, map.get(tree, s0).doubleValue(), 1e5);
	}

	@Test
	public void shouldHaveCorrectValueDespiteNewChildren() {
		map.put(tree, s0, 0.1);
		tree.addChild(child3);
		map.put(tree, s0, 0.1);
		assertEquals(0.1, map.get(tree, s0).doubleValue(), 1e5);
	}

	@Test
	public void shouldGetCorrectValue() {
		map.put(tree, s0, 0.1);
		map.put(child3, s1, 0.2);
		assertEquals(0.2, map.get(child3, s1).doubleValue(), 1e5);
	}

	@Test
	public void shouldNotHaveSameHashWithAddedNode() {
		tree.addChild(child3);
		assertNotSame(initialTreeHash, tree.hashCode());
	}

}
