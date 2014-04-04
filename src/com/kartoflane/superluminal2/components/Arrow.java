package com.kartoflane.superluminal2.components;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class Arrow implements PaintListener {

	private Point start = null;
	private Point end = null;
	private Point[] tips = null;
	private int tipSize = 0;

	/**
	 * @param sx
	 *            x coordinate of the start location
	 * @param sy
	 *            y coordinate of the start location
	 * @param ex
	 *            x coordinate of the end location
	 * @param ey
	 *            y coordinate of the end location
	 */
	public Arrow(int sx, int sy, int ex, int ey) {
		start = new Point(sx, sy);
		end = new Point(ex, ey);
		tips = new Point[2];
		tips[0] = new Point(0, 0);
		tips[1] = new Point(0, 0);
		setTipSize(10);
	}

	/**
	 * @param sx
	 *            x coordinate of the start location
	 * @param sy
	 *            y coordinate of the start location
	 * @param d
	 *            length of the arrow
	 * @param angle
	 *            in degrees
	 */
	public Arrow(int sx, int sy, int d, double angle) {
		start = new Point(sx, sy);
		end = new Point(sx + d, sy);
		tips = new Point[2];
		tips[0] = new Point(0, 0);
		tips[1] = new Point(0, 0);
		setRotation(angle);
		setTipSize(10);
	}

	public void setTipSize(int size) {
		tipSize = size;
		updateTips();
	}

	public int getTipSize() {
		return tipSize;
	}

	public void setStart(int x, int y) {
		start.x = x;
		start.y = y;
		updateTips();
	}

	public void setStart(Point p) {
		setStart(p.x, p.y);
	}

	public Point getStart() {
		return new Point(start.x, start.y);
	}

	public void setEnd(int x, int y) {
		end.x = x;
		end.y = y;
		updateTips();
	}

	public void setEnd(Point p) {
		setEnd(p.x, p.y);
	}

	/**
	 * 
	 * @param rotation
	 *            in degrees
	 * @param distance
	 *            distance in pixels from line's start point
	 */
	public void setEnd(double rotation, int distance) {
		double rad = rotation * Math.PI / 180;
		end.x = (int) (start.x + Math.cos(rad) * distance - Math.sin(rad) * distance);
		end.y = (int) (start.y + Math.sin(rad) * distance + Math.cos(rad) * distance);
		updateTips();
	}

	public Point getEnd() {
		return new Point(end.x, end.y);
	}

	/**
	 * Moves the end point to the specified direction relative to the start point,
	 * using the distance between the two points.
	 * 
	 * @param rotation
	 *            in degrees
	 */
	public void setRotation(double rotation) {
		double rad = (rotation + 135) * Math.PI / 180;
		int distance = (int) Math.sqrt(Math.pow(end.x - start.x, 2) + Math.pow(end.y - start.y, 2));

		end.x = (int) (start.x + Math.cos(rad) * distance - Math.sin(rad) * distance);
		end.y = (int) (start.y + Math.sin(rad) * distance + Math.cos(rad) * distance);
		updateTips();
	}

	public Rectangle getBounds() {
		return new Rectangle(Math.min(start.x, end.x) - 1,
				Math.min(start.y, end.y) - 1,
				Math.abs(end.x - start.x) + 2,
				Math.abs(end.y - start.y) + 2);
	}

	@Override
	public void paintControl(PaintEvent e) {
		e.gc.drawLine(start.x, start.y, end.x, end.y);
		e.gc.drawLine(end.x, end.y, tips[0].x, tips[0].y);
		e.gc.drawLine(end.x, end.y, tips[1].x, tips[1].y);
	}

	protected void updateTips() {
		int dx = end.x - start.x;
		int dy = end.y - start.y;
		dx = dx == 0 ? 1 : dx;
		dy = dy == 0 ? 1 : dy;
		double rad = Math.atan(dy / dx);
		tips[0].x = (int) (end.x + Math.cos(rad - Math.PI / 3) * tipSize - Math.sin(rad - Math.PI / 3) * tipSize);
		tips[0].y = (int) (end.y + Math.sin(rad - Math.PI / 3) * tipSize - Math.cos(rad - Math.PI / 3) * tipSize);
		tips[1].x = (int) (end.x + Math.cos(rad + Math.PI / 3) * tipSize + Math.sin(rad + Math.PI / 3) * tipSize);
		tips[1].y = (int) (end.y + Math.sin(rad + Math.PI / 3) * tipSize + Math.cos(rad + Math.PI / 3) * tipSize);
	}
}
