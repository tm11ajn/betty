package se.umu.cs.flp.aj.nbest;

import static org.junit.Assert.*;

import java.util.PriorityQueue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.umu.cs.flp.aj.nbest.semiring.Weight;

public class BestTreesTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		PriorityQueue<Weight> p = new PriorityQueue<>();
		p.add(new Weight(3));
		p.add(new Weight(1));
		p.add(new Weight(2));
		p.add(new Weight(4));
		p.add(new Weight(2.5));
		
		System.out.println(p);
		
		while (!p.isEmpty()) {
			System.out.println(p.poll());
		}
		
		PriorityQueue<Double> p2 = new PriorityQueue<>();
		p2.add(3.0);
		p2.add(1.0);
		p2.add(2.0);
		p2.add(4.0);
		p2.add(2.5);
		
		System.out.println(p2);
		
		while (!p2.isEmpty()) {
			System.out.println(p2.poll());
		}
		
		assertNull(p.poll());
	}

}
