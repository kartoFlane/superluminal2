package com.kartoflane.superluminal2.ui.sidebar;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.components.enums.Images;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ftl.DroneList;
import com.kartoflane.superluminal2.ftl.DroneObject;
import com.kartoflane.superluminal2.ftl.ShipObject;
import com.kartoflane.superluminal2.ftl.WeaponList;
import com.kartoflane.superluminal2.ftl.WeaponObject;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;
import com.kartoflane.superluminal2.ui.DroneSelectionDialog;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.WeaponSelectionDialog;
import com.kartoflane.superluminal2.utils.IOUtils;

public class PropertiesToolComposite extends Composite {

	private static int selectedTab = 0;
	private ShipContainer container;

	private Text txtHull;
	private Button btnHullBrowse;
	private Button btnHullClear;
	private Text txtFloor;
	private Button btnFloorBrowse;
	private Button btnFloorClear;
	private Text txtCloak;
	private Button btnCloakBrowse;
	private Button btnCloakClear;
	private Text txtShield;
	private Button btnShieldBrowse;
	private Button btnShieldClear;
	private Text txtMini;
	private Button btnMiniBrowse;
	private Button btnMiniClear;
	private Text txtName;
	private Text txtClass;
	private Text txtDesc;
	private Button btnHullView;
	private Button btnFloorView;
	private Button btnCloakView;
	private Button btnShieldView;
	private Button btnMiniView;
	private Spinner spHealth;
	private Spinner spPower;
	private TabItem tbtmCrew;
	private Composite compCrew;
	private Label lblDesc;
	private Spinner spMinSec;
	private Spinner spMaxSec;
	private TabFolder tabFolder;
	private ArrayList<Button> btnWeapons = new ArrayList<Button>();
	private ArrayList<Button> btnDrones = new ArrayList<Button>();
	private Spinner spMissiles;
	private Spinner spWeaponSlots;
	private Button btnWeaponList;
	private Button btnDroneList;
	private Group grpWeapons;
	private Composite compArm;
	private Group grpDrones;
	private Spinner spDrones;
	private Spinner spDroneSlots;
	private Label lblNYI;

	public PropertiesToolComposite(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(1, false));

		container = Manager.getCurrentShip();
		final ShipObject ship = container.getShipController().getGameObject();
		final boolean[] created = { false };

		Label lblPropertiesTool = new Label(this, SWT.NONE);
		lblPropertiesTool.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
		lblPropertiesTool.setText("Properties");

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (created[0])
					selectedTab = tabFolder.getSelectionIndex();
			}
		});

		/*
		 * =========================================================================
		 * XXX: Images tab
		 * =========================================================================
		 */

		TabItem tbtmImages = new TabItem(tabFolder, SWT.NONE);
		tbtmImages.setText("Images");

		Composite compImages = new Composite(tabFolder, SWT.NONE);
		tbtmImages.setControl(compImages);
		compImages.setLayout(new GridLayout(4, false));

		// Hull widgets
		Label lblHull = new Label(compImages, SWT.NONE);
		lblHull.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblHull.setText("Hull");

		btnHullView = new Button(compImages, SWT.NONE);
		btnHullView.setEnabled(false);
		btnHullView.setText("View");

		btnHullBrowse = new Button(compImages, SWT.NONE);
		btnHullBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnHullBrowse.setText("Browse");

		btnHullClear = new Button(compImages, SWT.NONE);
		btnHullClear.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnHullClear.setText("Clear");

		txtHull = new Text(compImages, SWT.BORDER | SWT.READ_ONLY);
		txtHull.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		if (ship.isPlayerShip()) {
			// Floor widgets
			Label lblFloor = new Label(compImages, SWT.NONE);
			lblFloor.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
			lblFloor.setText("Floor");

			btnFloorView = new Button(compImages, SWT.NONE);
			btnFloorView.setEnabled(false);
			btnFloorView.setText("View");

			btnFloorBrowse = new Button(compImages, SWT.NONE);
			btnFloorBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			btnFloorBrowse.setText("Browse");

			btnFloorClear = new Button(compImages, SWT.NONE);
			btnFloorClear.setText("Clear");

			txtFloor = new Text(compImages, SWT.BORDER | SWT.READ_ONLY);
			txtFloor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		}

		// Cloak widgets
		Label lblCloak = new Label(compImages, SWT.NONE);
		lblCloak.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblCloak.setText("Cloak");

		btnCloakView = new Button(compImages, SWT.NONE);
		btnCloakView.setEnabled(false);
		btnCloakView.setText("View");

		btnCloakBrowse = new Button(compImages, SWT.NONE);
		btnCloakBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnCloakBrowse.setText("Browse");

		btnCloakClear = new Button(compImages, SWT.NONE);
		btnCloakClear.setText("Clear");

		txtCloak = new Text(compImages, SWT.BORDER | SWT.READ_ONLY);
		txtCloak.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		if (ship.isPlayerShip()) {
			// Shield widgets
			Label lblShield = new Label(compImages, SWT.NONE);
			lblShield.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
			lblShield.setText("Shield");

			btnShieldView = new Button(compImages, SWT.NONE);
			btnShieldView.setEnabled(false);
			btnShieldView.setText("View");

			btnShieldBrowse = new Button(compImages, SWT.NONE);
			btnShieldBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			btnShieldBrowse.setText("Browse");

			btnShieldClear = new Button(compImages, SWT.NONE);
			btnShieldClear.setText("Clear");

			txtShield = new Text(compImages, SWT.BORDER | SWT.READ_ONLY);
			txtShield.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

			// Thumbnail widgets
			Label lblMini = new Label(compImages, SWT.NONE);
			lblMini.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
			lblMini.setText("Thumbnail");

			btnMiniView = new Button(compImages, SWT.NONE);
			btnMiniView.setEnabled(false);
			btnMiniView.setText("View");

			btnMiniBrowse = new Button(compImages, SWT.NONE);
			btnMiniBrowse.setText("Browse");

			btnMiniClear = new Button(compImages, SWT.NONE);
			btnMiniClear.setText("Clear");

			txtMini = new Text(compImages, SWT.BORDER | SWT.READ_ONLY);
			txtMini.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		}

		SelectionAdapter imageViewListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Images type = null;
				if (e.getSource() == btnHullView)
					type = Images.HULL;
				else if (e.getSource() == btnFloorView)
					type = Images.FLOOR;
				else if (e.getSource() == btnCloakView)
					type = Images.CLOAK;
				else if (e.getSource() == btnShieldView)
					type = Images.SHIELD;
				else if (e.getSource() == btnMiniView)
					type = Images.THUMBNAIL;

				String path = container.getImage(type);
				if (path == null)
					return;

				File file = new File(path);
				if (file.exists()) {
					if (Desktop.isDesktopSupported()) {
						Desktop desktop = Desktop.getDesktop();
						if (desktop != null) {
							try {
								desktop.open(file.getParentFile());
							} catch (IOException ex) {
							}
						}
					} else
						Superluminal.log.error("Unable to open file location - AWT Desktop not supported.");
				}
			}
		};

		btnHullView.addSelectionListener(imageViewListener);
		btnCloakView.addSelectionListener(imageViewListener);
		if (ship.isPlayerShip()) {
			btnFloorView.addSelectionListener(imageViewListener);
			btnShieldView.addSelectionListener(imageViewListener);
			btnMiniView.addSelectionListener(imageViewListener);
		}

		SelectionAdapter imageBrowseListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(EditorWindow.getInstance().getShell());
				dialog.setFilterExtensions(new String[] { "*.png" });
				String path = dialog.open();

				Images type = null;
				if (e.getSource() == btnHullBrowse)
					type = Images.HULL;
				else if (e.getSource() == btnFloorBrowse)
					type = Images.FLOOR;
				else if (e.getSource() == btnCloakBrowse)
					type = Images.CLOAK;
				else if (e.getSource() == btnShieldBrowse)
					type = Images.SHIELD;
				else if (e.getSource() == btnMiniBrowse)
					type = Images.THUMBNAIL;

				// path == null only when user cancels
				if (path != null) {
					container.setImage(type, "file:" + path);
					updateData();
				}
			}
		};

		btnHullBrowse.addSelectionListener(imageBrowseListener);
		btnCloakBrowse.addSelectionListener(imageBrowseListener);
		if (ship.isPlayerShip()) {
			btnFloorBrowse.addSelectionListener(imageBrowseListener);
			btnShieldBrowse.addSelectionListener(imageBrowseListener);
			btnMiniBrowse.addSelectionListener(imageBrowseListener);
		}

		SelectionAdapter imageClearListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Images type = null;
				if (e.getSource() == btnHullClear)
					type = Images.HULL;
				else if (e.getSource() == btnFloorClear)
					type = Images.FLOOR;
				else if (e.getSource() == btnCloakClear)
					type = Images.CLOAK;
				else if (e.getSource() == btnShieldClear)
					type = Images.SHIELD;
				else if (e.getSource() == btnMiniClear)
					type = Images.THUMBNAIL;

				container.setImage(type, null);
				updateData();
			}
		};

		btnHullClear.addSelectionListener(imageClearListener);
		btnCloakClear.addSelectionListener(imageClearListener);
		if (ship.isPlayerShip()) {
			btnFloorClear.addSelectionListener(imageClearListener);
			btnShieldClear.addSelectionListener(imageClearListener);
			btnMiniClear.addSelectionListener(imageClearListener);
		}

		/*
		 * =========================================================================
		 * XXX: General tab
		 * =========================================================================
		 */

		TabItem tbtmGeneral = new TabItem(tabFolder, SWT.NONE);
		tbtmGeneral.setText("General");

		Composite compGeneral = new Composite(tabFolder, SWT.NONE);
		tbtmGeneral.setControl(compGeneral);
		compGeneral.setLayout(new GridLayout(2, false));

		if (ship.isPlayerShip()) {
			Label lblName = new Label(compGeneral, SWT.NONE);
			lblName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
			lblName.setText("Name:");

			txtName = new Text(compGeneral, SWT.BORDER);
			txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

			// TODO listener to apply changes
		}

		Label lblClass = new Label(compGeneral, SWT.NONE);
		lblClass.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblClass.setText("Class:");

		txtClass = new Text(compGeneral, SWT.BORDER);
		txtClass.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		if (ship.isPlayerShip()) {
			lblDesc = new Label(compGeneral, SWT.NONE);
			lblDesc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
			lblDesc.setText("Description: (0/255)");

			txtDesc = new Text(compGeneral, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
			GridData gd_txtDesc = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
			gd_txtDesc.heightHint = 80;
			txtDesc.setLayoutData(gd_txtDesc);

			txtDesc.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					lblDesc.setText("Description: (" + txtDesc.getText().length() + "/255)");
				}
			});

			// TODO listener to apply changes
		}

		Label lblHealth = new Label(compGeneral, SWT.NONE);
		lblHealth.setText("Hull Health:");

		spHealth = new Spinner(compGeneral, SWT.BORDER);
		spHealth.setTextLimit(3);
		spHealth.setMinimum(0);
		spHealth.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		Label lblReactor = new Label(compGeneral, SWT.NONE);
		lblReactor.setText("Reactor Power:");

		spPower = new Spinner(compGeneral, SWT.BORDER);
		spPower.setTextLimit(3);
		spPower.setMinimum(0);
		spPower.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		if (!ship.isPlayerShip()) {
			Label lblMinSector = new Label(compGeneral, SWT.NONE);
			lblMinSector.setText("Min Sector:");

			spMinSec = new Spinner(compGeneral, SWT.BORDER);
			spMinSec.setTextLimit(1);
			spMinSec.setMaximum(8);
			spMinSec.setMinimum(1);
			spMinSec.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

			Label lblMaxSector = new Label(compGeneral, SWT.NONE);
			lblMaxSector.setText("Max Sector:");

			spMaxSec = new Spinner(compGeneral, SWT.BORDER);
			spMaxSec.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			spMaxSec.setTextLimit(1);
			spMaxSec.setMaximum(8);
			spMaxSec.setMinimum(1);
		}

		/*
		 * =========================================================================
		 * XXX: Armaments tab
		 * =========================================================================
		 */

		TabItem tbtmArmaments = new TabItem(tabFolder, 0);
		tbtmArmaments.setText("Armaments");

		compArm = new Composite(tabFolder, SWT.NONE);
		tbtmArmaments.setControl(compArm);
		compArm.setLayout(new GridLayout(1, false));

		grpWeapons = new Group(compArm, SWT.NONE);
		grpWeapons.setLayout(new GridLayout(2, false));
		grpWeapons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		grpWeapons.setText("Weapons");

		Label lblMissiles = new Label(grpWeapons, SWT.NONE);
		lblMissiles.setText("Starting Missiles");

		spMissiles = new Spinner(grpWeapons, SWT.BORDER);
		spMissiles.setMaximum(999);
		spMissiles.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

		Label lblWeaponSlots = new Label(grpWeapons, SWT.NONE);
		lblWeaponSlots.setText("Slots");

		spWeaponSlots = new Spinner(grpWeapons, SWT.BORDER);
		spWeaponSlots.setMaximum(4);
		spWeaponSlots.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		spWeaponSlots.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (ship.isPlayerShip()) {
					int slots = spWeaponSlots.getSelection();
					ship.setWeaponSlots(slots);
					clearWeaponSlots();
					createWeaponSlots(slots);
					updateData();
					container.updateMounts();
				}
			}
		});

		if (ship.isPlayerShip()) {
			createWeaponSlots(ship.getWeaponSlots());
		} else {
			btnWeaponList = new Button(grpWeapons, SWT.NONE);
			btnWeaponList.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
			btnWeaponList.setText("<weapon list>");

			btnWeaponList.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					WeaponList current = ship.getWeaponList();
					WeaponSelectionDialog dialog = new WeaponSelectionDialog(EditorWindow.getInstance().getShell());
					WeaponList neu = dialog.open(current);

					if (neu != null) {
						ship.setWeaponList(neu);
						updateData();
					}
				}
			});
		}

		grpDrones = new Group(compArm, SWT.NONE);
		grpDrones.setLayout(new GridLayout(2, false));
		grpDrones.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		grpDrones.setText("Drones");

		Label lblDrones = new Label(grpDrones, SWT.NONE);
		lblDrones.setText("Starting Drone Parts");

		spDrones = new Spinner(grpDrones, SWT.BORDER);
		spDrones.setMaximum(999);
		spDrones.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

		Label lblDroneSlots = new Label(grpDrones, SWT.NONE);
		lblDroneSlots.setText("Slots");

		spDroneSlots = new Spinner(grpDrones, SWT.BORDER);
		spDroneSlots.setMaximum(4);
		spDroneSlots.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		spDroneSlots.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (ship.isPlayerShip()) {
					int slots = spDroneSlots.getSelection();
					ship.setDroneSlots(slots);
					clearDroneSlots();
					createDroneSlots(slots);
					updateData();
				}
			}
		});

		if (ship.isPlayerShip()) {
			createDroneSlots(ship.getDroneSlots());
		} else {
			btnDroneList = new Button(grpDrones, SWT.NONE);
			btnDroneList.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
			btnDroneList.setText("<drone list>");

			btnDroneList.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					DroneList current = ship.getDroneList();
					DroneSelectionDialog dialog = new DroneSelectionDialog(EditorWindow.getInstance().getShell());
					DroneList neu = dialog.open(current);

					if (neu != null) {
						ship.setDroneList(neu);
						updateData();
					}
				}
			});
		}

		/*
		 * =========================================================================
		 * XXX: Crew tab
		 * =========================================================================
		 */

		tbtmCrew = new TabItem(tabFolder, SWT.NONE);
		tbtmCrew.setText("Crew");

		compCrew = new Composite(tabFolder, SWT.NONE);
		tbtmCrew.setControl(compCrew);
		compCrew.setLayout(new GridLayout(1, false));

		lblNYI = new Label(compCrew, SWT.NONE);
		lblNYI.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblNYI.setText("(not yet implemented)");

		pack();
		updateData();
		created[0] = true;
		tabFolder.setSelection(selectedTab);
	}

	public void updateData() {
		ShipController controller = container.getShipController();
		ShipObject ship = controller.getGameObject();

		// Images tab

		// Update image path text fields and scroll them to the end to show the file's name
		String content = container.getImage(Images.HULL);

		txtHull.setText(content == null ? "" : IOUtils.trimProtocol(content));
		txtHull.selectAll();
		btnHullView.setEnabled(content != null);

		content = container.getImage(Images.CLOAK);
		txtCloak.setText(content == null ? "" : IOUtils.trimProtocol(content));
		txtCloak.selectAll();
		btnCloakView.setEnabled(content != null);

		if (ship.isPlayerShip()) {
			content = container.getImage(Images.FLOOR);
			txtFloor.setText(content == null ? "" : IOUtils.trimProtocol(content));
			txtFloor.selectAll();
			btnFloorView.setEnabled(content != null);

			content = container.getImage(Images.SHIELD);
			txtShield.setText(content == null ? "" : IOUtils.trimProtocol(content));
			txtShield.selectAll();
			btnShieldView.setEnabled(content != null);

			content = container.getImage(Images.THUMBNAIL);
			txtMini.setText(content == null || !ship.isPlayerShip() ? "" : IOUtils.trimProtocol(content));
			txtMini.selectAll();
			btnMiniView.setEnabled(content != null);
		}

		// General tab

		content = ship.getShipClass();
		txtClass.setText(content == null ? "" : content);

		spHealth.setSelection(ship.getHealth());
		spPower.setSelection(ship.getPower());

		if (ship.isPlayerShip()) {
			content = ship.getShipName();
			txtName.setText(ship.isPlayerShip() && content != null ? content : "");

			content = ship.getShipDescription();
			txtDesc.setText(ship.isPlayerShip() && content != null ? content : "");
			lblDesc.setText("Description: (" + txtDesc.getText().length() + "/255)");
		} else {
			spMinSec.setSelection(ship.getMinSector());
			spMaxSec.setSelection(ship.getMaxSector());
			spMinSec.setEnabled(!ship.isPlayerShip());
			spMaxSec.setEnabled(!ship.isPlayerShip());
		}

		// Armaments tab

		spMissiles.setSelection(ship.getMissilesAmount());
		spWeaponSlots.setSelection(ship.getWeaponSlots());
		spDrones.setSelection(ship.getDronePartsAmount());
		spDroneSlots.setSelection(ship.getDroneSlots());

		if (ship.isPlayerShip()) {
			int count = 0;
			for (WeaponObject weapon : ship.getWeapons()) {
				if (count < ship.getWeaponSlots()) {
					btnWeapons.get(count).setText(weapon.toString());
					count++;
				}
			}

			count = 0;
			for (DroneObject drone : ship.getDrones()) {
				if (count < ship.getDroneSlots()) {
					btnDrones.get(count).setText(drone.toString());
					count++;
				}
			}
		} else {
			WeaponList wList = ship.getWeaponList();
			btnWeaponList.setText(wList.getBlueprintName());

			DroneList dList = ship.getDroneList();
			btnDroneList.setText(dList.getBlueprintName());
		}
	}

	private void clearWeaponSlots() {
		for (Button b : btnWeapons)
			b.dispose();
		btnWeapons.clear();
		compArm.layout();
	}

	private void clearDroneSlots() {
		for (Button b : btnDrones)
			b.dispose();
		btnDrones.clear();
		compArm.layout();
	}

	private void createWeaponSlots(int n) {
		SelectionAdapter listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int i = btnWeapons.indexOf(e.getSource());

				if (i != -1) {
					ShipObject ship = container.getShipController().getGameObject();
					WeaponObject current = ship.getWeapons()[i];

					WeaponSelectionDialog dialog = new WeaponSelectionDialog(EditorWindow.getInstance().getShell());
					WeaponObject neu = dialog.open(current);

					if (neu != null) {
						// If the weapon is the default dummy, then replace the first occurence of
						// the dummy weapon, so that there are no gaps
						if (current == Database.DEFAULT_WEAPON_OBJ)
							container.changeWeapon(current, neu);
						else
							container.changeWeapon(i, neu);
						updateData();
					}
				}
			}
		};

		for (int i = 0; i < n; i++) {
			Button b = new Button(grpWeapons, SWT.NONE);
			b.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
			b.setText("<weapon slot>");
			b.addSelectionListener(listener);
			btnWeapons.add(b);
		}

		compArm.layout();
	}

	private void createDroneSlots(int n) {
		SelectionAdapter listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int i = btnDrones.indexOf(e.getSource());

				if (i != -1) {
					ShipObject ship = container.getShipController().getGameObject();
					DroneObject current = ship.getDrones()[i];

					DroneSelectionDialog dialog = new DroneSelectionDialog(EditorWindow.getInstance().getShell());
					DroneObject neu = dialog.open(current);

					if (neu != null) {
						// If the drone is the default dummy, then replace the first occurence of
						// the dummy drone, so that there are no gaps
						if (current == Database.DEFAULT_DRONE_OBJ)
							ship.changeDrone(current, neu);
						else
							ship.changeDrone(i, neu);
						updateData();
					}
				}
			}
		};

		for (int i = 0; i < n; i++) {
			Button b = new Button(grpDrones, SWT.NONE);
			b.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
			b.setText("<drone slot>");
			b.addSelectionListener(listener);
			btnDrones.add(b);
		}

		compArm.layout();
	}

	@Override
	public boolean isFocusControl() {
		boolean result = false;
		result |= txtClass.isFocusControl() || spHealth.isFocusControl() || spPower.isFocusControl() ||
				spMissiles.isFocusControl() || spWeaponSlots.isFocusControl() ||
				spDrones.isFocusControl() || spDroneSlots.isFocusControl();
		if (container.getShipController().isPlayerShip()) {
			result |= txtName.isFocusControl() || txtDesc.isFocusControl();
		} else {
			result |= spMinSec.isFocusControl() || spMaxSec.isFocusControl();
		}
		return result;
	}
}
