package se.umu.cs.flp.aj.nbest.treedata;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.semiring.TropicalSemiring;
import se.umu.cs.flp.aj.nbest.treedata.Node;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.Symbol;
import se.umu.cs.flp.aj.nbest.wta.WTA;
import se.umu.cs.flp.aj.nbest.wta.parsers.RTGParser;
import se.umu.cs.flp.aj.nbest.wta.parsers.WTAParser;

public class NodeTest {

	public static final String wtaFile = "wta_examples/wta2.rtg";
	public static final String rtgFile = "rtg_examples/leaf_handling_test.rtg";
	private Semiring semiring = new TropicalSemiring();

	private RTGParser rtgParser = new RTGParser(semiring);
	private WTAParser wtaParser = new WTAParser(semiring);
	private WTA wta;

	Node root;
	Node root2;
	Node np1;
	Node np2;
	Node vp;
	Node n1;
	Node n2;
	Node v;
	Node jSN;
	Node lSN;
	Node mSN;

	Symbol sS = new Symbol("S", 0);
	Symbol npS = new Symbol("NP", 0);
	Symbol vpS = new Symbol("VP", 0);
	Symbol nS = new Symbol("N", 0);
	Symbol vS = new Symbol("V", 0);

	Symbol jS = new Symbol("John", 0);
	Symbol lS = new Symbol("loves", 0);
	Symbol mS = new Symbol("Mary", 0);

	@Before
	public void setUp() throws Exception {
		root = new Node(sS);
		root2 = new Node(sS);
		np1 = new Node(npS);
		np2 = new Node(npS);
		vp = new Node(vpS);
		n1 = new Node(nS);
		n2 = new Node(nS);
		v = new Node(vS);
		jSN = new Node(jS);
		lSN = new Node(lS);
		mSN = new Node(mS);

		n1.addChild(jSN);
		v.addChild(lSN);
		n2.addChild(mSN);

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
	public void shouldGetLeaves() {
		ArrayList<Node> leaves = new ArrayList<>();
		leaves.add(jSN);
		leaves.add(lSN);
		leaves.add(mSN);
		ArrayList<Node> result = root.getLeaves();
System.out.println("RESULT: " + result);
		assertEquals(leaves, result);
	}

	@Test
	public void shouldGetLeavesAfterParsingWTAFile() {
		wta = wtaParser.parseForBestTrees(wtaFile);
		Rule rule = wta.getRulesByState(new State(new Symbol("qe", 0))).get(0);
		ArrayList<Node> result = rule.getTree().getLeaves();

		ArrayList<Node> leaves = new ArrayList<>();
		leaves.add(new Node(new Symbol("qe", 0)));
		leaves.add(new Node(new Symbol("qe", 0)));

System.out.println("RESULT: " + result);
		assertEquals(leaves, result);
	}

	@Test
	public void shouldGetLeavesAfterParsingRTGFile() {
		wta = rtgParser.parseForBestTrees(rtgFile);
		Rule rule = wta.getRulesByState(new State(new Symbol("q", 0))).get(0);
		ArrayList<Node> result = rule.getTree().getLeaves();

		ArrayList<Node> leaves = new ArrayList<>();
		leaves.add(new Node(new Symbol("q", 0)));
		leaves.add(new Node(new Symbol("a", 0)));
		leaves.add(new Node(new Symbol("q", 0)));
		leaves.add(new Node(new Symbol("q2", 0)));

System.out.println("RESULT: " + result);
		assertEquals(leaves, result);
	}

	@Test
	public void shouldGetCorrectString() {
		assertEquals("S[NP[N[John]], VP[V[loves], NP[N[Mary]]]]",
				root.toString());
	}

	@Test
	public void shouldBeEqualLeafNodes() throws Exception {
		Node n1 = new Node(new Symbol("A", 0));
		Node n2 = new Node(new Symbol("A", 0));
		assertTrue(n1.equals(n2));
	}

	@Test
	public void shouldNotBeEqualLeafNodes() throws Exception {
		Node n1 = new Node(new Symbol("A", 0));
		Node n2 = new Node(new Symbol("B", 0));
		assertFalse(n1.equals(n2));
	}

	@Test
	public void shouldBeEqual() throws Exception {
		Node root2 = new Node(sS);
		Node np1 = new Node(npS);
		Node np2 = new Node(npS);
		Node vp = new Node(vpS);
		Node n1 = new Node(nS);
		Node n2 = new Node(nS);
		Node v = new Node(vS);

		n1.addChild(new Node(jS));
		v.addChild(new Node(lS));
		n2.addChild(new Node(mS));

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
		Node root2 = new Node(sS);
		Node np1 = new Node(npS);
		Node np2 = new Node(npS);
		Node vp = new Node(vpS);
		Node n1 = new Node(nS);
		Node n2 = new Node(nS);
		Node v = new Node(vS);

		n1.addChild(new Node(mS));
		v.addChild(new Node(lS));
		n2.addChild(new Node(jS));

		np1.addChild(n1);
		vp.addChild(v);
		np2.addChild(n2);

		vp.addChild(np2);

		root2.addChild(np1);
		root2.addChild(vp);

		assertNotEquals(root2, root);
	}

	@Test
	public void shouldHaveCorrectSize() throws Exception {
		assertEquals(10, root.getSize());
	}

	@Test
	public void shouldBeSmallerThanLargerTree() throws Exception {
		Node a = new Node(new Symbol("c", 0));
		Node b = new Node(new Symbol("ball", 2));
		b.addChild(new Node(new Symbol("b", 0)));
		b.addChild(new Node(new Symbol("b", 0)));
		assertEquals(-1, a.compareTo(b));
	}

	@Test
	public void shouldBeLargerThanSmallerTree() throws Exception {
		Node a = new Node(new Symbol("c", 0));
		Node b = new Node(new Symbol("ball", 2));
		b.addChild(new Node(new Symbol("b", 0)));
		b.addChild(new Node(new Symbol("b", 0)));
		assertEquals(1, b.compareTo(a));
	}

	//ball[ball[b, b], ball[b, b]]
	//ball[a, ball[b, b]]

	@Test
	public void shouldBeSmallerThanLargerTree2() throws Exception {
		Node aroot = new Node(new Symbol("ball", 2));
		Node broot = new Node(new Symbol("ball", 2));
		Node ba = new Node(new Symbol("ball", 2));
		Node b1 = new Node(new Symbol("ball", 2));
		Node b2 = new Node(new Symbol("ball", 2));
		ba.addChild(new Node(new Symbol("b", 0)));
		ba.addChild(new Node(new Symbol("b", 0)));
		b1.addChild(new Node(new Symbol("b", 0)));
		b1.addChild(new Node(new Symbol("b", 0)));
		b2.addChild(new Node(new Symbol("b", 0)));
		b2.addChild(new Node(new Symbol("b", 0)));
		broot.addChild(b1);
		broot.addChild(b2);
		aroot.addChild(new Node(new Symbol("a", 0)));
		aroot.addChild(b1);
		assertEquals(-1, aroot.compareTo(broot));
	}

	@Test
	public void shouldBeLargerThanSmallerTree2() throws Exception {
		Node a = new Node(new Symbol("ball", 2));
		Node broot = new Node(new Symbol("ball", 2));
		Node ba = new Node(new Symbol("ball", 2));
		Node b1 = new Node(new Symbol("ball", 2));
		Node b2 = new Node(new Symbol("ball", 2));
		ba.addChild(new Node(new Symbol("b", 0)));
		ba.addChild(new Node(new Symbol("b", 0)));
		b1.addChild(new Node(new Symbol("b", 0)));
		b1.addChild(new Node(new Symbol("b", 0)));
		b2.addChild(new Node(new Symbol("b", 0)));
		b2.addChild(new Node(new Symbol("b", 0)));
		broot.addChild(b1);
		broot.addChild(b2);
		a.addChild(new Node(new Symbol("a", 0)));
		a.addChild(b1);
		assertEquals(1, broot.compareTo(a));
	}

	@Test
	public void shouldBeSmallerThanEqualLengthButLexicallySmallerTree()
			throws Exception {
		Node a = new Node(new Symbol("a", 0));
		Node b = new Node(new Symbol("b", 0));
		assertEquals(-1, a.compareTo(b));
	}

	@Test
	public void shouldBeLargerThanEqualLengthButLexicallyLargerString()
			throws Exception {
		Node a = new Node(new Symbol("a", 0));
		Node b = new Node(new Symbol("b", 0));
		assertEquals(1, b.compareTo(a));
	}

	@Test
	public void shouldBeEqualToEqualString()
			throws Exception {
		Node a = new Node(new Symbol("ball[b, b]", 0));
		Node b = new Node(new Symbol("ball[b, b]", 0));
		assertEquals(0, b.compareTo(a));
	}
}
