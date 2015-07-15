package se.umu.cs.nfl.aj.nbest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NodeTest {

	Node<String> root;

	@Before
	public void setUp() throws Exception {
		root = new Node<String>("S");
		Node<String> np1 = new Node<String>("NP");
		Node<String> np2 = new Node<String>("NP");
		Node<String> vp = new Node<String>("VP");
		Node<String> n1 = new Node<String>("N");
		Node<String> n2 = new Node<String>("N");
		Node<String> v = new Node<String>("V");

		n1.addChild(new Node<String>("John"));
		v.addChild(new Node<String>("loves"));
		n2.addChild(new Node<String>("Mary"));

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
		Node<String> n1 = new Node<String>("A");
		Node<String> n2 = new Node<String>("A");
		assertTrue(n1.equals(n2));
	}

	@Test
	public void shouldNotBeEqualLeafNodes() throws Exception {
		Node<String> n1 = new Node<String>("A");
		Node<String> n2 = new Node<String>("B");
		assertFalse(n1.equals(n2));
	}

	@Test
	public void shouldBeEqual() throws Exception {
		Node<String> root2 = new Node<String>("S");
		Node<String> np1 = new Node<String>("NP");
		Node<String> np2 = new Node<String>("NP");
		Node<String> vp = new Node<String>("VP");
		Node<String> n1 = new Node<String>("N");
		Node<String> n2 = new Node<String>("N");
		Node<String> v = new Node<String>("V");

		n1.addChild(new Node<String>("John"));
		v.addChild(new Node<String>("loves"));
		n2.addChild(new Node<String>("Mary"));

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
		Node<String> root2 = new Node<String>("S");
		Node<String> np1 = new Node<String>("NP");
		Node<String> np2 = new Node<String>("NP");
		Node<String> vp = new Node<String>("VP");
		Node<String> n1 = new Node<String>("N");
		Node<String> n2 = new Node<String>("N");
		Node<String> v = new Node<String>("V");

		n1.addChild(new Node<String>("Mary"));
		v.addChild(new Node<String>("loves"));
		n2.addChild(new Node<String>("John"));

		np1.addChild(n1);
		vp.addChild(v);
		np2.addChild(n2);

		vp.addChild(np2);

		root2.addChild(np1);
		root2.addChild(vp);

		assertFalse(root2.equals(root));
	}


}
