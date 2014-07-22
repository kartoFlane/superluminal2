package com.kartoflane.superluminal2.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.widgets.Shell;

public class KeybindHandler {

	private HashMap<Shell, ArrayList<Hotkey>> keyMap = null;

	public KeybindHandler() {
	}

	@SuppressWarnings("unchecked")
	public List<Hotkey> getHotkeys(Shell shell) {
		if (keyMap == null)
			return new ArrayList<Hotkey>();

		ArrayList<Hotkey> result = keyMap.get(shell);
		if (result == null)
			return new ArrayList<Hotkey>();

		return (List<Hotkey>) result.clone();
	}

	public void hook(Shell shell, Hotkey hotkey) {
		if (shell == null || hotkey == null)
			throw new IllegalArgumentException("Argument must not be null.");

		if (keyMap == null)
			keyMap = new HashMap<Shell, ArrayList<Hotkey>>();
		if (keyMap.get(shell) == null)
			keyMap.put(shell, new ArrayList<Hotkey>());

		keyMap.get(shell).add(hotkey);
	}

	public boolean hooks(Shell shell) {
		if (keyMap == null)
			return false;

		return keyMap.containsKey(shell);
	}

	public void notify(Shell shell, boolean shift, boolean ctrl, boolean alt, int key) {
		if (shell == null)
			throw new IllegalArgumentException("Shell must not be null.");
		if (keyMap == null)
			return;
		if (!hooks(shell))
			return;

		for (Hotkey h : keyMap.get(shell)) {
			if (h.isEnabled() && h.passes(shift, ctrl, alt, key)) {
				h.execute();
				break;
			}
		}
	}

	public int size() {
		if (keyMap == null)
			return 0;
		return keyMap.size();
	}

	public void unhook(Shell shell, Hotkey hotkey) {
		if (keyMap == null)
			return;
		if (hooks(shell))
			keyMap.get(shell).remove(hotkey);
	}

	public void unhook(Shell shell) {
		if (keyMap == null)
			return;
		keyMap.remove(shell);
	}

	public void dispose() {
		if (keyMap != null)
			keyMap.clear();
		keyMap = null;
	}
}
