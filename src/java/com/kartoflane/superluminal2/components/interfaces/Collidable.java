package com.kartoflane.superluminal2.components.interfaces;

/**
 * Classes implementing this interface can collide with other objects.
 * 
 * @author kartoFlane
 * 
 */
public interface Collidable
{
	/** Set whether the object can be collided with or not. */
	public void setCollidable( boolean collidable );

	/** @return whether or not the object can be collided with. */
	public boolean isCollidable();

	/**
	 * Check collision of this object with the given area.
	 * 
	 * @return true if the object collides with the area, false otherwise.
	 */
	public boolean collides( int x, int y, int w, int h );

	/**
	 * Sets the collision tolerance for this object. The object's collidable
	 * area will be shrunk by this value.
	 * 
	 * @param px
	 */
	public void setTolerance( int px );

	/**
	 * @return the collision tolerance for this object. The object's collidable
	 *         area will be shrunk by this value.
	 */
	public int getTolerance();
}
