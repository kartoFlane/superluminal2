package com.kartoflane.superluminal2.ui.sidebar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.utils.UIUtils;

public class RoomToolComposite extends Composite {

	public RoomToolComposite(Composite parent) {
		super(parent, SWT.NONE);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		setLayout(new GridLayout(2, false));

		Image helpImage = Cache.checkOutImage(this, "cpath:/assets/help.png");

		Label lblRoomTool = new Label(this, SWT.NONE);
		lblRoomTool.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
		lblRoomTool.setText("Room Creation Tool");

		Label lblHelp = new Label(this, SWT.NONE);
		lblHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblHelp.setImage(helpImage);
		String msg = "- Left-click and drag to start drawing the room.\n" +
				"- Release left mouse button to confirm and place the room.\n" +
				"- Right-click to cancel.\n" +
				"- Rooms cannot be placed on top of each other, or beyond\n" +
				"  the ship's origin.";
		UIUtils.addTooltip(lblHelp, msg);

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
