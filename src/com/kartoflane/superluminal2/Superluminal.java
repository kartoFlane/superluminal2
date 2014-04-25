package com.kartoflane.superluminal2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import org.jdom2.input.JDOMParseException;

import com.kartoflane.superluminal2.components.Hotkey;
import com.kartoflane.superluminal2.components.Hotkey.Hotkeys;
import com.kartoflane.superluminal2.core.DataUtils;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.core.SuperluminalConfig;
import com.kartoflane.superluminal2.core.Utils;
import com.kartoflane.superluminal2.core.Utils.DecodeResult;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.ShipLoaderDialog;

public class Superluminal {
	public static final Logger log = LogManager.getLogger(Superluminal.class);

	public static final String APP_NAME = "Superluminal";
	public static final ComparableVersion APP_VERSION = new ComparableVersion("2.0.0 alpha");
	public static final String APP_URL = "http://www.google.com/"; // TODO
	public static final String APP_AUTHOR = "kartoFlane";

	public static final String HOTKEYS_FILE = "hotkeys.xml";

	public static Display display = null;

	/**
	 * settings ideas:
	 * - close ship loader after loading
	 * -
	 * -
	 * -
	 * - feature creeeeeeep
	 * 
	 * TODO:
	 * - move systems menu to its own class, so that it can be referenced without having to go through RoomDataComposite
	 * - same as above goes for door linking
	 * - include fine offsets in ship positioning?
	 * - come up with a way to set which system is first when assigned to the same room?
	 * - come up with another way of choosing weapons/drones -- combos are ugly
	 * ---- also need a way to select weapon lists for enemy ships
	 * - optimize anchor moving against its bounds with some rooms loaded -> stutters
	 * - rework interior drawing -> currently drawn on room layer, so higher rooms obscur the image -> bad
	 * - finish (ie. create) the props system
	 * - rework stuff to use props system (anchor lines, room resize handles perhaps?)
	 * - clear() in ShipObject -> wipe txt layout, + something else?
	 * - figure out glow images
	 * - add gibs
	 * - add reordering to ship overview
	 * - properties: general & armaments & crew tabs
	 * 
	 * - entity deletion --> add (to) undo
	 * - undo przy pomocy reflection -- UndoableBooleanFieldEdit, etc ?? ...chyba nie
	 * 
	 */

	public static void main(String[] args) {
		Display.setAppName(APP_NAME);
		Display.setAppVersion(APP_VERSION.toString());

		log.debug(String.format("%s v%s", APP_NAME, APP_VERSION));
		log.debug(String.format("%s %s", System.getProperty("os.name"), System.getProperty("os.version")));
		log.debug(String.format("%s, %s, %s", System.getProperty("java.vm.name"), System.getProperty("java.version"), System.getProperty("os.arch")));
		log.debug(String.format("SWT v%s", SWT.getVersion()));

		File configFile = new File("editor.cfg");

		Properties config = new Properties();
		// Setup defaults
		config.setProperty(SuperluminalConfig.FTL_RESOURCE, "");
		config.setProperty(SuperluminalConfig.SAVE_GEOMETRY, "true");
		config.setProperty(SuperluminalConfig.SIDEBAR_SIDE, "true");

		// Read the config file
		InputStream in = null;
		try {
			if (configFile.exists()) {
				log.trace("Loading properties from config file...");
				in = new FileInputStream(configFile);
				config.load(new InputStreamReader(in, "UTF-8"));
			}
		} catch (IOException e) {
			log.error("Error loading config.", e);
			Utils.showErrorDialog(null, "Error loading config from " + configFile.getPath());
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
		}

		// Read config values
		Manager.sidebarOnRightSide = Boolean.parseBoolean(config.getProperty(SuperluminalConfig.SIDEBAR_SIDE));
		Manager.rememberGeometry = Boolean.parseBoolean(config.getProperty(SuperluminalConfig.SAVE_GEOMETRY));

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
			Utils.showErrorDialog(null, "An error has occured while creating the editor's GUI: " + e.getMessage());
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
				datsDir = Utils.promptForDatsDir(editorWindow.getShell());
			}

			if (datsDir != null) {
				Manager.resourcePath = datsDir.getAbsolutePath();
				config.setProperty(SuperluminalConfig.FTL_RESOURCE, Manager.resourcePath);
				log.info("FTL dats located at: " + Manager.resourcePath);
			}
		}

		// Exit program if dats were not found, or load them if they were
		if (datsDir == null) {
			Utils.showErrorDialog(editorWindow.getShell(), "FTL resources were not found.\nThe editor will now exit.");
			log.debug("No FTL dats path found, exiting.");
			System.exit(1);
		} else {
			try {
				log.trace("Loading dat archives...");
				File dataFile = new File(datsDir + "/data.dat");
				File resourceFile = new File(datsDir + "/resource.dat");
				FTLPack data = new FTLPack(dataFile, "r");
				FTLPack resource = new FTLPack(resourceFile, "r");

				Database db = Database.getInstance();
				db.setDataDat(data);
				db.setResourceDat(resource);

				log.trace("Loading database...");
				loadDatabase();
			} catch (IOException e) {
				log.error("An error occured while loading dat archives:", e);

				StringBuilder buf = new StringBuilder();
				buf.append("An error has occured while loading the game's resources.\n\n");
				buf.append("Please check editor-log.txt in the editor's directory, and post it\n");
				buf.append("in the editor's thread on the FTL forums.");
				Utils.showErrorDialog(editorWindow.getShell(), buf.toString());
				System.exit(1);
			}
		}

		// Open the main window's shell
		editorWindow.open();

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
			Utils.showErrorDialog(editorWindow.getShell(), buf.toString());
		}

		log.info("Exiting...");

		saveHotkeys(hotkeysFile);

		// Save config
		SuperluminalConfig appConfig = new SuperluminalConfig(config, configFile);
		try {
			appConfig.setProperty(SuperluminalConfig.FTL_RESOURCE, Manager.resourcePath);
			appConfig.setProperty(SuperluminalConfig.SAVE_GEOMETRY, "" + Manager.rememberGeometry);
			appConfig.setProperty(SuperluminalConfig.SIDEBAR_SIDE, "" + Manager.sidebarOnRightSide);
			for (Hotkeys hotkey : Hotkeys.values()) {
				appConfig.setProperty(hotkey.toString(), Manager.getHotkey(hotkey).toString());
			}
			appConfig.writeConfig();
		} catch (IOException e) {
			String errorMsg = String.format("Error writing config to \"%s\".", configFile.getPath());
			log.error(errorMsg, e);
			Utils.showErrorDialog(editorWindow.getShell(), errorMsg);
		}

		editorWindow.dispose();
		display.dispose();
	}

	private static void loadDatabase() {
		Database db = Database.getInstance();
		FTLPack data = db.getDataDat();

		// Animations need to be loaded before weapons, since they reference them
		db.preloadAnims();

		for (String innerPath : data.list()) {
			if (innerPath.endsWith(".xml")) {
				InputStream is = null;
				try {
					is = data.getInputStream(innerPath);
					DecodeResult dr = Utils.decodeText(is, null);

					ArrayList<Element> elements = DataUtils.findTagsNamed(dr.text, "shipBlueprint");
					for (Element e : elements) {
						try {
							db.storeShipMetadata(DataUtils.loadShipMetadata(e));
						} catch (IllegalArgumentException ex) {
							log.warn("Could not load ship metadata: " + ex.getMessage());
						}
					}

					elements.clear();
					elements = null;
					elements = DataUtils.findTagsNamed(dr.text, "weaponBlueprint");
					for (Element e : elements) {
						try {
							db.storeWeapon(DataUtils.loadWeapon(e));
						} catch (IllegalArgumentException ex) {
							log.warn("Could not load weapon: " + ex.getMessage());
						}
					}

					elements.clear();
					elements = null;
					elements = DataUtils.findTagsNamed(dr.text, "droneBlueprint");
					for (Element e : elements) {
						try {
							db.storeDrone(DataUtils.loadDrone(e));
						} catch (IllegalArgumentException ex) {
							log.warn("Could not load drone: " + ex.getMessage());
						}
					}

					elements.clear();
					elements = null;
					elements = DataUtils.findTagsNamed(dr.text, "augBlueprint");
					for (Element e : elements) {
						try {
							db.storeAugment(DataUtils.loadAugment(e));
						} catch (IllegalArgumentException ex) {
							log.warn("Could not load augment: " + ex.getMessage());
						}
					}
				} catch (FileNotFoundException e) {
					log.error("Could not find file: " + innerPath);
				} catch (IOException e) {
					log.error("An error has occured while loading file " + innerPath + ":", e);
				} catch (JDOMParseException e) {
					log.error("An error has occured while parsing file " + innerPath + ":", e);
				} finally {
					try {
						if (is != null)
							is.close();
					} catch (IOException e) {
					}
				}
			}
		}

		// Clear anim sheets, as they're no longer needed
		db.clearAnimSheets();

		ShipLoaderDialog.getInstance().loadShipList(Database.getInstance().getShipMetadata());
	}

	private static void initHotkeys() {
		for (Hotkeys keyId : Hotkeys.values())
			Manager.HOTKEY_MAP.put(keyId, new Hotkey(keyId));
		loadDefaultHotkeys();
	}

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

	private static void loadHotkeys(File f) {
		try {
			Document keyDoc = Utils.readFileXML(f);

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
			Utils.writeFileXML(keyDoc, f);
		} catch (IOException e) {
			log.warn("An error occured while saving hotkeys file: ", e);
		}
	}
}
