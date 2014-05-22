package com.kartoflane.superluminal2.ui;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.utils.UIUtils;

public class SaveOptionsDialog {
	private static SaveOptionsDialog instance = null;

	private File result = null;

	private Shell shell = null;
	private Button btnCancel;
	private Button btnConfirm;
	private Label lblSaveLocation;
	private Button btnBrowse;
	private Text txtDestination;
	private Button btnDirectory;
	private Group grpSaveAs;
	private Button btnFTL;
	private Label lblDirectoryHelp;
	private Label lblArchiveHelp;

	public SaveOptionsDialog(Shell parent) {
		if (instance != null)
			throw new IllegalStateException("Previous instance has not been disposed!");
		instance = this;
		Image helpImage = Cache.checkOutImage(this, "cpath:/assets/help.png");

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText(Superluminal.APP_NAME + " - Save Options");
		shell.setLayout(new GridLayout(2, false));

		grpSaveAs = new Group(shell, SWT.NONE);
		grpSaveAs.setText("Save as...");
		grpSaveAs.setLayout(new GridLayout(2, false));
		grpSaveAs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		btnDirectory = new Button(grpSaveAs, SWT.RADIO);
		btnDirectory.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnDirectory.setText("Resource folder");

		lblDirectoryHelp = new Label(grpSaveAs, SWT.NONE);
		lblDirectoryHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDirectoryHelp.setImage(helpImage);
		String msg = "Saves the ship as a series of folders mirroring the internal\n" +
				"structure of the game's files -- source code for your mod, so to say.";
		UIUtils.addTooltip(lblDirectoryHelp, "", msg);

		btnFTL = new Button(grpSaveAs, SWT.RADIO);
		btnFTL.setText("FTL file");

		lblArchiveHelp = new Label(grpSaveAs, SWT.NONE);
		lblArchiveHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblArchiveHelp.setImage(helpImage);
		msg = "Saves the ship as a ready-to-install .ftl archive.";
		UIUtils.addTooltip(lblArchiveHelp, "", msg);

		lblSaveLocation = new Label(shell, SWT.NONE);
		lblSaveLocation.setText("Save location:");

		btnBrowse = new Button(shell, SWT.NONE);
		btnBrowse.setEnabled(false);
		GridData gd_btnBrowse = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnBrowse.widthHint = 80;
		btnBrowse.setLayoutData(gd_btnBrowse);
		btnBrowse.setText("Browse");

		txtDestination = new Text(shell, SWT.BORDER | SWT.READ_ONLY);
		txtDestination.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		btnConfirm = new Button(shell, SWT.NONE);
		btnConfirm.setEnabled(false);
		GridData gd_btnConfirm = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnConfirm.widthHint = 80;
		btnConfirm.setLayoutData(gd_btnConfirm);
		btnConfirm.setText("Confirm");

		btnCancel = new Button(shell, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setText("Cancel");

		btnDirectory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btnBrowse.setEnabled(true);
				result = null;
				txtDestination.setText("");
				btnConfirm.setEnabled(false);
			}
		});

		btnFTL.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btnBrowse.setEnabled(true);
				result = null;
				txtDestination.setText("");
				btnConfirm.setEnabled(false);
			}
		});

		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				File temp = null;
				if (btnDirectory.getSelection()) {
					temp = UIUtils.promptForDirectory(shell, "Save Ship as Directory", "Please select the directory to which the ship will be exported.");
				} else {
					temp = UIUtils.promptForSaveFile(shell, "Save Ship as FTL", new String[] { "*.ftl", "*.zip" });
				}

				// User could've aborted selection, which returns null.
				if (temp != null) {
					result = temp;
					txtDestination.setText(temp.getAbsolutePath());
				}

				btnConfirm.setEnabled(result != null);
			}
		});

		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dispose();
			}
		});

		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = null;
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

		shell.pack();
		Point size = shell.getSize();
		size.x = 300;
		shell.setSize(300, size.y);
		Point parSize = parent.getSize();
		Point parLoc = parent.getLocation();
		shell.setLocation(parLoc.x + parSize.x / 3 - size.x / 2, parLoc.y + parSize.y / 3 - size.y / 2);
	}

	public File open() {
		Display display = Display.getDefault();

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		return result;
	}

	public static SaveOptionsDialog getInstance() {
		return instance;
	}

	public boolean isActive() {
		return !shell.isDisposed();
	}

	public void dispose() {
		Cache.checkInImage(this, "cpath:/assets/help.png");
		shell.dispose();
		instance = null;
	}
}
