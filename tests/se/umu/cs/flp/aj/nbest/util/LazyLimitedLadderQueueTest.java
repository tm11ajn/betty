/*
 * Copyright 2018 Anna Jonsson for the research group Foundations of Language
 * Processing, Department of Computing Science, Umeå university
 *
 * This file is part of BestTrees.
 *
 * BestTrees is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BestTrees is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BestTrees.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.umu.cs.flp.aj.nbest.util;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.umu.cs.flp.aj.nbest.util.LazyLimitedLadderQueue.Configuration;


public class LazyLimitedLadderQueueTest {

	private static Comparator<Configuration<Integer>> comp =
			new Comparator<Configuration<Integer>>() {

		@Override
		public int compare(Configuration<Integer> arg0, Configuration<Integer> arg1) {

			Integer sum0 = 0;
			Integer sum1 = 0;

			for (int val : arg0.getValues()) {
				sum0 += val;
			}

			for (int val : arg1.getValues()) {
				sum1 += val;
			}

			return sum0.compareTo(sum1);
		}
	};

	private LazyLimitedLadderQueue<Integer> lq;
	private ArrayList<Integer> res;
	private int limit = 10;
	private ArrayList<LinkedList<Integer>> elements;
	private ArrayList<Integer> elementIndices;

//	@Before
//	public void setUp() throws Exception {
//		lq = new LazyLimitedLadderQueue<Integer>(0, comp, limit);
//	}

	@After
	public void tearDown() throws Exception {
	}

	private void init(int rank) {
		elements = new ArrayList<>();
		elementIndices = new ArrayList<>();
		for (int i = 0; i < rank; i++) {
			elements.add(new LinkedList<>());
			elementIndices.add(i);
		}
		this.lq = new LazyLimitedLadderQueue<>(rank, elements, elementIndices,
				comp, limit);
	}

	@Test
	public void testIsNotEmptyRank0() {
		init(0);
		assertFalse(lq.isEmpty());
	}

	@Test
	public void testIsEmptyRank1() {
		init(1);
		assertTrue(lq.isEmpty());
	}

	@Test
	public void testIsNotEmptyRank1() {
		init(1);
		elements.get(0).add(3);
		lq.update(0);
		assertFalse(lq.isEmpty());
	}

	@Test
	public void testIsEmptyRank2() {
		init(2);
		assertTrue(lq.isEmpty());
	}

	@Test
	public void test2IsEmptyRank2() {
		init(2);
		elements.get(1).add(3);
		lq.update(1);
		assertTrue(lq.isEmpty());
	}

	@Test
	public void testIsNotEmptyRank2() {
		init(2);
		elements.get(0).add(3);
		lq.update(0);
		elements.get(1).add(3);
		lq.update(1);
		assertFalse(lq.isEmpty());
	}

	@Test
	public void testIsEmptyRank3() {
		init(3);
		assertTrue(lq.isEmpty());
	}

	@Test
	public void test2IsEmptyRank3() {
		init(3);
		elements.get(0).add(3);
		lq.update(0);
		elements.get(0).add(3);
		lq.update(0);
		assertTrue(lq.isEmpty());
	}

	@Test
	public void test3IsEmptyRank3() {
		init(3);
		elements.get(1).add(3);
		lq.update(1);
		elements.get(2).add(3);
		lq.update(2);
		elements.get(2).add(3);
		lq.update(2);
		assertTrue(lq.isEmpty());
	}

	@Test
	public void testIsNotEmptyRank3() {
		init(3);
		elements.get(1).add(3);
		lq.update(1);
		elements.get(2).add(3);
		lq.update(2);
		elements.get(0).add(3);
		lq.update(0);
		assertFalse(lq.isEmpty());
	}

	@Test
	public void testHasNextRank0() {
		init(0);
		assertTrue(lq.hasNext());
	}

	@Test
	public void testHasNextRank1() {
		init(1);
		elements.get(0).add(4);
		lq.update(0);
		assertTrue(lq.hasNext());
	}

	@Test
	public void testHasNotNextRank1() {
		init(1);
		elements.get(0).add(4);
		lq.update(0);
		lq.dequeue();
		assertFalse(lq.hasNext());
	}

	@Test
	public void testHasNextRank2() {
		init(2);
		elements.get(0).add(6);
		lq.update(0);
		elements.get(1).add(4);
		lq.update(1);
		assertTrue(lq.hasNext());
	}

	@Test
	public void testHasNotNextRank2() {
		init(2);
		elements.get(1).add(4);
		lq.update(1);
		elements.get(1).add(5);
		lq.update(1);
		assertFalse(lq.hasNext());
	}

	@Test
	public void testHasNextRank3() {
		init(3);
		elements.get(1).add(4);
		lq.update(1);
		elements.get(2).add(5);
		lq.update(2);
		elements.get(0).add(6);
		lq.update(0);
		assertTrue(lq.hasNext());
	}

	@Test
	public void testHasNotNextRank3() {
		init(3);
		elements.get(1).add(8);
		lq.update(1);
		elements.get(2).add(8);
		lq.update(2);
		elements.get(2).add(8);
		lq.update(2);
		assertFalse(lq.hasNext());
	}

	@Test
	public void testDequeueRank0() {
		init(0);
		res = new ArrayList<>();
		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void testDequeueRank1() {
		init(1);
		elements.get(0).add(2);
		lq.update(0);
		res = new ArrayList<>();
		res.add(2);
		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void testDequeueRank2() {
		init(2);
		elements.get(0).add(1);
		lq.update(0);
		elements.get(1).add(2);
		lq.update(1);
		res = new ArrayList<>();
		res.add(1);
		res.add(2);
		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void testDequeueRank3() {
		init(3);
		elements.get(0).add(1);
		lq.update(0);
		elements.get(1).add(2);
		lq.update(1);
		elements.get(2).add(3);
		lq.update(2);
		res = new ArrayList<>();
		res.add(1);
		res.add(2);
		res.add(3);
		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void testHasNotNextAfterDequeueRank0() {
		init(0);
		lq.dequeue();
		assertFalse(lq.hasNext());
	}

	@Test
	public void testHasNextAfterDequeueRank3() {
		init(3);
		elements.get(1).add(4);
		lq.update(1);
		elements.get(2).add(5);
		lq.update(2);
		elements.get(0).add(6);
		lq.update(0);
		elements.get(1).add(4);
		lq.update(1);
		elements.get(2).add(5);
		lq.update(2);
		elements.get(0).add(6);
		lq.update(0);
		lq.dequeue();
		assertTrue(lq.hasNext());
	}

	@Test
	public void testHasNotNextAfterDequeueRank3() {
		init(3);
		elements.get(1).add(4);
		lq.update(1);
		elements.get(2).add(5);
		lq.update(2);
		elements.get(0).add(6);
		lq.update(0);
		lq.dequeue();
		assertFalse(lq.hasNext());
	}

	@Test
	public void testCorrectValue1() {
		init(3);
		elements.get(0).add(1);
		lq.update(0);
		elements.get(0).add(2);
		lq.update(0);
		elements.get(0).add(3);
		lq.update(0);
		elements.get(1).add(4);
		lq.update(1);
		elements.get(1).add(5);
		lq.update(1);
		elements.get(1).add(6);
		lq.update(1);
		elements.get(2).add(7);
		lq.update(2);
		elements.get(2).add(8);
		lq.update(2);
		elements.get(2).add(9);
		lq.update(2);

		res = new ArrayList<>();
		res.add(1);
		res.add(4);
		res.add(7);

		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void testCorrectValue2() {
		init(3);
		elements.get(0).add(1);
		lq.update(0);
		elements.get(0).add(2);
		lq.update(0);
		elements.get(0).add(3);
		lq.update(0);
		elements.get(1).add(4);
		lq.update(1);
		elements.get(1).add(5);
		lq.update(1);
		elements.get(1).add(6);
		lq.update(1);
		elements.get(2).add(7);
		lq.update(2);
		elements.get(2).add(8);
		lq.update(2);
		elements.get(2).add(9);
		lq.update(2);

		lq.dequeue();

		res = new ArrayList<>();
		res.add(2);
		res.add(4);
		res.add(7);

		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void testCorrectValue3() {
		init(3);
		elements.get(0).add(1);
		lq.update(0);
		elements.get(0).add(2);
		lq.update(0);
		elements.get(0).add(3);
		lq.update(0);
		elements.get(1).add(4);
		lq.update(1);
		elements.get(1).add(5);
		lq.update(1);
		elements.get(1).add(6);
		lq.update(1);
		elements.get(2).add(7);
		lq.update(2);
		elements.get(2).add(8);
		lq.update(2);
		elements.get(2).add(9);
		lq.update(2);

		lq.dequeue();
		lq.dequeue();

		res = new ArrayList<>();
		res.add(1);
		res.add(4);
		res.add(8);

		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void testCorrectValue4() {
		init(3);
		elements.get(0).add(1);
		lq.update(0);
		elements.get(0).add(2);
		lq.update(0);
		elements.get(0).add(3);
		lq.update(0);
		elements.get(1).add(4);
		lq.update(1);
		elements.get(1).add(5);
		lq.update(1);
		elements.get(1).add(6);
		lq.update(1);
		elements.get(2).add(7);
		lq.update(2);
		elements.get(2).add(8);
		lq.update(2);
		elements.get(2).add(9);
		lq.update(2);

		lq.dequeue();
		lq.dequeue();
		lq.dequeue();

		res = new ArrayList<>();
		res.add(1);
		res.add(5);
		res.add(7);

		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void test2CorrectValue1() {
		init(3);
		elements.get(0).add(1);
		lq.update(0);
		elements.get(0).add(1);
		lq.update(0);
		elements.get(0).add(11);
		lq.update(0);
		elements.get(1).add(2);
		lq.update(1);
		elements.get(1).add(2);
		lq.update(1);
		elements.get(1).add(12);
		lq.update(1);
		elements.get(2).add(3);
		lq.update(2);
		elements.get(2).add(3);
		lq.update(2);
		elements.get(2).add(13);
		lq.update(2);

		lq.dequeue();
		lq.dequeue();
		lq.dequeue();

		res = new ArrayList<>();
		res.add(1);
		res.add(2);
		res.add(3);

		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void test2CorrectValue2() {
		init(3);
		elements.get(0).add(1);
		lq.update(0);
		elements.get(0).add(1);
		lq.update(0);
		elements.get(0).add(11);
		lq.update(0);
		elements.get(1).add(2);
		lq.update(1);
		elements.get(1).add(2);
		lq.update(1);
		elements.get(1).add(12);
		lq.update(1);
		elements.get(2).add(3);
		lq.update(2);
		elements.get(2).add(3);
		lq.update(2);
		elements.get(2).add(13);
		lq.update(2);

		lq.dequeue();
		lq.dequeue();
		lq.dequeue();

		res = new ArrayList<>();
		res.add(1);
		res.add(2);
		res.add(3);

		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void test2CorrectValue3() {
		init(3);
		elements.get(0).add(1);
		lq.update(0);
		elements.get(0).add(1);
		lq.update(0);
		elements.get(0).add(11);
		lq.update(0);
		elements.get(1).add(2);
		lq.update(1);
		elements.get(1).add(2);
		lq.update(1);
		elements.get(1).add(12);
		lq.update(1);
		elements.get(2).add(3);
		lq.update(2);
		elements.get(2).add(3);
		lq.update(2);
		elements.get(2).add(13);
		lq.update(2);

		lq.dequeue();
		lq.dequeue();
		lq.dequeue();

		res = new ArrayList<>();
		res.add(1);
		res.add(2);
		res.add(3);

		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void test2CorrectValue4() {
		init(3);
		elements.get(0).add(1);
		lq.update(0);
		elements.get(0).add(1);
		lq.update(0);
		elements.get(0).add(11);
		lq.update(0);
		elements.get(1).add(2);
		lq.update(1);
		elements.get(1).add(2);
		lq.update(1);
		elements.get(1).add(12);
		lq.update(1);
		elements.get(2).add(3);
		lq.update(2);
		elements.get(2).add(3);
		lq.update(2);
		elements.get(2).add(13);
		lq.update(2);

		lq.dequeue();
		lq.dequeue();
		lq.dequeue();

		res = new ArrayList<>();
		res.add(1);
		res.add(2);
		res.add(3);

		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void test2CorrectValue9() {
		init(3);
		elements.get(0).add(1);
		lq.update(0);
		elements.get(0).add(1);
		lq.update(0);
		elements.get(0).add(30);
		lq.update(0);
		elements.get(1).add(2);
		lq.update(1);
		elements.get(1).add(2);
		lq.update(1);
		elements.get(1).add(20);
		lq.update(1);
		elements.get(2).add(3);
		lq.update(2);
		elements.get(2).add(3);
		lq.update(2);
		elements.get(2).add(10);
		lq.update(2);

		lq.dequeue();
		lq.dequeue();
		lq.dequeue();
		lq.dequeue();
		lq.dequeue();
		lq.dequeue();
		lq.dequeue();
		lq.dequeue();

		res = new ArrayList<>();
		res.add(1);
		res.add(2);
		res.add(10);

		assertThat(lq.dequeue(), is(res));
	}

}
