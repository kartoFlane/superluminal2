package com.kartoflane.superluminal2.components.interfaces;

import org.eclipse.swt.graphics.Point;


/**
 * Classes implementing this interface can be bounded to an area,
 * and cannot be moved outside of it.
 * 
 * @author kartoFlane
 * 
 */
public interface Boundable
{
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
	public void setBounded( boolean bound );

	public boolean isWithinBoundingArea( int x, int y );

	public Point limitToBoundingArea( int x, int y );
}
