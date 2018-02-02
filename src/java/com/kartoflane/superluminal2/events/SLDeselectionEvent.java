package com.kartoflane.superluminal2.events;

import com.kartoflane.superluminal2.components.interfaces.Selectable;


/**
 * Indicates that the sender has been deselected.<br>
 * Data is the sender.
 */
public class SLDeselectionEvent extends SLEvent
{
	public SLDeselectionEvent( Selectable source )
	{
		super( DESELECT, source, source );
	}
}
