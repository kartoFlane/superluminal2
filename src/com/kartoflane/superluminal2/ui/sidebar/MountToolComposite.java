package com.kartoflane.superluminal2.ui.sidebar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kartoflane.superluminal2.components.Directions;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.tools.MountTool;

public class MountToolComposite extends Composite {

	private MountTool tool = null;

	public MountToolComposite(Composite parent) {
		super(parent, SWT.NONE);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		setLayout(new GridLayout(1, false));

		tool = (MountTool) Manager.getSelectedTool();

		Label label = new Label(this, SWT.NONE);
		label.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
		label.setText("Weapon Mount Creation Tool");

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		Composite settingsContainer = new Composite(this, SWT.NONE);
		settingsContainer.setLayout(new GridLayout(2, false));
		settingsContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		final Button btnRotated = new Button(settingsContainer, SWT.CHECK);
		btnRotated.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		btnRotated.setText("Rotated");
		btnRotated.setSelection(tool.isRotated());

		final Button btnMirrored = new Button(settingsContainer, SWT.CHECK);
		btnMirrored.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		btnMirrored.setText("Mirrored");
		btnMirrored.setSelection(tool.isMirrored());

		Label lblDirection = new Label(settingsContainer, SWT.NONE);
		lblDirection.setText("Direction:");

		final Combo directionCombo = new Combo(settingsContainer, SWT.READ_ONLY);
		GridData gd_directionCombo = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_directionCombo.widthHint = 80;
		directionCombo.setLayoutData(gd_directionCombo);
		directionCombo.add("Up");
		directionCombo.add("Left");
		directionCombo.add("Right");
		directionCombo.add("Down");
		directionCombo.add("None");
		directionCombo.select(directionToInt(tool.getDirection()));

		btnRotated.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tool.setRotated(btnRotated.getSelection());
			}
		});

		btnMirrored.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tool.setMirrored(btnMirrored.getSelection());
			}
		});

		directionCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tool.setDirection(intToDirection(directionCombo.getSelectionIndex()));
			}
		});

		pack();
	}

	private Directions intToDirection(int i) {
		switch (i) {
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
				return null;
		}
	}

	private int directionToInt(Directions dir) {
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
				return -1;
		}
	}
}
