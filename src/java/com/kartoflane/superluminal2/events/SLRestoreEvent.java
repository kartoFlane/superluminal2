package com.kartoflane.superluminal2.events;

/**
 * Indicates that the sender has been restored.<br>
 * Data is the sender.
 */
public class SLRestoreEvent extends SLEvent {
	public SLRestoreEvent(Object source) {
		super(RESTORE, source, source);
	}
}
