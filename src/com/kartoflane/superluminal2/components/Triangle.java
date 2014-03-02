package com.kartoflane.superluminal2.components;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;

/**
 * A simple abstraction of a triangle.<br>
 * <br>
 * 
 * Could have used {@link java.awt.Polygon Polygon} instead, but noticed it too late... *shrug* <br>
 * It's slightly overly complex for what this class is needed for, anyway.
 * 
 * @author kartoFlane
 * 
 */
public class Triangle implements PaintListener {
	private final int[] points;
	private boolean fill = true;

	public Triangle() {
		points = new int[6];
	}

	public Triangle(int x1, int y1, int x2, int y2, int x3, int y3) {
		this();
		points[0] = x1;
		points[1] = y1;
		points[2] = x2;
		points[3] = y2;
		points[4] = x3;
		points[5] = y3;
	}

	/**
	 * @param points
	 *            an array of length 6, containing alternating x and y values, each pair specifying one of the triangle's corners.
	 */
	public Triangle(int[] points) {
		if (points == null)
			throw new NullPointerException("Array passed in argument is null");
		if (points.length != 6)
			throw new IllegalArgumentException("Array passed in the argument is not of length 6 (length = " + points.length + ")");

		this.points = points.clone();
	}

	/**
	 * Set how the {@link #paintControl(PaintEvent) paintControl} method should draw the triangle.<br>
	 * If true, the triangle area is filled with color, if false, only the triangle's outline is drawn.
	 */
	public void setFillDraw(boolean fill) {
		this.fill = fill;
	}

	public boolean isFillDraw() {
		return fill;
	}

	/** @return location of the first corner. */
	public Point getFirstPoint() {
		return new Point(points[0], points[1]);
	}

	/** @return location of the second corner. */
	public Point getSecondPoint() {
		return new Point(points[2], points[3]);
	}

	/** @return location of the third corner. */
	public Point getThirdPoint() {
		return new Point(points[4], points[5]);
	}

	/** @return locations of the triangle's conrners in an array */
	public Point[] getPoints() {
		return new Point[] { getFirstPoint(), getSecondPoint(), getThirdPoint() };
	}

	/**
	 * @return an array of alternating x and y values, each pair specifying a corner in the triangle.
	 * 
	 * @see {@link org.eclipse.swt.graphics.GC#drawPolygon(int[] pointArray) GC.drawPolygon(int[] pointArray)}
	 */
	public int[] getIntPoints() {
		return points.clone();
	}

	/** Changes the points representing the triangle. */
	public void set(int x1, int y1, int x2, int y2, int x3, int y3) {
		points[0] = x1;
		points[1] = y1;
		points[2] = x2;
		points[3] = y2;
		points[4] = x3;
		points[5] = y3;
	}

	/** Changes the points representing the triangle. */
	public void set(Point first, Point second, Point third) {
		set(first.x, first.y, second.x, second.y, third.x, third.y);
	}

	public void paintControl(PaintEvent e) {
		if (fill)
			e.gc.fillPolygon(points);
		else
			e.gc.drawPolygon(points);
	}

	public boolean contains(Point p) {
		return contains(p.x, p.y);
	}

	public boolean contains(int x, int y) {
		boolean b1, b2, b3;

		b1 = sign(x, y, points[0], points[1], points[2], points[3]) < 0;
		b2 = sign(x, y, points[2], points[3], points[4], points[5]) < 0;
		b3 = sign(x, y, points[4], points[5], points[0], points[1]) < 0;

		return ((b1 == b2) && (b2 == b3));
	}

	private int sign(int x1, int y1, int x2, int y2, int x3, int y3) {
		return (x1 - x3) * (y2 - y3) - (x2 - x3) * (y1 - y3);
	}

	public Triangle clone() {
		Triangle clone = new Triangle(points);
		clone.fill = fill;

		return clone;
	}
}
