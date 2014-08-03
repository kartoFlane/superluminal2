package com.kartoflane.superluminal2.components;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * A class representing a drawable convex polygon.
 * 
 * @author kartoFlane
 * 
 */
public class Polygon {
	/**
	 * An array of alternating x and y values<br>
	 * Even indices = x<br>
	 * Odd indices = y
	 */
	private int[] points;

	/** Number of the polygon's vertices */
	private int vertices;
	/** Cached rectangle containing the polygon */
	private Rectangle bounds;

	/**
	 * Creates a polygon from two arrays, one specifying the vertices' x coordinates,
	 * the other - y coordinates.<br>
	 * The arrays' lengths have to be equal, and greater than 2.
	 * 
	 * @param pointsX
	 * @param pointsY
	 */
	public Polygon(int[] pointsX, int[] pointsY) {
		set(pointsX, pointsY);
	}

	/**
	 * Creates a polygon from an array of alternating x and y values.<br>
	 * The array's length has to be greater than 2.
	 * 
	 * @param points
	 *            an array of alternating x and y values
	 */
	public Polygon(int[] points) {
		set(points);
	}

	/**
	 * Creates a polygon from an array of points.<br>
	 * The array's length has to be greater than 2.
	 * 
	 * @param points
	 */
	public Polygon(Point[] points) {
		set(points);
	}

	/**
	 * Creates a polygon with the specified number of vertices, but all values at 0.
	 * 
	 * @param vertices
	 */
	public Polygon(int vertices) {
		if (vertices <= 2)
			throw new IllegalArgumentException("Number of vertices is too low (3 minimum)");

		this.vertices = vertices;
		this.points = new int[vertices * 2];
	}

	/**
	 * Constructs a deep copy of the specified polygon.
	 */
	public Polygon(Polygon poly) {
		points = poly.toArray();
		vertices = poly.vertices;
		bounds = poly.getBounds();
	}

	/**
	 * @return the center of the polygon's bounds
	 */
	public Point getCenter() {
		return new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
	}

	/**
	 * @return the number of vertices this polygon has.
	 */
	public int getVertexCount() {
		return vertices;
	}

	/**
	 * Creates a polygon from two arrays, one specifying the vertices' x coordinates,
	 * the other - y coordinates.<br>
	 * The arrays' lengths have to be equal, and greater than 2.
	 * 
	 * @param pointsX
	 * @param pointsY
	 */
	public void set(int[] pointsX, int[] pointsY) {
		if (pointsX.length != pointsY.length)
			throw new IllegalArgumentException("Coordinate arrays are not equal.");
		if (pointsX.length <= 2)
			throw new IllegalArgumentException("The array is too small to create a polygon.");

		this.vertices = pointsX.length;
		this.points = new int[vertices * 2];

		for (int i = 0; i < vertices; i++) {
			points[i * 2] = pointsX[i];
			points[i * 2 + 1] = pointsY[i];
		}

		calculateBounds();
	}

	/**
	 * Creates a polygon from an array of alternating x and y values.<br>
	 * The array's length has to be greater than 2.
	 * 
	 * @param points
	 *            an array of alternating x and y values
	 */
	public void set(int[] points) {
		if (points.length % 2 != 0)
			throw new IllegalArgumentException("Array length must be even.");
		if (points.length <= 2)
			throw new IllegalArgumentException("The array is too small to create a polygon.");

		this.vertices = points.length / 2;
		this.points = points.clone();

		for (int i = 0; i < vertices; i++) {
			points[i * 2] = points[i * 2];
			points[i * 2 + 1] = points[i * 2 + 1];
		}

		calculateBounds();
	}

	/**
	 * Creates a polygon from an array of points.<br>
	 * The array's length has to be greater than 2.
	 * 
	 * @param points
	 */
	public void set(Point[] points) {
		if (points.length <= 2)
			throw new IllegalArgumentException("The array is too small to create a polygon.");

		this.vertices = points.length;
		this.points = new int[vertices * 2];

		for (int i = 0; i < vertices; i++) {
			this.points[i * 2] = points[i].x;
			this.points[i * 2 + 1] = points[i].y;
		}

		calculateBounds();
	}

	/**
	 * Moves the polygon by the specified vector.
	 */
	public void translate(int dx, int dy) {
		for (int i = 0; i < vertices; i++) {
			points[i * 2] += dx;
			points[i * 2 + 1] += dy;
		}
		bounds.x += dx;
		bounds.y += dy;
	}

	/**
	 * Centers the polygon at the specified location.
	 * 
	 * @see #getCenter()
	 */
	public void setLocation(int x, int y) {
		Point center = getCenter();
		translate(x - center.x, y - center.y);
	}

	/**
	 * @return the polygon represented as an array of alternating x and y values
	 */
	public int[] toArray() {
		return points.clone();
	}

	/**
	 * Rotates the polygon around the coordinates specified in the argument.
	 * 
	 * @param radians
	 *            angle in radians. 0 means north.
	 * @param centerX
	 *            x coodinate of the point around which the polygon will be rotated
	 * @param centerY
	 *            y coodinate of the point around which the polygon will be rotated
	 */
	public void rotate(float radians, int centerX, int centerY) {
		double sin = Math.sin(radians);
		double cos = Math.cos(radians);

		for (int i = 0; i < vertices; i++) {
			// Translate point back to origin
			points[i * 2] -= centerX;
			points[i * 2 + 1] -= centerY;

			// Rotate point
			int newx = (int) (points[i * 2] * cos - points[i * 2 + 1] * sin);
			int newy = (int) (points[i * 2] * sin + points[i * 2 + 1] * cos);

			// Translate point back
			points[i * 2] = newx + centerX;
			points[i * 2 + 1] = newy + centerY;
		}

		calculateBounds();
	}

	/**
	 * @see #rotate(float, int, int)
	 */
	public void rotate(float radians, Point p) {
		rotate(radians, p.x, p.y);
	}

	/**
	 * Rotates the polygon around its {@link #getCenter center point}.
	 * 
	 * @param radians
	 *            angle in radians. 0 means north.
	 */
	public void rotate(float radians) {
		Point c = getCenter();
		rotate(radians, c.x, c.y);
	}

	/**
	 * @return the smallest rectangle that can contain the polygon.
	 */
	public Rectangle getBounds() {
		return new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	/**
	 * Draw the polygon using {@link GC#drawPolygon(int[])}
	 */
	public void draw(PaintEvent e) {
		e.gc.drawPolygon(points);
	}

	/**
	 * Draw the polygon using {@link GC#fillPolygon(int[])}
	 */
	public void fill(PaintEvent e) {
		e.gc.fillPolygon(points);
	}

	/**
	 * Checks whether the polygon contains the specified point.<br>
	 * <br>
	 * StackOverflow magic: http://stackoverflow.com/questions/8721406/how-to-determine-if-a-point-is-inside-a-2d-convex-polygon
	 * 
	 * @return true if the polygon contains the point, false otherwise
	 */
	public boolean contains(int x, int y) {
		boolean result = false;
		for (int i = 0, j = vertices - 1; i < vertices; j = i++) {
			if ((points[i * 2 + 1] > y) != (points[j * 2 + 1] > y) &&
					(x < (points[j * 2] - points[i * 2]) * (y - points[i * 2 + 1]) / (points[j * 2 + 1] - points[i * 2 + 1]) + points[i * 2])) {
				result = !result;
			}
		}
		return result;
	}

	/**
	 * @see #contains(int, int)
	 */
	public boolean contains(Point p) {
		return contains(p.x, p.y);
	}

	/**
	 * Taken from {@link java.awt.Polygon#calculateBounds(int[], int[], int)}
	 */
	private void calculateBounds() {
		int boundsMinX = Integer.MAX_VALUE;
		int boundsMinY = Integer.MAX_VALUE;
		int boundsMaxX = Integer.MIN_VALUE;
		int boundsMaxY = Integer.MIN_VALUE;

		for (int i = 0; i < vertices; i++) {
			int x = points[i * 2];
			boundsMinX = Math.min(boundsMinX, x);
			boundsMaxX = Math.max(boundsMaxX, x);
			int y = points[i * 2 + 1];
			boundsMinY = Math.min(boundsMinY, y);
			boundsMaxY = Math.max(boundsMaxY, y);
		}
		bounds = new Rectangle(boundsMinX, boundsMinY, boundsMaxX - boundsMinX, boundsMaxY - boundsMinY);
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("Polygon: { ");
		for (int i = 0; i < vertices; i++) {
			buf.append(points[i * 2] + ", " + points[i * 2 + 1]);
			if (i != vertices - 1)
				buf.append(" ; ");
		}
		buf.append(" }");
		return buf.toString();
	}

	public int hashCode() {
		return bounds.hashCode() ^ points.hashCode();
	}
}
