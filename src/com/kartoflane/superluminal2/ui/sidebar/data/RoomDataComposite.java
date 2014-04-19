package com.kartoflane.superluminal2.ui.sidebar.data;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.core.Utils;
import com.kartoflane.superluminal2.ftl.SystemObject.Systems;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;
import com.kartoflane.superluminal2.mvc.controllers.SystemController;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.OverviewWindow;
import com.kartoflane.superluminal2.ui.ShipContainer;

public class RoomDataComposite extends Composite implements DataComposite {

	private Button btnSystem;
	private Menu systemMenu;
	private MenuItem mntmEmpty;
	private MenuItem mntmEngines;
	private MenuItem mntmMedbay;
	private MenuItem mntmOxygen;
	private MenuItem mntmShields;
	private MenuItem mntmWeapons;
	private MenuItem mntmArtillery;
	private MenuItem mntmCloaking;
	private MenuItem mntmDrones;
	private MenuItem mntmTeleporter;
	private MenuItem mntmDoors;
	private MenuItem mntmPilot;
	private MenuItem mntmSensors;
	private Label lblSysLevel;
	private Label lblMaxLevel;
	private Scale scaleSysLevel;
	private Text txtSysLevel;
	private Scale scaleMaxLevel;
	private Text txtMaxLevel;
	private Button btnAvailable;
	private Composite imagesComposite;
	private Button btnInteriorBrowse;
	private Button btnInteriorClear;
	private Text txtInterior;
	private Button btnInteriorView;

	private RoomController roomC = null;
	private ShipContainer container = null;
	private MenuItem mntmClonebay;
	private MenuItem mntmBattery;
	private MenuItem mntmHacking;
	private MenuItem mntmMind;
	private Label label;
	private Label lblGlow;
	private Combo glowCombo;

	public RoomDataComposite(Composite parent, RoomController control) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(3, false));

		this.roomC = control;
		container = Manager.getCurrentShip();
		final ShipController shipC = container.getShipController();

		label = new Label(this, SWT.NONE);
		label.setAlignment(SWT.CENTER);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		label.setText("Room");

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		Label lblSystem = new Label(this, SWT.NONE);
		lblSystem.setText("System: ");

		btnSystem = new Button(this, SWT.NONE);
		GridData gd_btnSystem = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1);
		gd_btnSystem.widthHint = 100;
		btnSystem.setLayoutData(gd_btnSystem);
		btnSystem.setText("");

		btnAvailable = new Button(this, SWT.CHECK);
		btnAvailable.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		btnAvailable.setText("Available At Start");

		lblSysLevel = new Label(this, SWT.NONE);
		lblSysLevel.setText("Starting Level:");

		scaleSysLevel = new Scale(this, SWT.NONE);
		scaleSysLevel.setMaximum(2);
		scaleSysLevel.setMinimum(1);
		scaleSysLevel.setPageIncrement(1);
		scaleSysLevel.setIncrement(1);
		scaleSysLevel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

		txtSysLevel = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		txtSysLevel.setText("");
		txtSysLevel.setTextLimit(3);
		GridData gd_txtSysLevel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_txtSysLevel.widthHint = 20;
		txtSysLevel.setLayoutData(gd_txtSysLevel);

		scaleSysLevel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Systems sys = container.getAssignedSystem(roomC.getGameObject());
				SystemController system = container.getSystemController(sys);
				system.setLevel(scaleSysLevel.getSelection());
				txtSysLevel.setText("" + scaleSysLevel.getSelection());
			}
		});

		if (!Manager.getCurrentShip().getShipController().isPlayerShip()) {
			lblMaxLevel = new Label(this, SWT.NONE);
			lblMaxLevel.setText("Max Level:");

			scaleMaxLevel = new Scale(this, SWT.NONE);
			scaleMaxLevel.setMaximum(2);
			scaleMaxLevel.setMinimum(1);
			scaleMaxLevel.setPageIncrement(1);
			scaleMaxLevel.setIncrement(1);
			scaleMaxLevel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

			txtMaxLevel = new Text(this, SWT.BORDER | SWT.READ_ONLY);
			txtMaxLevel.setText("");
			txtMaxLevel.setTextLimit(3);
			GridData gd_txtMaxLevel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
			gd_txtMaxLevel.widthHint = 20;
			txtMaxLevel.setLayoutData(gd_txtMaxLevel);

			scaleMaxLevel.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Systems sys = container.getAssignedSystem(roomC.getGameObject());
					SystemController system = container.getSystemController(sys);
					system.setLevelMax(scaleMaxLevel.getSelection());
					txtMaxLevel.setText("" + scaleMaxLevel.getSelection());
					if (!shipC.isPlayerShip()) {
						scaleSysLevel.setMaximum(scaleMaxLevel.getSelection());
						scaleSysLevel.notifyListeners(SWT.Selection, null);
					}
				}
			});
		}

		imagesComposite = new Composite(this, SWT.NONE);
		GridLayout gl_imagesComposite = new GridLayout(4, false);
		gl_imagesComposite.marginHeight = 0;
		gl_imagesComposite.marginWidth = 0;
		imagesComposite.setLayout(gl_imagesComposite);
		imagesComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));

		// Interior widgets
		Label lblInterior = new Label(imagesComposite, SWT.NONE);
		lblInterior.setText("Interior image:");

		btnInteriorView = new Button(imagesComposite, SWT.NONE);
		btnInteriorView.setEnabled(false);
		btnInteriorView.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnInteriorView.setText("View");

		btnInteriorBrowse = new Button(imagesComposite, SWT.NONE);
		btnInteriorBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnInteriorBrowse.setText("Browse");

		btnInteriorClear = new Button(imagesComposite, SWT.NONE);
		btnInteriorClear.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnInteriorClear.setText("Clear");

		txtInterior = new Text(imagesComposite, SWT.BORDER | SWT.READ_ONLY);
		txtInterior.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		lblGlow = new Label(imagesComposite, SWT.NONE);
		lblGlow.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblGlow.setText("Manning glow:");

		glowCombo = new Combo(imagesComposite, SWT.READ_ONLY);
		glowCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		// TODO read and add glow sets

		glowCombo.add("Define new glow set...");
		glowCombo.add("Default glow set");
		glowCombo.select(1);
		// TODO define dialog popup to define new glow set

		btnSystem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Point p = btnSystem.getLocation();
				systemMenu.setLocation(toDisplay(p.x, p.y + btnSystem.getSize().y));
				showSystemMenu();
			}
		});

		btnAvailable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Systems sys = container.getAssignedSystem(roomC.getGameObject());
				SystemController system = container.getSystemController(sys);
				system.setAvailableAtStart(btnAvailable.getSelection());
				roomC.redraw();
			}
		});

		SelectionAdapter imageViewListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Systems sys = container.getAssignedSystem(roomC.getGameObject());
				SystemController system = container.getSystemController(sys);
				File file = new File(system.getInteriorPath());

				if (file != null && file.exists()) {
					if (Desktop.isDesktopSupported()) {
						Desktop desktop = Desktop.getDesktop();
						if (desktop != null) {
							try {
								desktop.open(file.getParentFile());
							} catch (IOException ex) {
							}
						}
					} else {
						Superluminal.log.error("Unable to open file location - AWT Desktop not supported.");
					}
				}
			}
		};
		btnInteriorView.addSelectionListener(imageViewListener);

		SelectionAdapter imageBrowseListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Systems sys = container.getAssignedSystem(roomC.getGameObject());
				SystemController system = container.getSystemController(sys);
				FileDialog dialog = new FileDialog(EditorWindow.getInstance().getShell());
				dialog.setFilterExtensions(new String[] { "*.png" });
				String path = dialog.open();

				// path == null only when user cancels
				if (path != null) {
					system.setInteriorPath("file:" + path);
					updateData();
				}
			}
		};
		btnInteriorBrowse.addSelectionListener(imageBrowseListener);

		SelectionAdapter imageClearListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Systems sys = container.getAssignedSystem(roomC.getGameObject());
				SystemController system = container.getSystemController(sys);

				system.setInteriorPath(null);
				updateData();
			}
		};
		btnInteriorClear.addSelectionListener(imageClearListener);

		/*
		 * =========================
		 * Context menu with systems
		 * =========================
		 */
		systemMenu = new Menu(this);
		setMenu(systemMenu);

		mntmEmpty = new MenuItem(systemMenu, SWT.RADIO);
		mntmEmpty.setText("Empty");

		MenuItem mntmSystem = new MenuItem(systemMenu, SWT.CASCADE);
		mntmSystem.setText("Systems");

		Menu menu = new Menu(mntmSystem);
		mntmSystem.setMenu(menu);

		mntmEngines = new MenuItem(menu, SWT.RADIO);
		mntmEngines.setText("Engines");

		mntmMedbay = new MenuItem(menu, SWT.RADIO);
		mntmMedbay.setText("Medbay");

		mntmClonebay = new MenuItem(menu, SWT.RADIO);
		mntmClonebay.setText("Clonebay");

		mntmOxygen = new MenuItem(menu, SWT.RADIO);
		mntmOxygen.setText("Oxygen");

		mntmShields = new MenuItem(menu, SWT.RADIO);
		mntmShields.setText("Shields");

		mntmWeapons = new MenuItem(menu, SWT.RADIO);
		mntmWeapons.setText("Weapons");

		MenuItem mntmSubsystem = new MenuItem(systemMenu, SWT.CASCADE);
		mntmSubsystem.setText("Subsystems");

		Menu menu_1 = new Menu(mntmSubsystem);
		mntmSubsystem.setMenu(menu_1);

		mntmBattery = new MenuItem(menu_1, SWT.RADIO);
		mntmBattery.setText("Battery");

		mntmDoors = new MenuItem(menu_1, SWT.RADIO);
		mntmDoors.setText("Doors");

		mntmPilot = new MenuItem(menu_1, SWT.RADIO);
		mntmPilot.setText("Pilot");

		mntmSensors = new MenuItem(menu_1, SWT.RADIO);
		mntmSensors.setText("Sensors");

		MenuItem mntmSpecial = new MenuItem(systemMenu, SWT.CASCADE);
		mntmSpecial.setText("Special");

		Menu menu_2 = new Menu(mntmSpecial);
		mntmSpecial.setMenu(menu_2);

		mntmArtillery = new MenuItem(menu_2, SWT.RADIO);
		mntmArtillery.setText("Artillery");

		mntmCloaking = new MenuItem(menu_2, SWT.RADIO);
		mntmCloaking.setText("Cloaking");

		mntmDrones = new MenuItem(menu_2, SWT.RADIO);
		mntmDrones.setText("Drone Control");

		mntmHacking = new MenuItem(menu_2, SWT.RADIO);
		mntmHacking.setText("Hacking");

		mntmMind = new MenuItem(menu_2, SWT.RADIO);
		mntmMind.setText("Mind Control");

		mntmTeleporter = new MenuItem(menu_2, SWT.RADIO);
		mntmTeleporter.setText("Teleporter");

		SelectionAdapter systemListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.getSource() == mntmEmpty) {
					container.assign(Systems.EMPTY, roomC);
					btnSystem.setText(Systems.EMPTY.toString());
				} else if (e.getSource() == mntmEngines) {
					container.assign(Systems.ENGINES, roomC);
					btnSystem.setText(Systems.ENGINES.toString());
				} else if (e.getSource() == mntmMedbay) {
					container.assign(Systems.MEDBAY, roomC);
					btnSystem.setText(Systems.MEDBAY.toString());
				} else if (e.getSource() == mntmOxygen) {
					container.assign(Systems.OXYGEN, roomC);
					btnSystem.setText(Systems.OXYGEN.toString());
				} else if (e.getSource() == mntmShields) {
					container.assign(Systems.SHIELDS, roomC);
					btnSystem.setText(Systems.SHIELDS.toString());
				} else if (e.getSource() == mntmWeapons) {
					container.assign(Systems.WEAPONS, roomC);
					btnSystem.setText(Systems.WEAPONS.toString());
				} else if (e.getSource() == mntmArtillery) {
					container.assign(Systems.ARTILLERY, roomC);
					btnSystem.setText(Systems.ARTILLERY.toString());
				} else if (e.getSource() == mntmCloaking) {
					container.assign(Systems.CLOAKING, roomC);
					btnSystem.setText(Systems.CLOAKING.toString());
				} else if (e.getSource() == mntmDrones) {
					container.assign(Systems.DRONES, roomC);
					btnSystem.setText(Systems.DRONES.toString());
				} else if (e.getSource() == mntmTeleporter) {
					container.assign(Systems.TELEPORTER, roomC);
					btnSystem.setText(Systems.TELEPORTER.toString());
				} else if (e.getSource() == mntmDoors) {
					container.assign(Systems.DOORS, roomC);
					btnSystem.setText(Systems.DOORS.toString());
				} else if (e.getSource() == mntmPilot) {
					container.assign(Systems.PILOT, roomC);
					btnSystem.setText(Systems.PILOT.toString());
				} else if (e.getSource() == mntmSensors) {
					container.assign(Systems.SENSORS, roomC);
					btnSystem.setText(Systems.SENSORS.toString());

					// AE contenet:
				} else if (e.getSource() == mntmClonebay) {
					container.assign(Systems.CLONEBAY, roomC);
					btnSystem.setText(Systems.CLONEBAY.toString());
				} else if (e.getSource() == mntmHacking) {
					container.assign(Systems.HACKING, roomC);
					btnSystem.setText(Systems.HACKING.toString());
				} else if (e.getSource() == mntmMind) {
					container.assign(Systems.MIND, roomC);
					btnSystem.setText(Systems.MIND.toString());
				} else if (e.getSource() == mntmBattery) {
					container.assign(Systems.BATTERY, roomC);
					btnSystem.setText(Systems.BATTERY.toString());
				}

				updateData();
				roomC.redraw();
				OverviewWindow.getInstance().update(roomC);
			}
		};

		mntmEmpty.addSelectionListener(systemListener);
		mntmEngines.addSelectionListener(systemListener);
		mntmMedbay.addSelectionListener(systemListener);
		mntmOxygen.addSelectionListener(systemListener);
		mntmShields.addSelectionListener(systemListener);
		mntmWeapons.addSelectionListener(systemListener);
		mntmArtillery.addSelectionListener(systemListener);
		mntmCloaking.addSelectionListener(systemListener);
		mntmDrones.addSelectionListener(systemListener);
		mntmTeleporter.addSelectionListener(systemListener);
		mntmDoors.addSelectionListener(systemListener);
		mntmPilot.addSelectionListener(systemListener);
		mntmSensors.addSelectionListener(systemListener);
		mntmClonebay.addSelectionListener(systemListener);
		mntmBattery.addSelectionListener(systemListener);
		mntmHacking.addSelectionListener(systemListener);
		mntmMind.addSelectionListener(systemListener);

		updateData();

		pack();
	}

	public void showSystemMenu() {
		systemMenu.setVisible(true);

		Systems sys = container.getAssignedSystem(roomC.getGameObject());
		SystemController roomSystem = container.getSystemController(sys);
		mntmEmpty.setSelection(roomSystem.getSystemId() == Systems.EMPTY);
		for (Systems systemId : Systems.getSystems()) {
			MenuItem item = getSystemItem(systemId);
			item.setSelection(roomSystem.getSystemId() == systemId);
			if (container.isAssigned(systemId))
				item.setImage(Cache.checkOutImage(item, "cpath:/assets/tick.png"));
			else {
				Cache.checkInImage(item, "cpath:/assets/tick.png");
				item.setImage(null);
			}
		}
	}

	public void updateData() {
		if (roomC == null)
			return;

		Systems sys = container.getAssignedSystem(roomC.getGameObject());
		SystemController system = container.getSystemController(sys);
		ShipController shipController = container.getShipController();
		boolean playerShip = shipController.isPlayerShip();

		String alias = roomC.getAlias();
		label.setText("Room " + roomC.getId() + (alias == null || alias.equals("") ? "" : " (" + alias + ")"));

		btnSystem.setText(system.toString());

		// enable/disable buttons
		btnInteriorBrowse.setEnabled(system.canContainInterior());
		btnInteriorClear.setEnabled(system.canContainInterior());
		btnInteriorView.setEnabled(system.getInteriorPath() != null);
		glowCombo.setEnabled(system.canContainGlow());

		btnAvailable.setEnabled(system.getSystemId() != Systems.EMPTY);
		scaleSysLevel.setEnabled(system.getSystemId() != Systems.EMPTY);
		if (!playerShip)
			scaleMaxLevel.setEnabled(system.getSystemId() != Systems.EMPTY);

		if (system.getSystemId() != Systems.EMPTY) {
			// update widgets with the system's data
			btnAvailable.setSelection(system.isAvailableAtStart());

			if (!playerShip) {
				scaleMaxLevel.setMaximum(system.getLevelCap());
				scaleMaxLevel.setSelection(system.getLevelMax());
				scaleMaxLevel.notifyListeners(SWT.Selection, null);
			}
			scaleSysLevel.setMaximum(playerShip ? system.getLevelCap() : scaleMaxLevel.getSelection());
			scaleSysLevel.setSelection(system.getLevel());
			scaleSysLevel.notifyListeners(SWT.Selection, null);

			String temp = system.getInteriorPath();
			txtInterior.setText(temp == null ? "" : Utils.trimProtocol(temp));
			txtInterior.selectAll();
		} else {
			// no system - reset to default
			if (!playerShip) {
				scaleMaxLevel.setMaximum(2);
				scaleMaxLevel.setSelection(1);
				txtMaxLevel.setText("");
			}
			scaleSysLevel.setMaximum(2);
			scaleSysLevel.setSelection(1);
			txtSysLevel.setText("");

			txtInterior.setText("");
		}
		OverviewWindow overview = OverviewWindow.getInstance();
		if (overview != null && overview.isVisible())
			overview.update(roomC);
	}

	private MenuItem getSystemItem(Systems systemId) {
		switch (systemId) {
			case ENGINES:
				return mntmEngines;
			case MEDBAY:
				return mntmMedbay;
			case OXYGEN:
				return mntmOxygen;
			case SHIELDS:
				return mntmShields;
			case WEAPONS:
				return mntmWeapons;
			case ARTILLERY:
				return mntmArtillery;
			case CLOAKING:
				return mntmCloaking;
			case DRONES:
				return mntmDrones;
			case TELEPORTER:
				return mntmTeleporter;
			case DOORS:
				return mntmDoors;
			case PILOT:
				return mntmPilot;
			case SENSORS:
				return mntmSensors;
			case CLONEBAY:
				return mntmClonebay;
			case BATTERY:
				return mntmBattery;
			case HACKING:
				return mntmHacking;
			case MIND:
				return mntmMind;
			default:
				return null;
		}
	}

	@Override
	public void setController(AbstractController roomC) {
		this.roomC = (RoomController) roomC;
	}
}