package com.kartoflane.superluminal2.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import net.vhati.ftldat.FTLDat.FTLPack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.JDOMParseException;

import com.kartoflane.superluminal2.components.enums.DroneTypes;
import com.kartoflane.superluminal2.components.enums.WeaponTypes;
import com.kartoflane.superluminal2.components.interfaces.Predicate;
import com.kartoflane.superluminal2.ftl.AnimationObject;
import com.kartoflane.superluminal2.ftl.AugmentObject;
import com.kartoflane.superluminal2.ftl.BlueprintList;
import com.kartoflane.superluminal2.ftl.DroneList;
import com.kartoflane.superluminal2.ftl.DroneObject;
import com.kartoflane.superluminal2.ftl.GlowObject;
import com.kartoflane.superluminal2.ftl.GlowSet;
import com.kartoflane.superluminal2.ftl.GlowSet.Glows;
import com.kartoflane.superluminal2.ftl.ShipMetadata;
import com.kartoflane.superluminal2.ftl.WeaponList;
import com.kartoflane.superluminal2.ftl.WeaponObject;
import com.kartoflane.superluminal2.utils.DataUtils;
import com.kartoflane.superluminal2.utils.IOUtils;
import com.kartoflane.superluminal2.utils.IOUtils.DecodeResult;
import com.kartoflane.superluminal2.utils.Utils;

/**
 * A class representing a database entry that can be installed in the {@link com.kartoflane.superluminal2.core.Database Database} to modify its
 * contents.<br>
 * Database entries are basically the editor's representation of .ftl files.
 * 
 * @author kartoFlane
 *
 */
public class DatabaseEntry {

	private static final Logger log = LogManager.getLogger(DatabaseEntry.class);

	private final File file;
	private final ZipFile archive;
	private final FTLPack data;
	private final FTLPack resource;

	private ArrayList<ShipMetadata> shipMetadata = new ArrayList<ShipMetadata>();
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

	/**
	 * Creates a DatabaseEntry representing an installed mod.<br>
	 * The entry then has to be loaded using {@link #load()}
	 * 
	 * @param f
	 *            the .ftl or .zip file from which the data will be read
	 * @throws ZipException
	 *             when the file is not a zip archive
	 * @throws IOException
	 *             when an IO error occurs
	 */
	public DatabaseEntry(File f) throws ZipException, IOException {
		file = f;
		archive = new ZipFile(f);
		data = null;
		resource = null;
	}

	/**
	 * Creates the default DatabaseEntry, which serves as the core of the database.
	 * 
	 * @param data
	 *            the data.dat archive
	 * @param resource
	 *            the resource.dat archive
	 */
	public DatabaseEntry(FTLPack data, FTLPack resource) {
		file = new File("DatabaseCore");
		archive = null;
		this.data = data;
		this.resource = resource;
	}

	/**
	 * @return the name of the database entry
	 */
	public String getName() {
		return file == null ? "" : file.getName();
	}

	public File getFile() {
		return file;
	}

	/**
	 * @return true if the entry contains the innerPath, false otherwise
	 */
	public boolean contains(String innerPath) {
		if (innerPath == null)
			throw new IllegalArgumentException("Inner path must not be null.");

		if (archive == null) {
			boolean result = data.contains(innerPath);
			if (!result)
				result = resource.contains(innerPath);

			return result;
		} else
			return archive.getEntry(innerPath) != null;
	}

	/**
	 * @param innerPath
	 *            the inner path of the sought file
	 * @return the stream
	 * 
	 * @throws FileNotFoundException
	 *             when the inner path was not found in the entry
	 * @throws IOException
	 *             when an IO error occurs
	 */
	public InputStream getInputStream(String innerPath) throws FileNotFoundException, IOException {
		if (innerPath == null)
			throw new IllegalArgumentException("Inner path must not be null.");

		if (archive == null) {
			if (innerPath.endsWith(".txt") || innerPath.endsWith(".xml") ||
					innerPath.endsWith(".xml.append") || innerPath.endsWith(".append.xml"))
				return data.getInputStream(innerPath);
			else
				return resource.getInputStream(innerPath);
		} else {
			ZipEntry ze = archive.getEntry(innerPath);
			if (ze == null)
				throw new FileNotFoundException("Inner path not found: " + innerPath);
			return archive.getInputStream(ze);
		}
	}

	/**
	 * @return a list of all inner paths
	 */
	public ArrayList<String> list() {
		ArrayList<String> result = new ArrayList<String>();
		if (archive == null) {
			result.addAll(data.list());
			result.addAll(resource.list());
		} else {
			Enumeration<? extends ZipEntry> entries = archive.entries();
			while (entries.hasMoreElements()) {
				ZipEntry ze = entries.nextElement();
				if (!ze.isDirectory())
					result.add(ze.getName());
			}
		}
		return result;
	}

	/**
	 * Clears all data that was loaded and cached in this entry.
	 */
	public void clear() {
		shipMetadata.clear();
		animationObjects.clear();
		weaponObjects.clear();
		droneObjects.clear();
		augmentObjects.clear();
		glowObjects.clear();
		glowSets.clear();
		weaponLists.clear();
		droneLists.clear();
		animSheetMap.clear();
		System.gc();
	}

	/**
	 * Closes this entry and releases any system resources associated with the stream.
	 */
	public void close() throws IOException {
		if (archive == null) {
			data.close();
			resource.close();
		} else
			archive.close();
	}

	public void dispose() throws IOException {
		close();
		clear();
	}

	public void store(AnimationObject anim) {
		animationObjects.add(anim);
	}

	public void store(AugmentObject augment) {
		augmentObjects.add(augment);
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

	public void store(DroneObject drone) {
		droneObjects.add(drone);
	}

	public void store(GlowObject glow) {
		glowObjects.add(glow);
	}

	public void store(GlowSet set) {
		glowSets.add(set);
	}

	public void store(ShipMetadata metadata) {
		shipMetadata.add(metadata);
	}

	public void store(WeaponObject weapon) {
		weaponObjects.add(weapon);
	}

	/**
	 * @param animName
	 *            the animName of the sought animation (eg. laser_burst_1)
	 * @return
	 */
	public AnimationObject getAnimation(String animName) {
		AnimationObject[] anims = animationObjects.toArray(new AnimationObject[0]);
		try {
			return anims[Utils.binarySearch(anims, animName, 0, anims.length)];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * @param blueprint
	 *            the blueprint name of the sought augment
	 * @return the augment with the given blueprint, or null if not found
	 */
	public AugmentObject getAugment(String blueprint) {
		AugmentObject[] augments = getAugments();
		try {
			return augments[Utils.binarySearch(augments, blueprint, 0, augments.length)];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * @return an array of all augments in this entry
	 */
	public AugmentObject[] getAugments() {
		return augmentObjects.toArray(new AugmentObject[0]);
	}

	/**
	 * @return an array of all drone lists in this entry
	 */
	public DroneList[] getDroneLists() {
		return droneLists.toArray(new DroneList[0]);
	}

	/**
	 * @param the
	 *            name of the blueprint list
	 * @return the blueprint list with the given name
	 */
	public DroneList getDroneList(String name) {
		DroneList[] lists = getDroneLists();
		try {
			return lists[Utils.binarySearch(lists, name, 0, lists.length)];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * @param blueprint
	 *            the blueprint name of the sought drone
	 * @return the drone with the given blueprint name, or null if not found
	 */
	public DroneObject getDrone(String blueprint) {
		DroneObject[] drones = droneObjects.toArray(new DroneObject[0]);
		try {
			return drones[Utils.binarySearch(drones, blueprint, 0, drones.length)];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * @param type
	 *            the desired type of the drones
	 * @return a list containing all drones of the given type in this entry
	 */
	public ArrayList<DroneObject> getDronesByType(DroneTypes type) {
		ArrayList<DroneObject> typeDrones = new ArrayList<DroneObject>();
		for (DroneObject drone : droneObjects) {
			if (drone.getType() == type)
				typeDrones.add(drone);
		}

		return typeDrones;
	}

	/**
	 * @param id
	 *            the name of the glow object (eg. pilot_1)
	 * @return the glow object wit the given name, or null if not found
	 */
	public GlowObject getGlow(String id) {
		GlowObject[] glows = getGlows();
		try {
			return glows[Utils.binarySearch(glows, id, 0, glows.length)];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * @return an array of all glow objects in this entry
	 */
	public GlowObject[] getGlows() {
		return glowObjects.toArray(new GlowObject[0]);
	}

	/**
	 * @param id
	 *            the namespace of the glow image set (eg. computer1_glow)
	 * @return the glow image set with the given namespace, or null if not found
	 */
	public GlowSet getGlowSet(String id) {
		GlowSet[] glowSets = getGlowSets();
		try {
			return glowSets[Utils.binarySearch(glowSets, id, 0, glowSets.length)];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * @return an array of all glow sets in this entry
	 */
	public GlowSet[] getGlowSets() {
		return glowSets.toArray(new GlowSet[0]);
	}

	/**
	 * @return an array of all ships in this entry
	 */
	public ShipMetadata[] getShipMetadata() {
		return shipMetadata.toArray(new ShipMetadata[0]);
	}

	/**
	 * @return an array of all weapon lists in this entry
	 */
	public WeaponList[] getWeaponLists() {
		return weaponLists.toArray(new WeaponList[0]);
	}

	/**
	 * @param the
	 *            name of the blueprint list
	 * @return the blueprint list with the given name
	 */
	public WeaponList getWeaponList(String name) {
		WeaponList[] lists = getWeaponLists();
		try {
			return lists[Utils.binarySearch(lists, name, 0, lists.length)];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * @param blueprint
	 *            the blueprint name of the sought weapon
	 * @return the weapon with the given blueprint name, or null if not found
	 */
	public WeaponObject getWeapon(String blueprint) {
		WeaponObject[] weapons = weaponObjects.toArray(new WeaponObject[0]);
		try {
			return weapons[Utils.binarySearch(weapons, blueprint, 0, weapons.length)];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * @param type
	 *            the desired type of the weapons
	 * @return a list containing all weapons of the given type in this entry
	 */
	public ArrayList<WeaponObject> getWeaponsByType(WeaponTypes type) {
		ArrayList<WeaponObject> typeWeapons = new ArrayList<WeaponObject>();
		for (WeaponObject weapon : weaponObjects) {
			if (weapon.getType() == type)
				typeWeapons.add(weapon);
		}

		return typeWeapons;
	}

	/**
	 * Loads the contents of the database entry.<br>
	 * This method should not be called directly. Use {@link Database#addEntry(DatabaseEntry)} instead.
	 * 
	 * <pre>
	 * Loaded data:
	 *   - weapon anim, animSheets (only temporarily), weapon sprites
	 *   - ship blueprints
	 *   - weapon blueprints
	 *   - drone blueprints
	 *   - augment blueprints
	 *   - weapon and drone lists
	 *   - roomLayout tags (glow objects) from rooms.xml
	 *   - glow images (glow sets) in img/ship/interior, eg. pilot_glow1-3.png, etc
	 * </pre>
	 */
	public void load() {
		// Animations need to be loaded before weapons, since they reference them
		preloadAnims();
		loadGlowSets();

		String[] extensions = { ".xml", ".xml.append", ".append.xml" };
		InputStream is = null;

		String[] blueprintFiles = { "data/blueprints", "data/autoBlueprints",
				"data/dlcBlueprints", "data/dlcBlueprintsOverwrite" };

		for (String ext : extensions) {
			for (String innerPath : blueprintFiles) {
				try {
					is = getInputStream(innerPath + ext);
					DecodeResult dr = IOUtils.decodeText(is, null);

					ArrayList<Element> elements = DataUtils.findTagsNamed(dr.text, "shipBlueprint");
					for (Element e : elements) {
						try {
							store(DataUtils.loadShipMetadata(e));
						} catch (IllegalArgumentException ex) {
							log.warn(getName() + ": could not load ship metadata: " + ex.getMessage());
						}
					}

					elements.clear();
					elements = null;
					elements = DataUtils.findTagsNamed(dr.text, "weaponBlueprint");
					for (Element e : elements) {
						try {
							store(DataUtils.loadWeapon(e));
						} catch (IllegalArgumentException ex) {
							log.warn(getName() + ": could not load weapon: " + ex.getMessage());
						}
					}

					elements.clear();
					elements = null;
					elements = DataUtils.findTagsNamed(dr.text, "droneBlueprint");
					for (Element e : elements) {
						try {
							store(DataUtils.loadDrone(e));
						} catch (IllegalArgumentException ex) {
							log.warn(getName() + ": could not load drone: " + ex.getMessage());
						}
					}

					elements.clear();
					elements = null;
					elements = DataUtils.findTagsNamed(dr.text, "augBlueprint");
					for (Element e : elements) {
						try {
							store(DataUtils.loadAugment(e));
						} catch (IllegalArgumentException ex) {
							log.warn(getName() + ": could not load augment: " + ex.getMessage());
						}
					}
				} catch (FileNotFoundException e) {
					// Spammy and not very useful.
					// log.trace(String.format("Inner path '%s' could not be found.", innerPath + ext));
				} catch (IOException e) {
					log.error(getName() + ": an error has occured while loading file '" + innerPath + "':", e);
				} catch (JDOMParseException e) {
					log.error(getName() + ": an error has occured while parsing file '" + innerPath + "':", e);
				} finally {
					try {
						if (is != null)
							is.close();
					} catch (IOException e) {
					}
				}
			}
		}

		for (String ext : extensions) {
			// Lists reference their contents directly, so they have to be loaded in a separate loop
			for (String innerPath : blueprintFiles) {
				try {
					is = getInputStream(innerPath + ext);
					DecodeResult dr = IOUtils.decodeText(is, null);

					ArrayList<Element> elements = DataUtils.findTagsNamed(dr.text, "blueprintList");
					for (Element e : elements) {
						try {
							store(DataUtils.loadList(e));
						} catch (IllegalArgumentException ex) {
							log.warn(getName() + ": could not load blueprint list: " + ex.getMessage());
						}
					}

					elements.clear();
					elements = null;
				} catch (FileNotFoundException e) {
					// Spammy and not very useful.
					// log.trace(String.format("Inner path '%s' could not be found.", innerPath + ext));
				} catch (IOException e) {
					log.error(getName() + ": an error has occured while loading file '" + innerPath + "':", e);
				} catch (JDOMParseException e) {
					log.error(getName() + ": an error has occured while parsing file '" + innerPath + "':", e);
				} finally {
					try {
						if (is != null)
							is.close();
					} catch (IOException e) {
					}
				}
			}
		}

		// Scan rooms.xml alone
		for (String ext : extensions) {
			try {
				is = getInputStream("data/rooms" + ext);
				DecodeResult dr = IOUtils.decodeText(is, null);

				ArrayList<Element> elements = DataUtils.findTagsNamed(dr.text, "roomLayout");
				for (Element e : elements) {
					try {
						store(DataUtils.loadGlow(e));
					} catch (IllegalArgumentException ex) {
						log.warn(getName() + ": could not load glow object: " + ex.getMessage());
					}
				}

				elements.clear();
				elements = null;
			} catch (FileNotFoundException e) {
				// log.trace(String.format("Inner path '%s' could not be found.", "data/rooms" + ext));
			} catch (IOException e) {
				log.error(getName() + ": an error has occured while loading file 'data/rooms.xml':", e);
			} catch (JDOMParseException e) {
				log.error(getName() + ": an error has occured while parsing file 'data/rooms.xml':", e);
			} finally {
				try {
					if (is != null)
						is.close();
				} catch (IOException e) {
				}
			}
		}

		// Clear anim sheets, as they're no longer needed
		animSheetMap.clear();

		log.trace(getName() + " was loaded successfully.");
	}

	private void preloadAnims() {
		String[] extensions = { ".xml", ".xml.append", ".append.xml" };
		String[] animPaths = new String[] { "data/animations", "data/dlcAnimations" };

		for (String ext : extensions) {
			for (String innerPath : animPaths) {
				InputStream is = null;
				try {
					is = getInputStream(innerPath + ext);
					DecodeResult dr = IOUtils.decodeText(is, null);

					Document doc = null;
					doc = IOUtils.parseXML(dr.text);
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
							store(DataUtils.loadAnim(this, e));
						} catch (IllegalArgumentException ex) {
							log.warn(getName() + ": could not load animation: " + ex.getMessage());
						}
					}
				} catch (FileNotFoundException e) {
					// log.trace(String.format("Inner path '%s' could not be found.", innerPath + ext));
				} catch (IOException e) {
					log.error(getName() + ": an error has occured while loading file " + innerPath + ":", e);
				} catch (JDOMParseException e) {
					log.error(getName() + ": an error has occured while parsing file " + innerPath + ":", e);
				} finally {
					try {
						if (is != null)
							is.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	/**
	 * This method should not be used. It's been made public only to be
	 * accessible by {@link DataUtils#loadAnim(DatabaseEntry, Element)},
	 * and is used only during the preloading of anim sheets.
	 */
	public Element getAnimSheetElement(String anim) {
		return animSheetMap.get(anim);
	}

	private void loadGlowSets() {
		final Pattern glowPtrn = Pattern.compile("[0-9]\\.png");

		Predicate<String> filter = new Predicate<String>() {
			@Override
			public boolean accept(String path) {
				return path.contains("img/ship/interior/") &&
						(glowPtrn.matcher(path).find() || path.endsWith("_glow.png"));
			}
		};

		TreeSet<String> eligiblePaths = new TreeSet<String>();
		for (String path : list()) {
			if (filter.accept(path))
				eligiblePaths.add(path);
		}

		for (String s1 : eligiblePaths) {
			if (s1.endsWith("_glow.png")) {
				String namespace = s1.replaceAll("_glow.png", "");
				namespace = namespace.replace("img/ship/interior/", "");
				GlowSet set = new GlowSet(namespace);
				set.setImage(Glows.CLOAK, "db:" + s1);
				glowSets.add(set);
			} else if (s1.endsWith("1.png")) {
				String namespace = s1.replaceAll("[0-9]\\.png", "");
				String s2 = find(eligiblePaths, namespace + "2.png");
				String s3 = find(eligiblePaths, namespace + "3.png");

				if (s1 != null && s2 != null && s3 != null) {
					namespace = namespace.replace("img/ship/interior/", "");
					GlowSet set = new GlowSet(namespace);
					set.setImage(Glows.BLUE, "db:" + s1);
					set.setImage(Glows.GREEN, "db:" + s2);
					set.setImage(Glows.YELLOW, "db:" + s3);
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

	@Override
	public int hashCode() {
		return file.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof DatabaseEntry) {
			DatabaseEntry other = (DatabaseEntry) o;
			return file.equals(other.file);
		} else {
			return super.equals(o);
		}
	}

	@Override
	public String toString() {
		return getName();
	}
}
