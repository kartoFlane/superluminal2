package com.kartoflane.superluminal2.ftl;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.components.enums.Directions;
import com.kartoflane.superluminal2.components.interfaces.Identifiable;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.GlowController;
import com.kartoflane.superluminal2.ui.ShipContainer;

/**
 * A class representing a glow namespace.
 * 
 * @author kartoFlane
 * 
 */
public class GlowObject extends GameObject implements Comparable<GlowObject>, Identifiable {

	private final String identifier;
	private GlowSet glowSet = Database.DEFAULT_GLOW_SET;

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
			throw new IllegalArgumentException("Direction must not be null.");
		direction = dir;
	}

	public Directions getDirection() {
		return direction;
	}

	@Override
	public int compareTo(GlowObject o) {
		return identifier.compareTo(o.identifier);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof GlowObject) {
			return identifier.equals(((GlowObject) o).identifier);
		} else {
			return false;
		}
	}

	@Override
	public void update() {
		ShipContainer container = Manager.getCurrentShip();
		GlowController glowC = (GlowController) container.getController(this);
		Point glowLoc = glowC.getGlowLocRelativeToRoom();

		// direction updated by controller's setDirection()
		setX(glowLoc.x);
		setY(glowLoc.y);
	}
}
