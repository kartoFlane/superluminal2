package com.kartoflane.superluminal2.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.JDOMParseException;

import com.kartoflane.superluminal2.components.enums.Directions;
import com.kartoflane.superluminal2.components.enums.DroneStats;
import com.kartoflane.superluminal2.components.enums.DroneTypes;
import com.kartoflane.superluminal2.components.enums.WeaponStats;
import com.kartoflane.superluminal2.components.enums.WeaponTypes;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.core.DatabaseEntry;
import com.kartoflane.superluminal2.ftl.AnimationObject;
import com.kartoflane.superluminal2.ftl.AugmentObject;
import com.kartoflane.superluminal2.ftl.BlueprintList;
import com.kartoflane.superluminal2.ftl.DroneList;
import com.kartoflane.superluminal2.ftl.DroneObject;
import com.kartoflane.superluminal2.ftl.GameObject;
import com.kartoflane.superluminal2.ftl.GlowObject;
import com.kartoflane.superluminal2.ftl.ShipMetadata;
import com.kartoflane.superluminal2.ftl.WeaponList;
import com.kartoflane.superluminal2.ftl.WeaponObject;

/**
 * This class contains utility methods used to interpret XML tags as game objects.
 * 
 * @author kartoFlane
 * 
 */
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

		IOUtils.DecodeResult dr = IOUtils.decodeText(is, null);
		String contents = dr.text;
		Document doc = IOUtils.parseXML(contents);

		ArrayList<Element> shipList = new ArrayList<Element>();

		Element root = doc.getRootElement();

		if (root.getName().equals("shipBlueprint")) {
			String blueprint = root.getAttributeValue("name");

			if (blueprint != null && blueprint.equals(blueprintName))
				shipList.add(root);
		} else {
			for (Element e : root.getChildren("shipBlueprint")) {
				String blueprint = e.getAttributeValue("name");

				if (blueprint != null && blueprint.equals(blueprintName))
					shipList.add(e);
			}
		}

		return shipList;
	}

	public static ArrayList<Element> findShipsWithName(File f, String blueprintName)
			throws IllegalArgumentException, JDOMParseException, IOException {
		return findShipsWithName(blueprintName, new FileInputStream(f), f.getName());
	}

	public static ArrayList<Element> findTagsNamed(String contents, String tagName) throws JDOMParseException {
		Document doc = null;
		doc = IOUtils.parseXML(contents);
		ArrayList<Element> tagList = new ArrayList<Element>();

		Element root = doc.getRootElement();

		if (root.getName().equals(tagName)) {
			tagList.add(root);
		} else {
			for (Element e : root.getChildren(tagName))
				tagList.add(e);
		}

		return tagList;
	}

	public static ArrayList<Element> findTagsNamed(InputStream is, String fileName, String tagName)
			throws IllegalArgumentException, JDOMParseException, IOException {
		IOUtils.DecodeResult dr = IOUtils.decodeText(is, fileName);
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
			throw new IllegalArgumentException(e.getName() + " is missing 'name' attribute.");
		ShipMetadata metadata = new ShipMetadata(e, attr);

		attr = e.getAttributeValue("img");
		if (attr == null)
			throw new IllegalArgumentException(metadata.getBlueprintName() + " is missing 'img' attribute.");
		metadata.setShipImageNamespace(attr);

		child = e.getChild("class");
		if (child == null)
			throw new IllegalArgumentException(metadata.getBlueprintName() + " is missing <class> tag.");
		metadata.setShipClass(child.getValue());

		if (metadata.isPlayerShip()) {
			child = e.getChild("name");
			if (child == null)
				throw new IllegalArgumentException(metadata.getBlueprintName() + " is missing <name> tag.");
			metadata.setShipName(child.getValue());

			child = e.getChild("desc");
			if (child == null)
				metadata.setShipDescription("<desc> tag was missing!");
			else
				metadata.setShipDescription(child.getValue());
		}

		return metadata;
	}

	public static AnimationObject loadAnim(DatabaseEntry de, Element e) {
		if (e == null)
			throw new IllegalArgumentException("Element must not be null.");

		String attr = null;
		Element child = null;

		attr = e.getAttributeValue("name");
		if (attr == null)
			throw new IllegalArgumentException(e.getName() + " is missing 'name' attribute.");

		AnimationObject anim = new AnimationObject(attr);

		// Load mount offset
		child = e.getChild("mountPoint");
		if (child == null)
			throw new IllegalArgumentException(anim.getAnimName() + " is missing <mountPoint> tag");

		attr = child.getAttributeValue("x");
		if (attr == null)
			throw new IllegalArgumentException(anim.getAnimName() + " is missing 'x' attribute.");
		int x = Integer.parseInt(attr);

		attr = child.getAttributeValue("y");
		if (attr == null)
			throw new IllegalArgumentException(anim.getAnimName() + " is missing 'y' attribute.");
		int y = Integer.parseInt(attr);

		anim.setMountOffset(x, y);

		// Load the anim sheet
		child = e.getChild("sheet");
		if (child == null)
			throw new IllegalArgumentException(anim.getAnimName() + " is missing <sheet> tag");

		Element sheet = de.getAnimSheetElement(child.getValue());
		if (sheet == null)
			throw new IllegalArgumentException(anim.getAnimName() + "'s animSheet could not be found: " + child.getValue());

		// Load sheet dimensions
		attr = sheet.getAttributeValue("w");
		if (attr == null)
			throw new IllegalArgumentException(anim.getAnimName() + " is missing 'w' attribute.");
		x = Integer.parseInt(attr);

		attr = sheet.getAttributeValue("h");
		if (attr == null)
			throw new IllegalArgumentException(anim.getAnimName() + " is missing 'h' attribute.");
		y = Integer.parseInt(attr);

		anim.setSheetSize(x, y);

		// Load frame dimensions
		attr = sheet.getAttributeValue("fw");
		if (attr == null)
			throw new IllegalArgumentException(anim.getAnimName() + " is missing 'fw' attribute.");
		x = Integer.parseInt(attr);

		attr = sheet.getAttributeValue("fh");
		if (attr == null)
			throw new IllegalArgumentException(anim.getAnimName() + " is missing 'fh' attribute.");
		y = Integer.parseInt(attr);

		anim.setFrameSize(x, y);

		// Load the anim sheet image path
		anim.setSheetPath("db:img/" + sheet.getValue());

		return anim;
	}

	/**
	 * 
	 * @param e
	 *            element to be parsed
	 * @return blueprint list the element represents, or null if the list was not
	 *         holding weapons or drones
	 */
	public static BlueprintList<? extends GameObject> loadList(Element e) {
		if (e == null)
			throw new IllegalArgumentException("Element must not be null.");

		String attr = e.getAttributeValue("name");
		if (attr == null)
			throw new IllegalArgumentException(e.getName() + " is missing 'name' attribute.");

		List<Element> children = e.getChildren("name");
		if (children.size() == 0) {
			throw new IllegalArgumentException(attr + ": list is empty.");
		} else {
			// Need to figure out the type of the list...
			// Checking the type of the first child seems like the most reasonable option
			String name = children.get(0).getValue();

			if (Database.getInstance().getWeapon(name) != null) {
				return loadWeaponList(attr, children);
			} else if (Database.getInstance().getDrone(name) != null) {
				return loadDroneList(attr, children);
			} else {
				// Not interested in any other blueprintLists
			}
		}

		return null;
	}

	private static WeaponList loadWeaponList(String name, List<Element> children) {
		if (name == null)
			throw new IllegalArgumentException("Name must not be null.");
		if (children == null)
			throw new IllegalArgumentException("Children list must not be null.");

		Database db = Database.getInstance();
		WeaponList list = new WeaponList(name);
		for (Element child : children) {
			WeaponObject weapon = db.getWeapon(child.getValue());
			if (weapon != null)
				list.add(weapon);
		}

		return list;
	}

	private static DroneList loadDroneList(String name, List<Element> children) {
		if (name == null)
			throw new IllegalArgumentException("Name must not be null.");
		if (children == null)
			throw new IllegalArgumentException("Children list must not be null.");

		Database db = Database.getInstance();
		DroneList list = new DroneList(name);
		for (Element child : children) {
			DroneObject drone = db.getDrone(child.getValue());
			if (drone != null)
				list.add(drone);
		}

		return list;
	}

	public static WeaponObject loadWeapon(Element e) {
		if (e == null)
			throw new IllegalArgumentException("Element must not be null.");

		String attr = null;
		Element child = null;

		attr = e.getAttributeValue("name");
		if (attr == null)
			throw new IllegalArgumentException(e.getName() + " is missing 'name' attribute.");
		WeaponObject weapon = new WeaponObject(attr);

		child = e.getChild("type");
		if (child == null)
			throw new IllegalArgumentException(weapon.getBlueprintName() + " is missing <type> tag.");
		weapon.setType(WeaponTypes.valueOf(child.getValue()));

		child = e.getChild("title");
		if (child == null)
			throw new IllegalArgumentException(weapon.getBlueprintName() + " is missing <title> tag.");
		weapon.setTitle(child.getValue());

		child = e.getChild("short");
		if (child == null)
			weapon.setShortName("Missing short name");
		else
			weapon.setShortName(child.getValue());

		child = e.getChild("desc");
		if (child == null)
			weapon.setDescription("Missing description.");
		else
			weapon.setDescription(child.getValue());

		child = e.getChild("weaponArt");
		if (child == null)
			throw new IllegalArgumentException(weapon.getBlueprintName() + " is missing <weaponArt> tag.");
		try {
			weapon.setAnimName(child.getValue());
		} catch (IllegalArgumentException ex) {
			// Catch an re-throw the error to provide more information
			throw new IllegalArgumentException(weapon.getBlueprintName() + ": could not find animation '" + child.getValue() + "'.", ex);
		}

		for (WeaponStats stat : WeaponStats.values()) {
			try {
				child = e.getChild(stat.getTagName());
				if (child != null)
					weapon.setStat(stat, Float.parseFloat(child.getValue()));
			} catch (NumberFormatException ex) {
				// Catch an re-throw the error to provide more information
				throw new IllegalArgumentException(weapon.getBlueprintName() + ": <" + stat.getTagName() + "> tag's value could not be parsed: " + child.getValue());
			}
		}

		return weapon;
	}

	public static DroneObject loadDrone(Element e) {
		if (e == null)
			throw new IllegalArgumentException("Element must not be null.");

		String attr = null;
		Element child = null;

		attr = e.getAttributeValue("name");
		if (attr == null)
			throw new IllegalArgumentException(e.getName() + " is missing 'name' attribute.");
		DroneObject drone = new DroneObject(attr);

		child = e.getChild("type");
		if (child == null)
			throw new IllegalArgumentException(drone.getBlueprintName() + " is missing <type> tag.");
		drone.setType(DroneTypes.valueOf(child.getValue()));

		child = e.getChild("title");
		if (child == null)
			throw new IllegalArgumentException(drone.getBlueprintName() + " is missing <title> tag.");
		drone.setTitle(child.getValue());

		child = e.getChild("short");
		if (child == null)
			drone.setShortName("Missing short name");
		else
			drone.setShortName(child.getValue());

		child = e.getChild("desc");
		if (child == null)
			drone.setDescription("Missing description.");
		else
			drone.setDescription(child.getValue());

		for (DroneStats stat : DroneStats.values()) {
			try {
				child = e.getChild(stat.getTagName());
				if (child != null)
					drone.setStat(stat, Float.parseFloat(child.getValue()));
			} catch (NumberFormatException ex) {
				// Catch an re-throw the error to provide more information
				throw new IllegalArgumentException(drone.getBlueprintName() + ": <" + stat.getTagName() + "> tag's value could not be parsed: " + child.getValue());
			}
		}

		return drone;
	}

	public static AugmentObject loadAugment(Element e) {
		if (e == null)
			throw new IllegalArgumentException("Element must not be null.");

		String attr = null;
		Element child = null;

		attr = e.getAttributeValue("name");
		if (attr == null)
			throw new IllegalArgumentException(e.getName() + " is missing 'name' attribute.");
		AugmentObject augment = new AugmentObject(attr);

		child = e.getChild("title");
		if (child == null)
			throw new IllegalArgumentException(augment.getBlueprintName() + " is missing <title> tag.");
		augment.setTitle(child.getValue());

		child = e.getChild("desc");
		if (child == null)
			throw new IllegalArgumentException(augment.getBlueprintName() + " is missing <desc> tag.");
		augment.setDescription(child.getValue());

		return augment;
	}

	public static GlowObject loadGlow(Element e) {
		if (e == null)
			throw new IllegalArgumentException("Element must not be null.");

		String attr = null;
		Element child = null;

		// Identifier of the glow set, has to match an existing interior image in order for the game to link them
		attr = e.getAttributeValue("name");
		if (attr == null)
			throw new IllegalArgumentException(e.getName() + " is missing 'name' attribute.");
		GlowObject glow = new GlowObject(attr);

		child = e.getChild("computerGlow");
		if (child == null)
			throw new IllegalArgumentException(glow.getIdentifier() + " is missing a <computerGlow> tag.");

		// Optional attribute that allows to use a different image for the glow
		attr = child.getAttributeValue("name");
		if (attr != null)
			glow.setGlowSet(Database.getInstance().getGlowSet(attr));

		attr = child.getAttributeValue("x");
		if (attr == null)
			throw new IllegalArgumentException(glow.getIdentifier() + "'s <computerGlow> is missing 'x' attribute.");
		glow.setX(Integer.parseInt(attr));

		attr = child.getAttributeValue("y");
		if (attr == null)
			throw new IllegalArgumentException(glow.getIdentifier() + "'s <computerGlow> is missing 'y' attribute.");
		glow.setY(Integer.parseInt(attr));

		attr = child.getAttributeValue("dir");
		if (attr == null)
			throw new IllegalArgumentException(glow.getIdentifier() + "'s <computerGlow> is missing 'dir' attribute.");
		glow.setDirection(Directions.parseDir(attr));

		return glow;
	}
}
