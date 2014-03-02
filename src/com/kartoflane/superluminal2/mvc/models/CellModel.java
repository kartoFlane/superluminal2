package com.kartoflane.superluminal2.mvc.models;

import org.eclipse.swt.graphics.Point;

public class CellModel extends AbstractModel {

	private static final long serialVersionUID = 585589942839286372L;

	protected final int x;
	protected final int y;

	public CellModel(int x, int y) {
		super();

		this.x = x;
		this.y = y;

		setLocModifiable(false);
		setSizeModifiable(false);
	}

	public final Point getGridLocation() {
		return new Point(x, y);
	}
}
