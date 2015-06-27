package com.kartoflane.ftl.layout.located;

import com.kartoflane.ftl.layout.RoomLayoutObject;


public class LocatedRoomLayoutObject extends RoomLayoutObject
		implements LocatedLayoutObject {

	private final int line;

	public LocatedRoomLayoutObject(int line, int index, int x, int y, int w, int h) {
		super(index, x, y, w, h);
		this.line = line;
	}

	@Override
	public int getLine() {
		return line;
	}
}
