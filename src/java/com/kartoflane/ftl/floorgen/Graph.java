package com.kartoflane.ftl.floorgen;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A simple undirected point graph to hold the shape of the room layout in a more usable fashion.
 * 
 * The graph is symmetric, irreflexive and not transitive.
 */
class Graph implements Iterable<Point> {
	private Map<Point, List<Point>> adj;

	public Graph() {
		adj = new HashMap<Point, List<Point>>();
	}

	/**
	 * Creates a shallow copy of the specified graph.
	 */
	public Graph(Graph graph) {
		this();
		adj.putAll(graph.adj);
	}

	public Iterator<Point> iterator() {
		return adj.keySet().iterator();
	}

	public void addEdge(Point v1, Point v2) {
		newEdge(v1, v2);
		newEdge(v2, v1);
	}

	public void removeEdge(Point v1, Point v2) {
		delEdge(v1, v2);
		delEdge(v2, v1);
	}

	public boolean hasEdge(Point v1, Point v2) {
		List<Point> list = adj.get(v1);
		if (list == null)
			return false;
		return list.contains(v2);
	}

	public boolean hasEdge(Point v1, int v2x, int v2y) {
		return hasEdge(v1, new Point(v2x, v2y));
	}

	public boolean hasEdge(int v1x, int v1y, int v2x, int v2y) {
		return hasEdge(new Point(v1x, v1y), new Point(v2x, v2y));
	}

	public List<Point> listNeighbours(Point v) {
		if (adj.get(v) == null)
			return new ArrayList<Point>();
		else
			return new ArrayList<Point>(adj.get(v));
	}

	public int countEdges(Point v) {
		if (adj.get(v) == null)
			return 0;
		else
			return adj.get(v).size();
	}

	/**
	 * Prints the graph in the form of a table. Each row represents a point in the graph,
	 * and numbers listed in that row indicate points that row is connected to.
	 */
	public String toString() {
		int f = 1, s = adj.keySet().size();
		while (s > 0) {
			s /= 10;
			f += 1;
		}

		StringBuilder buf = new StringBuilder();
		int i = 1;

		for (Point v : adj.keySet()) {
			buf.append(String.format("%-" + f + "s", i));
			buf.append(String.format("%11s", toString(v)));
			buf.append(": ");
			List<Point> l = adj.get(v);
			int j = 1;
			for (Point w : adj.keySet()) {
				if (l.contains(w)) {
					buf.append(String.format("%" + f + "s", j));
				}
				j++;
			}
			buf.append("\n");
			i++;
		}
		return buf.toString();
	}

	private String toString(Point v) {
		return "{ " + v.x + ", " + v.y + " }";
	}

	/*
	 * Private methods
	 */

	private void delEdge(Point v1, Point v2) {
		List<Point> list = adj.get(v1);
		if (list == null)
			return;
		list.remove(v2);
		if (list.isEmpty())
			adj.remove(v1);
	}

	private void newEdge(Point v1, Point v2) {
		List<Point> list = adj.get(v1);
		if (list == null) {
			list = new LinkedList<Point>();
			adj.put(v1, list);
		}
		if (!list.contains(v2) && !v1.equals(v2))
			list.add(v2);
	}
}
