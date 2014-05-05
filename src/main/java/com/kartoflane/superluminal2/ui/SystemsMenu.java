package com.kartoflane.superluminal2.ui;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.kartoflane.superluminal2.components.enums.Systems;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.mvc.controllers.SystemController;
import com.kartoflane.superluminal2.tools.Tool.Tools;
import com.kartoflane.superluminal2.ui.sidebar.ManipulationToolComposite;
import com.kartoflane.superluminal2.ui.sidebar.data.RoomDataComposite;

public class SystemsMenu {

	private static SystemsMenu instance;

	private ShipContainer container = null;
	private RoomController controller = null;
	private RoomDataComposite dataComposite = null;

	private Menu systemMenu;
	private Menu menuAssign;
	private MenuItem mntmAssign;
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
	private MenuItem mntmClonebay;
	private MenuItem mntmBattery;
	private MenuItem mntmHacking;
	private MenuItem mntmMind;

	public SystemsMenu(Control parent) {
		instance = this;
		createContent(parent);
	}

	public static SystemsMenu getInstance() {
		return instance;
	}

	public void setController(RoomController controller) {
		this.controller = controller;
	}

	public void open() {
		if (Manager.getSelectedToolId() != Tools.POINTER)
			throw new IllegalStateException("Manipulation tool is not selected.");
		if (Manager.getSelected() instanceof RoomController == false)
			throw new IllegalStateException("The selected controller is not a RoomController.");

		ManipulationToolComposite mtc = (ManipulationToolComposite) EditorWindow.getInstance().getSidebarContent();
		dataComposite = (RoomDataComposite) mtc.getDataComposite();
		container = Manager.getCurrentShip();

		Systems sys = container.getActiveSystem(controller.getGameObject());
		SystemController roomSystem = container.getSystemController(sys);

		mntmEmpty.setSelection(roomSystem.getSystemId() == Systems.EMPTY);

		for (Systems systemId : Systems.getSystems()) {
			MenuItem item = getSystemItem(systemId);
			item.setSelection(roomSystem.getSystemId() == systemId);

			if (container.isAssigned(systemId)) {
				item.setImage(Cache.checkOutImage(item, "cpath:/assets/tick.png"));
			} else {
				Cache.checkInImage(item, "cpath:/assets/tick.png");
				item.setImage(null);
			}
		}

		systemMenu.setVisible(true);
	}

	public void setLocation(int x, int y) {
		systemMenu.setLocation(x, y);
	}

	public void setLocation(Point p) {
		systemMenu.setLocation(p.x, p.y);
	}

	public void disposeSystemSubmenus() {
		for (MenuItem item : systemMenu.getItems()) {
			if (item != mntmAssign)
				item.dispose();
		}
	}

	public void createSystemSubmenus() {
		container = Manager.getCurrentShip();
		ArrayList<Systems> assignedSystems = container.getAllAssignedSystems(controller.getGameObject());
		if (assignedSystems.size() > 0) {
			new MenuItem(systemMenu, SWT.SEPARATOR);

			for (Systems sys : assignedSystems)
				createSystemSubmenu(sys);
		}
	}

	public void dispose() {
		systemMenu.dispose();
	}

	public boolean isDisposed() {
		return systemMenu == null || systemMenu.isDisposed();
	}

	private void createContent(Control parent) {
		systemMenu = new Menu(parent);
		parent.setMenu(systemMenu);

		mntmAssign = new MenuItem(systemMenu, SWT.CASCADE);
		mntmAssign.setText("Assign");

		menuAssign = new Menu(mntmAssign);
		mntmAssign.setMenu(menuAssign);

		mntmEmpty = new MenuItem(menuAssign, SWT.NONE);
		mntmEmpty.setText("Empty");

		final MenuItem mntmSystem = new MenuItem(menuAssign, SWT.CASCADE);
		mntmSystem.setText("Systems");

		final Menu menuSystems = new Menu(mntmSystem);
		mntmSystem.setMenu(menuSystems);

		mntmEngines = new MenuItem(menuSystems, SWT.NONE);
		mntmEngines.setText("Engines");

		mntmMedbay = new MenuItem(menuSystems, SWT.NONE);
		mntmMedbay.setText("Medbay");

		mntmClonebay = new MenuItem(menuSystems, SWT.NONE);
		mntmClonebay.setText("Clonebay");

		mntmOxygen = new MenuItem(menuSystems, SWT.NONE);
		mntmOxygen.setText("Oxygen");

		mntmShields = new MenuItem(menuSystems, SWT.NONE);
		mntmShields.setText("Shields");

		mntmWeapons = new MenuItem(menuSystems, SWT.NONE);
		mntmWeapons.setText("Weapons");

		final MenuItem mntmSubsystem = new MenuItem(menuAssign, SWT.CASCADE);
		mntmSubsystem.setText("Subsystems");

		final Menu menuSubsystems = new Menu(mntmSubsystem);
		mntmSubsystem.setMenu(menuSubsystems);

		mntmBattery = new MenuItem(menuSubsystems, SWT.NONE);
		mntmBattery.setText("Battery");

		mntmDoors = new MenuItem(menuSubsystems, SWT.NONE);
		mntmDoors.setText("Doors");

		mntmPilot = new MenuItem(menuSubsystems, SWT.NONE);
		mntmPilot.setText("Pilot");

		mntmSensors = new MenuItem(menuSubsystems, SWT.NONE);
		mntmSensors.setText("Sensors");

		final MenuItem mntmSpecial = new MenuItem(menuAssign, SWT.CASCADE);
		mntmSpecial.setText("Special");

		final Menu menuSpecial = new Menu(mntmSpecial);
		mntmSpecial.setMenu(menuSpecial);

		mntmArtillery = new MenuItem(menuSpecial, SWT.NONE);
		mntmArtillery.setText("Artillery");

		mntmCloaking = new MenuItem(menuSpecial, SWT.NONE);
		mntmCloaking.setText("Cloaking");

		mntmDrones = new MenuItem(menuSpecial, SWT.NONE);
		mntmDrones.setText("Drone Control");

		mntmHacking = new MenuItem(menuSpecial, SWT.NONE);
		mntmHacking.setText("Hacking");

		mntmMind = new MenuItem(menuSpecial, SWT.NONE);
		mntmMind.setText("Mind Control");

		mntmTeleporter = new MenuItem(menuSpecial, SWT.NONE);
		mntmTeleporter.setText("Teleporter");

		SelectionAdapter systemListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.getSource() == mntmEmpty) {
					for (Systems sys : container.getAllAssignedSystems(controller.getGameObject()))
						container.unassign(sys);

					container.assign(Systems.EMPTY, controller);
				} else if (e.getSource() == mntmEngines) {
					container.assign(Systems.ENGINES, controller);
				} else if (e.getSource() == mntmMedbay) {
					container.assign(Systems.MEDBAY, controller);
				} else if (e.getSource() == mntmOxygen) {
					container.assign(Systems.OXYGEN, controller);
				} else if (e.getSource() == mntmShields) {
					container.assign(Systems.SHIELDS, controller);
				} else if (e.getSource() == mntmWeapons) {
					container.assign(Systems.WEAPONS, controller);
				} else if (e.getSource() == mntmArtillery) {
					container.assign(Systems.ARTILLERY, controller);
				} else if (e.getSource() == mntmCloaking) {
					container.assign(Systems.CLOAKING, controller);
				} else if (e.getSource() == mntmDrones) {
					container.assign(Systems.DRONES, controller);
				} else if (e.getSource() == mntmTeleporter) {
					container.assign(Systems.TELEPORTER, controller);
				} else if (e.getSource() == mntmDoors) {
					container.assign(Systems.DOORS, controller);
				} else if (e.getSource() == mntmPilot) {
					container.assign(Systems.PILOT, controller);
				} else if (e.getSource() == mntmSensors) {
					container.assign(Systems.SENSORS, controller);

					// AE contenet:
				} else if (e.getSource() == mntmClonebay) {
					container.assign(Systems.CLONEBAY, controller);
				} else if (e.getSource() == mntmHacking) {
					container.assign(Systems.HACKING, controller);
				} else if (e.getSource() == mntmMind) {
					container.assign(Systems.MIND, controller);
				} else if (e.getSource() == mntmBattery) {
					container.assign(Systems.BATTERY, controller);
				}

				dataComposite.updateData();
				controller.redraw();
				OverviewWindow.staticUpdate(controller);
			}
		};

		mntmEmpty.addSelectionListener(systemListener);
		mntmArtillery.addSelectionListener(systemListener);
		mntmCloaking.addSelectionListener(systemListener);
		mntmDrones.addSelectionListener(systemListener);
		mntmTeleporter.addSelectionListener(systemListener);
		mntmHacking.addSelectionListener(systemListener);
		mntmMind.addSelectionListener(systemListener);
		mntmDoors.addSelectionListener(systemListener);
		mntmPilot.addSelectionListener(systemListener);
		mntmSensors.addSelectionListener(systemListener);
		mntmBattery.addSelectionListener(systemListener);
		mntmEngines.addSelectionListener(systemListener);
		mntmMedbay.addSelectionListener(systemListener);
		mntmOxygen.addSelectionListener(systemListener);
		mntmShields.addSelectionListener(systemListener);
		mntmWeapons.addSelectionListener(systemListener);
		mntmClonebay.addSelectionListener(systemListener);
	}

	private MenuItem createSystemSubmenu(final Systems sys) {
		MenuItem mntmSystem = new MenuItem(systemMenu, SWT.CASCADE);
		mntmSystem.setText(sys.toString());

		final Menu menuOptions = new Menu(mntmSystem);
		mntmSystem.setMenu(menuOptions);

		final MenuItem mntmSelect = new MenuItem(menuOptions, SWT.NONE);
		mntmSelect.setText("Select");

		final MenuItem mntmUnassign = new MenuItem(menuOptions, SWT.NONE);
		mntmUnassign.setText("Unassign");

		mntmSelect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				container.setActiveSystem(controller.getGameObject(), sys);
				dataComposite.updateData();
			}
		});

		mntmUnassign.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				container.unassign(sys);
				dataComposite.updateData();
			}
		});

		return mntmSystem;
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
}
