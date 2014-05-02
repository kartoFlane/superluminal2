package com.kartoflane.superluminal2.ftl;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.components.enums.Directions;
import com.kartoflane.superluminal2.components.interfaces.Identifiable;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.ui.ShipContainer;

/**
 * A class representing a glow namespace.
 * 
 * @author kartoFlane
 * 
 */
public class GlowObject implements Comparable<GlowObject>, Identifiable {

	private final String identifier;
	private GlowSet glowSet = null;

	private int locX = 0;
	private int locY = 0;
	private Directions direction = Directions.NONE;

	/**
	 * Creates the default glow object.
	 */
	public GlowObject() {
		this("Default Glow");
	}

	public GlowObject(String identifier) {
		if (identifier == null)
			throw new IllegalArgumentException("Identifier must not be null.");
		this.identifier = identifier;
		setGlowSet(Database.DEFAULT_GLOW_SET);
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	public void setGlowSet(GlowSet set) {
		if (set == null)
			throw new IllegalArgumentException("Argument must not be null.");
		glowSet = set;
	}

	public GlowSet getGlowSet() {
		return glowSet;
	}

	public void setLocation(int x, int y) {
		locX = x;
		locY = y;
	}

	public void setX(int x) {
		locX = x;
	}

	public void setY(int y) {
		locY = y;
	}

	public Point getLocation() {
		return new Point(locX, locY);
	}

	public int getX() {
		return locX;
	}

	public int getY() {
		return locY;
	}

	public void setDirection(Directions dir) {
		if (dir == null)
			throw new NullPointerException("Direction must not be null.");
		direction = dir;
	}

	public Directions getDirection() {
		return direction;
	}

	/**
	 * Sets the location and direction of the glow in accordance with the data
	 * stored in the room and station supplied in argument.
	 * 
	 * @param room
	 *            the room in which the station is placed
	 * @param station
	 *            the station to which the glow is assigned
	 */
	public void setData(RoomObject room, StationObject station) {
		// TODO
		Point glowLoc = room.getSlotLocation(station.getSlotId());
		glowLoc.x -= ShipContainer.CELL_SIZE / 2;
		glowLoc.y -= ShipContainer.CELL_SIZE / 2;
		Point roomLoc = room.getLocation();
		roomLoc.x = roomLoc.x * ShipContainer.CELL_SIZE;
		roomLoc.y = roomLoc.y * ShipContainer.CELL_SIZE;

		glowLoc.x -= roomLoc.x;
		glowLoc.y -= roomLoc.y;

		switch (station.getSlotDirection()) {
			case UP:
				glowLoc.x += 5;
				glowLoc.y += 0;
				break;
			case RIGHT:
				glowLoc.x += 18;
				glowLoc.y += 7;
				break;
			case DOWN:
				glowLoc.x += 5;
				glowLoc.y += 18;
				break;
			case LEFT:
				glowLoc.x += 0;
				glowLoc.y += 9;
				break;
			default:
				break;
		}

		setDirection(station.getSlotDirection());
		setX(glowLoc.x);
		setY(glowLoc.y);
	}

	@Override
	public int compareTo(GlowObject o) {
		return identifier.compareTo(o.identifier);
	}
}
