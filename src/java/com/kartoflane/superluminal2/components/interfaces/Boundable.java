package com.kartoflane.superluminal2.components.interfaces;

import org.eclipse.swt.graphics.Rectangle;

/**
 * Classes implementing this interface can be bounded to an area,
 * and cannot be moved outside of it.
 * 
 * @author kartoFlane
 * 
 */
public interface Boundable {

	/**
	 * @return true if the object is bounded to an area, false otherwise
	 */
	public boolean isBounded();

	/**
	 * If true, the object becomes actively bounded to the area specified
	 * by {@link #setBoundingArea(int, int, int, int)} method.
	 * If false, the object is freed and can be moved outside of the area.<br>
	 * False by default.
	 */
	public void setBounded(boolean bound);

	/**
	 * @return the area to which the object is bound.
	 */
	public Rectangle getBoundingArea();

	/**
	 * Specify the area to which this object will be bounded.<br>
	 * The object cannot be moved to a location that falls outside
	 * of the bounding area, however parts of the object <b>can</b>
	 * fall outside of it, if it is large enough.
	 * 
	 * @param x
	 *            starting point of the bounding area
	 * @param y
	 *            starting point of the bounding area
	 * @param w
	 *            width of the bounding area
	 * @param h
	 *            height of the bounding area
	 */
	public void setBoundingArea(int x, int y, int w, int h);
}
