package com.kartoflane.superluminal2.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.vhati.ftldat.FTLDat.FTLPack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.graphics.Image;

import com.kartoflane.superluminal2.components.enums.DroneTypes;
import com.kartoflane.superluminal2.components.enums.PlayerShipBlueprints;
import com.kartoflane.superluminal2.components.enums.WeaponTypes;
import com.kartoflane.superluminal2.components.interfaces.Predicate;
import com.kartoflane.superluminal2.ftl.AnimationObject;
import com.kartoflane.superluminal2.ftl.AugmentObject;
import com.kartoflane.superluminal2.ftl.DroneList;
import com.kartoflane.superluminal2.ftl.DroneObject;
import com.kartoflane.superluminal2.ftl.GibObject;
import com.kartoflane.superluminal2.ftl.GlowObject;
import com.kartoflane.superluminal2.ftl.GlowSet;
import com.kartoflane.superluminal2.ftl.RoomObject;
import com.kartoflane.superluminal2.ftl.ShipMetadata;
import com.kartoflane.superluminal2.ftl.WeaponList;
import com.kartoflane.superluminal2.ftl.WeaponObject;
import com.kartoflane.superluminal2.utils.UIUtils;

public class Database {
	private static final Logger log = LogManager.getLogger(Database.class);

	public static final AugmentObject DEFAULT_AUGMENT_OBJ = new AugmentObject();
	public static final AnimationObject DEFAULT_ANIM_OBJ = new AnimationObject();
	public static final WeaponObject DEFAULT_WEAPON_OBJ = new WeaponObject();
	public static final DroneObject DEFAULT_DRONE_OBJ = new DroneObject();
	public static final GlowSet DEFAULT_GLOW_SET = new GlowSet();
	public static final GlowObject DEFAULT_GLOW_OBJ = new GlowObject();
	public static final GibObject DEFAULT_GIB_OBJ = new GibObject();
	public static final WeaponList DEFAULT_WEAPON_LIST = new WeaponList();
	public static final DroneList DEFAULT_DRONE_LIST = new DroneList();
	public static final RoomObject AIRLOCK_OBJECT = new RoomObject();

	/**
	 * Arbitrary value that needs to be added to enemy ships' ellipse's Y offset
	 * in order for them to load correctly.
	 */
	public static final int ENEMY_SHIELD_Y_OFFSET = 110;

	/**
	 * The amount of time it takes for the ship explosion animation to complete before the
	 * score screen appears, in seconds.<br>
	 * The death time in hangar is 6 seconds.
	 */
	public static final int GIB_DEATH_ANIM_TIME = 4;

	/**
	 * Amount of pixels a gib with linear velocity of 1 moves in 1 second.
	 */
	public static final int GIB_LINEAR_SPEED = 30;

	/**
	 * The angle that a gib with angular velocity of 1 gets rotated by in 1 second, in radians.<br>
	 * A gib with angular velocity of 10 does a full rotation over the course of death animation.
	 */
	public static final double GIB_ANGULAR_SPEED = Math.PI / 20;

	private static Database instance;

	// Constant
	private HashMap<String, String> shipFileMap = new HashMap<String, String>();

	// Dynamically loaded
	private ArrayList<DatabaseEntry> dataEntries = new ArrayList<DatabaseEntry>();

	/**
	 * Creates an empty Database.
	 */
	public Database() {
		instance = this;

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

	/**
	 * Creates a new Database and fills it with data from the specified archives.
	 * 
	 * @param data
	 *            the FTLPack representing the data.dat archive
	 * @param resource
	 *            the FTLPack representing the resource.dat archive
	 */
	public Database(FTLPack data, FTLPack resource) throws FileNotFoundException, IOException {
		this();

		loadCore(data, resource);
	}

	public static Database getInstance() {
		return instance;
	}

	/**
	 * @return the first DatabaseEntry in the Database, ie. the DatabaseEntry associated with base game archives.
	 */
	public DatabaseEntry getCore() {
		return dataEntries.size() > 0 ? dataEntries.get(0) : null;
	}

	/**
	 * Reloads the core of the Database using the specified archives.
	 * 
	 * @param data
	 *            the FTLPack representing the data.dat archive
	 * @param resource
	 *            the FTLPack representing the resource.dat archive
	 */
	public void loadCore(FTLPack data, FTLPack resource) {
		if (dataEntries.size() > 0)
			dataEntries.remove(0);
		DatabaseEntry core = new DatabaseEntry(data, resource);
		core.store(DEFAULT_ANIM_OBJ);
		dataEntries.add(0, core);
	}

	/**
	 * Verifies the Database, determining whether it is safe to read data from the archives.
	 * 
	 * @return true if the database has passed the verification, false otherwise.
	 * 
	 * @throws IllegalStateException
	 *             if the database has not been initialised yet
	 */
	public boolean verify() {
		DatabaseEntry core = getCore();
		if (core == null)
			throw new IllegalStateException("Database has not been initialised yet.");

		Image img = null;
		try {
			InputStream is = core.getInputStream("img/nullResource.png");
			img = new Image(UIUtils.getDisplay(), is);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if (img != null)
				img.dispose();
		}
	}

	/**
	 * @return all database entries currently installed in the database, in the order in which they take effect.
	 *         Entry at index 0 is the core entry (ie. represents the content of the game's archives)
	 */
	public DatabaseEntry[] getEntries() {
		return dataEntries.toArray(new DatabaseEntry[0]);
	}

	/**
	 * Adds a new database entry to the database.<br>
	 * {@link #cacheAnimations()} should be called afterwards to update weapon sprites and animations.
	 */
	public void addEntry(DatabaseEntry de) {
		dataEntries.add(de);
		de.load();
	}

	/**
	 * Removes the specified database entry from the database.<br>
	 * {@link #cacheAnimations()} should be called afterwards to update weapon sprites and animations.
	 */
	public void removeEntry(DatabaseEntry de) {
		try {
			dataEntries.remove(de);
			de.dispose();
		} catch (IOException e) {
			log.error(String.format("An error has occured while closing database entry '%s': ", de.getName()), e);
		}
	}

	/**
	 * Reorders the specified database entry, allowing to manipulate the order in which the entries' contents take effect.<br>
	 * {@link #cacheAnimations()} should be called afterwards to update weapon sprites and animations.
	 * 
	 * @param de
	 *            the entry to be reordered
	 * @param index
	 *            index at which the entry is to be inserted
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range (index < 0 || index > size())
	 */
	public void reorderEntry(DatabaseEntry de, int index) throws IndexOutOfBoundsException {
		if (index < 0 || index > dataEntries.size())
			throw new IndexOutOfBoundsException();
		dataEntries.remove(de);
		dataEntries.add(index, de);
		log.trace(String.format("%s reordered to position %s", de, index));
	}

	/**
	 * Updates all WeaponObject's animations, so that they display the correct weapon image.
	 */
	public void cacheAnimations() {
		for (WeaponTypes type : WeaponTypes.values()) {
			for (WeaponObject object : getWeaponsByType(type)) {
				object.cacheAnimation();
			}
		}
	}

	public boolean isPlayerShip(String blueprintName) {
		try {
			PlayerShipBlueprints.valueOf(blueprintName.replace("PLAYER_SHIP_", ""));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @param blueprint
	 *            blueprint name of a ship
	 * @return name of the file in which the given shipBlueprint should be saved
	 */
	public String getAssociatedFile(String blueprint) {
		if (shipFileMap.containsKey(blueprint))
			return shipFileMap.get(blueprint);
		else
			return "autoBlueprints.xml";
	}

	public AnimationObject getAnimation(String animName) {
		AnimationObject result = null;
		for (int i = dataEntries.size() - 1; i >= 0 && result == null; i--) {
			DatabaseEntry de = dataEntries.get(i);
			result = de.getAnimation(animName);
		}
		return result;
	}

	public AugmentObject getAugment(String blueprintName) {
		AugmentObject result = null;
		for (int i = dataEntries.size() - 1; i >= 0 && result == null; i--) {
			DatabaseEntry de = dataEntries.get(i);
			result = de.getAugment(blueprintName);
		}
		return result;
	}

	public ArrayList<AugmentObject> getAugments() {
		ArrayList<AugmentObject> result = new ArrayList<AugmentObject>();
		for (int i = dataEntries.size() - 1; i >= 0; i--) {
			DatabaseEntry de = dataEntries.get(i);
			for (AugmentObject o : de.getAugments())
				if (!result.contains(o))
					result.add(o);
		}
		return result;
	}

	public ArrayList<DroneList> getDroneLists() {
		ArrayList<DroneList> result = new ArrayList<DroneList>();
		for (int i = dataEntries.size() - 1; i >= 0; i--) {
			DatabaseEntry de = dataEntries.get(i);
			for (DroneList o : de.getDroneLists())
				if (!result.contains(o))
					result.add(o);
		}
		return result;
	}

	public DroneList getDroneList(String name) {
		DroneList result = null;
		for (int i = dataEntries.size() - 1; i >= 0 && result == null; i--) {
			DatabaseEntry de = dataEntries.get(i);
			result = de.getDroneList(name);
		}
		return result;

	}

	public DroneObject getDrone(String blueprint) {
		DroneObject result = null;
		for (int i = dataEntries.size() - 1; i >= 0 && result == null; i--) {
			DatabaseEntry de = dataEntries.get(i);
			result = de.getDrone(blueprint);
		}
		return result;
	}

	public ArrayList<DroneObject> getDronesByType(DroneTypes type) {
		ArrayList<DroneObject> result = new ArrayList<DroneObject>();
		for (int i = dataEntries.size() - 1; i >= 0; i--) {
			DatabaseEntry de = dataEntries.get(i);
			for (DroneObject o : de.getDronesByType(type))
				if (!result.contains(o))
					result.add(o);
		}
		return result;
	}

	public GlowObject getGlow(String id) {
		GlowObject result = null;
		for (int i = dataEntries.size() - 1; i >= 0 && result == null; i--) {
			DatabaseEntry de = dataEntries.get(i);
			result = de.getGlow(id);
		}
		return result;
	}

	public ArrayList<GlowObject> getGlows() {
		ArrayList<GlowObject> result = new ArrayList<GlowObject>();
		for (int i = dataEntries.size() - 1; i >= 0; i--) {
			DatabaseEntry de = dataEntries.get(i);
			for (GlowObject o : de.getGlows())
				if (!result.contains(o))
					result.add(o);
		}
		return result;
	}

	public GlowSet getGlowSet(String id) {
		GlowSet result = null;
		for (int i = dataEntries.size() - 1; i >= 0 && result == null; i--) {
			DatabaseEntry de = dataEntries.get(i);
			result = de.getGlowSet(id);
		}
		return result;
	}

	public ArrayList<GlowSet> getGlowSets() {
		ArrayList<GlowSet> result = new ArrayList<GlowSet>();
		for (int i = dataEntries.size() - 1; i >= 0; i--) {
			DatabaseEntry de = dataEntries.get(i);
			for (GlowSet o : de.getGlowSets())
				if (!result.contains(o))
					result.add(o);
		}
		return result;
	}

	public HashMap<String, ArrayList<ShipMetadata>> getShipMetadata() {
		HashMap<String, ArrayList<ShipMetadata>> map = new HashMap<String, ArrayList<ShipMetadata>>();
		for (DatabaseEntry de : dataEntries) {
			for (ShipMetadata metadata : de.getShipMetadata()) {
				ArrayList<ShipMetadata> list = map.get(metadata.getBlueprintName());
				if (list == null)
					list = new ArrayList<ShipMetadata>();
				list.add(metadata);
				map.put(metadata.getBlueprintName(), list);
			}
		}
		return map;
	}

	public ArrayList<WeaponList> getWeaponLists() {
		ArrayList<WeaponList> result = new ArrayList<WeaponList>();
		for (int i = dataEntries.size() - 1; i >= 0; i--) {
			DatabaseEntry de = dataEntries.get(i);
			for (WeaponList o : de.getWeaponLists())
				if (!result.contains(o))
					result.add(o);
		}
		return result;
	}

	public WeaponList getWeaponList(String name) {
		WeaponList result = null;
		for (int i = dataEntries.size() - 1; i >= 0 && result == null; i--) {
			DatabaseEntry de = dataEntries.get(i);
			result = de.getWeaponList(name);
		}
		return result;
	}

	public WeaponObject getWeapon(String blueprint) {
		WeaponObject result = null;
		for (int i = dataEntries.size() - 1; i >= 0 && result == null; i--) {
			DatabaseEntry de = dataEntries.get(i);
			result = de.getWeapon(blueprint);
		}
		return result;
	}

	public ArrayList<WeaponObject> getWeaponsByType(WeaponTypes type) {
		ArrayList<WeaponObject> result = new ArrayList<WeaponObject>();
		for (int i = dataEntries.size() - 1; i >= 0; i--) {
			DatabaseEntry de = dataEntries.get(i);
			for (WeaponObject o : de.getWeaponsByType(type))
				if (!result.contains(o))
					result.add(o);
		}
		return result;
	}

	/**
	 * @param innerPath
	 *            the sought innerPath
	 * @return true if any of the DatabaseEntries contains the innerPath, false otherwise
	 */
	public boolean contains(String innerPath) {
		boolean result = false;
		for (int i = dataEntries.size() - 1; i >= 0 && !result; i--) {
			DatabaseEntry de = dataEntries.get(i);
			result = de.contains(innerPath);
		}
		return result;
	}

	/**
	 * 
	 * @param innerPath
	 *            path to the sought resource
	 * @return InputStream for the sought resource, from the first DatabaseEntry that contains it (starting from last)
	 * 
	 * @throws FileNotFoundException
	 *             if the Database did not contain the specified innerPath
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public InputStream getInputStream(String innerPath) throws FileNotFoundException, IOException {
		InputStream is = null;
		for (int i = dataEntries.size() - 1; i >= 0 && is == null; i--) {
			DatabaseEntry de = dataEntries.get(i);
			if (de.contains(innerPath))
				is = de.getInputStream(innerPath);
		}
		if (is == null)
			throw new FileNotFoundException(String.format("Inner path '%s' was not found in the database.", innerPath));
		return is;
	}

	public List<String> listFiles(Predicate<String> filter) {
		List<String> result = new ArrayList<String>();

		for (int i = dataEntries.size() - 1; i >= 0; i--) {
			DatabaseEntry de = dataEntries.get(i);
			for (String innerPath : de.list()) {
				if (!result.contains(innerPath) && (filter == null || filter.accept(innerPath)))
					result.add(innerPath);
			}
		}

		return result;
	}
}
