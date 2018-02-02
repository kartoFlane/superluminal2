package com.kartoflane.superluminal2.events;

/**
 * Indicates that an object has been removed from the sender.<br>
 * Data is the removed object.
 */
public class SLRemoveEvent extends SLEvent
{
	public SLRemoveEvent( Object source, Object data )
	{
		super( REM_OBJECT, source, data );
	}
}
