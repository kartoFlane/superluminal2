package com.kartoflane.superluminal2.ftl;

public class AugmentObject extends GameObject {

	private static final long serialVersionUID = 7946331800522010830L;

	private String blueprintName;
	private String title;
	private String description;

	public AugmentObject(String blueprintName) {
		this.blueprintName = blueprintName;
	}

	public String getBlueprintName() {
		return blueprintName;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setDescription(String desc) {
		description = desc;
	}

	public String getDescription() {
		return description;
	}
}
