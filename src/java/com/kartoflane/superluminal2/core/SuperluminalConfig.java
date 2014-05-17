package com.kartoflane.superluminal2.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

import org.eclipse.swt.graphics.Point;

public class SuperluminalConfig {
	private Properties config;
	private File configFile;

	// All keys the config can save
	public static final String FTL_RESOURCE = "ftlResourcePath";
	public static final String SAVE_GEOMETRY = "rememberGeometry";
	public static final String SIDEBAR_SIDE = "sidebarRightSide";
	public static final String START_MAX = "startMaximized";
	public static final String CLOSE_LOADER = "closeLoaderAfterLoading";
	public static final String GEOMETRY = "geometry";
	public static final String CHECK_UPDATES = "checkUpdatesOnStartup";
	public static final String ALLOW_OVERLAP = "allowRoomOverlap";
	public static final String SLOT_WARNING = "shownSlotWarning";

	public SuperluminalConfig(Properties config, File configFile) {
		this.config = config;
		this.configFile = configFile;

		setDefaults();
	}

	/**
	 * Returns a copy of an existing Config object.
	 */
	public SuperluminalConfig(SuperluminalConfig srcConfig) {
		this.configFile = srcConfig.getConfigFile();
		this.config = new Properties();
		this.config.putAll(srcConfig.getConfig());
	}

	public Properties getConfig() {
		return config;
	}

	public File getConfigFile() {
		return configFile;
	}

	public Object setProperty(String key, String value) {
		return config.setProperty(key, value);
	}

	public int getPropertyAsInt(String key, int defaultValue) {
		return parseStringAsInt(config.getProperty(key), defaultValue);
	}

	private int parseStringAsInt(String s, int defaultValue) {
		if (s != null && s.matches("^\\d+$"))
			return Integer.parseInt(s);
		else
			return defaultValue;
	}

	public Point getPropertyAsPoint(String key, int defaultX, int defaultY) {
		String s = config.getProperty(key);
		if (s != null && s.matches("^\\d+,\\d+$")) {
			String[] words = s.split(",");
			return new Point(parseStringAsInt(words[0], defaultX), parseStringAsInt(words[1], defaultY));
		} else
			return new Point(defaultX, defaultY);
	}

	public String getProperty(String key, String defaultValue) {
		return config.getProperty(key, defaultValue);
	}

	public String getProperty(String key) {
		return config.getProperty(key);
	}

	/**
	 * Resets the config properties to their default values.
	 */
	public void setDefaults() {
		config.setProperty(FTL_RESOURCE, "");
		config.setProperty(SAVE_GEOMETRY, "true");
		config.setProperty(SIDEBAR_SIDE, "false");
		config.setProperty(START_MAX, "false");
		config.setProperty(CLOSE_LOADER, "false");
		config.setProperty(GEOMETRY, "");
		config.setProperty(CHECK_UPDATES, "true");
		config.setProperty(ALLOW_OVERLAP, "false");
		config.setProperty(SLOT_WARNING, "false");
	}

	/**
	 * Updates the config properties with the current runtime values.
	 */
	public void setCurrent() {
		config.setProperty(SuperluminalConfig.FTL_RESOURCE, Manager.resourcePath);
		config.setProperty(SuperluminalConfig.SAVE_GEOMETRY, "" + Manager.rememberGeometry);
		config.setProperty(SuperluminalConfig.START_MAX, "" + Manager.startMaximised);
		config.setProperty(SuperluminalConfig.SIDEBAR_SIDE, "" + Manager.sidebarOnRightSide);
		config.setProperty(SuperluminalConfig.CHECK_UPDATES, "" + Manager.checkUpdates);
		config.setProperty(SuperluminalConfig.CLOSE_LOADER, "" + Manager.closeLoader);
		config.setProperty(SuperluminalConfig.ALLOW_OVERLAP, "" + Manager.allowRoomOverlap);
		config.setProperty(SuperluminalConfig.SLOT_WARNING, "" + Manager.shownSlotWarning);
		if (Manager.rememberGeometry && !Manager.startMaximised)
			config.setProperty(SuperluminalConfig.GEOMETRY, Manager.windowSize.x + "," + Manager.windowSize.y);
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
			configComments += "\n";
			configComments += " " + GEOMETRY + " - Last saved size of the main window.\n";
			configComments += "\n";

			OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
			config.store(writer, configComments);
			writer.flush();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
			}
		}
	}
}
