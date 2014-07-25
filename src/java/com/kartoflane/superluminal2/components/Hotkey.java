package com.kartoflane.superluminal2.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;

import com.kartoflane.superluminal2.components.interfaces.Action;

public class Hotkey {
	private Action onPressAction;
	private Action onReleaseAction;
	private boolean enabled = true;
	private boolean shift = false;
	private boolean ctrl = false;
	private boolean alt = false;
	private int key = '\0';

	public Hotkey() {
	}

	public Hotkey(Action onPress, Action onRelease) {
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
		alt = h.ctrl;
		key = h.key;
	}

	public void executePress() {
		if (onPressAction == null)
			return;
		onPressAction.execute();
	}

	public void executeRelease() {
		if (onReleaseAction == null)
			return;
		onReleaseAction.execute();
	}

	public void setOnPress(Action action) {
		this.onPressAction = action;
	}

	public void setOnRelease(Action action) {
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

	/**
	 * Checks whether the hotkey is to be activated by comparing the hotkey's modifier settings with currently
	 * active modifiers, and the hotkey's trigger key with currently pressed key.
	 * 
	 * @param keyCode
	 *            int representing the currently pressed key
	 * @return true if the hotkey is tiggered, false otherwise
	 */
	public boolean passes(boolean shift, boolean ctrl, boolean alt, int keyCode) {
		return this.shift == shift && this.ctrl == ctrl && this.alt == alt &&
				Character.toLowerCase(key) == Character.toLowerCase(keyCode);
	}

	/**
	 * Checks whether the receiver collides with the hotkey passed in argument, ie. whether their actvation
	 * requirements are the same.<br>
	 * Basically a customised {@link #equals(Object)}.
	 */
	public boolean collides(Hotkey h) {
		return shift == h.shift && ctrl == h.ctrl && alt == h.alt && key == h.key;
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

		if (key == SWT.SPACE)
			msg += "Spacebar";
		else if (key == SWT.KEYPAD_CR || key == SWT.CR)
			msg += "Enter";
		else if (key == SWT.BS)
			msg += "Backspace";
		else if (key == SWT.TAB)
			msg += "Tab";
		else if (key == SWT.F1)
			msg += "F1";
		else if (key == SWT.F2)
			msg += "F2";
		else if (key == SWT.F3)
			msg += "F3";
		else if (key == SWT.F4)
			msg += "F4";
		else if (key == SWT.F5)
			msg += "F5";
		else if (key == SWT.F6)
			msg += "F6";
		else if (key == SWT.F7)
			msg += "F7";
		else if (key == SWT.F8)
			msg += "F8";
		else if (key == SWT.F9)
			msg += "F9";
		else if (key == SWT.F10)
			msg += "F10";
		else if (key == SWT.F11)
			msg += "F11";
		else if (key == SWT.F12)
			msg += "F12";
		else if (key == SWT.CAPS_LOCK)
			msg += "Caps Lock";
		else if (key == SWT.NUM_LOCK)
			msg += "Num Lock";
		else if (key == SWT.SCROLL_LOCK)
			msg += "Scroll Lock";
		else if (key == SWT.PRINT_SCREEN)
			msg += "Print Screen";
		else if (key == SWT.PAUSE)
			msg += "Pause";
		else if (key == SWT.BREAK)
			msg += "Break";
		else if (key == SWT.INSERT)
			msg += "Insert";
		else if (key == SWT.DEL)
			msg += "Delete";
		else if (key == SWT.HOME)
			msg += "Home";
		else if (key == SWT.END)
			msg += "End";
		else if (key == SWT.PAGE_UP)
			msg += "Page Up";
		else if (key == SWT.PAGE_DOWN)
			msg += "Page Down";
		else
			msg += getKeyString().toUpperCase();

		return msg;
	}

	/**
	 * Adds an action to send the widget specified in the argument a Selection event, if it is enabled.
	 */
	public void addNotifyAction(final MenuItem item, boolean onPress) {
		Action a = new Action() {
			public void execute() {
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
		Action a = new Action() {
			public void execute() {
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
		Action a = new Action() {
			public void execute() {
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
		Action a = new Action() {
			public void execute() {
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
		Action a = new Action() {
			public void execute() {
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
		Action a = new Action() {
			public void execute() {
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
