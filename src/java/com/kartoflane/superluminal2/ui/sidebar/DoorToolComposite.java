package com.kartoflane.superluminal2.ui.sidebar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.utils.UIUtils;

public class DoorToolComposite extends Composite {

	public DoorToolComposite(Composite parent) {
		super(parent, SWT.NONE);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		setLayout(new GridLayout(2, false));

		Image helpImage = Cache.checkOutImage(this, "cpath:/assets/help.png");

		Label lblRoomTool = new Label(this, SWT.NONE);
		lblRoomTool.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
		lblRoomTool.setText("Door Creation Tool");

		Label lblHelp = new Label(this, SWT.NONE);
		lblHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblHelp.setImage(helpImage);
		String msg = "- Left-clicking places the door.\n" +
				"- Doors can only be placed on room walls.";
		UIUtils.addTooltip(lblHelp, "", msg);

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

		pack();
	}

	@Override
	public void dispose() {
		super.dispose();
		Cache.checkInImage(this, "cpath:/assets/help.png");
	}
}
