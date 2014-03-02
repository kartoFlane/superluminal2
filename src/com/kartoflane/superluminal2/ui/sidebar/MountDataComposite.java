package com.kartoflane.superluminal2.ui.sidebar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.MountController;
import com.kartoflane.superluminal2.ui.EditorWindow;

public class MountDataComposite extends Composite implements DataComposite {

	private Button btnRotated;
	private Button btnMirrored;
	private Combo directionCombo;
	private Combo categoryCombo;
	private Combo weaponCombo;

	private MountController controller = null;

	public MountDataComposite(Composite parent, MountController controller) {
		super(parent, SWT.NONE);

		this.controller = controller;

		setLayout(new GridLayout(2, false));

		Label label = new Label(this, SWT.NONE);
		label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 3, 1));
		label.setText("Weapon Mount");

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		btnRotated = new Button(this, SWT.CHECK);
		btnRotated.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		btnRotated.setText("Rotated");

		btnMirrored = new Button(this, SWT.CHECK);
		btnMirrored.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		btnMirrored.setText("Mirrored");

		Label lblDirection = new Label(this, SWT.NONE);
		lblDirection.setText("Direction:");

		directionCombo = new Combo(this, SWT.READ_ONLY);
		GridData gd_directionCombo = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_directionCombo.widthHint = 80;
		directionCombo.setLayoutData(gd_directionCombo);
		directionCombo.add("Up");
		directionCombo.add("Left");
		directionCombo.add("Right");
		directionCombo.add("Down");
		directionCombo.add("None");
		directionCombo.select(0);

		Label lblWeapon = new Label(this, SWT.NONE);
		lblWeapon.setText("Weapon:");

		categoryCombo = new Combo(this, SWT.READ_ONLY);
		categoryCombo.setToolTipText("Category");
		GridData gd_categoryCombo = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_categoryCombo.widthHint = 80;
		categoryCombo.setLayoutData(gd_categoryCombo);
		categoryCombo.add("No Weapon");
		categoryCombo.add("Laser / Ion");
		categoryCombo.add("Beam");
		categoryCombo.add("Missile");
		categoryCombo.add("Bomb");
		categoryCombo.select(0);

		weaponCombo = new Combo(this, SWT.READ_ONLY);
		weaponCombo.setEnabled(false);
		weaponCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		btnRotated.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Rectangle oldBounds = MountDataComposite.this.controller.getBounds();
				MountDataComposite.this.controller.setRotated(btnRotated.getSelection());
				EditorWindow.getInstance().canvasRedraw(MountDataComposite.this.controller.getBounds());
				EditorWindow.getInstance().canvasRedraw(oldBounds);
			}
		});
		btnMirrored.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Rectangle oldBounds = MountDataComposite.this.controller.getBounds();
				MountDataComposite.this.controller.setMirrored(btnMirrored.getSelection());
				EditorWindow.getInstance().canvasRedraw(MountDataComposite.this.controller.getBounds());
				EditorWindow.getInstance().canvasRedraw(oldBounds);
			}
		});

		directionCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MountDataComposite.this.controller.setDirection(indexToDirection(directionCombo.getSelectionIndex()));
			}
		});

		updateData();
	}

	@Override
	public void updateData() {
		btnRotated.setSelection(controller.getModel().isRotated());
		btnMirrored.setSelection(controller.getModel().isMirrored());

		directionCombo.select(directionToIndex(controller.getModel().getDirection()));
	}

	@Override
	public MountController getController() {
		return controller;
	}

	@Override
	public void setController(AbstractController controller) {
		this.controller = (MountController) controller;
	}

	private int directionToIndex(int dir) {
		switch (dir) {
			case SWT.UP:
				return 0;
			case SWT.LEFT:
				return 1;
			case SWT.RIGHT:
				return 2;
			case SWT.DOWN:
				return 3;
			case SWT.NONE:
				return 4;
			default:
				throw new IllegalArgumentException("Unknown direction: " + dir);
		}
	}

	private int indexToDirection(int index) {
		switch (index) {
			case 0:
				return SWT.UP;
			case 1:
				return SWT.LEFT;
			case 2:
				return SWT.RIGHT;
			case 3:
				return SWT.DOWN;
			case 4:
				return SWT.NONE;
			default:
				throw new IllegalArgumentException("Unknown index: " + index);
		}
	}
}