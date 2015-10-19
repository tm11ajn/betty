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

import java.util.ArrayList;

public class Path<T> extends ArrayList<Edge<T>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public boolean isValid() {
		return (this.size() > 0);
	}
	
	public String getVertexNames() {
		
		if (!this.isValid()) {
			return "(empty)";
		}
		
		String string = "" + this.get(0).getTail();
		
		for (Edge<T> e : this) {
			string += "" + e.getHead();
		}
		
		return string;
	}
	
	public double getWeight() {
		double weightSum = 0;
		
		for (Edge<T> e : this) {
			weightSum += e.getWeight();
		}
		
		return weightSum;
	}
	
	public double getDeltaWeight() {
		
		double deltaWeightSum = 0;
		
		for (Edge<T> e : this) {
			deltaWeightSum += e.getDelta();
		}
		
		return deltaWeightSum;
	}
	
	@Override
	public String toString() {
		
		if (!this.isValid()) {
			return "(empty)";
		}
		
		int size = this.size();
		
		String string = "" + this.get(0).getDelta();
		
		for (int i = 1; i < size; i++) {
			string += ", " + this.get(i).getDelta();
		}
		
		return string;
	}
}
