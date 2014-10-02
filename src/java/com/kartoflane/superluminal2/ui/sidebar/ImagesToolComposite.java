package com.kartoflane.superluminal2.ui.sidebar;

import java.io.File;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.kartoflane.superluminal2.components.enums.Images;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ftl.GibObject;
import com.kartoflane.superluminal2.ftl.ShipObject;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.GibController;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.GibWidget;
import com.kartoflane.superluminal2.ui.ImageViewerDialog;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;
import com.kartoflane.superluminal2.undo.UndoablePropertyEdit;
import com.kartoflane.superluminal2.utils.IOUtils;
import com.kartoflane.superluminal2.utils.UIUtils;

@SuppressWarnings("serial")
public class ImagesToolComposite extends Composite implements DataComposite {

	private static String prevImagesPath = System.getProperty("user.home");
	private static String prevGibsPath = System.getProperty("user.home");

	private static int selectedTab = 0;
	private ShipContainer container;

	private Text txtHull;
	private Button btnHullBrowse;
	private Button btnHullClear;
	private Button btnHullView;
	private Text txtFloor;
	private Button btnFloorBrowse;
	private Button btnFloorClear;
	private Button btnFloorView;
	private Text txtCloak;
	private Button btnCloakBrowse;
	private Button btnCloakClear;
	private Button btnCloakView;
	private Text txtShield;
	private Button btnShieldBrowse;
	private Button btnShieldClear;
	private Button btnShieldView;
	private Text txtMini;
	private Button btnMiniBrowse;
	private Button btnMiniClear;
	private Button btnMiniView;
	private TabFolder tabFolder;
	private TabItem tbtmImages;
	private TabItem tbtmGibs;
	private Composite compImages;
	private Composite compGibs;
	private Button btnNew;
	private HashMap<GibController, GibWidget> gibWidgetMap;

	public ImagesToolComposite(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(4, false));

		gibWidgetMap = new HashMap<GibController, GibWidget>();

		container = Manager.getCurrentShip();
		ShipObject ship = container.getShipController().getGameObject();
		final boolean[] created = { false };

		Label lblPropertiesTool = new Label(this, SWT.NONE);
		lblPropertiesTool.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 4, 1));
		lblPropertiesTool.setText("Ship Images");

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 4, 1));

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

				String path = container.getImage(type);

				if (path != null) {
					ImageViewerDialog dialog = new ImageViewerDialog(EditorWindow.getInstance().getShell());
					dialog.open(path);
				}
			}
		};
		SelectionAdapter imageBrowseListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(EditorWindow.getInstance().getShell(), SWT.OPEN);
				dialog.setFilterExtensions(new String[] { "*.png" });
				dialog.setFilterPath(prevImagesPath);
				dialog.setFileName(prevImagesPath);

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

				boolean exit = false;
				while (!exit) {
					String path = dialog.open();

					// path == null only when user cancels
					if (path != null) {
						prevImagesPath = path;
						File temp = new File(path);
						if (temp.exists()) {
							final Images fType = type;
							UndoablePropertyEdit<String> edit = new UndoablePropertyEdit<String>(container) {
								public void callback(String arg) {
									container.setImage(fType, arg);
									if (!isDisposed())
										updateData();
								}

								@Override
								public String getPresentationName() {
									return String.format("change %s image", fType.toString());
								}
							};

							edit.setOld(container.getImage(type));
							edit.setCurrent("file:" + path);

							container.setImage(type, "file:" + path);
							updateData();
							exit = true;

							if (!edit.isValuesEqual())
								Manager.getCurrentShip().postEdit(edit);
						} else {
							UIUtils.showWarningDialog(EditorWindow.getInstance().getShell(), null, "The file you have selected does not exist.");
						}
					} else {
						exit = true;
					}
				}
			}
		};
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

				final Images fType = type;
				UndoablePropertyEdit<String> edit = new UndoablePropertyEdit<String>(container) {
					public void callback(String arg) {
						container.setImage(fType, arg);
						if (!isDisposed())
							updateData();
					}

					@Override
					public String getPresentationName() {
						return String.format("clear %s image", fType.toString());
					}
				};

				edit.setOld(container.getImage(type));
				edit.setCurrent(null);

				container.setImage(type, null);
				updateData();

				if (!edit.isValuesEqual())
					Manager.getCurrentShip().postEdit(edit);
			}
		};

		tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (created[0])
					selectedTab = tabFolder.getSelectionIndex();
			}
		});

		/*
		 * ===============================================
		 * IMAGES TAB
		 * ===============================================
		 */

		tbtmImages = new TabItem(tabFolder, SWT.NONE);
		tbtmImages.setText("Images");

		compImages = new Composite(tabFolder, SWT.NONE);
		tbtmImages.setControl(compImages);
		compImages.setLayout(new GridLayout(4, false));

		// Hull widgets
		Label lblHull = new Label(compImages, SWT.NONE);
		lblHull.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblHull.setText("Hull");

		btnHullView = new Button(compImages, SWT.NONE);
		btnHullView.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnHullView.setEnabled(false);
		btnHullView.setText("View");
		btnHullView.addSelectionListener(imageViewListener);

		btnHullBrowse = new Button(compImages, SWT.NONE);
		btnHullBrowse.setText("Browse");
		btnHullBrowse.addSelectionListener(imageBrowseListener);

		btnHullClear = new Button(compImages, SWT.NONE);
		btnHullClear.setText("Clear");
		btnHullClear.addSelectionListener(imageClearListener);

		txtHull = new Text(compImages, SWT.BORDER | SWT.READ_ONLY);
		txtHull.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		if (ship.isPlayerShip()) {
			// Floor widgets
			Label lblFloor = new Label(compImages, SWT.NONE);
			lblFloor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			lblFloor.setText("Floor");

			btnFloorView = new Button(compImages, SWT.NONE);
			btnFloorView.setEnabled(false);
			btnFloorView.setText("View");
			btnFloorView.addSelectionListener(imageViewListener);

			btnFloorBrowse = new Button(compImages, SWT.NONE);
			btnFloorBrowse.setText("Browse");
			btnFloorBrowse.addSelectionListener(imageBrowseListener);

			btnFloorClear = new Button(compImages, SWT.NONE);
			btnFloorClear.setText("Clear");
			btnFloorClear.addSelectionListener(imageClearListener);

			txtFloor = new Text(compImages, SWT.BORDER | SWT.READ_ONLY);
			txtFloor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		}

		// Cloak widgets
		Label lblCloak = new Label(compImages, SWT.NONE);
		lblCloak.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblCloak.setText("Cloak");

		btnCloakView = new Button(compImages, SWT.NONE);
		btnCloakView.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnCloakView.setEnabled(false);
		btnCloakView.setText("View");
		btnCloakView.addSelectionListener(imageViewListener);

		btnCloakBrowse = new Button(compImages, SWT.NONE);
		btnCloakBrowse.setText("Browse");
		btnCloakBrowse.addSelectionListener(imageBrowseListener);

		btnCloakClear = new Button(compImages, SWT.NONE);
		btnCloakClear.setText("Clear");

		txtCloak = new Text(compImages, SWT.BORDER | SWT.READ_ONLY);
		txtCloak.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		btnCloakClear.addSelectionListener(imageClearListener);

		if (ship.isPlayerShip()) {
			// Shield widgets
			Label lblShield = new Label(compImages, SWT.NONE);
			lblShield.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			lblShield.setText("Shield");

			btnShieldView = new Button(compImages, SWT.NONE);
			btnShieldView.setEnabled(false);
			btnShieldView.setText("View");
			btnShieldView.addSelectionListener(imageViewListener);

			btnShieldBrowse = new Button(compImages, SWT.NONE);
			btnShieldBrowse.setText("Browse");
			btnShieldBrowse.addSelectionListener(imageBrowseListener);

			btnShieldClear = new Button(compImages, SWT.NONE);
			btnShieldClear.setText("Clear");
			btnShieldClear.addSelectionListener(imageClearListener);

			txtShield = new Text(compImages, SWT.BORDER | SWT.READ_ONLY);
			txtShield.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

			// Thumbnail widgets
			Label lblMini = new Label(compImages, SWT.NONE);
			lblMini.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			lblMini.setText("Thumbnail");

			btnMiniView = new Button(compImages, SWT.NONE);
			btnMiniView.setEnabled(false);
			btnMiniView.setText("View");
			btnMiniView.addSelectionListener(imageViewListener);

			btnMiniBrowse = new Button(compImages, SWT.NONE);
			btnMiniBrowse.setText("Browse");
			btnMiniBrowse.addSelectionListener(imageBrowseListener);

			btnMiniClear = new Button(compImages, SWT.NONE);
			btnMiniClear.setText("Clear");
			btnMiniClear.addSelectionListener(imageClearListener);

			txtMini = new Text(compImages, SWT.BORDER | SWT.READ_ONLY);
			txtMini.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		}

		/*
		 * ===============================================
		 * GIBS TAB
		 * ===============================================
		 */

		tbtmGibs = new TabItem(tabFolder, SWT.NONE);
		tbtmGibs.setText("Gibs");

		compGibs = new Composite(tabFolder, SWT.NONE);
		tbtmGibs.setControl(compGibs);
		compGibs.setLayout(new GridLayout(1, false));

		btnNew = new Button(compGibs, SWT.NONE);
		btnNew.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnNew.setText("New Gib");

		btnNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(EditorWindow.getInstance().getShell(), SWT.OPEN);
				dialog.setFilterExtensions(new String[] { "*.png" });
				dialog.setFilterPath(prevGibsPath);
				dialog.setFileName(prevGibsPath);

				boolean exit = false;
				while (!exit) {
					String path = dialog.open();

					// path == null only when user cancels
					if (path != null) {
						prevGibsPath = path;
						File temp = new File(path);
						if (temp.exists()) {
							GibObject gib = new GibObject();
							gib.setId(container.getGibControllers().length + 1);
							gib.setImagePath("file:" + path);
							GibController gc = GibController.newInstance(container, gib);
							gc.updateFollower();
							gc.setParent(container.getImageController(Images.HULL));
							gc.updateFollowOffset();
							gc.redraw();

							container.add(gc);
							container.store(gc);

							GibWidget gw = new GibWidget(compGibs, gc);
							gibWidgetMap.put(gc, gw);

							compGibs.layout();

							exit = true;
						} else {
							UIUtils.showWarningDialog(EditorWindow.getInstance().getShell(), null, "The file you have selected does not exist.");
						}
					} else {
						exit = true;
					}
				}
			}
		});

		for (GibController gc : container.getGibControllers()) {
			GibWidget gw = new GibWidget(compGibs, gc);
			gibWidgetMap.put(gc, gw);
		}

		pack();
		updateData();

		created[0] = true;
		tabFolder.setSelection(selectedTab);
	}

	public void updateData() {
		ShipObject ship = container.getShipController().getGameObject();

		// Update image path text fields and scroll them to the end to show the file's name
		String content = container.getImage(Images.HULL);

		txtHull.setText(content == null ? "" : IOUtils.trimProtocol(content));
		txtHull.selectAll();
		txtHull.clearSelection();
		btnHullView.setEnabled(content != null);

		content = container.getImage(Images.CLOAK);
		txtCloak.setText(content == null ? "" : IOUtils.trimProtocol(content));
		txtCloak.selectAll();
		txtCloak.clearSelection();
		btnCloakView.setEnabled(content != null);

		if (ship.isPlayerShip()) {
			content = container.getImage(Images.FLOOR);
			txtFloor.setText(content == null ? "" : IOUtils.trimProtocol(content));
			txtFloor.selectAll();
			txtFloor.clearSelection();
			btnFloorView.setEnabled(content != null);

			content = container.getImage(Images.SHIELD);
			txtShield.setText(content == null ? "" : IOUtils.trimProtocol(content));
			txtShield.selectAll();
			txtShield.clearSelection();
			btnShieldView.setEnabled(content != null);

			content = container.getImage(Images.THUMBNAIL);
			txtMini.setText(content == null || !ship.isPlayerShip() ? "" : IOUtils.trimProtocol(content));
			txtMini.selectAll();
			txtMini.clearSelection();
			btnMiniView.setEnabled(content != null);
		}

		for (GibWidget gw : gibWidgetMap.values()) {
			gw.updateData();
		}
	}

	public void setController(AbstractController c) {
		throw new UnsupportedOperationException();
	}

	public void reloadController() {
	}

	public static void setPrevGibsPath(String path) {
		prevGibsPath = path;
	}

	public static String getPrevGibsPath() {
		return prevGibsPath;
	}
}
