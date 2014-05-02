package com.kartoflane.superluminal2.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import net.vhati.ftldat.FTLDat.FTLPack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.JDOMParseException;

import com.kartoflane.superluminal2.components.ShipMetadata;
import com.kartoflane.superluminal2.components.enums.DroneTypes;
import com.kartoflane.superluminal2.components.enums.PlayerShipBlueprints;
import com.kartoflane.superluminal2.components.enums.WeaponTypes;
import com.kartoflane.superluminal2.components.interfaces.Predicate;
import com.kartoflane.superluminal2.core.Utils.DecodeResult;
import com.kartoflane.superluminal2.ftl.AnimationObject;
import com.kartoflane.superluminal2.ftl.AugmentObject;
import com.kartoflane.superluminal2.ftl.BlueprintList;
import com.kartoflane.superluminal2.ftl.DroneList;
import com.kartoflane.superluminal2.ftl.DroneObject;
import com.kartoflane.superluminal2.ftl.GlowObject;
import com.kartoflane.superluminal2.ftl.GlowSet;
import com.kartoflane.superluminal2.ftl.GlowSet.Glows;
import com.kartoflane.superluminal2.ftl.WeaponList;
import com.kartoflane.superluminal2.ftl.WeaponObject;

public class Database {

	public static final Logger log = LogManager.getLogger(Database.class);

	public static final AnimationObject DEFAULT_ANIM_OBJ = new AnimationObject();
	public static final WeaponObject DEFAULT_WEAPON_OBJ = new WeaponObject();
	public static final DroneObject DEFAULT_DRONE_OBJ = new DroneObject();
	public static final GlowSet DEFAULT_GLOW_SET = new GlowSet();
	public static final GlowObject DEFAULT_GLOW_OBJ = new GlowObject();
	public static final WeaponList DEFAULT_WEAPON_LIST = new WeaponList();
	public static final DroneList DEFAULT_DRONE_LIST = new DroneList();

	private static final Database instance = new Database();

	private FTLPack data = null;
	private FTLPack resource = null;

	// Constant
	private HashMap<String, String> shipFileMap = new HashMap<String, String>();

	// Dynamically loaded
	private HashMap<String, ArrayList<ShipMetadata>> shipMetadata = new HashMap<String, ArrayList<ShipMetadata>>();
	private TreeSet<AnimationObject> animationObjects = new TreeSet<AnimationObject>();
	private TreeSet<WeaponObject> weaponObjects = new TreeSet<WeaponObject>();
	private TreeSet<DroneObject> droneObjects = new TreeSet<DroneObject>();
	private TreeSet<AugmentObject> augmentObjects = new TreeSet<AugmentObject>();
	private TreeSet<GlowObject> glowObjects = new TreeSet<GlowObject>();
	private TreeSet<GlowSet> glowSets = new TreeSet<GlowSet>();
	private TreeSet<WeaponList> weaponLists = new TreeSet<WeaponList>();
	private TreeSet<DroneList> droneLists = new TreeSet<DroneList>();

	/** Temporary map to hold anim sheets, since they need to be loaded before weaponAnims, which reference them */
	private HashMap<String, Element> animSheetMap = new HashMap<String, Element>();

	private Database() {
		String blueprints = "blueprints.xml";
		String dlcBlueprints = "dlcBlueprints.xml";
		String dlcOverwrite = "dlcBlueprintsOverwrite.xml";
		String bosses = "bosses.xml";
		// All pre-AE player ships are located in blueprints.xml
		shipFileMap.put(PlayerShipBlueprints.HARD.toString(), blueprints);
		shipFileMap.put(PlayerShipBlueprints.HARD_2.toString(), blueprints);
		shipFileMap.put(PlayerShipBlueprints.MANTIS.toString(), blueprints);
		shipFileMap.put(PlayerShipBlueprints.MANTIS_2.toString(), blueprints);
		shipFileMap.put(PlayerShipBlueprints.STEALTH.toString(), blueprints);
		shipFileMap.put(PlayerShipBlueprints.STEALTH_2.toString(), blueprints);
		shipFileMap.put(PlayerShipBlueprints.CIRCLE.toString(), blueprints);
		shipFileMap.put(PlayerShipBlueprints.CIRCLE_2.toString(), blueprints);
		shipFileMap.put(PlayerShipBlueprints.FED.toString(), blueprints);
		shipFileMap.put(PlayerShipBlueprints.FED_2.toString(), blueprints);
		shipFileMap.put(PlayerShipBlueprints.JELLY.toString(), blueprints);
		shipFileMap.put(PlayerShipBlueprints.JELLY_2.toString(), blueprints);
		shipFileMap.put(PlayerShipBlueprints.ROCK.toString(), blueprints);
		shipFileMap.put(PlayerShipBlueprints.ROCK_2.toString(), blueprints);
		shipFileMap.put(PlayerShipBlueprints.ENERGY.toString(), blueprints);
		shipFileMap.put(PlayerShipBlueprints.ENERGY_2.toString(), blueprints);
		shipFileMap.put(PlayerShipBlueprints.CRYSTAL.toString(), blueprints);
		shipFileMap.put(PlayerShipBlueprints.CRYSTAL_2.toString(), blueprints);
		// Lanius' ships are in dlcBlueprints.xml
		shipFileMap.put(PlayerShipBlueprints.ANAEROBIC.toString(), dlcBlueprints);
		shipFileMap.put(PlayerShipBlueprints.ANAEROBIC_2.toString(), dlcBlueprints);
		// Type C for all ships are located in dlcBlueprintsOverwrite.xml
		shipFileMap.put(PlayerShipBlueprints.HARD_3.toString(), dlcOverwrite);
		shipFileMap.put(PlayerShipBlueprints.MANTIS_3.toString(), dlcOverwrite);
		shipFileMap.put(PlayerShipBlueprints.STEALTH_3.toString(), dlcOverwrite);
		shipFileMap.put(PlayerShipBlueprints.CIRCLE_3.toString(), dlcOverwrite);
		shipFileMap.put(PlayerShipBlueprints.FED_3.toString(), dlcOverwrite);
		shipFileMap.put(PlayerShipBlueprints.JELLY_3.toString(), dlcOverwrite);
		shipFileMap.put(PlayerShipBlueprints.ROCK_3.toString(), dlcOverwrite);
		shipFileMap.put(PlayerShipBlueprints.ENERGY_3.toString(), dlcOverwrite);
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
		try {
			PlayerShipBlueprints.valueOf(blueprintName.replace("PLAYER_SHIP_", ""));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String getAssociatedFile(String blueprint) {
		if (shipFileMap.containsKey(blueprint))
			return shipFileMap.get(blueprint);
		else
			return "autoBlueprints.xml";
	}

	public void store(BlueprintList<?> list) {
		if (list instanceof WeaponList) {
			weaponLists.add((WeaponList) list);
		} else if (list instanceof DroneList) {
			droneLists.add((DroneList) list);
		} else {
			// Not interested in any other lists
		}
	}

	public WeaponList[] getWeaponLists() {
		return weaponLists.toArray(new WeaponList[0]);
	}

	public DroneList[] getDroneLists() {
		return droneLists.toArray(new DroneList[0]);
	}

	public WeaponList getWeaponList(String name) {
		WeaponList[] lists = getWeaponLists();
		try {
			return lists[Utils.binarySearch(lists, name, 0, lists.length)];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public DroneList getDroneList(String name) {
		DroneList[] lists = getDroneLists();
		try {
			return lists[Utils.binarySearch(lists, name, 0, lists.length)];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public void store(AnimationObject anim) {
		animationObjects.add(anim);
	}

	public AnimationObject getAnimation(String animName) {
		AnimationObject[] anims = animationObjects.toArray(new AnimationObject[0]);
		try {
			return anims[Utils.binarySearch(anims, animName, 0, anims.length)];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public void store(WeaponObject weapon) {
		weaponObjects.add(weapon);
	}

	public WeaponObject getWeapon(String blueprint) {
		WeaponObject[] weapons = weaponObjects.toArray(new WeaponObject[0]);
		try {
			return weapons[Utils.binarySearch(weapons, blueprint, 0, weapons.length)];
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

	public void store(DroneObject drone) {
		droneObjects.add(drone);
	}

	public DroneObject getDrone(String blueprint) {
		DroneObject[] drones = droneObjects.toArray(new DroneObject[0]);
		try {
			return drones[Utils.binarySearch(drones, blueprint, 0, drones.length)];
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

	public void store(AugmentObject augment) {
		augmentObjects.add(augment);
	}

	public AugmentObject getAugment(String blueprint) {
		AugmentObject[] augments = getAugments();
		try {
			return augments[Utils.binarySearch(augments, blueprint, 0, augments.length)];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public AugmentObject[] getAugments() {
		return augmentObjects.toArray(new AugmentObject[0]);
	}

	public void store(GlowObject glow) {
		glowObjects.add(glow);
	}

	public GlowObject getGlow(String id) {
		GlowObject[] glows = getGlows();
		try {
			return glows[Utils.binarySearch(glows, id, 0, glows.length)];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public GlowObject[] getGlows() {
		return glowObjects.toArray(new GlowObject[0]);
	}

	public void store(GlowSet set) {
		glowSets.add(set);
	}

	public GlowSet getGlowSet(String id) {
		GlowSet[] glowSets = getGlowSets();
		try {
			return glowSets[Utils.binarySearch(glowSets, id, 0, glowSets.length)];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public GlowSet[] getGlowSets() {
		return glowSets.toArray(new GlowSet[0]);
	}

	public void store(ShipMetadata metadata) {
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
						store(DataUtils.loadAnim(e));
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

	public void loadGlowSets() {
		final Pattern glowPtrn = Pattern.compile("[0-9]\\.png");

		Predicate<String> filter = new Predicate<String>() {
			@Override
			public boolean accept(String path) {
				return path.contains("img/ship/interior/") &&
						(glowPtrn.matcher(path).find() || path.endsWith("_glow.png"));
			}
		};

		TreeSet<String> eligiblePaths = new TreeSet<String>();
		for (String path : resource.list()) {
			if (filter.accept(path))
				eligiblePaths.add(path);
		}

		for (String s1 : eligiblePaths) {
			if (s1.endsWith("_glow.png")) {
				String namespace = s1.replaceAll("_glow.png", "");
				namespace = namespace.replace("img/ship/interior/", "");
				GlowSet set = new GlowSet(namespace);
				set.setImage(Glows.CLOAK, "rdat:" + s1);
				glowSets.add(set);
			} else if (s1.endsWith("1.png")) {
				String namespace = s1.replaceAll("[0-9]\\.png", "");
				String s2 = find(eligiblePaths, namespace + "2.png");
				String s3 = find(eligiblePaths, namespace + "3.png");

				if (s1 != null && s2 != null && s3 != null) {
					namespace = namespace.replace("img/ship/interior/", "");
					GlowSet set = new GlowSet(namespace);
					set.setImage(Glows.BLUE, "rdat:" + s1);
					set.setImage(Glows.GREEN, "rdat:" + s2);
					set.setImage(Glows.YELLOW, "rdat:" + s3);
					glowSets.add(set);
				}
			}
		}

	}

	private static String find(TreeSet<String> list, String name) {
		for (String s : list) {
			if (s.equals(name))
				return s;
			// Break if the current string is greater than the sought one
			else if (s.compareTo(name) > 0)
				break;
		}
		return null;
	}
}
