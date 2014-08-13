package com.kartoflane.superluminal2.events;

/**
 * Indicates that the sender has been disposed.<br>
 * Data is the sender.
 */
public class SLDisposeEvent extends SLEvent {
	public SLDisposeEvent(Object source) {
		super(DISPOSE, source, source);
	}
}
