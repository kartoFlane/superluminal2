package com.kartoflane.superluminal2.events;

/**
 * Indicates that the Command modifier has been pressed or released.<br>
 * Data field is a boolean, and indicates whether the key has been pressed.
 */
public class SLModCommandEvent extends SLEvent {

	public SLModCommandEvent(Object source, Boolean data) {
		super(MOD_COMMAND, source, data);
	}

	public boolean getData() {
		return (Boolean) data;
	}
}
