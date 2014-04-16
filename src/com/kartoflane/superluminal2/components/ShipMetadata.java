package com.kartoflane.superluminal2.components;

import org.jdom2.Element;

import com.kartoflane.superluminal2.core.Database;

public class ShipMetadata {

	private final Element element;
	private final boolean isPlayer;
	private final String blueprintName;
	private String shipClass = "";
	private String shipName = "";
	private String description = "";
	private String layoutTxt = null;
	private String layoutXml = null;

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

	public void setShipLayoutTXT(String layout) {
		layoutTxt = layout;
	}

	public String getShipLayoutTXT() {
		return layoutTxt;
	}

	public void setShipLayoutXML(String layout) {
		layoutXml = layout;
	}

	public String getShipLayoutXML() {
		return layoutXml;
	}
}
