package com.kartoflane.superluminal2.events;

import com.kartoflane.superluminal2.components.interfaces.Selectable;


/**
 * Indicates that the sender has been selected.<br>
 * Data is the sender.
 */
public class SLSelectionEvent extends SLEvent
{
	public SLSelectionEvent( Selectable source )
	{
		super( SELECT, source, source );
	}
}
