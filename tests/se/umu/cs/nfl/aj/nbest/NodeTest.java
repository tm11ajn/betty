package se.umu.cs.nfl.aj.nbest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NodeTest {

	Node root;

	@Before
	public void setUp() throws Exception {
		root = new Node("S");
		Node np1 = new Node("NP");
		Node np2 = new Node("NP");
		Node vp = new Node("VP");
		Node n1 = new Node("N");
		Node n2 = new Node("N");
		Node v = new Node("V");

		n1.addChild(new Node("John"));
		v.addChild(new Node("loves"));
		n2.addChild(new Node("Mary"));

		np1.addChild(n1);
		vp.addChild(v);
		np2.addChild(n2);

		vp.addChild(np2);

		root.addChild(np1);
		root.addChild(vp);
	}

	@After
	public void tearDown() throws Exception {
		root = null;
	}

	@Test
	public void shouldGetCorrectString() {
		assertEquals("S[NP[N[John]], VP[V[loves], NP[N[Mary]]]]",
				root.toString());
	}

	@Test
	public void shouldBeEqualLeafNodes() throws Exception {
		Node n1 = new Node("A");
		Node n2 = new Node("A");
		assertTrue(n1.equals(n2));
	}

	@Test
	public void shouldNotBeEqualLeafNodes() throws Exception {
		Node n1 = new Node("A");
		Node n2 = new Node("B");
		assertFalse(n1.equals(n2));
	}

	@Test
	public void shouldBeEqual() throws Exception {
		Node root2 = new Node("S");
		Node np1 = new Node("NP");
		Node np2 = new Node("NP");
		Node vp = new Node("VP");
		Node n1 = new Node("N");
		Node n2 = new Node("N");
		Node v = new Node("V");

		n1.addChild(new Node("John"));
		v.addChild(new Node("loves"));
		n2.addChild(new Node("Mary"));

		np1.addChild(n1);
		vp.addChild(v);
		np2.addChild(n2);

		vp.addChild(np2);

		root2.addChild(np1);
		root2.addChild(vp);

		assertTrue(root2.equals(root));
	}

	@Test
	public void shouldNotBeEqual() throws Exception {
		Node root2 = new Node("S");
		Node np1 = new Node("NP");
		Node np2 = new Node("NP");
		Node vp = new Node("VP");
		Node n1 = new Node("N");
		Node n2 = new Node("N");
		Node v = new Node("V");

		n1.addChild(new Node("Mary"));
		v.addChild(new Node("loves"));
		n2.addChild(new Node("John"));

		np1.addChild(n1);
		vp.addChild(v);
		np2.addChild(n2);

		vp.addChild(np2);

		root2.addChild(np1);
		root2.addChild(vp);

		assertFalse(root2.equals(root));
	}


}
