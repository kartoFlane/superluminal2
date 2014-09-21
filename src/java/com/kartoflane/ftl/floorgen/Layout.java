package com.kartoflane.ftl.floorgen;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

class Layout {
	private final Graph tileGraph;
	/** List of all airlocks in this layout */
	private final List<Door> airlocks;

	/** Width of the layout, in pixels */
	private final int width;
	/** Height of the layout, in pixels */
	private final int height;

	Layout(List<Rectangle> rooms, List<Door> airlocks) {
		this.airlocks = airlocks;
		tileGraph = new Graph();

		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = 0, maxY = 0;
		for (Rectangle r : rooms) {
			// Find the min and max points of the layout to determine its size
			if (r.x < minX)
				minX = r.x;
			if (r.y < minY)
				minY = r.y;
			if (r.x + r.width > maxX)
				maxX = r.x + r.width;
			if (r.y + r.height > maxY)
				maxY = r.y + r.height;

			// Add the tiles constituting the room to the graph
			for (int x = r.x; x < r.x + r.width; x++) {
				for (int y = r.y; y < r.y + r.height; y++)
					addTile(x, y);
			}
		}

		width = maxX - minX;
		height = maxY - minY;

		if (width < 0 || height < 0)
			throw new IllegalArgumentException("Error -- ship dimensions are negative");
	}

	/*
	 * Getters
	 */

	/**
	 * @return a point graph representing all tiles in this layout.
	 */
	protected Graph getTileGraph() {
		return tileGraph;
	}

	/**
	 * @return a collection of all airlocks in this layout.
	 */
	protected List<Door> getAirlocks() {
		return airlocks;
	}

	/**
	 * @return size of the layout in grid cells / tiles.
	 */
	protected Dimension getSize() {
		return new Dimension(width, height);
	}

	/**
	 * @return width of the layout in grid cells / tiles.
	 */
	protected int getWidth() {
		return width;
	}

	/**
	 * @return height of the layout in grid cells / tiles.
	 */
	protected int getHeight() {
		return height;
	}

	/*
	 * =============
	 * Other methods
	 */

	/**
	 * @param x
	 *            x coordinate of the tile at which the door is located
	 * @param y
	 *            y coordinate of the tile at which the door is located
	 * @param horizontal
	 *            whether the airlock has to be horizontal or vertical
	 * 
	 * @return true if an airlock matching the conditions is found, false otherwise.
	 */
	protected boolean hasAirlockAt(int x, int y, boolean horizontal) {
		for (Door d : airlocks) {
			if (d.isHorizontal() == horizontal && d.getX() == x && d.getY() == y)
				return true;
		}
		return false;
	}

	/**
	 * <pre>
	 * o --- N   <-- corner node (N)
	 *     / |
	 *   /   |
	 * o     o
	 * </pre>
	 */
	protected boolean isCorner(Point p) {
		return tileGraph.countEdges(p) == 3;
	}

	/** @see #isCorner(Point) */
	protected boolean isCorner(int x, int y) {
		return isCorner(new Point(x, y));
	}

	/**
	 * <pre>
	 * o     o     o
	 *   \   |   /
	 *     \ | /
	 * o --- N --- o   <-- dent node (N)
	 *     / |
	 *   /   |
	 * o     o
	 * </pre>
	 */
	protected boolean isDent(Point p) {
		return tileGraph.countEdges(p) == 7;
	}

	/** @see #isDent(Point) */
	protected boolean isDent(int x, int y) {
		return isDent(new Point(x, y));
	}

	/**
	 * <pre>
	 *       o     o
	 *       |   /
	 *       | /
	 * o --- N --- o   <-- double dent node (N)
	 *     / |
	 *   /   |
	 * o     o
	 * </pre>
	 */
	protected boolean isDoubleDent(Point p) {
		return tileGraph.countEdges(p) == 6;
	}

	/** @see #isDoubleDent(Point) */
	protected boolean isDoubleDent(int x, int y) {
		return isDoubleDent(new Point(x, y));
	}

	/**
	 * <pre>
	 * o     o     o
	 *   \   |   /
	 *     \ | /
	 * o --- N --- o   <-- inner node (N)
	 *     / | \
	 *   /   |   \
	 * o     o     o
	 * </pre>
	 */
	protected boolean isInner(Point p) {
		return tileGraph.countEdges(p) == 8;
	}

	/** @see #isInner(Point) */
	protected boolean isInner(int x, int y) {
		return isInner(new Point(x, y));
	}

	/**
	 * <pre>
	 * o     o
	 *   \   |
	 *     \ |
	 * o --- N   <-- wall node (N)
	 *     / |
	 *   /   |
	 * o     o
	 * </pre>
	 */
	protected boolean isWall(Point p) {
		return tileGraph.countEdges(p) == 5;
	}

	/** @see #isWall(Point) */
	protected boolean isWall(int x, int y) {
		return isWall(new Point(x, y));
	}

	/**
	 * Adds 6 edges representing a tile to the graph. The arguments specify top left corner of the tile.
	 */
	private void addTile(int x, int y) {
		Point[] points = new Point[4];

		points[0] = new Point(x, y); // topLeft
		points[1] = new Point(x + 1, y); // topRight
		points[2] = new Point(x, y + 1); // botLeft
		points[3] = new Point(x + 1, y + 1); // botRight

		tileGraph.addEdge(points[0], points[1]);
		tileGraph.addEdge(points[0], points[2]);
		tileGraph.addEdge(points[3], points[1]);
		tileGraph.addEdge(points[3], points[2]);
		tileGraph.addEdge(points[0], points[3]);
		tileGraph.addEdge(points[1], points[2]);
	}
}
