package se.umu.cs.flp.aj.nbest.wta;

import static org.junit.Assert.*;

import org.junit.Test;

import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.semiring.TropicalSemiring;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.Symbol;
import se.umu.cs.flp.aj.nbest.wta.WTA;
import se.umu.cs.flp.aj.nbest.wta.exceptions.DuplicateRuleException;
import se.umu.cs.flp.aj.nbest.wta.exceptions.SymbolUsageException;
import se.umu.cs.flp.aj.nbest.treedata.Node;

public class WTATest {

	private Semiring semiring = new TropicalSemiring();
	private WTA wta = new WTA(semiring);
	private Symbol aSymb = new Symbol("a", 0);
	private Symbol fSymb = new Symbol("f", 2);
	private Symbol gSymb = new Symbol("g", 2);
	private State state = new State(new Symbol("q0", 0));
	private State finalState = new State(new Symbol("qf", 0));


	@Test
	public void shouldBeEqualToStateWithSameLabel()
			throws SymbolUsageException {
		wta.addState("q0");
		assertEquals(state, wta.addState("q0"));
	}

	@Test
	public void shouldBeEqualToSymbolWithSameLabel()
			throws SymbolUsageException {
		wta.addSymbol("a", 0);
		assertEquals(aSymb, wta.addSymbol("a", 0));
	}

	@Test
	public void shouldCreateAndSetStateToFinal() throws SymbolUsageException {
		wta.setFinalState("q0");
		assertTrue(wta.addState("q0").isFinal());
	}

	@Test
	public void shouldGetRulesByState()
			throws SymbolUsageException, DuplicateRuleException {

		State resState = wta.addState("q0");
		Symbol symbol = wta.addSymbol("a", 0);
		Node tree = new Node(symbol);
		wta.addRule(new Rule(tree,
				semiring.one(), resState));
//		Rule rule = wta.getRulesByResultingState(state).get(0);
//System.out.println(state.getIncoming());
		Rule rule = resState.getIncoming().get(0);
		assertEquals(aSymb, rule.getTree().getLabel());
	}

	@Test
	public void shouldGetRulesBySymbol()
			throws SymbolUsageException, DuplicateRuleException {

		State resState = wta.addState("qf");
		Symbol symbol = wta.addSymbol("f", 2);
		wta.addRule(new Rule(new Node(symbol), semiring.one(),
				resState));
//		Rule rule = wta.getTransitionFunction().getRulesBySymbol(fSymb).get(0);
//		assertEquals(finalState, rule.getResultingState());
//		assertEquals(finalState,
//				wta.getRulesByResultingState(resState).get(0).getResultingState());
		assertEquals(finalState,
				resState.getIncoming().get(0).getResultingState());
	}

	@Test
	public void shouldHaveTwoRulesByResultingState()
			throws DuplicateRuleException {

		wta.addRule(new Rule(new Node(fSymb), semiring.one(), state));
		wta.addRule(new Rule(new Node(gSymb), semiring.one(), state));

//		assertEquals(2, wta.getRulesByResultingState(state).size());
		assertEquals(2, state.getIncoming().size());
	}

//	@Test
//	public void shouldHaveTwoRulesBySymbol()
//			throws SymbolUsageException, DuplicateRuleException {
//
//		wta.addSymbol("f", 2);
//		wta.getTransitionFunction().addRule(new Rule(new Symbol("f", 2),
//				semiring.one(), new State("q0")));
//		wta.getTransitionFunction().addRule(new Rule(new Symbol("f", 2),
//				semiring.one(), new State("q1")));
//
//		assertEquals(2, wta.getTransitionFunction().
//				getRulesBySymbol(new Symbol("f", 2)).size());
//	}

	@Test
	public void shouldGetFinalStates() throws Exception {
		wta.addState("qf");
		wta.addState("q0");
		wta.setFinalState("qf");
		assertEquals(finalState, wta.getFinalStates().get(0));
	}

}
