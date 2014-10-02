package com.kartoflane.superluminal2.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

import com.kartoflane.superluminal2.components.enums.Systems;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ftl.ShipObject;
import com.kartoflane.superluminal2.ftl.SystemObject;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.mvc.controllers.SystemController;
import com.kartoflane.superluminal2.tools.Tool.Tools;
import com.kartoflane.superluminal2.undo.UndoablePropertyEdit;
import com.kartoflane.superluminal2.undo.UndoableSystemAssignmentEdit;
import com.kartoflane.superluminal2.undo.UndoableSystemEmptyEdit;
import com.kartoflane.superluminal2.undo.UndoableSystemEmptyEdit.NoSystemsException;

@SuppressWarnings("serial")
public class SystemsMenu {

	private static SystemsMenu instance;

	private ShipContainer container = null;
	private RoomController controller = null;

	private HashMap<SystemObject, MenuItem> systemItemMap = new HashMap<SystemObject, MenuItem>();

	private Menu systemMenu;
	private Menu menuAssign;
	private MenuItem mntmAssign;
	private MenuItem mntmEmpty;
	private ArrayList<MenuItem> mntmArtilleries = new ArrayList<MenuItem>();

	public SystemsMenu(Control parent, RoomController controller) {
		if (instance != null)
			throw new IllegalStateException("Previous instance has not been disposed!");
		instance = this;
		container = Manager.getCurrentShip();
		this.controller = controller;
		createContent(parent);
	}

	public static SystemsMenu getInstance() {
		return instance;
	}

	public void open() {
		if (Manager.getSelectedToolId() != Tools.POINTER)
			throw new IllegalStateException("Manipulation tool is not selected.");
		if (Manager.getSelected() == null)
			throw new IllegalStateException("The selected controller is null.");
		if (Manager.getSelected() instanceof RoomController == false)
			throw new IllegalStateException("The selected controller is not a RoomController.");

		container = Manager.getCurrentShip();

		SystemObject sys = container.getActiveSystem(controller.getGameObject());
		SystemController roomSystem = (SystemController) container.getController(sys);

		mntmEmpty.setSelection(roomSystem.getSystemId() == Systems.EMPTY);

		for (Systems systemId : Systems.getSystems()) {
			for (SystemObject system : container.getShipController().getGameObject().getSystems(systemId)) {
				MenuItem item = getItem(system);

				item.setSelection(roomSystem.getSystemId() == systemId);

				if (system.isAssigned()) {
					Cache.checkInImage(item, systemId.getSmallIcon());
					item.setImage(Cache.checkOutImage(item, "cpath:/assets/tick.png"));
				} else {
					Cache.checkInImage(item, "cpath:/assets/tick.png");
					item.setImage(Cache.checkOutImage(item, systemId.getSmallIcon()));
				}
			}
		}

		systemMenu.setVisible(true);

		Display display = Display.getCurrent();
		while (systemMenu.isVisible()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		dispose();
	}

	public void setLocation(int x, int y) {
		systemMenu.setLocation(x, y);
	}

	public void setLocation(Point p) {
		systemMenu.setLocation(p.x, p.y);
	}

	public void dispose() {
		systemItemMap.clear();
		systemMenu.dispose();
		instance = null;
	}

	public boolean isDisposed() {
		return systemMenu == null || systemMenu.isDisposed();
	}

	private void createContent(Control parent) {
		ShipObject ship = container.getShipController().getGameObject();
		Systems[] systems = null;

		systemMenu = new Menu(parent);

		mntmAssign = new MenuItem(systemMenu, SWT.CASCADE);
		mntmAssign.setText("Assign");

		menuAssign = new Menu(mntmAssign);
		mntmAssign.setMenu(menuAssign);

		mntmEmpty = new MenuItem(menuAssign, SWT.NONE);
		mntmEmpty.setText("Empty");
		mntmEmpty.setData(ship.getSystem(Systems.EMPTY));

		final MenuItem mntmSystem = new MenuItem(menuAssign, SWT.CASCADE);
		mntmSystem.setText("Systems");

		final Menu menuSystems = new Menu(mntmSystem);
		mntmSystem.setMenu(menuSystems);

		systems = new Systems[] { Systems.ENGINES, Systems.MEDBAY,
				Systems.CLONEBAY, Systems.OXYGEN, Systems.SHIELDS, Systems.WEAPONS
		};
		for (Systems sys : systems) {
			for (SystemObject system : ship.getSystems(sys)) {
				SystemController sysC = (SystemController) container.getController(system);
				String alias = sysC.getAlias();
				MenuItem mntm = new MenuItem(menuSystems, SWT.NONE);
				mntm.setText(sys.toString() + (alias == null || alias.trim().equals("") ? "" : " (" + alias + ")"));
				mntm.setData(system);
				systemItemMap.put(system, mntm);
			}
		}

		final MenuItem mntmSubsystem = new MenuItem(menuAssign, SWT.CASCADE);
		mntmSubsystem.setText("Subsystems");

		final Menu menuSubsystems = new Menu(mntmSubsystem);
		mntmSubsystem.setMenu(menuSubsystems);

		systems = new Systems[] { Systems.BATTERY, Systems.DOORS, Systems.PILOT, Systems.SENSORS };
		for (Systems sys : systems) {
			for (SystemObject system : ship.getSystems(sys)) {
				SystemController sysC = (SystemController) container.getController(system);
				String alias = sysC.getAlias();
				MenuItem mntm = new MenuItem(menuSubsystems, SWT.NONE);
				mntm.setText(sys.toString() + (alias == null || alias.trim().equals("") ? "" : " (" + alias + ")"));
				mntm.setData(system);
				systemItemMap.put(system, mntm);
			}
		}

		final MenuItem mntmSpecial = new MenuItem(menuAssign, SWT.CASCADE);
		mntmSpecial.setText("Special");

		final Menu menuSpecial = new Menu(mntmSpecial);
		mntmSpecial.setMenu(menuSpecial);

		systems = new Systems[] { Systems.CLOAKING, Systems.DRONES,
				Systems.HACKING, Systems.MIND, Systems.TELEPORTER
		};
		for (Systems sys : systems) {
			for (SystemObject system : ship.getSystems(sys)) {
				SystemController sysC = (SystemController) container.getController(system);
				String alias = sysC.getAlias();
				MenuItem mntm = new MenuItem(menuSpecial, SWT.NONE);
				mntm.setText(sys.toString() + (alias == null || alias.trim().equals("") ? "" : " (" + alias + ")"));
				mntm.setData(system);
				systemItemMap.put(system, mntm);
			}
		}

		final MenuItem mntmArtillery = new MenuItem(menuAssign, SWT.CASCADE);
		mntmArtillery.setText("Artillery");

		final Menu menuArtillery = new Menu(mntmArtillery);
		mntmArtillery.setMenu(menuArtillery);

		for (SystemObject sys : ship.getSystems(Systems.ARTILLERY)) {
			SystemController sysC = (SystemController) container.getController(sys);
			String alias = sysC.getAlias();
			MenuItem mntm = new MenuItem(menuArtillery, SWT.NONE);
			mntm.setText(sys.toString() + (alias == null || alias.trim().equals("") ? "" : " (" + alias + ")"));
			mntm.setData(sys);
			systemItemMap.put(sys, mntm);
			mntmArtilleries.add(mntm);
		}

		SelectionAdapter systemListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Widget w = (Widget) e.getSource();

				if (w == mntmEmpty) {
					try {
						container.postEdit(new UndoableSystemEmptyEdit(controller));
					} catch (NoSystemsException ex) {
						// Ignore, only serves to prevent the edit from being posted
					}

					for (SystemObject system : container.getAllAssignedSystems(controller.getGameObject())) {
						container.unassign(system);
					}
				} else {
					SystemObject so = (SystemObject) w.getData();
					SystemController sys = (SystemController) container.getController(so);
					RoomController rc = (RoomController) container.getController(so.getRoom());

					container.assign((SystemObject) w.getData(), controller);

					UndoableSystemAssignmentEdit edit = new UndoableSystemAssignmentEdit(sys);
					edit.setOld(rc);
					edit.setCurrent(controller);
					container.postEdit(edit);
				}

				EditorWindow.getInstance().updateSidebarContent();
				controller.redraw();
				OverviewWindow.staticUpdate(controller);
			}
		};

		mntmEmpty.addSelectionListener(systemListener);
		for (MenuItem mntm : systemItemMap.values()) {
			mntm.addSelectionListener(systemListener);
		}

		ArrayList<SystemObject> assignedSystems = container.getAllAssignedSystems(controller.getGameObject());
		if (assignedSystems.size() > 0) {
			new MenuItem(systemMenu, SWT.SEPARATOR);

			for (SystemObject sys : assignedSystems)
				createSystemSubmenu(sys);
		}
	}

	private MenuItem createSystemSubmenu(final SystemObject sys) {
		MenuItem mntmSystem = new MenuItem(systemMenu, SWT.CASCADE);
		String alias = sys.getAlias();
		mntmSystem.setText(sys.toString() + (alias == null || alias.trim().equals("") ? "" : " (" + alias + ")"));

		final Menu menuOptions = new Menu(mntmSystem);
		mntmSystem.setMenu(menuOptions);

		final MenuItem mntmSelect = new MenuItem(menuOptions, SWT.NONE);
		mntmSelect.setText("Select");

		final MenuItem mntmUnassign = new MenuItem(menuOptions, SWT.NONE);
		mntmUnassign.setText("Unassign");

		mntmSelect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				UndoablePropertyEdit<SystemObject> edit = new UndoablePropertyEdit<SystemObject>(controller) {
					@Override
					public void callback(SystemObject arg) {
						RoomController controller = (RoomController) data;
						container.setActiveSystem(controller.getGameObject(), arg);
						EditorWindow.getInstance().updateSidebarContent();
					}

					@Override
					public String getPresentationName() {
						return "change active system";
					}
				};

				edit.setOld(container.getActiveSystem(controller.getGameObject()));
				edit.setCurrent(sys);

				container.setActiveSystem(controller.getGameObject(), sys);
				EditorWindow.getInstance().updateSidebarContent();

				if (!edit.isValuesEqual())
					Manager.postEdit(edit);
			}
		});

		mntmUnassign.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SystemController sc = (SystemController) container.getController(sys);
				RoomController rc = (RoomController) container.getController(sys.getRoom());

				container.unassign(sys);
				EditorWindow.getInstance().updateSidebarContent();

				UndoableSystemAssignmentEdit edit = new UndoableSystemAssignmentEdit(sc);
				edit.setOld(rc);
				edit.setCurrent(null);
				container.postEdit(edit);
			}
		});

		return mntmSystem;
	}

	private MenuItem getItem(SystemObject sys) {
		return systemItemMap.get(sys);
	}
}
