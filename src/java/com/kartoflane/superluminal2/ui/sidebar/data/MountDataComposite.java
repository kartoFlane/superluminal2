package com.kartoflane.superluminal2.ui.sidebar.data;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kartoflane.superluminal2.Superluminal;
import com.kartoflane.superluminal2.components.enums.Images;
import com.kartoflane.superluminal2.components.enums.OS;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ftl.GibObject;
import com.kartoflane.superluminal2.ftl.WeaponObject;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.MountController;
import com.kartoflane.superluminal2.tools.ManipulationTool;
import com.kartoflane.superluminal2.ui.DirectionCombo;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.WeaponSelectionDialog;
import com.kartoflane.superluminal2.utils.UIUtils;
import com.kartoflane.superluminal2.utils.Utils;

public class MountDataComposite extends Composite implements DataComposite {

	private static final String noLinkText = "Not linked";
	private static final String selectGibText = "Select a gib";

	private MountController controller = null;

	private Label label;
	private Button btnRotated;
	private Label lblRotHelp;
	private Button btnMirrored;
	private Label lblMirHelp;
	private DirectionCombo cmbDirection;
	private Button btnWeapon;
	private Label lblWeaponInfo;
	private Label lblDirHelp;
	private Label lblLinkedGib;
	private Label lblGibInfo;
	private Button btnLinkedGib;
	private Button btnSelectGib;
	private Button btnFollowHull;
	private Label lblFollowHelp;

	public MountDataComposite(Composite parent, MountController control) {
		super(parent, SWT.NONE);

		controller = control;
		Image helpImage = Cache.checkOutImage(this, "cpath:/assets/help.png");

		setLayout(new GridLayout(2, false));

		label = new Label(this, SWT.NONE);
		label.setAlignment(SWT.CENTER);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		label.setText("Mount");

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		btnFollowHull = new Button(this, SWT.CHECK);
		btnFollowHull.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnFollowHull.setText("Follow Hull");

		lblFollowHelp = new Label(this, SWT.NONE);
		lblFollowHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFollowHelp.setImage(helpImage);
		String msg = "When checked, this object will follow the hull image, so that " +
				"when hull is moved, this object is moved as well.";
		UIUtils.addTooltip(lblFollowHelp, Utils.wrapOSNot(msg, Superluminal.WRAP_WIDTH, Superluminal.WRAP_TOLERANCE, OS.MACOSX()));

		btnFollowHull.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnFollowHull.getSelection()) {
					controller.setParent(Manager.getCurrentShip().getImageController(Images.HULL));
				} else {
					controller.setParent(Manager.getCurrentShip().getShipController());
				}
				controller.updateFollowOffset();
			}
		});

		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);

		btnRotated = new Button(this, SWT.CHECK);
		btnRotated.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnRotated.setText("Rotated");

		lblRotHelp = new Label(this, SWT.NONE);
		lblRotHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblRotHelp.setImage(helpImage);
		msg = "This determines the direction the weapon is going to face, " +
				"and in which it's going to shoot.";
		UIUtils.addTooltip(lblRotHelp, Utils.wrapOSNot(msg, Superluminal.WRAP_WIDTH, Superluminal.WRAP_TOLERANCE, OS.MACOSX()));

		btnMirrored = new Button(this, SWT.CHECK);
		btnMirrored.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnMirrored.setText("Mirrored");

		lblMirHelp = new Label(this, SWT.NONE);
		lblMirHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMirHelp.setImage(helpImage);
		msg = "Flips the weapon along X or Y axis, depending on rotation.";
		UIUtils.addTooltip(lblMirHelp, msg);

		Label lblDirection = new Label(this, SWT.NONE);
		lblDirection.setText("Power-up Direction:");

		lblDirHelp = new Label(this, SWT.NONE);
		lblDirHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDirHelp.setImage(helpImage);
		msg = "This determines the direction in which the weapon " +
				"will 'slide' when it is powered up.";
		UIUtils.addTooltip(lblDirHelp, Utils.wrapOSNot(msg, Superluminal.WRAP_WIDTH, Superluminal.WRAP_TOLERANCE, OS.MACOSX()));

		cmbDirection = new DirectionCombo(this, SWT.READ_ONLY, true);
		GridData gd_cmbDirection = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1);
		gd_cmbDirection.widthHint = 80;
		cmbDirection.setLayoutData(gd_cmbDirection);
		cmbDirection.select(0);

		lblLinkedGib = new Label(this, SWT.NONE);
		lblLinkedGib.setText("Linked Gib:");

		lblGibInfo = new Label(this, SWT.NONE);
		lblGibInfo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblGibInfo.setImage(helpImage);
		msg = "This determines the gib to which this mount is attached. " +
				"When the ship explodes, the mount will float along with the gib. " +
				"If no gib is specified, the mount will simply disappear.";
		UIUtils.addTooltip(lblGibInfo, Utils.wrapOSNot(msg, Superluminal.WRAP_WIDTH, Superluminal.WRAP_TOLERANCE, OS.MACOSX()));

		btnLinkedGib = new Button(this, SWT.TOGGLE);
		btnLinkedGib.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnLinkedGib.setText(noLinkText);

		btnSelectGib = new Button(this, SWT.NONE);
		GridData gd_btnSelectGib = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnSelectGib.widthHint = 35;
		btnSelectGib.setLayoutData(gd_btnSelectGib);
		btnSelectGib.setText(">");
		UIUtils.addTooltip(btnSelectGib, "Select the linked gib");

		Label lblWeapon = new Label(this, SWT.NONE);
		lblWeapon.setText("Displayed Weapon:");

		lblWeaponInfo = new Label(this, SWT.NONE);
		lblWeaponInfo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblWeaponInfo.setImage(helpImage);
		msg = "This setting is only cosmetic, and allows " +
				"you to view how a given weapon would look, " +
				"were it placed on this mount.";
		UIUtils.addTooltip(lblWeaponInfo, Utils.wrapOSNot(msg, Superluminal.WRAP_WIDTH, Superluminal.WRAP_TOLERANCE, OS.MACOSX()));

		btnWeapon = new Button(this, SWT.NONE);
		btnWeapon.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		btnWeapon.setText("<No Weapon>");

		btnRotated.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Rectangle oldBounds = controller.getBounds();
				controller.setRotated(btnRotated.getSelection());
				controller.redraw();
				EditorWindow.getInstance().canvasRedraw(oldBounds);
			}
		});

		btnMirrored.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Rectangle oldBounds = controller.getBounds();
				controller.setMirrored(btnMirrored.getSelection());
				controller.redraw();
				EditorWindow.getInstance().canvasRedraw(oldBounds);
			}
		});

		cmbDirection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.setDirection(cmbDirection.getDirection());
			}
		});

		btnWeapon.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				WeaponObject current = controller.getWeapon();
				WeaponSelectionDialog dialog = new WeaponSelectionDialog(EditorWindow.getInstance().getShell());
				WeaponObject neu = dialog.open(current);

				if (neu != null) {
					controller.setWeapon(neu);
					updateData();
				}
			}
		});

		btnLinkedGib.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ManipulationTool tool = (ManipulationTool) Manager.getSelectedTool();

				if (btnLinkedGib.getSelection()) {
					btnLinkedGib.setText(selectGibText);
					tool.setStateMountGibLink();
				} else {
					GibObject gib = controller.getGib();
					btnLinkedGib.setText(gib == Database.DEFAULT_GIB_OBJ ? noLinkText : gib.toString());
					tool.setStateManipulate();
				}
			}
		});

		btnSelectGib.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Manager.setSelected(Manager.getCurrentShip().getController(controller.getGib()));
			}
		});

		updateData();
	}

	@Override
	public void updateData() {
		String alias = controller.getAlias();
		label.setText("Mount " + controller.getId() + (alias == null || alias.trim().equals("") ? "" : " (" + alias + ")"));

		btnFollowHull.setSelection(controller.getParent() == Manager.getCurrentShip().getImageController(Images.HULL));

		btnRotated.setSelection(controller.isRotated());
		btnMirrored.setSelection(controller.isMirrored());

		cmbDirection.select(DirectionCombo.toIndex(controller.getDirection()));

		WeaponObject weapon = controller.getWeapon();
		btnWeapon.setText(weapon.toString());

		GibObject gib = controller.getGib();
		if (gib == Database.DEFAULT_GIB_OBJ)
			btnLinkedGib.setText(noLinkText);
		else
			btnLinkedGib.setText(gib.toString());
		btnLinkedGib.setSelection(false);

		btnSelectGib.setEnabled(gib != Database.DEFAULT_GIB_OBJ);
	}

	@Override
	public void setController(AbstractController controller) {
		this.controller = (MountController) controller;
	}

	public void reloadController() {
	}

	@Override
	public void dispose() {
		super.dispose();
		Cache.checkInImage(this, "cpath:/assets/help.png");
	}
}