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
import org.junit.Before;
import org.junit.Test;

import se.umu.cs.flp.aj.nbest.semiring.TropicalSemiring;
import se.umu.cs.flp.aj.nbest.treedata.Configuration;


public class LazyLimitedLadderQueueTest {

	private static Comparator<Configuration<Integer>> comp =
			new Comparator<Configuration<Integer>>() {

		@Override
		public int compare(
				Configuration<Integer> arg0,
				Configuration<Integer> arg1) {

//			Integer sum0 = 0;
//			Integer sum1 = 0;
//
//			for (int val : arg0.getValues()) {
//				sum0 += val;
//			}
//
//			for (int val : arg1.getValues()) {
//				sum1 += val;
//			}

//			return sum0.compareTo(sum1);
			
			return arg0.getWeight().compareTo(arg1.getWeight());
		}
	};

	private LazyLimitedLadderQueue<Integer> lq;
	private int limit = 10;
	private int defaultID = 0;

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	private void init(int rank) {
		this.lq = new LazyLimitedLadderQueue<Integer>(defaultID, rank, comp, limit);
	}
	
	@Test 
	public void shouldReturnCorrectStartConfig() {
		init(3);
		assertThat(lq.getStartConfig().getIndices(), is(new int[]{0,0,0}));
	}
	
	@Test 
	public void shouldNotHaveDequeuedYet() {
		init(3);
		assertThat(lq.hasNotDequeuedYet(), is(true));
	}
	
	@Test 
	public void shouldHaveDequeued() {
		init(3);
		Configuration<Integer> startConfig = lq.getStartConfig();
		startConfig.setWeight(new TropicalSemiring().createWeight(2));
		lq.insert(startConfig);
		lq.dequeue();
		assertThat(lq.hasNotDequeuedYet(), is(false));
	}
	
	@Test 
	public void shouldReturnCorrectID() {
		init(3);
		assertThat(lq.getID(), is(defaultID));
	}
	
	@Test
	public void shouldHaveNext() {
		init(3);
		Configuration<Integer> startConfig = lq.getStartConfig();
		startConfig.setWeight(new TropicalSemiring().createWeight(10));
		lq.insert(startConfig);
		assertThat(lq.hasNext(), is(true));
	}
	
	@Test
	public void shouldNotHaveNext() {
		init(3);
		assertThat(lq.hasNext(), is(false));
	}
	
	@Test
	public void shouldBeOfSize0() {
		init(3);
		assertThat(lq.size(), is(0));
	}
	
	@Test
	public void shouldBeOfSize1() {
		init(3);
		Configuration<Integer> startConfig = lq.getStartConfig();
		startConfig.setWeight(new TropicalSemiring().createWeight(2));
		lq.insert(startConfig);
		assertThat(lq.size(), is(1));
	}
	
	@Test
	public void shouldBeOfSize0AfterEnqueuingAndDequeuingOneElement() {
		init(3);
		Configuration<Integer> startConfig = lq.getStartConfig();
		startConfig.setWeight(new TropicalSemiring().createWeight(2));
		lq.insert(startConfig);
		lq.dequeue();
		assertThat(lq.size(), is(0));
	}
	
	@Test
	public void shouldReturnTheCorrectNextConfigsOfRank3GivenTheStartConfig() {
		int currentRank = 3;
		init(currentRank);
		Configuration<Integer> startConfig = lq.getStartConfig();
		startConfig.setWeight(new TropicalSemiring().createWeight(2));
		lq.insert(startConfig);
		startConfig = lq.dequeue();
		ArrayList<Configuration<Integer>> nextConfigs = lq.getNextConfigs(startConfig);
		int[][] indices = new int[currentRank][currentRank];
		int[][] result = new int[][]{{1,0,0},{0,1,0},{0,0,1}};
		
		for (int i = 0; i < currentRank; i++) {
			indices[i] = nextConfigs.get(i).getIndices();
		}
		
		assertThat(indices, is(result));
	}
	
	@Test
	public void shouldReturnTheCorrectNextConfigsOfRank4GivenTheStartConfig() {
		int currentRank = 4;
		init(currentRank);
		Configuration<Integer> startConfig = lq.getStartConfig();
		startConfig.setWeight(new TropicalSemiring().createWeight(2));
		lq.insert(startConfig);
		startConfig = lq.dequeue();
		ArrayList<Configuration<Integer>> nextConfigs = lq.getNextConfigs(startConfig);
		int[][] indices = new int[currentRank][currentRank];
		int[][] result = new int[][]{{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
		
		for (int i = 0; i < currentRank; i++) {
			indices[i] = nextConfigs.get(i).getIndices();
		}
		
		assertThat(indices, is(result));
	}
	
	@Test
	public void shouldReturnTheCorrectNextConfigsOfRank3GivenPossibilitiesForDuplicates() {
		int currentRank = 3;
		init(currentRank);
		Configuration<Integer> startConfig = lq.getStartConfig();
		startConfig.setWeight(new TropicalSemiring().createWeight(2));
		lq.insert(startConfig);
		Configuration<Integer> currentConfig = null;
		ArrayList<Configuration<Integer>> nextConfigs = null;
		int[][] indices = new int[currentRank - 1][currentRank];
		int[][] result = new int[][]{{0,2,0},{0,1,1}};
		double d = 0;
		
		for (int i = 0; i < currentRank; i++) {
			currentConfig = lq.dequeue();
			nextConfigs = lq.getNextConfigs(currentConfig);
			
			for (Configuration<Integer> next : nextConfigs) {
				next.setWeight(new TropicalSemiring().createWeight(d));
				lq.insert(next);
				d++;
			}
		}
		
		for (int i = 0; i < currentRank - 1; i++) {
			indices[i] = nextConfigs.get(i).getIndices();
		}
		
		assertThat(indices, is(result));
	}
	
	@Test
	public void shouldReturnTheCorrectNextConfigsOfRank4GivenPossibilitiesForDuplicates() {
		int currentRank = 4;
		init(currentRank);
		Configuration<Integer> startConfig = lq.getStartConfig();
		startConfig.setWeight(new TropicalSemiring().createWeight(2));
		lq.insert(startConfig);
		Configuration<Integer> currentConfig = null;
		ArrayList<Configuration<Integer>> nextConfigs = null;
		int[][] indices = new int[currentRank - 1][currentRank];
		int[][] result = new int[][]{{0,2,0,0},{0,1,1,0},{0,1,0,1}};
		double d = 0;
		
		for (int i = 0; i < 3; i++) {
			currentConfig = lq.dequeue();
			nextConfigs = lq.getNextConfigs(currentConfig);
			
			for (Configuration<Integer> next : nextConfigs) {
				next.setWeight(new TropicalSemiring().createWeight(d));
				lq.insert(next);
				d++;
			}
		}
		
		for (int i = 0; i < currentRank - 1; i++) {
			indices[i] = nextConfigs.get(i).getIndices();
		}
		
		assertThat(indices, is(result));
	}
	
	@Test
	public void should() {
		
	}

}
