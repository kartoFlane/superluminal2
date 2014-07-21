package com.kartoflane.superluminal2.components.enums;

public enum DroneTypes {
	BATTLE,
	BOARDER,
	COMBAT,
	DEFENSE,
	REPAIR,
	SHIELD,
	SHIP_REPAIR,
	/** Used for the hacking system's drone only. */
	HACKING;

	/**
	 * @return an array of all drone types, sans HACKING
	 */
	public static DroneTypes[] getPlayableDroneTypes() {
		return new DroneTypes[] {
				BATTLE, BOARDER, COMBAT, DEFENSE, REPAIR, SHIELD, SHIP_REPAIR
		};
	}

	public String toString() {
		switch (this) {
			case BATTLE:
				return "Anti-Personnel";
			case REPAIR:
				return "System Repair";
			case SHIP_REPAIR:
				return "Hull Repair";
			default:
				String s = name();
				return s.substring(0, 1) + s.substring(1).toLowerCase();
		}
	}
}