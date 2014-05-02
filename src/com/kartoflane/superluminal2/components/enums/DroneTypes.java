package com.kartoflane.superluminal2.components.enums;

public enum DroneTypes {
	COMBAT,
	SHIP_REPAIR,
	BOARDER,
	BATTLE,
	REPAIR,
	DEFENSE,
	SHIELD,
	/** Used for the hacking system's drone only. */
	HACKING;

	/**
	 * @return an array of all drone types, sans HACKING
	 */
	public static DroneTypes[] getPlayableDroneTypes() {
		return new DroneTypes[] { COMBAT, SHIP_REPAIR, BOARDER, BATTLE, REPAIR, DEFENSE, SHIELD };
	}
}