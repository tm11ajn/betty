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

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		lq = new LazyLimitedLadderQueue<Integer>(0, comp, limit);
	}

	@After
	public void tearDown() throws Exception {
	}

	private void initLadderQueue(int rank) {
		this.lq = new LazyLimitedLadderQueue<>(rank, comp, limit);
	}

	@Test
	public void testIsNotEmptyRank0() {
		initLadderQueue(0);
		assertFalse(lq.isEmpty());
	}

	@Test
	public void testIsEmptyRank1() {
		initLadderQueue(1);
		assertTrue(lq.isEmpty());
	}

	@Test
	public void testIsNotEmptyRank1() {
		initLadderQueue(1);
		lq.addLast(0, 3);
		assertFalse(lq.isEmpty());
	}

	@Test
	public void testIsEmptyRank2() {
		initLadderQueue(2);
		assertTrue(lq.isEmpty());
	}

	@Test
	public void test2IsEmptyRank2() {
		initLadderQueue(2);
		lq.addLast(1, 3);
		assertTrue(lq.isEmpty());
	}

	@Test
	public void testIsNotEmptyRank2() {
		initLadderQueue(2);
		lq.addLast(0, 3);
		lq.addLast(1, 3);
		assertFalse(lq.isEmpty());
	}

	@Test
	public void testIsEmptyRank3() {
		initLadderQueue(3);
		assertTrue(lq.isEmpty());
	}

	@Test
	public void test2IsEmptyRank3() {
		initLadderQueue(3);
		lq.addLast(0, 3);
		lq.addLast(0, 3);
		assertTrue(lq.isEmpty());
	}

	@Test
	public void test3IsEmptyRank3() {
		initLadderQueue(3);
		lq.addLast(1, 3);
		lq.addLast(2, 3);
		lq.addLast(2, 3);
		assertTrue(lq.isEmpty());
	}

	@Test
	public void testIsNotEmptyRank3() {
		initLadderQueue(3);
		lq.addLast(1, 3);
		lq.addLast(2, 3);
		lq.addLast(0, 3);
		assertFalse(lq.isEmpty());
	}

	@Test
	public void testHasNextRank0() {
		initLadderQueue(0);
		assertTrue(lq.hasNext());
	}

	@Test
	public void testHasNextRank1() {
		initLadderQueue(1);
		lq.addLast(0, 4);
		assertTrue(lq.hasNext());
	}

	@Test
	public void testHasNotNextRank1() {
		initLadderQueue(1);
		lq.addLast(0, 4);
		lq.dequeue();
		assertFalse(lq.hasNext());
	}

	@Test
	public void testHasNextRank2() {
		initLadderQueue(2);
		lq.addLast(0, 6);
		lq.addLast(1, 4);
		assertTrue(lq.hasNext());
	}

	@Test
	public void testHasNotNextRank2() {
		initLadderQueue(2);
		lq.addLast(1, 4);
		lq.addLast(1, 5);
		assertFalse(lq.hasNext());
	}

	@Test
	public void testHasNextRank3() {
		initLadderQueue(3);
		lq.addLast(1, 4);
		lq.addLast(2, 5);
		lq.addLast(0, 6);
		assertTrue(lq.hasNext());
	}

	@Test
	public void testHasNotNextRank3() {
		initLadderQueue(3);
		lq.addLast(1, 4);
		lq.addLast(2, 5);
		lq.addLast(2, 8);
		assertFalse(lq.hasNext());
	}

	@Test
	public void testDequeueRank0() {
		initLadderQueue(0);
		res = new ArrayList<>();
		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void testDequeueRank1() {
		initLadderQueue(1);
		lq.addLast(0, 2);
		res = new ArrayList<>();
		res.add(2);
		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void testDequeueRank2() {
		initLadderQueue(2);
		lq.addLast(0, 1);
		lq.addLast(1, 2);
		res = new ArrayList<>();
		res.add(1);
		res.add(2);
		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void testDequeueRank3() {
		initLadderQueue(3);
		lq.addLast(0, 1);
		lq.addLast(1, 2);
		lq.addLast(2, 3);
		res = new ArrayList<>();
		res.add(1);
		res.add(2);
		res.add(3);
		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void testHasNotNextAfterDequeueRank0() {
		initLadderQueue(0);
		lq.dequeue();
		assertFalse(lq.hasNext());
	}

	@Test
	public void testHasNextAfterDequeueRank3() {
		initLadderQueue(3);
		lq.addLast(1, 4);
		lq.addLast(2, 5);
		lq.addLast(0, 6);
		lq.addLast(1, 4);
		lq.addLast(2, 5);
		lq.addLast(0, 6);
		lq.dequeue();
		assertTrue(lq.hasNext());
	}

	@Test
	public void testHasNotNextAfterDequeueRank3() {
		initLadderQueue(3);
		lq.addLast(1, 4);
		lq.addLast(2, 5);
		lq.addLast(0, 6);
		lq.dequeue();
		assertFalse(lq.hasNext());
	}

	@Test
	public void testCorrectValue1() {
		initLadderQueue(3);
		lq.addLast(0, 1);
		lq.addLast(0, 2);
		lq.addLast(0, 3);
		lq.addLast(1, 4);
		lq.addLast(1, 5);
		lq.addLast(1, 6);
		lq.addLast(2, 7);
		lq.addLast(2, 8);
		lq.addLast(2, 9);

		res = new ArrayList<>();
		res.add(1);
		res.add(4);
		res.add(7);

		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void testCorrectValue2() {
		initLadderQueue(3);
		lq.addLast(0, 1);
		lq.addLast(0, 2);
		lq.addLast(0, 3);
		lq.addLast(1, 4);
		lq.addLast(1, 5);
		lq.addLast(1, 6);
		lq.addLast(2, 7);
		lq.addLast(2, 8);
		lq.addLast(2, 9);
		lq.dequeue();

		res = new ArrayList<>();
		res.add(2);
		res.add(4);
		res.add(7);

		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void testCorrectValue3() {
		initLadderQueue(3);
		lq.addLast(0, 1);
		lq.addLast(0, 2);
		lq.addLast(0, 3);
		lq.addLast(1, 4);
		lq.addLast(1, 5);
		lq.addLast(1, 6);
		lq.addLast(2, 7);
		lq.addLast(2, 8);
		lq.addLast(2, 9);
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
		initLadderQueue(3);
		lq.addLast(0, 1);
		lq.addLast(0, 2);
		lq.addLast(0, 3);
		lq.addLast(1, 4);
		lq.addLast(1, 5);
		lq.addLast(1, 6);
		lq.addLast(2, 7);
		lq.addLast(2, 8);
		lq.addLast(2, 9);
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
		initLadderQueue(3);
		lq.addLast(0, 1);
		lq.addLast(0, 1);
		lq.addLast(0, 11);
		lq.addLast(1, 2);
		lq.addLast(1, 2);
		lq.addLast(1, 12);
		lq.addLast(2, 3);
		lq.addLast(2, 3);
		lq.addLast(2, 13);
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
		initLadderQueue(3);
		lq.addLast(0, 1);
		lq.addLast(0, 1);
		lq.addLast(0, 11);
		lq.addLast(1, 2);
		lq.addLast(1, 2);
		lq.addLast(1, 12);
		lq.addLast(2, 3);
		lq.addLast(2, 3);
		lq.addLast(2, 13);
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
		initLadderQueue(3);
		lq.addLast(0, 1);
		lq.addLast(0, 1);
		lq.addLast(0, 11);
		lq.addLast(1, 2);
		lq.addLast(1, 2);
		lq.addLast(1, 12);
		lq.addLast(2, 3);
		lq.addLast(2, 3);
		lq.addLast(2, 13);
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
		initLadderQueue(3);
		lq.addLast(0, 1);
		lq.addLast(0, 1);
		lq.addLast(0, 11);
		lq.addLast(1, 2);
		lq.addLast(1, 2);
		lq.addLast(1, 12);
		lq.addLast(2, 3);
		lq.addLast(2, 3);
		lq.addLast(2, 13);
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
		initLadderQueue(3);
		lq.addLast(0, 1);
		lq.addLast(0, 1);
		lq.addLast(0, 30);
		lq.addLast(1, 2);
		lq.addLast(1, 2);
		lq.addLast(1, 20);
		lq.addLast(2, 3);
		lq.addLast(2, 3);
		lq.addLast(2, 10);
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
