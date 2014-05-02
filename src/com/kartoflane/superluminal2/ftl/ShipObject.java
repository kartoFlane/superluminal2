package com.kartoflane.superluminal2.ftl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal2.components.enums.Directions;
import com.kartoflane.superluminal2.components.enums.Images;
import com.kartoflane.superluminal2.components.enums.PlayerShipBlueprints;
import com.kartoflane.superluminal2.components.enums.Races;
import com.kartoflane.superluminal2.components.enums.Systems;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.core.Utils;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;

public class ShipObject extends GameObject {

	private static final long serialVersionUID = 5820228601368854867L;

	private boolean isPlayer = false;
	private String blueprintName = PlayerShipBlueprints.HARD.toString();
	private String layout = "layout";
	private String img = "myship";

	private String shipClass = "Ship Class";
	private String shipName = "The Nameless One";
	private String shipDescription = "This ship is completely devoid of any description whatsoever!";

	private TreeSet<RoomObject> rooms;
	private HashSet<DoorObject> doors;
	private TreeSet<MountObject> mounts;
	private HashSet<GibObject> gibs;

	private ArrayList<AugmentObject> augments;
	private HashMap<Systems, SystemObject> systemMap;
	private HashMap<Images, ImageObject> imageMap;
	private HashMap<Races, Integer> crewCountMap;
	private HashMap<Races, Integer> crewMaxMap;

	private ArrayList<WeaponObject> weapons;
	private ArrayList<DroneObject> drones;
	private WeaponList weaponList = Database.DEFAULT_WEAPON_LIST;
	private DroneList droneList = Database.DEFAULT_DRONE_LIST;

	private int xOffset = 0;
	private int yOffset = 0;
	private int horizontal = 0;
	private int vertical = 0;
	private Rectangle ellipse = new Rectangle(0, 0, 0, 0);

	private Point hullOffset = new Point(0, 0);
	private Point hullSize = new Point(0, 0);
	private Point cloakOffset = new Point(0, 0);
	private Point floorOffset = new Point(0, 0);

	private int weaponSlots = 0;
	private int droneSlots = 0;
	private int missiles = 0;
	private int droneParts = 0;
	private int hullHealth = 0;
	private int maxPower = 0;

	private int minSector = 1;
	private int maxSector = 8;

	private ShipObject() {
		setDeletable(false);

		rooms = new TreeSet<RoomObject>();
		doors = new HashSet<DoorObject>();
		mounts = new TreeSet<MountObject>();
		gibs = new HashSet<GibObject>();
		systemMap = new HashMap<Systems, SystemObject>();
		imageMap = new HashMap<Images, ImageObject>();
		augments = new ArrayList<AugmentObject>();
		crewCountMap = new HashMap<Races, Integer>();
		crewMaxMap = new HashMap<Races, Integer>();

		weapons = new ArrayList<WeaponObject>();
		drones = new ArrayList<DroneObject>();

		for (int i = 0; i <= 4; i++) {
			weapons.add(Database.DEFAULT_WEAPON_OBJ);
			drones.add(Database.DEFAULT_DRONE_OBJ);
		}

		for (Systems system : Systems.values())
			systemMap.put(system, new SystemObject(system));
		for (Images image : Images.values()) {
			ImageObject object = new ImageObject();
			object.setAlias(image.name().toLowerCase());
			imageMap.put(image, object);
		}
		for (Races race : Races.values()) {
			crewCountMap.put(race, 0);
			crewMaxMap.put(race, 0);
		}
	}

	public ShipObject(boolean isPlayer) {
		this();

		this.isPlayer = isPlayer;
	}

	public void update() {
	}

	public boolean isPlayerShip() {
		return isPlayer;
	}

	public void setBlueprintName(String name) {
		if (blueprintName == null)
			throw new IllegalArgumentException("Blueprint name must not be null.");
		blueprintName = name;
	}

	public String getBlueprintName() {
		return blueprintName;
	}

	/**
	 * Sets the ship layout's namespace ('layout' attribute)
	 */
	public void setLayout(String layout) {
		if (layout == null)
			throw new IllegalArgumentException("Layout namespace must not be null.");
		this.layout = layout;
	}

	/**
	 * @return ship layout's namespace ('layout' attribute)
	 */
	public String getLayout() {
		return layout;
	}

	/**
	 * @return dat-relative path to the ship's TXT layout file.
	 */
	public String getLayoutTXT() {
		return "data/" + layout + ".txt";
	}

	/**
	 * @return dat-relative path to the ship's XML layout file.
	 */
	public String getLayoutXML() {
		return "data/" + layout + ".xml";
	}

	/**
	 * Sets the image namespace of the ship ('img' attribute)
	 */
	public void setImageNamespace(String image) {
		if (image == null)
			throw new IllegalArgumentException("Image namespace must not be null.");
		img = image;
	}

	/**
	 * @return the image namespace of the ship ('img' attribute)
	 */
	public String getImageNamespace() {
		return img;
	}

	/**
	 * Sets the name of the ship's class, eg. "Kestrel Cruiser", "Auto-Scout"
	 */
	public void setShipClass(String className) {
		if (className == null)
			throw new IllegalArgumentException("The ship class' name must not be null.");
		shipClass = className;
	}

	/**
	 * @return name of the ship's class, eg. "Kestrel Cruiser", "Auto-Scout"
	 */
	public String getShipClass() {
		return shipClass;
	}

	/**
	 * Sets the name of the ship, eg. "The Torus", "Gila Monster", etc.<br>
	 * Player ships only.
	 */
	public void setShipName(String shipName) {
		if (shipName == null)
			throw new IllegalArgumentException("The ship's name must not be null.");
		this.shipName = shipName;
	}

	/**
	 * @return the name of the ship, eg. "The Torus", "Gila Monster", etc.
	 */
	public String getShipName() {
		return shipName;
	}

	/**
	 * Sets the ship's description.<br>
	 * Player ships only.
	 */
	public void setShipDescription(String desc) {
		if (desc == null)
			throw new IllegalArgumentException("Description must not be null.");
		shipDescription = desc;
	}

	/**
	 * @return the ship's description.
	 */
	public String getShipDescription() {
		return shipDescription;
	}

	/**
	 * Sets the X offset of the ship's origin, in grid cells.
	 */
	public void setXOffset(int i) {
		xOffset = i;
	}

	/**
	 * @return grid cell component of the ship origin's offset on the X axis (X_OFFSET property)
	 */
	public int getXOffset() {
		return xOffset;
	}

	/**
	 * Sets the Y offset of the ship's origin, in grid cells.
	 */
	public void setYOffset(int i) {
		yOffset = i;
	}

	/**
	 * @return grid cell component of the ship origin's offset on the Y axis (Y_OFFSET property)
	 */
	public int getYOffset() {
		return yOffset;
	}

	/**
	 * Sets the X offset of the ship's origin, in pixels.
	 */
	public void setHorizontal(int i) {
		horizontal = i;
	}

	/**
	 * @return pixel component of the ship origin's offset on the X axis (HORIZONTAL property)
	 */
	public int getHorizontal() {
		return horizontal;
	}

	/**
	 * Sets the Y offset of the ship's origin, in pixels.
	 */
	public void setVertical(int i) {
		vertical = i;
	}

	/**
	 * @return pixel component of the ship origin's offset on the Y axis (VERTICAL property)
	 */
	public int getVertical() {
		return vertical;
	}

	/**
	 * Sets the dimensions of the shield image.
	 * 
	 * @see {@link #setEllipse(int, int, int, int)}
	 */
	public void setEllipse(Rectangle rect) {
		if (rect == null)
			throw new IllegalArgumentException("Ellipse must not be null");
		setEllipse(rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * Sets the dimensions of the shield image.
	 * 
	 * @param xOff
	 *            offset from the ship's center
	 * @param yOff
	 *            offset from teh ship's center
	 * @param width
	 *            half of the image's width
	 * @param height
	 *            half of the image's height
	 */
	public void setEllipse(int xOff, int yOff, int width, int height) {
		ellipse.x = xOff;
		ellipse.y = yOff;
		ellipse.width = width;
		ellipse.height = height;
	}

	/**
	 * @return dimensions of the shield image.<br>
	 *         X and Y values represent offset from the ship's center (center of
	 *         smallest rectangle containing all rooms)<br>
	 *         Width and height are half of the actual image's size.
	 */
	public Rectangle getEllipse() {
		return Utils.copy(ellipse);
	}

	/**
	 * Sets the dimensions of the hull image.
	 * 
	 * @see {@link #setHullDimensions(int, int, int, int)}
	 */
	public void setHullDimensions(Rectangle rect) {
		if (rect == null)
			throw new IllegalArgumentException("Hull dimensions must not be null");
		setHullDimensions(rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * Sets the dimensions of the hull image.
	 * 
	 * @param x
	 *            offset from the ship's origin
	 * @param y
	 *            offset from the ship's origin
	 * @param w
	 *            width of the image
	 * @param h
	 *            height of the image
	 */
	public void setHullDimensions(int x, int y, int w, int h) {
		hullOffset.x = x;
		hullOffset.y = y;
		hullSize.x = w;
		hullSize.y = h;
	}

	/**
	 * @return dimensions of the hull image.<br>
	 *         X and Y values represent the hull's offset from the ship's origin.
	 */
	public Rectangle getHullDimensions() {
		return new Rectangle(hullOffset.x, hullOffset.y, hullSize.x, hullSize.y);
	}

	/**
	 * @return offset of the hull image from the ship's origin.
	 */
	public Point getHullOffset() {
		return Utils.copy(hullOffset);
	}

	public Point getHullSize() {
		return Utils.copy(hullSize);
	}

	/**
	 * Sets the offset of the cloak image from the top-left corner of the hull image.
	 */
	public void setCloakOffset(Point p) {
		setCloakOffset(p.x, p.y);
	}

	/**
	 * Sets the offset of the cloak image from the top-left corner of the hull image.
	 */
	public void setCloakOffset(int x, int y) {
		cloakOffset.x = x;
		cloakOffset.y = y;
	}

	/**
	 * @return the offset of the cloak image from the top-left corner of the hull image.
	 */
	public Point getCloakOffset() {
		return Utils.copy(cloakOffset);
	}

	/**
	 * Sets the offset of the floor image from the top-left corner of the hull image.
	 */
	public void setFloorOffset(Point p) {
		setFloorOffset(p.x, p.y);
	}

	/**
	 * Sets the offset of the floor image from the top-left corner of the hull image.
	 */
	public void setFloorOffset(int x, int y) {
		floorOffset.x = x;
		floorOffset.y = y;
	}

	/**
	 * @return the offset of the floor image from the top-left corner of the hull image.
	 */
	public Point getFloorOffset() {
		return Utils.copy(floorOffset);
	}

	/**
	 * @return system object associated with the given system type.
	 */
	public SystemObject getSystem(Systems sys) {
		if (sys == null)
			throw new IllegalArgumentException("System type must not be null.");
		return systemMap.get(sys);
	}

	/**
	 * @return image object associated with the given image type.
	 */
	public ImageObject getImage(Images image) {
		if (image == null)
			throw new IllegalArgumentException("Image type must not be null.");
		return imageMap.get(image);
	}

	/**
	 * Sets the image used by the given image type.
	 */
	public void setImage(Images image, String path) {
		if (image == null)
			throw new IllegalArgumentException("Image type must not be null.");
		imageMap.get(image).setImagePath(path);
	}

	public void setHealth(int hp) {
		if (hp < 0)
			throw new IllegalArgumentException("Amount of health points must be non-negative.");
		hullHealth = hp;
	}

	public int getHealth() {
		return hullHealth;
	}

	/**
	 * Sets the amount of power bars available to the ship at the start of the game.
	 */
	public void setPower(int power) {
		if (power < 0)
			throw new IllegalArgumentException("Power must be non-negative.");
		maxPower = power;
	}

	/**
	 * @return the amount of power bars available to the ship at the start of the game.
	 */
	public int getPower() {
		return maxPower;
	}

	/**
	 * Sets the number of weapon slots that the ship has.<br>
	 * This determines how many weapons the ship can have active at once.
	 */
	public void setWeaponSlots(int slots) {
		if (slots < 0)
			throw new IllegalArgumentException("Number of slots must be non-negative.");
		weaponSlots = slots;
		if (weapons.size() > slots) {
			for (int i = slots; i < weapons.size(); i++)
				weapons.remove(i);
		} else if (weapons.size() < slots) {
			for (int i = weapons.size(); i <= slots; i++)
				weapons.add(Database.DEFAULT_WEAPON_OBJ);
		}
	}

	/**
	 * @return the number of weapon slots that the ship has
	 */
	public int getWeaponSlots() {
		return weaponSlots;
	}

	/**
	 * Sets the weapon list that the ship will use as its loadout.<br>
	 * Enemy ships only.
	 */
	public void setWeaponList(WeaponList list) {
		if (isPlayer)
			throw new IllegalStateException("Not an enemy ship.");
		if (list == null)
			throw new IllegalArgumentException("List must not be null.");
		weaponList = list;
	}

	/**
	 * @return the weapon list that the ship uses as its loadout
	 */
	public WeaponList getWeaponList() {
		return weaponList;
	}

	public WeaponObject[] getWeapons() {
		return weapons.toArray(new WeaponObject[0]);
	}

	/**
	 * Puts the new weapon at the specified index in the weapon list.
	 */
	public void changeWeapon(int index, WeaponObject neu) {
		if (index < 0 || index > weaponSlots)
			throw new IllegalArgumentException("Index is out of bounds: " + index);
		if (neu == null)
			throw new IllegalArgumentException("New weapon must not be null.");
		weapons.set(index, neu);
		coalesceWeapons();
	}

	/**
	 * Removes the first occurence of the old weapon, and puts the new weapon in its place.
	 * 
	 * @return index at which the new weapon was placed
	 */
	public int changeWeapon(WeaponObject old, WeaponObject neu) {
		if (old == null)
			throw new IllegalArgumentException("Old weapon must not be null.");
		if (neu == null)
			throw new IllegalArgumentException("New weapon must not be null.");

		int i = weapons.indexOf(old);
		if (i == -1)
			throw new IllegalArgumentException("Old weapon not found.");
		weapons.set(i, neu);
		return i;
	}

	/**
	 * Coalesces the weapons, moving all dummy weapons to the end of the list, so that
	 * there are no gaps between 'real' weapons.
	 */
	private void coalesceWeapons() {
		for (int i = 0; i < weapons.size(); i++) {
			WeaponObject weapon = weapons.get(i);
			if (weapon == Database.DEFAULT_WEAPON_OBJ) {
				weapons.remove(weapon);
				weapons.add(weapon);
			}
		}
	}

	/**
	 * Sets the number of drone slots that the ship has.<br>
	 * This determines how many drones the ship can have active at once.
	 */
	public void setDroneSlots(int slots) {
		if (slots < 0)
			throw new IllegalArgumentException("Number of slots must be non-negative.");
		droneSlots = slots;
	}

	/**
	 * @return the number of drone slots that the ship has
	 */
	public int getDroneSlots() {
		return droneSlots;
	}

	/**
	 * Sets the drone list that the ship will use as its loadout.<br>
	 * Enemy ships only.
	 */
	public void setDroneList(DroneList list) {
		if (isPlayer)
			throw new IllegalStateException("Not an enemy ship.");
		if (list == null)
			throw new IllegalArgumentException("List must not be null.");
		droneList = list;
	}

	/**
	 * @return the drone list that the ship uses as its loadout
	 */
	public DroneList getDroneList() {
		return droneList;
	}

	public DroneObject[] getDrones() {
		return drones.toArray(new DroneObject[0]);
	}

	/**
	 * Puts the new drone at the specified index in the drone list.
	 */
	public void changeDrone(int index, DroneObject neu) {
		if (index < 0 || index > weaponSlots)
			throw new IllegalArgumentException("Index is out of bounds: " + index);
		if (neu == null)
			throw new IllegalArgumentException("New drone must not be null.");
		drones.set(index, neu);
		coalesceDrones();
	}

	/**
	 * Removes the first occurence of the old drone, and puts the new drone in its place.
	 * 
	 * @return index at which the new drone was placed
	 */
	public int changeDrone(DroneObject old, DroneObject neu) {
		if (old == null)
			throw new IllegalArgumentException("Old drone must not be null.");
		if (neu == null)
			throw new IllegalArgumentException("New drone must not be null.");

		int i = drones.indexOf(old);
		if (i == -1)
			throw new IllegalArgumentException("Old drone not found.");
		drones.set(i, neu);
		return i;
	}

	/**
	 * Coalesces drones, moving all dummy drones to the end of the list, so that
	 * there are no gaps between 'real' drones.
	 */
	private void coalesceDrones() {
		for (int i = 0; i < drones.size(); i++) {
			DroneObject drone = drones.get(i);
			if (drone == Database.DEFAULT_DRONE_OBJ) {
				drones.remove(drone);
				drones.add(drone);
			}
		}
	}

	public void setMissilesAmount(int amount) {
		if (amount < 0)
			throw new IllegalArgumentException("Amount must be non-negative.");
		missiles = amount;
	}

	public int getMissilesAmount() {
		return missiles;
	}

	public void setDronePartsAmount(int amount) {
		if (amount < 0)
			throw new IllegalArgumentException("Amount must be non-negative.");
		droneParts = amount;
	}

	public int getDronePartsAmount() {
		return droneParts;
	}

	/**
	 * Sets the minimum sector in which the ship can appear, inclusive (?)
	 */
	public void setMinSector(int min) {
		if (min < 1 || min > 8)
			throw new IllegalArgumentException("Sector number must be within 1..8 range, inclusive.");
		minSector = min;
	}

	/**
	 * @return the minimum sector in which the ship can appear, inclusive (?)
	 */
	public int getMinSector() {
		return minSector;
	}

	/**
	 * Sets the maximum sector in which the ship can appear, inclusive (?)
	 */
	public void setMaxSector(int max) {
		if (max < 1 || max > 8)
			throw new IllegalArgumentException("Sector number must be within 1..8 range, inclusive.");
		maxSector = max;
	}

	/**
	 * @return the maximum sector in which the ship can appear, inclusive (?)
	 */
	public int getMaxSector() {
		return maxSector;
	}

	/**
	 * If player ship - the amount of crew members of the given race that the ship starts with<br>
	 * If enemy ship - the minimum amount of crew members of the given race that the ship can have
	 */
	public void setCrewCount(Races race, int amount) {
		if (race == null)
			throw new IllegalArgumentException("Race must not be null.");
		if (amount < 0)
			throw new IllegalArgumentException("Amount must be non-negative.");
		crewCountMap.put(race, amount);
	}

	/**
	 * @return if player ship -
	 *         the amount of crew members of the given race that the ship starts with<br>
	 *         if enemy ship -
	 *         the minimum amount of crew members of the given race that the ship can have.
	 */
	public int getCrewCount(Races race) {
		if (race == null)
			throw new IllegalArgumentException("Race must not be null.");
		return crewCountMap.get(race);
	}

	/**
	 * Sets the maximum amount of crew members of the given race that the ship can have.<br>
	 * Enemy ships only.
	 */
	public void setCrewMax(Races race, int amount) {
		if (race == null)
			throw new IllegalArgumentException("Race must not be null.");
		if (amount < 0)
			throw new IllegalArgumentException("Amount must be non-negative.");
		crewMaxMap.put(race, amount);
	}

	/**
	 * Enemy ships only.
	 * 
	 * @return maximum amount of crew members of the given race that the ship can have
	 */
	public int getCrewMax(Races race) {
		if (race == null)
			throw new IllegalArgumentException("Race must not be null.");
		return crewMaxMap.get(race);
	}

	/**
	 * Modifying the array doesn't change the order of elements in the ship.
	 * 
	 * @return an array of all augments in this ship
	 */
	public AugmentObject[] getAugments() {
		return augments.toArray(new AugmentObject[0]);
	}

	/**
	 * Modifying the array doesn't change the order of elements in the ship.
	 * 
	 * @return an array of all rooms in this ship
	 */
	public RoomObject[] getRooms() {
		return rooms.toArray(new RoomObject[0]);
	}

	/**
	 * Modifying the array doesn't change the order of elements in the ship.
	 * 
	 * @return an array of all doors in this ship
	 */
	public DoorObject[] getDoors() {
		return doors.toArray(new DoorObject[0]);
	}

	/**
	 * Modifying the array doesn't change the order of elements in the ship.
	 * 
	 * @return an array of all mounts in this ship
	 */
	public MountObject[] getMounts() {
		return mounts.toArray(new MountObject[0]);
	}

	/**
	 * Modifying the array doesn't change the order of elements in the ship.
	 * 
	 * @return an array of all gibs in this ship
	 */
	public GibObject[] getGibs() {
		return gibs.toArray(new GibObject[0]);
	}

	/**
	 * @param id
	 *            ID number of the sought room
	 * @return room with the given ID, or null if none was found
	 */
	public RoomObject getRoomById(int id) {
		RoomObject[] roomz = getRooms();
		try {
			return roomz[binarySearch(roomz, id, 0, roomz.length)];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * @param id
	 *            ID number of the sought mount
	 * @return mount with the given ID, or null if none was found
	 */
	public MountObject getMountById(int id) {
		for (MountObject mount : mounts) {
			if (mount.getId() == id)
				return mount;
		}
		return null;
	}

	/**
	 * @param id
	 *            ID number of the sought gib
	 * @return gib with the given ID, or null if none was found
	 */
	public GibObject getGibById(int id) {
		for (GibObject gib : gibs) {
			if (gib.getId() == id)
				return gib;
		}
		return null;
	}

	/**
	 * Adds the game object to the ship, storing it in the appropriate list
	 * depending on its type.<br>
	 * <br>
	 * This method should not be called directly.
	 * Use {@link com.kartoflane.superluminal2.ui.ShipContainer#add(AbstractController) ShipContainer.add()} instead
	 * 
	 * @param object
	 *            object that is to be added
	 */
	public void add(GameObject object) {
		if (object == null)
			throw new IllegalArgumentException("Object must not be null.");

		if (object instanceof RoomObject)
			rooms.add((RoomObject) object);
		else if (object instanceof DoorObject)
			doors.add((DoorObject) object);
		else if (object instanceof MountObject)
			mounts.add((MountObject) object);
		else if (object instanceof GibObject)
			gibs.add((GibObject) object);
		else if (object instanceof AugmentObject)
			augments.add((AugmentObject) object);
		else
			throw new IllegalArgumentException("Game object was of unexpected type: " + object.getClass().getSimpleName());
	}

	/**
	 * Removes the game object from the ship.<br>
	 * <br>
	 * This method should not be called directly.
	 * Use {@link com.kartoflane.superluminal2.ui.ShipContainer#remove(RoomController) ShipContainer.remove()} instead
	 * 
	 * @param object
	 *            object that is to be removed
	 */
	public void remove(GameObject object) {
		if (object instanceof RoomObject)
			rooms.remove(object);
		else if (object instanceof DoorObject)
			doors.remove(object);
		else if (object instanceof MountObject)
			mounts.remove(object);
		else if (object instanceof GibObject)
			gibs.remove(object);
		else if (object instanceof AugmentObject)
			augments.remove(object);
		else
			throw new IllegalArgumentException("Game object was of unexpected type: " + object.getClass().getSimpleName());
	}

	/**
	 * Coalesces the rooms, removing gaps in room numbering.
	 */
	public void coalesceRooms() {
		int id = 0;
		RoomObject[] roomArray = getRooms();
		rooms.clear();
		for (RoomObject room : roomArray) {
			room.setId(id++);
			rooms.add(room);
		}
	}

	/**
	 * Automatically links doors to adjacent rooms.
	 */
	public void linkDoors() {
		for (DoorObject door : doors) {
			door.verifyLinks();

			if (door.isHorizontal()) {
				if (door.getLeftRoom() == null)
					door.setLeftRoom(getRoomAt(door.getX(), door.getY() - 1));
				if (door.getRightRoom() == null)
					door.setRightRoom(getRoomAt(door.getX(), door.getY()));
			} else {
				if (door.getLeftRoom() == null)
					door.setLeftRoom(getRoomAt(door.getX() - 1, door.getY()));
				if (door.getRightRoom() == null)
					door.setRightRoom(getRoomAt(door.getX(), door.getY()));
			}
		}
	}

	private RoomObject getRoomAt(int x, int y) {
		for (RoomObject room : rooms) {
			if (x >= room.getX() && y >= room.getY() &&
					x <= room.getX() + room.getW() && y <= room.getY() + room.getH())
				return room;
		}

		return null;
	}

	/**
	 * Iterates over all systems, checking if there already exists a glow object
	 * for their interior image's namespace.<br>
	 * If no such glow object is found, and the room/station combination needs one,
	 * this function creates a new glow object for this system.
	 * 
	 * @return an array of all created glows
	 */
	public GlowObject[] createGlows() {
		ArrayList<GlowObject> glows = new ArrayList<GlowObject>();

		for (Systems sys : systemMap.keySet()) {
			SystemObject system = systemMap.get(sys);
			String namespace = system.getInteriorNamespace();
			if (sys.canContainStation() && sys.canContainGlow() &&
					namespace != null && system.getGlowSet() == Database.DEFAULT_GLOW_SET) {
				RoomObject room = system.getRoom();
				StationObject station = system.getStation();

				namespace = namespace.replace("room_", "");
				GlowObject glow = Database.getInstance().getGlow(namespace);

				if (glow == null && station.getSlotId() != -2 && station.getSlotDirection() != Directions.NONE) {
					glow = new GlowObject(namespace);
					glow.setData(room, station);
					glow.setGlowSet(system.getGlowSet());
					glows.add(glow);
				} else if (glow != null && glow.getGlowSet() != system.getGlowSet()) {
					glow.setGlowSet(system.getGlowSet());
					glows.add(glow);
				}
			}
		}

		return glows.toArray(new GlowObject[0]);
	}

	/**
	 * @return the next unused ID that can be assigned to a new room
	 */
	public int getNextRoomId() {
		int id = 0;
		for (RoomObject object : rooms) {
			if (id == object.getId())
				id++;
		}
		return id;
	}

	/**
	 * @return the next unused ID that can be assigned to a new mount
	 */
	public int getNextMountId() {
		int id = 0;
		for (MountObject object : mounts) {
			if (id == object.getId())
				id++;
		}
		return id;
	}

	private static int binarySearch(RoomObject[] array, int id, int min, int max) {
		if (min > max)
			return -1;
		int mid = (min + max) / 2;
		if (array[mid].getId() < id)
			return binarySearch(array, id, mid + 1, max);
		else if (array[mid].getId() > id)
			return binarySearch(array, id, min, mid - 1);
		else
			return mid;
	}
}
