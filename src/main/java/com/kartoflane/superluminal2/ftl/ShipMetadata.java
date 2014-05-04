package com.kartoflane.superluminal2.ftl;

import org.jdom2.Element;

import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.core.Utils;

public class ShipMetadata {

	private final Element element;
	private final boolean isPlayer;
	private final String blueprintName;
	private String shipClass = "";
	private String shipName = "";
	private String description = "";
	private String imageNamespace = "";

	public ShipMetadata(Element element, String blueprintName) {
		isPlayer = Database.getInstance().isPlayerShip(blueprintName);
		this.blueprintName = blueprintName;
		this.element = element;
	}

	public Element getElement() {
		return element;
	}

	public boolean isPlayerShip() {
		return isPlayer;
	}

	public String getBlueprintName() {
		return blueprintName;
	}

	public void setShipClass(String className) {
		if (className == null)
			throw new IllegalArgumentException("Class name must not be null.");
		shipClass = className;
	}

	public String getShipClass() {
		return shipClass;
	}

	public void setShipName(String name) {
		if (name == null)
			throw new IllegalArgumentException("Ship name must not be null.");
		shipName = name;
	}

	public String getShipName() {
		return shipName;
	}

	public void setShipDescription(String desc) {
		if (desc == null)
			throw new IllegalArgumentException("Description must not be null.");
		description = desc;
	}

	public String getShipDescription() {
		return description;
	}

	public void setShipImageNamespace(String namespace) {
		if (namespace == null)
			throw new IllegalArgumentException("Image namespace must not be null.");
		imageNamespace = namespace;
	}

	public String getShipImageNamespace() {
		return imageNamespace;
	}

	public String getHullImagePath() {
		return firstExisting(imageNamespace + "_base.png", Database.getInstance());
	}

	private static String firstExisting(String suffix, Database db) {
		String[] prefixes = { "db:img/ship/", "db:img/ships_glow/", "db:img/ships_noglow/" };
		for (String prefix : prefixes) {
			if (db.contains(Utils.trimProtocol(prefix) + suffix))
				return prefix + suffix;
		}
		return null;
	}
}
