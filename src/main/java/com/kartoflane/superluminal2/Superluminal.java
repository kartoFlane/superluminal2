package com.kartoflane.superluminal2;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;

import net.vhati.ftldat.FTLDat.FTLPack;
import net.vhati.modmanager.core.ComparableVersion;
import net.vhati.modmanager.core.FTLUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.JDOMParseException;

import com.kartoflane.superluminal2.components.Hotkey;
import com.kartoflane.superluminal2.components.enums.Hotkeys;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.core.SuperluminalConfig;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.utils.IOUtils;
import com.kartoflane.superluminal2.utils.UIUtils;
import com.kartoflane.superluminal2.utils.UIUtils.LoadTask;

public class Superluminal {
	public static final Logger log = LogManager.getLogger(Superluminal.class);

	public static final String APP_NAME = "Superluminal";
	public static final ComparableVersion APP_VERSION = new ComparableVersion("2.0.0 beta");
	public static final String APP_UPDATE_URL = "https://raw.github.com/kartoFlane/superluminal2/master/skel_common/auto_update.xml";
	public static final String APP_URL = "http://www.google.com/"; // TODO
	public static final String APP_AUTHOR = "kartoFlane";

	public static final String HOTKEYS_FILE = "hotkeys.xml";
	public static final String CONFIG_FILE = "editor.cfg";

	/**
	 * settings ideas:
	 * - checkboxes to make floor/cloak/shield/mounts follow hull, something along these lines
	 * - whether to load data from game's archives, or unpack them and use that
	 * - feature creeeeeeep
	 * 
	 * TODO:
	 * - add Edit > Reset All Door Links
	 * - drag .zip/.ftl files into mod management dialog to add them
	 * - add augments to armaments tab
	 * - changing interior image from 2x2 to 2x1 (for example) leaves unredrawn canvas area
	 * - dragging reorder to ship overview
	 * - figure out a better way to represent weapon stats in weapon selection dialog
	 * - rudimentary image viewer
	 * - ship offset modification
	 * - add blueprint name selection to general tab?
	 * - include fine offsets in ship positioning? --> offset hangar image from anchor to indicate this
	 * - come up with a way to set which system is first when assigned to the same room?
	 * - rework interior drawing -> currently drawn on room layer, so higher rooms obscur the image -> bad
	 * - add gibs
	 * - add reordering to ship overview
	 * - properties: crew tab
	 * - glow placement modification
	 * 
	 * - entity deletion --> add (to) undo
	 * - undo przy pomocy reflection -- UndoableBooleanFieldEdit, etc ?? ...chyba nie
	 * 
	 */

	public static void main(String[] args) {
		log.debug(String.format("%s v%s", APP_NAME, APP_VERSION));
		log.debug(String.format("%s %s", System.getProperty("os.name"), System.getProperty("os.version")));
		log.debug(String.format("%s, %s, %s", System.getProperty("java.vm.name"), System.getProperty("java.version"), System.getProperty("os.arch")));
		log.debug(String.format("SWT v%s", SWT.getVersion()));

		log.trace("Retrieving display... If the log cuts off after this entry, then wrong version of the editor has been downloaded.");
		Display display = Display.getCurrent();
		Display.setAppName(APP_NAME);
		Display.setAppVersion(APP_VERSION.toString());
		log.trace("Display retrieved successfully!");

		File configFile = new File(CONFIG_FILE);

		Properties config = new Properties();
		SuperluminalConfig appConfig = new SuperluminalConfig(config, configFile);

		// Read the config file
		InputStreamReader reader = null;
		try {
			if (configFile.exists()) {
				log.trace("Loading properties from config file...");
				reader = new InputStreamReader(new FileInputStream(configFile), "UTF-8");
				config.load(reader);
			}
		} catch (IOException e) {
			log.error("Error loading config.", e);
			UIUtils.showErrorDialog(null, null, "Error loading config from " + configFile.getPath());
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
			}
		}

		// Read config values
		Manager.sidebarOnRightSide = Boolean.parseBoolean(config.getProperty(SuperluminalConfig.SIDEBAR_SIDE));
		Manager.rememberGeometry = Boolean.parseBoolean(config.getProperty(SuperluminalConfig.SAVE_GEOMETRY));
		Manager.checkUpdates = Boolean.parseBoolean(config.getProperty(SuperluminalConfig.CHECK_UPDATES));
		Manager.startMaximised = Boolean.parseBoolean(config.getProperty(SuperluminalConfig.START_MAX));
		Manager.closeLoader = Boolean.parseBoolean(config.getProperty(SuperluminalConfig.CLOSE_LOADER));
		Manager.allowRoomOverlap = Boolean.parseBoolean(config.getProperty(SuperluminalConfig.ALLOW_OVERLAP));
		Manager.windowSize = appConfig.getPropertyAsPoint(SuperluminalConfig.GEOMETRY, 0, 0);

		initHotkeys();
		File hotkeysFile = new File(HOTKEYS_FILE);
		if (hotkeysFile.exists())
			loadHotkeys(hotkeysFile);

		// Read FTL resources path
		File datsDir = null;
		Manager.resourcePath = config.getProperty(SuperluminalConfig.FTL_RESOURCE, "");

		if (Manager.resourcePath.length() > 0) {
			log.info("Using FTL dats path from config: " + Manager.resourcePath);
			datsDir = new File(Manager.resourcePath);
			if (FTLUtilities.isDatsDirValid(datsDir) == false) {
				log.error("The config's ftlResourcePath does not exist, or it lacks data.dat.");
				datsDir = null;
			}
		} else {
			log.trace("No FTL dats path previously set.");
		}

		// Create the main window instance
		EditorWindow editorWindow = null;
		try {
			display = Display.getDefault();
			editorWindow = new EditorWindow(display);
		} catch (Exception e) {
			log.error("Exception occured while creating EditorWindow: ", e);
			UIUtils.showErrorDialog(null, null, "An error has occured while creating the editor's GUI: " + e.getMessage());
			System.exit(1);
		}

		// Find / prompt for the path to set in the config
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
				datsDir = UIUtils.promptForDatsDir(editorWindow.getShell());
			}

			if (datsDir != null) {
				Manager.resourcePath = datsDir.getAbsolutePath();
				config.setProperty(SuperluminalConfig.FTL_RESOURCE, Manager.resourcePath);
				log.info("FTL dats located at: " + Manager.resourcePath);
			}
		}

		// Exit program if dats were not found, or load them if they were
		if (datsDir == null) {
			UIUtils.showErrorDialog(editorWindow.getShell(), null, "FTL resources were not found.\nThe editor will now exit.");
			log.debug("No FTL dats path found, exiting.");
			System.exit(1);
		} else {
			try {
				log.trace("Loading dat archives...");
				File dataFile = new File(datsDir + "/data.dat");
				File resourceFile = new File(datsDir + "/resource.dat");
				FTLPack data = new FTLPack(dataFile, "r");
				FTLPack resource = new FTLPack(resourceFile, "r");

				final Database db = new Database(data, resource);

				log.trace("Loading database...");

				UIUtils.showLoadDialog(editorWindow.getShell(), null, null, new LoadTask() {
					public void execute() {
						db.getCore().load();
						db.cacheAnimations();
					}
				});
			} catch (IOException e) {
				log.error("An error occured while loading dat archives:", e);

				StringBuilder buf = new StringBuilder();
				buf.append("An error has occured while loading the game's resources.\n\n");
				buf.append("Please check editor-log.txt in the editor's directory, and post it\n");
				buf.append("in the editor's thread on the FTL forums.");
				UIUtils.showErrorDialog(editorWindow.getShell(), null, buf.toString());
				System.exit(1);
			}
		}

		// Open the main window's shell, making it visible
		editorWindow.open();

		if (Manager.checkUpdates) {
			checkForUpdates(false); // Automatic update check
		}

		log.info("Running...");

		try {
			while (!editorWindow.getShell().isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} catch (Throwable t) {
			log.error("An error has occured and the editor was forced to terminate.", t);
			StringBuilder buf = new StringBuilder();
			buf.append(APP_NAME + " has encountered a problem and needs to close.\n\n");
			buf.append("Please check editor-log.txt in the editor's directory, and post it\n");
			buf.append("in the editor's thread on the FTL forums.");
			UIUtils.showErrorDialog(editorWindow.getShell(), null, buf.toString());
		}

		log.info("Exiting...");

		saveHotkeys(hotkeysFile);

		// Save config
		try {
			appConfig.setProperty(SuperluminalConfig.FTL_RESOURCE, Manager.resourcePath);
			appConfig.setProperty(SuperluminalConfig.SAVE_GEOMETRY, "" + Manager.rememberGeometry);
			appConfig.setProperty(SuperluminalConfig.START_MAX, "" + Manager.startMaximised);
			appConfig.setProperty(SuperluminalConfig.SIDEBAR_SIDE, "" + Manager.sidebarOnRightSide);
			appConfig.setProperty(SuperluminalConfig.CHECK_UPDATES, "" + Manager.checkUpdates);
			appConfig.setProperty(SuperluminalConfig.CLOSE_LOADER, "" + Manager.closeLoader);
			appConfig.setProperty(SuperluminalConfig.ALLOW_OVERLAP, "" + Manager.allowRoomOverlap);
			if (Manager.rememberGeometry && !Manager.startMaximised)
				appConfig.setProperty(SuperluminalConfig.GEOMETRY, Manager.windowSize.x + "," + Manager.windowSize.y);
			appConfig.writeConfig();
		} catch (IOException e) {
			String errorMsg = String.format("Error writing config to \"%s\".", configFile.getPath());
			log.error(errorMsg, e);
			UIUtils.showErrorDialog(editorWindow.getShell(), null, errorMsg);
		}

		editorWindow.dispose();
		display.dispose();
	}

	/**
	 * 
	 * @param manual
	 *            if true, an information dialog will pop up even if the program is up to date
	 */
	public static void checkForUpdates(boolean manual) {
		log.info("Checking for updates...");

		final ComparableVersion[] remoteVersion = new ComparableVersion[1];
		UIUtils.showLoadDialog(EditorWindow.getInstance().getShell(), "Checking Updates...", "Checking for updates, please wait...", new LoadTask() {
			public void execute() {
				InputStream is = null;
				try {
					URL url = new URL(APP_UPDATE_URL);
					is = url.openStream();

					Document updateDoc = IOUtils.readStreamXML(is, "auto-update");
					Element root = updateDoc.getRootElement();
					Element latest = root.getChild("latest");
					String id = latest.getAttributeValue("id");

					remoteVersion[0] = new ComparableVersion(id);
				} catch (UnknownHostException e) {
					log.warn("Update check failed -- connection to the repository could not be estabilished.");
				} catch (JDOMException e) {
					log.warn("Udpate check failed -- an error has occured while parsing update file.", e);
				} catch (Exception e) {
					log.warn("An error occured while checking updates.", e);
				} finally {
					try {
						if (is != null)
							is.close();
					} catch (IOException e) {
					}
				}
			}
		});

		if (remoteVersion[0] == null) {
			// Version check failed, already logged by previous catches
		} else if (APP_VERSION.compareTo(remoteVersion[0]) < 0) {
			try {
				log.info("Update is available, user version: " + APP_VERSION + ", remote version: " + remoteVersion[0]);

				MessageBox box = new MessageBox(EditorWindow.getInstance().getShell(), SWT.ICON_INFORMATION | SWT.YES | SWT.NO);
				box.setText(APP_NAME + " - Update Available");
				StringBuilder buf = new StringBuilder();
				buf.append("A new version of the editor is available: v.");
				buf.append(remoteVersion[0]);
				buf.append("\nWould you like to download it now?");
				box.setMessage(buf.toString());

				if (box.open() == SWT.YES) {
					URL url = new URL(APP_URL);

					Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
					if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
						try {
							desktop.browse(url.toURI());
						} catch (Exception e) {
							log.error("An error has occured while opening web browser.", e);
						}
					}
				}
			} catch (IOException e) {
				log.warn("An error has occured while displaying update result.", e);
			}
		} else {
			log.info("Program is up to date.");
			if (manual) {
				// The user manually initiated the version check, so probably expects some kind of response in either case.
				UIUtils.showInfoDialog(EditorWindow.getInstance().getShell(), null, APP_NAME + " is up to date.");
			}
		}
	}

	/** Create a Hotkey object for each hotkey, and store them in the hotkey map. */
	private static void initHotkeys() {
		for (Hotkeys keyId : Hotkeys.values())
			Manager.HOTKEY_MAP.put(keyId, new Hotkey(keyId));
		loadDefaultHotkeys();
	}

	/** Loads the default hotkey values, which are later overridden by config or the user. */
	private static void loadDefaultHotkeys() {
		Hotkey hotkey = null;

		// Tool hotkeys
		Manager.getHotkey(Hotkeys.POINTER_TOOL).setKey('q');
		Manager.getHotkey(Hotkeys.CREATE_TOOL).setKey('w');
		Manager.getHotkey(Hotkeys.GIB_TOOL).setKey('e');
		Manager.getHotkey(Hotkeys.PROPERTIES_TOOL).setKey('r');
		Manager.getHotkey(Hotkeys.ROOM_TOOL).setKey('a');
		Manager.getHotkey(Hotkeys.DOOR_TOOL).setKey('s');
		Manager.getHotkey(Hotkeys.MOUNT_TOOL).setKey('d');
		Manager.getHotkey(Hotkeys.STATION_TOOL).setKey('f');
		Manager.getHotkey(Hotkeys.OVERVIEW_TOOL).setKey('o');

		// Command hotkeys
		Manager.getHotkey(Hotkeys.PIN).setKey(' ');

		hotkey = Manager.getHotkey(Hotkeys.DELETE);
		hotkey.setKey('d');
		hotkey.setShift(true);

		hotkey = Manager.getHotkey(Hotkeys.UNDO);
		hotkey.setKey('z');
		hotkey.setCtrl(true);

		hotkey = Manager.getHotkey(Hotkeys.REDO);
		hotkey.setKey('y');
		hotkey.setCtrl(true);

		hotkey = Manager.getHotkey(Hotkeys.NEW_SHIP);
		hotkey.setKey('n');
		hotkey.setCtrl(true);

		hotkey = Manager.getHotkey(Hotkeys.LOAD_SHIP);
		hotkey.setKey('l');
		hotkey.setCtrl(true);

		hotkey = Manager.getHotkey(Hotkeys.SAVE_SHIP);
		hotkey.setKey('s');
		hotkey.setCtrl(true);

		hotkey = Manager.getHotkey(Hotkeys.CLOSE_SHIP);
		hotkey.setKey('w');
		hotkey.setCtrl(true);

		hotkey = Manager.getHotkey(Hotkeys.MANAGE_MOD);
		hotkey.setKey('m');
		hotkey.setCtrl(true);

		hotkey = Manager.getHotkey(Hotkeys.SETTINGS);
		hotkey.setKey('o');
		hotkey.setCtrl(true);

		hotkey = Manager.getHotkey(Hotkeys.CLOAK);
		hotkey.setKey('c');
		hotkey.setShift(true);

		// View hotkeys
		Manager.getHotkey(Hotkeys.TOGGLE_GRID).setKey('x');
		Manager.getHotkey(Hotkeys.SHOW_ANCHOR).setKey('1');
		Manager.getHotkey(Hotkeys.SHOW_MOUNTS).setKey('2');
		Manager.getHotkey(Hotkeys.SHOW_ROOMS).setKey('3');
		Manager.getHotkey(Hotkeys.SHOW_DOORS).setKey('4');
		Manager.getHotkey(Hotkeys.SHOW_STATIONS).setKey('5');
		Manager.getHotkey(Hotkeys.SHOW_HULL).setKey('6');
		Manager.getHotkey(Hotkeys.SHOW_FLOOR).setKey('7');
		Manager.getHotkey(Hotkeys.SHOW_SHIELD).setKey('8');
	}

	/**
	 * Loads hotkey configuration from the given file.
	 * 
	 * @param f
	 *            File from which hotkey config will be read.
	 */
	private static void loadHotkeys(File f) {
		try {
			Document keyDoc = IOUtils.readFileXML(f);

			Element root = keyDoc.getRootElement();
			for (Element bind : root.getChildren("bind")) {
				String actionName = bind.getValue();
				if (actionName == null) {
					log.warn(HOTKEYS_FILE + " contained a bind without an assigned action.");
					continue;
				}

				Hotkeys action = null;
				try {
					action = Hotkeys.valueOf(actionName);
				} catch (IllegalArgumentException e) {
					log.warn("Action '" + actionName + "' was not recognised, and was not loaded.");
				}
				String loading = null;
				String attr = null;
				try {
					loading = "shift";
					attr = bind.getAttributeValue(loading);
					if (attr == null)
						throw new NullPointerException();
					boolean shift = Boolean.valueOf(attr);

					loading = "ctrl";
					attr = bind.getAttributeValue(loading);
					if (attr == null)
						throw new NullPointerException();
					boolean ctrl = Boolean.valueOf(attr);

					loading = "alt";
					attr = bind.getAttributeValue(loading);
					if (attr == null)
						throw new NullPointerException();
					boolean alt = Boolean.valueOf(attr);

					loading = "char";
					attr = bind.getAttributeValue(loading);
					if (attr.length() != 1)
						throw new IllegalArgumentException();
					int ch = attr.charAt(0);

					Hotkey h = Manager.getHotkey(action);
					h.setShift(shift);
					h.setCtrl(ctrl);
					h.setAlt(alt);
					h.setKey(ch);
				} catch (IllegalArgumentException e) {
					log.warn("A keybind for action " + action.name() + " had invalid '" + loading + "' attribute, and was not loaded.");
				} catch (NullPointerException e) {
					log.warn("A keybind for action " + action.name() + " was missing attribute '" + loading + "', and was not loaded.");
				}
			}
		} catch (FileNotFoundException ex) {
			log.warn("Keybind file could not be found: " + f.getAbsolutePath());
		} catch (IOException ex) {
			log.error("An error has occured while loading keybind file: ", ex);
		} catch (JDOMParseException ex) {
			log.error("JDOM exception occured while loading file " + f.getAbsolutePath(), ex);
		}
	}

	/**
	 * Saves the current hotkey configuration in the given file.
	 * 
	 * @param f
	 *            File in which hotkey config will be saved.
	 */
	private static void saveHotkeys(File f) {
		Document keyDoc = new Document();

		Element wrapper = new Element("wrapper");
		Element root = new Element("keybinds");

		for (Hotkeys action : Hotkeys.values()) {
			Hotkey h = Manager.getHotkey(action);
			Element bind = new Element("bind");
			bind.setText(action.name());
			bind.setAttribute("shift", "" + h.getShift());
			bind.setAttribute("ctrl", "" + h.getCtrl());
			bind.setAttribute("alt", "" + h.getAlt());
			bind.setAttribute("char", "" + (char) h.getKey());

			root.addContent(bind);
		}

		wrapper.addContent(root);
		keyDoc.setRootElement(wrapper);

		try {
			IOUtils.writeFileXML(keyDoc, f);
		} catch (IOException e) {
			log.warn("An error occured while saving hotkeys file: ", e);
		}
	}
}
