package com.kartoflane.superluminal2.components;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal2.core.Utils;

public class Line implements PaintListener {
	private Point start = null;
	private Point end = null;

	public Line(int sx, int sy, int ex, int ey) {
		start = new Point(sx, sy);
		end = new Point(ex, ey);
	}

	public void setStart(int x, int y) {
		start.x = x;
		start.y = y;
	}

	public void setStart(Point p) {
		setStart(p.x, p.y);
	}

	public Point getStart() {
		return Utils.copy(start);
	}

	public void setEnd(int x, int y) {
		end.x = x;
		end.y = y;
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
		end.x = (int) (start.x + Math.cos(rad) * distance - Math.cos(rad) * distance);
		end.y = (int) (start.y + Math.sin(rad) * distance + Math.cos(rad) * distance);
	}

	public Point getEnd() {
		return Utils.copy(end);
	}

	public Rectangle getBounds() {
		return new Rectangle(Math.min(start.x, end.x), Math.min(start.y, end.y),
				Math.max(1, end.x - start.x), Math.max(1, end.y - start.y));
	}

	@Override
	public void paintControl(PaintEvent e) {
		e.gc.drawLine(start.x, start.y, end.x, end.y);
	}
}
