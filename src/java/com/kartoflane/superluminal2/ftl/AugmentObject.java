package com.kartoflane.superluminal2.ftl;

import com.kartoflane.superluminal2.components.interfaces.Identifiable;

public class AugmentObject extends GameObject implements Comparable<AugmentObject>, Identifiable {

	private static final long serialVersionUID = 7946331800522010830L;

	private final String blueprintName;
	private String title = "";
	private String description = "";

	/** Creates a default augment object. */
	public AugmentObject() {
		blueprintName = "Default Augment";
		title = "<No Augment>";
	}

	public AugmentObject(String blueprintName) {
		this.blueprintName = blueprintName;
	}

	public String getIdentifier() {
		return blueprintName;
	}

	public void update() {
		// Nothing to do here
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

	@Override
	public boolean equals(Object o) {
		if (o instanceof AugmentObject) {
			AugmentObject other = (AugmentObject) o;
			return blueprintName.equals(other.blueprintName);
		} else
			return false;
	}

	@Override
	public String toString() {
		return title;
	}
}
