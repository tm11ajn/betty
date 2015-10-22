package se.umu.cs.flp.aj.nbest.data;

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
	
	@Test
	public void shouldHaveCorrectSize() throws Exception {
		assertEquals(10, root.getSize());
	}

	@Test
	public void shouldBeSmallerThanLargerTree() throws Exception {
		Node<String> a = new Node<>("c");
		Node<String> b = new Node<>("ball");
		b.addChild(new Node<>("b"));
		b.addChild(new Node<>("b"));
		assertEquals(-1, a.compareTo(b));
	}
	
	@Test
	public void shouldBeLargerThanSmallerTree() throws Exception {
		Node<String> a = new Node<>("c");
		Node<String> b = new Node<>("ball");
		b.addChild(new Node<>("b"));
		b.addChild(new Node<>("b"));
		assertEquals(1, b.compareTo(a));
	}
	
	//ball[ball[b, b], ball[b, b]]
	//ball[a, ball[b, b]]
	
	@Test
	public void shouldBeSmallerThanLargerTree2() throws Exception {
		Node<String> aroot = new Node<>("ball");
		Node<String> broot = new Node<>("ball");
		Node<String> ba = new Node<>("ball");
		Node<String> b1 = new Node<>("ball");
		Node<String> b2 = new Node<>("ball");
		ba.addChild(new Node<>("b"));
		ba.addChild(new Node<>("b"));
		b1.addChild(new Node<>("b"));
		b1.addChild(new Node<>("b"));
		b2.addChild(new Node<>("b"));
		b2.addChild(new Node<>("b"));
		broot.addChild(b1);
		broot.addChild(b2);
		aroot.addChild(new Node<>("a"));
		aroot.addChild(b1);
		assertEquals(-1, aroot.compareTo(broot));
	}
	
	@Test
	public void shouldBeLargerThanSmallerTree2() throws Exception {
		Node<String> a = new Node<>("ball");
		Node<String> broot = new Node<>("ball");
		Node<String> ba = new Node<>("ball");
		Node<String> b1 = new Node<>("ball");
		Node<String> b2 = new Node<>("ball");
		ba.addChild(new Node<>("b"));
		ba.addChild(new Node<>("b"));
		b1.addChild(new Node<>("b"));
		b1.addChild(new Node<>("b"));
		b2.addChild(new Node<>("b"));
		b2.addChild(new Node<>("b"));
		broot.addChild(b1);
		broot.addChild(b2);
		a.addChild(new Node<>("a"));
		a.addChild(b1);
		assertEquals(1, broot.compareTo(a));
	}
	
	@Test
	public void shouldBeSmallerThanEqualLengthButLexicallySmallerTree() 
			throws Exception {
		Node<String> a = new Node<>("a");
		Node<String> b = new Node<>("b");
		assertEquals(-1, a.compareTo(b));
	}
	
	@Test
	public void shouldBeLargerThanEqualLengthButLexicallyLargerString() 
			throws Exception {
		Node<String> a = new Node<>("a");
		Node<String> b = new Node<>("b");
		assertEquals(1, b.compareTo(a));
	}
	
	@Test
	public void shouldBeEqualToEqualString() 
			throws Exception {
		Node<String> a = new Node<>("ball[b, b]");
		Node<String> b = new Node<>("ball[b, b]");
		assertEquals(0, b.compareTo(a));
	}
}
