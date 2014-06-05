package com.kartoflane.superluminal2.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal2.components.EventHandler;
import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.components.NotDeletableException;
import com.kartoflane.superluminal2.components.enums.Directions;
import com.kartoflane.superluminal2.components.enums.Images;
import com.kartoflane.superluminal2.components.enums.Systems;
import com.kartoflane.superluminal2.components.interfaces.Disposable;
import com.kartoflane.superluminal2.components.interfaces.Follower;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.events.SLEvent;
import com.kartoflane.superluminal2.events.SLListener;
import com.kartoflane.superluminal2.ftl.DoorObject;
import com.kartoflane.superluminal2.ftl.GameObject;
import com.kartoflane.superluminal2.ftl.GibObject;
import com.kartoflane.superluminal2.ftl.ImageObject;
import com.kartoflane.superluminal2.ftl.MountObject;
import com.kartoflane.superluminal2.ftl.RoomObject;
import com.kartoflane.superluminal2.ftl.ShipObject;
import com.kartoflane.superluminal2.ftl.SystemObject;
import com.kartoflane.superluminal2.ftl.WeaponObject;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.DoorController;
import com.kartoflane.superluminal2.mvc.controllers.GibController;
import com.kartoflane.superluminal2.mvc.controllers.ImageController;
import com.kartoflane.superluminal2.mvc.controllers.MountController;
import com.kartoflane.superluminal2.mvc.controllers.ObjectController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;
import com.kartoflane.superluminal2.mvc.controllers.StationController;
import com.kartoflane.superluminal2.mvc.controllers.SystemController;
import com.kartoflane.superluminal2.mvc.controllers.props.PropController;
import com.kartoflane.superluminal2.tools.CreationTool;
import com.kartoflane.superluminal2.tools.Tool.Tools;
import com.kartoflane.superluminal2.utils.Utils;

/**
 * A simple class serving as a holder and communication layer between different controllers.
 * A meta-controller, so to say, or a semi-GUI widget.
 * 
 * @author kartoFlane
 * 
 */
public class ShipContainer implements Disposable, SLListener {

	/** The size of a single cell. Both width and height are equal to this value. */
	public static final int CELL_SIZE = 35;
	public static final String HANGAR_IMG_PATH = "cpath:/assets/hangar.png";
	public static final String ENEMY_IMG_PATH = "cpath:/assets/enemy.png";
	public static final String SHIELD_RESIZE_PROP_ID = "ShieldResizeHandle";

	private ArrayList<RoomController> roomControllers;
	private ArrayList<DoorController> doorControllers;
	private ArrayList<MountController> mountControllers;
	private ArrayList<GibController> gibControllers;
	private ArrayList<SystemController> systemControllers;

	private HashMap<GameObject, AbstractController> objectControllerMap;
	private HashMap<Images, ImageController> imageControllerMap;
	private HashMap<RoomObject, SystemObject> activeSystemMap;

	private boolean anchorVisible = true;
	private boolean mountsVisible = true;
	private boolean roomsVisible = true;
	private boolean doorsVisible = true;
	private boolean stationsVisible = true;

	private boolean shipSaved = false;
	private File saveDestination = null;

	private ShipController shipController = null;
	private EventHandler eventHandler = null;
	private EditorWindow window = null;

	private SLListener hangarListener = null;

	private ShipContainer() {
		roomControllers = new ArrayList<RoomController>();
		doorControllers = new ArrayList<DoorController>();
		mountControllers = new ArrayList<MountController>();
		gibControllers = new ArrayList<GibController>();
		systemControllers = new ArrayList<SystemController>();

		objectControllerMap = new HashMap<GameObject, AbstractController>();
		imageControllerMap = new HashMap<Images, ImageController>();
		activeSystemMap = new HashMap<RoomObject, SystemObject>();

		eventHandler = new EventHandler();
		hangarListener = new SLListener() {
			public void handleEvent(SLEvent e) {
				if (!shipController.isPlayerShip() && e.source instanceof RoomController) {
					ImageController hangar = getImageController(Images.HANGAR);
					Point size = findShipSize();
					hangar.setFollowOffset(size.x / 2, hangar.getFollowOffset().y);
				}
			}
		};
	}

	public ShipContainer(EditorWindow window, ShipObject ship) {
		this();
		this.window = window;

		shipController = ShipController.newInstance(this, ship);
		window.addListener(SLEvent.MOD_SHIFT, this);
		addListener(SLEvent.MOD_SHIFT, shipController);

		((CreationTool) Manager.getTool(Tools.CREATOR)).setEnabled(Tools.STATION, ship.isPlayerShip());

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

			mc.setFollowOffset(totalX, totalY);
			mc.updateFollower();

			add(mc);
		}
		// Gibs are not always listed in their order of appearance... Get by id instead
		// of iterating over array.
		GibObject gib = null;
		int i = ship.getGibs().length;
		// Gibs need to be added in reverse order for correct layering
		while ((gib = ship.getGibById(i)) != null) {
			GibController gc = GibController.newInstance(this, gib);

			Point offset = ship.getHullOffset();
			offset.x += ship.getXOffset() * CELL_SIZE + gc.getSize().x / 2 + gib.getOffsetX();
			offset.y += ship.getYOffset() * CELL_SIZE + gc.getSize().y / 2 + gib.getOffsetY();
			gc.setFollowOffset(offset);
			gc.updateFollower();

			add(gc);
			i--;
		}

		// Instantiate first, assign later
		for (Systems sys : Systems.values()) {
			for (SystemObject system : ship.getSystems(sys)) {
				SystemController systemC = SystemController.newInstance(this, system);

				add(systemC);
				if (sys.canContainStation())
					add(StationController.newInstance(this, systemC, system.getStation()));
			}
		}

		for (Systems sys : Systems.getSystems()) {
			for (SystemObject system : ship.getSystems(sys)) {
				RoomController room = (RoomController) getController(system.getRoom());

				if (room != null) {
					assign(system, room);
				}
			}
		}

		createImageControllers();
		ImageController hangarC = getImageController(Images.HANGAR);
		Point fo = hangarC.getFollowOffset();
		hangarC.setFollowOffset(fo.x - ship.getHorizontal(), fo.y - ship.getVertical());
		hangarC.updateFollower();

		updateBoundingArea();
		updateChildBoundingAreas();
	}

	public EditorWindow getParent() {
		return window;
	}

	/**
	 * This method causes game objects to be updated with data represented by the models.<br>
	 * It should be called right before saving the ship.
	 */
	public void updateGameObjects() {
		ShipObject ship = shipController.getGameObject();
		Point offset = findShipOffset();
		ship.setXOffset(offset.x / 35);
		ship.setYOffset(offset.y / 35);

		// Update image offsets, as they cannot be updated in ImageObjects, since they lack the needed data
		ImageController imageC = null;

		// Shield image is anchored at the center of the smallest rectangle that contains all rooms
		imageC = getImageController(Images.SHIELD);
		Point size = findShipSize();
		Point center = new Point(0, 0);
		center.x = offset.x + size.x / 2;
		center.y = offset.y + size.y / 2;

		Rectangle ellipse = new Rectangle(0, 0, 0, 0);
		ellipse.x = imageC.getX() - center.x - shipController.getX();
		ellipse.y = imageC.getY() - center.y - shipController.getY();
		// Ellipse's width and height are half of the actual shield image's dimensions
		ellipse.width = imageC.getW() / 2;
		ellipse.height = imageC.getH() / 2;

		ship.setEllipse(ellipse);
		center = null;
		size = null;

		// Hull image is anchored at the ship origin
		ImageController hull = imageC = getImageController(Images.HULL);
		Point hullSize = imageC.getSize();
		Point hullOffset = imageC.getLocation();
		hullOffset.x += -imageC.getW() / 2 - shipController.getX() - ship.getXOffset() * CELL_SIZE;
		hullOffset.y += -imageC.getH() / 2 - shipController.getY() - ship.getYOffset() * CELL_SIZE;

		ship.setHullDimensions(hullOffset.x, hullOffset.y, hullSize.x, hullSize.y);
		hullSize = null;
		hullOffset = null;

		// Floor is anchored at the top-left corner of the hull image
		imageC = getImageController(Images.FLOOR);
		Point floorOffset = imageC.getLocation();
		floorOffset.x += -imageC.getW() / 2 - (hull.getX() - hull.getW() / 2);
		floorOffset.y += -imageC.getH() / 2 - (hull.getY() - hull.getH() / 2);

		ship.setFloorOffset(floorOffset);
		floorOffset = null;

		// Cloak is anchored at the top-left corner of the hull image
		imageC = getImageController(Images.CLOAK);
		Point cloakOffset = imageC.getLocation();
		cloakOffset.x += -imageC.getW() / 2 - (hull.getX() - hull.getW() / 2);
		cloakOffset.y += -imageC.getH() / 2 - (hull.getY() - hull.getH() / 2);

		ship.setCloakOffset(cloakOffset);
		cloakOffset = null;

		// Update member objects of the ship
		for (GameObject gameObject : objectControllerMap.keySet()) {
			gameObject.update();
		}
	}

	public boolean isSaved() {
		return shipSaved;
	}

	public void setSaveDestination(File f) {
		saveDestination = f;
	}

	public File getSaveDestination() {
		return saveDestination;
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

	public GibController[] getGibControllers() {
		return gibControllers.toArray(new GibController[0]);
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

	public void setCloakedAppearance(boolean cloak) {
		ImageController cloakC = getImageController(Images.CLOAK);
		ImageController hullC = getImageController(Images.HULL);

		cloakC.setVisible(cloak);
		hullC.setAlpha(cloak ? 255 / 3 : 255);
	}

	/**
	 * @return the ship's offset, in pixels.
	 */
	public Point findShipOffset() {
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
	public Point findShipSize() {
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

	public Point findOptimalThickOffset() {
		if (roomControllers.size() == 0)
			return new Point(0, 0);

		Point result = new Point(0, 0);
		Point size = findShipSize();

		if (shipController.isPlayerShip()) {
			int hangarWidth = 259;
			int hangarHeight = 177;

			int horizontalSpace = hangarWidth - size.x / 2;
			int verticalSpace = hangarHeight - size.y / 2;

			result.x = Math.max(horizontalSpace / CELL_SIZE, 0);
			result.y = Math.max(verticalSpace / CELL_SIZE, 0);
		} else {
			// All enemy ships have to have thick offset equal to 0
			result.x = 0;
			result.y = 0;
		}

		return result;
	}

	public Point findOptimalFineOffset() {
		if (roomControllers.size() == 0)
			return new Point(0, 0);

		Point result = new Point(0, 0);
		Point size = findShipSize();

		if (shipController.isPlayerShip()) {
			int hangarWidth = 259;
			int hangarHeight = 177;

			int horizontalSpace = hangarWidth - size.x / 2;
			int verticalSpace = hangarHeight - size.y / 2;

			result.x = horizontalSpace % CELL_SIZE;
			result.y = verticalSpace % CELL_SIZE;
		} else {
			// All enemy ships have to have fine horizontal offset equal to 0, that way they're centered
			// Actualy viewable area of the enemy window is 376 x 504
			// Select midpoint in relation to which the ship's offset will be calculated
			int enemyWindowMidpoint = 249;
			// The amount of space taken up by the "target" text, and hull and shield indicators
			int topMargin = 77;

			// Horizontal doesn't affect enemy ships, so 0.
			result.x = 0;
			result.y = enemyWindowMidpoint - size.y / 2 - topMargin;
		}

		return result;
	}

	public void setShipOffset(int x, int y) {
		ImageController hangarC = getImageController(Images.HANGAR);
		if (hangarC == null)
			return;

		ShipObject ship = shipController.getGameObject();
		for (Follower fol : shipController.getFollowers()) {
			if (fol instanceof PropController == false && fol != hangarC) {
				Point old = fol.getFollowOffset();
				fol.setFollowOffset(old.x + (x - ship.getXOffset()) * CELL_SIZE,
						old.y + (y - ship.getYOffset()) * CELL_SIZE);
				fol.updateFollower();
			}
		}

		ship.setXOffset(x);
		ship.setYOffset(y);
	}

	/**
	 * Horizontal:<br>
	 * - positive values move the ship to the right (hangar to the left relative to the ship)<br>
	 * Vertical:<br>
	 * - positive values move the ship to the bottom (hangar to the top relative to the ship)
	 * 
	 * @param x
	 * @param y
	 */
	public void setShipFineOffset(int x, int y) {
		ImageController hangarC = getImageController(Images.HANGAR);
		if (hangarC == null)
			return;

		ShipObject ship = shipController.getGameObject();
		Point fo = hangarC.getFollowOffset();

		hangarC.setFollowOffset(fo.x + ship.getHorizontal() - x, fo.y + ship.getVertical() - y);
		hangarC.updateFollower();

		ship.setHorizontal(x);
		ship.setVertical(y);
	}

	public void assign(SystemObject sys, RoomController room) {
		if (sys == null)
			throw new NullPointerException("System id must not be null.");
		if (room == null)
			throw new NullPointerException("Room controller is null. Use unassign() instead.");

		SystemController system = (SystemController) getController(sys);
		StationController station = (StationController) getController(sys.getStation());

		unassign(sys);
		system.assignTo(room.getGameObject());
		system.reposition(room.getX(), room.getY());
		system.setParent(room);

		setActiveSystem(room.getGameObject(), sys);

		room.addListener(SLEvent.RESIZE, system);
		system.notifySizeChanged(room.getW(), room.getH());

		if (sys.canContainStation()) {
			if (!shipController.isPlayerShip()) {
				station.setSlotId(-2);
			}
			room.addListener(SLEvent.RESIZE, station);
			station.updateFollowOffset();
			station.updateFollower();
			station.updateView();
		}
	}

	public void unassign(SystemObject system) {
		if (system == null)
			throw new NullPointerException("System must not be null.");

		SystemController systemC = (SystemController) getController(system);
		StationController stationC = (StationController) getController(system.getStation());
		RoomController roomC = (RoomController) getController(system.getRoom());

		if (system.canContainStation())
			stationC.setVisible(false);
		systemC.unassign();

		if (roomC != null) {
			roomC.removeListener(SLEvent.RESIZE, systemC);
			roomC.removeListener(SLEvent.RESIZE, stationC);
			roomC.redraw();

			if (activeSystemMap.get(roomC.getGameObject()) == system) {
				ArrayList<SystemObject> systems = getAllAssignedSystems(roomC.getGameObject());
				if (systems.size() > 0)
					setActiveSystem(roomC.getGameObject(), systems.get(0));
				else
					activeSystemMap.remove(roomC.getGameObject());
			}
		}
	}

	public boolean isAssigned(Systems sys) {
		ShipObject ship = shipController.getGameObject();
		for (SystemObject system : ship.getSystems(sys)) {
			if (system.isAssigned())
				return true;
		}
		return false;
	}

	public void add(AbstractController controller) {
		if (controller instanceof RoomController) {
			RoomController room = (RoomController) controller;
			if (room.getId() == Database.AIRLOCK_OBJECT.getId())
				room.setId(getNextRoomId());
			roomControllers.add(room);
			shipController.getGameObject().add(room.getGameObject());

			room.addListener(SLEvent.MOVE, hangarListener);
			room.addListener(SLEvent.RESIZE, hangarListener);
			room.addListener(SLEvent.DELETE, hangarListener);
			room.addListener(SLEvent.RESTORE, hangarListener);
		} else if (controller instanceof DoorController) {
			DoorController door = (DoorController) controller;
			doorControllers.add(door);
			shipController.getGameObject().add(door.getGameObject());
		} else if (controller instanceof MountController) {
			MountController mount = (MountController) controller;
			if (mount.getId() == -2)
				mount.setId(getNextMountId());
			mountControllers.add(mount);
			shipController.getGameObject().add(mount.getGameObject());
			updateMounts();
		} else if (controller instanceof GibController) {
			GibController gib = (GibController) controller;
			gibControllers.add(gib);
			shipController.getGameObject().add(gib.getGameObject());
		} else if (controller instanceof SystemController) {
			SystemController system = (SystemController) controller;
			systemControllers.add(system);
		}

		if (controller instanceof ObjectController) {
			objectControllerMap.put(((ObjectController) controller).getGameObject(), controller);

			eventHandler.sendEvent(new SLEvent(SLEvent.ADD_OBJECT, this, controller));
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
			updateMounts();
		} else if (controller instanceof GibController) {
			GibController gib = (GibController) controller;
			gibControllers.remove(gib);
			shipController.getGameObject().remove(gib.getGameObject());
		} else if (controller instanceof SystemController) {
			SystemController system = (SystemController) controller;
			systemControllers.remove(system);
		}

		if (controller instanceof ObjectController) {
			objectControllerMap.remove(((ObjectController) controller).getGameObject());

			eventHandler.sendEvent(new SLEvent(SLEvent.REM_OBJECT, this, controller));
		}
	}

	public void setImage(Images imageType, String path) {
		ImageController image = imageControllerMap.get(imageType);

		if (imageType == Images.THUMBNAIL) { // Thumbnail has no visual representation
			ImageObject object = image.getGameObject();
			object.setImagePath(path);
		} else {
			image.setVisible(false);
			image.setImage(path);
			image.updateView();
			image.setVisible(path != null);
		}
	}

	public String getImage(Images imageType) {
		ImageController image = imageControllerMap.get(imageType);
		return image.getImage();
	}

	public ImageController getImageController(Images imageType) {
		return imageControllerMap.get(imageType);
	}

	private int getNextRoomId() {
		return shipController.getGameObject().getNextRoomId();
	}

	private int getNextMountId() {
		return shipController.getGameObject().getNextMountId();
	}

	public void coalesceRooms() {
		shipController.getGameObject().coalesceRooms();
	}

	public void changeWeapon(int index, WeaponObject neu) {
		shipController.getGameObject().changeWeapon(index, neu);
		updateMounts();
	}

	public void changeWeapon(WeaponObject old, WeaponObject neu) {
		shipController.getGameObject().changeWeapon(old, neu);
		updateMounts();
	}

	public void updateMounts() {
		int i = 0;
		WeaponObject[] weapons = shipController.getGameObject().getWeapons();
		for (MountController mount : mountControllers) {
			mount.setId(i);
			if (i < weapons.length)
				mount.setWeapon(weapons[i]);
			else
				mount.setWeapon(Database.DEFAULT_WEAPON_OBJ);
			i++;
		}
	}

	public void delete(AbstractController controller) {
		if (!controller.isDeletable())
			throw new NotDeletableException();

		controller.delete();
		remove(controller);
		updateBoundingArea();

		if (controller instanceof RoomController) {
			RoomController rc = (RoomController) controller;
			for (SystemObject sys : getAllAssignedSystems(rc.getGameObject())) {
				unassign(sys);
			}
		}
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
		for (AbstractController ac : objectControllers) {
			ac.dispose();
		}

		roomControllers.clear();
		doorControllers.clear();
		mountControllers.clear();
		gibControllers.clear();
		systemControllers.clear();
		imageControllerMap.clear();
		objectControllerMap.clear();

		window.removeListener(SLEvent.MOD_SHIFT, this);
		shipController.dispose();
		shipController = null;

		eventHandler.dispose();

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
		for (ImageController ic : imageControllerMap.values())
			ic.updateBoundingArea();
	}

	public void updateBoundingArea() {
		Point gridSize = Grid.getInstance().getSize();
		gridSize.x -= (gridSize.x % CELL_SIZE) + CELL_SIZE;
		gridSize.y -= (gridSize.y % CELL_SIZE) + CELL_SIZE;
		Point offset = findShipOffset();
		Point size = findShipSize();
		shipController.setBoundingPoints(0, 0, gridSize.x - offset.x - size.x, gridSize.y - offset.y - size.y);
		offset.x = offset.x / CELL_SIZE;
		offset.y = offset.y / CELL_SIZE;
		shipController.getGameObject().setXOffset(offset.x);
		shipController.getGameObject().setYOffset(offset.y);
		shipController.updateProps();
	}

	private void createImageControllers() {
		// Instantiation order is important for proper layering
		final ShipObject ship = shipController.getGameObject();
		ImageObject imgObject = null;
		Point offset = null;
		Point center = null;

		// Load hangar image
		imgObject = new ImageObject();
		imgObject.setAlias("hangar");
		imgObject.setImagePath(ship.isPlayerShip() ? HANGAR_IMG_PATH : ENEMY_IMG_PATH);
		final ImageController hangar = ImageController.newInstance(shipController, imgObject);
		hangar.setSelectable(false);
		hangar.removeFromPainter();
		hangar.addToPainter(Layers.BACKGROUND);
		hangar.setImage(imgObject.getImagePath());
		offset = hangar.getSize();
		// For FTL hangar image: 365, 30
		// For SL2 hangar image: 94, 56
		// For enemy window image: 193, 239
		if (ship.isPlayerShip()) {
			hangar.setFollowOffset(offset.x / 2 - 94, offset.y / 2 - 56);
		} else {
			// Not entirely clear correction; perhaps related to the difference between in-game enemy
			// window, and the image SL2 uses, which is 26 pixels smaller. 26 * 2 = 52, so kinda related...
			// Either way, adding this value causes all enemy ships to become correctly aligned
			int correction = 53;
			hangar.setFollowOffset(offset.x / 2 - 193, offset.y / 2 - 239 + Database.ENEMY_SHIELD_Y_OFFSET + correction);
		}
		hangar.updateFollower();
		hangar.updateView();
		imageControllerMap.put(Images.HANGAR, hangar);

		// Enemy ships don't use HORIZONTAL offset; the game automatically centers them in the enemy window
		// Have the hangar image listen to events when the ship's width changes, so that it can modify its own
		// location as needed
		addListener(SLEvent.ADD_OBJECT, hangarListener);
		addListener(SLEvent.REM_OBJECT, hangarListener);
		addListener(SLEvent.MOVE, hangarListener);

		// Load shield
		imgObject = ship.getImage(Images.SHIELD);
		ImageController shield = ImageController.newInstance(shipController, imgObject);
		shield.setImage(imgObject.getImagePath());

		offset = findShipOffset();
		center = findShipSize();
		center.x = offset.x + center.x / 2;
		center.y = offset.y + center.y / 2;

		shield.setFollowOffset(center.x + ship.getEllipseX(), center.y + ship.getEllipseY());
		if (!ship.isPlayerShip()) {
			shield.setSize(ship.getEllipseWidth() * 2, ship.getEllipseHeight() * 2);
		}
		shield.updateView();

		imageControllerMap.put(Images.SHIELD, shield);
		add(shield);
		shield.setBounded(true);

		if (!ship.isPlayerShip()) {
			// Shield resize prop
			PropController prop = new PropController(shield, SHIELD_RESIZE_PROP_ID);
			prop.setSelectable(true);
			prop.setInheritVisibility(true);
			prop.setDefaultBackgroundColor(128, 128, 255);
			prop.setDefaultBorderColor(0, 0, 0);
			prop.setBorderThickness(3);
			prop.setCompositeTitle("Shield Resize Handle");
			prop.setSize(CELL_SIZE / 2, CELL_SIZE / 2);
			prop.setLocation(shield.getX() + shield.getW() / 2, shield.getY() + shield.getH() / 2);
			prop.updateFollowOffset();
			prop.addToPainter(Layers.SHIP_ORIGIN);
			prop.updateView();
			shield.addProp(prop);
			prop.addListener(SLEvent.MOVE, new SLListener() {
				@Override
				public void handleEvent(SLEvent e) {
					Point p = (Point) e.data;
					ImageController shieldC = getImageController(Images.SHIELD);
					shieldC.resize(Math.abs(p.x - shieldC.getX()) * 2, Math.abs(p.y - shieldC.getY()) * 2);
				}
			});
		}

		// Load hull
		imgObject = ship.getImage(Images.HULL);
		ImageController hull = ImageController.newInstance(shipController, imgObject);
		hull.setImage(imgObject.getImagePath());

		offset = ship.getHullOffset();
		offset.x += ship.getXOffset() * CELL_SIZE + ship.getHullSize().x / 2;
		offset.y += ship.getYOffset() * CELL_SIZE + ship.getHullSize().y / 2;
		hull.setSize(ship.getHullSize());
		hull.setFollowOffset(offset);
		hull.updateFollower();
		hull.updateView();

		imageControllerMap.put(Images.HULL, hull);
		add(hull);
		hull.setBounded(true);

		// Load cloak
		imgObject = ship.getImage(Images.CLOAK);
		ImageController cloak = ImageController.newInstance(hull, imgObject);
		cloak.setImage(imgObject.getImagePath());

		// Cloak's offset is relative to hull's top-left corner
		offset = ship.getCloakOffset();
		center = Utils.add(hull.getLocationCorner(), ship.getCloakOffset());
		cloak.setLocationCorner(center.x, center.y);
		cloak.updateFollowOffset();
		cloak.updateView();

		imageControllerMap.put(Images.CLOAK, cloak);
		add(cloak);
		cloak.setVisible(false); // Cloak is only displayed when View Cloak is enabled
		cloak.setBounded(true);

		// Load floor
		imgObject = ship.getImage(Images.FLOOR);
		ImageController floor = ImageController.newInstance(hull, imgObject);
		floor.setImage(imgObject.getImagePath());

		// Floor's offset is relative to hull's top-left corner
		offset = ship.getFloorOffset();
		center.x = (int) Math.round((floor.getW() - hull.getW()) / 2.0) + offset.x;
		center.y = (int) Math.round((floor.getH() - hull.getH()) / 2.0) + offset.y;
		floor.setFollowOffset(center.x, center.y);
		floor.updateView();

		imageControllerMap.put(Images.FLOOR, floor);
		add(floor);
		floor.setBounded(true);

		// Load thumbnail
		imgObject = ship.getImage(Images.THUMBNAIL);
		ImageController thumbnail = ImageController.newInstance(shipController, imgObject);
		thumbnail.setImage(imgObject.getImagePath());

		imageControllerMap.put(Images.THUMBNAIL, thumbnail);
		add(thumbnail);
		thumbnail.setVisible(false);
	}

	public void setHangarVisible(boolean vis) {
		getImageController(Images.HANGAR).setVisible(vis);
	}

	public boolean isHangarVisible() {
		return getImageController(Images.HANGAR).isVisible();
	}

	public void setAnchorVisible(boolean vis) {
		anchorVisible = vis;
		shipController.setVisible(vis);
	}

	public boolean isAnchorVisible() {
		return anchorVisible;
	}

	public void setRoomsVisible(boolean vis) {
		roomsVisible = vis;
		for (RoomController r : roomControllers)
			r.setVisible(vis);
		for (SystemController s : systemControllers) {
			SystemObject sys = s.getGameObject();
			s.setVisible(vis && s.isAssigned() && getActiveSystem(sys.getRoom()) == sys);
		}
		applyStationVisibility(vis && stationsVisible);
	}

	public boolean isRoomsVisible() {
		return roomsVisible;
	}

	public void setDoorsVisible(boolean vis) {
		doorsVisible = vis;
		for (DoorController d : doorControllers)
			d.setVisible(vis);
	}

	public boolean isDoorsVisible() {
		return doorsVisible;
	}

	public void setMountsVisible(boolean vis) {
		mountsVisible = vis;
		for (MountController m : mountControllers) {
			m.setVisible(vis);
			m.getProp(MountController.ARROW_PROP_ID).setVisible(vis && m.getDirection() != Directions.NONE);
		}
	}

	public boolean isMountsVisible() {
		return mountsVisible;
	}

	public void setStationsVisible(boolean vis) {
		stationsVisible = vis;
		applyStationVisibility(vis && roomsVisible);
	}

	public boolean isStationsVisible() {
		return stationsVisible;
	}

	private void applyStationVisibility(boolean vis) {
		for (SystemController s : systemControllers) {
			if (s.isAssigned() && s.canContainStation()) {
				StationController st = (StationController) getController(s.getGameObject().getStation());
				st.setVisible(vis && st.getSlotId() != -2 && getActiveSystem(s.getGameObject().getRoom()) == s.getGameObject());
			}
		}
	}

	public void setActiveSystem(RoomObject room, SystemObject sys) {
		if (room == null)
			throw new IllegalArgumentException("Room must not be null.");
		if (sys == null)
			throw new IllegalArgumentException("System must not be null.");

		SystemObject prevSystem = getActiveSystem(room);
		SystemController prevSystemC = (SystemController) getController(prevSystem);
		prevSystemC.setVisible(false);

		activeSystemMap.put(room, sys);

		if (prevSystem != null && prevSystem.canContainStation())
			getController(prevSystem.getStation()).updateView();

		getController(sys).setVisible(true);
		if (sys.canContainStation())
			getController(sys.getStation()).updateView();
		getController(room).redraw();
	}

	public SystemObject getActiveSystem(RoomObject room) {
		if (activeSystemMap.containsKey(room))
			return activeSystemMap.get(room);
		return shipController.getGameObject().getSystem(Systems.EMPTY);
	}

	public ArrayList<SystemObject> getAllAssignedSystems(RoomObject room) {
		ArrayList<SystemObject> systems = new ArrayList<SystemObject>();

		for (Systems sys : Systems.getSystems()) {
			for (SystemObject system : shipController.getGameObject().getSystems(sys)) {
				if (system.getRoom() == room)
					systems.add(system);
			}
		}
		return systems;
	}

	public void addListener(int eventType, SLListener listener) {
		eventHandler.hook(eventType, listener);
	}

	public void removeListener(int eventType, SLListener listener) {
		eventHandler.unhook(eventType, listener);
	}

	@Override
	public void handleEvent(SLEvent e) {
		// Send the event over to controllers
		eventHandler.sendEvent(e);
	}
}
