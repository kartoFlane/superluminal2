package com.kartoflane.superluminal2.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import net.vhati.ftldat.FTLDat.FTLPack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.JDOMParseException;

import com.kartoflane.superluminal2.components.ShipMetadata;
import com.kartoflane.superluminal2.core.Utils.DecodeResult;
import com.kartoflane.superluminal2.ftl.AnimationObject;
import com.kartoflane.superluminal2.ftl.AugmentObject;
import com.kartoflane.superluminal2.ftl.DroneObject;
import com.kartoflane.superluminal2.ftl.WeaponObject;

public class Database {

	public enum DroneTypes {
		COMBAT, SHIP_REPAIR, BOARDER, BATTLE, REPAIR, DEFENSE, SHIELD, HACKING
	}

	public enum WeaponTypes {
		MISSILES, LASER, BEAM, BOMB, BURST
	}

	public static final Logger log = LogManager.getLogger(Database.class);

	public static final AnimationObject DEFAULT_ANIM_OBJ = new AnimationObject();
	public static final WeaponObject DEFAULT_WEAPON_OBJ = new WeaponObject();

	private static final Database instance = new Database();

	private FTLPack data = null;
	private FTLPack resource = null;

	// Constant
	private ArrayList<String> playerShipBlueprintNames = new ArrayList<String>();
	private HashMap<String, String> shipFileMap = new HashMap<String, String>();

	// Dynamically loaded
	private HashMap<String, ArrayList<ShipMetadata>> shipMetadata = new HashMap<String, ArrayList<ShipMetadata>>();
	private TreeSet<AnimationObject> animationObjects = new TreeSet<AnimationObject>();
	private TreeSet<WeaponObject> weaponObjects = new TreeSet<WeaponObject>();
	private TreeSet<DroneObject> droneObjects = new TreeSet<DroneObject>();
	private TreeSet<AugmentObject> augmentObjects = new TreeSet<AugmentObject>();

	/** Temporary map to hold anim sheets, since they need to be loaded before weaponAnims, which reference them */
	private HashMap<String, Element> animSheetMap = new HashMap<String, Element>();

	private Database() {
		playerShipBlueprintNames.add("PLAYER_SHIP_HARD");
		playerShipBlueprintNames.add("PLAYER_SHIP_HARD_2");
		playerShipBlueprintNames.add("PLAYER_SHIP_HARD_3");
		playerShipBlueprintNames.add("PLAYER_SHIP_MANTIS");
		playerShipBlueprintNames.add("PLAYER_SHIP_MANTIS_2");
		playerShipBlueprintNames.add("PLAYER_SHIP_MANTIS_3");
		playerShipBlueprintNames.add("PLAYER_SHIP_STEALTH");
		playerShipBlueprintNames.add("PLAYER_SHIP_STEALTH_2");
		playerShipBlueprintNames.add("PLAYER_SHIP_STEALTH_3");
		playerShipBlueprintNames.add("PLAYER_SHIP_CIRCLE");
		playerShipBlueprintNames.add("PLAYER_SHIP_CIRCLE_2");
		playerShipBlueprintNames.add("PLAYER_SHIP_CIRCLE_3");
		playerShipBlueprintNames.add("PLAYER_SHIP_FED");
		playerShipBlueprintNames.add("PLAYER_SHIP_FED_2");
		playerShipBlueprintNames.add("PLAYER_SHIP_FED_3");
		playerShipBlueprintNames.add("PLAYER_SHIP_JELLY");
		playerShipBlueprintNames.add("PLAYER_SHIP_JELLY_2");
		playerShipBlueprintNames.add("PLAYER_SHIP_JELLY_3");
		playerShipBlueprintNames.add("PLAYER_SHIP_ROCK");
		playerShipBlueprintNames.add("PLAYER_SHIP_ROCK_2");
		playerShipBlueprintNames.add("PLAYER_SHIP_ROCK_3");
		playerShipBlueprintNames.add("PLAYER_SHIP_ENERGY");
		playerShipBlueprintNames.add("PLAYER_SHIP_ENERGY_2");
		playerShipBlueprintNames.add("PLAYER_SHIP_ENERGY_3");
		playerShipBlueprintNames.add("PLAYER_SHIP_CRYSTAL");
		playerShipBlueprintNames.add("PLAYER_SHIP_CRYSTAL_2");
		playerShipBlueprintNames.add("PLAYER_SHIP_ANAEROBIC");
		playerShipBlueprintNames.add("PLAYER_SHIP_ANAEROBIC_2");

		String blueprints = "blueprints.xml";
		String dlcBlueprints = "dlcBlueprints.xml";
		String dlcOverwrite = "dlcBlueprintsOverwrite.xml";
		String bosses = "bosses.xml";
		// All pre-AE player ships are located in blueprints.xml
		shipFileMap.put("PLAYER_SHIP_HARD", blueprints);
		shipFileMap.put("PLAYER_SHIP_HARD_2", blueprints);
		shipFileMap.put("PLAYER_SHIP_MANTIS", blueprints);
		shipFileMap.put("PLAYER_SHIP_MANTIS_2", blueprints);
		shipFileMap.put("PLAYER_SHIP_STEALTH", blueprints);
		shipFileMap.put("PLAYER_SHIP_STEALTH_2", blueprints);
		shipFileMap.put("PLAYER_SHIP_CIRCLE", blueprints);
		shipFileMap.put("PLAYER_SHIP_CIRCLE_2", blueprints);
		shipFileMap.put("PLAYER_SHIP_FED", blueprints);
		shipFileMap.put("PLAYER_SHIP_FED_2", blueprints);
		shipFileMap.put("PLAYER_SHIP_JELLY", blueprints);
		shipFileMap.put("PLAYER_SHIP_JELLY_2", blueprints);
		shipFileMap.put("PLAYER_SHIP_ROCK", blueprints);
		shipFileMap.put("PLAYER_SHIP_ROCK_2", blueprints);
		shipFileMap.put("PLAYER_SHIP_ENERGY", blueprints);
		shipFileMap.put("PLAYER_SHIP_ENERGY_2", blueprints);
		shipFileMap.put("PLAYER_SHIP_CRYSTAL", blueprints);
		shipFileMap.put("PLAYER_SHIP_CRYSTAL_2", blueprints);
		// Lanius' ships are in dlcBlueprints.xml
		shipFileMap.put("PLAYER_SHIP_ANAEROBIC", dlcBlueprints);
		shipFileMap.put("PLAYER_SHIP_ANAEROBIC_2", dlcBlueprints);
		// Type C for all ships are located in dlcBlueprintsOverwrite.xml
		shipFileMap.put("PLAYER_SHIP_HARD_3", dlcOverwrite);
		shipFileMap.put("PLAYER_SHIP_MANTIS_3", dlcOverwrite);
		shipFileMap.put("PLAYER_SHIP_STEALTH_3", dlcOverwrite);
		shipFileMap.put("PLAYER_SHIP_CIRCLE_3", dlcOverwrite);
		shipFileMap.put("PLAYER_SHIP_FED_3", dlcOverwrite);
		shipFileMap.put("PLAYER_SHIP_JELLY_3", dlcOverwrite);
		shipFileMap.put("PLAYER_SHIP_ROCK_3", dlcOverwrite);
		shipFileMap.put("PLAYER_SHIP_ENERGY_3", dlcOverwrite);
		shipFileMap.put("PLAYER_SHIP_CRYSTAL_3", dlcOverwrite);
		// Boss ships are located in bosses.xml
		shipFileMap.put("BOSS_1_EASY", bosses);
		shipFileMap.put("BOSS_2_EASY", bosses);
		shipFileMap.put("BOSS_3_EASY", bosses);
		shipFileMap.put("BOSS_1_EASY_DLC", bosses);
		shipFileMap.put("BOSS_2_EASY_DLC", bosses);
		shipFileMap.put("BOSS_3_EASY_DLC", bosses);
		shipFileMap.put("BOSS_1_NORMAL", bosses);
		shipFileMap.put("BOSS_2_NORMAL", bosses);
		shipFileMap.put("BOSS_3_NORMAL", bosses);
		shipFileMap.put("BOSS_1_NORMAL_DLC", bosses);
		shipFileMap.put("BOSS_2_NORMAL_DLC", bosses);
		shipFileMap.put("BOSS_3_NORMAL_DLC", bosses);
		shipFileMap.put("BOSS_1_HARD", bosses);
		shipFileMap.put("BOSS_2_HARD", bosses);
		shipFileMap.put("BOSS_3_HARD", bosses);
		shipFileMap.put("BOSS_1_HARD_DLC", bosses);
		shipFileMap.put("BOSS_2_HARD_DLC", bosses);
		shipFileMap.put("BOSS_3_HARD_DLC", bosses);
	}

	public static Database getInstance() {
		return instance;
	}

	public void setDataDat(FTLPack dat) {
		data = dat;
	}

	public FTLPack getDataDat() {
		return data;
	}

	public void setResourceDat(FTLPack dat) {
		resource = dat;
	}

	public FTLPack getResourceDat() {
		return resource;
	}

	public boolean isPlayerShip(String blueprintName) {
		return playerShipBlueprintNames.contains(blueprintName);
	}

	public String getAssociatedFile(String blueprint) {
		if (shipFileMap.containsKey(blueprint))
			return shipFileMap.get(blueprint);
		else
			return "autoBlueprints.xml";
	}

	public String[] getPlayerShipBlueprintNames() {
		return playerShipBlueprintNames.toArray(new String[0]);
	}

	public void storeAnimation(AnimationObject anim) {
		animationObjects.add(anim);
	}

	public AnimationObject getAnimation(String animName) {
		AnimationObject[] anims = animationObjects.toArray(new AnimationObject[0]);
		try {
			return anims[binarySearch(anims, animName, 0, anims.length)];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public void storeWeapon(WeaponObject weapon) {
		weaponObjects.add(weapon);
	}

	public WeaponObject getWeapon(String blueprint) {
		WeaponObject[] weapons = weaponObjects.toArray(new WeaponObject[0]);
		try {
			return weapons[binarySearch(weapons, blueprint, 0, weapons.length)];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public ArrayList<WeaponObject> getWeaponsByType(WeaponTypes type) {
		ArrayList<WeaponObject> typeWeapons = new ArrayList<WeaponObject>();
		for (WeaponObject weapon : weaponObjects) {
			if (weapon.getType() == type)
				typeWeapons.add(weapon);
		}

		return typeWeapons;
	}

	public void storeDrone(DroneObject drone) {
		droneObjects.add(drone);
	}

	public DroneObject getDrone(String blueprint) {
		DroneObject[] drones = droneObjects.toArray(new DroneObject[0]);
		try {
			return drones[binarySearch(drones, blueprint, 0, drones.length)];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public ArrayList<DroneObject> getDronesByType(DroneTypes type) {
		ArrayList<DroneObject> typeDrones = new ArrayList<DroneObject>();
		for (DroneObject drone : droneObjects) {
			if (drone.getType() == type)
				typeDrones.add(drone);
		}

		return typeDrones;
	}

	public void storeAugment(AugmentObject augment) {
		augmentObjects.add(augment);
	}

	public AugmentObject getAugment(String blueprint) {
		AugmentObject[] augments = augmentObjects.toArray(new AugmentObject[0]);
		try {
			return augments[binarySearch(augments, blueprint, 0, augments.length)];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public void storeShipMetadata(ShipMetadata metadata) {
		ArrayList<ShipMetadata> dataList = shipMetadata.get(metadata.getBlueprintName());
		if (dataList == null) {
			dataList = new ArrayList<ShipMetadata>();
			shipMetadata.put(metadata.getBlueprintName(), dataList);
		}

		dataList.add(metadata);
	}

	public HashMap<String, ArrayList<ShipMetadata>> getShipMetadata() {
		HashMap<String, ArrayList<ShipMetadata>> copiedMap = new HashMap<String, ArrayList<ShipMetadata>>();
		copiedMap.putAll(shipMetadata);
		return copiedMap;
	}

	public void clearShipMetadata() {
		shipMetadata.clear();
	}

	public ShipMetadata[] listShipMetadata() {
		return shipMetadata.values().toArray(new ShipMetadata[0]);
	}

	public void preloadAnims() {
		String[] animPaths = new String[] { "data/animations.xml", "data/dlcAnimations.xml" };

		for (String innerPath : animPaths) {
			InputStream is = null;
			try {
				is = data.getInputStream(innerPath);
				DecodeResult dr = Utils.decodeText(is, null);

				Document doc = null;
				doc = Utils.parseXML(dr.text);
				Element root = doc.getRootElement();

				// Preload anim sheets
				for (Element e : root.getChildren("animSheet")) {
					String name = e.getAttributeValue("name");
					// If older entries are allowed to be overwritten by newer ones, bomb weapons
					// load the bomb projectile images instead of weapon images, since their sheets
					// share the same name
					if (name != null && !animSheetMap.containsKey(name))
						animSheetMap.put(name, e);
				}

				// Load and store weaponAnims
				for (Element e : root.getChildren("weaponAnim")) {
					try {
						storeAnimation(DataUtils.loadAnim(e));
					} catch (IllegalArgumentException ex) {
						log.warn("Could not load animation: " + ex.getMessage());
					}
				}
			} catch (FileNotFoundException e) {
				log.error("Could not find file: " + innerPath);
			} catch (IOException e) {
				log.error("An error has occured while loading file " + innerPath + ":", e);
			} catch (JDOMParseException e) {
				log.error("An error has occured while parsing file " + innerPath + ":", e);
			} finally {
				try {
					if (is != null)
						is.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public void clearAnimSheets() {
		animSheetMap.clear();
		animSheetMap = null;
	}

	public Element getAnimSheetElement(String anim) {
		return animSheetMap.get(anim);
	}

	private static int binarySearch(AnimationObject[] array, String animName, int min, int max) {
		if (min > max)
			return -1;
		int mid = (min + max) / 2;
		int result = animName.compareTo(array[mid].getAnimName());
		if (result > 0) {
			return binarySearch(array, animName, mid + 1, max);
		} else if (result < 0) {
			return binarySearch(array, animName, min, mid - 1);
		} else {
			return mid;
		}
	}

	private static int binarySearch(WeaponObject[] array, String blueprint, int min, int max) {
		if (min > max)
			return -1;
		int mid = (min + max) / 2;
		int result = blueprint.compareTo(array[mid].getBlueprintName());
		if (result > 0) {
			return binarySearch(array, blueprint, mid + 1, max);
		} else if (result < 0) {
			return binarySearch(array, blueprint, min, mid - 1);
		} else {
			return mid;
		}
	}

	private static int binarySearch(DroneObject[] array, String blueprint, int min, int max) {
		if (min > max)
			return -1;
		int mid = (min + max) / 2;
		int result = blueprint.compareTo(array[mid].getBlueprintName());
		if (result > 0) {
			return binarySearch(array, blueprint, mid + 1, max);
		} else if (result < 0) {
			return binarySearch(array, blueprint, min, mid - 1);
		} else {
			return mid;
		}
	}

	private static int binarySearch(AugmentObject[] array, String blueprint, int min, int max) {
		if (min > max)
			return -1;
		int mid = (min + max) / 2;
		int result = blueprint.compareTo(array[mid].getBlueprintName());
		if (result > 0) {
			return binarySearch(array, blueprint, mid + 1, max);
		} else if (result < 0) {
			return binarySearch(array, blueprint, min, mid - 1);
		} else {
			return mid;
		}
	}
}
