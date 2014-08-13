package com.kartoflane.superluminal2.components.enums;

public enum DroneStats {
	/** Chance to avoid colliding with a projectile and getting destroyed */
	DODGE_CHANCE,
	/** The blueprint's rarity */
	RARITY,
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

	public String formatValue(float value) {
		switch (this) {
			case POWER_COST:
			case SCRAP_COST:
			case SPEED:
				return "" + (int) value;
			case COOLDOWN:
				return (int) value + " ms";
			case DODGE_CHANCE:
				return "" + (((int) value) * 10) + "%";
			case RARITY:
				int r = (int) value;
				switch (r) {
					case 0:
						return "Unobtainable (0)";
					case 1:
						return "Common (1)";
					case 2:
						return "Uncommon (2)";
					case 3:
						return "Unusual (3)";
					case 4:
						return "Rare (4)";
					case 5:
						return "Very Rare (5)";
					default:
						throw new IllegalArgumentException("Incorrect rarity value: " + value);
				}
			default:
				return "" + value;
		}
	}

	public boolean doesApply(DroneTypes type) {
		switch (this) {
			case COOLDOWN:
				return type == DroneTypes.DEFENSE || type == DroneTypes.SHIELD;
			case DODGE_CHANCE:
			case SPEED:
				return type != DroneTypes.BATTLE && type != DroneTypes.REPAIR && type != DroneTypes.BOARDER;
			default:
				return true;
		}
	}

	@Override
	public String toString() {
		switch (this) {
			case DODGE_CHANCE:
				return "Dodge Chance";
			case POWER_COST:
				return "Power";
			case SCRAP_COST:
				return "Cost";
			default:
				String s = getTagName();
				s = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
				return s;
		}
	}
}
