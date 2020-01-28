package se.umu.cs.flp.aj.nbest.wta;

import static org.junit.Assert.*;

import org.junit.Test;

import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.semiring.SemiringFactory;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.Configuration;
import se.umu.cs.flp.aj.nbest.treedata.Node;
import se.umu.cs.flp.aj.nbest.treedata.TreeKeeper2;

public class RuleTest {

	SemiringFactory sf = new SemiringFactory();
	Semiring semiring = sf.getSemiring("tropical");
	Symbol fSym = new Symbol("f", 2);
	Symbol aSym = new Symbol("a", 0);
	Symbol bSym = new Symbol("b", 0);
	Symbol cSym = new Symbol("c", 0);
	Symbol dSym = new Symbol("d", 0);
	Symbol eSym = new Symbol("e", 1);
	Node fNode = new Node(fSym);
	Node aNode = new Node(aSym);
	Node bNode = new Node(bSym);
	Node cNode = new Node(cSym);
	Node dNode = new Node(dSym);
	Node eNode = new Node(eSym);

	Symbol q0Sym = new Symbol("q0", 0);
	Symbol q1Sym = new Symbol("q1", 0);
	Symbol q2Sym = new Symbol("q2", 0);
	Symbol q3Sym = new Symbol("q3", 0);
	Symbol qfSym = new Symbol("qf", 0);

	State resState = new State(qfSym);
	State q0 = new State(q0Sym);
	State q1 = new State(q1Sym);
	State q2 = new State(q2Sym);
	State q3 = new State(q3Sym);
	Weight[] smallestCompletions = {
			semiring.one(),
			semiring.createWeight(2),
			semiring.createWeight(1)};

	Rule rule = new Rule(fNode, semiring.createWeight(0.5), resState, q0, q1);
	Rule rule2 = new Rule(fNode, semiring.createWeight(0.5), resState);
	TreeKeeper2[] tklist = new TreeKeeper2[rule.getNumberOfStates()];

	Node n0 = new Node(q0Sym);
	Node n1 = new Node(q1Sym);
	
	Configuration<TreeKeeper2> config;

	private void init() {

		TreeKeeper2.init(smallestCompletions);

		q0Sym.setNonterminal(true);
		q1Sym.setNonterminal(true);
		q2Sym.setNonterminal(true);
		q3Sym.setNonterminal(true);
		qfSym.setNonterminal(true);

		n0 = new Node(q0Sym);
		n1 = new Node(q1Sym);
	}

	private void init1a() {
		init();

		eNode.addChild(aNode);

		fNode.addChild(aNode);
		fNode.addChild(n0);
		fNode.addChild(cNode);
		fNode.addChild(n1);
		fNode.addChild(eNode);

		tklist = new TreeKeeper2[rule.getNumberOfStates()];
		tklist[0] = new TreeKeeper2(eNode, semiring.createWeight(1), q0);
		tklist[1] = new TreeKeeper2(cNode, semiring.createWeight(2), q1);
		config = new Configuration<>(new int[tklist.length], tklist.length, null);
		config.setWeight(semiring.createWeight(3));
		config.setValues(tklist);
	}
	
	private void init1b() {
		init();

		eNode.addChild(aNode);

		fNode.addChild(aNode);
		fNode.addChild(n0);
		fNode.addChild(cNode);
		fNode.addChild(eNode);
		fNode.addChild(n1);

		tklist = new TreeKeeper2[rule.getNumberOfStates()];
		tklist[0] = new TreeKeeper2(eNode, semiring.createWeight(1), q0);
		tklist[1] = new TreeKeeper2(cNode, semiring.createWeight(2), q1);
		config = new Configuration<>(new int[tklist.length], tklist.length, null);
		config.setWeight(semiring.createWeight(3));
		config.setValues(tklist);
	}

	private void init2() {
		init();

		fNode.addChild(n0);
		fNode.addChild(n1);
		
		tklist = new TreeKeeper2[rule.getNumberOfStates()];
		tklist[0] = new TreeKeeper2(aNode, semiring.createWeight(1), q0);
		tklist[1] = new TreeKeeper2(aNode, semiring.createWeight(2), q1);
		config = new Configuration<>(new int[tklist.length], tklist.length, null);
		config.setWeight(semiring.createWeight(3));
		config.setValues(tklist);
	}

	private void init3() {
		init();

		fNode.addChild(aNode);
		fNode.addChild(bNode);

		tklist = new TreeKeeper2[rule2.getNumberOfStates()];
		config = new Configuration<>(new int[tklist.length], tklist.length, null);
		config.setWeight(semiring.createWeight(0));
		config.setValues(tklist);
	}

	@Test
	public void testRuleApplication1a() {
		init1a();
		TreeKeeper2 result = rule.apply(config);
		String resString = result.getTree().toString();
		assertEquals("f[a, e[a], c, c, e[a]]", resString);
	}
	
	@Test
	public void testRuleApplication1b() {
		init1b();
		TreeKeeper2 result = rule.apply(config);
		String resString = result.getTree().toString();
		assertEquals("f[a, e[a], c, e[a], c]", resString);
	}

	@Test
	public void testRuleApplication2a() {
		init2();
		TreeKeeper2 result = rule.apply(config);
		String resString = result.getTree().toString();
		assertEquals("f[a, a]", resString);
	}

	@Test
	public void testRuleApplication2b() {
		init3();
		TreeKeeper2 result = rule.apply(config);
		String resString = result.getTree().toString();
		assertEquals("f[a, b]", resString);
	}

	@Test
	public void testApplicationWeight() {
		init1a();
		TreeKeeper2 result = rule.apply(config);
		Weight resWeight = semiring.createWeight(3.5);
		assertEquals(resWeight, result.getRunWeight());
	}

	@Test
	public void testFinalState() {
		init1a();
		TreeKeeper2 result = rule.apply(config);
		assertEquals(resState, result.getResultingState());
	}

	@Test
	public void testFinalState2() {
		init3();
		TreeKeeper2 result = rule2.apply(config);
		assertEquals(resState, result.getResultingState());
	}

}
