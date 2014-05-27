package com.kartoflane.superluminal2.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.graphics.Rectangle;
import org.jdom2.Comment;
import org.jdom2.Document;
import org.jdom2.Element;

import com.kartoflane.superluminal2.components.enums.Images;
import com.kartoflane.superluminal2.components.enums.LayoutObjects;
import com.kartoflane.superluminal2.components.enums.Races;
import com.kartoflane.superluminal2.components.enums.Systems;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ftl.AugmentObject;
import com.kartoflane.superluminal2.ftl.DoorObject;
import com.kartoflane.superluminal2.ftl.DroneList;
import com.kartoflane.superluminal2.ftl.DroneObject;
import com.kartoflane.superluminal2.ftl.GibObject;
import com.kartoflane.superluminal2.ftl.GlowObject;
import com.kartoflane.superluminal2.ftl.GlowSet;
import com.kartoflane.superluminal2.ftl.GlowSet.Glows;
import com.kartoflane.superluminal2.ftl.ImageObject;
import com.kartoflane.superluminal2.ftl.MountObject;
import com.kartoflane.superluminal2.ftl.RoomObject;
import com.kartoflane.superluminal2.ftl.ShipObject;
import com.kartoflane.superluminal2.ftl.StationObject;
import com.kartoflane.superluminal2.ftl.SystemObject;
import com.kartoflane.superluminal2.ftl.WeaponList;
import com.kartoflane.superluminal2.ftl.WeaponObject;
import com.kartoflane.superluminal2.ui.ShipContainer;

public class ShipSaveUtils {

	private static final Logger log = LogManager.getLogger(ShipSaveUtils.class);

	public static void saveShipFTL(File saveFile, ShipContainer container) throws IllegalArgumentException, IOException {
		if (saveFile == null)
			throw new IllegalArgumentException("Destination file must not be null.");
		if (saveFile.isDirectory())
			throw new IllegalArgumentException("Not a file: " + saveFile.getName());

		HashMap<String, byte[]> fileMap = saveShip(container);

		// Write the zip archive
		ZipInputStream in = null;
		ZipOutputStream out = null;
		try {
			in = new ZipInputStream(new ByteArrayInputStream(createZip(fileMap)));
			out = new ZipOutputStream(new FileOutputStream(saveFile));

			ZipEntry entry = null;
			while ((entry = in.getNextEntry()) != null) {
				out.putNextEntry(entry);

				byte[] byteBuff = new byte[4096];
				int bytesRead = 0;
				while ((bytesRead = in.read(byteBuff)) != -1)
					out.write(byteBuff, 0, bytesRead);

				in.closeEntry();
			}
		} finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}
	}

	public static void saveShipXML(File destination, ShipContainer container) throws IllegalArgumentException, IOException {
		if (destination == null)
			throw new IllegalArgumentException("Destination file must not be null.");
		if (!destination.isDirectory())
			throw new IllegalArgumentException("Not a directory: " + destination.getName());

		HashMap<String, byte[]> fileMap = saveShip(container);

		// Write the files
		for (String fileName : fileMap.keySet()) {
			ByteArrayInputStream in = null;
			FileOutputStream out = null;

			File file = new File(destination.getAbsolutePath() + "/" + fileName);
			file.getParentFile().mkdirs();

			try {
				in = new ByteArrayInputStream(fileMap.get(fileName));
				out = new FileOutputStream(file);
				IOUtils.write(in, out);
			} finally {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			}
		}
	}

	public static HashMap<String, byte[]> saveShip(ShipContainer container) throws IOException {
		if (container == null)
			throw new IllegalArgumentException("ShipContainer must not be null.");

		ShipObject ship = container.getShipController().getGameObject();

		if (ship == null)
			throw new IllegalArgumentException("Ship object must not be null.");

		container.updateGameObjects();
		ship.coalesceRooms();
		ship.linkDoors();
		GlowObject[] newGlows = ship.createGlows();

		HashMap<String, byte[]> fileMap = new HashMap<String, byte[]>();
		String fileName = null;
		byte[] bytes = null;

		// Create the files in memory
		fileName = "data/" + Database.getInstance().getAssociatedFile(ship.getBlueprintName()) + ".append";
		bytes = IOUtils.readDocument(generateBlueprintXML(ship)).getBytes();
		fileMap.put(fileName, bytes);

		fileName = "data/" + ship.getLayout() + ".txt";
		bytes = generateLayoutTXT(ship).getBytes();
		fileMap.put(fileName, bytes);

		fileName = "data/" + ship.getLayout() + ".xml";
		bytes = IOUtils.readDocument(generateLayoutXML(ship)).getBytes();
		fileMap.put(fileName, bytes);

		// Create the rooms.xml.append file for glow locations
		if (newGlows.length != 0) {
			fileName = "data/rooms.xml.append";
			bytes = IOUtils.readDocument(generateRoomsXML(newGlows)).getBytes();
			fileMap.put(fileName, bytes);
		}

		// Copy images
		for (Images img : Images.values()) {
			ImageObject object = ship.getImage(img);
			String path = object.getImagePath();

			if (path != null) {
				InputStream is = null;
				try {
					is = Manager.getInputStream(path);
					fileName = img.getDatRelativePath(ship) + img.getPrefix() + ship.getImageNamespace() + img.getSuffix() + ".png";
					fileMap.put(fileName, IOUtils.readStream(is));
				} catch (FileNotFoundException e) {
					log.warn(String.format("File for %s image could not be found: %s", img, path));
				} finally {
					if (is != null)
						is.close();
				}
			}
		}

		for (Systems sys : Systems.getSystems()) {
			for (SystemObject object : ship.getSystems(sys)) {
				String path = object.getInteriorPath();

				if (path != null) {
					InputStream is = null;
					try {
						is = Manager.getInputStream(path);
						fileName = "img/ship/interior/" + object.getInteriorNamespace() + ".png";
						fileMap.put(fileName, IOUtils.readStream(is));
					} catch (FileNotFoundException e) {
						log.warn(String.format("File for %s interior image could not be found: %s", sys, path));
					} finally {
						if (is != null)
							is.close();
					}
				}
			}
		}

		for (GlowObject object : newGlows) {
			GlowSet set = object.getGlowSet();
			String path = set.getImage(Glows.CLOAK);

			if (path != null) {
				SystemObject cloaking = ship.getSystem(Systems.CLOAKING);
				InputStream is = null;
				try {
					is = Manager.getInputStream(path);
					fileName = "img/ship/interior/" + cloaking.getInteriorNamespace() + Glows.CLOAK.getSuffix() + ".png";
					fileMap.put(fileName, IOUtils.readStream(is));
				} catch (FileNotFoundException e) {
					log.warn("File for cloaking glow image could not be found: " + path);
				} finally {
					if (is != null)
						is.close();
				}
			} else {
				for (Glows glowId : Glows.getGlows()) {
					path = set.getImage(glowId);

					if (path != null) {
						InputStream is = null;
						try {
							is = Manager.getInputStream(path);
							fileName = "img/ship/interior/" + set.getIdentifier() + glowId.getSuffix() + ".png";
							fileMap.put(fileName, IOUtils.readStream(is));
						} catch (FileNotFoundException e) {
							log.warn(String.format("File for %s's %s glow image could not be found: %s", object.getIdentifier(), glowId, path));
						} finally {
							if (is != null)
								is.close();
						}
					}
				}
			}
		}

		// TODO gib images

		return fileMap;
	}

	public static void saveLayoutTXT(ShipObject ship, File f) throws FileNotFoundException, IOException {
		if (ship == null)
			throw new IllegalArgumentException("Ship object must not be null.");
		if (f == null)
			throw new IllegalArgumentException("File must not be null.");

		FileWriter writer = null;

		try {
			writer = new FileWriter(f);
			writer.write(generateLayoutTXT(ship));
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	public static void saveLayoutXML(ShipObject ship, File f) throws IllegalArgumentException, IOException {
		if (ship == null)
			throw new IllegalArgumentException("Ship object must not be null.");
		if (f == null)
			throw new IllegalArgumentException("File must not be null.");

		IOUtils.writeFileXML(generateLayoutXML(ship), f);
	}

	/**
	 * This method generates the shipBlueprint tag that describes the ship passed as argument.
	 * 
	 * @param ship
	 *            the ship to be saved
	 * @return the XML document containing the shipBlueprint tag
	 */
	public static Document generateBlueprintXML(ShipObject ship) {
		Document doc = new Document();
		Element root = new Element("wrapper");
		Element e = null;
		String attr = null;

		Element shipBlueprint = new Element("shipBlueprint");
		attr = ship.getBlueprintName();
		shipBlueprint.setAttribute("name", attr == null ? "" : attr);
		attr = ship.getLayout();
		shipBlueprint.setAttribute("layout", attr == null ? "" : attr);
		attr = ship.getImageNamespace();
		shipBlueprint.setAttribute("img", attr == null ? "" : attr);

		// The ship's class name, used for flavor only on player ships,
		// but on enemy ships it is used as the enemy ship's name
		e = new Element("class");
		attr = ship.getShipClass();
		e.setText(attr == null ? "" : attr);
		shipBlueprint.addContent(e);

		// Name and description only affect player ships
		if (ship.isPlayerShip()) {
			e = new Element("name");
			attr = ship.getShipName();
			e.setText(attr == null ? "" : attr);
			shipBlueprint.addContent(e);

			e = new Element("desc");
			attr = ship.getShipDescription();
			e.setText(attr == null ? "" : attr);
			shipBlueprint.addContent(e);
		}
		// Sector tags
		// Enemy exclusive
		else {
			e = new Element("minSector");
			e.setText("" + ship.getMinSector());
			shipBlueprint.addContent(e);

			e = new Element("maxSector");
			e.setText("" + ship.getMaxSector());
			shipBlueprint.addContent(e);
		}

		Element systemList = new Element("systemList");
		for (Systems sys : Systems.getSystems()) {
			for (SystemObject system : ship.getSystems(sys)) {
				if (system.isAssigned()) {
					Element sysEl = new Element(sys.toString().toLowerCase());

					sysEl.setAttribute("power", "" + system.getLevelStart());

					// Enemy ships' system have a 'max' attribute which determines the max level of the system
					if (!ship.isPlayerShip())
						sysEl.setAttribute("max", "" + system.getLevelMax());

					sysEl.setAttribute("room", "" + system.getRoom().getId());

					sysEl.setAttribute("start", "" + system.isAvailable());

					// Artillery has a special 'weapon' attribute to determine which weapon is used as artillery weapon
					if (sys == Systems.ARTILLERY)
						sysEl.setAttribute("weapon", "ARTILLERY_FED"); // TODO artillery weapon, for now default to ARTILLERY_FED

					if (system.canContainInterior() && ship.isPlayerShip() && system.getInteriorNamespace() != null)
						sysEl.setAttribute("img", system.getInteriorNamespace());

					StationObject station = system.getStation();

					if (sys.canContainStation() && ship.isPlayerShip()) {
						Element slotEl = new Element("slot");

						// Medbay and Clonebay slots don't have a direction - they're always NONE
						if (sys != Systems.MEDBAY && sys != Systems.CLONEBAY) {
							e = new Element("direction");
							e.setText(station.getSlotDirection().toString());
							slotEl.addContent(e); // Add <direction> to <slot>
						}

						e = new Element("number");
						e.setText("" + station.getSlotId());
						slotEl.addContent(e); // Add <number> to <slot>

						sysEl.addContent(slotEl);
					}

					systemList.addContent(sysEl);
				}
			}
		}
		shipBlueprint.addContent(systemList);

		e = new Element("weaponSlots");
		e.setText("" + ship.getWeaponSlots());
		shipBlueprint.addContent(e);

		e = new Element("droneSlots");
		e.setText("" + ship.getDroneSlots());
		shipBlueprint.addContent(e);

		Element weaponList = new Element("weaponList");
		weaponList.setAttribute("missiles", "" + ship.getMissilesAmount());
		weaponList.setAttribute("count", "" + ship.getWeaponSlots());

		// Player ships' weapons have to be declared explicitly, ie. listed by name
		// Only the first 'count' weapons are loaded in-game
		if (ship.isPlayerShip()) {
			for (WeaponObject weapon : ship.getWeapons()) {
				if (weapon == Database.DEFAULT_WEAPON_OBJ)
					continue;
				e = new Element("weapon");
				e.setAttribute("name", weapon.getBlueprintName());
				weaponList.addContent(e);
			}
		}
		// Enemy ships' weapons are randomly drafted from a list of weapons
		// 'count' determines how many weapons are drafted
		else {
			WeaponList list = ship.getWeaponList();
			if (list != Database.DEFAULT_WEAPON_LIST)
				weaponList.setAttribute("load", list.getBlueprintName());
		}
		shipBlueprint.addContent(weaponList);

		Element droneList = new Element("droneList");
		droneList.setAttribute("drones", "" + ship.getDronePartsAmount());
		droneList.setAttribute("count", "" + ship.getDroneSlots());

		// Player ships' drones have to be declared explicitly, ie. listed by name
		// Only the first 'count' drones are loaded in-game
		if (ship.isPlayerShip()) {
			for (DroneObject drone : ship.getDrones()) {
				if (drone == Database.DEFAULT_DRONE_OBJ)
					continue;
				e = new Element("drone");
				e.setAttribute("name", drone.getBlueprintName());
				droneList.addContent(e);
			}
		}
		// Enemy ships' drones are randomly drafted from a list of drones
		// 'count' determines how many drones are drafted
		else {
			DroneList list = ship.getDroneList();
			if (list != Database.DEFAULT_DRONE_LIST)
				droneList.setAttribute("load", list.getBlueprintName());
		}
		shipBlueprint.addContent(droneList);

		// Defines the ship's health points
		e = new Element("health");
		e.setAttribute("amount", "" + ship.getHealth());
		shipBlueprint.addContent(e);

		// Defines the amount of power the ship starts with
		e = new Element("maxPower");
		e.setAttribute("amount", "" + ship.getPower());
		shipBlueprint.addContent(e);

		if (ship.isPlayerShip()) {
			// List every crew member individually to allow ordering of crew
			for (Races race : ship.getCrew()) {
				if (race == Races.NO_CREW)
					continue;
				e = new Element("crewCount");
				e.setAttribute("amount", "1");
				e.setAttribute("class", race.name().toLowerCase());

				shipBlueprint.addContent(e);
			}
		} else {
			for (Races race : Races.getRaces()) {
				int amount = ship.getCrewMin(race);
				int max = ship.getCrewMax(race);

				e = new Element("crewCount");
				e.setAttribute("amount", "" + amount);
				e.setAttribute("max", "" + max);
				e.setAttribute("class", race.name().toLowerCase());

				// Don't print an empty tag
				if (amount > 0 && (ship.isPlayerShip() || max > 0))
					shipBlueprint.addContent(e);
			}

			// <boardingAI> tag, enemy exclusive
			e = new Element("boardingAI");
			e.setText(ship.getBoardingAI().toString().toLowerCase());
			shipBlueprint.addContent(e);
		}

		for (AugmentObject aug : ship.getAugments()) {
			if (aug == Database.DEFAULT_AUGMENT_OBJ)
				continue;
			e = new Element("aug");
			e.setAttribute("name", aug.getBlueprintName());
			shipBlueprint.addContent(e);
		}

		root.addContent(shipBlueprint);
		doc.setRootElement(root);

		return doc;
	}

	public static String generateLayoutTXT(ShipObject ship) {
		StringBuilder buf = new StringBuilder();

		buf.append(LayoutObjects.X_OFFSET);
		buf.append("\r\n");
		buf.append("" + ship.getXOffset());
		buf.append("\r\n");

		buf.append(LayoutObjects.Y_OFFSET);
		buf.append("\r\n");
		buf.append("" + ship.getYOffset());
		buf.append("\r\n");

		buf.append(LayoutObjects.HORIZONTAL);
		buf.append("\r\n");
		buf.append("" + ship.getHorizontal());
		buf.append("\r\n");

		buf.append(LayoutObjects.VERTICAL);
		buf.append("\r\n");
		buf.append("" + ship.getVertical());
		buf.append("\r\n");

		buf.append(LayoutObjects.ELLIPSE);
		buf.append("\r\n");
		Rectangle ellipse = ship.getEllipse();
		buf.append("" + ellipse.width);
		buf.append("\r\n");
		buf.append("" + ellipse.height);
		buf.append("\r\n");
		buf.append("" + ellipse.x);
		buf.append("\r\n");
		buf.append("" + (ellipse.y - (ship.isPlayerShip() ? 0 : Database.ENEMY_SHIELD_Y_OFFSET)));
		buf.append("\r\n");

		for (RoomObject room : ship.getRooms()) {
			buf.append(LayoutObjects.ROOM);
			buf.append("\r\n");
			buf.append("" + room.getId());
			buf.append("\r\n");
			buf.append("" + room.getX());
			buf.append("\r\n");
			buf.append("" + room.getY());
			buf.append("\r\n");
			buf.append("" + room.getW());
			buf.append("\r\n");
			buf.append("" + room.getH());
			buf.append("\r\n");
		}

		RoomObject linked = null;
		for (DoorObject door : ship.getDoors()) {
			buf.append(LayoutObjects.DOOR);
			buf.append("\r\n");
			buf.append("" + door.getX());
			buf.append("\r\n");
			buf.append("" + door.getY());
			buf.append("\r\n");
			linked = door.getLeftRoom();
			buf.append(linked == null ? "-1" : linked.getId());
			buf.append("\r\n");
			linked = door.getRightRoom();
			buf.append(linked == null ? "-1" : linked.getId());
			buf.append("\r\n");
			buf.append("" + (door.isHorizontal() ? "0" : "1"));
			buf.append("\r\n");
		}

		return buf.toString();
	}

	public static Document generateLayoutXML(ShipObject ship) {
		Document doc = new Document();
		Element root = new Element("wrapper");
		Element e = null;

		Comment c = new Comment("Copyright (c) 2012 by Subset Games. All rights reserved.");
		root.addContent(c);

		e = new Element("img");
		Rectangle hullDimensions = ship.getHullDimensions();
		e.setAttribute("x", "" + hullDimensions.x);
		e.setAttribute("y", "" + hullDimensions.y);
		e.setAttribute("w", "" + hullDimensions.width);
		e.setAttribute("h", "" + hullDimensions.height);
		root.addContent(e);

		Element offsets = new Element("offsets");

		e = new Element("floor");
		e.setAttribute("x", "" + ship.getFloorOffset().x);
		e.setAttribute("y", "" + ship.getFloorOffset().y);
		offsets.addContent(e);

		e = new Element("cloak");
		e.setAttribute("x", "" + ship.getCloakOffset().x);
		e.setAttribute("y", "" + ship.getCloakOffset().y);
		offsets.addContent(e);

		root.addContent(offsets);

		Element weaponMounts = new Element("weaponMounts");

		MountObject[] mounts = ship.getMounts();
		for (int i = 0; i < mounts.length; i++) {
			e = new Element("mount");
			e.setAttribute("x", "" + mounts[i].getX());
			e.setAttribute("y", "" + mounts[i].getY());
			e.setAttribute("rotate", "" + mounts[i].isRotated());
			e.setAttribute("mirror", "" + mounts[i].isMirrored());
			e.setAttribute("gib", "" + mounts[i].getGib().getId());
			e.setAttribute("slide", "" + mounts[i].getDirection().toString());
			weaponMounts.addContent(e);
		}
		root.addContent(weaponMounts);

		Element explosion = new Element("explosion");

		DecimalFormat decimal = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));
		GibObject[] gibs = ship.getGibs();
		for (int i = 0; i < gibs.length; i++) {
			Element gib = new Element("gib" + (i + 1));

			e = new Element("velocity");
			e.setAttribute("min", "" + decimal.format(gibs[i].getVelocityMin()));
			e.setAttribute("max", "" + decimal.format(gibs[i].getVelocityMax()));
			gib.addContent(e);

			e = new Element("angular");
			e.setAttribute("min", "" + decimal.format(gibs[i].getAngularMin()));
			e.setAttribute("max", "" + decimal.format(gibs[i].getAngularMax()));
			gib.addContent(e);

			e = new Element("direction");
			e.setAttribute("min", "" + gibs[i].getDirectionMin());
			e.setAttribute("max", "" + gibs[i].getDirectionMax());
			gib.addContent(e);

			e = new Element("x");
			e.setText("" + gibs[i].getX());
			gib.addContent(e);

			e = new Element("y");
			e.setText("" + gibs[i].getY());
			gib.addContent(e);

			explosion.addContent(gib);
		}

		root.addContent(explosion);
		doc.setRootElement(root);

		return doc;
	}

	public static Document generateRoomsXML(GlowObject[] newGlows) {
		Document doc = new Document();
		Element root = new Element("wrapper");

		for (GlowObject glow : newGlows) {
			Element glowEl = new Element("roomLayout");
			glowEl.setAttribute("name", glow.getIdentifier());

			Element e = new Element("computerGlow");
			if (!glow.getGlowSet().getIdentifier().equals("glow"))
				e.setAttribute("name", glow.getGlowSet().getIdentifier());
			e.setAttribute("x", "" + glow.getX());
			e.setAttribute("y", "" + glow.getY());
			e.setAttribute("dir", "" + glow.getDirection().name());

			glowEl.addContent(e);
			root.addContent(glowEl);
		}

		doc.setRootElement(root);
		return doc;
	}

	private static byte[] createZip(Map<String, byte[]> files) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ZipOutputStream zf = new ZipOutputStream(bos);
		Iterator<String> it = files.keySet().iterator();
		String fileName = null;
		ZipEntry ze = null;

		while (it.hasNext()) {
			fileName = it.next();
			ze = new ZipEntry(fileName);
			zf.putNextEntry(ze);
			zf.write(files.get(fileName));
		}
		zf.close();

		return bos.toByteArray();
	}
}
