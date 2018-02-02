package com.kartoflane.superluminal2.events;

import org.eclipse.swt.graphics.Point;


/**
 * Indicates that the sender has been resized.<br>
 * Data is a point indicating the sender's new size.
 */
public class SLResizeEvent extends SLEvent
{
	public SLResizeEvent( Object source, Point data )
	{
		super( RESIZE, source, data );
	}

	public Point getData()
	{
		return (Point)data;
	}
}
