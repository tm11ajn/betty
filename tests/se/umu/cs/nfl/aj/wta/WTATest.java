package se.umu.cs.nfl.aj.wta;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WTATest {

	private WTA wta;
	private Symbol aSymb;
	private Symbol fSymb;
	private State state;
	private State finalState;
//	private Rule leafRule;
//	private Rule nonLeafRule;

	@Before
	public void setUp() throws Exception {
		wta = new WTA();
		aSymb = new Symbol("a", 0);
		fSymb = new Symbol("f", 2);
		state = new State("q0");
		finalState = new State("qf");
//		leafRule = new Rule(aSymb, state);
//		nonLeafRule = new Rule(fSymb, finalState, state, state);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldBeEqualToStateWithSameLabelTest() {
		wta.getState("q0");
		assertEquals(state, wta.getState("q0"));
	}

	@Test
	public void shouldBeEqualToSymbolWithSameLabelTest() {
		wta.getSymbol("a", 0);
		assertEquals(aSymb, wta.getSymbol("a", 0));
	}

	@Test
	public void shouldCreateAndSetStateToFinalTest() {
		wta.setFinalState("q0");
		assertTrue(wta.getState("q0").isFinal());
	}

	@Test
	public void shouldGetRulesByStateTest() {
		State resState = wta.getState("q0");
		Symbol symbol = wta.getSymbol("a", 0);
		wta.addRule(new Rule(symbol, resState));
		Rule rule = wta.getRulesByResultingState(state).get(0);
		assertEquals(aSymb, rule.getSymbol());
	}

	@Test
	public void shouldGetRulesBySymbolTest() {
		State resState = wta.getState("qf");
		Symbol symbol = wta.getSymbol("f", 2);
		wta.addRule(new Rule(symbol, resState));
		Rule rule = wta.getRulesBySymbol(fSymb).get(0);
		assertEquals(finalState, rule.getResultingState());
	}

	@Test
	public void shouldHaveTwoRulesByResultingStateTest() {
		wta.addRule(new Rule(new Symbol("f", 2), new State("q0")));
		wta.addRule(new Rule(new Symbol("g", 2), new State("q0")));
		assertEquals(2, wta.getRulesByResultingState(new State("q0")).size());
	}

	@Test
	public void shouldHaveTwoRulesBySymbolTest() {
		wta.getSymbol("f", 2);
		wta.addRule(new Rule(new Symbol("f", 2), new State("q0")));
		wta.addRule(new Rule(new Symbol("f", 2), new State("q1")));
		assertEquals(2, wta.getRulesBySymbol(new Symbol("f", 2)).size());
	}

}
