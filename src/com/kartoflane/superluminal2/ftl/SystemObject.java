package com.kartoflane.superluminal2.ftl;

import com.kartoflane.superluminal2.components.Directions;
import com.kartoflane.superluminal2.components.Systems;

public class SystemObject extends GameObject {

	private static final long serialVersionUID = -4784249566049070706L;

	private final Systems id;

	private final int levelCap;
	private int levelStart = 0;
	private int levelMax = 0;
	private boolean availableAtStart = true;

	private RoomObject room;
	private StationObject station;
	private GlowObject glow;

	private String interiorNamespace = null;
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
			setInteriorNamespace(getDefaultInteriorNamespace(systemId));
	}

	public void update() {
		// Nothing to do here
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

	public void setInteriorNamespace(String namespace) {
		interiorNamespace = namespace;
		if (namespace == null)
			interiorPath = null;
		else
			interiorPath = "rdat:img/ship/interior/" + namespace + ".png";
	}

	public String getInteriorNamespace() {
		return interiorNamespace;
	}

	/**
	 * Sets path to the interior image if it is not located in the game's archives.
	 * 
	 * @param path
	 *            path to the interior image in the filesystem, or null to use dat archive location
	 */
	public void setInteriorPath(String path) {
		interiorPath = path;
	}

	/**
	 * @return Path to the interior image, or null if not set.
	 */
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
		return id.canContainStation();
	}

	/** Returns true if the system can have a interior image, false otherwise */
	public boolean canContainInterior() {
		return id.canContainInterior();
	}

	/** Returns true if the system can have a glow image, false otherwise. */
	public boolean canContainGlow() {
		return id.canContainGlow();
	}

	@Override
	public String toString() {
		return id.toString();
	}

	public static int getDefaultSlotId(Systems systemId) {
		return systemId.getDefaultSlotId();
	}

	public static Directions getDefaultSlotDirection(Systems systemId) {
		return systemId.getDefaultSlotDirection();
	}

	public static String getDefaultInteriorNamespace(Systems systemId) {
		return systemId.getDefaultInteriorNamespace();
	}
}
