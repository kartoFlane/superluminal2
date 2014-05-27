package com.kartoflane.superluminal2.components.enums;

import org.eclipse.swt.graphics.Point;

public enum Directions {
	UP,
	RIGHT,
	DOWN,
	LEFT,
	NONE;

	/**
	 * A non-case-sensitive alternative to valueOf(String), that also
	 * interprets NO as NONE and ignores trailing/leading whitespace.
	 */
	public static Directions parseDir(String value) {
		value = value.toUpperCase().trim();
		if (value.equals("NO"))
			return NONE;
		else
			return valueOf(value);
	}

	public Directions nextDirection() {
		return Directions.values()[(ordinal() + 1) % Directions.values().length];
	}

	@Override
	public String toString() {
		switch (this) {
			case NONE:
				return "no";
			default:
				return name().toLowerCase();
		}
	}

	/**
	 * @return angle represented by the direction, in degrees
	 */
	public int getAngleDeg() {
		return (ordinal() % Directions.values().length) * 90;
	}

	/**
	 * @return angle represented by the direction, in radians
	 */
	public float getAngleRad() {
		return (float) ((ordinal() % Directions.values().length) * Math.PI / 4);
	}

	public int getVectorX() {
		if (this == LEFT)
			return -1;
		else if (this == RIGHT)
			return 1;
		else
			return 0;
	}

	public int getVectorY() {
		if (this == UP)
			return -1;
		else if (this == DOWN)
			return 1;
		else
			return 0;
	}

	public Point getVector() {
		return new Point(getVectorX(), getVectorY());
	}
}
