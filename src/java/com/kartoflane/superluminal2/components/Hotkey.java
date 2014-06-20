package com.kartoflane.superluminal2.components;

import org.eclipse.swt.SWT;

import com.kartoflane.superluminal2.components.enums.Hotkeys;
import com.kartoflane.superluminal2.core.Manager;

public class Hotkey {
	private final Hotkeys id;

	private boolean shift = false;
	private boolean ctrl = false;
	private boolean alt = false;
	private int key = '\0';

	public Hotkey(Hotkeys id) {
		this.id = id;
	}

	public Hotkeys getId() {
		return id;
	}

	public void setKey(int ch) {
		key = ch;
	}

	public int getKey() {
		return key;
	}

	public void setShift(boolean shift) {
		this.shift = shift;
	}

	public boolean getShift() {
		return shift;
	}

	public void setCtrl(boolean control) {
		ctrl = control;
	}

	public boolean getCtrl() {
		return ctrl;
	}

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
	public boolean passes(int keyCode) {
		return Manager.modShift == shift && Manager.modCtrl == ctrl && Manager.modAlt == alt &&
				Character.toLowerCase(key) == Character.toLowerCase(keyCode);
	}

	public boolean collides(Hotkey h) {
		return shift == h.shift && ctrl == h.ctrl && alt == h.alt && key == h.key;
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

		if (key == ' ')
			msg += "Spacebar";
		else if (key == '\t')
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
		else
			msg += Character.toUpperCase((char) key);

		return msg;
	}
}
