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
import com.kartoflane.superluminal2.tools.MountTool;
import com.kartoflane.superluminal2.ui.DirectionCombo;

public class MountToolComposite extends Composite {

	private MountTool tool = null;

	public MountToolComposite(Composite parent) {
		super(parent, SWT.NONE);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		setLayout(new GridLayout(2, false));

		tool = (MountTool) Manager.getSelectedTool();

		Label label = new Label(this, SWT.NONE);
		label.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 2, 1));
		label.setText("Weapon Mount Creation Tool");

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

		final Button btnRotated = new Button(this, SWT.CHECK);
		btnRotated.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		btnRotated.setText("Rotated");
		btnRotated.setSelection(tool.isRotated());

		btnRotated.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tool.setRotated(btnRotated.getSelection());
			}
		});

		final Button btnMirrored = new Button(this, SWT.CHECK);
		btnMirrored.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		btnMirrored.setText("Mirrored");
		btnMirrored.setSelection(tool.isMirrored());

		btnMirrored.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tool.setMirrored(btnMirrored.getSelection());
			}
		});

		Label lblDirection = new Label(this, SWT.NONE);
		lblDirection.setText("Direction:");

		final DirectionCombo cmbDirection = new DirectionCombo(this, SWT.READ_ONLY, true);
		GridData gd_cmbDirection = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_cmbDirection.widthHint = 80;
		cmbDirection.setLayoutData(gd_cmbDirection);
		cmbDirection.select(DirectionCombo.toIndex(tool.getDirection()));

		cmbDirection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tool.setDirection(cmbDirection.getDirection());
			}
		});

		pack();
	}
}
