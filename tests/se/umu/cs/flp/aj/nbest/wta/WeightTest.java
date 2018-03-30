package se.umu.cs.flp.aj.nbest.wta;

import static org.junit.Assert.*;

import org.junit.Test;

import se.umu.cs.flp.aj.nbest.semiring.Weight;

public class WeightTest {

	Weight w = new Weight(2.1);

	Weight wLarger = new Weight(2.2);
	Weight wSmaller = new Weight(2.0);

	Weight inf = (Weight) (new Weight()).zero();
//	Weight ninf = new Weight(Weight.NINF);
	Weight zero = new Weight(0);

	@Test
	public void shouldBeEqual() throws Exception {
		assertTrue(w.equals(new Weight(2.1)));
	}

	@Test
	public void shouldBeSmaller() throws Exception {
		assertEquals(-1, w.compareTo(wLarger));
	}

	@Test
	public void shouldBeSmallerINF() throws Exception {
		assertEquals(-1, w.compareTo(inf));
	}

	@Test
	public void shouldBeLarger() throws Exception {
		assertEquals(1, w.compareTo(wSmaller));
	}

//	@Test
//	public void shouldBeLargerNINF() throws Exception {
//		assertEquals(1, w.compareTo(ninf));
//	}

//	@Test
//	public void shouldBeSmallerNINFComparedToINF() throws Exception {
//		assertEquals(-1, ninf.compareTo(inf));
//	}

//	@Test
//	public void shouldBeNegativeInfinity() throws Exception {
//		assertTrue(ninf.isNegativeInfinity());
//	}

	@Test
	public void shouldBeInfinity() throws Exception {
		assertTrue(inf.isZero());
	}

	@Test
	public void shouldBeZero() throws Exception {
		assertTrue(zero.isOne());
	}

	@Test
	public void shouldAddTwoNumbers() throws Exception {
		assertEquals(new Weight(2+2.1), w.mult(wSmaller));
	}

	@Test
	public void shouldAddInfinityAndGetInfinity() throws Exception {
		assertEquals(inf, w.mult(inf));
	}

}
