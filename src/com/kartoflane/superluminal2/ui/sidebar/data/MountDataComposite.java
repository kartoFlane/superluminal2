package com.kartoflane.superluminal2.ui.sidebar.data;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kartoflane.superluminal2.ftl.WeaponObject;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.MountController;
import com.kartoflane.superluminal2.ui.DirectionCombo;
import com.kartoflane.superluminal2.ui.EditorWindow;
import org.eclipse.swt.widgets.Text;

public class MountDataComposite extends Composite implements DataComposite {

	private MountController controller = null;

	private Label label;
	private Button btnRotated;
	private Button btnMirrored;
	private DirectionCombo cmbDirection;
	private Text txtWeapon;

	public MountDataComposite(Composite parent, MountController control) {
		super(parent, SWT.NONE);

		controller = control;

		setLayout(new GridLayout(2, false));

		label = new Label(this, SWT.NONE);
		label.setAlignment(SWT.CENTER);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		label.setText("Mount");

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		btnRotated = new Button(this, SWT.CHECK);
		btnRotated.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		btnRotated.setText("Rotated");

		btnMirrored = new Button(this, SWT.CHECK);
		btnMirrored.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		btnMirrored.setText("Mirrored");

		Label lblDirection = new Label(this, SWT.NONE);
		lblDirection.setText("Power-up Direction:");

		cmbDirection = new DirectionCombo(this, SWT.READ_ONLY, true);
		GridData gd_cmbDirection = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_cmbDirection.widthHint = 80;
		cmbDirection.setLayoutData(gd_cmbDirection);
		cmbDirection.select(0);

		Label lblWeapon = new Label(this, SWT.NONE);
		lblWeapon.setText("Weapon:");

		txtWeapon = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		txtWeapon.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtWeapon.setText("None");

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

		updateData();
	}

	@Override
	public void updateData() {
		String alias = controller.getAlias();
		label.setText("Mount " + controller.getId() + (alias == null || alias.equals("") ? "" : " (" + alias + ")"));

		btnRotated.setSelection(controller.isRotated());
		btnMirrored.setSelection(controller.isMirrored());

		cmbDirection.select(DirectionCombo.toIndex(controller.getDirection()));

		WeaponObject weapon = controller.getWeapon();
		txtWeapon.setText(weapon.toString());
	}

	@Override
	public void setController(AbstractController controller) {
		this.controller = (MountController) controller;
	}
}