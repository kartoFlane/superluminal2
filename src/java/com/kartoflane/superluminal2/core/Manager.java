package com.kartoflane.superluminal2.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.components.Hotkey;
import com.kartoflane.superluminal2.components.enums.Hotkeys;
import com.kartoflane.superluminal2.components.interfaces.Deletable;
import com.kartoflane.superluminal2.ftl.ShipObject;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;
import com.kartoflane.superluminal2.tools.Tool;
import com.kartoflane.superluminal2.tools.Tool.Tools;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.OverviewWindow;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.utils.IOUtils;

/**
 * Manager class to manage the current ship, interface flags, selection.
 * 
 * @author kartoFlane
 * 
 */
public class Manager {
	private static final Logger log = LogManager.getLogger(Manager.class);

	/**
	 * A regex pattern which matches the path to the file, along with its extension.
	 * For example, in 'C:/path/file.ext/inner/path' matches 'C:/path/file.ext'<br>
	 * <br>
	 * To be specific, matches all strings that meet the following conditions:<br>
	 * - Starts with a non-zero number of any characters<br>
	 * - Those characters then have to be terminated by a period<br>
	 * - After that, a non-zero number of any characters other than a forward slash
	 */
	private static final Pattern FILE_PTRN = Pattern.compile(".+\\.[^/]+");

	public static final HashMap<Tools, Tool> TOOL_MAP = new HashMap<Tools, Tool>();
	public static final LinkedList<Deletable> DELETED_LIST = new LinkedList<Deletable>();
	public static final HashMap<Hotkeys, Hotkey> HOTKEY_MAP = new HashMap<Hotkeys, Hotkey>();

	// Config variables
	public static boolean sidebarOnRightSide = true;
	public static boolean rememberGeometry = true;
	public static boolean startMaximised = false;
	public static boolean closeLoader = false;
	public static boolean checkUpdates = true;
	public static Point windowSize = null;
	public static String resourcePath = "";
	public static boolean allowRoomOverlap = false;
	public static boolean resetDoorLinksOnMove = false;
	public static boolean shownSlotWarning = false;

	// Runtime variables
	public static boolean leftMouseDown = false;
	public static boolean rightMouseDown = false;
	public static boolean modShift = false;
	public static boolean modAlt = false;
	public static boolean modCtrl = false;

	private static AbstractController selectedController;
	private static ShipContainer currentShip = null;
	private static Tools selectedTool = null;

	/**
	 * Selects the given box, deselecting the previous one if it was already selected.<br>
	 * 
	 * @param box
	 *            the box that is to be selected. Can be null to clear selection.
	 */
	public static void setSelected(AbstractController controller) {
		if (controller != null && !controller.isSelectable())
			throw new IllegalArgumentException("Controller is not selectable.");

		if (selectedController != null)
			selectedController.deselect();
		selectedController = null;

		if (controller != null) {
			controller.select();
			selectedController = controller;
		}

		try {
			EditorWindow.getInstance().setSidebarContentController(controller);
		} catch (UnsupportedOperationException e) {
			// Ignore
		}
	}

	/** Returns the currently selected controller. */
	public static AbstractController getSelected() {
		return selectedController;
	}

	public static void createNewShip(boolean playerShip) {
		EditorWindow window = EditorWindow.getInstance();
		closeShip();

		currentShip = new ShipContainer(window, new ShipObject(playerShip));
		currentShip.getShipController().reposition(3 * ShipContainer.CELL_SIZE, 3 * ShipContainer.CELL_SIZE);

		window.enableTools(true);
		window.enableOptions(true);
		window.setVisibilityOptions(true);

		// select the manipulation tool by default
		selectTool(Tools.IMAGES);
		OverviewWindow.staticUpdate();
	}

	public static void loadShip(ShipObject ship) {
		EditorWindow window = EditorWindow.getInstance();
		closeShip();

		currentShip = new ShipContainer(window, ship);
		ShipController sc = currentShip.getShipController();
		// Selecting the anchor makes children non-collidable
		// Prevents rooms and doors from bugging out when repositioning
		sc.select();
		sc.reposition(3 * ShipContainer.CELL_SIZE, 3 * ShipContainer.CELL_SIZE);
		sc.deselect();

		currentShip.updateBoundingArea();
		currentShip.updateChildBoundingAreas();

		window.enableTools(true);
		window.enableOptions(true);
		window.setVisibilityOptions(true);

		// select the manipulation tool by default
		selectTool(Tools.IMAGES);
		OverviewWindow.staticUpdate();
	}

	public static void closeShip() {
		if (currentShip == null)
			return;

		if (currentShip.isSaved()) {
			closeShipForce();
		} else {
			closeShipForce();
			// TODO UI prompts and shit
		}
	}

	public static void closeShipForce() {
		EditorWindow window = EditorWindow.getInstance();
		setSelected(null);

		if (currentShip != null)
			currentShip.dispose();

		currentShip = null;

		window.enableTools(false);
		window.enableOptions(false);
		window.canvasRedraw();
		selectTool(null);
		OverviewWindow.staticUpdate();
	}

	/** Returns the currently loaded ship. */
	public static ShipContainer getCurrentShip() {
		return currentShip;
	}

	/**
	 * Select the given tool item, also triggering the tools' {@link Tool#select() select()} and {@link Tool#deselect() deselect()} methods, as needed.
	 */
	public static void selectTool(Tools tool) {
		// Deny trying to select the same tool twice
		if (selectedTool != null && selectedTool == tool)
			return;

		EditorWindow window = EditorWindow.getInstance();

		if (selectedTool != null)
			TOOL_MAP.get(selectedTool).deselect();

		selectedTool = tool;
		if (tool != null) {
			MouseInputDispatcher.getInstance().setCurrentTool(TOOL_MAP.get(tool));
			window.selectTool(tool);
			TOOL_MAP.get(tool).select();
		} else {
			MouseInputDispatcher.getInstance().setCurrentTool(null);
			window.selectTool(null);
			window.disposeSidebarContent();
		}
	}

	public static Tools getSelectedToolId() {
		return selectedTool;
	}

	public static Tool getSelectedTool() {
		return TOOL_MAP.get(selectedTool);
	}

	public static Tool getTool(Tools tool) {
		return TOOL_MAP.get(tool);
	}

	public static Hotkey getHotkey(Hotkeys key) {
		return HOTKEY_MAP.get(key);
	}

	/** Loads the default hotkey values, which are later overridden by config or the user. */
	public static void loadDefaultHotkeys() {
		Hotkey hotkey = null;

		// Tool hotkeys
		getHotkey(Hotkeys.POINTER_TOOL).setKey('q');
		getHotkey(Hotkeys.CREATE_TOOL).setKey('w');
		getHotkey(Hotkeys.IMAGES_TOOL).setKey('e');
		getHotkey(Hotkeys.PROPERTIES_TOOL).setKey('r');
		getHotkey(Hotkeys.ROOM_TOOL).setKey('a');
		getHotkey(Hotkeys.DOOR_TOOL).setKey('s');
		getHotkey(Hotkeys.MOUNT_TOOL).setKey('d');
		getHotkey(Hotkeys.STATION_TOOL).setKey('f');
		getHotkey(Hotkeys.OVERVIEW_TOOL).setKey('o');

		// Command hotkeys
		getHotkey(Hotkeys.PIN).setKey(' ');

		hotkey = getHotkey(Hotkeys.DELETE);
		hotkey.setKey('d');
		hotkey.setShift(true);

		hotkey = getHotkey(Hotkeys.UNDO);
		hotkey.setKey('z');
		hotkey.setCtrl(true);

		hotkey = getHotkey(Hotkeys.REDO);
		hotkey.setKey('y');
		hotkey.setCtrl(true);

		hotkey = getHotkey(Hotkeys.NEW_SHIP);
		hotkey.setKey('n');
		hotkey.setCtrl(true);

		hotkey = getHotkey(Hotkeys.LOAD_SHIP);
		hotkey.setKey('l');
		hotkey.setCtrl(true);

		hotkey = getHotkey(Hotkeys.SAVE_SHIP);
		hotkey.setKey('s');
		hotkey.setCtrl(true);

		hotkey = getHotkey(Hotkeys.CLOSE_SHIP);
		hotkey.setKey('w');
		hotkey.setCtrl(true);

		hotkey = getHotkey(Hotkeys.MANAGE_MOD);
		hotkey.setKey('m');
		hotkey.setCtrl(true);

		hotkey = getHotkey(Hotkeys.SETTINGS);
		hotkey.setKey('o');
		hotkey.setCtrl(true);

		hotkey = getHotkey(Hotkeys.CLOAK);
		hotkey.setKey('c');
		hotkey.setShift(true);

		// View hotkeys
		getHotkey(Hotkeys.TOGGLE_GRID).setKey('g');
		getHotkey(Hotkeys.TOGGLE_HANGAR).setKey('h');
		getHotkey(Hotkeys.SHOW_ANCHOR).setKey('1');
		getHotkey(Hotkeys.SHOW_MOUNTS).setKey('2');
		getHotkey(Hotkeys.SHOW_ROOMS).setKey('3');
		getHotkey(Hotkeys.SHOW_DOORS).setKey('4');
		getHotkey(Hotkeys.SHOW_STATIONS).setKey('5');
		getHotkey(Hotkeys.SHOW_HULL).setKey('6');
		getHotkey(Hotkeys.SHOW_FLOOR).setKey('7');
		getHotkey(Hotkeys.SHOW_SHIELD).setKey('8');
	}

	/**
	 * Request an InputStream for the given path.<br>
	 * <br>
	 * All paths must have a protocol declared at their beginning, like so:
	 * 
	 * <pre>
	 * <tt>file:C:/example/absolute/path.txt</tt>
	 * </pre>
	 * 
	 * If a path is missing its protocol, or if it is mistyped, the image will not be loaded.<br>
	 * Protocols that this method recognizes:
	 * 
	 * <pre>
	 * <tt>file:    - for use when the resource is located in the OS' filesystem,
	 *              eg. an absolute or relative path
	 * cpath:   - for use when the resource is located inside the jar
	 *              eg. cpath:/assets/image.png
	 * db:      - for use when the resource is loaded in the database
	 *              eg. db:img/ship/kestral_base.png</tt>
	 * zip:     - for use when the resource is located inside an unloaded zip archive
	 *              eg. zip:/path/to/file.zip/inner/path/image.png
	 * </pre>
	 * 
	 * @param path
	 *            path to the requested file, preceded by protocol
	 * @return input stream for the given file, or null if not found
	 * 
	 * @throws FileNotFoundException
	 *             when there was no file under the specified path
	 * @throws IllegalArgumentException
	 *             when the path is null, is wrongly formatted, is missing protocol or uses an
	 *             unknown one, or when zip file path has no inner path
	 */
	public static InputStream getInputStream(String path) throws FileNotFoundException, IllegalArgumentException {
		if (path == null)
			throw new IllegalArgumentException("Path must not be null.");
		InputStream result = null;
		String protocol = IOUtils.getProtocol(path);
		String loadPath = IOUtils.trimProtocol(path);

		try {
			// Employ "protocols" to spare the Cache from having to guess where the file is located
			if (protocol.equals("db:")) {
				// Refers to file in database
				result = Database.getInstance().getInputStream(loadPath);
			} else if (protocol.equals("cpath:")) {
				// Refers to file in classpath
				result = Manager.class.getResourceAsStream(loadPath);
			} else if (protocol.equals("file:")) {
				// Refers to file in the OS' filesystem
				result = new FileInputStream(new File(loadPath));
			} else if (protocol.equals("zip:")) {
				// Refers to file in a zip archive
				Matcher m = FILE_PTRN.matcher(loadPath);
				if (m.find()) {
					String zipPath = m.group(1);
					String innerPath = loadPath.replace(zipPath + "/", "");

					if (innerPath == null || innerPath.equals(""))
						throw new IllegalArgumentException("Path doesn't have an inner path: " + path);

					try {
						ZipFile zf = new ZipFile(zipPath);
						ZipEntry ze = zf.getEntry(innerPath);
						if (ze == null)
							throw new IllegalArgumentException(String.format("Inner path '%s' was not found in archive '%s'", innerPath, zipPath));

						// Closing the ZipFile also closes all streams that it opened...
						// Copy the stream so that it's possible to access the file
						// without having to keep the archive open
						result = IOUtils.cloneStream(zf.getInputStream(ze));
						zf.close();
					} catch (ZipException e) {
						log.warn(String.format("File is not a zip archive: '%s'", zipPath));
					}
				} else {
					log.warn(String.format("Path was wrongly formatted: '%s'", loadPath));
				}
			} else {
				throw new IllegalArgumentException(String.format("Path uses unknown protocol, or doesn't have it: '%s'", path));
			}
		} catch (FileNotFoundException e) {
			log.warn(String.format("%s - resource could not be found.", path));
		} catch (IOException e) {
			log.error(String.format("An error has occured while getting input stream for %s: ", loadPath), e);
		}

		if (result == null)
			throw new FileNotFoundException("Could not find file: " + path);

		return result;
	}
}
