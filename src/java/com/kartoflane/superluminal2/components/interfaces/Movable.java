package com.kartoflane.superluminal2.components.interfaces;

import org.eclipse.swt.graphics.Point;


/**
 * Classes implementing this interface can be positioned on canvas and moved around.
 * 
 * @author kartoFlane
 * 
 */
public interface Movable
{
	/**
	 * Sets the location of the object to the given coordinates
	 * 
	 * @param x
	 *            the x coordinate of the new location
	 * @param y
	 *            the y coordinate of the new location
	 * @return true if location was changed, false otherwise
	 */
	public boolean setLocation( int x, int y );

	/**
	 * Moves the object by the given displacement vector.
	 * 
	 * @param dx
	 *            displacement on the X axis. Positive values move to the right, negative - to the left.
	 * @param dy
	 *            displacement on the Y axis. Positive values move downwards, negative - upwards.
	 * @return true if location was changed, false otherwise
	 */
	public boolean translate( int dx, int dy );

	/** @return a new point representing the location of the object */
	public Point getLocation();

	/** @return the x coordinate of the object's position */
	public int getX();

	/** @return the y coordinate of the object's position */
	public int getY();
}
