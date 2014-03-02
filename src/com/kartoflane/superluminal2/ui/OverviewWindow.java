package com.kartoflane.superluminal2.ui;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.kartoflane.superluminal2.components.interfaces.Alias;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.core.Superluminal;
import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.DoorController;
import com.kartoflane.superluminal2.mvc.controllers.MountController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;
import com.kartoflane.superluminal2.mvc.models.MountModel;
import com.kartoflane.superluminal2.mvc.models.RoomModel;
import com.kartoflane.superluminal2.tools.Tool.Tools;

public class OverviewWindow {

	private static OverviewWindow instance = null;

	private ShipController ship;
	private Controller highlightedController = null;
	private HashMap<Controller, TreeItem> carrierMap = new HashMap<Controller, TreeItem>();

	private Shell shell;
	private TreeItem trtmRooms;
	private TreeItem trtmDoors;
	private TreeItem trtmMounts;
	private Tree tree;
	private Menu overviewMenu;

	public OverviewWindow(Shell parent) {
		instance = this;

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.MIN | SWT.RESIZE);
		shell.setSize(250, 350);
		Point pLoc = parent.getLocation();
		shell.setLocation(pLoc.x + parent.getSize().x - shell.getSize().x, pLoc.y + 50);
		shell.setText(String.format("%s - Ship Overview", Superluminal.APP_NAME));
		shell.setLayout(new GridLayout(1, false));

		tree = new Tree(shell, SWT.BORDER);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		trtmRooms = new TreeItem(tree, SWT.NONE);
		trtmRooms.setText("Rooms");

		trtmDoors = new TreeItem(tree, SWT.NONE);
		trtmDoors.setText("Doors");

		trtmMounts = new TreeItem(tree, SWT.NONE);
		trtmMounts.setText("Mounts");

		// Overview popup menu
		overviewMenu = new Menu(tree);
		tree.setMenu(overviewMenu);

		final MenuItem mntmSetAlias = new MenuItem(overviewMenu, SWT.NONE);
		mntmSetAlias.setText("Set Alias");

		final MenuItem mntmRemoveAlias = new MenuItem(overviewMenu, SWT.NONE);
		mntmRemoveAlias.setText("Remove Alias");

		// Finalize

		shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event) {
				close();
				event.doit = false;
			}
		});

		shell.addListener(SWT.FocusIn, new Listener() {
			@Override
			public void handleEvent(Event e) {
				update();
				tree.setFocus();
			}
		});

		tree.addListener(SWT.FocusIn, new Listener() {
			@Override
			public void handleEvent(Event e) {
				update();
			}
		});

		tree.addListener(SWT.MouseMove, new Listener() {
			@Override
			public void handleEvent(Event e) {
				TreeItem item = tree.getItem(new Point(e.x, e.y));
				Controller controller = null;
				if (item != null && item.getData() != null)
					controller = (Controller) item.getData();

				if (highlightedController != null && highlightedController != controller && highlightedController.getView().isHighlighted())
					highlightedController.getView().setHighlighted(false);
				if (controller != null && !controller.getView().isHighlighted())
					controller.getView().setHighlighted(true);
				highlightedController = controller;
			}
		});

		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (tree.getSelectionCount() == 0 || Manager.getSelectedToolId() != Tools.POINTER)
					return;

				TreeItem item = tree.getSelection()[0];
				AbstractController controller = (AbstractController) item.getData();
				Manager.setSelected(controller);
			}
		});

		overviewMenu.addMenuListener(new MenuListener() {
			@Override
			public void menuHidden(MenuEvent e) {
			}

			@Override
			public void menuShown(MenuEvent e) {
				if (tree.getSelectionCount() == 0 || tree.getSelection()[0].getData() == null) {
					overviewMenu.setVisible(false);
					return;
				}

				Alias aliasable = (Alias) tree.getSelection()[0].getData();
				String alias = aliasable.getAlias();
				mntmSetAlias.setEnabled(true);
				mntmRemoveAlias.setEnabled(alias != null);
			}
		});

		mntmSetAlias.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Alias alias = (Alias) tree.getSelection()[0].getData();
				AliasDialog dialog = new AliasDialog(shell, alias);
				dialog.open();
				update(tree.getSelection()[0]);
			}
		});

		mntmRemoveAlias.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Alias alias = (Alias) tree.getSelection()[0].getData();
				alias.setAlias(null);
				update(tree.getSelection()[0]);
			}
		});
	}

	public void open() {
		shell.open();
		update();

		shell.setMinimized(false);
	}

	public void close() {
		shell.setVisible(false);
	}

	public void update() {
		ship = Manager.getCurrentShip();

		for (TreeItem it : trtmRooms.getItems())
			it.dispose();
		for (TreeItem it : trtmDoors.getItems())
			it.dispose();
		for (TreeItem it : trtmMounts.getItems())
			it.dispose();
		carrierMap.clear();

		for (RoomController r : ship.getRoomControllers())
			createItem(r);
		for (DoorController d : ship.getDoorControllers())
			createItem(d);
		for (MountController m : ship.getMountControllers())
			createItem(m);

		setEnabled(Manager.getSelectedToolId() == Tools.POINTER);
	}

	public void update(Controller controller) {
		TreeItem item = carrierMap.get(controller);
		if (item == null) {
			item = createItem(controller);
			carrierMap.put(controller, item);
			return; // no need to update, since we've already created a new item
		}
		// throw new IllegalArgumentException("DataCarrier " + data + " has not yet been added to the manager.");

		update(item);
	}

	private void update(TreeItem item) {
		Object data = item.getData();

		if (data instanceof RoomController) {
			RoomController controller = (RoomController) data;
			RoomModel model = controller.getModel();
			StringBuilder buf = new StringBuilder();
			buf.append("Room " + model.getId());

			if (model.getSystem() != null)
				buf.append(": " + model.getSystem().toString());
			String alias = model.getAlias();
			if (alias != null)
				buf.append(" - \"" + alias + "\"");

			item.setText(buf.toString());

			if (controller.isSelected())
				tree.select(item);
		} else if (data instanceof DoorController) {
			DoorController controller = (DoorController) data;

			StringBuilder buf = new StringBuilder();
			buf.append("Door " + (controller.getModel().isHorizontal() ? "H" : "V"));

			String alias = controller.getAlias();
			if (alias != null)
				buf.append(" - \"" + alias + "\"");

			item.setText(buf.toString());

			if (controller.isSelected())
				tree.select(item);
		} else if (data instanceof MountController) {
			MountController controller = (MountController) data;
			MountModel model = controller.getModel();

			StringBuilder buf = new StringBuilder();
			buf.append("Mount " + model.getId());

			String alias = controller.getAlias();
			if (alias != null)
				buf.append(" - \"" + alias + "\"");

			item.setText(buf.toString());

			if (controller.isSelected())
				tree.select(item);
		}
	}

	public static OverviewWindow getInstance() {
		return instance;
	}

	public void setEnabled(boolean b) {
		tree.setEnabled(b);
		if (highlightedController != null && highlightedController.getView().isHighlighted())
			highlightedController.getView().setHighlighted(false);
	}

	public boolean isEnabled() {
		return tree.isEnabled();
	}

	public boolean isVisible() {
		return shell.isVisible();
	}

	public boolean isDisposed() {
		return shell.isDisposed();
	}

	public boolean isFocusControl() {
		return tree.isFocusControl();
	}

	private TreeItem createItem(Controller controller) {
		TreeItem parent = controller instanceof RoomController ? trtmRooms :
				controller instanceof DoorController ? trtmDoors : trtmMounts;
		TreeItem item = new TreeItem(parent, SWT.NONE);
		item.setData(controller);

		update(item);

		carrierMap.put(controller, item);
		return item;
	}
}
