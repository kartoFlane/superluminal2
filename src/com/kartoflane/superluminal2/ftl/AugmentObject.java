package com.kartoflane.superluminal2.ftl;

import com.kartoflane.superluminal2.components.interfaces.Identifiable;

public class AugmentObject extends GameObject implements Comparable<AugmentObject>, Identifiable {

	private static final long serialVersionUID = 7946331800522010830L;

	private String blueprintName;
	private String title;
	private String description;

	public AugmentObject(String blueprintName) {
		this.blueprintName = blueprintName;
	}

	public String getIdentifier() {
		return blueprintName;
	}

	public void update() {
		// Nothing to do here
	}

	public void setBlueprintName(String name) {
		if (name == null)
			throw new IllegalArgumentException("Blueprint name must not be null.");
		blueprintName = name;
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

	public void setDescription(String desc) {
		if (desc == null)
			throw new IllegalArgumentException(blueprintName + ": description must not be null.");
		description = desc;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public int compareTo(AugmentObject o) {
		return blueprintName.compareTo(o.getBlueprintName());
	}
}
