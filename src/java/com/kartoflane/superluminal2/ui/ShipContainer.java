package com.kartoflane.superluminal2.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.components.NotDeletableException;
import com.kartoflane.superluminal2.components.enums.Images;
import com.kartoflane.superluminal2.components.enums.Systems;
import com.kartoflane.superluminal2.components.interfaces.Disposable;
import com.kartoflane.superluminal2.components.interfaces.Follower;
import com.kartoflane.superluminal2.components.interfaces.LocationListener;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.core.Manager;
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
import com.kartoflane.superluminal2.mvc.controllers.PropController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;
import com.kartoflane.superluminal2.mvc.controllers.StationController;
import com.kartoflane.superluminal2.mvc.controllers.SystemController;
import com.kartoflane.superluminal2.utils.Utils;

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
	public static final String HANGAR_IMG_PATH = "db:img/customizeUI/custom_main.png";
	public static final String SHIELD_RESIZE_PROP_ID = "ShieldResizeHandle";

	private ArrayList<RoomController> roomControllers;
	private ArrayList<DoorController> doorControllers;
	private ArrayList<MountController> mountControllers;
	private ArrayList<GibController> gibControllers;
	private ArrayList<SystemController> systemControllers;

	private HashMap<GameObject, AbstractController> objectControllerMap;
	private HashMap<Images, ImageController> imageControllerMap;
	private HashMap<RoomObject, Systems> activeSystemMap;

	private boolean anchorVisible = true;
	private boolean mountsVisible = true;
	private boolean roomsVisible = true;
	private boolean doorsVisible = true;
	private boolean stationsVisible = true;

	private boolean shipSaved = false;

	private ShipController shipController = null;

	private ShipContainer() {
		roomControllers = new ArrayList<RoomController>();
		doorControllers = new ArrayList<DoorController>();
		mountControllers = new ArrayList<MountController>();
		gibControllers = new ArrayList<GibController>();
		systemControllers = new ArrayList<SystemController>();

		objectControllerMap = new HashMap<GameObject, AbstractController>();
		imageControllerMap = new HashMap<Images, ImageController>();
		activeSystemMap = new HashMap<RoomObject, Systems>();
	}

	public ShipContainer(ShipObject ship) {
		this();

		shipController = ShipController.newInstance(this, ship);
		Manager.addModifierListener(shipController);

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
		for (GibObject gib : ship.getGibs()) {
			GibController gc = GibController.newInstance(this, gib);

			// TODO

			add(gc);
		}

		// Instantiate first, assign later
		for (Systems sys : Systems.values()) {
			SystemObject system = ship.getSystem(sys);
			SystemController systemC = SystemController.newInstance(this, system);

			add(systemC);
			if (sys.canContainStation())
				add(StationController.newInstance(this, systemC, system.getStation()));
		}

		for (Systems sys : Systems.getSystems()) {
			SystemObject system = ship.getSystem(sys);
			RoomController room = (RoomController) getController(system.getRoom());

			if (room != null)
				assign(sys, room);
		}

		createImageControllers();
		ImageController hangarC = getImageController(Images.HANGAR);
		Point fo = hangarC.getFollowOffset();
		hangarC.setFollowOffset(fo.x - ship.getHorizontal(), fo.y - ship.getVertical());
		hangarC.updateFollower();

		updateBoundingArea();
		updateChildBoundingAreas();
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
			int hangarWidth = 235;
			int hangarHeight = 180;

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
			int hangarWidth = 235;
			int hangarHeight = 180;

			int horizontalSpace = hangarWidth - size.x / 2;
			int verticalSpace = hangarHeight - size.y / 2;

			result.x = horizontalSpace % CELL_SIZE;
			result.y = verticalSpace % CELL_SIZE;
		} else {
			// Enemy window is 376 x 504, 55px top margin
			// final int enemyWindowWidth = 376;
			// All enemy ships have to have fine horizontal offset equal to 0, that way they're centered
			int enemyWindowHeight = 504;
			int topMargin = 55;

			shipController.getGameObject().setHorizontal(0);
			shipController.getGameObject().setVertical((enemyWindowHeight - size.y) / 2 - 3 * topMargin / 2);
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

	public void assign(Systems sys, RoomController room) {
		if (sys == null)
			throw new NullPointerException("System id must not be null.");
		if (room == null)
			throw new NullPointerException("Room controller is null. Use unassign() instead.");

		SystemController system = getSystemController(sys);
		StationController station = getStationController(sys);

		unassign(sys);
		system.assignTo(room.getGameObject());
		system.reposition(room.getX(), room.getY());
		system.setParent(room);

		setActiveSystem(room.getGameObject(), sys);

		room.addSizeListener(system);
		system.notifySizeChanged(room.getW(), room.getH());

		if (sys.canContainStation()) {
			room.addSizeListener(station);
			station.updateFollowOffset();
			station.updateFollower();
			station.updateView();
		}
	}

	public void unassign(Systems sys) {
		if (sys == null)
			throw new NullPointerException("System id must not be null.");

		SystemController system = getSystemController(sys);
		StationController station = getStationController(sys);
		RoomController room = (RoomController) getController(system.getGameObject().getRoom());

		if (sys.canContainStation())
			station.setVisible(false);
		system.unassign();

		if (room != null) {
			room.removeSizeListener(system);
			room.removeSizeListener(station);
			room.redraw();

			if (activeSystemMap.get(room.getGameObject()) == sys) {
				ArrayList<Systems> systems = getAllAssignedSystems(room.getGameObject());
				if (systems.size() > 0)
					setActiveSystem(room.getGameObject(), systems.get(0));
				else
					activeSystemMap.remove(room.getGameObject());
			}
		}
	}

	public boolean isAssigned(Systems sys) {
		return getSystemController(sys).isAssigned();
	}

	public void add(AbstractController controller) {
		if (controller instanceof RoomController) {
			RoomController room = (RoomController) controller;
			if (room.getId() == Database.AIRLOCK_OBJECT.getId())
				room.setId(getNextRoomId());
			roomControllers.add(room);
			shipController.getGameObject().add(room.getGameObject());
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
			updateMounts();
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
			for (Systems sys : getAllAssignedSystems(rc.getGameObject())) {
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
		for (AbstractController ac : objectControllers)
			ac.dispose();

		roomControllers.clear();
		doorControllers.clear();
		mountControllers.clear();
		gibControllers.clear();
		systemControllers.clear();
		imageControllerMap.clear();
		objectControllerMap.clear();

		Manager.removeModifierListener(shipController);
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
		offset.x = offset.x / CELL_SIZE;
		offset.y = offset.y / CELL_SIZE;
		shipController.getGameObject().setXOffset(offset.x);
		shipController.getGameObject().setYOffset(offset.y);
		shipController.updateProps();
	}

	private void createImageControllers() {
		// Instantiation order is important for proper layering
		ShipObject ship = shipController.getGameObject();
		ImageObject imgObject = null;
		Point offset = null;
		Point center = null;

		// Load hangar image
		imgObject = new ImageObject();
		imgObject.setAlias("hangar");
		imgObject.setImagePath(HANGAR_IMG_PATH);
		ImageController hangar = ImageController.newInstance(shipController, imgObject);
		hangar.setSelectable(false);
		hangar.removeFromPainter();
		hangar.addToPainter(Layers.BACKGROUND);
		hangar.setImage(imgObject.getImagePath());
		offset = hangar.getSize();
		hangar.setFollowOffset(offset.x / 2 - 365, offset.y / 2 - 30);
		hangar.updateFollower();
		hangar.updateView();
		imageControllerMap.put(Images.HANGAR, hangar);

		// Load shield
		imgObject = ship.getImage(Images.SHIELD);
		ImageController shield = ImageController.newInstance(shipController, imgObject);
		shield.setImage(imgObject.getImagePath());

		offset = findShipOffset();
		center = findShipSize();
		center.x = offset.x + center.x / 2;
		center.y = offset.y + center.y / 2;

		shield.setFollowOffset(center.x + ship.getEllipse().x, center.y + ship.getEllipse().y);
		if (!ship.isPlayerShip()) {
			Rectangle ellipse = ship.getEllipse();
			shield.setSize(ellipse.width * 2, ellipse.height * 2);
		}
		shield.updateView();

		imageControllerMap.put(Images.SHIELD, shield);
		add(shield);

		if (!ship.isPlayerShip()) {
			// Shield resize prop
			PropController prop = new PropController(shield, SHIELD_RESIZE_PROP_ID);
			prop.setSelectable(true);
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
			prop.addLocationListener(new LocationListener() {
				@Override
				public void notifyLocationChanged(int x, int y) {
					ImageController shieldC = getImageController(Images.SHIELD);
					shieldC.resize(Math.abs(x - shieldC.getX()) * 2, Math.abs(y - shieldC.getY()) * 2);
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
		for (MountController m : mountControllers)
			m.setVisible(vis);
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
				st.setVisible(vis && st.getSlotId() != -2 && getActiveSystem(s.getGameObject().getRoom()) == s.getSystemId());
			}
		}
	}

	public void setActiveSystem(RoomObject room, Systems sys) {
		if (room == null)
			throw new IllegalArgumentException("Room must not be null.");
		if (sys == null)
			throw new IllegalArgumentException("System must not be null.");

		Systems prevSystem = getActiveSystem(room);
		SystemController prevSystemC = getSystemController(prevSystem);
		prevSystemC.setVisible(false);

		activeSystemMap.put(room, sys);

		if (prevSystem != null && prevSystem.canContainStation())
			getStationController(prevSystem).updateView();

		getSystemController(sys).setVisible(true);
		if (sys.canContainStation())
			getStationController(sys).updateView();
		getController(room).redraw();
	}

	public Systems getActiveSystem(RoomObject room) {
		if (activeSystemMap.containsKey(room))
			return activeSystemMap.get(room);
		return Systems.EMPTY;
	}

	public ArrayList<Systems> getAllAssignedSystems(RoomObject room) {
		ArrayList<Systems> systems = new ArrayList<Systems>();

		for (Systems sys : Systems.getSystems()) {
			SystemObject system = shipController.getGameObject().getSystem(sys);
			if (system.getRoom() == room)
				systems.add(sys);
		}
		return systems;
	}
}
