package com.kartoflane.superluminal2.components.interfaces;

/**
 * Classes implementing this interface can be "deleted" -- or rather hidden -- and removed from
 * collections of active objects used by the application. Deleted objects can be retrieved via undo.
 * 
 * @author kartoFlane
 * 
 */
public interface Deletable
{
	/**
	 * Hide the object from view, removing it from collections of active objects,
	 * and add the object to the list of deleted objects in UndoManager, so that
	 * it can be restored or disposed (deleted completely)
	 */
	public void delete();

	/**
	 * Restore the object, registering it again in collections of active objects,
	 * and removing it from the list of deleted objects in UndoManager.
	 */
	public void restore();

	public void setDeletable( boolean deletable );

	public boolean isDeletable();
}
