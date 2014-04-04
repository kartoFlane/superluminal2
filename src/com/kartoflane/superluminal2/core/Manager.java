package com.kartoflane.superluminal2.core;

import java.util.HashMap;
import java.util.LinkedList;

import com.kartoflane.superluminal2.components.interfaces.Deletable;
import com.kartoflane.superluminal2.ftl.ShipObject;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.tools.Tool;
import com.kartoflane.superluminal2.tools.Tool.Tools;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.ManipulationToolComposite;

/**
 * Manager class to manage the current ship, interface flags, selection.
 * 
 * @author kartoFlane
 * 
 */
public class Manager {
	public enum Hotkeys {
		POINTER_TOOL_KEY,
		CREATE_TOOL_KEY,
		GIB_TOOL_KEY,
		PROPERTIES_TOOL_KEY,
		OVERVIEW_TOOL_KEY,
		ROOM_TOOL_KEY,
		DOOR_TOOL_KEY,
		MOUNT_TOOL_KEY,
		STATION_TOOL_KEY
	}

	public static final HashMap<Tools, Tool> TOOL_MAP = new HashMap<Tools, Tool>();
	public static final LinkedList<Deletable> DELETED_LIST = new LinkedList<Deletable>();
	public static final HashMap<Hotkeys, Character> HOTKEY_MAP = new HashMap<Hotkeys, Character>();

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
			((ManipulationToolComposite) EditorWindow.getInstance().getSidebarContent()).setController(controller);
		} else
			((ManipulationToolComposite) EditorWindow.getInstance().getSidebarContent()).setController(null);
	}

	/** Returns the currently selected controller. */
	public static AbstractController getSelected() {
		return selectedController;
	}

	public static void createNewShip() {
		if (currentShip != null)
			currentShip.dispose();
		currentShip = new ShipContainer(new ShipObject(true)); // TODO choose if player ship
		currentShip.getShipController().reposition(2 * ShipContainer.CELL_SIZE, 2 * ShipContainer.CELL_SIZE);
	}

	public static void loadShip(ShipObject ship) {
		currentShip = new ShipContainer(ship);
		// TODO load the ship
		// currentShip = ship;
	}

	/** Returns the currently loaded ship. */
	public static ShipContainer getCurrentShip() {
		return currentShip;
	}

	/**
	 * Select the given tool item, also triggering the tools' {@link Tool#select() select()} and {@link Tool#deselect() deselect()} methods, as needed.
	 */
	public static void selectTool(Tools tool) {
		// deny trying to select the same tool twice
		if (selectedTool != null && selectedTool == tool)
			return;

		if (selectedTool != null)
			TOOL_MAP.get(selectedTool).deselect();

		selectedTool = tool;
		if (tool != null) {
			TOOL_MAP.get(tool).select();
			MouseInputDispatcher.getInstance().setCurrentTool(TOOL_MAP.get(selectedTool));
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
}
