package com.kartoflane.superluminal2.events;

public class SLEvent {

	// @formatter:off
	
	public static final int NONE         =  0;
	/** Indicates that the sender was moved. Data is a point. */
	public static final int MOVE         =  1;
	/** Indicates that the sender was resized. Data is a point. */
	public static final int RESIZE       =  2;
	/** Indicates that the sender was deleted. Data is the sender. */
	public static final int DELETE       =  3;
	/** Indicates that the sender was restored. Data is the sender. */
	public static final int RESTORE      =  4;
	/** Indicates that the sender was selected. Data is the sender. */
	public static final int SELECT       =  5;
	/** Indicates that the sender was deselected. Data is the sender.*/
	public static final int DESELECT     =  6;
	/** Indicates that an object has been added to the sender. Data is the object. */
	public static final int ADD_OBJECT   =  7;
	/** Indicates that an object has been removed from the sender. Data is the object. */
	public static final int REM_OBJECT   =  8;
	/** Indicates that Shift key has been pressed or released. Data is a boolean. */
	public static final int MOD_SHIFT    =  9;
	/** Indicates that Control key has been pressed or released. Data is a boolean. */
	public static final int MOD_CTRL     = 10;
	/** Indicates that Alt key has been pressed or released. Data is a boolean. */
	public static final int MOD_ALT      = 11;
	/** Indicates that the sender has been disposed. Data is the sender. */
	public static final int DISPOSE      = 12;
	/** Indicates that the sender's visibility has been changed. Data is a boolean. */
	public static final int VISIBLE      = 13;

	// @formatter:on

	/** Type of the event, dictating how the data field should be interpreted. */
	public final int type;
	/** The object that sent the event */
	public final Object source;
	/**
	 * The data of the event - this field is used to pass information that is important to the event,
	 * like new location in a MOVE event.
	 */
	public final Object data;

	/**
	 * Constructs a new event with the specified parameters.
	 * 
	 * @param type
	 *            the type of the event
	 * @param source
	 *            the object in which the event originated
	 * @param data
	 *            the data of the event, that can be used to pass information that is important to the event
	 */
	public SLEvent(int type, Object source, Object data) {
		this.type = type;
		this.source = source;
		this.data = data;
	}
}
