package com.kartoflane.superluminal2.ui.sidebar.data;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.GlowController;

public class GlowDataComposite extends Composite implements DataComposite {

	public GlowDataComposite(Composite parent, GlowController controller) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(3, false));

		Label label = new Label(this, SWT.NONE);
		label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 3, 1));
		label.setText("Manning Glow");

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
	}

	public void updateData() {
	}

	public void setController(AbstractController controller) {
	}

	public void reloadController() {
	}
}