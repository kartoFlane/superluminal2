package com.kartoflane.superluminal2.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import net.vhati.ftldat.FTLDat.FTLPack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.JDOMParseException;

import com.kartoflane.superluminal2.components.Directions;
import com.kartoflane.superluminal2.components.Images;
import com.kartoflane.superluminal2.components.ShipMetadata;
import com.kartoflane.superluminal2.core.Utils.DecodeResult;
import com.kartoflane.superluminal2.ftl.DoorObject;
import com.kartoflane.superluminal2.ftl.GibObject;
import com.kartoflane.superluminal2.ftl.MountObject;
import com.kartoflane.superluminal2.ftl.RoomObject;
import com.kartoflane.superluminal2.ftl.ShipObject;
import com.kartoflane.superluminal2.ftl.StationObject;
import com.kartoflane.superluminal2.ftl.SystemObject;
import com.kartoflane.superluminal2.ftl.SystemObject.Systems;

public class ShipUtils {
	public static final Logger log = LogManager.getLogger(ShipUtils.class);

	private enum LayoutObjects {
		X_OFFSET,
		Y_OFFSET,
		HORIZONTAL,
		VERTICAL,
		ELLIPSE,
		ROOM,
		DOOR
	}

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

	public static ArrayList<Element> findShips(InputStream is, String fileName)
			throws IllegalArgumentException, JDOMParseException, IOException {
		Document doc = null;
		DecodeResult dr = Utils.decodeText(is, null);
		String contents = dr.text;
		try {
			doc = Utils.parseXML(contents);
		} catch (JDOMParseException e) {
			System.out.println(contents);
			throw e;
		}
		ArrayList<Element> shipList = new ArrayList<Element>();

		Element root = doc.getRootElement();

		for (Element e : root.getChildren("shipBlueprint"))
			shipList.add(e);

		return shipList;
	}

	public static ArrayList<Element> findShips(File f)
			throws IllegalArgumentException, JDOMParseException, IOException {
		return findShips(new FileInputStream(f), f.getName());
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

	/**
	 * ------------------------------------------------------ TODO documentation
	 * 
	 * @param e
	 * @return
	 * @throws IllegalArgumentException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws JDOMParseException
	 */
	public static ShipObject loadShipXML(Element e)
			throws IllegalArgumentException, FileNotFoundException, IOException, NumberFormatException, JDOMParseException {
		if (e == null)
			throw new IllegalArgumentException("Element must not be null.");

		FTLPack data = Database.getInstance().getDataDat();

		String attr = null;
		Element child = null;

		// Get the blueprint name of the ship first, to determine whether it is a player ship
		attr = e.getAttributeValue("name");
		if (attr == null)
			throw new IllegalArgumentException("Missing 'name' attribute.");
		String blueprintName = attr;

		// Create the ship object
		boolean isPlayer = Database.getInstance().isPlayerShip(blueprintName);
		ShipObject ship = new ShipObject(isPlayer);
		ship.setBlueprintName(blueprintName);

		// Load the TXT layout of the ship (rooms, doors, offsets, shield)
		attr = e.getAttributeValue("layout");
		if (attr == null)
			throw new IllegalArgumentException("Missing 'layout' attribute.");
		ship.setLayoutTXT("data/" + attr + ".txt");

		if (!data.contains(ship.getLayoutTXT()))
			throw new FileNotFoundException("TXT layout file could not be found in game's archives: " + ship.getLayoutTXT());

		InputStream is = data.getInputStream(ship.getLayoutTXT());
		loadLayoutTXT(ship, is, ship.getLayoutTXT());

		// Load ship's images
		attr = e.getAttributeValue("img");
		if (attr == null)
			throw new IllegalArgumentException("Missing 'img' attribute.");
		ship.setLayoutXML("data/" + attr + ".xml");

		String namespace = attr;
		String prefix = isPlayer ? "rdat:img/ship/" : "rdat:img/ships_glow/";

		// Load the hull image
		ship.setImage(Images.HULL, prefix + namespace + "_base.png");

		// Load the cloak image, check for override
		child = e.getChild("cloakImage");
		if (child != null)
			namespace = child.getValue();
		// For whatever reason, cloak images for enemy ships are located in img/ship... durr FTL devs
		ship.setImage(Images.CLOAK, "rdat:img/ship/" + namespace + "_cloak.png");
		namespace = attr;

		// Floor and shield images are exclusive to player ships.
		// Enemies use a common shield image that has its dimensions
		// defined by the ELLIPSE layout object.
		if (isPlayer) {
			// Load the floor image, check for override
			child = e.getChild("floorImage");
			if (child != null)
				namespace = child.getValue();
			ship.setImage(Images.FLOOR, prefix + namespace + "_floor.png");
			namespace = attr;

			// Load the shield image, check for override
			child = e.getChild("shieldImage");
			if (child != null)
				namespace = child.getValue();
			ship.setImage(Images.SHIELD, prefix + namespace + "_shields1.png");
			namespace = attr;

			// Load the thumbnail/miniship image path (not represented in the editor)
			ship.setImage(Images.THUMBNAIL, "rdat:img/customizeUI/miniship_" + namespace + ".png");
		}

		// Load the XML layout of the ship (images' offsets, gibs, weapon mounts)
		if (!data.contains(ship.getLayoutXML()))
			throw new FileNotFoundException("XML layout file could not be found in game's archives: " + ship.getLayoutXML());

		is = data.getInputStream(ship.getLayoutXML());
		loadLayoutXML(ship, is, ship.getLayoutXML());

		// Get the class of the ship
		child = e.getChild("class");
		if (child == null)
			throw new IllegalArgumentException("Missing <class> tag.");
		ship.setShipClass(child.getValue());

		// Get the name of the ship
		// Exclusive to player ships
		if (isPlayer) {
			child = e.getChild("name");
			if (child == null)
				throw new IllegalArgumentException("Missing <name> tag.");
			ship.setShipName(child.getValue());
		}

		// Get the description of the ship
		// Exclusive to player ships
		if (isPlayer) {
			child = e.getChild("desc");
			if (child == null)
				ship.setShipDescription("<desc> tag was missing!");
			else
				ship.setShipDescription(child.getValue());
		}

		// Get the list of systems installed on the ship
		child = e.getChild("systemList");
		if (child == null)
			throw new IllegalArgumentException("Missing <systemList> tag.");

		for (Systems sys : Systems.getSystems()) {
			Element sysEl = child.getChild(sys.name().toLowerCase());

			if (sysEl != null) {
				SystemObject system = ship.getSystem(sys);

				// Get the min level the system can have, or the starting level of the system
				attr = sysEl.getAttributeValue("power");
				if (attr == null)
					throw new IllegalArgumentException(sys.toString() + " is missing 'power' attribute.");
				system.setLevelStart(Integer.valueOf(attr));

				// Get the max level the system can have
				// Exclusive to enemy ships
				if (!isPlayer) {
					attr = sysEl.getAttributeValue("max");
					if (attr != null) {
						system.setLevelMax(Integer.valueOf(attr));
					} else {
						// Some ships (mostly BOSS) are missing the 'max' attribute
						// Guess-default to the system's level cap
						system.setLevelMax(system.getLevelCap());
					}
				}

				// Get the room to which the system is assigned
				attr = sysEl.getAttributeValue("room");
				if (attr == null)
					throw new IllegalArgumentException(sys.toString() + " is missing 'room' attribute.");
				int id = Integer.valueOf(attr);
				system.setRoom(ship.getRoomById(id));

				// Whether the ship starts with the system installed or not
				// Optional
				attr = sysEl.getAttributeValue("start");
				if (attr != null) {
					system.setAvailable(Boolean.valueOf(attr));
				} else {
					// TODO default to true?
				}

				// Get the interior image used for this system
				// System objects are assigned default interior images on their creation
				// Optional
				attr = sysEl.getAttributeValue("img");
				if (attr != null) {
					prefix = "rdat:img/ship/interior/";
					system.setInteriorPath(prefix + attr + ".png");
					// TODO get glows by pruning room_ from attr?
					// what happens with custom-named interior images?
				} else if (!isPlayer) {
					// Enemy ships' systems don't use interior images
					system.setInteriorPath(null);
				}

				// Get the weapon used by this system
				// Exclusive to artillery
				if (sys == Systems.ARTILLERY) {
					attr = sysEl.getAttributeValue("weapon");
					if (attr == null)
						throw new IllegalArgumentException("Artillery is missing 'weapon' attribute.");
					String artilleryWeapon = attr;
					// TODO
				}

				// Load station position and direction for this system
				if (system.canContainStation()) {
					Element stEl = sysEl.getChild("slot");

					// Station objects are instantiated with default values for their system
					// Optional
					if (stEl != null) {
						StationObject station = system.getStation();

						Element slotEl = stEl.getChild("number");
						if (slotEl != null)
							station.setSlotId(Integer.valueOf(slotEl.getValue()));

						Element dirEl = stEl.getChild("direction");
						if (dirEl != null)
							station.setSlotDirection(Directions.valueOf(dirEl.getValue().toUpperCase()));
					} else if (!isPlayer) {
						// Enemy ships' stations are placed and rotated mostly randomly
						// No reason to try to emulate this, so just hide the station
						StationObject station = system.getStation();
						station.setSlotId(-2);
					}
				}
			}
		}

		// TODO weapons / drones / reactor / hull

		return ship;
	}

	public static boolean saveShipXML(File f, ShipObject ship) {
		// TODO
		return false;
	}

	public static ShipObject loadShipSHPO(File f) {
		// TODO
		return null;
	}

	public static boolean saveShipSHPO(File f, ShipObject ship) {
		// TODO
		return false;
	}

	/**
	 * Loads the layout from the given stream, and adds it to the given ship object.
	 * 
	 * @param ship
	 *            the ship object to which any added layout object will be added
	 * @param is
	 *            input stream of the file that is to be read. The stream is always closed by this method.
	 * @param fileName
	 *            name of the file being read (for logging purposes)
	 * @throws IllegalArgumentException
	 *             when an error occurs during reading of the file, for example when the file is malformed.
	 */
	public static void loadLayoutTXT(ShipObject ship, InputStream is, String fileName) throws IllegalArgumentException {
		Scanner sc = new Scanner(is);

		HashMap<DoorObject, Integer> leftMap = new HashMap<DoorObject, Integer>();
		HashMap<DoorObject, Integer> rightMap = new HashMap<DoorObject, Integer>();

		try {
			while (sc.hasNext()) {
				String line = sc.nextLine();

				if (line == null || line.trim().equals(""))
					continue;

				LayoutObjects layoutObject = null;
				try {
					layoutObject = LayoutObjects.valueOf(line);
				} catch (IllegalArgumentException e) {
					try {
						Integer.parseInt(line);
					} catch (NumberFormatException ex) {
						// not a number
						throw new IllegalArgumentException(fileName + " contained an unknown layout object: " + line);
					}
				}
				switch (layoutObject) {
					case X_OFFSET:
						ship.setXOffset(sc.nextInt());
						break;
					case Y_OFFSET:
						ship.setYOffset(sc.nextInt());
						break;
					case HORIZONTAL:
						ship.setHorizontal(sc.nextInt());
						break;
					case VERTICAL:
						ship.setVertical(sc.nextInt());
						break;
					case ELLIPSE:
						Rectangle ellipse = new Rectangle(0, 0, 0, 0);
						ellipse.width = sc.nextInt();
						ellipse.height = sc.nextInt();
						ellipse.x = sc.nextInt();
						ellipse.y = sc.nextInt();
						ship.setEllipse(ellipse);
						break;
					case ROOM:
						RoomObject room = new RoomObject();
						room.setId(sc.nextInt());
						room.setLocation(sc.nextInt(), sc.nextInt());
						room.setSize(sc.nextInt(), sc.nextInt());
						ship.add(room);
						break;
					case DOOR:
						DoorObject door = new DoorObject();
						door.setLocation(sc.nextInt(), sc.nextInt());
						leftMap.put(door, sc.nextInt());
						rightMap.put(door, sc.nextInt());
						door.setHorizontal(sc.nextInt() == 0);
						ship.add(door);
						break;
					default:
						throw new Exception("Unrecognised layout object: " + layoutObject);
				}
			}

			for (DoorObject door : ship.getDoors()) {
				door.setLeftRoom(ship.getRoomById(leftMap.get(door)));
				door.setRightRoom(ship.getRoomById(rightMap.get(door)));
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(fileName + " is wrongly formatted: " + e.getMessage());
		} finally {
			sc.close();
		}
	}

	public static void loadLayoutTXT(ShipObject ship, File f) throws IllegalArgumentException, FileNotFoundException {
		loadLayoutTXT(ship, new FileInputStream(f), f.getName());
	}

	public static void loadLayoutXML(ShipObject ship, InputStream is, String fileName)
			throws IllegalArgumentException, JDOMParseException, IOException {
		Document doc = Utils.readStreamXML(is, fileName);

		Element root = doc.getRootElement();
		Element child = null;
		String attr = null;

		// Load the total offset of the image set
		child = root.getChild("img");
		if (child == null)
			throw new IllegalArgumentException("Missing <img> tag");

		Rectangle hullDimensions = new Rectangle(0, 0, 0, 0);
		attr = child.getAttributeValue("x");
		if (attr == null)
			throw new IllegalArgumentException("Img missing 'x' attribute");
		hullDimensions.x = Integer.valueOf(attr);

		attr = child.getAttributeValue("y");
		if (attr == null)
			throw new IllegalArgumentException("Img missing 'y' attribute");
		hullDimensions.y = Integer.valueOf(attr);

		attr = child.getAttributeValue("w");
		if (attr == null)
			throw new IllegalArgumentException("Img missing 'w' attribute");
		hullDimensions.width = Integer.valueOf(attr);

		attr = child.getAttributeValue("h");
		if (attr == null)
			throw new IllegalArgumentException("Img missing 'h' attribute");
		hullDimensions.height = Integer.valueOf(attr);

		ship.setHullDimensions(hullDimensions);

		// Ignore <glowOffset> - only concerns iPad version of FTL

		// Load additional offsets for other images
		Point offset = new Point(0, 0);
		Element offsets = root.getChild("offsets");
		if (offsets != null) {
			child = offsets.getChild("cloak");
			if (child != null) {
				attr = child.getAttributeValue("x");
				if (attr == null)
					throw new IllegalArgumentException("Cloak missing 'x' attribute");
				offset.x = Integer.valueOf(attr);

				attr = child.getAttributeValue("y");
				if (attr == null)
					throw new IllegalArgumentException("Cloak missing 'y' attribute");
				offset.y = Integer.valueOf(attr);

				ship.setCloakOffset(offset);
			}

			child = offsets.getChild("floor");
			if (child != null) {
				attr = child.getAttributeValue("x");
				if (attr == null)
					throw new IllegalArgumentException("Floor missing 'x' attribute");
				offset.x = Integer.valueOf(attr);

				attr = child.getAttributeValue("y");
				if (attr == null)
					throw new IllegalArgumentException("Floor missing 'y' attribute");
				offset.y = Integer.valueOf(attr);

				ship.setFloorOffset(offset);
			}
		}

		HashMap<MountObject, Integer> gibMap = new HashMap<MountObject, Integer>();

		// Load weapon mounts
		child = root.getChild("weaponMounts");
		if (child == null)
			throw new IllegalArgumentException("Missing <weaponMounts> tag");

		for (Element mountEl : child.getChildren("mount")) {
			MountObject mount = new MountObject();

			attr = mountEl.getAttributeValue("x");
			if (attr == null)
				throw new IllegalArgumentException("Mount missing 'x' attribute");
			offset.x = Integer.valueOf(attr);

			attr = mountEl.getAttributeValue("y");
			if (attr == null)
				throw new IllegalArgumentException("Mount missing 'y' attribute");
			offset.y = Integer.valueOf(attr);

			mount.setLocation(offset.x, offset.y); // TODO when loading remember that this is offset from the ship's hull

			attr = mountEl.getAttributeValue("rotate");
			if (attr == null)
				throw new IllegalArgumentException("Mount missing 'rotate' attribute");
			mount.setRotated(Boolean.valueOf(attr));

			attr = mountEl.getAttributeValue("mirror");
			if (attr == null)
				throw new IllegalArgumentException("Moumt missing 'mirror' attribute");
			mount.setMirrored(Boolean.valueOf(attr));

			attr = mountEl.getAttributeValue("gib");
			if (attr == null)
				throw new IllegalArgumentException("Moumt missing 'gib' attribute");
			gibMap.put(mount, Integer.valueOf(attr));

			attr = mountEl.getAttributeValue("slide");
			if (attr != null)
				mount.setDirection(Directions.parseDir(attr.toUpperCase()));
			else
				mount.setDirection(Directions.NONE);

			ship.add(mount);
		}

		// Load gibs
		child = root.getChild("explosion");
		if (child == null)
			throw new IllegalArgumentException("Missing <explosion> tag");

		for (Element gibEl : child.getChildren()) {
			if (gibEl.getName().startsWith("gib")) {
				GibObject gib = new GibObject();

				attr = gibEl.getName().substring(3);
				gib.setId(Integer.valueOf(attr));

				child = gibEl.getChild("x");
				if (child == null)
					throw new IllegalArgumentException("Gib missing <x> tag");
				offset.x = Integer.valueOf(child.getValue());

				child = gibEl.getChild("y");
				if (child == null)
					throw new IllegalArgumentException("Gib missing <y> tag");
				offset.y = Integer.valueOf(child.getValue());

				gib.setOffset(offset.x, offset.y);

				child = gibEl.getChild("velocity");
				if (child == null)
					throw new IllegalArgumentException("Gib missing <velocity> tag");

				attr = child.getAttributeValue("min");
				if (attr == null)
					throw new IllegalArgumentException("Velocity missing 'min' attribute");
				gib.setVelocityMin(Float.valueOf(attr));

				attr = child.getAttributeValue("max");
				if (attr == null)
					throw new IllegalArgumentException("Velocity missing 'max' attribute");
				gib.setVelocityMax(Float.valueOf(attr));

				child = gibEl.getChild("direction");
				if (child == null)
					throw new IllegalArgumentException("Missing <direction> tag");

				attr = child.getAttributeValue("min");
				if (attr == null)
					throw new IllegalArgumentException("Direction missing 'min' attribute");
				gib.setDirectionMin(Integer.valueOf(attr));

				attr = child.getAttributeValue("max");
				if (attr == null)
					throw new IllegalArgumentException("Direction missing 'max' attribute");
				gib.setDirectionMax(Integer.valueOf(attr));

				child = gibEl.getChild("velocity");
				if (child == null)
					throw new IllegalArgumentException("Missing <angular> tag");

				attr = child.getAttributeValue("min");
				if (attr == null)
					throw new IllegalArgumentException("Angular missing 'min' attribute");
				gib.setAngularMin(Float.valueOf(attr));

				attr = child.getAttributeValue("max");
				if (attr == null)
					throw new IllegalArgumentException("Angular missing 'max' attribute");
				gib.setAngularMax(Float.valueOf(attr));

				ship.add(gib);
			}
		}
	}

	public static void loadLayoutXML(ShipObject ship, File f) throws IllegalArgumentException, JDOMParseException, IOException {
		loadLayoutXML(ship, new FileInputStream(f), f.getName());
	}
}
