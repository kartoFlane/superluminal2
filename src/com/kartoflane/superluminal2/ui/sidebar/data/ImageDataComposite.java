package com.kartoflane.superluminal2.ui.sidebar.data;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.ImageController;
import com.kartoflane.superluminal2.tools.ManipulationTool;

public class ImageDataComposite extends Composite implements DataComposite {

	private ManipulationTool tool = null;
	private ImageController controller = null;
	private Label label = null;

	public ImageDataComposite(Composite parent, ImageController control) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(3, false));

		tool = (ManipulationTool) Manager.getSelectedTool();
		controller = control;

		label = new Label(this, SWT.NONE);
		label.setAlignment(SWT.CENTER);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		String alias = control.getAlias();
		label.setText("Image" + (alias == null ? "" : " (" + alias + ")"));

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
	}

	@Override
	public void updateData() {
		String alias = controller.getAlias();
		label.setText("Image" + (alias == null ? "" : " (" + alias + ")"));
	}

	@Override
	public void setController(AbstractController controller) {
		this.controller = (ImageController) controller;
	}
}
