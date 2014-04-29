package com.kartoflane.superluminal2.ftl;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.components.Directions;
import com.kartoflane.superluminal2.components.interfaces.Identifiable;

/**
 * A class representing a glow namespace.
 * 
 * @author kartoFlane
 * 
 */
public class GlowObject implements Comparable<GlowObject>, Identifiable {

	private final String identifier;
	private String imageNamespace = null;

	private int locX = 0;
	private int locY = 0;
	private Directions direction = Directions.NONE;

	public GlowObject() {
		this("Default Glow");
	}

	public GlowObject(String identifier) {
		if (identifier == null)
			throw new IllegalArgumentException("Identifier must not be null.");
		this.identifier = identifier;
		setGlowImageNamespace(identifier);
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	public void setGlowImageNamespace(String namespace) {
		if (namespace == null)
			throw new IllegalArgumentException("Namespace must not be null.");
		imageNamespace = namespace;
	}

	public String getGlowImageNamespace() {
		return imageNamespace;
	}

	public String getGlowImagePath() {
		return "img/ship/interior/" + imageNamespace + ".png";
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

	@Override
	public int compareTo(GlowObject o) {
		return identifier.compareTo(o.identifier);
	}
}
