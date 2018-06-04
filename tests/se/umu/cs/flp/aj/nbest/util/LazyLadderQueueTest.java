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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.semiring.SemiringFactory;
import se.umu.cs.flp.aj.nbest.semiring.Weight;


public class LazyLadderQueueTest {

	public class Integer2 implements Weighted {
		public Weight weight;

		public Integer2(int i) {
			SemiringFactory sf = new SemiringFactory();
			Semiring s = sf.getSemiring("tropcial");
			s.createWeight((double)i);
		}

		@Override
		public Weight getWeight() {
			return weight;
		}
	}

	private LazyLadderQueue<Integer2> lq;
	private ArrayList<Integer2> res;
	private Integer2 i1 = new Integer2(1);
	private Integer2 i2 = new Integer2(2);
	private Integer2 i3 = new Integer2(3);
	private Integer2 i4 = new Integer2(4);
	private Integer2 i5 = new Integer2(5);
	private Integer2 i6 = new Integer2(6);
	private Integer2 i7 = new Integer2(7);
	private Integer2 i8 = new Integer2(8);
	private Integer2 i9 = new Integer2(9);
	private Integer2 i10 = new Integer2(10);
	private Integer2 i11 = new Integer2(11);
	private Integer2 i12 = new Integer2(12);
	private Integer2 i13 = new Integer2(13);
	private Integer2 i20 = new Integer2(20);
	private Integer2 i30 = new Integer2(30);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		lq = new LazyLadderQueue<Integer2>(0);
	}

	@After
	public void tearDown() throws Exception {
	}

	private void initLadderQueue(int rank) {
		this.lq = new LazyLadderQueue<>(rank);
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
		lq.addLast(0, i3);
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
		lq.addLast(1, i3);
		assertTrue(lq.isEmpty());
	}

	@Test
	public void testIsNotEmptyRank2() {
		initLadderQueue(2);
		lq.addLast(0, i3);
		lq.addLast(1, i3);
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
		lq.addLast(0, i3);
		lq.addLast(0, i3);
		assertTrue(lq.isEmpty());
	}

	@Test
	public void test3IsEmptyRank3() {
		initLadderQueue(3);
		lq.addLast(1, i3);
		lq.addLast(2, i3);
		lq.addLast(2, i3);
		assertTrue(lq.isEmpty());
	}

	@Test
	public void testIsNotEmptyRank3() {
		initLadderQueue(3);
		lq.addLast(1, i3);
		lq.addLast(2, i3);
		lq.addLast(0, i3);
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
		lq.addLast(0, i4);
		assertTrue(lq.hasNext());
	}

	@Test
	public void testHasNotNextRank1() {
		initLadderQueue(1);
		lq.addLast(0, i4);
		lq.dequeue();
		assertFalse(lq.hasNext());
	}

	@Test
	public void testHasNextRank2() {
		initLadderQueue(2);
		lq.addLast(0, i6);
		lq.addLast(1, i4);
		assertTrue(lq.hasNext());
	}

	@Test
	public void testHasNotNextRank2() {
		initLadderQueue(2);
		lq.addLast(1, i4);
		lq.addLast(1, i5);
		assertFalse(lq.hasNext());
	}

	@Test
	public void testHasNextRank3() {
		initLadderQueue(3);
		lq.addLast(1, i4);
		lq.addLast(2, i5);
		lq.addLast(0, i6);
		assertTrue(lq.hasNext());
	}

	@Test
	public void testHasNotNextRank3() {
		initLadderQueue(3);
		lq.addLast(1, i4);
		lq.addLast(2, i5);
		lq.addLast(2, i8);
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
		lq.addLast(0, i2);
		res = new ArrayList<>();
		res.add(i2);
		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void testDequeueRank2() {
		initLadderQueue(2);
		lq.addLast(0, i1);
		lq.addLast(1, i2);
		res = new ArrayList<>();
		res.add(i1);
		res.add(i2);
		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void testDequeueRank3() {
		initLadderQueue(3);
		lq.addLast(0, i1);
		lq.addLast(1, i2);
		lq.addLast(2, i3);
		res = new ArrayList<>();
		res.add(i1);
		res.add(i2);
		res.add(i3);
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
		lq.addLast(1, i4);
		lq.addLast(2, i5);
		lq.addLast(0, i6);
		lq.addLast(1, i4);
		lq.addLast(2, i5);
		lq.addLast(0, i6);
		lq.dequeue();
		assertTrue(lq.hasNext());
	}

	@Test
	public void testHasNotNextAfterDequeueRank3() {
		initLadderQueue(3);
		lq.addLast(1, i4);
		lq.addLast(2, i5);
		lq.addLast(0, i6);
		lq.dequeue();
		assertFalse(lq.hasNext());
	}

	@Test
	public void testCorrectValue1() {
		initLadderQueue(3);
		lq.addLast(0, i1);
		lq.addLast(0, i2);
		lq.addLast(0, i3);
		lq.addLast(1, i4);
		lq.addLast(1, i5);
		lq.addLast(1, i6);
		lq.addLast(2, i7);
		lq.addLast(2, i8);
		lq.addLast(2, i9);

		res = new ArrayList<>();
		res.add(i1);
		res.add(i4);
		res.add(i7);

		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void testCorrectValue2() {
		initLadderQueue(3);
		lq.addLast(0, i1);
		lq.addLast(0, i2);
		lq.addLast(0, i3);
		lq.addLast(1, i4);
		lq.addLast(1, i5);
		lq.addLast(1, i6);
		lq.addLast(2, i7);
		lq.addLast(2, i8);
		lq.addLast(2, i9);
		lq.dequeue();

		res = new ArrayList<>();
		res.add(i2);
		res.add(i4);
		res.add(i7);

		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void testCorrectValue3() {
		initLadderQueue(3);
		lq.addLast(0, i1);
		lq.addLast(0, i2);
		lq.addLast(0, i3);
		lq.addLast(1, i4);
		lq.addLast(1, i5);
		lq.addLast(1, i6);
		lq.addLast(2, i7);
		lq.addLast(2, i8);
		lq.addLast(2, i9);
		lq.dequeue();
		lq.dequeue();

		res = new ArrayList<>();
		res.add(i1);
		res.add(i5);
		res.add(i7);

		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void testCorrectValue4() {
		initLadderQueue(3);
		lq.addLast(0, i1);
		lq.addLast(0, i2);
		lq.addLast(0, i3);
		lq.addLast(1, i4);
		lq.addLast(1, i5);
		lq.addLast(1, i6);
		lq.addLast(2, i7);
		lq.addLast(2, i8);
		lq.addLast(2, i9);
		lq.dequeue();
		lq.dequeue();
		lq.dequeue();

		res = new ArrayList<>();
		res.add(i1);
		res.add(i4);
		res.add(i8);

		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void test2CorrectValue1() {
		initLadderQueue(3);
		lq.addLast(0, i1);
		lq.addLast(0, i1);
		lq.addLast(0, i11);
		lq.addLast(1, i2);
		lq.addLast(1, i2);
		lq.addLast(1, i12);
		lq.addLast(2, i3);
		lq.addLast(2, i3);
		lq.addLast(2, i13);
		lq.dequeue();
		lq.dequeue();
		lq.dequeue();

		res = new ArrayList<>();
		res.add(i1);
		res.add(i2);
		res.add(i3);

		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void test2CorrectValue2() {
		initLadderQueue(3);
		lq.addLast(0, i1);
		lq.addLast(0, i1);
		lq.addLast(0, i11);
		lq.addLast(1, i2);
		lq.addLast(1, i2);
		lq.addLast(1, i12);
		lq.addLast(2, i3);
		lq.addLast(2, i3);
		lq.addLast(2, i13);
		lq.dequeue();
		lq.dequeue();
		lq.dequeue();

		res = new ArrayList<>();
		res.add(i1);
		res.add(i2);
		res.add(i3);

		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void test2CorrectValue3() {
		initLadderQueue(3);
		lq.addLast(0, i1);
		lq.addLast(0, i1);
		lq.addLast(0, i11);
		lq.addLast(1, i2);
		lq.addLast(1, i2);
		lq.addLast(1, i12);
		lq.addLast(2, i3);
		lq.addLast(2, i3);
		lq.addLast(2, i13);
		lq.dequeue();
		lq.dequeue();
		lq.dequeue();

		res = new ArrayList<>();
		res.add(i1);
		res.add(i2);
		res.add(i3);

		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void test2CorrectValue4() {
		initLadderQueue(3);
		lq.addLast(0, i1);
		lq.addLast(0, i1);
		lq.addLast(0, i11);
		lq.addLast(1, i2);
		lq.addLast(1, i2);
		lq.addLast(1, i12);
		lq.addLast(2, i3);
		lq.addLast(2, i3);
		lq.addLast(2, i13);
		lq.dequeue();
		lq.dequeue();
		lq.dequeue();

		res = new ArrayList<>();
		res.add(i1);
		res.add(i2);
		res.add(i3);

		assertThat(lq.dequeue(), is(res));
	}

	@Test
	public void test2CorrectValue9() {
		initLadderQueue(3);
		lq.addLast(0, i1);
		lq.addLast(0, i1);
		lq.addLast(0, i30);
		lq.addLast(1, i2);
		lq.addLast(1, i2);
		lq.addLast(1, i20);
		lq.addLast(2, i3);
		lq.addLast(2, i3);
		lq.addLast(2, i10);
		lq.dequeue();
		lq.dequeue();
		lq.dequeue();
		lq.dequeue();
		lq.dequeue();
		lq.dequeue();
		lq.dequeue();
		lq.dequeue();

		res = new ArrayList<>();
		res.add(i1);
		res.add(i2);
		res.add(i10);

		assertThat(lq.dequeue(), is(res));
	}

}

