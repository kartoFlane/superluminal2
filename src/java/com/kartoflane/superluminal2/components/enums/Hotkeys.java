package com.kartoflane.superluminal2.components.enums;

public enum Hotkeys {
	// Tools
	POINTER_TOOL,
	CREATE_TOOL,
	IMAGES_TOOL,
	PROPERTIES_TOOL,
	OVERVIEW_TOOL,
	ROOM_TOOL,
	DOOR_TOOL,
	MOUNT_TOOL,
	STATION_TOOL,

	// Commands
	DELETE,
	PIN,
	NEW_SHIP,
	LOAD_SHIP,
	SAVE_SHIP,
	SAVE_SHIP_AS,
	CLOSE_SHIP,
	LOAD_LEGACY,
	MANAGE_MOD,
	SETTINGS,
	UNDO,
	REDO,
	CLOAK,
	ANIMATE,

	// View
	OPEN_ZOOM,
	TOGGLE_GRID,
	TOGGLE_HANGAR,
	SHOW_ANCHOR,
	SHOW_MOUNTS,
	SHOW_ROOMS,
	SHOW_DOORS,
	SHOW_STATIONS,
	SHOW_HULL,
	SHOW_FLOOR,
	SHOW_SHIELD,
	SHOW_GIBS;

	@Override
	public String toString() {
		switch (this) {
			case POINTER_TOOL:
				return "Manipulation Tool";
			case CREATE_TOOL:
				return "Layout Creation Tool";
			case IMAGES_TOOL:
				return "Ship Images";
			case PROPERTIES_TOOL:
				return "Ship Loadout and Properties";
			case OVERVIEW_TOOL:
				return "Ship Overview";
			case ROOM_TOOL:
				return "Room Creation Tool";
			case DOOR_TOOL:
				return "Door Creation Tool";
			case MOUNT_TOOL:
				return "Mount Creation Tool";
			case STATION_TOOL:
				return "Station Placement Tool";

			case DELETE:
				return "Delete";
			case PIN:
				return "Pin";
			case NEW_SHIP:
				return "New Ship";
			case LOAD_SHIP:
				return "Load Ship";
			case SAVE_SHIP:
				return "Save Ship";
			case SAVE_SHIP_AS:
				return "Save Ship As";
			case CLOSE_SHIP:
				return "Close Ship";
			case LOAD_LEGACY:
				return "Load .shp";
			case MANAGE_MOD:
				return "Mod Management";
			case SETTINGS:
				return "Settings";
			case UNDO:
				return "Undo";
			case REDO:
				return "Redo";
			case CLOAK:
				return "View Cloaked Appearance";
			case ANIMATE:
				return "Animate Gibs";

			case OPEN_ZOOM:
				return "Open Zoom Window";
			case TOGGLE_GRID:
				return "Show Grid";
			case TOGGLE_HANGAR:
				return "Show Hangar";
			case SHOW_ANCHOR:
				return "Show Ship Origin";
			case SHOW_MOUNTS:
				return "Show Mounts";
			case SHOW_ROOMS:
				return "Show Rooms";
			case SHOW_DOORS:
				return "Show Doors";
			case SHOW_STATIONS:
				return "Show Stations";
			case SHOW_HULL:
				return "Show Hull";
			case SHOW_FLOOR:
				return "Show Floor";
			case SHOW_SHIELD:
				return "Show Shields";
			case SHOW_GIBS:
				return "Show Gibs";

			default:
				return "";
		}
	}
}