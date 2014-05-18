package com.kartoflane.superluminal2.ui.sidebar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kartoflane.superluminal2.components.enums.Images;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ftl.ShipObject;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.ImageViewerDialog;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;
import com.kartoflane.superluminal2.utils.IOUtils;

public class ImagesToolComposite extends Composite implements DataComposite {
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

	public ImagesToolComposite(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(4, false));

		container = Manager.getCurrentShip();
		ShipObject ship = container.getShipController().getGameObject();

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
				FileDialog dialog = new FileDialog(EditorWindow.getInstance().getShell());
				dialog.setFilterExtensions(new String[] { "*.png" });
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
					container.setImage(type, "file:" + path);
					updateData();
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

				container.setImage(type, null);
				updateData();
			}
		};

		// Hull widgets
		Label lblHull = new Label(this, SWT.NONE);
		lblHull.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblHull.setText("Hull");

		btnHullView = new Button(this, SWT.NONE);
		btnHullView.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnHullView.setEnabled(false);
		btnHullView.setText("View");

		btnHullView.addSelectionListener(imageViewListener);

		btnHullBrowse = new Button(this, SWT.NONE);
		btnHullBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnHullBrowse.setText("Browse");

		btnHullBrowse.addSelectionListener(imageBrowseListener);

		btnHullClear = new Button(this, SWT.NONE);
		btnHullClear.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnHullClear.setText("Clear");

		btnHullClear.addSelectionListener(imageClearListener);

		txtHull = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		txtHull.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		if (ship.isPlayerShip()) {
			// Floor widgets
			Label lblFloor = new Label(this, SWT.NONE);
			lblFloor.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
			lblFloor.setText("Floor");

			btnFloorView = new Button(this, SWT.NONE);
			btnFloorView.setEnabled(false);
			btnFloorView.setText("View");

			btnFloorBrowse = new Button(this, SWT.NONE);
			btnFloorBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			btnFloorBrowse.setText("Browse");

			btnFloorClear = new Button(this, SWT.NONE);
			btnFloorClear.setText("Clear");

			txtFloor = new Text(this, SWT.BORDER | SWT.READ_ONLY);
			txtFloor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		}

		// Cloak widgets
		Label lblCloak = new Label(this, SWT.NONE);
		lblCloak.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblCloak.setText("Cloak");

		btnCloakView = new Button(this, SWT.NONE);
		btnCloakView.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnCloakView.setEnabled(false);
		btnCloakView.setText("View");
		btnCloakView.addSelectionListener(imageViewListener);

		btnCloakBrowse = new Button(this, SWT.NONE);
		btnCloakBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnCloakBrowse.setText("Browse");
		btnCloakBrowse.addSelectionListener(imageBrowseListener);

		btnCloakClear = new Button(this, SWT.NONE);
		btnCloakClear.setText("Clear");
		btnCloakClear.addSelectionListener(imageClearListener);

		txtCloak = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		txtCloak.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		if (ship.isPlayerShip()) {
			// Shield widgets
			Label lblShield = new Label(this, SWT.NONE);
			lblShield.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
			lblShield.setText("Shield");

			btnShieldView = new Button(this, SWT.NONE);
			btnShieldView.setEnabled(false);
			btnShieldView.setText("View");

			btnShieldBrowse = new Button(this, SWT.NONE);
			btnShieldBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			btnShieldBrowse.setText("Browse");

			btnShieldClear = new Button(this, SWT.NONE);
			btnShieldClear.setText("Clear");

			txtShield = new Text(this, SWT.BORDER | SWT.READ_ONLY);
			txtShield.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

			// Thumbnail widgets
			Label lblMini = new Label(this, SWT.NONE);
			lblMini.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
			lblMini.setText("Thumbnail");

			btnMiniView = new Button(this, SWT.NONE);
			btnMiniView.setEnabled(false);
			btnMiniView.setText("View");

			btnMiniBrowse = new Button(this, SWT.NONE);
			btnMiniBrowse.setText("Browse");

			btnMiniClear = new Button(this, SWT.NONE);
			btnMiniClear.setText("Clear");

			txtMini = new Text(this, SWT.BORDER | SWT.READ_ONLY);
			txtMini.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

			btnFloorView.addSelectionListener(imageViewListener);
			btnShieldView.addSelectionListener(imageViewListener);
			btnMiniView.addSelectionListener(imageViewListener);

			btnFloorBrowse.addSelectionListener(imageBrowseListener);
			btnShieldBrowse.addSelectionListener(imageBrowseListener);
			btnMiniBrowse.addSelectionListener(imageBrowseListener);

			btnFloorClear.addSelectionListener(imageClearListener);
			btnShieldClear.addSelectionListener(imageClearListener);
			btnMiniClear.addSelectionListener(imageClearListener);
		}

		pack();
		updateData();
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
	}

	public void setController(AbstractController c) {
		throw new UnsupportedOperationException();
	}
}
