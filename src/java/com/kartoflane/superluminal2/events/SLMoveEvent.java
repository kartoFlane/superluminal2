package com.kartoflane.superluminal2.events;

import org.eclipse.swt.graphics.Point;

/**
 * Indicates that the sender has been moved.<br>
 * Data is a point indicating the sender's new location.
 */
public class SLMoveEvent extends SLEvent {
	public SLMoveEvent(Object source, Point data) {
		super(MOVE, source, data);
	}

	public Point getData() {
		return (Point) data;
	}
}
