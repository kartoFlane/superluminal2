package com.kartoflane.superluminal2.ftl;

import com.kartoflane.superluminal2.core.Database.DroneTypes;

public class DroneObject extends GameObject implements Comparable<DroneObject> {

	private static final long serialVersionUID = -4645066316142355841L;

	private DroneTypes droneType;
	private String blueprintName;
	private String title;
	private String shortName;

	public DroneObject(String blueprint) {
		if (blueprint == null)
			throw new IllegalArgumentException("Blueprint name must not be null.");
		this.blueprintName = blueprint;
	}

	public void setType(DroneTypes type) {
		droneType = type;
	}

	public DroneTypes getType() {
		return droneType;
	}

	public void setBlueprintName(String name) {
		if (name == null)
			throw new IllegalArgumentException("Name must not be null.");
		blueprintName = name;
	}

	public String getBlueprintName() {
		return blueprintName;
	}

	public void setTitle(String title) {
		if (title == null)
			throw new IllegalArgumentException("Title must not be null.");
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setShortName(String name) {
		if (name == null)
			throw new IllegalArgumentException("Name must not be null.");
		shortName = name;
	}

	public String getShortName() {
		return shortName;
	}

	@Override
	public int compareTo(DroneObject o) {
		return blueprintName.compareTo(o.blueprintName);
	}
}
