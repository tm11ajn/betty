package se.umu.cs.flp.aj.nbest.wta;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.umu.cs.flp.aj.nbest.wta.Symbol;

public class SymbolTest {

	Symbol symbol1 = new Symbol("a", 0);
	Symbol symbol2 = new Symbol("f", 2);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldBeCreated() {
		assertNotNull(new Symbol("a", 0));
	}

	@Test
	public void shouldGetLabel() throws Exception {
		assertEquals("a", symbol1.getLabel());
	}

	@Test
	public void shouldGetRank() throws Exception {
		assertEquals(2, symbol2.getRank());
	}

	@Test
	public void shouldBeEqualToASymbolWithTheSameLabelAndRank() throws Exception {
		assertEquals(new Symbol("a", 0), symbol1);
	}

	@Test
	public void shouldNotBeEqualToASymbolWithAnotherLabel() throws Exception {
		assertNotSame(new Symbol("b", 0), symbol1);
	}

	@Test
	public void shouldNotBeEqualToASymbolWithAnotherRank() throws Exception {
		assertNotSame(new Symbol("a", 1), symbol1);
	}

}
