package com.kartoflane.superluminal2.components.interfaces;

public interface Pinnable {

	/**
	 * Set whether the object is pinned.
	 * Pinned objects cannot be moved or resized.<br>
	 * False by default.
	 */
	public void setPinned(boolean pin);

	public boolean isPinned();
}
