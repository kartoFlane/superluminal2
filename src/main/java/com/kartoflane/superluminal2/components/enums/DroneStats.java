package com.kartoflane.superluminal2.components.enums;

public enum DroneStats {
	/** Chance to avoid colliding with a projectile and getting destroyed */
	DODGE_CHANCE,
	/** How many power bars the weapon reserves when active */
	POWER_COST,
	/** How much the weapon costs to buy */
	SCRAP_COST,
	/** The interval between the drone's actions, in miliseconds (doesn't affect combat drones) */
	COOLDOWN,
	/** The flying speed of the drone, and attack speed for combat drones */
	SPEED;

	public String getTagName() {
		switch (this) {
			case POWER_COST:
				return "power";
			case SCRAP_COST:
				return "cost";
			case DODGE_CHANCE:
				return "dodge";
			default:
				return name().toLowerCase();
		}
	}

	@Override
	public String toString() {
		switch (this) {
			case DODGE_CHANCE:
				return "Dodge chance:";
			default:
				String s = getTagName();
				s = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
				return s;
		}
	}
}
