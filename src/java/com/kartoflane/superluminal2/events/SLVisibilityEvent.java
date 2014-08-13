package com.kartoflane.superluminal2.events;

/**
 * Indicates that the sender's visibility has been changed.<br>
 * Data field is a boolean, and indicates the sender's new visible state.
 */
public class SLVisibilityEvent extends SLEvent {

	public SLVisibilityEvent(Object source, Boolean data) {
		super(VISIBLE, source, data);
	}

	public boolean getData() {
		return (Boolean) data;
	}
}
