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


public class Edge<T> {
	
	private Vertex<T> tail;
	private Vertex<T> head;
	private T label;
	private double weight;
	//private String group;
	
	public Edge(Vertex<T> tail, Vertex<T> head, T label, double weight
			/*, String group*/) {
		this.tail = tail;
		this.head = head;
		this.label = label;
		this.weight = weight;
		//this.group = group;
	}
	
	public Edge(Vertex<T> tail, Vertex<T> head, double weight
			/*, String group*/) {
		this.tail = tail;
		this.head = head;
		this.weight = weight;
		//this.group = group;
	}
	
	public Vertex<T> getTail() {
		return this.tail;
	}

	public Vertex<T> getHead() {
		return this.head;
	}
	
	public T getLabel() {
		return label;
	}
	
	public double getWeight() {
		return this.weight;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
//	public String getGroup() {
//		return this.group;
//	}
	
	public double getDelta() {
		return this.weight + this.head.getDistance() - this.tail.getDistance();
	}
	
	public boolean isSidetrackOf(Vertex<T> v) {
		return (this.tail == v && this != v.getEdgeToPath() && this.weight >= 0); 
	}
	
	@Override
	public String toString() {
		return this.tail + "--" + this.weight + "-->" + this.head;
	}

}
