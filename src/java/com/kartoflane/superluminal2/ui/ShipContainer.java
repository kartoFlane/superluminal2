package com.kartoflane.superluminal2.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.undo.AbstractUndoableEdit;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.kartoflane.ftl.floorgen.FloorImageFactory;
import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.components.EventHandler;
import com.kartoflane.superluminal2.components.NotDeletableException;
import com.kartoflane.superluminal2.components.Tuple;
import com.kartoflane.superluminal2.components.enums.Images;
import com.kartoflane.superluminal2.components.enums.Systems;
import com.kartoflane.superluminal2.components.interfaces.Disposable;
import com.kartoflane.superluminal2.components.interfaces.Follower;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.core.Grid;
import com.kartoflane.superluminal2.core.Grid.Snapmodes;
import com.kartoflane.superluminal2.core.LayeredPainter;
import com.kartoflane.superluminal2.core.LayeredPainter.Layers;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.events.SLAddEvent;
import com.kartoflane.superluminal2.events.SLEvent;
import com.kartoflane.superluminal2.events.SLListener;
import com.kartoflane.superluminal2.events.SLRemoveEvent;
import com.kartoflane.superluminal2.events.SLRestoreEvent;
import com.kartoflane.superluminal2.ftl.DoorObject;
import com.kartoflane.superluminal2.ftl.GameObject;
import com.kartoflane.superluminal2.ftl.GibObject;
import com.kartoflane.superluminal2.ftl.GlowObject;
import com.kartoflane.superluminal2.ftl.GlowSet;
import com.kartoflane.superluminal2.ftl.ImageObject;
import com.kartoflane.superluminal2.ftl.MountObject;
import com.kartoflane.superluminal2.ftl.RoomObject;
import com.kartoflane.superluminal2.ftl.ShipObject;
import com.kartoflane.superluminal2.ftl.SystemObject;
import com.kartoflane.superluminal2.ftl.WeaponObject;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.DoorController;
import com.kartoflane.superluminal2.mvc.controllers.GibController;
import com.kartoflane.superluminal2.mvc.controllers.GlowController;
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
import com.kartoflane.superluminal2.utils.IOUtils;
import com.kartoflane.superluminal2.utils.ShipSaveUtils;
import com.kartoflane.superluminal2.utils.UIUtils;
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
	private boolean stationsVisible = true;
	private volatile boolean animateGibs = false;

	private boolean shipSaved = false;
	private File saveDestination = null;

	private ShipController shipController = null;
	private GibPropContainer gibContainer = null;
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
				if (!isPlayerShip() && e.source instanceof RoomController) {
					ImageController hangar = getImageController(Images.HANGAR);
					Point size = findShipSize();
					hangar.setFollowOffset(size.x / 2, hangar.getFollowOffset().y);
				}
			}
		};

		addListener(SLEvent.RESTORE, OverviewWindow.getInstance());
		addListener(SLEvent.RESTORE, hangarListener);
	}

	public ShipContainer(EditorWindow window, ShipObject ship) {
		this();
		this.window = window;

		shipController = ShipController.newInstance(this, ship);
		gibContainer = new GibPropContainer();
		window.addListener(SLEvent.MOD_SHIFT, this);
		addListener(SLEvent.MOD_SHIFT, shipController);

		((CreationTool) Manager.getTool(Tools.CREATOR)).setEnabled(Tools.STATION, isPlayerShip());

		Grid grid = Grid.getInstance();

		for (RoomObject room : ship.getRooms()) {
			RoomController rc = RoomController.newInstance(this, room);

			int totalX = (ship.getXOffset() + room.getX() + room.getW() / 2) * CELL_SIZE;
			int totalY = (ship.getYOffset() + room.getY() + room.getH() / 2) * CELL_SIZE;

			rc.setSize(room.getW() * CELL_SIZE, room.getH() * CELL_SIZE);
			rc.setLocation(grid.snapToGrid(totalX, totalY, rc.getSnapMode()));
			rc.updateFollowOffset();

			add(rc);
			store(rc);
		}

		for (DoorObject door : ship.getDoors()) {
			DoorController dc = DoorController.newInstance(this, door);

			int totalX = (ship.getXOffset() + door.getX()) * CELL_SIZE;
			int totalY = (ship.getYOffset() + door.getY()) * CELL_SIZE;

			dc.setLocation(grid.snapToGrid(totalX, totalY, dc.getSnapMode()));
			dc.updateFollowOffset();

			add(dc);
			store(dc);
		}

		createImageControllers();
		ImageController hangarC = getImageController(Images.HANGAR);
		Point fo = hangarC.getFollowOffset();
		hangarC.setFollowOffset(fo.x - ship.getHorizontal(), fo.y - ship.getVertical());
		hangarC.updateFollower();

		// Gibs are not always listed in their order of appearance... Get by id instead
		// of iterating over array.
		GibObject gib = null;
		int i = ship.getGibs().length;
		// Gibs need to be added in reverse order for correct layering
		while ((gib = ship.getGibById(i)) != null) {
			GibController gc = GibController.newInstance(this, gib);

			// Calculate offset relative to the ship's anchor to prevent rounding errors from occuring
			Point offset = ship.getHullOffset();
			offset.x += ship.getXOffset() * CELL_SIZE + gc.getSize().x / 2 + gib.getOffsetX();
			offset.y += ship.getYOffset() * CELL_SIZE + gc.getSize().y / 2 + gib.getOffsetY();
			gc.setFollowOffset(offset);
			gc.updateFollower();

			gc.setParent(getImageController(Images.HULL));
			gc.updateFollowOffset();

			add(gc);
			store(gc);
			i--;
		}

		Point hullOffset = ship.getHullOffset();
		for (MountObject mount : ship.getMounts()) {
			MountController mc = MountController.newInstance(this, mount);

			int totalX = ship.getXOffset() * CELL_SIZE + mount.getX() + hullOffset.x;
			int totalY = ship.getYOffset() * CELL_SIZE + mount.getY() + hullOffset.y;

			mc.setFollowOffset(totalX, totalY);
			mc.updateFollower();

			mc.setParent(getImageController(Images.HULL));
			mc.updateFollowOffset();

			add(mc);
			store(mc);
		}

		// Instantiate first, assign later
		for (Systems sys : Systems.values()) {
			for (SystemObject system : ship.getSystems(sys)) {
				SystemController systemC = SystemController.newInstance(this, system);

				add(systemC);
				store(systemC);
				if (sys.canContainStation()) {
					StationController sc = StationController.newInstance(this, systemC, system.getStation());
					if (isPlayerShip() && sys.canContainGlow()) {
						GlowController gc = GlowController.newInstance(sc, system.getGlow());
						add(gc);
						store(gc);
					}

					add(sc);
					store(sc);
				}
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

		updateMounts();
		updateBoundingArea();
		updateChildBoundingAreas();

		// Mark the ship as saved
		shipSaved = true;
	}

	public EditorWindow getParent() {
		return window;
	}

	public GibPropContainer getGibContainer() {
		return gibContainer;
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

	/**
	 * Posts the edit passed in argument to the UndoManager, and flags the ship as not saved.
	 * 
	 * @param aue
	 *            the undoable edit to be posted. Must not be null.
	 */
	public void postEdit(AbstractUndoableEdit aue) {
		if (aue == null)
			throw new IllegalArgumentException("Argument must not be null.");
		shipSaved = false;
		Manager.postEdit(aue);
	}

	/**
	 * Saves the ship at the location supplied in argument.<br>
	 * <br>
	 * Saving method depends on the argument:<br>
	 * - if the argument is a directory, the ship is saved as a resource folder (ie. creates "data" and
	 * "img" folders in the directory passed in argument)
	 * - if the argument is a file, the ship is saved as a zip archive
	 * 
	 * @param f
	 *            the file the ship is to be saved as, or the directory in which it is to be saved
	 */
	public void save(File f) {
		if (f == null)
			throw new IllegalStateException("Save destination must not be null.");

		saveDestination = f;

		if (saveDestination.isDirectory()) {
			EditorWindow.log.trace("Saving ship to " + saveDestination.getAbsolutePath());

			try {
				ShipSaveUtils.saveShipXML(saveDestination, this);
				shipSaved = true;
				EditorWindow.log.trace("Ship saved successfully.");
			} catch (Exception ex) {
				EditorWindow.log.error("An error occured while saving the ship: ", ex);
				UIUtils.showWarningDialog(window.getShell(), null, "An error has occured while saving the ship:\n" + ex.getMessage() + "\n\nCheck log for details.");
			}
		} else {
			EditorWindow.log.trace("Saving ship as " + saveDestination.getAbsolutePath());

			try {
				ShipSaveUtils.saveShipFTL(saveDestination, this);
				shipSaved = true;
				EditorWindow.log.trace("Ship saved successfully.");
			} catch (Exception ex) {
				EditorWindow.log.error("An error occured while saving the ship: ", ex);
				UIUtils.showWarningDialog(window.getShell(), null, "An error has occured while saving the ship:\n" + ex.getMessage() + "\n\nCheck log for details.");
			}
		}
	}

	public File getSaveDestination() {
		return saveDestination;
	}

	public boolean isSaved() {
		return shipSaved;
	}

	public boolean isPlayerShip() {
		return shipController.isPlayerShip();
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

	public GibController getGibControllerById(int id) {
		for (GibController gc : gibControllers) {
			if (gc.getGameObject().getId() == id)
				return gc;
		}
		return null;
	}

	public StationController getStationController(Systems systemId) {
		for (SystemController sys : systemControllers) {
			if (sys.getSystemId() == systemId)
				return (StationController) getController(sys.getGameObject().getStation());
		}

		return null;
	}

	/**
	 * @return the controller associated with the object passed in argument, or null if not found.
	 */
	public AbstractController getController(GameObject object) {
		return objectControllerMap.get(object);
	}

	public void setCloakedAppearance(boolean cloak) {
		ImageController cloakC = getImageController(Images.CLOAK);
		ImageController hullC = getImageController(Images.HULL);

		cloakC.setVisible(cloak);
		hullC.setAlpha(cloak ? 255 / 3 : 255);
		hullC.redraw();
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

	/**
	 * Calculates the optimal values for the X_OFFSET and Y_OFFSET ship layout properties,
	 * so that the ship will be centered in the game screen.
	 * 
	 * @return point containing the calculated values; x holds x_offset, y holds y_offset
	 */
	public Point findOptimalThickOffset() {
		if (roomControllers.size() == 0)
			return new Point(0, 0);

		Point result = new Point(0, 0);
		Point size = findShipSize();

		if (isPlayerShip()) {
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

	/**
	 * Calculates the optimal values for the HORIZONTAL and VERTICAL ship layout properties,
	 * so that the ship will be centered in the game screen.
	 * 
	 * @return point containing the calculated values; x holds horizontal, y holds vertical
	 */
	public Point findOptimalFineOffset() {
		if (roomControllers.size() == 0)
			return new Point(0, 0);

		Point result = new Point(0, 0);
		Point size = findShipSize();

		if (isPlayerShip()) {
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

	public Point getShipOffset() {
		ShipObject ship = shipController.getGameObject();
		return ship.getOffsetThick();
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

	public Point getShipFineOffset() {
		ShipObject ship = shipController.getGameObject();
		return ship.getOffsetFine();
	}

	public void generateFloorImage() {
		ShipObject ship = shipController.getGameObject();

		// Prepare the ship data
		updateGameObjects();
		ship.coalesceRooms();

		// Remember door links and recover them later -- linking doors automatically persists after saving
		// is completed, which can cause bugs when the user moves the doors/rooms around and saves again
		HashMap<DoorObject, Tuple<RoomObject, RoomObject>> doorLinkMap = new HashMap<DoorObject, Tuple<RoomObject, RoomObject>>();
		for (DoorObject d : ship.getDoors())
			doorLinkMap.put(d, new Tuple<RoomObject, RoomObject>(d.getLeftRoom(), d.getRightRoom()));
		ship.linkDoors();

		// Generate the image
		FloorImageFactory fif = new FloorImageFactory();
		String content = ShipSaveUtils.generateLayoutTXT(ship);
		InputStream is = null;

		try {
			is = IOUtils.encodeText(content, "UTF-8", null);
			BufferedImage floorImage = fif.generateFloorImage(is);
			// Save the image to a file, since that's how the editor handles images...
			ImageIO.write(floorImage, "PNG", new File("floor_image.png"));

			setImage(Images.FLOOR, null);
			setImage(Images.FLOOR, "file:floor_image.png");
			ImageController floorC = getImageController(Images.FLOOR);
			floorC.setVisible(true);
		} catch (Exception ex) {
			StringBuilder buf = new StringBuilder();
			buf.append(Superluminal.APP_NAME);
			buf.append(" has encountered an error while generating the floor image:\n\n");
			buf.append(ex.getMessage());
			buf.append("\n\nCheck log for details.");

			Superluminal.log.warn("Error while generating floor image:", ex);
			UIUtils.showWarningDialog(null, null, buf.toString());
		} finally {
			// Recover door links
			for (DoorObject d : ship.getDoors()) {
				d.setLeftRoom(doorLinkMap.get(d).getKey());
				d.setRightRoom(doorLinkMap.get(d).getValue());
			}

			try {
				if (is != null)
					is.close();
			} catch (IOException ex) {
			}
		}
	}

	public void assign(SystemObject sys, RoomController room) {
		if (sys == null)
			throw new IllegalArgumentException("System id must not be null.");
		if (room == null)
			throw new IllegalArgumentException("Room controller is null. Use unassign() instead.");

		SystemController system = (SystemController) getController(sys);

		unassign(sys);
		system.assignTo(room.getGameObject());
		system.reposition(room.getX(), room.getY());
		system.setParent(room);

		setActiveSystem(room.getGameObject(), sys);
		system.notifySizeChanged(room.getW(), room.getH());

		if (isPlayerShip() && system.canContainGlow() && sys.getGlow() == Database.DEFAULT_GLOW_OBJ) {
			Database db = Database.getInstance();
			String glow = sys.getSystemId().getDefaultInteriorNamespace();

			if (system.getSystemId() == Systems.CLOAKING) {
				GlowSet glowSet = db.getGlowSet(glow);
				sys.getGlow().setGlowSet(glowSet);
			} else {
				glow = glow.replace("room_", "");
				GlowObject glowObject = db.getGlow(glow);
				if (glowObject != null) {
					sys.setGlow(glowObject);
				}
			}
		}

		if (isPlayerShip() && system.canContainGlow() && system.canContainStation()) {
			GlowController glow = (GlowController) getController(sys.getGlow());
			glow.applyGlowSettings();
		}

		if (system.getSystemId() == Systems.ARTILLERY)
			updateMounts();
	}

	public void unassign(SystemObject system) {
		if (system == null)
			throw new IllegalArgumentException("System must not be null.");

		SystemController systemC = (SystemController) getController(system);
		RoomObject room = system.getRoom();

		if (activeSystemMap.get(room) == system) {
			ArrayList<SystemObject> systems = getAllAssignedSystems(room);
			if (systems.size() > 0)
				setActiveSystem(room, systems.get(0));
			else
				activeSystemMap.remove(room);
		}

		systemC.unassign();

		if (system.getSystemId() == Systems.ARTILLERY)
			updateMounts();
	}

	public boolean isAssigned(Systems sys) {
		ShipObject ship = shipController.getGameObject();
		for (SystemObject system : ship.getSystems(sys)) {
			if (system.isAssigned())
				return true;
		}
		return false;
	}

	public void add(AbstractController controller, int index) {
		if (index < 0) {
			add(controller);
			return;
		}

		if (controller instanceof RoomController) {
			if (index >= roomControllers.size()) {
				add(controller);
				return;
			}
			RoomController room = (RoomController) controller;
			room.setId(index);
			roomControllers.add(index, room);
			shipController.getGameObject().add(room.getGameObject());

			room.addListener(SLEvent.MOVE, hangarListener);
			room.addListener(SLEvent.RESIZE, hangarListener);
			room.addListener(SLEvent.DELETE, hangarListener);

		} else if (controller instanceof DoorController) {
			if (index >= doorControllers.size()) {
				add(controller);
				return;
			}
			DoorController door = (DoorController) controller;
			doorControllers.add(index, door);
			shipController.getGameObject().add(door.getGameObject());

		} else if (controller instanceof MountController) {
			if (index >= mountControllers.size()) {
				add(controller);
				return;
			}
			MountController mount = (MountController) controller;
			mount.setId(index);
			mountControllers.add(index, mount);
			shipController.getGameObject().add(mount.getGameObject());
			updateMounts();

		} else if (controller instanceof GibController) {
			if (index >= gibControllers.size()) {
				add(controller);
				return;
			}
			GibController gib = (GibController) controller;
			gib.setId(index);
			gibControllers.add(index, gib);
			shipController.getGameObject().add(gib.getGameObject());
			sort();
		}

		if (controller instanceof ObjectController) {
			eventHandler.sendEvent(new SLAddEvent(this, controller));
		}

		addListener(SLEvent.MOD_SHIFT, controller);
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
			sort();
		} else if (controller instanceof SystemController) {
			SystemController system = (SystemController) controller;
			systemControllers.add(system);
		}

		if (controller instanceof ObjectController) {
			eventHandler.sendEvent(new SLAddEvent(this, controller));
		}

		addListener(SLEvent.MOD_SHIFT, controller);
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
			eventHandler.sendEvent(new SLRemoveEvent(this, controller));
		}

		removeListener(SLEvent.MOD_SHIFT, controller);
	}

	public void store(AbstractController controller) {
		if (controller instanceof ObjectController)
			objectControllerMap.put(((ObjectController) controller).getGameObject(), controller);
	}

	public void dispose(AbstractController controller) {
		if (controller instanceof ObjectController)
			objectControllerMap.remove(((ObjectController) controller).getGameObject());
		remove(controller);
		controller.dispose();
	}

	public void sort() {
		int r = roomControllers.hashCode();
		int m = mountControllers.hashCode();
		int g = gibControllers.hashCode();

		Collections.sort(roomControllers);
		Collections.sort(mountControllers);
		Collections.sort(gibControllers);

		shipController.getGameObject().sort();

		// Reinsert controllers into the painter so that they're drawn in the correct order

		// Compare hash codes to determine whether the collections have changed
		if (r != roomControllers.hashCode()) {
			for (RoomController c : roomControllers)
				c.removeFromPainter();
			for (RoomController c : roomControllers)
				c.addToPainter(Layers.ROOM);
		}
		if (m != mountControllers.hashCode()) {
			for (MountController c : mountControllers)
				c.removeFromPainter();
			for (MountController c : mountControllers)
				c.addToPainter(Layers.MOUNT);
		}
		if (g != gibControllers.hashCode()) {
			for (GibController c : gibControllers)
				c.removeFromPainter();
			for (GibController c : gibControllers)
				c.addToPainterBottom(Layers.GIBS);
		}
		window.canvasRedraw();
	}

	public void setImage(Images imageType, String path) {
		ImageController image = imageControllerMap.get(imageType);

		if (imageType == Images.THUMBNAIL) { // Thumbnail has no visual representation
			ImageObject object = image.getGameObject();
			object.setImagePath(path);
		} else {
			boolean vis = getParent().isImageDrawn(imageType);
			image.setVisible(false);
			image.setImage(path);
			image.updateView();
			image.setVisible(path != null && vis);
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
		ShipObject ship = shipController.getGameObject();
		WeaponObject[] weapons = ship.getWeapons();
		ArrayList<SystemObject> artilleries = ship.getSystems(Systems.ARTILLERY);
		int slots = ship.getWeaponSlots();

		for (MountController mount : mountControllers) {
			mount.setId(i);
			if (i < slots) {
				mount.setWeapon(weapons[i]);
			} else {
				// Artillery starts taking up mounts after the weapons, or 4th slot if the
				// ship has less than 4 slots. As such, a dummy mount is required
				int j = i - Math.max(4, slots);
				if (j >= 0 && j < artilleries.size() && artilleries.get(j).isAssigned()) {
					mount.setWeapon(artilleries.get(j).getWeapon());
				} else {
					mount.setWeapon(Database.DEFAULT_WEAPON_OBJ);
				}
			}
			i++;
		}
	}

	/**
	 * Deletes the controller and posts an undoable edit.
	 */
	public void delete(AbstractController controller) {
		if (controller == null)
			throw new IllegalArgumentException("Argument must not be null.");
		if (!controller.isDeletable())
			throw new NotDeletableException();

		controller.delete();
		remove(controller);
		updateBoundingArea();
	}

	/**
	 * Restores the deleted controller.
	 */
	public void restore(AbstractController controller, int index) {
		if (controller == null)
			throw new IllegalArgumentException("Argument must not be null.");
		if (!controller.isDeletable())
			throw new NotDeletableException();
		if (!controller.isDeleted())
			throw new IllegalArgumentException("The controller has not been deleted.");

		controller.restore();
		add(controller, index);
		updateBoundingArea();

		if (eventHandler.hooks(SLEvent.RESTORE))
			eventHandler.sendEvent(new SLRestoreEvent(controller));
	}

	public void dispose() throws NotDeletableException {
		if (isDisposed())
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
		gibContainer.dispose();
		shipController.dispose();
		shipController = null;

		eventHandler.dispose();

		System.gc();
	}

	public boolean isDisposed() {
		return shipController == null;
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
		imgObject.setImagePath(isPlayerShip() ? HANGAR_IMG_PATH : ENEMY_IMG_PATH);
		final ImageController hangar = ImageController.newInstance(shipController, imgObject);
		hangar.setSelectable(false);
		hangar.removeFromPainter();
		hangar.addToPainter(Layers.BACKGROUND);
		hangar.setImage(imgObject.getImagePath());
		offset = hangar.getSize();
		// For FTL hangar image: 365, 30
		// For SL2 hangar image: 94, 56
		// For enemy window image: 193, 239
		if (isPlayerShip()) {
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
		if (!isPlayerShip()) {
			shield.setSize(ship.getEllipseWidth() * 2, ship.getEllipseHeight() * 2);
		}
		shield.updateView();

		imageControllerMap.put(Images.SHIELD, shield);
		add(shield);
		store(shield);
		shield.setBounded(true);

		if (!isPlayerShip()) {
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
		store(hull);
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
		store(cloak);
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
		store(floor);
		floor.setBounded(true);

		// Load thumbnail
		imgObject = ship.getImage(Images.THUMBNAIL);
		ImageController thumbnail = ImageController.newInstance(shipController, imgObject);
		thumbnail.setImage(imgObject.getImagePath());

		imageControllerMap.put(Images.THUMBNAIL, thumbnail);
		add(thumbnail);
		store(thumbnail);
		thumbnail.setVisible(false);
	}

	public void setHangarVisible(boolean vis) {
		getImageController(Images.HANGAR).setVisible(vis);
	}

	public boolean isHangarVisible() {
		return getImageController(Images.HANGAR).isVisible();
	}

	public void setAnchorVisible(boolean vis) {
		if (shipController.isSelected())
			Manager.setSelected(null);
		anchorVisible = vis;
		shipController.setVisible(vis);
	}

	public boolean isAnchorVisible() {
		return anchorVisible;
	}

	public void setRoomsVisible(boolean vis) {
		if (Manager.getSelected() != null && Manager.getSelected() instanceof RoomController)
			Manager.setSelected(null);
		LayeredPainter painter = LayeredPainter.getInstance();
		painter.setLayerDrawn(Layers.ROOM, vis);
		painter.setLayerDrawn(Layers.SYSTEM, vis);
		painter.setLayerDrawn(Layers.STATION, vis && isStationsVisible());
		painter.setLayerDrawn(Layers.GLOW, vis && isStationsVisible());
		window.canvasRedraw();
	}

	public boolean isRoomsVisible() {
		return LayeredPainter.getInstance().isLayerDrawn(Layers.ROOM);
	}

	public void setDoorsVisible(boolean vis) {
		if (Manager.getSelected() != null && Manager.getSelected() instanceof DoorController)
			Manager.setSelected(null);
		LayeredPainter.getInstance().setLayerDrawn(Layers.DOOR, vis);
		window.canvasRedraw();
	}

	public boolean isDoorsVisible() {
		return LayeredPainter.getInstance().isLayerDrawn(Layers.DOOR);
	}

	public void setMountsVisible(boolean vis) {
		if (Manager.getSelected() != null && Manager.getSelected() instanceof MountController)
			Manager.setSelected(null);
		LayeredPainter.getInstance().setLayerDrawn(Layers.MOUNT, vis);
		window.canvasRedraw();
	}

	public boolean isMountsVisible() {
		return LayeredPainter.getInstance().isLayerDrawn(Layers.MOUNT);
	}

	public void setStationsVisible(boolean vis) {
		stationsVisible = vis;
		LayeredPainter.getInstance().setLayerDrawn(Layers.STATION, vis && isRoomsVisible());
		LayeredPainter.getInstance().setLayerDrawn(Layers.GLOW, vis && isRoomsVisible());
		window.canvasRedraw();
	}

	public boolean isStationsVisible() {
		return stationsVisible;
	}

	public void setGibsVisible(boolean vis) {
		if (Manager.getSelected() != null && Manager.getSelected() instanceof GibController)
			Manager.setSelected(null);
		LayeredPainter.getInstance().setLayerDrawn(Layers.GIBS, vis);
		window.canvasRedraw();
	}

	public boolean isGibsVisible() {
		return LayeredPainter.getInstance().isLayerDrawn(Layers.GIBS);
	}

	public void setActiveSystem(RoomObject room, SystemObject newSystem) {
		if (room == null)
			throw new IllegalArgumentException("Room must not be null.");
		if (newSystem == null)
			throw new IllegalArgumentException("System must not be null.");

		RoomController roomC = (RoomController) getController(room);
		SystemController newSystemC = (SystemController) getController(newSystem);

		SystemObject prevSystem = getActiveSystem(room);
		SystemController prevSystemC = (SystemController) getController(prevSystem);

		if (newSystemC == prevSystemC)
			return; // They're the same, nothing to do

		prevSystemC.setVisible(false);
		newSystemC.setVisible(true);

		roomC.removeListener(SLEvent.VISIBLE, prevSystemC);
		roomC.removeListener(SLEvent.RESIZE, prevSystemC);
		if (prevSystemC.canContainStation()) {
			StationController station = (StationController) getController(prevSystem.getStation());
			roomC.removeListener(SLEvent.RESIZE, station);
		}

		activeSystemMap.put(room, newSystem);

		roomC.addListener(SLEvent.VISIBLE, newSystemC);
		roomC.addListener(SLEvent.RESIZE, newSystemC);
		if (newSystemC.canContainStation()) {
			StationController station = (StationController) getController(newSystem.getStation());
			if (!isPlayerShip())
				station.setSlotId(-2);
			roomC.addListener(SLEvent.RESIZE, station);
			station.updateFollowOffset();
			station.updateFollower();
		}
	}

	public SystemObject getActiveSystem(RoomObject room) {
		if (activeSystemMap.containsKey(room)) {
			SystemObject result = activeSystemMap.get(room);
			if (result != null && result.isAssigned() && result.getRoom() == room)
				return result;
		}
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

		SystemObject active = getActiveSystem(room);
		if (active.getSystemId() != Systems.EMPTY) {
			systems.remove(active);
			systems.add(active);
		}

		return systems;
	}

	public void addListener(int eventType, SLListener listener) {
		eventHandler.hook(eventType, listener);
	}

	public void removeListener(int eventType, SLListener listener) {
		eventHandler.unhook(eventType, listener);
	}

	public void removeListener(SLListener listener) {
		eventHandler.unhook(listener);
	}

	@Override
	public void handleEvent(SLEvent e) {
		// Send the event over to controllers
		eventHandler.sendEvent(e);
	}

	/**
	 * Initiates gib animation, while also putting the editor in a non-interactable state.<br>
	 * Does nothing if gib animation is currently in progress.
	 */
	public void triggerGibAnimation() {
		if (!isGibAnimationInProgress()) {
			if (gibControllers.size() == 0) {
				UIUtils.showInfoDialog(null, null, "Unable to animte gibs because the ship has no gibs.");
				return;
			}

			animateGibs = true;
			window.updateGibAnimationButton(false);
			window.setInteractable(false);
			animateGibs();
		}
	}

	/**
	 * Cancels the gib animation.
	 */
	public void stopGibAnimation() {
		animateGibs = false;
		window.updateGibAnimationButton(true);
		window.setInteractable(true);
	}

	public boolean isGibAnimationInProgress() {
		return animateGibs;
	}

	/**
	 * Animates gibs and weapon mounts attached to them, trying to mimic how the animation will look in-game.<br>
	 * 
	 * Animation is executed in a separate thread, leaving GUI responsive.
	 */
	private void animateGibs() {
		// Stores time that has elapsed since the animation started, in ms
		final int[] elapsed = new int[1];
		// Time between animation frames, in ms
		final int interval = 20;
		// Total time the animation is supposed to last, in ms
		final int animTime = Database.GIB_DEATH_ANIM_TIME * 1000;

		// Map to store the gib's index for use with the arrays defined below
		// Sure there's indexOf(), but it'd have to be ran multiple times every interval
		final HashMap<GibController, Integer> gMap = new HashMap<GibController, Integer>();
		// Store mounts that are to be animated (ie. ones that are linked to a gib)
		final ArrayList<MountController> animMounts = new ArrayList<MountController>();
		// Remember which mounts were shown and hidden (since unlinked mounts are hidden during animation)
		final boolean[] mVis = new boolean[mountControllers.size()];

		// Mounts that are not linked to a gib are hidden and not animated
		for (int i = 0; i < mountControllers.size(); i++) {
			MountController mc = mountControllers.get(i);
			boolean visible = mc.getGib() != Database.DEFAULT_GIB_OBJ;
			if (visible)
				animMounts.add(mc);
			mVis[i] = mc.isVisible();
			mc.setVisible(mVis[i] && visible);
			mc.getProp(MountController.ARROW_PROP_ID).setVisible(false);
		}

		final int gSize = gibControllers.size();
		final int mSize = animMounts.size();

		// Select direction in which the gib will float
		final int[] direction = new int[gSize];
		// Select linear velocity
		final double[] linear = new double[gSize];
		// Select angular velocity, in degrees
		final double[] angular = new double[gSize];
		// Remember old locations
		final Point[] gLocations = new Point[gSize];
		final Point[] mLocations = new Point[mSize];
		// Distance between mount and its linked gib
		final int[] distance = new int[mSize];
		// Angle between the mount and its linked gib
		final double[] angles = new double[mSize];

		// Gather data from gibs
		for (int i = 0; i < gSize; i++) {
			GibController gc = gibControllers.get(i);
			gMap.put(gc, i);
			gLocations[i] = gc.getLocation();
			direction[i] = Utils.convertAngle(Utils.random(gc.getDirectionMin(), gc.getDirectionMax()));
			linear[i] = Utils.random(gc.getLinearVelocityMin(), gc.getLinearVelocityMax()) * Database.GIB_LINEAR_SPEED;
			angular[i] = Utils.random(gc.getAngularVelocityMin(), gc.getAngularVelocityMax()) * Math.toDegrees(Database.GIB_ANGULAR_SPEED);
		}

		// Gather data from mounts
		for (int i = 0; i < mSize; i++) {
			MountController mc = animMounts.get(i);
			Point p = getController(mc.getGib()).getLocation();
			mLocations[i] = mc.getLocation();
			distance[i] = Utils.distance(mLocations[i], p);
			angles[i] = Utils.convertAngle(Utils.angle(p, mLocations[i]));
		}

		final Display display = UIUtils.getDisplay();
		Runnable animateRun = new Runnable() {
			public void run() {
				// Prevent the editor from crashing if the user exits the application while animation is in progress
				if (window.getShell().isDisposed())
					return;

				// Animate gibs
				for (int i = 0; i < gSize; i++) {
					GibController gc = gibControllers.get(i);
					int x = gLocations[i].x + (int) Math.round(linear[i] * (elapsed[0] / 1000f) * Math.cos(Math.toRadians(direction[i])));
					int y = gLocations[i].y + (int) Math.round(linear[i] * (elapsed[0] / 1000f) * Math.sin(Math.toRadians(direction[i])));
					gc.setVisible(false);
					gc.setRotation((float) angular[i] * (elapsed[0] / 1000f));
					gc.setLocation(x, y);
					gc.setVisible(true);
				}

				// Animate mounts
				for (int i = 0; i < mSize; i++) {
					MountController mc = animMounts.get(i);
					GibController gc = (GibController) getController(mc.getGib());
					int j = gMap.get(gc);
					float ang = (float) angular[j] * (elapsed[0] / 1000f);
					Point p = Utils.polar(gc.getLocation(), Math.toRadians(angles[i] + ang), distance[i]);
					mc.setVisible(false);
					mc.setRotation((mc.isRotated() ? 90 : 0) + ang);
					mc.setLocation(p);
					mc.setVisible(true);
				}

				elapsed[0] += interval;
				// User can cancel animation via button, which modifies the animateGibs var, so check for that too
				animateGibs = elapsed[0] < animTime && animateGibs;

				if (animateGibs) {
					// If animation is supposed to continue, run the runnable again
					display.timerExec(interval, this);
				} else {
					// Restore data
					for (int i = 0; i < gSize; i++) {
						GibController gc = gibControllers.get(i);
						gc.setRotation(0);
						gc.reposition(gLocations[i]);
					}
					for (int i = 0; i < mountControllers.size(); i++) {
						MountController mc = mountControllers.get(i);
						mc.setVisible(mVis[i]);
						mc.getProp(MountController.ARROW_PROP_ID).setVisible(mVis[i]);
					}
					for (int i = 0; i < mSize; i++) {
						MountController mc = animMounts.get(i);
						mc.setRotation(mc.isRotated() ? 90 : 0);
						mc.reposition(mLocations[i]);
					}
					stopGibAnimation();
				}
			}
		};
		display.timerExec(interval, animateRun);
	}
}
