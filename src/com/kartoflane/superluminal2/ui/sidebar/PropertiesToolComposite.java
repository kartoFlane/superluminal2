package com.kartoflane.superluminal2.ui.sidebar;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ftl.ShipObject.Images;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;
import com.kartoflane.superluminal2.ui.EditorWindow;

public class PropertiesToolComposite extends Composite implements SidebarComposite {
	private Text txtHull;
	private Button btnHullBrowse;
	private Button btnHullClear;
	private Text txtFloor;
	private Button btnFloorBrowse;
	private Button btnFloorClear;
	private Text txtCloak;
	private Button btnCloakBrowse;
	private Button btnCloakClear;
	private Text txtShield;
	private Button btnShieldBrowse;
	private Button btnShieldClear;
	private Text txtMini;
	private Button btnMiniBrowse;
	private Button btnMiniClear;
	private Text txtName;
	private Text txtClass;
	private Text txtDesc;
	private Button btnHullView;
	private Button btnFloorView;
	private Button btnCloakView;
	private Button btnShieldView;
	private Button btnMiniView;
	private Spinner spHealth;
	private Spinner spPower;
	private TabItem tbtmCrew;
	private Composite crewComposite;
	private Tree crewTree;
	private TreeItem trtmCrewSlot1;
	private TreeItem trtmCrewSlot2;
	private TreeItem trtmCrewSlot3;
	private TreeItem trtmCrewSlot4;
	private TreeItem trtmCrewSlot5;
	private TreeItem trtmCrewSlot6;
	private TreeItem trtmCrewSlot7;
	private TreeItem trtmCrewSlot8;

	private ShipController controller = null;
	private int crewBudget = 8;

	public PropertiesToolComposite(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(1, false));

		controller = Manager.getCurrentShip().getShipController();

		Label lblPropertiesTool = new Label(this, SWT.NONE);
		lblPropertiesTool.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
		lblPropertiesTool.setText("Properties");

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		final TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		/*
		 * =========================================================================
		 * XXX: Images tab
		 * =========================================================================
		 */
		TabItem tbtmImages = new TabItem(tabFolder, SWT.NONE);
		tbtmImages.setText("Images");

		Composite imagesComposite = new Composite(tabFolder, SWT.NONE);
		tbtmImages.setControl(imagesComposite);
		imagesComposite.setLayout(new GridLayout(4, false));

		// Hull widgets
		Label lblHull = new Label(imagesComposite, SWT.NONE);
		lblHull.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblHull.setText("Hull");

		btnHullView = new Button(imagesComposite, SWT.NONE);
		btnHullView.setEnabled(false);
		btnHullView.setText("View");

		btnHullBrowse = new Button(imagesComposite, SWT.NONE);
		btnHullBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnHullBrowse.setText("Browse");

		btnHullClear = new Button(imagesComposite, SWT.NONE);
		btnHullClear.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnHullClear.setText("Clear");

		txtHull = new Text(imagesComposite, SWT.BORDER | SWT.READ_ONLY);
		txtHull.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		// Floor widgets
		Label lblFloor = new Label(imagesComposite, SWT.NONE);
		lblFloor.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblFloor.setText("Floor");

		btnFloorView = new Button(imagesComposite, SWT.NONE);
		btnFloorView.setEnabled(false);
		btnFloorView.setText("View");

		btnFloorBrowse = new Button(imagesComposite, SWT.NONE);
		btnFloorBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnFloorBrowse.setText("Browse");

		btnFloorClear = new Button(imagesComposite, SWT.NONE);
		btnFloorClear.setText("Clear");

		txtFloor = new Text(imagesComposite, SWT.BORDER | SWT.READ_ONLY);
		txtFloor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		// Cloak widgets
		Label lblCloak = new Label(imagesComposite, SWT.NONE);
		lblCloak.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblCloak.setText("Cloak");

		btnCloakView = new Button(imagesComposite, SWT.NONE);
		btnCloakView.setEnabled(false);
		btnCloakView.setText("View");

		btnCloakBrowse = new Button(imagesComposite, SWT.NONE);
		btnCloakBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnCloakBrowse.setText("Browse");

		btnCloakClear = new Button(imagesComposite, SWT.NONE);
		btnCloakClear.setText("Clear");

		txtCloak = new Text(imagesComposite, SWT.BORDER | SWT.READ_ONLY);
		txtCloak.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		// Shield widgets
		Label lblShield = new Label(imagesComposite, SWT.NONE);
		lblShield.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblShield.setText("Shield");

		btnShieldView = new Button(imagesComposite, SWT.NONE);
		btnShieldView.setEnabled(false);
		btnShieldView.setText("View");

		btnShieldBrowse = new Button(imagesComposite, SWT.NONE);
		btnShieldBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnShieldBrowse.setText("Browse");

		btnShieldClear = new Button(imagesComposite, SWT.NONE);
		btnShieldClear.setText("Clear");

		txtShield = new Text(imagesComposite, SWT.BORDER | SWT.READ_ONLY);
		txtShield.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		// Thumbnail widgets
		Label lblMini = new Label(imagesComposite, SWT.NONE);
		lblMini.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblMini.setText("Thumbnail");

		btnMiniView = new Button(imagesComposite, SWT.NONE);
		btnMiniView.setEnabled(false);
		btnMiniView.setText("View");

		btnMiniBrowse = new Button(imagesComposite, SWT.NONE);
		btnMiniBrowse.setText("Browse");

		btnMiniClear = new Button(imagesComposite, SWT.NONE);
		btnMiniClear.setText("Clear");

		txtMini = new Text(imagesComposite, SWT.BORDER | SWT.READ_ONLY);
		txtMini.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		SelectionAdapter imageViewListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Images type = null;
				if (e.getSource() == btnHullView)
					type = Images.HULL;
				else if (e.getSource() == btnFloorView)
					type = Images.FLOOR;
				else if (e.getSource() == btnCloakView)
					type = Images.CLOAK;
				else if (e.getSource() == btnShieldView)
					type = Images.SHIELD;
				else if (e.getSource() == btnMiniView)
					type = Images.THUMBNAIL;

				String path = controller.getImagePath(type);
				if (path == null)
					return;

				File file = new File(path);
				if (file.exists()) {
					if (Desktop.isDesktopSupported()) {
						Desktop desktop = Desktop.getDesktop();
						if (desktop != null) {
							try {
								desktop.open(file.getParentFile());
							} catch (IOException ex) {
							}
						}
					} else
						Superluminal.log.error("Unable to open file location - AWT Desktop not supported.");
				}
			}
		};
		btnHullView.addSelectionListener(imageViewListener);
		btnFloorView.addSelectionListener(imageViewListener);
		btnCloakView.addSelectionListener(imageViewListener);
		btnShieldView.addSelectionListener(imageViewListener);
		btnMiniView.addSelectionListener(imageViewListener);

		SelectionAdapter imageBrowseListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(EditorWindow.getInstance().getShell());
				String path = dialog.open();

				Images type = null;
				if (e.getSource() == btnHullBrowse)
					type = Images.HULL;
				else if (e.getSource() == btnFloorBrowse)
					type = Images.FLOOR;
				else if (e.getSource() == btnCloakBrowse)
					type = Images.CLOAK;
				else if (e.getSource() == btnShieldBrowse)
					type = Images.SHIELD;
				else if (e.getSource() == btnMiniBrowse)
					type = Images.THUMBNAIL;

				// path == null only when user cancels
				if (path != null) {
					controller.setImagePath(type, path);
					updateData();
				}
			}
		};
		btnHullBrowse.addSelectionListener(imageBrowseListener);
		btnFloorBrowse.addSelectionListener(imageBrowseListener);
		btnCloakBrowse.addSelectionListener(imageBrowseListener);
		btnShieldBrowse.addSelectionListener(imageBrowseListener);
		btnMiniBrowse.addSelectionListener(imageBrowseListener);

		SelectionAdapter imageClearListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Images type = null;
				if (e.getSource() == btnHullClear)
					type = Images.HULL;
				else if (e.getSource() == btnFloorClear)
					type = Images.FLOOR;
				else if (e.getSource() == btnCloakClear)
					type = Images.CLOAK;
				else if (e.getSource() == btnShieldClear)
					type = Images.SHIELD;
				else if (e.getSource() == btnMiniClear)
					type = Images.THUMBNAIL;

				controller.setImagePath(type, null);
				updateData();
			}
		};
		btnHullClear.addSelectionListener(imageClearListener);
		btnFloorClear.addSelectionListener(imageClearListener);
		btnCloakClear.addSelectionListener(imageClearListener);
		btnShieldClear.addSelectionListener(imageClearListener);
		btnMiniClear.addSelectionListener(imageClearListener);

		/*
		 * =========================================================================
		 * XXX: General tab
		 * =========================================================================
		 */
		TabItem tbtmGeneral = new TabItem(tabFolder, SWT.NONE);
		tbtmGeneral.setText("General");

		Composite generalComposite = new Composite(tabFolder, SWT.NONE);
		tbtmGeneral.setControl(generalComposite);
		generalComposite.setLayout(new GridLayout(2, false));

		Label lblName = new Label(generalComposite, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblName.setText("Name:");

		txtName = new Text(generalComposite, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		Label lblClass = new Label(generalComposite, SWT.NONE);
		lblClass.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblClass.setText("Class:");

		txtClass = new Text(generalComposite, SWT.BORDER);
		txtClass.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		final Label lblDesc = new Label(generalComposite, SWT.NONE);
		lblDesc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		lblDesc.setText("Description: (0/255)");

		txtDesc = new Text(generalComposite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		GridData gd_txtDesc = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_txtDesc.heightHint = 80;
		txtDesc.setLayoutData(gd_txtDesc);

		txtDesc.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				lblDesc.setText("Description: (" + txtDesc.getText().length() + "/255)");
			}
		});

		Label lblHealth = new Label(generalComposite, SWT.NONE);
		lblHealth.setText("Hull Health:");

		spHealth = new Spinner(generalComposite, SWT.BORDER);
		spHealth.setTextLimit(3);
		spHealth.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		Label lblReactor = new Label(generalComposite, SWT.NONE);
		lblReactor.setText("Reactor Power:");

		spPower = new Spinner(generalComposite, SWT.BORDER);
		spPower.setTextLimit(3);
		spPower.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		// Enemy ship-specific widgets
		if (!controller.isPlayerShip()) {
			Label lblMinSector = new Label(generalComposite, SWT.NONE);
			lblMinSector.setText("Min Sector:");

			Spinner spMinSec = new Spinner(generalComposite, SWT.BORDER);
			spMinSec.setTextLimit(1);
			spMinSec.setMaximum(7);
			spMinSec.setMinimum(1);
			spMinSec.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

			Label lblMaxSector = new Label(generalComposite, SWT.NONE);
			lblMaxSector.setText("Max Sector:");

			Spinner spMaxSec = new Spinner(generalComposite, SWT.BORDER);
			spMaxSec.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			spMaxSec.setTextLimit(1);
			spMaxSec.setMaximum(7);
			spMaxSec.setMinimum(1);
		}

		/*
		 * =========================================================================
		 * XXX: Armaments tab
		 * =========================================================================
		 */
		TabItem tbtmArmaments = new TabItem(tabFolder, 0);
		tbtmArmaments.setText("Armaments");

		Composite armComposite = new Composite(tabFolder, SWT.NONE);
		tbtmArmaments.setControl(armComposite);
		armComposite.setLayout(new GridLayout(1, false));

		Group grpWeapons = new Group(armComposite, SWT.NONE);
		grpWeapons.setLayout(new GridLayout(2, false));
		grpWeapons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		grpWeapons.setText("Weapons");

		Label lblMissiles = new Label(grpWeapons, SWT.NONE);
		lblMissiles.setText("Starting Missiles");

		Spinner spMissiles = new Spinner(grpWeapons, SWT.BORDER);
		spMissiles.setMaximum(999);
		spMissiles.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

		Label lblWeaponSlots = new Label(grpWeapons, SWT.NONE);
		lblWeaponSlots.setText("Slots");

		Spinner spWeaponSlots = new Spinner(grpWeapons, SWT.BORDER);
		spWeaponSlots.setMaximum(10);
		spWeaponSlots.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

		/*
		 * =========================================================================
		 * XXX: Crew tab
		 * =========================================================================
		 */
		tbtmCrew = new TabItem(tabFolder, SWT.NONE);
		tbtmCrew.setText("Crew");

		crewComposite = new Composite(tabFolder, SWT.NONE);
		tbtmCrew.setControl(crewComposite);
		crewComposite.setLayout(new GridLayout(1, false));

		crewTree = new Tree(crewComposite, SWT.BORDER);
		crewTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		trtmCrewSlot1 = new TreeItem(crewTree, SWT.NONE);
		trtmCrewSlot1.setText("Empty");

		trtmCrewSlot2 = new TreeItem(crewTree, SWT.NONE);
		trtmCrewSlot2.setText("Empty");

		trtmCrewSlot3 = new TreeItem(crewTree, SWT.NONE);
		trtmCrewSlot3.setText("Empty");

		trtmCrewSlot4 = new TreeItem(crewTree, SWT.NONE);
		trtmCrewSlot4.setText("Empty");

		trtmCrewSlot5 = new TreeItem(crewTree, SWT.NONE);
		trtmCrewSlot5.setText("Empty");

		trtmCrewSlot6 = new TreeItem(crewTree, SWT.NONE);
		trtmCrewSlot6.setText("Empty");

		trtmCrewSlot7 = new TreeItem(crewTree, SWT.NONE);
		trtmCrewSlot7.setText("Empty");

		trtmCrewSlot8 = new TreeItem(crewTree, SWT.NONE);
		trtmCrewSlot8.setText("Empty");

		Menu crewMenu = new Menu(crewTree);
		crewTree.setMenu(crewMenu);

		MenuItem mntmHuman = new MenuItem(crewMenu, SWT.NONE);
		mntmHuman.setText("Human");

		MenuItem mntmEngi = new MenuItem(crewMenu, SWT.NONE);
		mntmEngi.setText("Engi");

		MenuItem mntmZoltan = new MenuItem(crewMenu, SWT.NONE);
		mntmZoltan.setText("Zoltan");

		MenuItem mntmRock = new MenuItem(crewMenu, SWT.NONE);
		mntmRock.setText("Rock");

		MenuItem mntmMantis = new MenuItem(crewMenu, SWT.NONE);
		mntmMantis.setText("Mantis");

		MenuItem mntmSlug = new MenuItem(crewMenu, SWT.NONE);
		mntmSlug.setText("Slug");

		MenuItem mntmCrystal = new MenuItem(crewMenu, SWT.NONE);
		mntmCrystal.setText("Crystal");

		MenuItem mntmGhost = new MenuItem(crewMenu, SWT.NONE);
		mntmGhost.setText("Ghost");

		MenuItem mntmRandom = new MenuItem(crewMenu, SWT.NONE);
		mntmRandom.setText("Random");

		pack();
		updateData();
	}

	public void updateData() {
		if (controller == null)
			return;

		// btnPlayer.setSelection(ship.isPlayerShip());

		// update image path text fields and scroll them to the end to show the file's name
		String path = controller.getImagePath(Images.HULL);

		txtHull.setText(path == null ? "" : path);
		txtHull.selectAll();
		btnHullView.setEnabled(path != null);

		path = controller.getImagePath(Images.FLOOR);
		txtFloor.setText(path == null ? "" : path);
		txtFloor.selectAll();
		btnFloorView.setEnabled(path != null);

		path = controller.getImagePath(Images.CLOAK);
		txtCloak.setText(path == null ? "" : path);
		txtCloak.selectAll();
		btnCloakView.setEnabled(path != null);

		path = controller.getImagePath(Images.SHIELD);
		txtShield.setText(path == null ? "" : path);
		txtShield.selectAll();
		btnShieldView.setEnabled(path != null);

		txtMini.setText(path == null || !controller.isPlayerShip() ? "" : path);
		txtMini.selectAll();
		btnMiniView.setEnabled(controller.isPlayerShip() && path != null);
		btnMiniBrowse.setEnabled(controller.isPlayerShip());
		btnMiniClear.setEnabled(controller.isPlayerShip());
	}

	@Override
	public DataComposite getDataComposite() {
		return null; // no data container
	}

	@Override
	public Composite getComposite() {
		return null; // no data container
	}

	@Override
	public boolean isFocusControl() {
		return txtName.isFocusControl() || txtClass.isFocusControl() || txtDesc.isFocusControl() ||
				spHealth.isFocusControl() || spPower.isFocusControl();
	}
}
