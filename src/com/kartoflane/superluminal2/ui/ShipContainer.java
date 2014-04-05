package com.kartoflane.superluminal2.ui;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.components.NotDeletableException;
import com.kartoflane.superluminal2.components.interfaces.Disposable;
import com.kartoflane.superluminal2.ftl.DoorObject;
import com.kartoflane.superluminal2.ftl.GameObject;
import com.kartoflane.superluminal2.ftl.MountObject;
import com.kartoflane.superluminal2.ftl.RoomObject;
import com.kartoflane.superluminal2.ftl.ShipObject;
import com.kartoflane.superluminal2.ftl.SystemObject;
import com.kartoflane.superluminal2.ftl.SystemObject.Systems;
import com.kartoflane.superluminal2.ftl.WeaponObject;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.DoorController;
import com.kartoflane.superluminal2.mvc.controllers.MountController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;
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
	// private ArrayList<GibController> gibControllers; // TODO
	private ArrayList<SystemController> systemControllers;
	private ShipController shipController;

	private ShipContainer() {
		roomControllers = new ArrayList<RoomController>();
		doorControllers = new ArrayList<DoorController>();
		mountControllers = new ArrayList<MountController>();
		// gibControllers = new ArrayList<GibController>();
		systemControllers = new ArrayList<SystemController>();
	}

	public ShipContainer(ShipObject ship) {
		this();

		shipController = ShipController.newInstance(this, ship);

		for (RoomObject room : ship.getRooms())
			roomControllers.add(RoomController.newInstance(this, room));
		for (DoorObject door : ship.getDoors())
			doorControllers.add(DoorController.newInstance(this, door));
		for (MountObject mount : ship.getMounts())
			mountControllers.add(MountController.newInstance(this, mount));
		for (Systems sys : Systems.values()) {
			SystemObject system = ship.getSystem(sys);
			systemControllers.add(SystemController.newInstance(this, system));
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
			if (sys.getGameObject().getSystemId() == systemId)
				return sys;
		}
		throw new IllegalArgumentException("Controller for the specified system id was not found: " + systemId);
	}

	public AbstractController getController(GameObject object) {
		if (object instanceof RoomObject) {
			for (RoomController control : roomControllers)
				if (control.getGameObject() == object)
					return control;
		} else if (object instanceof DoorObject) {
			for (DoorController control : doorControllers)
				if (control.getGameObject() == object)
					return control;
		} else if (object instanceof MountObject) {
			for (MountController control : mountControllers)
				if (control.getGameObject() == object)
					return control;
		}
		return null;
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
			throw new NullPointerException("System ID is null.");
		if (room == null)
			throw new NullPointerException("Room controller is null. Use system.unassign() instead.");

		unassign(room.getSystemId());
		SystemController system = getSystemController(sys);
		system.assignTo(room.getGameObject());
		system.setSize(room.getSize());
		system.reposition(room.getLocation());
		system.setParent(room);
		room.addSizeListener(system);
	}

	public void unassign(Systems sys) {
		if (sys == null)
			throw new NullPointerException("System ID is null.");
		SystemController system = getSystemController(sys);
		AbstractController room = getController(system.getGameObject().getRoom());
		if (room != null)
			room.removeSizeListener(system);
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
		} else {
			// not an ObjectController, nothing to do here
		}
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
		} else {
			// not an ObjectController, nothing to do here
		}
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

		if (controller instanceof RoomController)
			remove(controller);
		// TODO remove from lists
	}

	public void restore(AbstractController controller) {
		controller.restore();

		// TODO readd to lists
	}

	@Override
	public void dispose() throws NotDeletableException {
		// TODO dispose and clear all lists, call GC
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
