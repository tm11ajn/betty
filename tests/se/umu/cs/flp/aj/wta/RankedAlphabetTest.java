package se.umu.cs.flp.aj.wta;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.umu.cs.flp.aj.wta.exceptions.SymbolUsageException;

public class RankedAlphabetTest {

	RankedAlphabet ranked = new RankedAlphabet();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void shouldInsertSymbol() throws SymbolUsageException {
		assertEquals(new Symbol("a", 0), ranked.addSymbol("a", 0));
	}

//	@SuppressWarnings("serial")
//	@Test
//	public void shouldReturnSymbolsByIndex() {
//		ranked.addSymbol("a", 0);
//		ranked.addSymbol("b", 0);
//		ranked.addSymbol("ball", 2);
////		assertEquals("ball", ranked.getSymbolsByRank(2).get(0).getSymbol());
//
//		assertEquals(new ArrayList<Symbol>(){{add(0, new Symbol("ball", 2));}},
//				ranked.getSymbolsByRank(2));
//	}

}
