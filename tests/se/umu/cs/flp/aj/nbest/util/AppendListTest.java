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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class AppendListTest {

	private AppendList<Integer> list;

	@Before
	public void setUp() throws Exception {
		list = new AppendList<>();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test 
	public void shouldCreateEmptyList() {
		assertThat(list.size(), is(0));
	}
	
	@Test 
	public void shouldCreateSingletonList() {
		list.appendLast(2);
		assertThat(list.size(), is(1));
	}
	
	@Test 
	public void shouldContainCorrectFirstElementForSingletonList() {
		list.appendLast(2);
		assertThat(list.getFirst(), is(2));
	}
	
	@Test 
	public void shouldContainCorrectLastElementForSingletonList() {
		list.appendLast(2);
		assertThat(list.getLast(), is(2));
	}
	
	@Test 
	public void shouldContainCorrectFirstElement() {
		list.appendLast(1);
		list.appendLast(2);
		list.appendLast(3);
		list.appendLast(4);
		assertThat(list.getFirst(), is(1));
	}
	
	@Test 
	public void shouldContainCorrectLastElement() {
		list.appendLast(1);
		list.appendLast(2);
		list.appendLast(3);
		list.appendLast(4);
		assertThat(list.getLast(), is(4));
	}
	
	@Test 
	public void shouldConcatenateTwoListsAndContainCorrectFirstElement() {
		AppendList<Integer> list1 = new AppendList<Integer>();
		AppendList<Integer> list2 = new AppendList<Integer>();
		list1.appendLast(1);
		list1.appendLast(2);
		list1.appendLast(3);
		list1.appendLast(4);
		list2.appendLast(5);
		list2.appendLast(6);
		list2.appendLast(7);
		list2.appendLast(8);
		list1.concatenate(list2);
		assertThat(list1.getFirst(), is(1));
	}
	
	@Test 
	public void shouldConcatenateTwoListsAndContainCorrectLastElement() {
		AppendList<Integer> list1 = new AppendList<Integer>();
		AppendList<Integer> list2 = new AppendList<Integer>();
		list1.appendLast(1);
		list1.appendLast(2);
		list1.appendLast(3);
		list1.appendLast(4);
		list2.appendLast(5);
		list2.appendLast(6);
		list2.appendLast(7);
		list2.appendLast(8);
		list1.concatenate(list2);
		assertThat(list1.getLast(), is(8));
	}
	
	@Test 
	public void shouldConcatenateTwoListsAndContainCorrectElements() {
		AppendList<Integer> list1 = new AppendList<Integer>();
		AppendList<Integer> list2 = new AppendList<Integer>();
		boolean correct = true;
		int counter = 1;
		
		list1.appendLast(1);
		list1.appendLast(2);
		list1.appendLast(3);
		list1.appendLast(4);
		list2.appendLast(5);
		list2.appendLast(6);
		list2.appendLast(7);
		list2.appendLast(8);
		list1.concatenate(list2);
		
		for (Integer i : list1) {
			if (i != counter) {
				correct = false;
			}
			counter++;
		}
		
		assertThat(correct, is(true));
	}
	
	@Test
	public void should() {
		assertThat(true, is(true));
	}

}

