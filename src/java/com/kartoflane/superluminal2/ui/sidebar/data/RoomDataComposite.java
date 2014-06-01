package com.kartoflane.superluminal2.ui.sidebar.data;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;

import com.kartoflane.superluminal2.components.enums.Systems;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ftl.GlowSet;
import com.kartoflane.superluminal2.ftl.SystemObject;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;
import com.kartoflane.superluminal2.mvc.controllers.SystemController;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.GlowSelectionDialog;
import com.kartoflane.superluminal2.ui.ImageViewerDialog;
import com.kartoflane.superluminal2.ui.OverviewWindow;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.SystemsMenu;
import com.kartoflane.superluminal2.utils.IOUtils;
import com.kartoflane.superluminal2.utils.UIUtils;

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

		Image helpImage = Cache.checkOutImage(this, "cpath:/assets/help.png");
		roomC = control;
		container = Manager.getCurrentShip();
		final ShipController shipC = container.getShipController();

		label = new Label(this, SWT.NONE);
		label.setAlignment(SWT.CENTER);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		label.setText("Room");

		Label lblHelp = new Label(this, SWT.NONE);
		lblHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblHelp.setImage(helpImage);
		String msg = "- Click on the system button to open system assignment menu\n" +
				"- Alternatively, right-click on the room to open the menu\n" +
				"- When a system is assigned, it'll be visible in the assignment\n" +
				"  menu, under 'Assign' - this menu contains more options\n" +
				"- Multiple systems can be assigned to a single room (should\n" +
				"  only be used for Clonebay/Medbay)\n" +
				"- The editor only shows one system at a time -- you can bring\n" +
				"  one of the other assigned systems into focus by clicking on\n" +
				"  'Select' in the system's submenu.";
		UIUtils.addTooltip(lblHelp, "", msg);

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
		btnAvailable.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		btnAvailable.setText("Available At Start");

		Label lblStartHelp = new Label(this, SWT.NONE);
		lblStartHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStartHelp.setImage(helpImage);
		msg = "This determines whether the ship starts with this system already installed.";
		UIUtils.addTooltip(lblStartHelp, "", msg);

		lblSysLevel = new Label(this, SWT.NONE);
		lblSysLevel.setText("Starting Level:");

		scaleSysLevel = new Scale(this, SWT.NONE);
		scaleSysLevel.setMaximum(2);
		scaleSysLevel.setMinimum(1);
		scaleSysLevel.setPageIncrement(1);
		scaleSysLevel.setIncrement(1);
		scaleSysLevel.setSelection(1);
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
				SystemObject sys = container.getActiveSystem(roomC.getGameObject());
				SystemController system = (SystemController) container.getController(sys);
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
			scaleMaxLevel.setSelection(1);
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
					SystemObject sys = container.getActiveSystem(roomC.getGameObject());
					SystemController system = (SystemController) container.getController(sys);
					system.setLevelMax(scaleMaxLevel.getSelection());
					txtMaxLevel.setText("" + scaleMaxLevel.getSelection());
					if (!shipC.isPlayerShip()) {
						scaleSysLevel.setMaximum(scaleMaxLevel.getSelection());
						scaleSysLevel.notifyListeners(SWT.Selection, null);
						scaleSysLevel.setEnabled(scaleMaxLevel.getSelection() > 1);
						scaleSysLevel.setSelection(Math.min(scaleSysLevel.getSelection(), scaleSysLevel.getMaximum()));
					}
				}
			});
		} else {
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
			btnInteriorClear.setText("Reset");

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

			SelectionAdapter imageViewListener = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					SystemObject sys = container.getActiveSystem(roomC.getGameObject());
					SystemController system = (SystemController) container.getController(sys);
					String path = system.getInteriorPath();
					if (path != null) {
						ImageViewerDialog dialog = new ImageViewerDialog(EditorWindow.getInstance().getShell());
						dialog.open(path);
					}
				}
			};
			btnInteriorView.addSelectionListener(imageViewListener);

			SelectionAdapter imageBrowseListener = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					SystemObject sys = container.getActiveSystem(roomC.getGameObject());
					SystemController system = (SystemController) container.getController(sys);
					FileDialog dialog = new FileDialog(EditorWindow.getInstance().getShell(), SWT.OPEN);
					dialog.setFilterExtensions(new String[] { "*.png" });

					boolean exit = false;
					while (!exit) {
						String path = dialog.open();

						// path == null only when user cancels
						if (path != null) {
							File temp = new File(path);
							if (temp.exists()) {
								system.setVisible(false);
								system.setInteriorPath("file:" + path);
								updateData();
								system.setVisible(true);
								exit = true;
							} else {
								UIUtils.showWarningDialog(EditorWindow.getInstance().getShell(), null, "The file you have selected does not exist.");
							}
						} else {
							exit = true;
						}
					}
				}
			};
			btnInteriorBrowse.addSelectionListener(imageBrowseListener);

			SelectionAdapter imageClearListener = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					SystemObject sys = container.getActiveSystem(roomC.getGameObject());
					SystemController system = (SystemController) container.getController(sys);

					system.setInteriorPath(null);
					updateData();
				}
			};
			btnInteriorClear.addSelectionListener(imageClearListener);

			btnGlow.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					SystemObject systemObject = container.getActiveSystem(roomC.getGameObject());

					GlowSelectionDialog dialog = new GlowSelectionDialog(EditorWindow.getInstance().getShell());
					GlowSet glowSet = dialog.open(systemObject);

					if (glowSet != null) {
						btnGlow.setText(glowSet.getIdentifier());
						systemObject.setGlowSet(glowSet);
					}
				}
			});
		}

		btnSystem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Point p = btnSystem.getLocation();
				SystemsMenu sysMenu = new SystemsMenu(container.getParent().getShell(), roomC);
				sysMenu.setLocation(toDisplay(p.x, p.y + btnSystem.getSize().y));
				sysMenu.open();
			}
		});

		btnAvailable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SystemObject sys = container.getActiveSystem(roomC.getGameObject());
				SystemController system = (SystemController) container.getController(sys);
				system.setAvailableAtStart(btnAvailable.getSelection());
				roomC.redraw();
			}
		});

		updateData();

		pack();
	}

	public void updateData() {
		if (roomC == null)
			return;

		SystemObject sys = container.getActiveSystem(roomC.getGameObject());
		SystemController system = (SystemController) container.getController(sys);
		ShipController shipController = container.getShipController();
		boolean playerShip = shipController.isPlayerShip();

		String alias = roomC.getAlias();
		label.setText("Room " + roomC.getId() + (alias == null || alias.trim().equals("") ? "" : " (" + alias + ")"));

		alias = sys.getAlias();
		btnSystem.setText(sys.toString() + (alias == null || alias.trim().equals("") ? "" : " (" + alias + ")"));

		btnAvailable.setEnabled(system.getSystemId() != Systems.EMPTY);
		scaleSysLevel.setEnabled(system.getSystemId() != Systems.EMPTY);
		if (!playerShip)
			scaleMaxLevel.setEnabled(system.getSystemId() != Systems.EMPTY);

		if (system.getSystemId() != Systems.EMPTY) {
			// Update widgets with the system's data
			btnAvailable.setSelection(system.isAvailableAtStart());

			scaleSysLevel.setMaximum(playerShip ? system.getLevelCap() : scaleMaxLevel.getSelection());
			scaleSysLevel.setSelection(system.getLevel());
			scaleSysLevel.notifyListeners(SWT.Selection, null);

			if (playerShip) {
				btnInteriorBrowse.setEnabled(system.canContainInterior());
				btnInteriorClear.setEnabled(system.canContainInterior());
				btnInteriorView.setEnabled(system.getInteriorPath() != null);

				String temp = system.getInteriorPath();
				txtInterior.setText(temp == null ? "" : IOUtils.trimProtocol(temp));
				txtInterior.selectAll();
				txtInterior.clearSelection();

				btnGlow.setEnabled(system.canContainGlow());
				if (system.canContainGlow()) {
					btnGlow.setText(system.getGameObject().getGlowSet().getIdentifier());
				} else {
					btnGlow.setText("None");
				}
			} else {
				scaleMaxLevel.setMaximum(system.getLevelCap());
				scaleMaxLevel.setSelection(system.getLevelMax());
				scaleMaxLevel.notifyListeners(SWT.Selection, null);

				scaleSysLevel.setEnabled(scaleMaxLevel.getSelection() > 1);
			}
		} else {
			// No system - reset to default
			scaleSysLevel.setMaximum(2);
			scaleSysLevel.setSelection(1);
			txtSysLevel.setText("");

			if (playerShip) {
				btnInteriorBrowse.setEnabled(false);
				btnInteriorClear.setEnabled(false);
				btnInteriorView.setEnabled(false);

				txtInterior.setText("");
				btnGlow.setText("None");
				btnGlow.setEnabled(false);
			} else {
				scaleMaxLevel.setMaximum(2);
				scaleMaxLevel.setSelection(1);
				txtMaxLevel.setText("");
			}
		}
		OverviewWindow.staticUpdate(roomC);
	}

	@Override
	public void setController(AbstractController roomC) {
		this.roomC = (RoomController) roomC;
	}

	public void reloadController() {
	}
}