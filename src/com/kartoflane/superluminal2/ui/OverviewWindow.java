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
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.components.interfaces.Alias;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.controllers.DoorController;
import com.kartoflane.superluminal2.mvc.controllers.MountController;
import com.kartoflane.superluminal2.mvc.controllers.ObjectController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.tools.Tool.Tools;

public class OverviewWindow {

	private static OverviewWindow instance = null;

	private ShipContainer ship;
	private ObjectController highlightedController = null;
	private HashMap<Controller, TreeItem> controllerMap = new HashMap<Controller, TreeItem>();

	private Shell shell;
	private TreeItem trtmRooms;
	private TreeItem trtmDoors;
	private TreeItem trtmMounts;
	private Tree tree;
	private Menu overviewMenu;
	private ToolBar toolBar;
	private ToolItem tltmAlias;
	private ToolItem tltmRemove;

	public OverviewWindow(Shell parent) {
		instance = this;

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.MIN | SWT.RESIZE);
		shell.setSize(250, 350);
		Point pLoc = parent.getLocation();
		shell.setLocation(pLoc.x + parent.getSize().x - shell.getSize().x, pLoc.y + 50);
		shell.setText(String.format("%s - Ship Overview", Superluminal.APP_NAME));
		shell.setLayout(new GridLayout(1, false));

		toolBar = new ToolBar(shell, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		tltmAlias = new ToolItem(toolBar, SWT.NONE);
		tltmAlias.setToolTipText("Set Alias\n\nAlias is a short name to help you distinguish between objects.");
		tltmAlias.setImage(SWTResourceManager.getImage(OverviewWindow.class, "/assets/alias.png"));

		tltmRemove = new ToolItem(toolBar, SWT.NONE);
		tltmRemove.setToolTipText("Remove Alias");
		tltmRemove.setImage(SWTResourceManager.getImage(OverviewWindow.class, "/assets/noalias.png"));

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

		new AliasDialog(shell);

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
				if (Manager.getSelectedToolId() == Tools.POINTER) {
					TreeItem item = tree.getItem(new Point(e.x, e.y));
					ObjectController controller = null;
					if (item != null && item.getData() != null)
						controller = (ObjectController) item.getData();

					if (highlightedController != null && highlightedController != controller && highlightedController.isHighlighted())
						highlightedController.setHighlighted(false);
					if (controller != null && !controller.isHighlighted())
						controller.setHighlighted(true);
					highlightedController = controller;
				}
			}
		});

		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (tree.getSelectionCount() != 0 && Manager.getSelectedToolId() == Tools.POINTER) {
					TreeItem item = tree.getSelection()[0];
					ObjectController controller = (ObjectController) item.getData();
					Manager.setSelected(controller);
				}

				boolean enable = tree.getSelectionCount() != 0 && tree.getSelection()[0].getData() != null;
				tltmAlias.setEnabled(enable);
				tltmRemove.setEnabled(enable);
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
			}
		});

		SelectionAdapter setAliasListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Alias alias = (Alias) tree.getSelection()[0].getData();
				AliasDialog dialog = AliasDialog.getInstance();
				dialog.setAlias(alias);
				dialog.open();
				update(tree.getSelection()[0]);
			}
		};
		mntmSetAlias.addSelectionListener(setAliasListener);
		tltmAlias.addSelectionListener(setAliasListener);

		SelectionAdapter removeAliasListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Alias alias = (Alias) tree.getSelection()[0].getData();
				alias.setAlias(null);
				update(tree.getSelection()[0]);
			}
		};
		mntmRemoveAlias.addSelectionListener(removeAliasListener);
		tltmRemove.addSelectionListener(removeAliasListener);
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
		controllerMap.clear();

		for (RoomController r : ship.getRoomControllers())
			createItem(r);
		for (DoorController d : ship.getDoorControllers())
			createItem(d);
		for (MountController m : ship.getMountControllers())
			createItem(m);
	}

	public void update(Controller controller) {
		TreeItem item = controllerMap.get(controller);
		if (item == null) {
			item = createItem(controller);
			controllerMap.put(controller, item);
			return; // no need to update, since we've already created a new item
		}
		// throw new IllegalArgumentException("DataCarrier " + data + " has not yet been added to the manager.");

		update(item);
	}

	private void update(TreeItem item) {
		Object data = item.getData();

		if (data instanceof RoomController) {
			RoomController controller = (RoomController) data;
			item.setText(controller.toString());

			if (controller.isSelected())
				tree.select(item);
		} else if (data instanceof DoorController) {
			DoorController controller = (DoorController) data;
			item.setText(controller.toString());

			if (controller.isSelected())
				tree.select(item);
		} else if (data instanceof MountController) {
			MountController controller = (MountController) data;

			StringBuilder buf = new StringBuilder();
			buf.append("Mount " + controller.getId());

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
		if (highlightedController != null && highlightedController.isHighlighted())
			highlightedController.setHighlighted(false);
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

		controllerMap.put(controller, item);
		return item;
	}
}
