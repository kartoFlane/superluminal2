package com.kartoflane.superluminal2.ftl;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.components.Directions;

/**
 * A class representing a glow namespace.
 * 
 * @author kartoFlane
 * 
 */
public class GlowObject extends GameObject {

	private static final long serialVersionUID = -7938000186887328057L;

	public enum Glows {
		BLUE, GREEN, YELLOW
	}

	private final String identifier;
	private String imageNamespace = null;

	private String glowPathBlue;
	private String glowPathGreen;
	private String glowPathYellow;

	private int locX = 0;
	private int locY = 0;
	private Directions direction = Directions.NONE;

	public GlowObject(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setImageNamespace(String namespace) {
		imageNamespace = namespace;
	}

	public String getImageNamespace() {
		return imageNamespace;
	}

	public void setLocation(int x, int y) {
		locX = x;
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

	public void setGlowPath(String path, Glows id) {
		switch (id) {
			case BLUE:
				glowPathBlue = path;
				break;
			case GREEN:
				glowPathGreen = path;
				break;
			case YELLOW:
				glowPathYellow = path;
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	public String getGlowPath(Glows id) {
		switch (id) {
			case BLUE:
				return glowPathBlue;
			case GREEN:
				return glowPathGreen;
			case YELLOW:
				return glowPathYellow;
			default:
				throw new IllegalArgumentException();
		}
	}

	public String[] getGlowsPaths() {
		return new String[] { glowPathBlue, glowPathGreen, glowPathYellow };
	}
}
