package com.kartoflane.superluminal2.ui.sidebar.data;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;

public class ShipDataComposite extends Composite implements DataComposite {

	private ShipController controller = null;

	public ShipDataComposite(Composite parent, ShipController controller) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(3, false));

		this.controller = controller;

		Label label = new Label(this, SWT.NONE);
		label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 3, 1));
		label.setText("Ship Origin");

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
	}

	public void updateData() {
	}

	@Override
	public void setController(AbstractController controller) {
		this.controller = (ShipController) controller;
	}
}