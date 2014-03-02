package com.kartoflane.superluminal2.mvc;

import com.kartoflane.superluminal2.components.interfaces.Resizable;

public interface Model extends Resizable {

	/**
	 * Flags this box's location value as modifiable via the sidebar UI.<br>
	 * True by default.
	 */
	public void setLocModifiable(boolean b);

	/** Whether this box's location value can be modified via the sidebar UI. */
	public boolean isLocModifiable();

	/**
	 * Flags this box's size value as modifiable via the sidebar UI.<br>
	 * True by default.
	 */
	public void setSizeModifiable(boolean b);

	/** Whether this box's size value can be modified via the sidebar UI. */
	public boolean isSizeModifiable();
}
