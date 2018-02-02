package com.kartoflane.superluminal2.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;

public class Hotkey {
	private Runnable onPressAction;
	private Runnable onReleaseAction;
	private boolean enabled = true;
	private boolean shift = false;
	private boolean ctrl = false;
	private boolean alt = false;
	/** the Mac Command button */
	private boolean command = false;
	private int key = '\0';

	public Hotkey() {
	}

	public Hotkey(Runnable onPress, Runnable onRelease) {
		onPressAction = onPress;
		onReleaseAction = onRelease;
	}

	/**
	 * Creates a shallow copy of the Hotkey passed in argument.
	 * 
	 * @param h
	 */
	public Hotkey(Hotkey h) {
		if (h == null)
			throw new IllegalArgumentException("Argument must not be null.");
		onPressAction = h.onPressAction;
		onReleaseAction = h.onReleaseAction;
		enabled = h.enabled;
		shift = h.shift;
		ctrl = h.ctrl;
		alt = h.alt;
		command = h.command;
		key = h.key;
	}

	public void executePress() {
		if (onPressAction == null)
			return;
		onPressAction.run();
	}

	public void executeRelease() {
		if (onReleaseAction == null)
			return;
		onReleaseAction.run();
	}

	public void setOnPress(Runnable action) {
		this.onPressAction = action;
	}

	public void setOnRelease(Runnable action) {
		this.onReleaseAction = action;
	}

	/**
	 * Sets whether the hotkey is active. An inactive hotkey is considered to not be bound to anything.
	 */
	public void setEnabled(boolean e) {
		enabled = e;
	}

	/**
	 * @return whether the hotkey is active. An inactive hotkey is considered to not be bound to anything.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets the character that this hotkey is bound to.
	 * 
	 * @param ch
	 *            the code point representing the character, according to {@link Character}
	 * 
	 * @see Character
	 */
	public void setKey(int ch) {
		key = ch;
	}

	/**
	 * @return the code point representing the character to which this hotkey is bound, according to {@link Character}
	 */
	public int getKey() {
		return key;
	}

	/**
	 * @see Character#toString(char)
	 */
	public String getKeyString() {
		if (key == '\0')
			return "";
		else
			return Character.toString((char) getKey());
	}

	/** Sets whether the hotkey's activation requires Shift modifier to be pressed. */
	public void setShift(boolean shift) {
		this.shift = shift;
	}

	public boolean getShift() {
		return shift;
	}

	/** Sets whether the hotkey's activation requires Control modifier to be pressed. */
	public void setCtrl(boolean control) {
		ctrl = control;
	}

	public boolean getCtrl() {
		return ctrl;
	}

	/** Sets whether the hotkey's activation requires Alt / Function(?) modifier to be pressed. */
	public void setAlt(boolean alt) {
		this.alt = alt;
	}

	public boolean getAlt() {
		return alt;
	}

	/** Sets whether the hotkey's activation requires Command modifier to be pressed. */
	public void setCommand(boolean command) {
		this.command = command;
	}

	public boolean getCommand() {
		return command;
	}

	/**
	 * Checks whether the hotkey is to be activated by comparing the hotkey's modifier settings with currently
	 * active modifiers, and the hotkey's trigger key with currently pressed key.
	 * 
	 * @param keyCode
	 *            int representing the currently pressed key
	 * @return true if the hotkey is tiggered, false otherwise
	 */
	public boolean passes(boolean shift, boolean ctrl, boolean alt, boolean cmd, int keyCode) {
		return this.shift == shift && this.ctrl == ctrl && this.alt == alt && this.command == cmd &&
				compareKeyCodes(key, keyCode);
	}

	private boolean compareKeyCodes(int key1, int key2) {
		if ((key1 == SWT.CR || key1 == SWT.LF || key1 == SWT.KEYPAD_CR) &&
				(key2 == SWT.CR || key2 == SWT.LF || key2 == SWT.KEYPAD_CR)) {
			return true;
		} else {
			return Character.toLowerCase(key1) == Character.toLowerCase(key2);
		}
	}

	/**
	 * Checks whether the receiver collides with the hotkey passed in argument, ie. whether their actvation
	 * requirements are the same.<br>
	 * Basically a customised {@link #equals(Object)}.
	 */
	public boolean collides(Hotkey h) {
		return shift == h.shift && ctrl == h.ctrl && alt == h.alt && command == h.command && compareKeyCodes(key, h.key);
	}

	public boolean equals(Object o) {
		if (o instanceof Hotkey == true)
			return collides((Hotkey) o);
		return super.equals(o);
	}

	@Override
	public String toString() {
		if (key == '\0')
			return "";

		String msg = "";

		if (shift)
			msg += "Shift+";
		if (ctrl)
			msg += "Ctrl+";
		if (alt)
			msg += "Alt+";
		if (command)
			msg += "⌘+";

		// @formatter:off
		switch (key) {
			case SWT.SPACE:         msg += "Spacebar"; break;
			case SWT.KEYPAD_CR:
			case SWT.CR:
			case SWT.LF:            msg += "Enter"; break;
			case SWT.BS:            msg += "Backspace"; break;
			case SWT.TAB:           msg += "Tab"; break;

			case SWT.F1:            msg += "F1"; break;
			case SWT.F2:            msg += "F2"; break;
			case SWT.F3:            msg += "F3"; break;
			case SWT.F4:            msg += "F4"; break;
			case SWT.F5:            msg += "F5"; break;
			case SWT.F6:            msg += "F6"; break;
			case SWT.F7:            msg += "F7"; break;
			case SWT.F8:            msg += "F8"; break;
			case SWT.F9:            msg += "F9"; break;
			case SWT.F10:           msg += "F10"; break;
			case SWT.F11:           msg += "F11"; break;
			case SWT.F12:           msg += "F12"; break;

			case SWT.CAPS_LOCK:     msg += "Caps Lock"; break;
			case SWT.NUM_LOCK:      msg += "Num Lock"; break;
			case SWT.SCROLL_LOCK:   msg += "Scroll Lock"; break;
			case SWT.PRINT_SCREEN:  msg += "Print Screen"; break;
			case SWT.PAUSE:         msg += "Pause"; break;
			case SWT.BREAK:         msg += "Break"; break;
			case SWT.INSERT:        msg += "Insert"; break;
			case SWT.DEL:           msg += "Delete"; break;
			case SWT.HOME:          msg += "Home"; break;
			case SWT.END:           msg += "End"; break;
			case SWT.PAGE_UP:       msg += "Page Up"; break;
			case SWT.PAGE_DOWN:     msg += "Page Down"; break;

			default:                msg += getKeyString().toUpperCase();
		}
		// @formatter:on

		return msg;
	}

	/**
	 * Adds an action to send the widget specified in the argument a Selection event, if it is enabled.
	 */
	public void addNotifyAction(final MenuItem item, boolean onPress) {
		Runnable a = new Runnable() {
			public void run() {
				if (item.isEnabled())
					item.notifyListeners(SWT.Selection, null);
			}
		};

		if (onPress)
			setOnPress(a);
		else
			setOnRelease(a);
	}

	/**
	 * Adds an action to send the widget specified in the argument a Selection event, if it is enabled.
	 */
	public void addNotifyAction(final ToolItem item, boolean onPress) {
		Runnable a = new Runnable() {
			public void run() {
				if (item.isEnabled())
					item.notifyListeners(SWT.Selection, null);
			}
		};

		if (onPress)
			setOnPress(a);
		else
			setOnRelease(a);
	}

	/**
	 * Adds an action to send the widget specified in the argument a Selection event, if it is enabled.
	 */
	public void addNotifyAction(final Button item, boolean onPress) {
		Runnable a = new Runnable() {
			public void run() {
				if (item.isEnabled())
					item.notifyListeners(SWT.Selection, null);
			}
		};

		if (onPress)
			setOnPress(a);
		else
			setOnRelease(a);
	}

	/**
	 * Adds an action to send the widget specified in the argument a Selection event and toggle it, if it is enabled.
	 */
	public void addNotifyAndToggleAction(final MenuItem item, boolean onPress) {
		Runnable a = new Runnable() {
			public void run() {
				if (item.isEnabled()) {
					item.setSelection(!item.getSelection());
					item.notifyListeners(SWT.Selection, null);
				}
			}
		};

		if (onPress)
			setOnPress(a);
		else
			setOnRelease(a);
	}

	/**
	 * Adds an action to send the widget specified in the argument a Selection event and toggle it, if it is enabled.
	 */
	public void addNotifyAndToggleAction(final ToolItem item, boolean onPress) {
		Runnable a = new Runnable() {
			public void run() {
				if (item.isEnabled()) {
					item.setSelection(!item.getSelection());
					item.notifyListeners(SWT.Selection, null);
				}
			}
		};

		if (onPress)
			setOnPress(a);
		else
			setOnRelease(a);
	}

	/**
	 * Adds an action to send the widget specified in the argument a Selection event and toggle it, if it is enabled.
	 */
	public void addNotifyAndToggleAction(final Button item, boolean onPress) {
		Runnable a = new Runnable() {
			public void run() {
				if (item.isEnabled()) {
					item.setSelection(!item.getSelection());
					item.notifyListeners(SWT.Selection, null);
				}
			}
		};

		if (onPress)
			setOnPress(a);
		else
			setOnRelease(a);
	}
}
