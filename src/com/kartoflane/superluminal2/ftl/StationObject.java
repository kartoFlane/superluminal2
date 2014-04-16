package com.kartoflane.superluminal2.ftl;

import com.kartoflane.superluminal2.components.Directions;

public class StationObject extends GameObject {

	private static final long serialVersionUID = 6671265790779853629L;

	/**
	 * Id of the tile on which the station will be located.<br>
	 * -2 = use default value<br>
	 * -1 = no station<br>
	 * A 2x2 room has slot ids like this:
	 * <p>
	 * [ 0 1 ]<br>
	 * [ 2 3 ]
	 * </p>
	 */
	protected int slotId = -2;
	protected Directions slotDirection = Directions.UP;

	public StationObject(SystemObject object) {
	}

	/**
	 * Id of the tile on which the station will be located.<br>
	 * -2 = use default value<br>
	 * -1 = no station<br>
	 * A 2x2 room has slot ids like this:
	 * <p>
	 * [ 0 1 ]<br>
	 * [ 2 3 ]
	 * </p>
	 */
	public void setSlotId(int id) {
		slotId = id;
	}

	public int getSlotId() {
		return slotId;
	}

	public void setSlotDirection(Directions dir) {
		if (dir == null)
			throw new NullPointerException("Direction must not be null.");
		slotDirection = dir;
	}

	public Directions getSlotDirection() {
		return slotDirection;
	}
}
