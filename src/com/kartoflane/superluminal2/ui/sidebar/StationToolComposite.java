package com.kartoflane.superluminal2.ui.sidebar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.tools.StationTool;
import com.kartoflane.superluminal2.ui.DirectionCombo;

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

		final DirectionCombo cmbDirection = new DirectionCombo(this, SWT.READ_ONLY, false);
		GridData gd_cmbDirection = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_cmbDirection.widthHint = 80;
		cmbDirection.setLayoutData(gd_cmbDirection);
		cmbDirection.select(DirectionCombo.toIndex(tool.getDirection()));

		Button btnRemove = new Button(this, SWT.RADIO);
		btnRemove.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnRemove.setText("Removal");

		cmbDirection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tool.setDirection(cmbDirection.getDirection());
			}
		});
		btnPlace.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmbDirection.setEnabled(false);
				tool.setStatePlacement();
			}
		});
		btnDirection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmbDirection.setEnabled(true);
				tool.setStateDirection();
			}
		});
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmbDirection.setEnabled(false);
				tool.setStateRemoval();
			}
		});

		btnPlace.setSelection(tool.isStatePlacement());
		btnDirection.setSelection(tool.isStateDirection());
		btnRemove.setSelection(tool.isStateRemoval());
		cmbDirection.setEnabled(tool.isStateDirection());

		pack();
	}
}
