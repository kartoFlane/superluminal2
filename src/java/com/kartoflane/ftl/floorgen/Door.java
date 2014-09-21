package com.kartoflane.ftl.floorgen;

/**
 * Similarily to rooms, doors' coordinates indicate a tile -- however, they always "hug" the
 * left (if vertical) or top (if horizontal) wall of the tile they are positioned on.
 */
class Door {
	private final int x;
	private final int y;
	private boolean horizontal;

	Door(int x, int y, boolean h) {
		this.x = x;
		this.y = y;
		horizontal = h;
	}

	protected int getX() {
		return x;
	}

	protected int getY() {
		return y;
	}

	protected boolean isHorizontal() {
		return horizontal;
	}
}
