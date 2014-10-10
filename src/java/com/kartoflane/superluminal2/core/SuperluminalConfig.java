package com.kartoflane.superluminal2.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

import org.eclipse.swt.graphics.Point;

@SuppressWarnings("serial")
public class SuperluminalConfig extends Properties {

	private File configFile;

	// @formatter:off
	// All keys the config can save
	public static final String FTL_RESOURCE =			"ftlResourcePath";
	public static final String SAVE_GEOMETRY =			"rememberGeometry";
	public static final String GEOMETRY =				"geometry";

	public static final String SIDEBAR_SIDE =			"sidebarRightSide";
	public static final String START_MAX =				"startMaximized";
	public static final String CLOSE_LOADER =			"closeLoaderAfterLoading";
	public static final String CHECK_UPDATES =			"checkUpdatesOnStartup";
	public static final String ALLOW_OVERLAP =			"allowRoomOverlap";
	public static final String ALLOW_OVERLAP_DOOR =		"allowDoorOverlap";
	public static final String RESET_LINKS =			"resetDoorLinksOnMove";
	public static final String MOUSE_SHIP_RELATIVE =	"pointerRelativeToShip";

	public static final String SLOT_WARNING =			"shownSlotWarning";
	public static final String ARTILLERY_WARNING =		"shownArtilleryWarning";
	// @formatter:on

	public SuperluminalConfig(File configFile) {
		super();
		this.configFile = configFile;
	}

	/**
	 * Returns a copy of an existing Config object.
	 */
	public SuperluminalConfig(SuperluminalConfig srcConfig) {
		super();
		configFile = srcConfig.getConfigFile();
		putAll(srcConfig);
	}

	public File getConfigFile() {
		return configFile;
	}

	public int getPropertyAsInt(String key, int defaultValue) {
		return parseStringAsInt(getProperty(key), defaultValue);
	}

	public Point getPropertyAsPoint(String key, int defaultX, int defaultY) {
		String s = getProperty(key);
		if (s != null && s.matches("^\\d+,\\d+$")) {
			String[] words = s.split(",");
			return new Point(parseStringAsInt(words[0], defaultX), parseStringAsInt(words[1], defaultY));
		} else
			return new Point(defaultX, defaultY);
	}

	public boolean getPropertyAsBoolean(String key, boolean defaultValue) {
		String s = getProperty(key);
		try {
			if (s == null)
				return defaultValue;
			return Boolean.parseBoolean(s);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Updates the config properties with the current runtime values.
	 */
	public void setCurrent() {
		setProperty(START_MAX, "" + Manager.startMaximised);
		setProperty(SIDEBAR_SIDE, "" + Manager.sidebarOnRightSide);
		setProperty(CHECK_UPDATES, "" + Manager.checkUpdates);
		setProperty(CLOSE_LOADER, "" + Manager.closeLoader);
		setProperty(ALLOW_OVERLAP, "" + Manager.allowRoomOverlap);
		setProperty(ALLOW_OVERLAP_DOOR, "" + Manager.allowDoorOverlap);
		setProperty(RESET_LINKS, "" + Manager.resetDoorLinksOnMove);
		setProperty(MOUSE_SHIP_RELATIVE, "" + Manager.mouseShipRelative);

		setProperty(SLOT_WARNING, "" + Manager.shownSlotWarning);
		setProperty(ARTILLERY_WARNING, "" + Manager.shownArtilleryWarning);

		setProperty(FTL_RESOURCE, Manager.resourcePath);
		setProperty(SAVE_GEOMETRY, "" + Manager.rememberGeometry);
		if (Manager.rememberGeometry && !Manager.startMaximised)
			setProperty(GEOMETRY, Manager.windowSize.x + "," + Manager.windowSize.y);
	}

	public void writeConfig() throws IOException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(configFile);
			String configComments = "";
			configComments += "\n";
			configComments += " " + FTL_RESOURCE + " - The path to FTL's resources folder. If invalid, you'll be prompted.\n";
			configComments += " " + SAVE_GEOMETRY + " - If true, window geometry will be saved on exit and restored on startup.\n";
			configComments += " " + START_MAX + " - If true, the application's window will be maximised on startup (overrides geometry).\n";
			configComments += " " + SIDEBAR_SIDE + " - If true, the sidebar will appear on the right side of the editor's main display.\n";
			configComments += " " + CLOSE_LOADER + " - If true, ship loader window will automatically close after a ship is loaded.\n";
			configComments += " " + CHECK_UPDATES + " - If true, the program will automatically check for updates each time it is started.\n";
			configComments += " " + ALLOW_OVERLAP + " - If true, room collision will be disabled, allowing rooms to be placed on top of each other.\n";
			configComments += " " + ALLOW_OVERLAP_DOOR + " - If true, door collision will be disabled, allowing doors to be placed on top of each other.\n";
			configComments += " " + MOUSE_SHIP_RELATIVE + " - If true, the mouse tracker will display the pointer's location relative to the ship's origin.\n";
			configComments += "\n";
			configComments += " " + GEOMETRY + " - Last saved size of the main window.\n";
			configComments += "\n";

			OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
			store(writer, configComments);
			writer.flush();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
			}
		}
	}

	private int parseStringAsInt(String s, int defaultValue) {
		if (s != null && s.matches("^\\d+$"))
			return Integer.parseInt(s);
		else
			return defaultValue;
	}
}
