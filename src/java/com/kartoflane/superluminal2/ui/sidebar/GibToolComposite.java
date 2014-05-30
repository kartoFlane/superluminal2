package com.kartoflane.superluminal2.ui.sidebar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;

public class GibToolComposite extends Composite implements DataComposite {
	private Composite dataContainer;

	public GibToolComposite(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(1, false));

		Label label = new Label(this, SWT.NONE);
		label.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
		label.setText("Gib Tool");

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		dataContainer = new Composite(this, SWT.BORDER);
		GridLayout gl_dataContainer = new GridLayout(1, false);
		gl_dataContainer.marginWidth = 0;
		gl_dataContainer.marginHeight = 0;
		dataContainer.setLayout(gl_dataContainer);
		dataContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblNYI = new Label(dataContainer, SWT.NONE);
		lblNYI.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblNYI.setText("(not yet implemented)");

	}

	public void updateData() {
	}

	public void setController(AbstractController c) {
	}

	@Override
	public void dispose() {
		super.dispose();
		Cache.checkInImage(this, "cpath:/assets/help.png");
	}
}
