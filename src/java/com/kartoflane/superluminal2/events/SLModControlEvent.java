package com.kartoflane.superluminal2.events;

/**
 * Indicates that the Control modifier has been pressed or released.<br>
 * Data field is a boolean, and indicates whether the key has been pressed.
 */
public class SLModControlEvent extends SLEvent
{
	public SLModControlEvent( Object source, Boolean data )
	{
		super( MOD_CTRL, source, data );
	}

	public boolean getData()
	{
		return (Boolean)data;
	}
}
