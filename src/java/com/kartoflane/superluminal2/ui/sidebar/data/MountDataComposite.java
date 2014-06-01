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

import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.ftl.WeaponObject;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.MountController;
import com.kartoflane.superluminal2.ui.DirectionCombo;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.WeaponSelectionDialog;
import com.kartoflane.superluminal2.utils.UIUtils;

public class MountDataComposite extends Composite implements DataComposite {

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

		btnRotated = new Button(this, SWT.CHECK);
		btnRotated.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnRotated.setText("Rotated");

		lblRotHelp = new Label(this, SWT.NONE);
		lblRotHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblRotHelp.setImage(helpImage);
		String msg = "This determines the direction the weapon is going to face,\n" +
				"and in which it's going to shoot.";
		UIUtils.addTooltip(lblRotHelp, "", msg);

		btnMirrored = new Button(this, SWT.CHECK);
		btnMirrored.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		btnMirrored.setText("Mirrored");

		lblMirHelp = new Label(this, SWT.NONE);
		lblMirHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblMirHelp.setImage(helpImage);
		msg = "Flips the weapon along X or Y axis, depending on rotation.";
		UIUtils.addTooltip(lblMirHelp, "", msg);

		Label lblDirection = new Label(this, SWT.NONE);
		lblDirection.setText("Power-up Direction:");

		lblDirHelp = new Label(this, SWT.NONE);
		lblDirHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblDirHelp.setImage(helpImage);
		msg = "This determines the direction in which the weapon\n" +
				"will 'slide' when it is powered up.";
		UIUtils.addTooltip(lblDirHelp, "", msg);

		cmbDirection = new DirectionCombo(this, SWT.READ_ONLY, true);
		GridData gd_cmbDirection = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1);
		gd_cmbDirection.widthHint = 80;
		cmbDirection.setLayoutData(gd_cmbDirection);
		cmbDirection.select(0);

		Label lblWeapon = new Label(this, SWT.NONE);
		lblWeapon.setText("Displayed Weapon:");

		lblWeaponInfo = new Label(this, SWT.NONE);
		lblWeaponInfo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblWeaponInfo.setImage(helpImage);
		msg = "This setting is only cosmetic, and allows\n" +
				"you to view how a given weapon would look,\n" +
				"were it placed on this mount.";
		UIUtils.addTooltip(lblWeaponInfo, "", msg);

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

		updateData();
	}

	@Override
	public void updateData() {
		String alias = controller.getAlias();
		label.setText("Mount " + controller.getId() + (alias == null || alias.trim().equals("") ? "" : " (" + alias + ")"));

		btnRotated.setSelection(controller.isRotated());
		btnMirrored.setSelection(controller.isMirrored());

		cmbDirection.select(DirectionCombo.toIndex(controller.getDirection()));

		WeaponObject weapon = controller.getWeapon();
		btnWeapon.setText(weapon.toString());
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