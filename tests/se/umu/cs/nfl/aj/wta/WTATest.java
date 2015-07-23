package se.umu.cs.nfl.aj.wta;

import static org.junit.Assert.*;

import org.junit.Test;

import se.umu.cs.nfl.aj.wta.exceptions.DuplicateRuleException;
import se.umu.cs.nfl.aj.wta.exceptions.SymbolUsageException;

public class WTATest {

	private WTA wta = new WTA();
	private Symbol aSymb = new Symbol("a", 0);
	private Symbol fSymb = new Symbol("f", 2);
	private State state = new State("q0");
	private State finalState = new State("qf");


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
		wta.addRule(new Rule(symbol, resState));
		Rule rule = wta.getRulesByResultingState(state).get(0);
		assertEquals(aSymb, rule.getSymbol());
	}

	@Test
	public void shouldGetRulesBySymbol() 
			throws SymbolUsageException, DuplicateRuleException {
		
		State resState = wta.addState("qf");
		Symbol symbol = wta.addSymbol("f", 2);
		wta.addRule(new Rule(symbol, resState));
		Rule rule = wta.getRulesBySymbol(fSymb).get(0);
		assertEquals(finalState, rule.getResultingState());
	}

	@Test
	public void shouldHaveTwoRulesByResultingState() 
			throws DuplicateRuleException {
		
		wta.addRule(new Rule(new Symbol("f", 2), new State("q0")));
		wta.addRule(new Rule(new Symbol("g", 2), new State("q0")));
		assertEquals(2, wta.getRulesByResultingState(new State("q0")).size());
	}

	@Test
	public void shouldHaveTwoRulesBySymbol() 
			throws SymbolUsageException, DuplicateRuleException {
		
		wta.addSymbol("f", 2);
		wta.addRule(new Rule(new Symbol("f", 2), new State("q0")));
		wta.addRule(new Rule(new Symbol("f", 2), new State("q1")));
		assertEquals(2, wta.getRulesBySymbol(new Symbol("f", 2)).size());
	}

	@Test
	public void shouldGetFinalStates() throws Exception {
		wta.addState("qf");
		wta.addState("q0");
		wta.setFinalState("qf");
		assertEquals(finalState, wta.getFinalStates().get(0));
	}

}
