package com.kartoflane.superluminal2.components.interfaces;

import org.eclipse.swt.graphics.Point;

public interface Presentable {

	/** Converts the given values to the controller's presented values. */
	public abstract Point toPresentedLocation(int x, int y);

	/** Converts the given values to the controller's presented values. */
	public abstract Point toPresentedSize(int w, int h);

	/** Converts the given presented values to normal values. */
	public abstract Point toNormalLocation(int x, int y);

	/** Converts the given presented values to normal values. */
	public abstract Point toNormalSize(int w, int h);
}
