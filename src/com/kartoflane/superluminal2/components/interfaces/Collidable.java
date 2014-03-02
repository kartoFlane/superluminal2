package com.kartoflane.superluminal2.components.interfaces;

/**
 * Classes implementing this interface can collide with other objects.<br>
 * 
 * @author kartoFlane
 * 
 */
public interface Collidable {

	/** Set whether the object can be collided with or not. */
	public void setCollidable(boolean collidable);

	/** @return whether or not the object can be collided with. */
	public boolean isCollidable();

	/**
	 * Check collision of this object with the given area.
	 * 
	 * @return true if the object collides with the area, false otherwise.
	 */
	public boolean collides(int x, int y, int w, int h);
}
