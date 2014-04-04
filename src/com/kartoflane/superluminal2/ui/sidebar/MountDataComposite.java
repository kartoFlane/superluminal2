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

import com.kartoflane.superluminal2.ftl.MountObject.Directions;
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

	public MountDataComposite(Composite parent, MountController control) {
		super(parent, SWT.NONE);

		this.controller = control;

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

		directionCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.setDirection(indexToDirection(directionCombo.getSelectionIndex()));
			}
		});

		updateData();
	}

	@Override
	public void updateData() {
		btnRotated.setSelection(controller.isRotated());
		btnMirrored.setSelection(controller.isMirrored());

		directionCombo.select(directionToIndex(controller.getDirection()));
	}

	@Override
	public void setController(AbstractController controller) {
		this.controller = (MountController) controller;
	}

	private int directionToIndex(Directions dir) {
		switch (dir) {
			case UP:
				return 0;
			case LEFT:
				return 1;
			case RIGHT:
				return 2;
			case DOWN:
				return 3;
			case NONE:
				return 4;
			default:
				throw new IllegalArgumentException("Unknown direction: " + dir);
		}
	}

	private Directions indexToDirection(int index) {
		switch (index) {
			case 0:
				return Directions.UP;
			case 1:
				return Directions.LEFT;
			case 2:
				return Directions.RIGHT;
			case 3:
				return Directions.DOWN;
			case 4:
				return Directions.NONE;
			default:
				throw new IllegalArgumentException("Unknown index: " + index);
		}
	}
}