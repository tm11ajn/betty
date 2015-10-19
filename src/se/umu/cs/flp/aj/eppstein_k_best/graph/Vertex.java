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

public class Vertex<T> {

	private String label;

	private Edge<T> edgeToPath = null;
	private double distance = Double.MIN_VALUE;
	private ArrayList<Edge<T>> relatedEdges = new ArrayList<>();

	public Vertex(String label) {
		this.label = label;
	}

	public Vertex<T> next() {

		if (edgeToPath == null) {
			return null;
		}

		return edgeToPath.getHead();
	}

	public String getLabel() {
		return this.label;
	}

	public Edge<T> getEdgeToPath() {
		return this.edgeToPath;
	}

	public void setEdgeToPath(Edge<T> edgeToPath) {
		this.edgeToPath = edgeToPath;
	}

	public double getDistance() {
		return this.distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public ArrayList<Edge<T>> getRelatedEdges() {
		return this.relatedEdges;
	}

	@Override
	public String toString() {
		return this.label;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Vertex) {
			return this.label.equals(((Vertex<?>) obj).label);
		} else if (obj instanceof String) {
			return this.label.equals(obj);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return this.label.hashCode();
	}
}
