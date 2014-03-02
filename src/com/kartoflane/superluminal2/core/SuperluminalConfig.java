package com.kartoflane.superluminal2.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;

public class SuperluminalConfig {
	private Properties config;
	private File configFile;

	// all keys the config can save
	public static final String FTL_RESOURCE = "ftlResourcePath";
	public static final String SAVE_GEOMETRY = "rememberGeometry";
	public static final String SIDEBAR_SIDE = "sidebarRightSide";
	public static final String GEOMETRY = "geometry";

	public SuperluminalConfig(Properties config, File configFile) {
		this.config = config;
		this.configFile = configFile;
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
		String s = config.getProperty(key);
		if (s != null && s.matches("^\\d+$"))
			return Integer.parseInt(s);
		else
			return defaultValue;
	}

	public String getProperty(String key, String defaultValue) {
		return config.getProperty(key, defaultValue);
	}

	public String getProperty(String key) {
		return config.getProperty(key);
	}

	public void writeConfig() throws IOException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(configFile);
			String configComments = "";
			configComments += "\n";
			configComments += " " + FTL_RESOURCE + " - The path to FTL's resources folder. If invalid, you'll be prompted.\n";
			configComments += " " + SAVE_GEOMETRY + " - If true, window geometry will be saved on exit and restored on startup.\n";
			configComments += " " + SIDEBAR_SIDE + " - If true, the sidebar will appear on the right side of the editor's main display.\n";
			configComments += "\n";
			configComments += " " + GEOMETRY + " - Last saved position/size/etc of the main window.\n";
			configComments += "\n";
			configComments += " " + "Hotkeys" + " - Rudimentary hotkey customization is supported - lowercase characters only, accessible\n";
			configComments += " " + "without any modifiers (Shift, Ctrl or Alt)";

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
