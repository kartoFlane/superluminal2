package com.kartoflane.superluminal2.components.interfaces;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;


public interface Redrawable extends PaintListener
{
	/**
	 * Calls paintControl if the object is visible.
	 */
	public void redraw( PaintEvent e );
}
