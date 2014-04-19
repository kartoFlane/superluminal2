package com.kartoflane.superluminal2.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.JDOMParseException;

import com.kartoflane.superluminal2.components.ShipMetadata;
import com.kartoflane.superluminal2.core.Database.DroneTypes;
import com.kartoflane.superluminal2.core.Database.WeaponTypes;
import com.kartoflane.superluminal2.core.Utils.DecodeResult;
import com.kartoflane.superluminal2.ftl.DroneObject;
import com.kartoflane.superluminal2.ftl.WeaponObject;

public class DataUtils {

	public static final Logger log = LogManager.getLogger(DataUtils.class);

	/**
	 * Finds all ships with the specified blueprintName within the file.
	 * 
	 * @param f
	 * @param blueprintName
	 * @return
	 * @throws JDOMParseException
	 *             when the contents of the stream could not be parsed.
	 * @throws IOException
	 */
	public static ArrayList<Element> findShipsWithName(String blueprintName, InputStream is, String fileName)
			throws IllegalArgumentException, JDOMParseException, IOException {
		if (blueprintName == null)
			throw new IllegalArgumentException("Blueprint name must not be null.");

		DecodeResult dr = Utils.decodeText(is, null);
		String contents = dr.text;
		Document doc = Utils.parseXML(contents);

		ArrayList<Element> shipList = new ArrayList<Element>();

		Element root = doc.getRootElement();

		for (Element e : root.getChildren("shipBlueprint")) {
			String blueprint = e.getAttributeValue("name");

			if (blueprint != null && blueprint.equals(blueprintName))
				shipList.add(e);
		}

		return shipList;
	}

	public static ArrayList<Element> findShipsWithName(File f, String blueprintName)
			throws IllegalArgumentException, JDOMParseException, IOException {
		return findShipsWithName(blueprintName, new FileInputStream(f), f.getName());
	}

	public static ArrayList<Element> findTagsNamed(String contents, String tagName) throws JDOMParseException {
		Document doc = null;
		try {
			doc = Utils.parseXML(contents);
		} catch (JDOMParseException e) {
			System.out.println(contents);
			throw e;
		}
		ArrayList<Element> tagList = new ArrayList<Element>();

		Element root = doc.getRootElement();

		for (Element e : root.getChildren(tagName))
			tagList.add(e);

		return tagList;
	}

	public static ArrayList<Element> findTagsNamed(InputStream is, String fileName, String tagName)
			throws IllegalArgumentException, JDOMParseException, IOException {
		DecodeResult dr = Utils.decodeText(is, fileName);
		return findTagsNamed(dr.text, tagName);
	}

	public static ArrayList<Element> findTagsNamed(File f, String tagName)
			throws IllegalArgumentException, JDOMParseException, IOException {
		return findTagsNamed(new FileInputStream(f), f.getName(), tagName);
	}

	/**
	 * Loads the ship's metadata from the supplied Element
	 * 
	 * @param e
	 *            XML element for the shipBlueprint tag
	 * @return the ship's metadata - blueprint name, txt and xml layouts, name, class, description
	 */
	public static ShipMetadata loadShipMetadata(Element e) {
		if (e == null)
			throw new IllegalArgumentException("Element must not be null.");

		String attr = null;
		Element child = null;

		attr = e.getAttributeValue("name");
		if (attr == null)
			throw new IllegalArgumentException("Unidentified: missing 'name' attribute.");
		ShipMetadata metadata = new ShipMetadata(e, attr);

		attr = e.getAttributeValue("layout");
		if (attr == null)
			throw new IllegalArgumentException(metadata.getBlueprintName() + ": missing 'layout' attribute.");
		metadata.setShipLayoutTXT(attr);

		attr = e.getAttributeValue("img");
		if (attr == null)
			throw new IllegalArgumentException(metadata.getBlueprintName() + ": missing 'img' attribute.");
		metadata.setShipLayoutXML(attr);

		child = e.getChild("class");
		if (child == null)
			throw new IllegalArgumentException(metadata.getBlueprintName() + ": missing <class> tag.");
		metadata.setShipClass(child.getValue());

		if (metadata.isPlayerShip()) {
			child = e.getChild("name");
			if (child == null)
				throw new IllegalArgumentException(metadata.getBlueprintName() + ": missing <name> tag.");
			metadata.setShipName(child.getValue());

			child = e.getChild("desc");
			if (child == null)
				metadata.setShipDescription("<desc> tag was missing!");
			else
				metadata.setShipDescription(child.getValue());
		}

		return metadata;
	}

	public static WeaponObject loadWeapon(Element e) {
		if (e == null)
			throw new IllegalArgumentException("Element must not be null.");

		String attr = null;
		Element child = null;

		attr = e.getAttributeValue("name");
		if (attr == null)
			throw new IllegalArgumentException("Unidentified: missing 'name' attribute.");
		WeaponObject weapon = new WeaponObject(attr);

		child = e.getChild("type");
		if (child == null)
			throw new IllegalArgumentException(weapon.getBlueprintName() + ": missing <type> tag.");
		weapon.setType(WeaponTypes.valueOf(child.getValue()));

		child = e.getChild("title");
		if (child == null)
			throw new IllegalArgumentException(weapon.getBlueprintName() + ": missing <title> tag.");
		weapon.setTitle(child.getValue());

		child = e.getChild("short");
		if (child == null)
			weapon.setShortName("N/A");
		else
			weapon.setShortName(child.getValue());

		// TODO Get animations first

		child = e.getChild("weaponArt");
		if (child == null)
			throw new IllegalArgumentException(weapon.getBlueprintName() + ": missing <weaponArt> tag.");
		weapon.setSheetPath("rdat:/img/weapons/" + child.getValue());

		return weapon;
	}

	public static DroneObject loadDrone(Element e) {
		if (e == null)
			throw new IllegalArgumentException("Element must not be null.");

		String attr = null;
		Element child = null;

		attr = e.getAttributeValue("name");
		if (attr == null)
			throw new IllegalArgumentException("Unidentified: missing 'name' attribute.");
		DroneObject drone = new DroneObject(attr);

		child = e.getChild("type");
		if (child == null)
			throw new IllegalArgumentException(drone.getBlueprintName() + ": missing <type> tag.");
		drone.setType(DroneTypes.valueOf(child.getValue()));

		child = e.getChild("title");
		if (child == null)
			throw new IllegalArgumentException(drone.getBlueprintName() + ": missing <title> tag.");
		drone.setTitle(child.getValue());

		child = e.getChild("short");
		if (child == null)
			drone.setShortName("N/A");
		else
			drone.setShortName(child.getValue());

		return drone;
	}
}
