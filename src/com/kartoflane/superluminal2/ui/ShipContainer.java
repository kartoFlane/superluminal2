package com.kartoflane.superluminal2.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.components.Images;
import com.kartoflane.superluminal2.components.NotDeletableException;
import com.kartoflane.superluminal2.components.interfaces.Disposable;
import com.kartoflane.superluminal2.ftl.DoorObject;
import com.kartoflane.superluminal2.ftl.GameObject;
import com.kartoflane.superluminal2.ftl.GibObject;
import com.kartoflane.superluminal2.ftl.ImageObject;
import com.kartoflane.superluminal2.ftl.MountObject;
import com.kartoflane.superluminal2.ftl.RoomObject;
import com.kartoflane.superluminal2.ftl.ShipObject;
import com.kartoflane.superluminal2.ftl.SystemObject;
import com.kartoflane.superluminal2.ftl.SystemObject.Systems;
import com.kartoflane.superluminal2.ftl.WeaponObject;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.DoorController;
import com.kartoflane.superluminal2.mvc.controllers.GibController;
import com.kartoflane.superluminal2.mvc.controllers.ImageController;
import com.kartoflane.superluminal2.mvc.controllers.MountController;
import com.kartoflane.superluminal2.mvc.controllers.ObjectController;
import com.kartoflane.superluminal2.mvc.controllers.PropController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;
import com.kartoflane.superluminal2.mvc.controllers.StationController;
import com.kartoflane.superluminal2.mvc.controllers.SystemController;

/**
 * A simple class serving as a holder and communication layer between different controllers.
 * A meta-controller, so to say, or a semi-GUI widget.
 * 
 * @author kartoFlane
 * 
 */
public class ShipContainer implements Disposable {

	/** The size of a single cell. Both width and height are equal to this value. */
	public static final int CELL_SIZE = 35;
	public static final WeaponObject DEFAULT_WEAPON_OBJ = new WeaponObject();

	private ArrayList<RoomController> roomControllers;
	private ArrayList<DoorController> doorControllers;
	private ArrayList<MountController> mountControllers;
	private ArrayList<GibController> gibControllers;
	private ArrayList<SystemController> systemControllers;

	private HashMap<GameObject, AbstractController> objectControllerMap;
	private HashMap<Images, ImageController> imageControllerMap;
	private HashMap<AbstractController, ArrayList<PropController>> propMap; // TODO

	private ShipController shipController = null;

	private ShipContainer() {
		roomControllers = new ArrayList<RoomController>();
		doorControllers = new ArrayList<DoorController>();
		mountControllers = new ArrayList<MountController>();
		gibControllers = new ArrayList<GibController>();
		systemControllers = new ArrayList<SystemController>();

		objectControllerMap = new HashMap<GameObject, AbstractController>();
		imageControllerMap = new HashMap<Images, ImageController>();
		propMap = new HashMap<AbstractController, ArrayList<PropController>>();
	}

	public ShipContainer(ShipObject ship) {
		this();

		shipController = ShipController.newInstance(this, ship);

		Grid grid = Grid.getInstance();

		for (RoomObject room : ship.getRooms()) {
			RoomController rc = RoomController.newInstance(this, room);

			int totalX = (ship.getXOffset() + room.getX() + room.getW() / 2) * CELL_SIZE;
			int totalY = (ship.getYOffset() + room.getY() + room.getH() / 2) * CELL_SIZE;

			rc.setSize(room.getW() * CELL_SIZE, room.getH() * CELL_SIZE);
			rc.setLocation(grid.snapToGrid(totalX, totalY, rc.getSnapMode()));
			rc.updateFollowOffset();

			add(rc);
		}
		for (DoorObject door : ship.getDoors()) {
			DoorController dc = DoorController.newInstance(this, door);

			int totalX = (ship.getXOffset() + door.getX()) * CELL_SIZE;
			int totalY = (ship.getYOffset() + door.getY()) * CELL_SIZE;

			dc.setLocation(grid.snapToGrid(totalX, totalY, dc.getSnapMode()));
			dc.updateFollowOffset();

			add(dc);
		}
		Point hullOffset = ship.getHullOffset();
		for (MountObject mount : ship.getMounts()) {
			MountController mc = MountController.newInstance(this, mount);

			int totalX = ship.getXOffset() * CELL_SIZE + mount.getX() + hullOffset.x;
			int totalY = ship.getYOffset() * CELL_SIZE + mount.getY() + hullOffset.y;

			mc.setLocation(grid.snapToGrid(totalX, totalY, mc.getSnapMode()));
			mc.updateFollowOffset();

			add(mc);
		}
		for (GibObject gib : ship.getGibs()) {
			GibController gc = GibController.newInstance(this, gib);

			// TODO

			add(gc);
		}

		for (Systems sys : Systems.values()) {
			SystemObject system = ship.getSystem(sys);
			SystemController systemC = SystemController.newInstance(this, system);
			add(systemC);
			if (system.canContainStation())
				add(StationController.newInstance(this, systemC, system.getStation()));

			RoomController room = (RoomController) getController(system.getRoom());
			if (room != null)
				assign(sys, room);
		}

		createImageControllers();
	}

	public ShipController getShipController() {
		return shipController;
	}

	public RoomController[] getRoomControllers() {
		return roomControllers.toArray(new RoomController[0]);
	}

	public DoorController[] getDoorControllers() {
		return doorControllers.toArray(new DoorController[0]);
	}

	public MountController[] getMountControllers() {
		return mountControllers.toArray(new MountController[0]);
	}

	public SystemController[] getSystemControllers() {
		return systemControllers.toArray(new SystemController[0]);
	}

	public SystemController getSystemController(Systems systemId) {
		for (SystemController sys : systemControllers) {
			if (sys.getSystemId() == systemId)
				return sys;
		}
		throw new IllegalArgumentException("Controller for the specified system id was not found: " + systemId);
	}

	public StationController getStationController(Systems systemId) {
		for (SystemController sys : systemControllers) {
			if (sys.getSystemId() == systemId)
				return (StationController) getController(sys.getGameObject().getStation());
		}

		return null;
	}

	public AbstractController getController(GameObject object) {
		return objectControllerMap.get(object);
	}

	/**
	 * @return the ship's offset, in pixels.
	 */
	protected Point findShipOffset() {
		int nx = -1, ny = -1;
		for (RoomController room : roomControllers) {
			int t = room.getX() - room.getW() / 2 - shipController.getX();
			if (nx == -1 || nx > t)
				nx = t;
			t = room.getY() - room.getH() / 2 - shipController.getY();
			if (ny == -1 || ny > t)
				ny = t;
		}
		return Grid.getInstance().snapToGrid(nx, ny, Snapmodes.CROSS);
	}

	/**
	 * @return the size of the ship, in pixels.
	 */
	protected Point findShipSize() {
		int mx = -1, my = -1;
		Point offset = findShipOffset();
		for (RoomController room : roomControllers) {
			int t = room.getX() + room.getW() / 2 - shipController.getX() - offset.x;
			if (t > mx)
				mx = t;
			t = room.getY() + room.getH() / 2 - shipController.getY() - offset.y;
			if (t > my)
				my = t;
		}
		return Grid.getInstance().snapToGrid(mx, my, Snapmodes.CROSS);
	}

	public void assign(Systems sys, RoomController room) {
		if (sys == null)
			throw new NullPointerException("System id must not be null.");
		if (room == null)
			throw new NullPointerException("Room controller is null. Use system.unassign() instead.");

		SystemController system = getSystemController(sys);
		StationController station = getStationController(sys);

		unassign(room.getSystemId());
		unassign(sys);
		system.assignTo(room.getGameObject());
		system.reposition(room.getX(), room.getY());
		system.setParent(room);

		room.addSizeListener(system);
		system.notifySizeChanged(room.getW(), room.getH());

		if (system.canContainStation()) {
			room.addSizeListener(station);
			station.updateFollowOffset();
			station.updateFollower();
			station.setVisible(room.canContainSlotId(station.getSlotId()));
		}
	}

	public void unassign(Systems sys) {
		if (sys == null)
			throw new NullPointerException("System id must not be null.");
		SystemController system = getSystemController(sys);
		StationController station = getStationController(sys);
		AbstractController room = getController(system.getGameObject().getRoom());

		if (system.canContainStation())
			station.setVisible(false);
		system.unassign();

		if (room != null) {
			room.removeSizeListener(system);
			room.removeSizeListener(station);
			room.redraw();
		}
	}

	public boolean isAssigned(Systems sys) {
		return getSystemController(sys).isAssigned();
	}

	public void add(AbstractController controller) {
		if (controller instanceof RoomController) {
			RoomController room = (RoomController) controller;
			room.getGameObject().setId(getNextRoomId());
			roomControllers.add(room);
			shipController.getGameObject().add(room.getGameObject());
		} else if (controller instanceof DoorController) {
			DoorController door = (DoorController) controller;
			doorControllers.add(door);
			shipController.getGameObject().add(door.getGameObject());
		} else if (controller instanceof MountController) {
			MountController mount = (MountController) controller;
			mount.getGameObject().setId(getNextMountId());
			mountControllers.add(mount);
			shipController.getGameObject().add(mount.getGameObject());
		} else if (controller instanceof GibController) {
			GibController gib = (GibController) controller;
			gibControllers.add(gib);
			shipController.getGameObject().add(gib.getGameObject());
		} else if (controller instanceof SystemController) {
			SystemController system = (SystemController) controller;
			systemControllers.add(system);
		}

		if (controller instanceof ObjectController)
			objectControllerMap.put(((ObjectController) controller).getGameObject(), controller);
	}

	public void remove(AbstractController controller) {
		if (controller instanceof RoomController) {
			RoomController room = (RoomController) controller;
			roomControllers.remove(room);
			shipController.getGameObject().remove(room.getGameObject());
		} else if (controller instanceof DoorController) {
			DoorController door = (DoorController) controller;
			doorControllers.remove(door);
			shipController.getGameObject().remove(door.getGameObject());
		} else if (controller instanceof MountController) {
			MountController mount = (MountController) controller;
			mountControllers.remove(mount);
			shipController.getGameObject().remove(mount.getGameObject());
		} else if (controller instanceof GibController) {
			GibController gib = (GibController) controller;
			gibControllers.remove(gib);
			shipController.getGameObject().remove(gib.getGameObject());
		} else if (controller instanceof SystemController) {
			SystemController system = (SystemController) controller;
			systemControllers.remove(system);
		}

		if (controller instanceof ObjectController)
			objectControllerMap.remove(((ObjectController) controller).getGameObject());
	}

	public void setImage(Images imageType, String path) {
		ImageController image = imageControllerMap.get(imageType);

		if (imageType == Images.THUMBNAIL) { // Thumbnail has no visual representation
			ImageObject object = image.getGameObject();
			object.setImagePath(path);
		} else {
			image.setVisible(false);
			image.setImage(path);
			image.setVisible(path != null);
		}
	}

	public String getImage(Images imageType) {
		ImageController image = imageControllerMap.get(imageType);
		return image.getImage();
	}

	private int getNextRoomId() {
		int id = 0;
		for (RoomObject object : shipController.getGameObject().getRooms()) {
			if (id == object.getId())
				id++;
		}
		return id;
	}

	private int getNextMountId() {
		return shipController.getGameObject().getNextMountId();
	}

	public void coalesceRooms() {
		shipController.getGameObject().coalesceRooms();
	}

	public void delete(AbstractController controller) {
		if (!controller.isDeletable())
			throw new NotDeletableException();

		controller.delete();
		remove(controller);
	}

	public void restore(AbstractController controller) {
		controller.restore();
		add(controller);
	}

	@Override
	public void dispose() throws NotDeletableException {
		if (shipController == null)
			return;

		for (MountController mount : mountControllers) {
			objectControllerMap.remove(mount.getGameObject());
			mount.dispose();
		}
		for (DoorController door : doorControllers) {
			objectControllerMap.remove(door.getGameObject());
			door.dispose();
		}
		for (RoomController room : roomControllers) {
			objectControllerMap.remove(room.getGameObject());
			room.dispose();
		}
		for (SystemController system : systemControllers) {
			objectControllerMap.remove(system.getGameObject());
			system.dispose();
		}
		for (GibController gib : gibControllers) {
			objectControllerMap.remove(gib.getGameObject());
			gib.dispose();
		}
		for (ImageController image : imageControllerMap.values()) {
			objectControllerMap.remove(image.getGameObject());
			image.dispose();
		}

		AbstractController[] objectControllers = objectControllerMap.values().toArray(new AbstractController[0]);
		for (AbstractController ac : objectControllers)
			ac.dispose();

		roomControllers.clear();
		doorControllers.clear();
		mountControllers.clear();
		gibControllers.clear();
		systemControllers.clear();
		imageControllerMap.clear();
		objectControllerMap.clear();

		shipController.dispose();
		shipController = null;

		System.gc();
	}

	public void updateChildBoundingAreas() {
		for (RoomController rc : roomControllers)
			rc.updateBoundingArea();
		for (DoorController dc : doorControllers)
			dc.updateBoundingArea();
		for (MountController mc : mountControllers)
			mc.updateBoundingArea();
		for (GibController gc : gibControllers)
			gc.updateBoundingArea();
	}

	public void updateBoundingArea() {
		Point gridSize = Grid.getInstance().getSize();
		gridSize.x -= (gridSize.x % CELL_SIZE) + CELL_SIZE;
		gridSize.y -= (gridSize.y % CELL_SIZE) + CELL_SIZE;
		Point offset = findShipOffset();
		Point size = findShipSize();
		shipController.setBoundingPoints(0, 0, gridSize.x - offset.x - size.x, gridSize.y - offset.y - size.y);
	}

	private void createImageControllers() {
		// Instantiation order is important for proper layering
		ShipObject ship = shipController.getGameObject();
		ImageObject imgObject = null;
		Point offset = null;
		Point center = null;

		// Load shield
		imgObject = ship.getImage(Images.SHIELD);
		ImageController shield = ImageController.newInstance(shipController, imgObject);
		shield.setImage(imgObject.getImagePath());

		offset = findShipOffset();
		center = findShipSize();
		center.x = offset.x + center.x / 2;
		center.y = offset.y + center.y / 2;

		shield.setLocation(center.x + ship.getEllipse().x, center.y + ship.getEllipse().y);
		shield.updateFollowOffset();

		imageControllerMap.put(Images.SHIELD, shield);
		add(shield);

		// Load hull
		imgObject = ship.getImage(Images.HULL);
		ImageController hull = ImageController.newInstance(shipController, imgObject);
		hull.setImage(imgObject.getImagePath());

		offset = ship.getHullOffset();
		offset.x += ship.getXOffset() * CELL_SIZE + ship.getHullSize().x / 2;
		offset.y += ship.getYOffset() * CELL_SIZE + ship.getHullSize().y / 2;
		hull.setSize(ship.getHullSize());
		hull.setLocation(offset);
		hull.updateFollowOffset();

		imageControllerMap.put(Images.HULL, hull);
		add(hull);

		// Load cloak
		imgObject = ship.getImage(Images.CLOAK);
		ImageController cloak = ImageController.newInstance(hull, imgObject);
		cloak.setImage(imgObject.getImagePath());

		// The offset is used to center the cloak on the hull image
		// Since the editor uses center-relative positioning, we don't need to anything here
		cloak.updateFollower();

		imageControllerMap.put(Images.CLOAK, cloak);
		add(cloak);

		// Load floor
		imgObject = ship.getImage(Images.FLOOR);
		ImageController floor = ImageController.newInstance(hull, imgObject);
		floor.setImage(imgObject.getImagePath());

		// Floor's offset is relative to hull's top-left corner
		offset = ship.getFloorOffset();
		center = hull.getLocation();
		center.x += -hull.getW() / 2 + offset.x + floor.getW() / 2;
		center.y += -hull.getH() / 2 + offset.y + floor.getH() / 2;
		floor.setLocation(center.x, center.y);
		floor.updateFollowOffset();

		imageControllerMap.put(Images.FLOOR, floor);
		add(floor);

		// Load thumbnail
		imgObject = ship.getImage(Images.THUMBNAIL);
		ImageController thumbnail = ImageController.newInstance(shipController, imgObject);
		thumbnail.setImage(imgObject.getImagePath());

		imageControllerMap.put(Images.THUMBNAIL, thumbnail);
		add(thumbnail);
		thumbnail.setVisible(false);
	}
}
