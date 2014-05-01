package com.kartoflane.superluminal2.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.kartoflane.superluminal2.Superluminal;

public class SettingsDialog {
	private static SettingsDialog instance = null;

	private Shell shell = null;

	public SettingsDialog(Shell parent) {
		instance = this;

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText(Superluminal.APP_NAME + " - Settings");
		shell.setLayout(new GridLayout(2, false));

		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		TabItem tbtmBehaviour = new TabItem(tabFolder, SWT.NONE);
		tbtmBehaviour.setText("Behaviour");

		Composite cBehaviour = new Composite(tabFolder, SWT.NONE);
		tbtmBehaviour.setControl(cBehaviour);
		cBehaviour.setLayout(new GridLayout(1, false));

		Button btnAllowRoomOverlap = new Button(cBehaviour, SWT.CHECK);
		btnAllowRoomOverlap.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnAllowRoomOverlap.setText("Allow Room Overlap");

		Label lblAllowRoomOverlap = new Label(cBehaviour, SWT.NONE);
		lblAllowRoomOverlap.setText("Disables room collision, allowing them to overlap.");

		Label separator1 = new Label(cBehaviour, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		TabItem tbtmKeybinds = new TabItem(tabFolder, SWT.NONE);
		tbtmKeybinds.setText("Keybinds");

		ScrolledComposite scKeybinds = new ScrolledComposite(tabFolder, SWT.BORDER | SWT.V_SCROLL);
		scKeybinds.setAlwaysShowScrollBars(true);
		tbtmKeybinds.setControl(scKeybinds);
		scKeybinds.setExpandHorizontal(true);
		scKeybinds.setExpandVertical(true);

		Composite cKeybinds = new Composite(scKeybinds, SWT.NONE);
		scKeybinds.setContent(cKeybinds);
		scKeybinds.setMinSize(cKeybinds.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		Button btnConfirm = new Button(shell, SWT.NONE);
		GridData gd_btnConfirm = new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 1);
		gd_btnConfirm.widthHint = 80;
		btnConfirm.setLayoutData(gd_btnConfirm);
		btnConfirm.setText("Confirm");

		Button btnCancel = new Button(shell, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.BOTTOM, false, false, 1, 1);
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setText("Cancel");

		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO apply settings
				shell.setVisible(false);
			}
		});

		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.setVisible(false);
			}
		});

		shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event e) {
				shell.setVisible(false);
				e.doit = false;
			}
		});

		shell.setMinimumSize(450, 300);

		shell.pack();
	}

	public void open() {
		// TODO load settings

		shell.open();
	}

	public static SettingsDialog getInstance() {
		return instance;
	}

	public boolean isVisible() {
		return shell.isVisible();
	}

	public void dispose() {
		shell.dispose();
	}
}
