package se.umu.cs.nfl.aj.wta;

import static org.junit.Assert.*;

import org.junit.Test;

public class WTATest {

	private WTA wta = new WTA();
	private Symbol aSymb = new Symbol("a", 0);
	private Symbol fSymb = new Symbol("f", 2);
	private State state = new State("q0");
	private State finalState = new State("qf");


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
