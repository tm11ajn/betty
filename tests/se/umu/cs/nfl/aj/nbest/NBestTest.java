package se.umu.cs.nfl.aj.nbest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.umu.cs.nfl.aj.wta.State;
import se.umu.cs.nfl.aj.wta.WTA;
import se.umu.cs.nfl.aj.wta.WTAParser;

public class NBestTest {
	
	private String fileName = "wta_examples/wta0.rtg";
	private WTA wta;

	@Before
	public void setUp() throws Exception {
		WTAParser parser = new WTAParser();
		wta = parser.parse(fileName);
	}

	@After
	public void tearDown() throws Exception {
		wta = null;
	}
	
	@Test
	public void shouldGetModifiedWTA() throws Exception {
		WTA modWTA = NBest.buildModifiedWTA(wta, new State("pa"));
		System.out.println(modWTA);
		assertNotNull(modWTA);
	}

}
