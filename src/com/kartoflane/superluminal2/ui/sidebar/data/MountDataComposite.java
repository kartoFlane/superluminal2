package com.kartoflane.superluminal2.ui.sidebar.data;

import java.util.ArrayList;

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

import com.kartoflane.superluminal2.components.Directions;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.core.Database.WeaponTypes;
import com.kartoflane.superluminal2.ftl.WeaponObject;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.MountController;
import com.kartoflane.superluminal2.ui.EditorWindow;

public class MountDataComposite extends Composite implements DataComposite {

	private Label label;
	private Button btnRotated;
	private Button btnMirrored;
	private Combo directionCombo;
	private Combo categoryCombo;
	private Combo weaponCombo;

	private ArrayList<WeaponObject> weaponList = null;

	private MountController controller = null;

	public MountDataComposite(Composite parent, MountController control) {
		super(parent, SWT.NONE);

		this.controller = control;

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
		categoryCombo.add("Burst");
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

		categoryCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selection = categoryCombo.getSelectionIndex();
				weaponCombo.setEnabled(selection != 0);
				weaponCombo.removeAll();
				WeaponTypes type = indexToType(selection);
				if (type != null)
					loadWeapons(type);
			}
		});

		weaponCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.setVisible(false);
				if (weaponCombo.getSelectionIndex() > 0) {
					WeaponObject weapon = weaponList.get(weaponCombo.getSelectionIndex() - 1);
					controller.setWeapon(weapon);
				} else {
					controller.setWeapon(Database.DEFAULT_WEAPON_OBJ);
				}
				controller.reposition(controller.getX(), controller.getY());
				controller.setVisible(true);
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

		directionCombo.select(directionToIndex(controller.getDirection()));

		WeaponObject weapon = controller.getWeapon();
		WeaponTypes type = weapon.getType();
		categoryCombo.select(typeToIndex(type));
		categoryCombo.notifyListeners(SWT.Selection, null);
		if (type != null) {
			int i = 0;
			for (WeaponObject o : weaponList) {
				i++;
				if (o == weapon)
					break;
			}
			weaponCombo.select(i);
		}
	}

	@Override
	public void setController(AbstractController controller) {
		this.controller = (MountController) controller;
	}

	private void loadWeapons(WeaponTypes type) {
		weaponList = Database.getInstance().getWeaponsByType(type);
		weaponCombo.add("No Weapon");
		weaponCombo.select(0);
		for (WeaponObject weapon : weaponList) {
			weaponCombo.add(String.format("%-30s [%s]", weapon.getTitle(), weapon.getBlueprintName()));
		}
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

	private int typeToIndex(WeaponTypes type) {
		if (type == null)
			return 0;
		switch (type) {
			case LASER:
				return 1;
			case BEAM:
				return 2;
			case MISSILES:
				return 3;
			case BOMB:
				return 4;
			case BURST:
				return 5;
			default:
				throw new IllegalArgumentException("Unknown type: " + type);
		}
	}

	private WeaponTypes indexToType(int index) {
		switch (index) {
			case 0: // No weapon
				return null;
			case 1: // Laser / Ion
				return WeaponTypes.LASER;
			case 2: // Beam
				return WeaponTypes.BEAM;
			case 3: // Missile
				return WeaponTypes.MISSILES;
			case 4: // Bomb
				return WeaponTypes.BOMB;
			case 5: // Burst
				return WeaponTypes.BURST;
			default:
				throw new IllegalArgumentException("Unknown index: " + index);
		}
	}
}