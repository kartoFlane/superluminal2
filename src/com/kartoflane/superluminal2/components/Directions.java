package com.kartoflane.superluminal2.components;

public enum Directions {
	UP,
	DOWN,
	LEFT,
	RIGHT,
	NONE;

	/**
	 * A non case-sensitive alternative to valueOf(String), that also
	 * interprets NO as NONE.
	 */
	public static Directions parseDir(String value) {
		value = value.toUpperCase();
		if (value.equals("NO"))
			return NONE;
		else
			return valueOf(value);
	}

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
