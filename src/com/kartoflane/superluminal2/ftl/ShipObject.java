package com.kartoflane.superluminal2.ftl;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.TreeSet;

import com.kartoflane.superluminal2.ftl.SystemObject.Systems;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;

public class ShipObject extends GameObject {

	public enum Images {
		HULL, FLOOR, CLOAK, SHIELD, THUMBNAIL;
	}

	private static final long serialVersionUID = 5820228601368854867L;

	private boolean isPlayer = false;

	private TreeSet<RoomObject> rooms;
	private HashSet<DoorObject> doors;
	private TreeSet<MountObject> mounts;
	private HashSet<GibObject> gibs;
	private LinkedHashMap<Systems, SystemObject> systemMap;

	private String hullPath = null;
	private String floorPath = null;
	private String cloakPath = null;
	private String shieldPath = null;
	private String thumbnailPath = null;

	private ShipObject() {
		setDeletable(false);

		rooms = new TreeSet<RoomObject>();
		doors = new HashSet<DoorObject>();
		mounts = new TreeSet<MountObject>();
		gibs = new HashSet<GibObject>();
		systemMap = new LinkedHashMap<Systems, SystemObject>();

		for (Systems system : Systems.values()) {
			systemMap.put(system, new SystemObject(system));
		}
	}

	public ShipObject(boolean isPlayer) {
		this();

		this.isPlayer = isPlayer;
	}

	public boolean isPlayerShip() {
		return isPlayer;
	}

	public SystemObject getSystem(Systems sys) {
		return systemMap.get(sys);
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

	/**
	 * Adds the game object to the ship, storing it in the appropriate list
	 * depending on its type.<br>
	 * <br>
	 * This method should not be called directly.
	 * Use {@link com.kartoflane.superluminal2.ui.ShipContainer#add(RoomController) ShipContainer.add()} instead
	 * 
	 * @param object
	 *            object that is to be added
	 */
	public void add(GameObject object) {
		if (object instanceof RoomObject)
			rooms.add((RoomObject) object);
		else if (object instanceof DoorObject)
			doors.add((DoorObject) object);
		else if (object instanceof MountObject)
			mounts.add((MountObject) object);
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
			rooms.remove((RoomObject) object);
		else if (object instanceof DoorObject)
			doors.remove((DoorObject) object);
		else if (object instanceof MountObject)
			mounts.remove((MountObject) object);
		else
			throw new IllegalArgumentException("Game object was of unexpected type: " + object.getClass().getSimpleName());
	}

	public void setImagePath(String path, Images type) {
		switch (type) {
			case HULL:
				hullPath = path;
				break;
			case FLOOR:
				floorPath = path;
				break;
			case CLOAK:
				cloakPath = path;
				break;
			case SHIELD:
				shieldPath = path;
				break;
			case THUMBNAIL:
				thumbnailPath = path;
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	public String getImagePath(Images type) {
		switch (type) {
			case HULL:
				return hullPath;
			case FLOOR:
				return floorPath;
			case CLOAK:
				return cloakPath;
			case SHIELD:
				return shieldPath;
			case THUMBNAIL:
				return thumbnailPath;
			default:
				throw new IllegalArgumentException();
		}
	}
}
