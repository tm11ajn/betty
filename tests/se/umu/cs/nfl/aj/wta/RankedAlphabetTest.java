package se.umu.cs.nfl.aj.wta;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RankedAlphabetTest {

	RankedAlphabet ranked = new RankedAlphabet();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldInsertSymbolTest() {
		ranked.addSymbol(new Symbol("a", 0));
	}

	@SuppressWarnings("serial")
	@Test
	public void shouldReturnSymbolsByIndex() {
		ranked.addSymbol(new Symbol("a", 0));
		ranked.addSymbol(new Symbol("b", 0));
		ranked.addSymbol(new Symbol("ball", 2));
//		assertEquals("ball", ranked.getSymbolsByRank(2).get(0).getSymbol());

		assertEquals(new ArrayList<Symbol>(){{add(0, new Symbol("ball", 2));}},
				ranked.getSymbolsByRank(2));
	}

}
