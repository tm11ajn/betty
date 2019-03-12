package se.umu.cs.flp.aj.heap;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.semiring.SemiringFactory;
import se.umu.cs.flp.aj.nbest.semiring.Weight;

public class BinaryHeapTest {

	BinaryHeap<String, Weight> heap = new BinaryHeap<>();
	SemiringFactory sf = new SemiringFactory();
	Semiring s = sf.getSemiring("tropical");

	@Test
	public void shouldInsert() {
		heap.add("korv", s.createWeight(0.02));
		assertThat(heap.empty(), is(false));
	}

	@Test
	public void shouldDequeueCorrectObjectForOneElement() {
		heap.add("korv", s.createWeight(0.02));
		assertThat(heap.dequeue().getObject(), is("korv"));
	}

	@Test
	public void shouldDequeueCorrectWeightForOneElement() {
		heap.add("korv", s.createWeight(0.02));
		assertThat(heap.dequeue().getWeight(), is(s.createWeight(0.02)));
	}

	@Test
	public void shouldDequeueCorrectObjectForTenElements() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		assertThat(heap.dequeue().getObject(), is("korv0"));
	}

	@Test
	public void shouldDequeueCorrectWeightForTenElements() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		assertThat(heap.dequeue().getWeight(), is(s.createWeight(0.01)));
	}

	@Test
	public void shouldDequeueCorrectSecondObjectForTenElements() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		for (int i = 0; i < 1; i++) {
			heap.dequeue();
		}
		assertThat(heap.dequeue().getObject(), is("korv1"));
	}

	@Test
	public void shouldDequeueCorrectSecondWeightForTenElements() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		for (int i = 0; i < 1; i++) {
			heap.dequeue();
		}
		assertThat(heap.dequeue().getWeight(), is(s.createWeight(0.02)));
	}

	@Test
	public void shouldDequeueCorrectThirdObjectForTenElements() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		for (int i = 0; i < 2; i++) {
			heap.dequeue();
		}
		assertThat(heap.dequeue().getObject(), is("korv2"));
	}

	@Test
	public void shouldDequeueCorrectThirdWeightForTenElements() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		for (int i = 0; i < 2; i++) {
			heap.dequeue();
		}
		assertThat(heap.dequeue().getWeight(), is(s.createWeight(0.03)));
	}

	@Test
	public void shouldDequeueCorrectFourthObjectForTenElements() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		for (int i = 0; i < 3; i++) {
			heap.dequeue();
		}
		assertThat(heap.dequeue().getObject(), is("korv3"));
	}

	@Test
	public void shouldDequeueCorrectFourthWeightForTenElements() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		for (int i = 0; i < 3; i++) {
			heap.dequeue();
		}
		assertThat(heap.dequeue().getWeight(), is(s.createWeight(0.04)));
	}

	@Test
	public void shouldDequeueCorrectFifthObjectForTenElements() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		for (int i = 0; i < 4; i++) {
			heap.dequeue();
		}
		assertThat(heap.dequeue().getObject(), is("korv4"));
	}

	@Test
	public void shouldDequeueCorrectFifthWeightForTenElements() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		for (int i = 0; i < 4; i++) {
			heap.dequeue();
		}
		assertThat(heap.dequeue().getWeight(), is(s.createWeight(0.05)));
	}

	@Test
	public void shouldDequeueCorrectSixthObjectForTenElements() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		for (int i = 0; i < 5; i++) {
			heap.dequeue();
		}
		assertThat(heap.dequeue().getObject(), is("korv5"));
	}

	@Test
	public void shouldDequeueCorrectSixthWeightForTenElements() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		for (int i = 0; i < 5; i++) {
			heap.dequeue();
		}
		assertThat(heap.dequeue().getWeight(), is(s.createWeight(0.06)));
	}

	@Test
	public void shouldDequeueCorrectSeventhObjectForTenElements() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		for (int i = 0; i < 6; i++) {
			heap.dequeue();
		}
		assertThat(heap.dequeue().getObject(), is("korv6"));
	}

	@Test
	public void shouldDequeueCorrectSeventhWeightForTenElements() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		for (int i = 0; i < 6; i++) {
			heap.dequeue();
		}
		assertThat(heap.dequeue().getWeight(), is(s.createWeight(0.07)));
	}

	@Test
	public void shouldDequeueCorrectEighthObjectForTenElements() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		for (int i = 0; i < 7; i++) {
			heap.dequeue();
		}
		assertThat(heap.dequeue().getObject(), is("korv7"));
	}

	@Test
	public void shouldDequeueCorrectEighthWeightForTenElements() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		for (int i = 0; i < 7; i++) {
			heap.dequeue();
		}
		assertThat(heap.dequeue().getWeight(), is(s.createWeight(0.08)));
	}

	@Test
	public void shouldDequeueCorrectNinethObjectForTenElements() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		for (int i = 0; i < 8; i++) {
			heap.dequeue();
		}
		assertThat(heap.dequeue().getObject(), is("korv8"));
	}

	@Test
	public void shouldDequeueCorrectNinethWeightForTenElements() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		for (int i = 0; i < 8; i++) {
			heap.dequeue();
		}
		assertThat(heap.dequeue().getWeight(), is(s.createWeight(0.09)));
	}

	@Test
	public void shouldDequeueCorrectTenthObjectForTenElements() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		for (int i = 0; i < 9; i++) {
			heap.dequeue();
		}
		assertThat(heap.dequeue().getObject(), is("korv9"));
	}

	@Test
	public void shouldDequeueCorrectTenthWeightForTenElements() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		for (int i = 0; i < 9; i++) {
			heap.dequeue();
		}
		assertThat(heap.dequeue().getWeight(), is(s.createWeight(0.1)));
	}

	@Test
	public void shouldDequeueCorrectObjectForTenElementsAfterDecreasingOneWeight() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		heap.decreaseWeight("korv9", s.createWeight(0.005));
		assertThat(heap.dequeue().getObject(), is("korv9"));
	}

	@Test
	public void shouldDequeueCorrectWeightForTenElementsAfterDecreasingOneWeight() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		heap.decreaseWeight("korv9", s.createWeight(0.005));
		assertThat(heap.dequeue().getWeight(), is(s.createWeight(0.005)));
	}

	@Test
	public void shouldDequeueCorrectObjectForTenElementsAfterDecreasingAllWeights() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		heap.decreaseWeight("korv9", s.createWeight(0.010));
		heap.decreaseWeight("korv8", s.createWeight(0.009));
		heap.decreaseWeight("korv7", s.createWeight(0.008));
		heap.decreaseWeight("korv6", s.createWeight(0.007));
		heap.decreaseWeight("korv5", s.createWeight(0.006));
		heap.decreaseWeight("korv4", s.createWeight(0.005));
		heap.decreaseWeight("korv3", s.createWeight(0.004));
		heap.decreaseWeight("korv2", s.createWeight(0.003));
		heap.decreaseWeight("korv1", s.createWeight(0.002));
		heap.decreaseWeight("korv0", s.createWeight(0.001));
		assertThat(heap.dequeue().getObject(), is("korv0"));
	}

	@Test
	public void shouldDequeueCorrectWeightForTenElementsAfterDecreasingAllWeights() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		heap.decreaseWeight("korv0", s.createWeight(0.001));
		heap.decreaseWeight("korv1", s.createWeight(0.002));
		heap.decreaseWeight("korv2", s.createWeight(0.003));
		heap.decreaseWeight("korv3", s.createWeight(0.004));
		heap.decreaseWeight("korv4", s.createWeight(0.005));
		heap.decreaseWeight("korv5", s.createWeight(0.006));
		heap.decreaseWeight("korv6", s.createWeight(0.007));
		heap.decreaseWeight("korv7", s.createWeight(0.008));
		heap.decreaseWeight("korv8", s.createWeight(0.009));
		heap.decreaseWeight("korv9", s.createWeight(0.010));
		assertThat(heap.dequeue().getWeight(), is(s.createWeight(0.001)));
	}

	@Test
	public void shouldDequeueCorrectLastObjectForTenElementsAfterDecreasingAllWeights() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		heap.decreaseWeight("korv9", s.createWeight(0.010));
		heap.decreaseWeight("korv8", s.createWeight(0.009));
		heap.decreaseWeight("korv7", s.createWeight(0.008));
		heap.decreaseWeight("korv6", s.createWeight(0.007));
		heap.decreaseWeight("korv5", s.createWeight(0.006));
		heap.decreaseWeight("korv4", s.createWeight(0.005));
		heap.decreaseWeight("korv3", s.createWeight(0.004));
		heap.decreaseWeight("korv2", s.createWeight(0.003));
		heap.decreaseWeight("korv1", s.createWeight(0.002));
		heap.decreaseWeight("korv0", s.createWeight(0.001));
		for (int i = 0; i < 9; i++) {
			heap.dequeue();
		}
		assertThat(heap.dequeue().getObject(), is("korv9"));
	}

	@Test
	public void shouldDequeueCorrectLastWeightForTenElementsAfterDecreasingAllWeights() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		heap.decreaseWeight("korv0", s.createWeight(0.001));
		heap.decreaseWeight("korv1", s.createWeight(0.002));
		heap.decreaseWeight("korv2", s.createWeight(0.003));
		heap.decreaseWeight("korv3", s.createWeight(0.004));
		heap.decreaseWeight("korv4", s.createWeight(0.005));
		heap.decreaseWeight("korv5", s.createWeight(0.006));
		heap.decreaseWeight("korv6", s.createWeight(0.007));
		heap.decreaseWeight("korv7", s.createWeight(0.008));
		heap.decreaseWeight("korv8", s.createWeight(0.009));
		heap.decreaseWeight("korv9", s.createWeight(0.010));
		for (int i = 0; i < 9; i++) {
			heap.dequeue();
		}
		assertThat(heap.dequeue().getWeight(), is(s.createWeight(0.01)));
	}

	@Test
	public void shouldBeEmptyAfterEnqueueingAndDequeueingTenElements() {
		heap.add("korv9", s.createWeight(0.10));
		heap.add("korv8", s.createWeight(0.09));
		heap.add("korv7", s.createWeight(0.08));
		heap.add("korv6", s.createWeight(0.07));
		heap.add("korv5", s.createWeight(0.06));
		heap.add("korv4", s.createWeight(0.05));
		heap.add("korv3", s.createWeight(0.04));
		heap.add("korv2", s.createWeight(0.03));
		heap.add("korv1", s.createWeight(0.02));
		heap.add("korv0", s.createWeight(0.01));
		for (int i = 0; i < 10; i++) {
			heap.dequeue();
		}
		assertThat(heap.empty(), is(true));
	}
}
