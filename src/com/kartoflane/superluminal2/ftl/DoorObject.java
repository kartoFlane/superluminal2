package com.kartoflane.superluminal2.ftl;

import java.io.Serializable;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.components.interfaces.Alias;
import com.kartoflane.superluminal2.components.interfaces.Movable;

public class DoorObject extends GameObject implements Alias, Movable, Serializable {

	private static final long serialVersionUID = 6051451407440322891L;

	public static final int DOOR_WIDTH = 27;
	public static final int DOOR_HEIGHT = 8;

	private RoomObject leftRoom = null;
	private RoomObject rightRoom = null;
	private boolean horizontal = false;

	private int locX = 0;
	private int locY = 0;

	private String alias;

	public DoorObject() {
		setDeletable(true);
	}

	public DoorObject(boolean horizontal) {
		this();
		setHorizontal(horizontal);
	}

	/**
	 * Sets the distance from the ship anchor, in grid cells.
	 */
	@Override
	public boolean setLocation(int x, int y) {
		locX = x;
		locY = y;
		return true;
	}

	/**
	 * Moves the door by the specified number of grid cells.
	 */
	@Override
	public boolean translate(int dx, int dy) {
		locX += dx;
		locY += dy;
		return true;
	}

	/**
	 * @return the distance from the ship anchor, in grid cells.
	 */
	@Override
	public Point getLocation() {
		return new Point(locX, locY);
	}

	@Override
	public int getX() {
		return locX;
	}

	@Override
	public int getY() {
		return locY;
	}

	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
	}

	public boolean isHorizontal() {
		return horizontal;
	}

	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setLeftRoom(RoomObject room) {
		leftRoom = room;
	}

	public void setRightRoom(RoomObject room) {
		rightRoom = room;
	}

	public RoomObject getLeftRoom() {
		return leftRoom;
	}

	public RoomObject getRightRoom() {
		return rightRoom;
	}
}
