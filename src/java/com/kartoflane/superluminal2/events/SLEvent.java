package com.kartoflane.superluminal2.events;

public abstract class SLEvent {

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
	/** Indicates that Command key has been pressed or released. Data is a boolean. */
	public static final int MOD_COMMAND  = 12;
	/** Indicates that the sender has been disposed. Data is the sender. */
	public static final int DISPOSE      = 13;
	/** Indicates that the sender's visibility has been changed. Data is a boolean. */
	public static final int VISIBLE      = 14;

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
		checkType(type);
		if (source == null)
			throw new IllegalArgumentException("Source must not be null.");
		this.type = type;
		this.source = source;
		this.data = data;
	}

	public static final void checkType(int type) {
		switch (type) {
			case NONE:
			case MOVE:
			case RESIZE:
			case DELETE:
			case RESTORE:
			case SELECT:
			case DESELECT:
			case ADD_OBJECT:
			case REM_OBJECT:
			case MOD_SHIFT:
			case MOD_CTRL:
			case MOD_ALT:
			case MOD_COMMAND:
			case DISPOSE:
			case VISIBLE:
				break;
			default:
				throw new IllegalArgumentException("Incorrect event type value: " + type);
		}
	}

	public static String typeToString(int type) {
		checkType(type);
		switch (type) {
			case NONE:
				return "None";
			case MOVE:
				return "Move";
			case RESIZE:
				return "Resize";
			case DELETE:
				return "Delete";
			case RESTORE:
				return "Restore";
			case SELECT:
				return "Selection";
			case DESELECT:
				return "Deselection";
			case ADD_OBJECT:
				return "Add Object";
			case REM_OBJECT:
				return "Remove Object";
			case MOD_SHIFT:
				return "Mod Shift";
			case MOD_ALT:
				return "Mod Alt";
			case MOD_CTRL:
				return "Mod Control";
			case MOD_COMMAND:
				return "Mod Command";
			case DISPOSE:
				return "Dispose";
			case VISIBLE:
				return "Visibility";
			default:
				return "";
		}
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("SLEvent: { type: ");
		buf.append(typeToString(type));
		buf.append(", source: ");
		buf.append(source);
		buf.append(", data: ");
		buf.append(data);
		buf.append(" }");
		return buf.toString();
	}
}
