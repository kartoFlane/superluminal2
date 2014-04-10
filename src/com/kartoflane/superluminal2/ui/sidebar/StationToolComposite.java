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

import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ftl.MountObject.Directions;
import com.kartoflane.superluminal2.tools.StationTool;

public class StationToolComposite extends Composite {

	private StationTool tool = null;

	public StationToolComposite(Composite parent) {
		super(parent, SWT.NONE);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		setLayout(new GridLayout(2, false));

		tool = (StationTool) Manager.getSelectedTool();

		Label lblRoomTool = new Label(this, SWT.NONE);
		lblRoomTool.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 2, 1));
		lblRoomTool.setText("Station Placement Tool");

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

		Button btnPlace = new Button(this, SWT.RADIO);
		btnPlace.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnPlace.setText("Placement");

		Button btnDirection = new Button(this, SWT.RADIO);
		btnDirection.setText("Direction");

		final Combo directionCombo = new Combo(this, SWT.READ_ONLY);
		GridData gd_directionCombo = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_directionCombo.heightHint = 17;
		gd_directionCombo.widthHint = 80;
		directionCombo.setLayoutData(gd_directionCombo);
		directionCombo.add("Up");
		directionCombo.add("Left");
		directionCombo.add("Right");
		directionCombo.add("Down");
		directionCombo.select(directionToInt(tool.getDirection()));

		Button btnRemove = new Button(this, SWT.RADIO);
		btnRemove.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnRemove.setText("Removal");

		directionCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tool.setDirection(intToDirection(directionCombo.getSelectionIndex()));
			}
		});
		btnPlace.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				directionCombo.setEnabled(false);
				tool.setStatePlacement();
			}
		});
		btnDirection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				directionCombo.setEnabled(true);
				tool.setStateDirection();
			}
		});
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				directionCombo.setEnabled(false);
				tool.setStateRemoval();
			}
		});

		btnPlace.setSelection(tool.isStatePlacement());
		btnDirection.setSelection(tool.isStateDirection());
		btnRemove.setSelection(tool.isStateRemoval());
		directionCombo.setEnabled(tool.isStateDirection());

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
