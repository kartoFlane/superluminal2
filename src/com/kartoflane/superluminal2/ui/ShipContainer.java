package com.kartoflane.superluminal2.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.components.NotDeletableException;
import com.kartoflane.superluminal2.components.interfaces.Disposable;
import com.kartoflane.superluminal2.ftl.DoorObject;
import com.kartoflane.superluminal2.ftl.GameObject;
import com.kartoflane.superluminal2.ftl.GibObject;
import com.kartoflane.superluminal2.ftl.MountObject;
import com.kartoflane.superluminal2.ftl.RoomObject;
import com.kartoflane.superluminal2.ftl.ShipObject;
import com.kartoflane.superluminal2.ftl.SystemObject;
import com.kartoflane.superluminal2.ftl.SystemObject.Systems;
import com.kartoflane.superluminal2.ftl.WeaponObject;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.DoorController;
import com.kartoflane.superluminal2.mvc.controllers.GibController;
import com.kartoflane.superluminal2.mvc.controllers.MountController;
import com.kartoflane.superluminal2.mvc.controllers.ObjectController;
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

	private ShipController shipController = null;

	private ShipContainer() {
		roomControllers = new ArrayList<RoomController>();
		doorControllers = new ArrayList<DoorController>();
		mountControllers = new ArrayList<MountController>();
		gibControllers = new ArrayList<GibController>();
		systemControllers = new ArrayList<SystemController>();

		objectControllerMap = new HashMap<GameObject, AbstractController>();
	}

	public ShipContainer(ShipObject ship) {
		this();

		shipController = ShipController.newInstance(this, ship);

		for (RoomObject room : ship.getRooms())
			add(RoomController.newInstance(this, room));
		for (DoorObject door : ship.getDoors())
			add(DoorController.newInstance(this, door));
		for (MountObject mount : ship.getMounts())
			add(MountController.newInstance(this, mount));
		for (GibObject gib : ship.getGibs())
			add(GibController.newInstance(this, gib));
		for (Systems sys : Systems.values()) {
			SystemObject system = ship.getSystem(sys);
			SystemController systemC = SystemController.newInstance(this, system);
			add(systemC);
			if (system.canContainStation())
				add(StationController.newInstance(this, systemC, system.getStation()));
		}
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
		system.assignTo(room.getGameObject());
		system.reposition(room.getX(), room.getY());
		system.setParent(room);

		room.addSizeListener(system);

		if (system.canContainStation()) {
			room.addSizeListener(station);
			station.updateFollowOffset();
			station.updateFollower();
			station.setVisible(true);
		}
	}

	public void unassign(Systems sys) {
		if (sys == null)
			throw new NullPointerException("System id must not be null.");
		SystemController system = getSystemController(sys);
		StationController station = getStationController(sys);
		AbstractController room = getController(system.getGameObject().getRoom());

		if (room != null) {
			room.removeSizeListener(system);
			room.removeSizeListener(station);
		}
		if (system.canContainStation())
			station.setVisible(false);
		system.unassign();
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
		} else {
			// not an ObjectController, nothing to do here
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
		} else {
			// not an ObjectController, nothing to do here
		}

		if (controller instanceof ObjectController)
			objectControllerMap.remove(((ObjectController) controller).getGameObject());
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
		int id = 0;
		for (MountObject object : shipController.getGameObject().getMounts()) {
			if (id == object.getId())
				id++;
		}
		return id;
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
			return; // nothing loaded yet

		ArrayList<AbstractController> controllers = new ArrayList<AbstractController>();
		controllers.addAll(roomControllers);
		controllers.addAll(doorControllers);
		controllers.addAll(mountControllers);
		controllers.addAll(gibControllers);

		for (AbstractController ac : controllers) {
			delete(ac);
		}

		controllers.clear();
		roomControllers.clear();
		doorControllers.clear();
		mountControllers.clear();
		gibControllers.clear();
		systemControllers.clear();

		shipController.dispose();
		shipController = null;

		// TODO dispose and clear all lists

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
}
