package com.kartoflane.superluminal2.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.core.DatabaseEntry;
import com.kartoflane.superluminal2.utils.UIUtils;
import com.kartoflane.superluminal2.utils.UIUtils.LoadTask;

public class ModManagementDialog {
	private static final Logger log = LogManager.getLogger(ModManagementDialog.class);

	private TreeItem dragItem = null;
	private DatabaseEntry dragData = null;

	private static ModManagementDialog instance = null;
	private Shell shell;
	private Tree tree;
	private Button btnRemove;
	private Button btnLoad;
	private TreeItem trtmCore;
	private Color disabledColor = null;
	private Button btnConfirm;
	private Button btnCancel;
	private DragSource dragSource;
	private DropTarget dropTarget;

	public ModManagementDialog(Shell parent) {
		instance = this;

		final ArrayList<DatabaseEntry> entries = new ArrayList<DatabaseEntry>();
		final Database db = Database.getInstance();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText(Superluminal.APP_NAME + " - Mod Management");
		shell.setLayout(new GridLayout(4, false));

		tree = new Tree(shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));

		trtmCore = new TreeItem(tree, SWT.NONE);
		trtmCore.setText("DatabaseCore");
		RGB rgb = trtmCore.getBackground().getRGB();
		rgb.red = (int) (0.85 * rgb.red);
		rgb.green = (int) (0.85 * rgb.green);
		rgb.blue = (int) (0.85 * rgb.blue);
		disabledColor = Cache.checkOutColor(this, rgb);
		trtmCore.setBackground(disabledColor);
		trtmCore.setData(db.getCore());
		entries.add(db.getCore());

		// Need to specify a transfer type, even if it's not used, because
		// otherwise it's not even possible to initiate drag and drop...
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
		dragSource = new DragSource(tree, DND.DROP_MOVE);
		dragSource.setTransfer(types);

		dropTarget = new DropTarget(tree, DND.DROP_MOVE);
		dropTarget.setTransfer(types);

		btnLoad = new Button(shell, SWT.NONE);
		GridData gd_btnLoad = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnLoad.widthHint = 80;
		btnLoad.setLayoutData(gd_btnLoad);
		btnLoad.setText("Load Mod");

		btnRemove = new Button(shell, SWT.NONE);
		GridData gd_btnRemove = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnRemove.widthHint = 80;
		btnRemove.setLayoutData(gd_btnRemove);
		btnRemove.setText("Remove");
		btnRemove.setEnabled(false);

		btnConfirm = new Button(shell, SWT.NONE);
		GridData gd_btnConfirm = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnConfirm.widthHint = 80;
		btnConfirm.setLayoutData(gd_btnConfirm);
		btnConfirm.setText("Confirm");

		btnCancel = new Button(shell, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setText("Cancel");

		for (DatabaseEntry de : db.getDatabaseEntries()) {
			if (de == db.getCore())
				continue;
			createTreeItem(de);
			entries.add(de);
		}

		dragSource.addDragListener(new DragSourceListener() {
			@Override
			public void dragStart(DragSourceEvent e) {
				TreeItem[] selection = tree.getSelection();
				if (selection.length > 0 && selection[0].getItemCount() == 0) {
					e.doit = true;
					dragItem = selection[0];
				} else {
					e.doit = false;
				}
			}

			@Override
			public void dragSetData(DragSourceEvent e) {
				e.data = "whatever"; // This need not be an empty string, otherwise the drag mechanism freaks out...
				dragData = (DatabaseEntry) dragItem.getData();
			}

			@Override
			public void dragFinished(DragSourceEvent e) {
				if (dragItem != null && dragItem.getData() == null)
					dragItem.dispose();
				dragItem = null;
			}
		});

		dropTarget.addDropListener(new DropTargetAdapter() {
			@Override
			public void dragOver(DropTargetEvent e) {
				e.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
				if (dragItem != null) {
					Point p = tree.toControl(e.x, e.y);
					TreeItem item = tree.getItem(p);
					if (item == null) {
						e.feedback |= DND.FEEDBACK_NONE;
					} else {
						Rectangle bounds = item.getBounds();
						if (p.y < bounds.y + bounds.height / 2) {
							if (item == trtmCore)
								e.feedback |= DND.FEEDBACK_NONE;
							else
								e.feedback |= DND.FEEDBACK_INSERT_BEFORE;
						} else {
							e.feedback |= DND.FEEDBACK_INSERT_AFTER;
						}
					}
				}
			}

			@Override
			public void drop(DropTargetEvent e) {
				if (dragData == null) {
					e.detail = DND.DROP_NONE;
					return;
				}
				if (dragItem == null) {
					createTreeItem(dragData);
				} else {
					Point p = tree.toControl(e.x, e.y);
					TreeItem item = tree.getItem(p);
					if (item != null) {
						Rectangle bounds = item.getBounds();
						TreeItem[] items = tree.getItems();
						int index = indexOf(items, item);

						if (p.y < bounds.y + bounds.height / 2) {
							if (item != trtmCore) {
								dragItem.setData(null);
								createTreeItem(dragData, index);
								entries.remove(dragData);
								entries.add(index - 1, dragData);
							}
						} else {
							dragItem.setData(null);
							createTreeItem(dragData, index + 1);
							entries.remove(dragData);
							entries.add(index, dragData);
						}
					}
				}
			}
		});

		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (contains(tree.getSelection(), trtmCore))
					tree.deselect(trtmCore);
				btnRemove.setEnabled(tree.getSelectionCount() > 0);
			}
		});

		btnLoad.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				File temp = UIUtils.promptForLoadFile(shell, "Load Mod", new String[] { "*.zip;*.ftl" });
				if (temp != null) {
					try {
						DatabaseEntry de = new DatabaseEntry(temp);
						if (!entries.contains(de)) {
							createTreeItem(de);
							entries.add(de);
						}
					} catch (IOException ex) {
						log.warn(String.format("An error has occured while loading mod file '%s': ", temp.getName(), ex));
					}
				}
			}
		});

		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (TreeItem trtm : tree.getSelection()) {
					DatabaseEntry de = (DatabaseEntry) trtm.getData();
					if (de == db.getCore()) // Redundant check to make sure that the core database is never unloaded
						continue;
					trtm.dispose();
					entries.remove(de);
					de = null;
				}
				btnRemove.setEnabled(tree.getSelectionCount() > 0);
			}
		});

		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				UIUtils.showLoadDialog(shell, null, "Loading mods, please wait...", new LoadTask() {
					public void execute() {
						// Load added entries
						DatabaseEntry[] dbEntries = db.getDatabaseEntries();
						for (DatabaseEntry de : entries) {
							if (!contains(dbEntries, de))
								db.addEntry(de);
						}
						// Unload deleted entries
						for (DatabaseEntry de : db.getDatabaseEntries()) {
							if (!entries.contains(de))
								db.removeEntry(de);
						}
					}
				});
				db.cacheAnimations();
				dispose();
			}
		});

		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dispose();
			}
		});

		shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event e) {
				btnCancel.notifyListeners(SWT.Selection, null);
				e.doit = false;
			}
		});

		shell.setSize(450, 220);
		Point size = shell.getSize();
		Point parSize = parent.getSize();
		Point parLoc = parent.getLocation();
		shell.setLocation(parLoc.x + parSize.x / 3 - size.x / 2, parLoc.y + parSize.y / 3 - size.y / 2);
	}

	public static ModManagementDialog getInstance() {
		return instance;
	}

	public void open() {
		shell.open();
	}

	public void dispose() {
		if (disabledColor != null)
			disabledColor.dispose();
		shell.dispose();
	}

	public boolean isActive() {
		return !shell.isDisposed() && shell.isVisible();
	}

	private TreeItem createTreeItem(DatabaseEntry de) {
		return createTreeItem(de, tree.getItemCount());
	}

	private TreeItem createTreeItem(DatabaseEntry de, int index) {
		TreeItem trtm = new TreeItem(tree, SWT.NONE, index);
		trtm.setText(de.getName());
		trtm.setData(de);
		return trtm;
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

	private boolean contains(Object[] array, Object object) {
		for (Object o : array)
			if (o == object)
				return true;
		return false;
	}
}
