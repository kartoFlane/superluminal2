package com.kartoflane.superluminal2.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.components.Systems;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.ftl.GlowSet;
import com.kartoflane.superluminal2.ftl.GlowSet.Glows;
import com.kartoflane.superluminal2.ftl.SystemObject;
import com.kartoflane.superluminal2.mvc.views.Preview;

public class GlowSelectionDialog {
	private static GlowSelectionDialog instance = null;

	private int response = SWT.NO;
	private Preview preview = null;
	private SystemObject selectedSystem = null;

	private Shell shell;
	private Tree tree;
	private Button btnCancel;
	private Button btnConfirm;
	private Button btnNew;
	private Canvas canvas;

	public GlowSelectionDialog(Shell parent) {
		instance = this;

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText(Superluminal.APP_NAME + " - Glow Set Selection");
		shell.setLayout(new GridLayout(3, false));

		tree = new Tree(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2));

		Label lblPreview = new Label(shell, SWT.NONE);
		lblPreview.setText("Preview:");

		canvas = new Canvas(shell, SWT.NONE);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		preview = new Preview();
		RGB rgb = canvas.getBackground().getRGB();
		preview.setBackgroundColor((int) (0.9 * rgb.red), (int) (0.9 * rgb.green), (int) (0.9 * rgb.blue));
		preview.setDrawBackground(true);
		canvas.addPaintListener(preview);

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
					GlowSet glowSet = (GlowSet) selectedItem.getData();
					btnConfirm.setEnabled(glowSet != null);
					updateData(glowSet);
				} else {
					btnConfirm.setEnabled(false);
					updateData(null);
				}
			}
		});

		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				response = SWT.NO;
				shell.setVisible(false);
			}
		});

		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				response = SWT.YES;
				shell.setVisible(false);
			}
		});

		btnNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				GlowSet newGlow = GlowSetDialog.getInstance().open();
				if (newGlow != null) {
					Database.getInstance().storeGlowSet(newGlow);
					TreeItem currentItem = updateTree(selectedSystem);
					updateData(selectedSystem.getGlowSet());
					if (currentItem != null)
						tree.setSelection(currentItem);
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

		shell.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_RETURN && tree.getSelectionCount() != 0) {
					TreeItem selectedItem = tree.getSelection()[0];
					if (selectedItem.getItemCount() == 0 && btnConfirm.isEnabled())
						btnConfirm.notifyListeners(SWT.Selection, null);
					else
						selectedItem.setExpanded(!selectedItem.getExpanded());
				}
			}
		});

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		shell.setSize(400, 300);
	}

	public static GlowSelectionDialog getInstance() {
		return instance;
	}

	public GlowSet open(SystemObject system) {
		selectedSystem = system;
		response = SWT.NO;
		TreeItem currentItem = updateTree(system);

		shell.open();

		updateData(system.getGlowSet());
		if (currentItem != null)
			tree.setSelection(currentItem);

		Display display = Display.getCurrent();
		while (shell.isVisible()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		if (response == SWT.YES) {
			TreeItem selectedItem = tree.getSelection()[0];
			if (selectedItem == null) {
				throw new IllegalStateException("No TreeItem was selected.");
			} else {
				return (GlowSet) selectedItem.getData();
			}
		} else {
			return null;
		}
	}

	public Shell getShell() {
		return shell;
	}

	public boolean isVisible() {
		return shell.isVisible();
	}

	private TreeItem updateTree(SystemObject system) {
		TreeItem currentItem = null;
		for (TreeItem trtm : tree.getItems())
			trtm.dispose();

		for (GlowSet glowSet : Database.getInstance().getGlowSets()) {
			// Only create widgets for cloaking glows if Cloaking system is selected
			boolean cloak = glowSet.getImage(Glows.CLOAK) != null;
			if ((system.getSystemId() == Systems.CLOAKING) != cloak)
				continue;

			TreeItem trtm = new TreeItem(tree, SWT.NONE);
			trtm.setText(glowSet.getIdentifier());
			trtm.setData(glowSet);
			if (glowSet.compareTo(system.getGlowSet()) == 0)
				currentItem = trtm;
		}
		return currentItem;
	}

	private void updateData(GlowSet glow) {
		String path = null;

		if (glow != null) {
			path = glow.getImage(Glows.CLOAK);
			if (path == null)
				path = glow.getImage(Glows.BLUE);
		}

		preview.setImage(path);
		preview.setLocation(canvas.getSize().x / 2, canvas.getSize().y / 2);
		canvas.redraw();
	}
}