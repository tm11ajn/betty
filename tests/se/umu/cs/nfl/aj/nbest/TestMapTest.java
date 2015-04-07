package se.umu.cs.nfl.aj.nbest;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestMapTest {

	private Map<String, Map<String, String>> testMap =
			new HashMap<String, Map<String, String>>();

	@Before
	public void setUp() throws Exception {

		HashMap<String, String> firstMap = new HashMap<>();
		HashMap<String, String> secondMap = new HashMap<>();
		HashMap<String, String> thirdMap = new HashMap<>();

		firstMap.put("ffirst", "value11");
		firstMap.put("fsecond", "value12");
		firstMap.put("fthird", "value13");

		secondMap.put("sfirst", "value21");
		secondMap.put("ssecond", "value22");
		secondMap.put("sthird", "value23");

		thirdMap.put("tfirst", "value31");
		thirdMap.put("tsecond", "value32");
		thirdMap.put("tthird", "value33");

		testMap.put("first", firstMap);
		testMap.put("second", secondMap);
		testMap.put("third", thirdMap);
	}

	@After
	public void tearDown() throws Exception {
		testMap = null;
	}

	@Test
	public void getTest() {
		assertEquals("value22", testMap.get("second").get("ssecond"));
	}

	@Test
	public void removeTest1() {
		testMap.get("second").remove("ssecond");
		assertNull(testMap.get("second").get("ssecond"));
	}

	@Test
	public void removeTest2() {
		testMap.get("second").remove("ssecond");
		assertEquals("value23", testMap.get("second").get("sthird"));
	}

}
