package com.kartoflane.superluminal2.mvc.models;

import org.eclipse.swt.graphics.Point;

@SuppressWarnings("serial")
public class CursorModel extends AbstractModel {

	protected Point mousePosition = null;

	public CursorModel() {
		super();

		setLocModifiable(false);
		setSizeModifiable(false);

		mousePosition = new Point(0, 0);
	}

	public void setMouseLocation(int x, int y) {
		mousePosition.x = x;
		mousePosition.y = y;
	}

	public Point getMouseLocation() {
		return new Point(mousePosition.x, mousePosition.y);
	}
}
