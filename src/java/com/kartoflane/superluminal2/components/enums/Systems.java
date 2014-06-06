package com.kartoflane.superluminal2.components.enums;

public enum Systems {
	EMPTY,
	ARTILLERY, BATTERY, CLOAKING, CLONEBAY, DOORS, DRONES, ENGINES, HACKING,
	MEDBAY, MIND, OXYGEN, PILOT, SENSORS, SHIELDS, TELEPORTER, WEAPONS;

	@Override
	public String toString() {
		return name().substring(0, 1) + name().substring(1).toLowerCase();
	}

	/** @return array of all systems, excluding {@link #EMPTY} */
	public static Systems[] getSystems() {
		return new Systems[] {
				ARTILLERY, BATTERY, CLOAKING, CLONEBAY, DOORS, DRONES, ENGINES, HACKING,
				MEDBAY, MIND, OXYGEN, PILOT, SENSORS, SHIELDS, TELEPORTER, WEAPONS,
		};
	}

	/** Returns true if the system can have a station, false otherwise. */
	public boolean canContainStation() {
		return this == Systems.SHIELDS || this == Systems.ENGINES ||
				this == Systems.WEAPONS || this == Systems.MEDBAY ||
				this == Systems.PILOT || this == Systems.DOORS ||
				this == Systems.SENSORS || this == Systems.CLONEBAY;
	}

	/** Returns true if the system can have a interior image, false otherwise */
	public boolean canContainInterior() {
		return this != Systems.EMPTY && this != Systems.CLONEBAY && this != Systems.TELEPORTER;
	}

	/** Returns true if the system can have a glow image, false otherwise. */
	public boolean canContainGlow() {
		return this == Systems.SHIELDS || this == Systems.ENGINES ||
				this == Systems.WEAPONS || this == Systems.PILOT ||
				this == Systems.CLOAKING;
	}

	public int getDefaultSlotId() {
		switch (this) {
			case PILOT:
			case DOORS:
			case SHIELDS:
				return 0;
			case WEAPONS:
			case MEDBAY:
			case CLONEBAY:
			case SENSORS:
				return 1;
			case ENGINES:
				return 2;
			default:
				return -2;
		}
	}

	public Directions getDefaultSlotDirection() {
		switch (this) {
			case ENGINES:
				return Directions.DOWN;
			case PILOT:
				return Directions.RIGHT;
			case SHIELDS:
				return Directions.LEFT;
			case WEAPONS:
			case DOORS:
			case SENSORS:
				return Directions.UP;
			case MEDBAY:
			case CLONEBAY:
				return Directions.NONE;
			default:
				return Directions.UP;
		}
	}

	public String getDefaultInteriorNamespace() {
		switch (this) {
			case ARTILLERY:
				return "room_artillery";
			case BATTERY:
				return "room_battery";
			case CLOAKING:
				return "room_cloaking";
			case DOORS:
				return "room_doors";
			case DRONES:
				return "room_drones";
			case ENGINES:
				return "room_engines";
			case HACKING:
				return "room_hacking";
			case MEDBAY:
				return "room_medbay";
			case MIND:
				return "room_mind";
			case OXYGEN:
				return "room_oxygen";
			case PILOT:
				return "room_pilot";
			case SENSORS:
				return "room_sensors";
			case SHIELDS:
				return "room_shields";
			case WEAPONS:
				return "room_weapons";
			case CLONEBAY: // always uses the clonebay "station", can't have interior
			case TELEPORTER: // always uses teleporter pads, can't have interior
			case EMPTY: // can't have interior
			default:
				return null;
		}
	}

	public String getIcon() {
		return "cpath:/assets/system/" + toString().toLowerCase() + ".png";
	}

	public String getSmallIcon() {
		return "cpath:/assets/smallsystem/" + toString().toLowerCase() + ".png";
	}
}
