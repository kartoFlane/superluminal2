package com.kartoflane.superluminal2.ui;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.components.enums.OS;
import com.kartoflane.superluminal2.components.interfaces.Alias;
import com.kartoflane.superluminal2.components.interfaces.Indexable;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.events.SLDeleteEvent;
import com.kartoflane.superluminal2.events.SLDisposeEvent;
import com.kartoflane.superluminal2.events.SLEvent;
import com.kartoflane.superluminal2.events.SLListener;
import com.kartoflane.superluminal2.events.SLRestoreEvent;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.DoorController;
import com.kartoflane.superluminal2.mvc.controllers.GibController;
import com.kartoflane.superluminal2.mvc.controllers.MountController;
import com.kartoflane.superluminal2.mvc.controllers.ObjectController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.tools.Tool.Tools;
import com.kartoflane.superluminal2.utils.UIUtils;
import com.kartoflane.superluminal2.utils.Utils;

public class OverviewWindow implements SLListener {

	private static final OverviewWindow _instance = new OverviewWindow();

	private ShipContainer ship;
	private ObjectController highlightedController = null;
	private HashMap<AbstractController, TreeItem> controllerMap = new HashMap<AbstractController, TreeItem>();

	private Color disabledColor = null;

	private TreeItem dragItem = null;
	private ObjectController dragData = null;

	private Shell shell;
	private TreeItem trtmRooms;
	private TreeItem trtmDoors;
	private TreeItem trtmMounts;
	private TreeItem trtmGibs;
	private Tree tree;
	private Menu overviewMenu;
	private ToolBar toolBar;
	private ToolItem tltmAlias;
	private ToolItem tltmRemove;
	private ToolItem tltmToggleVis;
	private TreeColumn trclmnName;
	private TreeColumn trclmnAlias;
	private DragSource dragSource;
	private DropTarget dropTarget;

	private OverviewWindow() {
	}

	public void init(Shell parent) {
		Image helpImage = Cache.checkOutImage(this, "cpath:/assets/help.png");

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.MIN | SWT.RESIZE);
		shell.setText(String.format("%s - Ship Overview", Superluminal.APP_NAME));
		shell.setLayout(new GridLayout(2, false));

		toolBar = new ToolBar(shell, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		Label lblHelp = new Label(shell, SWT.NONE);
		lblHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblHelp.setImage(helpImage);
		String msg = "Alias is a short name to help you distinguish between objects.";
		UIUtils.addTooltip(lblHelp, Utils.wrapOSNot(msg, Superluminal.WRAP_WIDTH, Superluminal.WRAP_TOLERANCE, OS.MACOSX()));

		tltmAlias = new ToolItem(toolBar, SWT.NONE);
		tltmAlias.setToolTipText("Set Alias");
		tltmAlias.setImage(Cache.checkOutImage(this, "cpath:/assets/alias.png"));

		tltmRemove = new ToolItem(toolBar, SWT.NONE);
		tltmRemove.setToolTipText("Remove Alias");
		tltmRemove.setImage(Cache.checkOutImage(this, "cpath:/assets/noalias.png"));

		tltmToggleVis = new ToolItem(toolBar, SWT.NONE);
		tltmToggleVis.setToolTipText("Show/Hide");
		tltmToggleVis.setImage(Cache.checkOutImage(this, "cpath:/assets/cloak.png"));

		tree = new Tree(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		RGB rgb = tree.getBackground().getRGB();
		rgb.red = (int) (0.85 * rgb.red);
		rgb.green = (int) (0.85 * rgb.green);
		rgb.blue = (int) (0.85 * rgb.blue);
		disabledColor = Cache.checkOutColor(this, rgb);

		trclmnName = new TreeColumn(tree, SWT.NONE);
		trclmnName.setWidth(175);
		trclmnName.setText("Name");

		trclmnAlias = new TreeColumn(tree, SWT.RIGHT);
		trclmnAlias.setWidth(100);
		trclmnAlias.setText("Alias");

		trtmRooms = new TreeItem(tree, SWT.NONE);
		trtmRooms.setText("Rooms");

		trtmDoors = new TreeItem(tree, SWT.NONE);
		trtmDoors.setText("Doors");

		trtmMounts = new TreeItem(tree, SWT.NONE);
		trtmMounts.setText("Mounts");

		trtmGibs = new TreeItem(tree, SWT.NONE);
		trtmGibs.setText("Gibs");

		// Need to specify a transfer type, even if it's not used, because
		// otherwise it's not even possible to initiate drag and drop...
		Transfer[] sourceTypes = new Transfer[] { TextTransfer.getInstance() };
		dragSource = new DragSource(tree, DND.DROP_MOVE);
		dragSource.setTransfer(sourceTypes);

		Transfer[] dropTypes = new Transfer[] { TextTransfer.getInstance(), FileTransfer.getInstance() };
		dropTarget = new DropTarget(tree, DND.DROP_MOVE | DND.DROP_DEFAULT);
		dropTarget.setTransfer(dropTypes);

		// Overview popup menu
		overviewMenu = new Menu(tree);
		tree.setMenu(overviewMenu);

		final MenuItem mntmSetAlias = new MenuItem(overviewMenu, SWT.NONE);
		mntmSetAlias.setText("Set Alias");

		final MenuItem mntmRemoveAlias = new MenuItem(overviewMenu, SWT.NONE);
		mntmRemoveAlias.setText("Remove Alias");

		dragSource.addDragListener(new DragSourceListener() {
			@Override
			public void dragStart(DragSourceEvent e) {
				TreeItem[] selection = tree.getSelection();
				if (selection.length > 0 && selection[0].getData() instanceof Indexable) {
					e.doit = true;
					dragItem = selection[0];
					dragData = (ObjectController) dragItem.getData();
				} else {
					e.doit = false;
				}
			}

			@Override
			public void dragSetData(DragSourceEvent e) {
				e.data = "whatever"; // This needs not be an empty string, otherwise the drag mechanism freaks out...
			}

			@Override
			public void dragFinished(DragSourceEvent e) {
				dragItem = null;
				dragData = null;
			}
		});

		dropTarget.addDropListener(new DropTargetAdapter() {
			@Override
			public void dragOver(DropTargetEvent e) {
				e.detail = DND.DROP_MOVE;
				e.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;

				if (dragItem != null) {
					Point p = tree.toControl(e.x, e.y);
					TreeItem item = tree.getItem(p);

					if (item == null) {
						e.detail = DND.DROP_NONE;
						e.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
					} else {
						TreeItem parent = item.getParentItem();
						if (canDrop(parent, dragData)) {
							e.detail = DND.DROP_MOVE;
							Rectangle bounds = item.getBounds();
							if (p.y < bounds.y + bounds.height / 2) {
								e.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL | DND.FEEDBACK_INSERT_BEFORE;
							} else {
								e.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL | DND.FEEDBACK_INSERT_AFTER;
							}
						} else {
							e.detail = DND.DROP_NONE;
							e.feedback |= DND.FEEDBACK_NONE;
						}
					}
				}
			}

			@Override
			public void drop(DropTargetEvent e) {
				if (dragData != null) {
					if ((e.detail & DND.DROP_MOVE) == DND.DROP_MOVE) {
						Point p = tree.toControl(e.x, e.y);
						TreeItem item = tree.getItem(p);
						if (item != null) {
							Rectangle bounds = item.getBounds();
							TreeItem parent = item.getParentItem();
							if (parent != null) {
								ShipContainer container = Manager.getCurrentShip();
								TreeItem[] items = parent.getItems();
								int from = indexOf(items, dragItem);
								int to = indexOf(items, item);

								if (dragItem != item) {
									if (p.y > bounds.y + bounds.height / 2) {
										to += from > to ? 1 : 0;
									} else {
										to += from > to ? 0 : -1;
									}
								}

								Indexable[] array = new Indexable[items.length];
								for (int i = 0; i < items.length; i++) {
									array[i] = (Indexable) items[i].getData();
								}

								insert(array, from, to);
								container.sort();
								update();
							}
						}
					}
				}
			}
		});

		shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event) {
				dispose();
				event.doit = false;
			}
		});

		ControlAdapter resizer = new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				final int BORDER_OFFSET = tree.getBorderWidth();
				if (trclmnName.getWidth() > tree.getClientArea().width - BORDER_OFFSET)
					trclmnName.setWidth(tree.getClientArea().width - BORDER_OFFSET);
				trclmnAlias.setWidth(tree.getClientArea().width - trclmnName.getWidth() - BORDER_OFFSET);
			}
		};
		tree.addControlListener(resizer);
		trclmnName.addControlListener(resizer);

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

		tree.addListener(SWT.MouseExit, new Listener() {
			@Override
			public void handleEvent(Event e) {
				if (Manager.getSelectedToolId() == Tools.POINTER) {
					if (highlightedController != null)
						highlightedController.setHighlighted(false);
					highlightedController = null;
				}
			}
		});

		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ObjectController controller = null;

				if (tree.getSelectionCount() != 0) {
					TreeItem item = tree.getSelection()[0];
					controller = (ObjectController) item.getData();
				}

				if (Manager.getSelectedToolId() == Tools.POINTER && (controller == null || controller.isVisible()))
					Manager.setSelected(controller);

				tltmAlias.setEnabled(controller != null);
				tltmRemove.setEnabled(controller != null && controller.getAlias() != null && !controller.getAlias().equals(""));
				tltmToggleVis.setEnabled(controller != null);
			}
		});

		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (e.button == 1 && tree.getSelectionCount() != 0) {
					TreeItem selectedItem = tree.getSelection()[0];
					if (selectedItem.getItemCount() != 0 && selectedItem.getBounds().contains(e.x, e.y))
						selectedItem.setExpanded(!selectedItem.getExpanded());
				}
			}
		});

		tree.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_RETURN && tree.getSelectionCount() != 0) {
					TreeItem sel = tree.getSelection()[0];
					if (sel.getItemCount() != 0)
						sel.setExpanded(!sel.getExpanded());
				}
			}
		});

		overviewMenu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(MenuEvent e) {
				if (tree.getSelectionCount() == 0 || tree.getSelection()[0].getData() == null) {
					overviewMenu.setVisible(false);
				}
			}
		});

		SelectionAdapter setAliasListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Alias alias = (Alias) tree.getSelection()[0].getData();
				AliasDialog dialog = new AliasDialog(shell);
				dialog.setAlias(alias);
				dialog.open();
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

		tltmToggleVis.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (tree.getSelectionCount() != 0) {
					AbstractController ac = (AbstractController) tree.getSelection()[0].getData();
					if (ac != null) {
						ac.setVisible(!ac.isVisible());
						tree.getSelection()[0].setBackground(ac.isVisible() ? null : disabledColor);
					}
				}
			}
		});

		shell.setSize(300, 600);
		Point size = shell.getSize();
		Point pSize = parent.getSize();
		Point pLoc = parent.getLocation();
		shell.setLocation(pLoc.x + pSize.x - size.x, pLoc.y + 50);
	}

	public void open() {
		shell.open();
		update();

		shell.setMinimized(false);
	}

	public void update() {
		ship = Manager.getCurrentShip();

		AbstractController prevSelection = Manager.getSelected();
		if (Manager.getSelectedToolId() != Tools.POINTER && tree.getSelectionCount() == 1 && tree.getSelection()[0] != null)
			prevSelection = (AbstractController) tree.getSelection()[0].getData();

		for (TreeItem it : trtmRooms.getItems())
			it.dispose();
		for (TreeItem it : trtmDoors.getItems())
			it.dispose();
		for (TreeItem it : trtmMounts.getItems())
			it.dispose();
		for (TreeItem it : trtmGibs.getItems())
			it.dispose();
		controllerMap.clear();

		if (ship != null) {
			for (RoomController r : ship.getRoomControllers())
				createItem(r);
			trtmRooms.setText(String.format("Rooms (%s)", trtmRooms.getItemCount()));
			for (DoorController d : ship.getDoorControllers())
				createItem(d);
			trtmDoors.setText(String.format("Doors (%s)", trtmDoors.getItemCount()));
			for (MountController m : ship.getMountControllers())
				createItem(m);
			trtmMounts.setText(String.format("Mounts (%s)", trtmMounts.getItemCount()));
			for (int i = 1; i <= ship.getGibControllers().length; i++) {
				GibController g = ship.getGibControllerById(i);
				if (g != null)
					createItem(g);
			}
			trtmGibs.setText(String.format("Gibs (%s)", trtmGibs.getItemCount()));
		}

		TreeItem item = controllerMap.get(prevSelection);
		if (item == null) {
			tree.select(trtmRooms);
		} else {
			tree.select(item);
			Rectangle b = item.getBounds();
			if (!tree.getClientArea().contains(b.x, b.y))
				tree.setTopItem(item);
		}

		String alias = null;
		if (prevSelection != null && !prevSelection.isDisposed() && prevSelection instanceof Alias)
			alias = ((Alias) prevSelection).getAlias();
		tltmAlias.setEnabled(prevSelection != null);
		tltmRemove.setEnabled(prevSelection != null && alias != null && !alias.equals(""));
		tltmToggleVis.setEnabled(prevSelection != null);
	}

	public void update(ObjectController controller) {
		if (controller == null)
			throw new IllegalArgumentException("Argument must not be null.");

		TreeItem item = controllerMap.get(controller);

		if (item == null && !controller.isDeleted()) {
			item = createItem(controller);
			return; // No need to update, since we've already created a new item
		}
		if (item != null)
			update(item);
	}

	private void update(TreeItem item) {
		if (item.getData() instanceof ObjectController == false)
			return;

		ObjectController oc = (ObjectController) item.getData();

		if (oc.isDeleted()) {
			item.dispose();
			controllerMap.remove(oc);
		} else {
			if (oc instanceof RoomController) {
				RoomController controller = (RoomController) oc;
				item.setText(0, controller.getGameObject().toStringNoAlias());
			} else if (oc instanceof DoorController) {
				DoorController controller = (DoorController) oc;
				item.setText(0, "Door " + (controller.isHorizontal() ? "H" : "V"));
			} else if (oc instanceof MountController) {
				MountController controller = (MountController) oc;
				item.setText(0, "Mount " + controller.getId());
			} else if (oc instanceof GibController) {
				GibController controller = (GibController) oc;
				item.setText(0, "Gib " + controller.getId());
			}

			String alias = oc.getAlias();
			item.setText(1, alias == null ? "" : alias);

			item.setBackground(oc.isVisible() ? null : disabledColor);

			if (oc.isSelected()) {
				tree.select(item);
				Rectangle b = item.getBounds();
				if (!tree.getClientArea().contains(b.x, b.y))
					tree.setTopItem(item);
			}
		}

		tltmAlias.setEnabled(oc != null);
		tltmRemove.setEnabled(oc != null && oc.getAlias() != null && !oc.getAlias().equals(""));
		tltmToggleVis.setEnabled(oc != null);
	}

	/**
	 * Updates the overview window if it exists, or does nothing if it does not.
	 */
	public static void staticUpdate() {
		if (_instance != null && !_instance.isDisposed()) {
			_instance.update();
		}
	}

	/**
	 * Updates the overview window if it exists, or does nothing if it does not.
	 */
	public static void staticUpdate(ObjectController controller) {
		if (_instance != null && !_instance.isDisposed()) {
			_instance.update(controller);
		}
	}

	public static OverviewWindow getInstance() {
		return _instance;
	}

	public void setEnabled(boolean b) {
		tree.setEnabled(b);
		if (highlightedController != null && highlightedController.isHighlighted())
			highlightedController.setHighlighted(false);
	}

	public boolean isEnabled() {
		return tree.isEnabled();
	}

	public boolean isActive() {
		return !shell.isDisposed() && tree.isFocusControl();
	}

	public boolean isDisposed() {
		return shell == null || shell.isDisposed();
	}

	private boolean canDrop(TreeItem newParent, ObjectController data) {
		return (newParent == trtmRooms && data instanceof RoomController) ||
				(newParent == trtmMounts && data instanceof MountController) ||
				(newParent == trtmGibs && data instanceof GibController);
	}

	private int indexOf(TreeItem[] items, TreeItem item) {
		if (items == null)
			throw new IllegalArgumentException("Array must not be null.");
		if (item == null)
			throw new IllegalArgumentException("Item must not be null.");
		int result = -1;
		for (int i = 0; i < items.length && result == -1; i++) {
			if (items[i] == item)
				result = i;
		}
		return result;
	}

	private void insert(Indexable[] array, int from, int to) {
		if (from < 0 || from >= array.length)
			throw new IndexOutOfBoundsException("" + from);
		if (to < 0 || to >= array.length)
			throw new IndexOutOfBoundsException("" + to);
		if (to == from)
			return;

		int dir = from > to ? 1 : -1;
		int oldId = array[to].getId();
		/*
		 * The loop needs to go on as long as
		 * (from > to && i < from) || (from < to && i > from) is true
		 * Let's say A = (from > to); B = (i < from), then the above becomes:
		 * (A && B) || (!A && !B)
		 * Hence the simplified condition is A == B
		 */
		for (int i = to; from > to == i < from; i += dir)
			array[i].setId(array[i + dir < 0 ? 0 : i + dir].getId());
		array[from].setId(oldId);
	}

	private TreeItem createItem(AbstractController controller) {
		TreeItem parent = null;
		if (controller instanceof RoomController)
			parent = trtmRooms;
		else if (controller instanceof DoorController)
			parent = trtmDoors;
		else if (controller instanceof MountController)
			parent = trtmMounts;
		else if (controller instanceof GibController)
			parent = trtmGibs;
		else
			return null;

		TreeItem item = new TreeItem(parent, SWT.NONE);
		item.setData(controller);

		update(item);

		controllerMap.put(controller, item);
		return item;
	}

	public void dispose() {
		Cache.checkInColor(this, disabledColor.getRGB());
		disabledColor = null;
		Cache.checkInImage(this, "cpath:/assets/help.png");
		Cache.checkInImage(this, "cpath:/assets/alias.png");
		Cache.checkInImage(this, "cpath:/assets/noalias.png");
		Cache.checkInImage(this, "cpath:/assets/cloak.png");
		shell.dispose();

		controllerMap.clear();
	}

	@Override
	public void handleEvent(SLEvent e) {
		if (e.data instanceof ObjectController) {
			ObjectController data = (ObjectController) e.data;
			if (e instanceof SLDeleteEvent || e instanceof SLRestoreEvent) {
				if (data instanceof RoomController || data instanceof DoorController ||
						data instanceof MountController || data instanceof GibController) {
					update(data);
				}
			} else if (e instanceof SLDisposeEvent) {
				data.removeListener(this);
			}
		}
	}
}
