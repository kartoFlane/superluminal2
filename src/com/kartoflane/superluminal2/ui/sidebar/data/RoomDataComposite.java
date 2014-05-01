package com.kartoflane.superluminal2.ui.sidebar.data;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.components.Systems;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.core.Utils;
import com.kartoflane.superluminal2.ftl.GlowSet;
import com.kartoflane.superluminal2.ftl.SystemObject;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;
import com.kartoflane.superluminal2.mvc.controllers.SystemController;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.GlowSelectionDialog;
import com.kartoflane.superluminal2.ui.OverviewWindow;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.SystemsMenu;

public class RoomDataComposite extends Composite implements DataComposite {

	private RoomController roomC = null;
	private ShipContainer container = null;

	private Button btnSystem;
	private Label lblSysLevel;
	private Label lblMaxLevel;
	private Scale scaleSysLevel;
	private Text txtSysLevel;
	private Scale scaleMaxLevel;
	private Text txtMaxLevel;
	private Button btnAvailable;
	private Composite imagesComposite;
	private Button btnInteriorBrowse;
	private Button btnInteriorClear;
	private Text txtInterior;
	private Button btnInteriorView;
	private Label label;
	private Label lblGlow;
	private Button btnGlow;

	public RoomDataComposite(Composite parent, RoomController control) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(3, false));

		this.roomC = control;
		container = Manager.getCurrentShip();
		final ShipController shipC = container.getShipController();

		label = new Label(this, SWT.NONE);
		label.setAlignment(SWT.CENTER);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		label.setText("Room");

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		Label lblSystem = new Label(this, SWT.NONE);
		lblSystem.setText("System: ");

		btnSystem = new Button(this, SWT.NONE);
		GridData gd_btnSystem = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1);
		gd_btnSystem.widthHint = 100;
		btnSystem.setLayoutData(gd_btnSystem);
		btnSystem.setText("");

		btnAvailable = new Button(this, SWT.CHECK);
		btnAvailable.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		btnAvailable.setText("Available At Start");

		lblSysLevel = new Label(this, SWT.NONE);
		lblSysLevel.setText("Starting Level:");

		scaleSysLevel = new Scale(this, SWT.NONE);
		scaleSysLevel.setMaximum(2);
		scaleSysLevel.setMinimum(1);
		scaleSysLevel.setPageIncrement(1);
		scaleSysLevel.setIncrement(1);
		scaleSysLevel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

		txtSysLevel = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		txtSysLevel.setText("");
		txtSysLevel.setTextLimit(3);
		GridData gd_txtSysLevel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_txtSysLevel.widthHint = 20;
		txtSysLevel.setLayoutData(gd_txtSysLevel);

		scaleSysLevel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Systems sys = container.getActiveSystem(roomC.getGameObject());
				SystemController system = container.getSystemController(sys);
				system.setLevel(scaleSysLevel.getSelection());
				txtSysLevel.setText("" + scaleSysLevel.getSelection());
			}
		});

		if (!Manager.getCurrentShip().getShipController().isPlayerShip()) {
			lblMaxLevel = new Label(this, SWT.NONE);
			lblMaxLevel.setText("Max Level:");

			scaleMaxLevel = new Scale(this, SWT.NONE);
			scaleMaxLevel.setMaximum(2);
			scaleMaxLevel.setMinimum(1);
			scaleMaxLevel.setPageIncrement(1);
			scaleMaxLevel.setIncrement(1);
			scaleMaxLevel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

			txtMaxLevel = new Text(this, SWT.BORDER | SWT.READ_ONLY);
			txtMaxLevel.setText("");
			txtMaxLevel.setTextLimit(3);
			GridData gd_txtMaxLevel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
			gd_txtMaxLevel.widthHint = 20;
			txtMaxLevel.setLayoutData(gd_txtMaxLevel);

			scaleMaxLevel.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Systems sys = container.getActiveSystem(roomC.getGameObject());
					SystemController system = container.getSystemController(sys);
					system.setLevelMax(scaleMaxLevel.getSelection());
					txtMaxLevel.setText("" + scaleMaxLevel.getSelection());
					if (!shipC.isPlayerShip()) {
						scaleSysLevel.setMaximum(scaleMaxLevel.getSelection());
						scaleSysLevel.notifyListeners(SWT.Selection, null);
					}
				}
			});
		}

		imagesComposite = new Composite(this, SWT.NONE);
		GridLayout gl_imagesComposite = new GridLayout(4, false);
		gl_imagesComposite.marginHeight = 0;
		gl_imagesComposite.marginWidth = 0;
		imagesComposite.setLayout(gl_imagesComposite);
		imagesComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));

		// Interior widgets
		Label lblInterior = new Label(imagesComposite, SWT.NONE);
		lblInterior.setText("Interior image:");

		btnInteriorView = new Button(imagesComposite, SWT.NONE);
		btnInteriorView.setEnabled(false);
		btnInteriorView.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnInteriorView.setText("View");

		btnInteriorBrowse = new Button(imagesComposite, SWT.NONE);
		btnInteriorBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnInteriorBrowse.setText("Browse");

		btnInteriorClear = new Button(imagesComposite, SWT.NONE);
		btnInteriorClear.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnInteriorClear.setText("Clear");

		txtInterior = new Text(imagesComposite, SWT.BORDER | SWT.READ_ONLY);
		txtInterior.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		// Glow widgets
		lblGlow = new Label(imagesComposite, SWT.NONE);
		lblGlow.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblGlow.setText("Manning glow:");

		btnGlow = new Button(imagesComposite, SWT.NONE);
		GridData gd_btnGlow = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 3, 1);
		gd_btnGlow.widthHint = 120;
		btnGlow.setLayoutData(gd_btnGlow);
		btnGlow.setText("None");

		btnSystem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Point p = btnSystem.getLocation();
				SystemsMenu sysMenu = SystemsMenu.getInstance();
				sysMenu.setLocation(toDisplay(p.x, p.y + btnSystem.getSize().y));
				sysMenu.open();
			}
		});

		btnAvailable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Systems sys = container.getActiveSystem(roomC.getGameObject());
				SystemController system = container.getSystemController(sys);
				system.setAvailableAtStart(btnAvailable.getSelection());
				roomC.redraw();
			}
		});

		SelectionAdapter imageViewListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Systems sys = container.getActiveSystem(roomC.getGameObject());
				SystemController system = container.getSystemController(sys);
				File file = new File(system.getInteriorPath());

				if (file != null && file.exists()) {
					if (Desktop.isDesktopSupported()) {
						Desktop desktop = Desktop.getDesktop();
						if (desktop != null) {
							try {
								desktop.open(file.getParentFile());
							} catch (IOException ex) {
							}
						}
					} else {
						Superluminal.log.error("Unable to open file location - AWT Desktop not supported.");
					}
				}
			}
		};
		btnInteriorView.addSelectionListener(imageViewListener);

		SelectionAdapter imageBrowseListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Systems sys = container.getActiveSystem(roomC.getGameObject());
				SystemController system = container.getSystemController(sys);
				FileDialog dialog = new FileDialog(EditorWindow.getInstance().getShell());
				dialog.setFilterExtensions(new String[] { "*.png" });
				String path = dialog.open();

				// path == null only when user cancels
				if (path != null) {
					system.setInteriorPath("file:" + path);
					updateData();
				}
			}
		};
		btnInteriorBrowse.addSelectionListener(imageBrowseListener);

		SelectionAdapter imageClearListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Systems sys = container.getActiveSystem(roomC.getGameObject());
				SystemController system = container.getSystemController(sys);

				system.setInteriorPath(null);
				updateData();
			}
		};
		btnInteriorClear.addSelectionListener(imageClearListener);

		btnGlow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Systems sys = container.getActiveSystem(roomC.getGameObject());
				SystemObject systemObject = container.getSystemController(sys).getGameObject();
				GlowSet glowSet = GlowSelectionDialog.getInstance().open(systemObject);

				if (glowSet != null) {
					btnGlow.setText(glowSet.getIdentifier());
					systemObject.setGlowSet(glowSet);
				}
			}
		});

		updateData();

		pack();
	}

	public void updateData() {
		if (roomC == null)
			return;

		SystemsMenu sysMenu = SystemsMenu.getInstance();
		sysMenu.setController(roomC);
		sysMenu.disposeSystemSubmenus();
		sysMenu.createSystemSubmenus();

		Systems sys = container.getActiveSystem(roomC.getGameObject());
		SystemController system = container.getSystemController(sys);
		ShipController shipController = container.getShipController();
		boolean playerShip = shipController.isPlayerShip();

		String alias = roomC.getAlias();
		label.setText("Room " + roomC.getId() + (alias == null || alias.equals("") ? "" : " (" + alias + ")"));

		btnSystem.setText(system.toString());

		// Enable/disable buttons
		btnInteriorBrowse.setEnabled(system.canContainInterior());
		btnInteriorClear.setEnabled(system.canContainInterior());
		btnInteriorView.setEnabled(system.getInteriorPath() != null);

		btnGlow.setEnabled(system.canContainGlow() && playerShip);

		btnAvailable.setEnabled(system.getSystemId() != Systems.EMPTY);
		scaleSysLevel.setEnabled(system.getSystemId() != Systems.EMPTY);
		if (!playerShip)
			scaleMaxLevel.setEnabled(system.getSystemId() != Systems.EMPTY);

		if (system.getSystemId() != Systems.EMPTY) {
			// Update widgets with the system's data
			btnAvailable.setSelection(system.isAvailableAtStart());

			if (!playerShip) {
				scaleMaxLevel.setMaximum(system.getLevelCap());
				scaleMaxLevel.setSelection(system.getLevelMax());
				scaleMaxLevel.notifyListeners(SWT.Selection, null);
			}
			scaleSysLevel.setMaximum(playerShip ? system.getLevelCap() : scaleMaxLevel.getSelection());
			scaleSysLevel.setSelection(system.getLevel());
			scaleSysLevel.notifyListeners(SWT.Selection, null);

			String temp = system.getInteriorPath();
			txtInterior.setText(temp == null ? "" : Utils.trimProtocol(temp));
			txtInterior.selectAll();

			if (system.canContainGlow() && playerShip) {
				btnGlow.setText(system.getGameObject().getGlowSet().getIdentifier());
			} else {
				btnGlow.setText("None");
			}

			EditorWindow.getInstance().forceFocus();
		} else {
			// No system - reset to default
			if (!playerShip) {
				scaleMaxLevel.setMaximum(2);
				scaleMaxLevel.setSelection(1);
				txtMaxLevel.setText("");
			}
			scaleSysLevel.setMaximum(2);
			scaleSysLevel.setSelection(1);
			txtSysLevel.setText("");

			txtInterior.setText("");
			btnGlow.setText("None");
		}
		OverviewWindow overview = OverviewWindow.getInstance();
		if (overview != null && overview.isVisible())
			overview.update(roomC);
	}

	@Override
	public void setController(AbstractController roomC) {
		this.roomC = (RoomController) roomC;
	}
}