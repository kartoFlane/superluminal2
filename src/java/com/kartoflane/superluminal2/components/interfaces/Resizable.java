package com.kartoflane.superluminal2.components.interfaces;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public interface Resizable extends Movable {
	/**
	 * Change size of the object to the specified values
	 * 
	 * @return true if size was changed, false otherwise
	 */
	public boolean setSize(int w, int h);

	/**
	 * @return size of the object
	 */
	public Point getSize();

	/** @return width of the object */
	public int getW();

	/** @return height of the object */
	public int getH();

	/** @return the redraw bounds of the box. <b>NOT</b> the same as location and size. */
	public Rectangle getBounds();

	public boolean contains(int x, int y);

	public boolean intersects(Rectangle rect);
}
