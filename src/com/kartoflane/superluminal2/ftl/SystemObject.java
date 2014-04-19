package com.kartoflane.superluminal2.ftl;

import com.kartoflane.superluminal2.components.Directions;

public class SystemObject extends GameObject {

	public enum Systems {
		EMPTY,
		ARTILLERY, CLOAKING, DOORS, DRONES, ENGINES, MEDBAY,
		OXYGEN, PILOT, SENSORS, SHIELDS, TELEPORTER, WEAPONS,
		MIND, HACKING, BATTERY, CLONEBAY; // AE

		@Override
		public String toString() {
			String result = super.toString().toLowerCase();
			result = result.substring(0, 1).toUpperCase() + result.substring(1);
			return result;
		}

		/** @return array of all systems, excluding {@link #EMPTY} */
		public static Systems[] getSystems() {
			return new Systems[] {
					ARTILLERY, BATTERY, CLOAKING, CLONEBAY, DOORS, DRONES, ENGINES, HACKING,
					MEDBAY, MIND, OXYGEN, PILOT, SENSORS, SHIELDS, TELEPORTER, WEAPONS,
			};
		}
	}

	private static final long serialVersionUID = -4784249566049070706L;

	private final Systems id;

	private final int levelCap;
	private int levelStart = 0;
	private int levelMax = 0;
	private boolean availableAtStart = true;

	private RoomObject room;
	private StationObject station;
	private GlowObject glow;

	private String interiorPath = null;

	public SystemObject(Systems systemId) {
		if (systemId == null)
			throw new NullPointerException("System id must not be null.");
		id = systemId;

		setDeletable(false);

		switch (systemId) {
			case BATTERY:
				levelCap = 2;
				break;
			case PILOT:
			case DOORS:
			case SENSORS:
			case CLOAKING:
			case MEDBAY:
			case CLONEBAY:
			case TELEPORTER:
			case OXYGEN:
			case HACKING:
			case MIND:
				levelCap = 3;
				break;
			case ARTILLERY:
				levelCap = 4;
				break;
			case SHIELDS:
			case WEAPONS:
			case ENGINES:
			case DRONES:
				levelCap = 8;
				break;
			default:
				levelCap = 0;
		}

		if (canContainStation()) {
			station = new StationObject(this);
			station.setSlotId(getDefaultSlotId(systemId));
			station.setSlotDirection(getDefaultSlotDirection(systemId));
		}

		if (canContainInterior())
			setInteriorPath(getDefaultInterior(systemId));
	}

	public Systems getSystemId() {
		return id;
	}

	public void setRoom(RoomObject newRoom) {
		room = newRoom;
	}

	public RoomObject getRoom() {
		return room;
	}

	public StationObject getStation() {
		return station;
	}

	public GlowObject getGlow() {
		return glow;
	}

	public void setGlow(GlowObject glow) {
		this.glow = glow;
	}

	public void setInteriorPath(String path) {
		interiorPath = path;
	}

	public String getInteriorPath() {
		return interiorPath;
	}

	public int getLevelCap() {
		return levelCap;
	}

	public void setLevelStart(int level) {
		levelStart = level;
	}

	public int getLevelStart() {
		return levelStart;
	}

	public void setLevelMax(int level) {
		levelMax = level;
	}

	public int getLevelMax() {
		return levelMax;
	}

	public void setAvailable(boolean available) {
		availableAtStart = available;
	}

	public boolean isAvailable() {
		return availableAtStart;
	}

	/** Returns true is the system is assigned to a room, false otherwise. */
	public boolean isAssigned() {
		return room != null;
	}

	/** Returns true if the system can have a station, false otherwise. */
	public boolean canContainStation() {
		return id == Systems.SHIELDS || id == Systems.ENGINES ||
				id == Systems.WEAPONS || id == Systems.MEDBAY ||
				id == Systems.PILOT || id == Systems.DOORS ||
				id == Systems.SENSORS || id == Systems.CLONEBAY;
	}

	/** Returns true if the system can have a interior image, false otherwise */
	public boolean canContainInterior() {
		return id != Systems.EMPTY && id != Systems.CLONEBAY; // TODO can clonebays have interiors?
	}

	/** Returns true if the system can have a glow image, false otherwise. */
	public boolean canContainGlow() {
		return id == Systems.SHIELDS || id == Systems.ENGINES ||
				id == Systems.WEAPONS || id == Systems.PILOT ||
				id == Systems.CLOAKING;
	}

	@Override
	public String toString() {
		return id.toString();
	}

	public static int getDefaultSlotId(Systems systemId) {
		switch (systemId) {
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

	public static Directions getDefaultSlotDirection(Systems systemId) {
		switch (systemId) {
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

	public static String getDefaultInterior(Systems systemId) {
		switch (systemId) {
			case ARTILLERY:
				return "rdat:img/ship/interior/room_artillery.png";
			case BATTERY:
				return "rdat:img/ship/interior/room_battery.png";
			case CLOAKING:
				return "rdat:img/ship/interior/room_cloaking.png";
			case DOORS:
				return "rdat:img/ship/interior/room_doors.png";
			case DRONES:
				return "rdat:img/ship/interior/room_drones.png";
			case ENGINES:
				return "rdat:img/ship/interior/room_engines.png";
			case HACKING:
				return "rdat:img/ship/interior/room_hacking.png";
			case MEDBAY:
				return "rdat:img/ship/interior/room_medbay.png";
			case MIND:
				return "rdat:img/ship/interior/room_mind.png";
			case OXYGEN:
				return "rdat:img/ship/interior/room_oxygen.png";
			case PILOT:
				return "rdat:img/ship/interior/room_pilot.png";
			case SENSORS:
				return "rdat:img/ship/interior/room_sensors.png";
			case SHIELDS:
				return "rdat:img/ship/interior/room_shields.png";
			case WEAPONS:
				return "rdat:img/ship/interior/room_weapons.png";
			case CLONEBAY: // always uses the clonebay "station", not interior?
			case TELEPORTER: // can't have interior
			case EMPTY: // can't have interior
			default:
				return null;
		}
	}
}
