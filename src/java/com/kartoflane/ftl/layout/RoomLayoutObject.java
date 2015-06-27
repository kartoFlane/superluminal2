package com.kartoflane.ftl.layout;

/**
 * A dumb representation of a room as a layout object.
 * 
 * @author kartoFlane
 *
 */
public class RoomLayoutObject extends LayoutObject {

	public RoomLayoutObject(int index, int x, int y, int w, int h) {
		super(LOType.ROOM, 5);
		values[0] = index;
		values[1] = x;
		values[2] = y;
		values[3] = w;
		values[4] = h;
	}

	public int getIndex() {
		return values[0];
	}

	public int getX() {
		return values[1];
	}

	public int getY() {
		return values[2];
	}

	public int getW() {
		return values[3];
	}

	public int getH() {
		return values[4];
	}
}
