package com.kartoflane.superluminal2.mvc.controllers.props;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.utils.Utils;

public class OrbitPropController extends PropController {
	private int offset = 0;

	public OrbitPropController(AbstractController parent, String id) {
		super(parent, id);
		setBounded(true);
	}

	public void setOrbitOffset(int o) {
		offset = o;
	}

	public int getOrbitOffset() {
		return offset;
	}

	/**
	 * Calculates the location at the angle specified in the argument, at the offset
	 * specified by {@link #setOrbitOffset()} method.
	 * 
	 * @param angle
	 *            in degrees, 0 means north, increases counter-clockwise
	 */
	public Point angleToOrbitLocation(double angle) {
		Point p = new Point(getX(), getY());
		if (getParent() == null)
			return p;
		angle = Math.toRadians(-angle + 90);
		p.x = getParent().getX() - (int) Math.round(Math.cos(angle) * offset);
		p.y = getParent().getY() - (int) Math.round(Math.sin(angle) * offset);
		return p;
	}

	/**
	 * @return angle in degrees, 0 means north, increasing counter-clockwise
	 */
	public double getOrbitAngle() {
		if (getParent() == null)
			return 0;
		return 270 - Utils.angle(getLocation(), getParent().getLocation());
	}

	@Override
	public boolean isWithinBoundingArea(int x, int y) {
		return false;
	}

	@Override
	public Point limitToBoundingArea(int x, int y) {
		Point p = new Point(x, y);
		if (getParent() == null)
			return p;

		double angle = (float) Math.toRadians(270 - Utils.angle(p, getParent().getLocation()));
		p.x = getParent().getX() - (int) (Math.cos(angle) * offset);
		p.y = getParent().getY() - (int) (Math.sin(angle) * offset);
		return p;
	}
}