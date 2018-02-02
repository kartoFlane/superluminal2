package com.kartoflane.superluminal2.events;

/**
 * Indicates that an object has been added to the sender.<br>
 * Data is the added object.
 */
public class SLAddEvent extends SLEvent
{
	public SLAddEvent( Object source, Object data )
	{
		super( ADD_OBJECT, source, data );
	}
}
