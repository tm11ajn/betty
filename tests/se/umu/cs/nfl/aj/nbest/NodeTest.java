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

//	@Test
//	public void test() {
//		fail("Not yet implemented");
//	}

	@Test
	public void toStringTest() {
		assertEquals("S[NP[N[John]], VP[V[loves], NP[N[Mary]]]]",
				root.toString());
	}


}
