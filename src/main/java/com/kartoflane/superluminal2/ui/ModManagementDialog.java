package com.kartoflane.superluminal2.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
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
import com.kartoflane.superluminal2.core.UIUtils;
import com.kartoflane.superluminal2.core.UIUtils.LoadTask;

public class ModManagementDialog {
	private static final Logger log = LogManager.getLogger(ModManagementDialog.class);

	private static ModManagementDialog instance = null;
	private Shell shell;
	private Tree tree;
	private Button btnRemove;
	private Button btnLoad;
	private TreeItem trtmCore;
	private Color disabledColor = null;
	private Button btnConfirm;
	private Button btnCancel;

	public ModManagementDialog(Shell parent) {
		instance = this;

		final HashSet<DatabaseEntry> entries = new HashSet<DatabaseEntry>();
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
				File temp = UIUtils.promptForLoadFile(shell);
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
		TreeItem trtm = new TreeItem(tree, SWT.NONE);
		trtm.setText(de.getName());
		trtm.setData(de);
		return trtm;
	}

	private boolean contains(Object[] array, Object object) {
		for (Object o : array)
			if (o == object)
				return true;
		return false;
	}
}
