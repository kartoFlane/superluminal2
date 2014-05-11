package com.kartoflane.superluminal2.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
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
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;

public class SettingsDialog {
	private static SettingsDialog instance = null;

	private Shell shell = null;
	private Button btnOverlap;
	private Button btnLoader;
	private Button btnUpdates;
	private Button btnGeometry;
	private Button btnMaximise;
	private Button btnSidebar;
	private Button btnCancel;

	public SettingsDialog(Shell parent) {
		if (instance != null)
			throw new IllegalStateException("Previous instance has not been disposed!");
		instance = this;

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText(Superluminal.APP_NAME + " - Settings");
		shell.setLayout(new GridLayout(2, false));

		final TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		TabItem tbtmBehaviour = new TabItem(tabFolder, SWT.NONE);
		tbtmBehaviour.setText("Behaviour");

		final ScrolledComposite scBehaviour = new ScrolledComposite(tabFolder, SWT.V_SCROLL);
		scBehaviour.setAlwaysShowScrollBars(true);
		tbtmBehaviour.setControl(scBehaviour);
		scBehaviour.setExpandHorizontal(true);
		scBehaviour.setExpandVertical(true);

		Composite compBehaviour = new Composite(scBehaviour, SWT.NONE);
		compBehaviour.setLayout(new GridLayout(1, false));

		btnOverlap = new Button(compBehaviour, SWT.CHECK);
		btnOverlap.setText("Allow Room Overlap");

		Label lblOverlap = new Label(compBehaviour, SWT.NONE);
		lblOverlap.setText("Disables room collision, allowing them to overlap.");

		Label separator01 = new Label(compBehaviour, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator01.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		btnLoader = new Button(compBehaviour, SWT.CHECK);
		btnLoader.setText("Close Ship Loader After Loading");

		Label lblLoader = new Label(compBehaviour, SWT.NONE);
		lblLoader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblLoader.setText("Closes the ship loader when a ship is successfully loaded.");
		scBehaviour.setContent(compBehaviour);
		scBehaviour.setMinSize(compBehaviour.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		TabItem tbtmConfig = new TabItem(tabFolder, SWT.NONE);
		tbtmConfig.setText("Config");

		final ScrolledComposite scConfig = new ScrolledComposite(tabFolder, SWT.V_SCROLL);
		scConfig.setAlwaysShowScrollBars(true);
		tbtmConfig.setControl(scConfig);
		scConfig.setExpandHorizontal(true);
		scConfig.setExpandVertical(true);

		Composite compConfig = new Composite(scConfig, SWT.NONE);
		compConfig.setLayout(new GridLayout(1, false));

		btnUpdates = new Button(compConfig, SWT.CHECK);
		btnUpdates.setText("Check for Updates on Startup");

		Label lblUpdates = new Label(compConfig, SWT.WRAP);
		lblUpdates.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		lblUpdates.setText("If true, the editor will check for available updates each time it is started.");

		Label separator11 = new Label(compConfig, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator11.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		btnGeometry = new Button(compConfig, SWT.CHECK);
		btnGeometry.setText("Remember Window Size");

		Label lblGeometry = new Label(compConfig, SWT.WRAP);
		lblGeometry.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		lblGeometry.setText("If true, size of the editor's window will be remembered and restored on startup.");

		Label separator12 = new Label(compConfig, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator12.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		btnMaximise = new Button(compConfig, SWT.CHECK);
		btnMaximise.setText("Start Maximised");

		Label lblMaximise = new Label(compConfig, SWT.NONE);
		lblMaximise.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblMaximise.setText("If true, the editor window will start maximised.\nOverrides 'Remember Window Size'");

		Label separator13 = new Label(compConfig, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator13.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		btnSidebar = new Button(compConfig, SWT.CHECK);
		btnSidebar.setText("Sidebar on Right Side");

		Label lblSidebar = new Label(compConfig, SWT.NONE);
		lblSidebar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblSidebar.setText("If true, the sidebar will be located on the right side of the window.");
		scConfig.setContent(compConfig);
		scConfig.setMinSize(compConfig.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		TabItem tbtmKeybinds = new TabItem(tabFolder, SWT.NONE);
		tbtmKeybinds.setText("Keybinds");

		final ScrolledComposite scKeybinds = new ScrolledComposite(tabFolder, SWT.V_SCROLL);
		scKeybinds.setAlwaysShowScrollBars(true);
		tbtmKeybinds.setControl(scKeybinds);
		scKeybinds.setExpandHorizontal(true);
		scKeybinds.setExpandVertical(true);

		Composite compKeybinds = new Composite(scKeybinds, SWT.NONE);
		compKeybinds.setLayout(new GridLayout(1, false));

		Label lblNYI = new Label(compKeybinds, SWT.NONE);
		lblNYI.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblNYI.setText("(UI not yet implemented - can be edited by hand in hotkeys.xml.\nEditor must not be running, or the changes will not be applied)");
		scKeybinds.setContent(compKeybinds);
		scKeybinds.setMinSize(compKeybinds.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		Button btnConfirm = new Button(shell, SWT.NONE);
		GridData gd_btnConfirm = new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 1);
		gd_btnConfirm.widthHint = 80;
		btnConfirm.setLayoutData(gd_btnConfirm);
		btnConfirm.setText("Confirm");

		btnCancel = new Button(shell, SWT.NONE);
		GridData gd_btnCancel = new GridData(SWT.RIGHT, SWT.BOTTOM, false, false, 1, 1);
		gd_btnCancel.widthHint = 80;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setText("Cancel");

		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Behaviour
				Manager.allowRoomOverlap = btnOverlap.getSelection();
				Manager.closeLoader = btnLoader.getSelection();

				// Config
				Manager.checkUpdates = btnUpdates.getSelection();
				Manager.rememberGeometry = btnGeometry.getSelection();
				Manager.startMaximised = btnMaximise.getSelection();

				// Hotkeys

				// TODO apply settings

				if (Manager.sidebarOnRightSide != btnSidebar.getSelection()) {
					Manager.sidebarOnRightSide = btnSidebar.getSelection();
					EditorWindow.getInstance().layoutSidebar();
				}

				ShipContainer container = Manager.getCurrentShip();
				if (container != null) {
					ShipController sc = container.getShipController();
					for (RoomController rc : container.getRoomControllers()) {
						rc.setCollidable(!Manager.allowRoomOverlap && !sc.isSelected());
					}
				}

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

		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int i = tabFolder.getSelectionIndex();
				if (i == 0)
					scBehaviour.forceFocus();
				else if (i == 1)
					scConfig.forceFocus();
				else if (i == 2)
					scKeybinds.forceFocus();
			}
		});

		shell.setMinimumSize(400, 300);
		shell.pack();

		Point size = shell.getSize();
		Point parSize = parent.getSize();
		Point parLoc = parent.getLocation();
		shell.setLocation(parLoc.x + parSize.x / 3 - size.x / 2, parLoc.y + parSize.y / 3 - size.y / 2);
	}

	public void open() {
		// Behaviour
		btnOverlap.setSelection(Manager.allowRoomOverlap);
		btnLoader.setSelection(Manager.closeLoader);

		// Config
		btnGeometry.setSelection(Manager.rememberGeometry);
		btnMaximise.setSelection(Manager.startMaximised);
		btnUpdates.setSelection(Manager.checkUpdates);
		btnSidebar.setSelection(Manager.sidebarOnRightSide);

		// Hotkeys

		// TODO load settings

		shell.open();
	}

	public static SettingsDialog getInstance() {
		return instance;
	}

	public boolean isActive() {
		return !shell.isDisposed() && shell.isVisible();
	}

	public void dispose() {
		shell.dispose();
		instance = null;
	}
}
