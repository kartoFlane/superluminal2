package com.kartoflane.superluminal2.ui.sidebar;

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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;

import com.kartoflane.superluminal2.core.Superluminal;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;
import com.kartoflane.superluminal2.mvc.controllers.SystemController;
import com.kartoflane.superluminal2.mvc.models.SystemModel;
import com.kartoflane.superluminal2.mvc.models.SystemModel.Glows;
import com.kartoflane.superluminal2.mvc.models.SystemModel.Systems;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.OverviewWindow;

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
	private Group grpSystemImages;
	private Button btnInteriorBrowse;
	private Button btnInteriorClear;
	private Text txtInterior;
	private Button btnInteriorView;
	private Text txtGlow1;
	private Button btnGlow1Browse;
	private Button btnGlow1Clear;
	private Button btnGlow1View;
	private Text txtGlow2;
	private Button btnGlow2Browse;
	private Button btnGlow2Clear;
	private Button btnGlow2View;
	private Text txtGlow3;
	private Button btnGlow3Browse;
	private Button btnGlow3Clear;
	private Button btnGlow3View;

	private RoomController controller = null;

	public RoomDataComposite(Composite parent, RoomController control) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(3, false));

		this.controller = control;

		Label label = new Label(this, SWT.NONE);
		label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 3, 1));
		label.setText(controller.getModel().getSystem() == null ? "Room" : "Room / System");

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
				controller.getModel().getSystem().setLevel(scaleSysLevel.getSelection());
				txtSysLevel.setText("" + scaleSysLevel.getSelection());
			}
		});

		if (!controller.getShipController().getModel().isPlayerShip()) {
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
					controller.getModel().getSystem().setLevelMax(scaleMaxLevel.getSelection());
					txtMaxLevel.setText("" + scaleMaxLevel.getSelection());
					if (!controller.getShipController().getModel().isPlayerShip()) {
						scaleSysLevel.setMaximum(scaleMaxLevel.getSelection());
						scaleSysLevel.notifyListeners(SWT.Selection, null);
					}
				}
			});
		}

		grpSystemImages = new Group(this, SWT.NONE);
		grpSystemImages.setLayout(new GridLayout(4, false));
		grpSystemImages.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		grpSystemImages.setText("System Images");

		// Interior widgets
		Label lblInterior = new Label(grpSystemImages, SWT.NONE);
		lblInterior.setText("Interior");

		btnInteriorView = new Button(grpSystemImages, SWT.NONE);
		btnInteriorView.setEnabled(false);
		btnInteriorView.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnInteriorView.setText("View");

		btnInteriorBrowse = new Button(grpSystemImages, SWT.NONE);
		btnInteriorBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnInteriorBrowse.setText("Browse");

		btnInteriorClear = new Button(grpSystemImages, SWT.NONE);
		btnInteriorClear.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnInteriorClear.setText("Clear");

		txtInterior = new Text(grpSystemImages, SWT.BORDER | SWT.READ_ONLY);
		txtInterior.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		// Glow 1 widgets
		Label lblGlow1 = new Label(grpSystemImages, SWT.NONE);
		lblGlow1.setText("Blue Glow");

		btnGlow1View = new Button(grpSystemImages, SWT.NONE);
		btnGlow1View.setEnabled(false);
		btnGlow1View.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnGlow1View.setText("View");

		btnGlow1Browse = new Button(grpSystemImages, SWT.NONE);
		btnGlow1Browse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnGlow1Browse.setText("Browse");

		btnGlow1Clear = new Button(grpSystemImages, SWT.NONE);
		btnGlow1Clear.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnGlow1Clear.setText("Clear");

		txtGlow1 = new Text(grpSystemImages, SWT.BORDER | SWT.READ_ONLY);
		txtGlow1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		// Glow 2 widgets
		Label lblGlow2 = new Label(grpSystemImages, SWT.NONE);
		lblGlow2.setText("Green Glow");

		btnGlow2View = new Button(grpSystemImages, SWT.NONE);
		btnGlow2View.setEnabled(false);
		btnGlow2View.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnGlow2View.setText("View");

		btnGlow2Browse = new Button(grpSystemImages, SWT.NONE);
		btnGlow2Browse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnGlow2Browse.setText("Browse");

		btnGlow2Clear = new Button(grpSystemImages, SWT.NONE);
		btnGlow2Clear.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnGlow2Clear.setText("Clear");

		txtGlow2 = new Text(grpSystemImages, SWT.BORDER | SWT.READ_ONLY);
		txtGlow2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		// Glow 3 widgets
		Label lblGlow3 = new Label(grpSystemImages, SWT.NONE);
		lblGlow3.setText("Gold Glow");

		btnGlow3View = new Button(grpSystemImages, SWT.NONE);
		btnGlow3View.setEnabled(false);
		btnGlow3View.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnGlow3View.setText("View");

		btnGlow3Browse = new Button(grpSystemImages, SWT.NONE);
		btnGlow3Browse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnGlow3Browse.setText("Browse");

		btnGlow3Clear = new Button(grpSystemImages, SWT.NONE);
		btnGlow3Clear.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnGlow3Clear.setText("Clear");

		txtGlow3 = new Text(grpSystemImages, SWT.BORDER | SWT.READ_ONLY);
		txtGlow3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

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
				controller.getSystemController().setAvailableAtStart(btnAvailable.getSelection());
			}
		});

		SelectionAdapter imageViewListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				File file = null;
				if (e.getSource() == btnInteriorView)
					file = new File(controller.getModel().getSystem().getInteriorPath());
				else if (e.getSource() == btnGlow1View)
					file = new File(controller.getModel().getSystem().getGlowPath(Glows.BLUE));
				else if (e.getSource() == btnGlow2View)
					file = new File(controller.getModel().getSystem().getGlowPath(Glows.GREEN));
				else if (e.getSource() == btnGlow3View)
					file = new File(controller.getModel().getSystem().getGlowPath(Glows.YELLOW));

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
		btnGlow1View.addSelectionListener(imageViewListener);
		btnGlow2View.addSelectionListener(imageViewListener);
		btnGlow3View.addSelectionListener(imageViewListener);

		SelectionAdapter imageBrowseListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(EditorWindow.getInstance().getShell());
				String path = dialog.open();

				Glows type = null;
				if (e.getSource() == btnGlow1Browse)
					type = Glows.BLUE;
				else if (e.getSource() == btnGlow2Browse)
					type = Glows.GREEN;
				else if (e.getSource() == btnGlow3Browse)
					type = Glows.YELLOW;

				// path == null only when user cancels
				if (path != null) {
					if (type == null)
						controller.getSystemController().setInterior(path);
					else
						controller.getSystemController().setGlow(type, path);
					updateData();
				}
			}
		};
		btnInteriorBrowse.addSelectionListener(imageBrowseListener);
		btnGlow1Browse.addSelectionListener(imageBrowseListener);
		btnGlow2Browse.addSelectionListener(imageBrowseListener);
		btnGlow3Browse.addSelectionListener(imageBrowseListener);

		SelectionAdapter imageClearListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Glows type = null;
				if (e.getSource() == btnGlow1Clear)
					type = Glows.BLUE;
				else if (e.getSource() == btnGlow2Clear)
					type = Glows.GREEN;
				else if (e.getSource() == btnGlow3Clear)
					type = Glows.YELLOW;

				if (type == null)
					controller.getSystemController().setInterior(null);
				else
					controller.getSystemController().setGlow(type, null);
				updateData();
			}
		};
		btnInteriorClear.addSelectionListener(imageClearListener);
		btnGlow1Clear.addSelectionListener(imageClearListener);
		btnGlow2Clear.addSelectionListener(imageClearListener);
		btnGlow3Clear.addSelectionListener(imageClearListener);

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

		ShipController shipController = controller.getShipController();

		mntmEngines = new MenuItem(menu, SWT.RADIO);
		mntmEngines.setText("Engines");
		mntmEngines.setEnabled(!shipController.getModel().getSystem(Systems.ENGINES).isAssigned());

		mntmMedbay = new MenuItem(menu, SWT.RADIO);
		mntmMedbay.setText("Medbay");
		mntmMedbay.setEnabled(!shipController.getModel().getSystem(Systems.MEDBAY).isAssigned());

		mntmOxygen = new MenuItem(menu, SWT.RADIO);
		mntmOxygen.setText("Oxygen");
		mntmOxygen.setEnabled(!shipController.getModel().getSystem(Systems.OXYGEN).isAssigned());

		mntmShields = new MenuItem(menu, SWT.RADIO);
		mntmShields.setText("Shields");
		mntmShields.setEnabled(!shipController.getModel().getSystem(Systems.SHIELDS).isAssigned());

		mntmWeapons = new MenuItem(menu, SWT.RADIO);
		mntmWeapons.setText("Weapons");
		mntmWeapons.setEnabled(!shipController.getModel().getSystem(Systems.WEAPONS).isAssigned());

		MenuItem mntmSubsystem = new MenuItem(systemMenu, SWT.CASCADE);
		mntmSubsystem.setText("Subsystems");

		Menu menu_1 = new Menu(mntmSubsystem);
		mntmSubsystem.setMenu(menu_1);

		mntmDoors = new MenuItem(menu_1, SWT.RADIO);
		mntmDoors.setText("Doors");
		mntmDoors.setEnabled(!shipController.getModel().getSystem(Systems.DOORS).isAssigned());

		mntmPilot = new MenuItem(menu_1, SWT.RADIO);
		mntmPilot.setText("Pilot");
		mntmPilot.setEnabled(!shipController.getModel().getSystem(Systems.PILOT).isAssigned());

		mntmSensors = new MenuItem(menu_1, SWT.RADIO);
		mntmSensors.setText("Sensors");
		mntmSensors.setEnabled(!shipController.getModel().getSystem(Systems.SENSORS).isAssigned());

		MenuItem mntmSpecial = new MenuItem(systemMenu, SWT.CASCADE);
		mntmSpecial.setText("Special");

		Menu menu_2 = new Menu(mntmSpecial);
		mntmSpecial.setMenu(menu_2);

		mntmArtillery = new MenuItem(menu_2, SWT.RADIO);
		mntmArtillery.setText("Artillery");
		mntmArtillery.setEnabled(!shipController.getModel().getSystem(Systems.ARTILLERY).isAssigned());

		mntmCloaking = new MenuItem(menu_2, SWT.RADIO);
		mntmCloaking.setText("Cloaking");
		mntmCloaking.setEnabled(!shipController.getModel().getSystem(Systems.CLOAK).isAssigned());

		mntmDrones = new MenuItem(menu_2, SWT.RADIO);
		mntmDrones.setText("Drone Control");
		mntmDrones.setEnabled(!shipController.getModel().getSystem(Systems.DRONES).isAssigned());

		mntmTeleporter = new MenuItem(menu_2, SWT.RADIO);
		mntmTeleporter.setText("Teleporter");
		mntmTeleporter.setEnabled(!shipController.getModel().getSystem(Systems.TELEPORTER).isAssigned());

		SelectionAdapter systemListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ShipController shipController = controller.getShipController();
				if (e.getSource() == mntmEmpty) {
					shipController.getSystemController(Systems.EMPTY).assignTo(controller);
					btnSystem.setText(Systems.EMPTY.toString());
				} else if (e.getSource() == mntmEngines) {
					shipController.getSystemController(Systems.ENGINES).assignTo(controller);
					btnSystem.setText(Systems.ENGINES.toString());
				} else if (e.getSource() == mntmMedbay) {
					shipController.getSystemController(Systems.MEDBAY).assignTo(controller);
					btnSystem.setText(Systems.MEDBAY.toString());
				} else if (e.getSource() == mntmOxygen) {
					shipController.getSystemController(Systems.OXYGEN).assignTo(controller);
					btnSystem.setText(Systems.OXYGEN.toString());
				} else if (e.getSource() == mntmShields) {
					shipController.getSystemController(Systems.SHIELDS).assignTo(controller);
					btnSystem.setText(Systems.SHIELDS.toString());
				} else if (e.getSource() == mntmWeapons) {
					shipController.getSystemController(Systems.WEAPONS).assignTo(controller);
					btnSystem.setText(Systems.WEAPONS.toString());
				} else if (e.getSource() == mntmArtillery) {
					shipController.getSystemController(Systems.ARTILLERY).assignTo(controller);
					btnSystem.setText(Systems.ARTILLERY.toString());
				} else if (e.getSource() == mntmCloaking) {
					shipController.getSystemController(Systems.CLOAK).assignTo(controller);
					btnSystem.setText(Systems.CLOAK.toString());
				} else if (e.getSource() == mntmDrones) {
					shipController.getSystemController(Systems.DRONES).assignTo(controller);
					btnSystem.setText(Systems.DRONES.toString());
				} else if (e.getSource() == mntmTeleporter) {
					shipController.getSystemController(Systems.TELEPORTER).assignTo(controller);
					btnSystem.setText(Systems.TELEPORTER.toString());
				} else if (e.getSource() == mntmDoors) {
					shipController.getSystemController(Systems.DOORS).assignTo(controller);
					btnSystem.setText(Systems.DOORS.toString());
				} else if (e.getSource() == mntmPilot) {
					shipController.getSystemController(Systems.PILOT).assignTo(controller);
					btnSystem.setText(Systems.PILOT.toString());
				} else if (e.getSource() == mntmSensors) {
					shipController.getSystemController(Systems.SENSORS).assignTo(controller);
					btnSystem.setText(Systems.SENSORS.toString());
				}

				updateData();
				EditorWindow.getInstance().canvasRedraw(controller.getBounds());
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

		updateData();

		pack();
	}

	public void showSystemMenu() {
		systemMenu.setVisible(true);

		mntmEmpty.setSelection(controller.getModel().getSystem().getId() == Systems.EMPTY);
		for (Systems systemId : Systems.getSystems()) {
			MenuItem item = getSystemItem(systemId);
			item.setEnabled(!controller.getShipController().getModel().getSystem(systemId).isAssigned());
			item.setSelection(controller.getModel().getSystem().getId() == systemId);
		}
	}

	public void updateData() {
		if (controller == null)
			return;

		SystemController systemController = controller.getSystemController();
		SystemModel systemModel = systemController.getModel();
		ShipController shipController = controller.getShipController();
		boolean playerShip = shipController.getModel().isPlayerShip();

		btnSystem.setText(controller.getModel().getSystem().getId().toString());

		// enable/disable buttons
		btnInteriorBrowse.setEnabled(systemModel.canContainInterior());
		btnInteriorClear.setEnabled(systemModel.canContainInterior());
		btnInteriorView.setEnabled(systemModel.getInteriorPath() != null);

		btnGlow1Browse.setEnabled(systemModel.canContainGlow());
		btnGlow1Clear.setEnabled(systemModel.canContainGlow());
		btnGlow1View.setEnabled(systemModel.getGlowPath(Glows.BLUE) != null);
		// cloak only uses the first glow image (for the blue glow when activated)
		btnGlow2Browse.setEnabled(systemModel.canContainGlow() && systemModel.getId() != Systems.CLOAK);
		btnGlow2Clear.setEnabled(systemModel.canContainGlow() && systemModel.getId() != Systems.CLOAK);
		btnGlow1View.setEnabled(systemModel.getGlowPath(Glows.GREEN) != null);
		btnGlow3Browse.setEnabled(systemModel.canContainGlow() && systemModel.getId() != Systems.CLOAK);
		btnGlow3Clear.setEnabled(systemModel.canContainGlow() && systemModel.getId() != Systems.CLOAK);
		btnGlow1View.setEnabled(systemModel.getGlowPath(Glows.YELLOW) != null);

		btnAvailable.setEnabled(systemModel.getId() != Systems.EMPTY);
		scaleSysLevel.setEnabled(systemModel.getId() != Systems.EMPTY);
		if (!playerShip)
			scaleMaxLevel.setEnabled(systemModel.getId() != Systems.EMPTY);

		if (systemModel.getId() != Systems.EMPTY) {
			// update widgets with the system's data
			btnAvailable.setSelection(systemModel.isAvailableAtStart());

			if (!playerShip) {
				scaleMaxLevel.setMaximum(systemModel.getLevelCap());
				scaleMaxLevel.setSelection(systemModel.getLevelMax());
				scaleMaxLevel.notifyListeners(SWT.Selection, null);
			}
			scaleSysLevel.setMaximum(playerShip ? systemModel.getLevelCap() : scaleMaxLevel.getSelection());
			scaleSysLevel.setSelection(systemModel.getLevel());
			scaleSysLevel.notifyListeners(SWT.Selection, null);

			String temp = systemModel.getInteriorPath();
			txtInterior.setText(temp == null ? "" : temp);
			txtInterior.selectAll();
			temp = systemModel.getGlowPath(Glows.BLUE);
			txtGlow1.setText(temp == null ? "" : temp);
			txtGlow1.selectAll();
			temp = systemModel.getGlowPath(Glows.GREEN);
			txtGlow2.setText(temp == null ? "" : temp);
			txtGlow2.selectAll();
			temp = systemModel.getGlowPath(Glows.YELLOW);
			txtGlow3.setText(temp == null ? "" : temp);
			txtGlow3.selectAll();
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
			txtGlow1.setText("");
			txtGlow2.setText("");
			txtGlow3.setText("");
		}
		OverviewWindow overview = OverviewWindow.getInstance();
		if (overview != null && overview.isVisible())
			overview.update(controller);
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
			case CLOAK:
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
			default:
				return null;
		}
	}

	@Override
	public RoomController getController() {
		return controller;
	}

	@Override
	public void setController(AbstractController controller) {
		this.controller = (RoomController) controller;
	}
}