package com.kartoflane.superluminal2.events;

/**
 * Indicates that the sender has been deleted.<br>
 * Data is the sender.
 */
public class SLDeleteEvent extends SLEvent {
	public SLDeleteEvent(Object source) {
		super(DELETE, source, source);
	}
}
