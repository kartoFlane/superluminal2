package com.kartoflane.superluminal2.events;

/**
 * Indicates that the Shift modifier has been pressed or released.<br>
 * Data field is a boolean, and indicates whether the key has been pressed.
 */
public class SLModShiftEvent extends SLEvent
{
	public SLModShiftEvent( Object source, Boolean data )
	{
		super( MOD_SHIFT, source, data );
	}

	public boolean getData()
	{
		return (Boolean)data;
	}
}
