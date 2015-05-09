package se.umu.cs.nfl.aj.nbest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.umu.cs.nfl.aj.wta.State;

public class NestedMapTest {

	NestedMap<Node, State, Double> map = new NestedMap<>();

	Node tree = new Node("root");
	Node child3 = new Node("child3");

	State s0 = new State("s0");
	State s1 = new State("s1");
	State s2 = new State("s2");
	State s3 = new State("s3");

	int initialTreeHash;

	@Before
	public void setUp() throws Exception {
		initialTreeHash = tree.hashCode();

		tree.addChild(new Node("child0"));
		tree.addChild(new Node("child1"));
		tree.addChild(new Node("child2"));

		child3.addChild(new Node("grandchild30"));
		child3.addChild(new Node("grandchild31"));

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
		assertEquals(0.1, map.get(tree, s0).doubleValue(), 1e5);
	}

	@Test
	public void shouldGetCorrectValue() {
		map.put(tree, s0, 0.1);
		map.put(child3, s1, 0.2);
		assertEquals(0.2, map.get(child3, s1).doubleValue(), 1e5);
	}

	@Test
	public void shouldHaveSameHashDespiteNewNode() {
		tree.addChild(child3);
		assertEquals(initialTreeHash, tree.hashCode());
	}
	
}
