package com.kartoflane.superluminal2.ui.sidebar.data;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.GlowController;
import com.kartoflane.superluminal2.utils.UIUtils;

public class GlowDataComposite extends Composite implements DataComposite {

	public GlowDataComposite(Composite parent, GlowController controller) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(2, false));

		Label label = new Label(this, SWT.NONE);
		label.setAlignment(SWT.CENTER);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		label.setText("Manning Glow");

		Image helpImage = Cache.checkOutImage(this, "cpath:/assets/help.png");
		Label lblHelp = new Label(this, SWT.NONE);
		lblHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblHelp.setImage(helpImage);
		String msg = "- You may want to use the View > Zoom Window while aligning glow images.";
		UIUtils.addTooltip(lblHelp, msg);

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		updateData();
	}

	public void updateData() {
	}

	public void setController(AbstractController controller) {
	}

	public void reloadController() {
	}
}
