package com.kartoflane.superluminal2.events;

import com.kartoflane.superluminal2.components.enums.Directions;


/**
 * Indicates that the sender has been moved.<br>
 * Data is a point indicating the sender's new location.
 */
public class SLDirectionEvent extends SLEvent
{
	public SLDirectionEvent( Object source, Directions data )
	{
		super( DIRECTION, source, data );
	}

	public Directions getData()
	{
		return (Directions)data;
	}
}
