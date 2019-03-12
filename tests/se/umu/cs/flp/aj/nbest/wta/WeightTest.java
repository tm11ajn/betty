package se.umu.cs.flp.aj.nbest.wta;

import static org.junit.Assert.*;

import org.junit.Test;

import se.umu.cs.flp.aj.nbest.semiring.TropicalSemiring;
import se.umu.cs.flp.aj.nbest.semiring.Weight;

public class WeightTest {

	TropicalSemiring semiring = new TropicalSemiring();

	Weight w = semiring.createWeight(2.1);

	Weight wLarger = semiring.createWeight(2.2);
	Weight wSmaller = semiring.createWeight(2.0);

	Weight inf = semiring.zero();
//	Weight ninf = new Weight(Weight.NINF);
	Weight zero = semiring.one();

	@Test
	public void shouldBeEqual() throws Exception {
		assertTrue(w.equals(semiring.createWeight(2.1)));
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
		assertEquals(semiring.createWeight(2+2.1), w.mult(wSmaller));
	}

	@Test
	public void shouldAddInfinityAndGetInfinity() throws Exception {
		assertEquals(inf, w.mult(inf));
	}

}
