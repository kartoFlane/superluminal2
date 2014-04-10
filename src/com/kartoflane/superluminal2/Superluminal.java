package com.kartoflane.superluminal2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import net.vhati.modmanager.core.ComparableVersion;
import net.vhati.modmanager.core.FTLUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.core.Manager.Hotkeys;
import com.kartoflane.superluminal2.core.SuperluminalConfig;
import com.kartoflane.superluminal2.ui.EditorWindow;

public class Superluminal {
	public static final Logger log = LogManager.getLogger(Superluminal.class);

	public static final String APP_NAME = "Superluminal";
	public static final ComparableVersion APP_VERSION = new ComparableVersion("2.0.0 pre-alpha");
	public static final String APP_URL = "http://www.google.com/"; // TODO
	public static final String APP_AUTHOR = "kartoFlane";

	// config variables
	public static boolean sidebarOnRightSide = true;
	public static boolean rememberGeometry = true;
	public static String resourcePath = "";

	public static Display display = null;

	/**
	 * TODO:
	 * - hull image etc positioning f'd up --> see hull's ImageBox
	 * - entity deletion --> add (to) undo
	 * - load data directly from archives - don't unpack
	 * - undo przy pomocy reflection -- UndoableBooleanFieldEdit, etc ?? ...chyba nie
	 * 
	 */

	public static void main(String[] args) {
		Display.setAppName(APP_NAME);
		Display.setAppVersion(APP_VERSION.toString());

		log.debug(String.format("%s v%s", APP_NAME, APP_VERSION));
		log.debug(String.format("%s %s", System.getProperty("os.name"), System.getProperty("os.version")));
		log.debug(String.format("%s, %s, %s", System.getProperty("java.vm.name"), System.getProperty("java.version"), System.getProperty("os.arch")));

		File configFile = new File("editor.cfg");

		Properties config = new Properties();
		// setup defaults
		config.setProperty(SuperluminalConfig.FTL_RESOURCE, "");
		config.setProperty(SuperluminalConfig.SAVE_GEOMETRY, "true");
		config.setProperty(SuperluminalConfig.SIDEBAR_SIDE, "true");
		// hotkeys
		config.setProperty(Hotkeys.POINTER_TOOL_KEY.toString(), "q");
		config.setProperty(Hotkeys.CREATE_TOOL_KEY.toString(), "w");
		config.setProperty(Hotkeys.GIB_TOOL_KEY.toString(), "e");
		config.setProperty(Hotkeys.PROPERTIES_TOOL_KEY.toString(), "r");
		config.setProperty(Hotkeys.OVERVIEW_TOOL_KEY.toString(), "o");
		config.setProperty(Hotkeys.ROOM_TOOL_KEY.toString(), "a");
		config.setProperty(Hotkeys.DOOR_TOOL_KEY.toString(), "s");
		config.setProperty(Hotkeys.MOUNT_TOOL_KEY.toString(), "d");
		config.setProperty(Hotkeys.STATION_TOOL_KEY.toString(), "f");

		// read the config file
		InputStream in = null;
		try {
			if (configFile.exists()) {
				log.trace("Loading properties from config file.");
				in = new FileInputStream(configFile);
				config.load(new InputStreamReader(in, "UTF-8"));
			}
		} catch (IOException e) {
			log.error("Error loading config.", e);
			showErrorDialog("Error loading config from " + configFile.getPath());
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
		}

		// read config values
		sidebarOnRightSide = Boolean.parseBoolean(config.getProperty(SuperluminalConfig.SIDEBAR_SIDE));
		rememberGeometry = Boolean.parseBoolean(config.getProperty(SuperluminalConfig.SAVE_GEOMETRY));

		for (Hotkeys hotkey : Hotkeys.values()) {
			String keyString = config.getProperty(hotkey.toString());
			if (keyString == null || keyString.length() != 1) {
				log.warn("Hotkey for " + hotkey + " was incorrect and was not loaded.");
				continue;
			}
			char key = keyString.toLowerCase().charAt(0);
			Manager.HOTKEY_MAP.put(hotkey, key);
		}

		// read FTL resources path
		File datsDir = null;
		resourcePath = config.getProperty(SuperluminalConfig.FTL_RESOURCE, "");

		if (resourcePath.length() > 0) {
			log.info("Using FTL dats path from config: " + resourcePath);
			datsDir = new File(resourcePath);
			if (FTLUtilities.isDatsDirValid(datsDir) == false) {
				log.error("The config's ftlResourcePath does not exist, or it lacks data.dat.");
				datsDir = null;
			}
		} else {
			log.trace("No FTL dats path previously set.");
		}

		// create the main window instance
		EditorWindow editorWindow = null;
		try {
			display = Display.getDefault();
			editorWindow = new EditorWindow(display);
		} catch (Exception e) {
			log.error("Exception while creating EditorWindow.", e);
			System.exit(1);
		}

		// find/prompt for the path to set in the config
		if (datsDir == null) {
			datsDir = FTLUtilities.findDatsDir();
			if (datsDir != null) {
				MessageBox box = new MessageBox(editorWindow.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				box.setText("Confirm");
				box.setMessage("FTL resources were found in:\n" + datsDir.getPath() + "\nIs this correct?");
				int response = box.open();
				if (response == SWT.NO)
					datsDir = null;
			}

			if (datsDir == null) {
				log.debug("FTL dats path was not located automatically. Prompting user for location.");
				datsDir = FTLUtilities.promptForDatsDir(editorWindow.getShell());
			}

			if (datsDir != null) {
				resourcePath = datsDir.getAbsolutePath();
				config.setProperty(SuperluminalConfig.FTL_RESOURCE, resourcePath);
				log.info("FTL dats located at: " + resourcePath);
			}
		}

		if (datsDir == null) {
			showErrorDialog("FTL resources were not found.\nThe editor will now exit.");
			log.debug("No FTL dats path found, exiting.");
			System.exit(1);
		}

		// open the main window's shell - make it visible
		editorWindow.open();

		log.info("Running...");

		try {
			while (!editorWindow.getShell().isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Throwable t) {
			log.error("An exception occured and the editor was forced to terminate.", t);
			StringBuilder buf = new StringBuilder();
			buf.append(APP_NAME + " has encountered a problem and needs to close.\n\n");
			buf.append("Please check editor-log.txt in the editor's directory, and post it\n");
			buf.append("in the editor's thread on the FTL forums.");
			showErrorDialog(buf.toString());
		}

		log.info("Exiting...");

		SuperluminalConfig appConfig = new SuperluminalConfig(config, configFile);
		try {
			appConfig.setProperty(SuperluminalConfig.FTL_RESOURCE, resourcePath);
			appConfig.setProperty(SuperluminalConfig.SAVE_GEOMETRY, "" + rememberGeometry);
			appConfig.setProperty(SuperluminalConfig.SIDEBAR_SIDE, "" + sidebarOnRightSide);
			for (Hotkeys hotkey : Hotkeys.values()) {
				appConfig.setProperty(hotkey.toString(), Manager.HOTKEY_MAP.get(hotkey).toString());
			}
			appConfig.writeConfig();
		} catch (IOException e) {
			String errorMsg = String.format("Error writing config to \"%s\".", configFile.getPath());
			log.error(errorMsg, e);
			showErrorDialog(errorMsg);
		}

		Grid.getInstance().dispose();
		editorWindow.dispose();
		display.dispose();
	}

	public static void showErrorDialog(String message) {
		Shell shell = null;
		if (EditorWindow.getInstance() == null || EditorWindow.getInstance().getShell() == null)
			shell = new Shell(display);
		else
			shell = EditorWindow.getInstance().getShell();

		MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);

		box.setText(String.format("%s - Error", APP_NAME));
		box.setMessage(message);

		box.open();
	}
}
