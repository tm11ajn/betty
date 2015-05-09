package se.umu.cs.nfl.aj.wta;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
	public void shouldBeEqualToASymbolWithTheSameLabel() throws Exception {
		fail();
	}



}
