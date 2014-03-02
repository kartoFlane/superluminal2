package com.kartoflane.superluminal2.mvc.models;

import java.util.HashMap;

import org.eclipse.swt.SWT;

import com.kartoflane.superluminal2.mvc.Model;

public class SystemModel extends AbstractDependentModel {

	public enum Systems {
		EMPTY,
		ARTILLERY, CLOAK, DOORS, DRONES, ENGINES, MEDBAY,
		OXYGEN, PILOT, SENSORS, SHIELDS, TELEPORTER, WEAPONS;

		@Override
		public String toString() {
			String result = super.toString().toLowerCase();
			result = result.substring(0, 1).toUpperCase() + result.substring(1);
			return result;
		}

		/** @return array of all systems, excluding {@link #EMPTY} */
		public static Systems[] getSystems() {
			return new Systems[] {
					ARTILLERY, CLOAK, DOORS, DRONES, ENGINES, MEDBAY,
					OXYGEN, PILOT, SENSORS, SHIELDS, TELEPORTER, WEAPONS
			};
		}
	}

	public enum Glows {
		BLUE, GREEN, YELLOW
	}

	/** A constant indicating the system represented by this object */
	protected final Systems systemId;
	/** A constant representing the maximum level this system can have */
	protected final int levelCap;

	protected RoomModel room = null;
	protected StationModel station = null;

	protected int level = 0;
	protected int levelMax = 0;
	protected boolean availableAtStart = true;

	protected String interiorPath = null;
	protected HashMap<Glows, String> glowPathMap = null;

	public SystemModel(Systems id) {
		super();

		systemId = id;

		if (systemId == Systems.ARTILLERY)
			levelCap = 4;
		else if (systemId == Systems.PILOT || systemId == Systems.DOORS || systemId == Systems.SENSORS || systemId == Systems.CLOAK ||
				systemId == Systems.MEDBAY || systemId == Systems.TELEPORTER || systemId == Systems.OXYGEN)
			levelCap = 3;
		else if (systemId == Systems.SHIELDS || systemId == Systems.WEAPONS || systemId == Systems.ENGINES || systemId == Systems.DRONES)
			levelCap = 8;
		else
			levelCap = 0;

		glowPathMap = new HashMap<Glows, String>();
		for (Glows g : Glows.values()) {
			glowPathMap.put(g, null);
		}

		if (canContainStation())
			station = new StationModel(this);
	}

	public Systems getId() {
		return systemId;
	}

	public int getLevelCap() {
		return levelCap;
	}

	/** Returns true is the system is assigned to a room, false otherwise. */
	public boolean isAssigned() {
		return room != null;
	}

	public void setRoom(RoomModel model) {
		setDependency(model);
	}

	public RoomModel getRoom() {
		return (RoomModel) getDependency();
	}

	public StationModel getStation() {
		return station;
	}

	public void setAvailableAtStart(boolean available) {
		availableAtStart = available;
	}

	public boolean isAvailableAtStart() {
		return availableAtStart;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	public void setLevelMax(int level) {
		this.levelMax = level;
	}

	public int getLevelMax() {
		return levelMax;
	}

	public void setInteriorPath(String interiorPath) {
		this.interiorPath = interiorPath;
	}

	public String getInteriorPath() {
		return interiorPath;
	}

	public void setGlowPath(Glows glowId, String glowPath) {
		if (glowId == null)
			throw new IllegalArgumentException("Glow ID must not be null.");
		glowPathMap.put(glowId, glowPath);
	}

	public String getGlowPath(Glows glowId) {
		if (glowId == null)
			throw new IllegalArgumentException("Glow ID must not be null.");
		return glowPathMap.get(glowId);
	}

	@Override
	protected void setDependency(Model model) {
		room = (RoomModel) model;
	}

	@Override
	protected Model getDependency() {
		return room;
	}

	/** Returns true if the system can have a station, false otherwise. */
	public boolean canContainStation() {
		return systemId == Systems.SHIELDS || systemId == Systems.ENGINES ||
				systemId == Systems.WEAPONS || systemId == Systems.MEDBAY ||
				systemId == Systems.PILOT;
	}

	/** Returns true if the system can have a interior image, false otherwise */
	public boolean canContainInterior() {
		return systemId != Systems.EMPTY;
	}

	/** Returns true if the system can have a glow image, false otherwise. */
	public boolean canContainGlow() {
		return systemId == Systems.SHIELDS || systemId == Systems.ENGINES ||
				systemId == Systems.WEAPONS || systemId == Systems.PILOT ||
				systemId == Systems.CLOAK;
	}

	@Override
	public String toString() {
		return getSystemName(systemId);
	}

	public static String getSystemName(Systems systemId) {
		switch (systemId) {
			case EMPTY:
			case SHIELDS:
			case ENGINES:
			case OXYGEN:
			case WEAPONS:
			case DRONES:
			case MEDBAY:
			case PILOT:
			case SENSORS:
			case DOORS:
			case CLOAK:
			case ARTILLERY:
			case TELEPORTER:
				return systemId.toString();
			default:
				return "";
		}
	}

	public static int getDefaultSlotId(Systems systemId) {
		switch (systemId) {
			case ENGINES:
				return 2;
			case PILOT:
				return 0;
			case SHIELDS:
				return 0;
			case WEAPONS:
				return 1;
			case MEDBAY:
				return 1;
			default:
				return -2;
		}
	}

	public static int getDefaultSlotDirection(Systems systemId) {
		switch (systemId) {
			case ENGINES:
				return SWT.DOWN;
			case PILOT:
				return SWT.RIGHT;
			case SHIELDS:
				return SWT.LEFT;
			case WEAPONS:
				return SWT.UP;
			case MEDBAY:
				return SWT.NONE;
			default:
				return SWT.UP;
		}
	}
}
