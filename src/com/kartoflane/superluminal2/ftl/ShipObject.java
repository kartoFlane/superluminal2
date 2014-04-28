package com.kartoflane.superluminal2.ftl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal2.components.Images;
import com.kartoflane.superluminal2.components.Races;
import com.kartoflane.superluminal2.components.Systems;
import com.kartoflane.superluminal2.core.Database.PlayerShipBlueprints;
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
	private ArrayList<DroneObject> drones;
	private ArrayList<AugmentObject> augments;
	private HashMap<Systems, SystemObject> systemMap;
	private HashMap<Images, ImageObject> imageMap;
	private HashMap<Races, Integer> crewCountMap;
	private HashMap<Races, Integer> crewMaxMap;

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
		drones = new ArrayList<DroneObject>();
		systemMap = new HashMap<Systems, SystemObject>();
		imageMap = new HashMap<Images, ImageObject>();
		augments = new ArrayList<AugmentObject>();
		crewCountMap = new HashMap<Races, Integer>();
		crewMaxMap = new HashMap<Races, Integer>();

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

	public void setLayout(String layout) {
		if (layout == null)
			throw new IllegalArgumentException("Layout namespace must not be null.");
		this.layout = layout;
	}

	public String getLayout() {
		return layout;
	}

	public String getLayoutTXT() {
		return "data/" + layout + ".txt";
	}

	public String getLayoutXML() {
		return "data/" + layout + ".xml";
	}

	public void setImageNamespace(String image) {
		if (image == null)
			throw new IllegalArgumentException("Image namespace must not be null.");
		img = image;
	}

	public String getImageNamespace() {
		return img;
	}

	public void setShipClass(String className) {
		if (className == null)
			throw new IllegalArgumentException("The ship class' name must not be null.");
		shipClass = className;
	}

	public String getShipClass() {
		return shipClass;
	}

	public void setShipName(String shipName) {
		if (shipName == null)
			throw new IllegalArgumentException("The ship's name must not be null.");
		this.shipName = shipName;
	}

	public String getShipName() {
		return shipName;
	}

	public void setShipDescription(String desc) {
		if (desc == null)
			throw new IllegalArgumentException("Description must not be null.");
		shipDescription = desc;
	}

	public String getShipDescription() {
		return shipDescription;
	}

	public void setXOffset(int i) {
		xOffset = i;
	}

	public int getXOffset() {
		return xOffset;
	}

	public void setYOffset(int i) {
		yOffset = i;
	}

	public int getYOffset() {
		return yOffset;
	}

	public void setHorizontal(int i) {
		horizontal = i;
	}

	public int getHorizontal() {
		return horizontal;
	}

	public void setVertical(int i) {
		vertical = i;
	}

	public int getVertical() {
		return vertical;
	}

	public void setEllipse(Rectangle rect) {
		if (rect == null)
			throw new IllegalArgumentException("Ellipse must not be null");
		setEllipse(rect.x, rect.y, rect.width, rect.height);
	}

	public void setEllipse(int xOff, int yOff, int width, int height) {
		ellipse.x = xOff;
		ellipse.y = yOff;
		ellipse.width = width;
		ellipse.height = height;
	}

	public Rectangle getEllipse() {
		return Utils.copy(ellipse);
	}

	public void setHullDimensions(Rectangle rect) {
		if (rect == null)
			throw new IllegalArgumentException("Hull dimensions must not be null");
		setHullDimensions(rect.x, rect.y, rect.width, rect.height);
	}

	public void setHullDimensions(int x, int y, int w, int h) {
		hullOffset.x = x;
		hullOffset.y = y;
		hullSize.x = w;
		hullSize.y = h;
	}

	public Rectangle getHullDimensions() {
		return new Rectangle(hullOffset.x, hullOffset.y, hullSize.x, hullSize.y);
	}

	public Point getHullOffset() {
		return Utils.copy(hullOffset);
	}

	public Point getHullSize() {
		return Utils.copy(hullSize);
	}

	public void setCloakOffset(Point p) {
		setCloakOffset(p.x, p.y);
	}

	public void setCloakOffset(int x, int y) {
		cloakOffset.x = x;
		cloakOffset.y = y;
	}

	public Point getCloakOffset() {
		return Utils.copy(cloakOffset);
	}

	public void setFloorOffset(Point p) {
		setFloorOffset(p.x, p.y);
	}

	public void setFloorOffset(int x, int y) {
		floorOffset.x = x;
		floorOffset.y = y;
	}

	public Point getFloorOffset() {
		return Utils.copy(floorOffset);
	}

	public SystemObject getSystem(Systems sys) {
		return systemMap.get(sys);
	}

	public ImageObject getImage(Images image) {
		return imageMap.get(image);
	}

	public void setImage(Images image, String path) {
		imageMap.get(image).setImagePath(path);
	}

	public void setHealth(int hp) {
		hullHealth = hp;
	}

	public int getHealth() {
		return hullHealth;
	}

	public void setPower(int power) {
		maxPower = power;
	}

	public int getPower() {
		return maxPower;
	}

	public void setWeaponSlots(int slots) {
		weaponSlots = slots;
	}

	public int getWeaponSlots() {
		return weaponSlots;
	}

	public void setDroneSlots(int slots) {
		droneSlots = slots;
	}

	public int getDroneSlots() {
		return droneSlots;
	}

	public void setMissilesAmount(int amount) {
		missiles = amount;
	}

	public int getMissilesAmount() {
		return missiles;
	}

	public void setDronePartsAmount(int amount) {
		droneParts = amount;
	}

	public int getDronePartsAmount() {
		return droneParts;
	}

	public void setMinSector(int min) {
		minSector = min;
	}

	public int getMinSector() {
		return minSector;
	}

	public void setMaxSector(int max) {
		maxSector = max;
	}

	public int getMaxSector() {
		return maxSector;
	}

	public void setCrewCount(Races race, int amount) {
		crewCountMap.put(race, amount);
	}

	public int getCrewCount(Races race) {
		return crewCountMap.get(race);
	}

	public void setCrewMax(Races race, int amount) {
		crewMaxMap.put(race, amount);
	}

	public int getCrewMax(Races race) {
		return crewMaxMap.get(race);
	}

	public AugmentObject[] getAugments() {
		return augments.toArray(new AugmentObject[0]);
	}

	public RoomObject[] getRooms() {
		return rooms.toArray(new RoomObject[0]);
	}

	public DoorObject[] getDoors() {
		return doors.toArray(new DoorObject[0]);
	}

	public MountObject[] getMounts() {
		return mounts.toArray(new MountObject[0]);
	}

	public GibObject[] getGibs() {
		return gibs.toArray(new GibObject[0]);
	}

	public RoomObject getRoomById(int id) {
		RoomObject[] roomz = getRooms();
		try {
			return roomz[binarySearch(roomz, id, 0, roomz.length)];
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public MountObject getMountById(int id) {
		for (MountObject mount : mounts) {
			if (mount.getId() == id)
				return mount;
		}
		return null;
	}

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
		if (object instanceof RoomObject)
			rooms.add((RoomObject) object);
		else if (object instanceof DoorObject)
			doors.add((DoorObject) object);
		else if (object instanceof MountObject) {
			mounts.add((MountObject) object);
		} else if (object instanceof GibObject)
			gibs.add((GibObject) object);
		else if (object instanceof DroneObject)
			drones.add((DroneObject) object);
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
		else if (object instanceof DroneObject)
			drones.remove(object);
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

	public int getNextRoomId() {
		int id = 0;
		for (RoomObject object : rooms) {
			if (id == object.getId())
				id++;
		}
		return id;
	}

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