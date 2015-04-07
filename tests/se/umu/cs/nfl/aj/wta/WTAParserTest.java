package se.umu.cs.nfl.aj.wta;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WTAParserTest {

	private String emptyLine = "		    			";
	private String finalLine = "	final    1.2   	";
	private String leafRuleLine = " a   -> q0";
	private String ruleLine = "  a[q0, q1] ->   qf ";

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void emptyLineRegExpTest() {
		assertTrue(emptyLine.matches("^\\s*$"));
	}

	@Test
	public void finalLineRegExpTest() {
		assertTrue(finalLine.matches("^\\s*final\\s*\\d+(\\.\\d+)?\\s*$"));
	}

}
