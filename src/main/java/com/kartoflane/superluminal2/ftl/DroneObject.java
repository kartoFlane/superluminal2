package com.kartoflane.superluminal2.ftl;

import java.util.HashMap;

import com.kartoflane.superluminal2.components.enums.DroneStats;
import com.kartoflane.superluminal2.components.enums.DroneTypes;
import com.kartoflane.superluminal2.components.interfaces.Identifiable;

public class DroneObject extends GameObject implements Comparable<DroneObject>, Identifiable {

	private static final long serialVersionUID = -4645066316142355841L;

	private final String blueprintName;
	private DroneTypes droneType;
	private String title = "";
	private String shortName = "";
	private String description = "";

	private HashMap<DroneStats, Float> statMap = null;

	public DroneObject() {
		droneType = null;
		blueprintName = "Default Drone";
		title = "<No Drone>";
		shortName = "<No Drone>";
	}

	public DroneObject(String blueprint) {
		blueprintName = blueprint;
	}

	@Override
	public String getIdentifier() {
		return blueprintName;
	}

	public void update() {
		// Nothing to do here
	}

	public void setType(DroneTypes type) {
		droneType = type;
	}

	public DroneTypes getType() {
		return droneType;
	}

	public String getBlueprintName() {
		return blueprintName;
	}

	public void setTitle(String title) {
		if (title == null)
			throw new IllegalArgumentException(blueprintName + ": title must not be null.");
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setShortName(String name) {
		if (name == null)
			throw new IllegalArgumentException(blueprintName + ": name must not be null.");
		shortName = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setDescription(String desc) {
		if (desc == null)
			throw new IllegalArgumentException(blueprintName + ": description must not be null.");
		description = desc;
	}

	public String getDescription() {
		return description;
	}

	public void setStat(DroneStats stat, float value) {
		if (stat == null)
			throw new IllegalArgumentException("Stat type must not be null.");
		if (statMap == null)
			initStatMap();
		statMap.put(stat, value);
	}

	public float getStat(DroneStats stat) {
		if (stat == null)
			throw new IllegalArgumentException("Stat type must not be null.");
		if (statMap == null)
			initStatMap();
		return statMap.get(stat);
	}

	private void initStatMap() {
		statMap = new HashMap<DroneStats, Float>();
		for (DroneStats stat : DroneStats.values())
			statMap.put(stat, 0f);
	}

	@Override
	public String toString() {
		return shortName;
	}

	@Override
	public int compareTo(DroneObject o) {
		return blueprintName.compareTo(o.blueprintName);
	}
}
