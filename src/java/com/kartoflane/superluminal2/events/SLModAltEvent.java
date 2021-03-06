package com.kartoflane.superluminal2.events;

/**
 * Indicates that the Alt modifier has been pressed or released.<br>
 * Data field is a boolean, and indicates whether the key has been pressed.
 */
public class SLModAltEvent extends SLEvent
{
	public SLModAltEvent( Object source, Boolean data )
	{
		super( MOD_ALT, source, data );
	}

	public boolean getData()
	{
		return (Boolean)data;
	}
}
