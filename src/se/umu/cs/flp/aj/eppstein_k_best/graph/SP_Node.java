/*
 * Copyright 2015 Anna Jonsson for the research group Foundations of Language 
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

/* 
 * This file is part of a Java adaption (of which all files are resided in 
 * package se.umu.cs.flp.aj.eppstein_k_best) of the implementation of 
 * Eppstein's k best graph algorithm implementation by NickDinges found in 
 * the GitHub repository https://github.com/NickDinges/k-shortest-path.
 */

package se.umu.cs.flp.aj.eppstein_k_best.graph;

public class SP_Node<T> implements Comparable<SP_Node<T>> {

	private Edge<T> edge;
	private double weight;
	
	public SP_Node(Edge<T> edge, double weight) {
		this.edge = edge;
		this.weight = weight;
	}
	
	public Edge<T> getEdge() {
		return this.edge;
	}
	
	public double getWeight() {
		return this.weight;
	}
	
	@Override
	public int compareTo(SP_Node<T> o) {
		
		if (this.weight == o.weight) {
			return 0;
		} else if (this.weight < o.weight) {
			return -1;
		}
		
		return 1;
	}
	
	@Override
	public String toString() {
		return this.edge + " [" + this.weight + "]";
	}

}
