package com.kartoflane.superluminal2.components.enums;

/**
 * Values conform with {@link org.eclipse.swt.SWT SWT}'s constants
 * 
 * @author kartoFlane
 * 
 * @see {@link org.eclipse.swt.SWT#ALT SWT.ALT}
 * @see {@link org.eclipse.swt.SWT#SHIFT SWT.SHIFT}
 * @see {@link org.eclipse.swt.SWT#CONTROL SWT.CONTROL}
 * @see {@link org.eclipse.swt.SWT#COMMAND SWT.COMMAND}
 *
 */
public enum Modifiers {
	// @formatter:off
	ALT     (1 << 16),
	SHIFT   (1 << 17),
	CONTROL (1 << 18),
	COMMAND (1 << 22);
	// @formatter:on

	private final int _id;

	private Modifiers(int id) {
		_id = id;
	}

	public int id() {
		return _id;
	}

	public int getSWTMask() {
		return _id;
	}

	@Override
	public String toString() {
		switch (this) {
			case ALT:
				return "Alt";
			case SHIFT:
				return "Shift";
			case CONTROL:
				return "Control";
			case COMMAND:
				return "Command";

			default:
				return "";
		}
	}
}
