package com.kartoflane.superluminal2.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.JDOMParseException;

import com.kartoflane.superluminal2.components.enums.Directions;
import com.kartoflane.superluminal2.components.enums.Images;
import com.kartoflane.superluminal2.components.enums.LayoutObjects;
import com.kartoflane.superluminal2.components.enums.Races;
import com.kartoflane.superluminal2.components.enums.Systems;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.ftl.AugmentObject;
import com.kartoflane.superluminal2.ftl.DoorObject;
import com.kartoflane.superluminal2.ftl.DroneList;
import com.kartoflane.superluminal2.ftl.DroneObject;
import com.kartoflane.superluminal2.ftl.GibObject;
import com.kartoflane.superluminal2.ftl.GlowObject;
import com.kartoflane.superluminal2.ftl.MountObject;
import com.kartoflane.superluminal2.ftl.RoomObject;
import com.kartoflane.superluminal2.ftl.ShipObject;
import com.kartoflane.superluminal2.ftl.StationObject;
import com.kartoflane.superluminal2.ftl.SystemObject;
import com.kartoflane.superluminal2.ftl.WeaponList;
import com.kartoflane.superluminal2.ftl.WeaponObject;

public class ShipLoadUtils {

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

		Database db = Database.getInstance();

		String attr = null;
		Element child = null;

		// Get the blueprint name of the ship first, to determine whether it is a player ship
		attr = e.getAttributeValue("name");
		if (attr == null)
			throw new IllegalArgumentException("Missing 'name' attribute.");
		String blueprintName = attr;

		// Create the ship object
		boolean isPlayer = db.isPlayerShip(blueprintName);
		ShipObject ship = new ShipObject(isPlayer);
		ship.setBlueprintName(blueprintName);

		// Load the TXT layout of the ship (rooms, doors, offsets, shield)
		attr = e.getAttributeValue("layout");
		if (attr == null)
			throw new IllegalArgumentException("Missing 'layout' attribute.");
		ship.setLayout(attr);

		if (!db.contains(ship.getLayoutTXT()))
			throw new FileNotFoundException("TXT layout file could not be found in game's archives: " + ship.getLayoutTXT());

		InputStream is = db.getInputStream(ship.getLayoutTXT());
		loadLayoutTXT(ship, is, ship.getLayoutTXT());

		// Load ship's images
		attr = e.getAttributeValue("img");
		if (attr == null)
			throw new IllegalArgumentException("Missing 'img' attribute.");
		ship.setImageNamespace(attr);

		String namespace = attr;
		// Ship images can be located in either ship/, ships_glow/ or ships_noglow/
		String[] prefixes = { "db:img/ship/", "db:img/ships_glow/", "db:img/ships_noglow/" };

		// Load the hull image
		ship.setImage(Images.HULL, firstExisting(prefixes, namespace + "_base.png", db));

		// Load the cloak image, check for override
		child = e.getChild("cloakImage");
		if (child != null)
			namespace = child.getValue();
		ship.setImage(Images.CLOAK, firstExisting(prefixes, namespace + "_cloak.png", db));
		namespace = attr;

		// Floor and shield images are exclusive to player ships.
		// Enemies use a common shield image that has its dimensions
		// defined by the ELLIPSE layout object.
		if (isPlayer) {
			// Load the floor image, check for override
			child = e.getChild("floorImage");
			if (child != null)
				namespace = child.getValue();
			ship.setImage(Images.FLOOR, firstExisting(prefixes, namespace + "_floor.png", db));
			namespace = attr;

			// Load the shield image, check for override
			child = e.getChild("shieldImage");
			if (child != null)
				namespace = child.getValue();
			ship.setImage(Images.SHIELD, firstExisting(prefixes, namespace + "_shields1.png", db));
			namespace = attr;

			// Load the thumbnail/miniship image path (not represented in the editor)
			ship.setImage(Images.THUMBNAIL, firstExisting(new String[] { "db:img/customizeUI/miniship_" }, namespace + ".png", db));
		}

		// Load the XML layout of the ship (images' offsets, gibs, weapon mounts)
		if (!db.contains(ship.getLayoutXML()))
			throw new FileNotFoundException("XML layout file could not be found in game's archives: " + ship.getLayoutXML());

		is = db.getInputStream(ship.getLayoutXML());
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
					// Default to true
					system.setAvailable(true);
				}

				// Get the interior image used for this system
				// System objects are assigned default interior images on their creation
				// Optional
				attr = sysEl.getAttributeValue("img");
				if (attr != null) {
					system.setInteriorNamespace(attr);
					system.setInteriorPath("db:img/ship/interior/" + system.getInteriorNamespace() + ".png");
				} else if (isPlayer) {
					// No 'img' attribute, use default
					system.setInteriorNamespace(system.getSystemId().getDefaultInteriorNamespace());
					system.setInteriorPath(null);
				} else {
					// Enemy ships' systems don't use interior images or glows
					system.setInteriorNamespace(null);
					system.setInteriorPath(null);
				}

				// When an interior image doesn't have a corresponding glow,
				// No glow is used -- apparently there is no default to fall back to.
				// The editor will try to create a glow, should no match be found
				if (system.canContainGlow()) {
					if (attr == null)
						attr = sys.getDefaultInteriorNamespace();
					attr = attr.replace("room_", "");
					GlowObject glowObject = db.getGlow(attr);
					if (glowObject != null) {
						system.setGlowSet(glowObject.getGlowSet());
					}
				}

				// Get the weapon used by this system
				// Exclusive to artillery
				if (sys == Systems.ARTILLERY) {
					attr = sysEl.getAttributeValue("weapon");
					if (attr == null)
						throw new IllegalArgumentException("Artillery is missing 'weapon' attribute.");
					String artilleryWeapon = attr;
					// TODO ??????
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

		child = e.getChild("weaponSlots");
		if (child == null) {
			ship.setWeaponSlots(4); // Default
		} else {
			ship.setWeaponSlots(Integer.valueOf(child.getValue()));
		}

		child = e.getChild("droneSlots");
		if (child == null) {
			ship.setDroneSlots(2); // Default
		} else {
			ship.setDroneSlots(Integer.valueOf(child.getValue()));
		}

		child = e.getChild("weaponList");
		if (child == null) {
			ship.setMissilesAmount(0);
		} else {
			attr = child.getAttributeValue("missiles");
			if (attr == null)
				ship.setMissilesAmount(0);
			else
				ship.setMissilesAmount(Integer.valueOf(attr));

			attr = child.getAttributeValue("count");
			int count = -1; // Default - load all
			if (attr != null)
				count = Integer.valueOf(attr);

			if (isPlayer) {
				int loaded = 0;
				for (Element weapon : child.getChildren("weapon")) {
					if (count != -1 && loaded >= count)
						break;

					attr = weapon.getAttributeValue("name");
					if (attr == null)
						throw new IllegalArgumentException("A weapon in <weaponList> is missing 'name' attribute.");

					WeaponObject weaponObject = db.getWeapon(attr);
					if (weaponObject == null)
						throw new IllegalArgumentException("WeaponBlueprint not found: " + attr);

					ship.changeWeapon(Database.DEFAULT_WEAPON_OBJ, weaponObject);

					loaded++;
				}
			} else {
				attr = child.getAttributeValue("load");
				if (attr != null) {
					WeaponList list = db.getWeaponList(attr);
					if (list == null)
						throw new IllegalArgumentException("BlueprintList could not be found: " + attr);
					ship.setWeaponList(list);
				}
			}
		}

		child = e.getChild("droneList");
		if (child == null) {
			ship.setDronePartsAmount(0);
		} else {
			attr = child.getAttributeValue("drones");
			if (attr == null)
				ship.setDronePartsAmount(0);
			else
				ship.setDronePartsAmount(Integer.valueOf(attr));

			attr = child.getAttributeValue("count");
			int count = -1;
			if (attr != null)
				count = Integer.valueOf(attr);

			if (ship.isPlayerShip()) {
				int loaded = 0;
				for (Element drone : child.getChildren("drone")) {
					if (count != -1 && loaded >= count)
						break;

					attr = drone.getAttributeValue("name");
					if (attr == null)
						throw new IllegalArgumentException("A drone in <droneList> is missing 'name' attribute.");

					DroneObject droneObject = db.getDrone(attr);
					if (droneObject == null)
						throw new IllegalArgumentException("DroneBlueprint not found: " + attr);

					ship.changeDrone(Database.DEFAULT_DRONE_OBJ, droneObject);

					loaded++;
				}
			} else {
				attr = child.getAttributeValue("load");
				if (attr != null) {
					DroneList list = db.getDroneList(attr);
					if (list == null)
						throw new IllegalArgumentException("BlueprintList could not be found: " + attr);
					ship.setDroneList(list);
				}
			}
		}

		child = e.getChild("health");
		if (child == null)
			throw new IllegalArgumentException("Missing <health> tag");
		attr = child.getAttributeValue("amount");
		if (attr == null)
			throw new IllegalArgumentException("<health> tag is missing 'amount' attribute.");
		ship.setHealth(Integer.valueOf(attr));

		child = e.getChild("maxPower");
		if (child == null)
			throw new IllegalArgumentException("Missing <maxPower> tag");
		attr = child.getAttributeValue("amount");
		if (attr == null)
			throw new IllegalArgumentException("<maxPower> tag is missing 'amount' attribute.");
		ship.setPower(Integer.valueOf(attr));

		if (!ship.isPlayerShip()) {
			child = e.getChild("minSector");
			if (child == null)
				ship.setMinSector(1);
			else {
				attr = child.getValue();
				ship.setMinSector(Integer.valueOf(attr));
			}

			child = e.getChild("maxSector");
			if (child == null)
				ship.setMaxSector(8);
			else {
				attr = child.getValue();
				ship.setMaxSector(Integer.valueOf(attr));
			}
		}

		for (Element crew : e.getChildren("crewCount")) {
			attr = crew.getAttributeValue("class");
			if (attr == null)
				throw new IllegalArgumentException("<crewCount> tag is missing 'class' attribute.");
			Races race = null;
			try {
				race = Races.valueOf(attr.toUpperCase());
			} catch (IllegalArgumentException ex) {
				throw new IllegalArgumentException("Race class not recognised: " + attr);
			}

			if (ship.isPlayerShip()) {
				attr = crew.getAttributeValue("amount");
				if (attr == null)
					throw new IllegalArgumentException("<crewCount> tag is missing 'amount' attribute.");
				for (int i = 0; i < Integer.valueOf(attr); i++)
					ship.changeCrew(Races.NO_CREW, race);
			} else {
				attr = crew.getAttributeValue("amount");
				if (attr == null)
					throw new IllegalArgumentException("<crewCount> tag is missing 'amount' attribute.");
				ship.setCrewMin(race, Integer.valueOf(attr));

				attr = crew.getAttributeValue("max");
				if (attr == null)
					throw new IllegalArgumentException("<crewCount> tag is missing 'max' attribute.");
				ship.setCrewMax(race, Integer.valueOf(attr));
			}
		}

		for (Element aug : e.getChildren("aug")) {
			attr = aug.getAttributeValue("name");
			if (attr == null)
				throw new IllegalArgumentException("An augment is missing 'name' attribute.");

			AugmentObject augmentObject = db.getAugment(attr);
			if (augmentObject == null)
				throw new IllegalArgumentException("AugBlueprint not found: " + attr);

			ship.changeAugment(Database.DEFAULT_AUGMENT_OBJ, augmentObject);
		}

		return ship;
	}

	/**
	 * Loads the layout from the given stream, and adds it to the given ship object.
	 * 
	 * @param ship
	 *            the ship object in which the loaded data will be saved
	 * @param is
	 *            input stream from which the data is read. The stream is always closed by this method.
	 * @param fileName
	 *            how error messages will refer to the stream
	 * @throws IllegalArgumentException
	 *             when the file is wrongly formatted
	 */
	public static void loadLayoutTXT(ShipObject ship, InputStream is, String fileName) throws IllegalArgumentException {
		if (ship == null)
			throw new IllegalArgumentException("Ship object must not be null.");
		if (is == null)
			throw new IllegalArgumentException("Stream must not be null.");

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
						// Not a number
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
						ship.setEllipseWidth(sc.nextInt());
						ship.setEllipseHeight(sc.nextInt());
						ship.setEllipseX(sc.nextInt());
						ship.setEllipseY(sc.nextInt() + (ship.isPlayerShip() ? 0 : Database.ENEMY_SHIELD_Y_OFFSET));
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
						throw new IllegalArgumentException("Unrecognised layout object: " + layoutObject);
				}
			}

			// Link doors to rooms
			for (DoorObject door : ship.getDoors()) {
				door.setLeftRoom(ship.getRoomById(leftMap.get(door)));
				door.setRightRoom(ship.getRoomById(rightMap.get(door)));
			}
		} finally {
			sc.close();
		}
	}

	/**
	 * Loads the layout from the given file, and adds it to the given ship object.
	 * 
	 * @param ship
	 *            the ship object in which the loaded data will be saved
	 * @param f
	 *            file from which the data is read
	 * @throws IllegalArgumentException
	 *             when the file is wrongly formatted
	 * @throws IOException
	 *             when a general IO error occurs
	 */
	public static void loadLayoutTXT(ShipObject ship, File f) throws IllegalArgumentException, IOException {
		if (ship == null)
			throw new IllegalArgumentException("Ship object must not be null.");
		if (f == null)
			throw new IllegalArgumentException("File must not be null.");
		loadLayoutTXT(ship, new FileInputStream(f), f.getName());
	}

	/**
	 * Loads the XML layout from the stream.
	 * 
	 * @param ship
	 *            ship object in which the loaded data will be saved
	 * @param is
	 *            stream from which the data is read. The stream is always closed by this method.
	 * @param fileName
	 *            how error messages will refer to the stream
	 * @throws IllegalArgumentException
	 *             when the file is wrongly formatted - a tag or an attribute is missing
	 * @throws JDOMParseException
	 *             when a parsing error occurs
	 * @throws IOException
	 *             when a general IO error occurs
	 */
	public static void loadLayoutXML(ShipObject ship, InputStream is, String fileName)
			throws IllegalArgumentException, JDOMParseException, IOException {
		if (ship == null)
			throw new IllegalArgumentException("Ship object must not be null.");
		if (is == null)
			throw new IllegalArgumentException("Stream must not be null.");

		try {
			Document doc = IOUtils.readStreamXML(is, fileName);

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

			int id = 0;
			for (Element mountEl : child.getChildren("mount")) {
				MountObject mount = new MountObject();
				mount.setId(id++);

				attr = mountEl.getAttributeValue("x");
				if (attr == null)
					throw new IllegalArgumentException("Mount missing 'x' attribute");
				offset.x = Integer.valueOf(attr);

				attr = mountEl.getAttributeValue("y");
				if (attr == null)
					throw new IllegalArgumentException("Mount missing 'y' attribute");
				offset.y = Integer.valueOf(attr);

				mount.setLocation(offset.x, offset.y);

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

			// Link mounts to gibs
			for (MountObject mount : gibMap.keySet())
				mount.setGib(ship.getGibById(gibMap.get(mount)));

			gibMap.clear();
		} finally {
			is.close();
		}
	}

	/**
	 * 
	 * @param ship
	 *            ship object in which the loaded data will be saved
	 * @param f
	 *            file from which the data is read
	 * @throws IllegalArgumentException
	 *             when the file is wrongly formatted - a tag or an attribute is missing
	 * @throws JDOMParseException
	 *             when a parsing error occurs
	 * @throws IOException
	 *             when a general IO error occurs
	 */
	public static void loadLayoutXML(ShipObject ship, File f) throws IllegalArgumentException, JDOMParseException, IOException {
		if (ship == null)
			throw new IllegalArgumentException("Ship object must not be null.");
		if (f == null)
			throw new IllegalArgumentException("File must not be null.");
		loadLayoutXML(ship, new FileInputStream(f), f.getName());
	}

	private static String firstExisting(String[] prefixes, String suffix, Database db) {
		for (String prefix : prefixes) {
			if (db.contains(IOUtils.trimProtocol(prefix) + suffix))
				return prefix + suffix;
		}
		return null;
	}
}
