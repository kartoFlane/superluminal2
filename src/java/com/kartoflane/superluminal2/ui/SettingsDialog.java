package com.kartoflane.superluminal2.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
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
	private Button btnResetLinks;

	public SettingsDialog(Shell parent) {
		if (instance != null)
			throw new IllegalStateException("Previous instance has not been disposed!");
		instance = this;

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText(Superluminal.APP_NAME + " - Settings");
		shell.setLayout(new GridLayout(2, false));

		final TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		/*
		 * ====================
		 * Behaviour tab
		 * ====================
		 */

		TabItem tbtmBehaviour = new TabItem(tabFolder, SWT.NONE);
		tbtmBehaviour.setText("Behaviour");

		final ScrolledComposite scBehaviour = new ScrolledComposite(tabFolder, SWT.V_SCROLL);
		scBehaviour.setAlwaysShowScrollBars(true);
		tbtmBehaviour.setControl(scBehaviour);
		scBehaviour.setExpandHorizontal(true);
		scBehaviour.setExpandVertical(true);

		final Composite compBehaviour = new Composite(scBehaviour, SWT.NONE);
		compBehaviour.setLayout(new GridLayout(1, false));

		btnOverlap = new Button(compBehaviour, SWT.CHECK);
		btnOverlap.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		btnOverlap.setText("Allow Room Overlap");

		Label lblOverlap = new Label(compBehaviour, SWT.WRAP);
		lblOverlap.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		lblOverlap.setText("Disables room collision when checked, allowing them to overlap.");

		Label separator01 = new Label(compBehaviour, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator01.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));

		btnLoader = new Button(compBehaviour, SWT.CHECK);
		btnLoader.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		btnLoader.setText("Close Ship Loader After Loading");

		Label lblLoader = new Label(compBehaviour, SWT.WRAP);
		lblLoader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		lblLoader.setText("Closes the ship loader when a ship is successfully loaded.");

		Label separator02 = new Label(compBehaviour, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator02.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));

		btnResetLinks = new Button(compBehaviour, SWT.CHECK);
		btnResetLinks.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		btnResetLinks.setText("Reset Door Links When Door Is Moved");

		Label lblResetLinks = new Label(compBehaviour, SWT.WRAP);
		lblResetLinks.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		lblResetLinks.setText("Resets the door links when a door is moved, so that no accidental connections will be made.");
		scBehaviour.setContent(compBehaviour);

		/*
		 * ====================
		 * Config tab
		 * ====================
		 */

		TabItem tbtmConfig = new TabItem(tabFolder, SWT.NONE);
		tbtmConfig.setText("Config");

		final ScrolledComposite scConfig = new ScrolledComposite(tabFolder, SWT.V_SCROLL);
		scConfig.setExpandHorizontal(true);
		scConfig.setExpandVertical(true);
		scConfig.setAlwaysShowScrollBars(true);
		tbtmConfig.setControl(scConfig);

		final Composite compConfig = new Composite(scConfig, SWT.NONE);
		compConfig.setLayout(new GridLayout(1, false));

		btnUpdates = new Button(compConfig, SWT.CHECK);
		btnUpdates.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		btnUpdates.setText("Check for Updates on Startup");

		Label lblUpdates = new Label(compConfig, SWT.WRAP);
		lblUpdates.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		lblUpdates.setText("If checked, the editor will check for available updates each time it is started.");

		Label separator11 = new Label(compConfig, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator11.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		btnGeometry = new Button(compConfig, SWT.CHECK);
		btnGeometry.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		btnGeometry.setText("Remember Window Size");

		Label lblGeometry = new Label(compConfig, SWT.WRAP);
		lblGeometry.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		lblGeometry.setText("If checked, size of the editor's window will be remembered and restored on startup.");

		Label separator12 = new Label(compConfig, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator12.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		btnMaximise = new Button(compConfig, SWT.CHECK);
		btnMaximise.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		btnMaximise.setText("Start Maximised");

		Label lblMaximise = new Label(compConfig, SWT.WRAP);
		lblMaximise.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		lblMaximise.setText("If checked, the editor window will start maximised. Overrides 'Remember Window Size'");

		Label separator13 = new Label(compConfig, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator13.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		btnSidebar = new Button(compConfig, SWT.CHECK);
		btnSidebar.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		btnSidebar.setText("Sidebar on Right Side");

		Label lblSidebar = new Label(compConfig, SWT.WRAP);
		lblSidebar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		lblSidebar.setText("If checked, the sidebar will be located on the right side of the window.");
		scConfig.setContent(compConfig);

		/*
		 * ====================
		 * Keybinds tab
		 * ====================
		 */

		TabItem tbtmKeybinds = new TabItem(tabFolder, SWT.NONE);
		tbtmKeybinds.setText("Keybinds");

		final ScrolledComposite scKeybinds = new ScrolledComposite(tabFolder, SWT.V_SCROLL);
		scKeybinds.setAlwaysShowScrollBars(true);
		tbtmKeybinds.setControl(scKeybinds);
		scKeybinds.setExpandHorizontal(true);
		scKeybinds.setExpandVertical(true);

		final Composite compKeybinds = new Composite(scKeybinds, SWT.NONE);
		compKeybinds.setLayout(new GridLayout(1, false));

		Label lblNYI = new Label(compKeybinds, SWT.NONE);
		lblNYI.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
		lblNYI.setText("(UI not yet implemented - can be edited by hand in hotkeys.xml.\nEditor must not be running, or the changes will not be applied)");
		scKeybinds.setContent(compKeybinds);

		/*
		 * ====================
		 * Buttons
		 * ====================
		 */

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
				Manager.resetDoorLinksOnMove = btnResetLinks.getSelection();

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

		scKeybinds.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				// Recalculate height in case the resize makes texts
				// wrap or things happen that require it
				Rectangle r = scKeybinds.getClientArea();
				scKeybinds.setMinHeight(compKeybinds.computeSize(r.width, SWT.DEFAULT).y);
			}
		});

		scConfig.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle r = scConfig.getClientArea();
				scConfig.setMinHeight(compConfig.computeSize(r.width, SWT.DEFAULT, true).y);
			}
		});

		scBehaviour.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle r = scBehaviour.getClientArea();
				scBehaviour.setMinHeight(compBehaviour.computeSize(r.width, SWT.DEFAULT).y);
			}
		});

		shell.setMinimumSize(400, 300);
		shell.pack();

		Point size = shell.getSize();
		Point parSize = parent.getSize();
		Point parLoc = parent.getLocation();
		shell.setLocation(parLoc.x + parSize.x / 3 - size.x / 2, parLoc.y + parSize.y / 3 - size.y / 2);

		tabFolder.notifyListeners(SWT.Selection, null);
	}

	public void open() {

		// Behaviour
		btnOverlap.setSelection(Manager.allowRoomOverlap);
		btnLoader.setSelection(Manager.closeLoader);
		btnResetLinks.setSelection(Manager.resetDoorLinksOnMove);

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
