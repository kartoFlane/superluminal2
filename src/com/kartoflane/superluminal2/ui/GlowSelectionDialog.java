package com.kartoflane.superluminal2.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.ftl.GlowObject;

public class GlowSelectionDialog {
	private static GlowSelectionDialog instance = null;

	private boolean confirmed = false;

	private Shell shell;
	private Tree tree;
	private TreeItem trtmNone;
	private TreeItem trtmDefault;
	private TreeItem trtmLoaded;
	private Button btnCancel;
	private Button btnConfirm;
	private Button btnNew;
	private Text txtNamespace;
	private Text txtGlowImage;
	private Label lblTileLoc;
	private Label lblPrecLoc;
	private Label lblDirection;
	private DirectionCombo cmbDir;

	public GlowSelectionDialog(Shell parent) {
		instance = this;

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText(Superluminal.APP_NAME + " - Glow Set Selection");
		shell.setLayout(new GridLayout(3, false));

		tree = new Tree(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		trtmNone = new TreeItem(tree, SWT.NONE);
		trtmNone.setText("No Glow");

		trtmDefault = new TreeItem(tree, SWT.NONE);
		trtmDefault.setText("Default Glow");
		trtmDefault.setData(Database.DEFAULT_GLOW_OBJ);

		trtmLoaded = new TreeItem(tree, SWT.NONE);
		trtmLoaded.setText("Loaded Glows");

		Composite compData = new Composite(shell, SWT.NONE);
		compData.setLayout(new GridLayout(2, false));
		compData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblNamespace = new Label(compData, SWT.NONE);
		lblNamespace.setText("Namespace:");

		txtNamespace = new Text(compData, SWT.BORDER | SWT.READ_ONLY);
		txtNamespace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblGlowImage = new Label(compData, SWT.NONE);
		lblGlowImage.setText("Glow Image:");

		txtGlowImage = new Text(compData, SWT.BORDER | SWT.READ_ONLY);
		txtGlowImage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Group grpLocation = new Group(compData, SWT.NONE);
		grpLocation.setLayout(new GridLayout(2, false));
		grpLocation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		grpLocation.setText("Location");

		Label lblTiles = new Label(grpLocation, SWT.NONE);
		lblTiles.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblTiles.setText("Tiles:");

		lblTileLoc = new Label(grpLocation, SWT.NONE);
		lblTileLoc.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblTileLoc.setText("0, 0");

		Label lblPrecise = new Label(grpLocation, SWT.NONE);
		lblPrecise.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblPrecise.setText("Precise:");

		lblPrecLoc = new Label(grpLocation, SWT.NONE);
		lblPrecLoc.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblPrecLoc.setText("0, 0");

		lblDirection = new Label(compData, SWT.NONE);
		lblDirection.setText("Direction:");

		cmbDir = new DirectionCombo(compData, SWT.READ_ONLY, false);
		cmbDir.setEnabled(false);
		cmbDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite compButtons = new Composite(shell, SWT.NONE);
		GridLayout gl_compButtons = new GridLayout(3, false);
		gl_compButtons.marginHeight = 0;
		gl_compButtons.marginWidth = 0;
		compButtons.setLayout(gl_compButtons);
		compButtons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));

		btnNew = new Button(compButtons, SWT.NONE);
		btnNew.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnNew.setText("Add New Set");

		btnConfirm = new Button(compButtons, SWT.NONE);
		GridData gd_btnConfirm = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnConfirm.widthHint = 80;
		btnConfirm.setLayoutData(gd_btnConfirm);
		btnConfirm.setText("Confirm");

		btnCancel = new Button(compButtons, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setText("Cancel");

		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				confirmed = false;
				shell.setVisible(false);
			}
		});

		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				confirmed = true;
				shell.setVisible(false);
			}
		});

		tree.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (e.button == 1 && tree.getSelectionCount() != 0) {
					TreeItem selectedItem = tree.getSelection()[0];
					if (selectedItem.getItemCount() == 0 && btnConfirm.isEnabled())
						btnConfirm.notifyListeners(SWT.Selection, null);
					else if (selectedItem.getBounds().contains(e.x, e.y))
						selectedItem.setExpanded(!selectedItem.getExpanded());
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {
			}

			@Override
			public void mouseUp(MouseEvent e) {
			}
		});

		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (tree.getSelectionCount() != 0) {
					TreeItem selectedItem = tree.getSelection()[0];
					GlowObject glowObject = (GlowObject) selectedItem.getData();

					btnConfirm.setEnabled(glowObject != null || selectedItem == trtmNone);
				} else {
					btnConfirm.setEnabled(false);
				}
			}
		});

		shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event e) {
				btnCancel.notifyListeners(SWT.Selection, null);
				e.doit = false;
			}
		});

		shell.pack();
		shell.setMinimumSize(shell.getSize());

		shell.setSize(400, 250);
	}

	public static GlowSelectionDialog getInstance() {
		return instance;
	}

	public GlowObject open(GlowObject currentGlow) {
		confirmed = false;
		TreeItem currentItem = trtmDefault;

		for (TreeItem trtm : trtmLoaded.getItems())
			trtm.dispose();

		for (GlowObject glowObject : Database.getInstance().getGlows()) {
			TreeItem trtm = new TreeItem(trtmLoaded, SWT.NONE);
			trtm.setText(glowObject.getIdentifier());
			trtm.setData(glowObject);
			if (glowObject == currentGlow)
				currentItem = trtm;
		}

		tree.setSelection(currentItem);

		shell.open();

		Display display = Display.getCurrent();
		while (shell.isVisible()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		if (confirmed) {
			TreeItem selectedItem = tree.getSelection()[0];
			if (selectedItem == null) {
				throw new IllegalStateException("No TreeItem was selected.");
			} else {
				return (GlowObject) selectedItem.getData();
			}
		} else {
			return null;
		}
	}

	private void updateData(GlowObject glow) {
		if (glow == null) {
			txtNamespace.setText("No Glow");
			txtGlowImage.setText("");
			lblTileLoc.setText("N/A");
			lblPrecLoc.setText("N/A");
		} else {

		}
	}
}
