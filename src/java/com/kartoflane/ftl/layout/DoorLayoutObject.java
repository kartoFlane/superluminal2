package com.kartoflane.ftl.layout;

/**
 * A dumb representation of a door as a layout object.
 * 
 * @author kartoFlane
 *
 */
public class DoorLayoutObject extends LayoutObject {

	/** h = 0 means horizontal, 1 means vertical */
	public DoorLayoutObject(int x, int y, int lid, int rid, int h) {
		super(LOType.DOOR, 5);
		values[0] = x;
		values[1] = y;
		values[2] = lid;
		values[3] = rid;
		values[4] = h;
	}

	public int getX() {
		return values[0];
	}

	public int getY() {
		return values[1];
	}

	public int getLeftIndex() {
		return values[2];
	}

	public int getRightIndex() {
		return values[3];
	}

	public int getHorizontal() {
		return values[4];
	}

	public boolean isHorizontal() {
		return values[4] == 0;
	}
}
