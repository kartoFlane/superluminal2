package com.kartoflane.superluminal2.ui.sidebar.data;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ftl.RoomObject;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.DoorController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.tools.ManipulationTool;
import com.kartoflane.superluminal2.ui.OverviewWindow;
import com.kartoflane.superluminal2.utils.UIUtils;

public class DoorDataComposite extends Composite implements DataComposite {

	private static final String autolinkText = "Linked automatically";
	private static final String selectRoomText = "Select a room";

	private ManipulationTool tool = null;

	private Button btnHorizontal;
	private Button btnIdLeft;
	private Button btnIdRight;

	private DoorController controller = null;
	private Button btnSelectLeft;
	private Button btnSelectRight;
	private Label lblIdLeft;
	private Label lblIdRight;
	private Label label;

	public DoorDataComposite(Composite parent, DoorController control) {
		super(parent, SWT.NONE);

		tool = (ManipulationTool) Manager.getSelectedTool();
		controller = control;

		setLayout(new GridLayout(3, false));

		label = new Label(this, SWT.NONE);
		label.setAlignment(SWT.CENTER);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		label.setText("Door");

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		btnHorizontal = new Button(this, SWT.CHECK);
		btnHorizontal.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		btnHorizontal.setText("Horizontal");
		btnHorizontal.setSelection(controller.isHorizontal());

		lblIdLeft = new Label(this, SWT.NONE);
		lblIdLeft.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblIdLeft.setText("XXXXX ID:");

		btnIdLeft = new Button(this, SWT.TOGGLE);
		btnIdLeft.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnIdLeft.setText(autolinkText);

		btnSelectLeft = new Button(this, SWT.NONE);
		GridData gd_btnSelectLeft = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnSelectLeft.widthHint = 30;
		btnSelectLeft.setLayoutData(gd_btnSelectLeft);
		btnSelectLeft.setText(">");
		UIUtils.addTooltip(btnSelectLeft, "", "Select the linked room");

		lblIdRight = new Label(this, SWT.NONE);
		lblIdRight.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblIdRight.setText("XXXXX ID:");

		btnIdRight = new Button(this, SWT.TOGGLE);
		btnIdRight.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnIdRight.setText(autolinkText);

		btnSelectRight = new Button(this, SWT.NONE);
		GridData gd_btnSelectRight = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnSelectRight.widthHint = 30;
		btnSelectRight.setLayoutData(gd_btnSelectRight);
		btnSelectRight.setText(">");
		UIUtils.addTooltip(btnSelectRight, "", "Select the linked room");

		btnHorizontal.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.setHorizontal(btnHorizontal.getSelection());
				OverviewWindow.staticUpdate(controller);
				updateData();
			}
		});

		btnIdLeft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button btn = (Button) e.getSource();

				if (btn.getSelection()) {
					btn.setText(selectRoomText);
					btnIdRight.setEnabled(false);
					tool.setStateDoorLinkLeft();
				} else {
					RoomObject room = controller.getLeftRoom();
					btn.setText(room == null ? autolinkText : room.toString());
					btnIdRight.setEnabled(true);
					tool.setStateManipulate();
				}
			}
		});

		btnIdRight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button btn = (Button) e.getSource();

				if (btn.getSelection()) {
					btn.setText(selectRoomText);
					btnIdLeft.setEnabled(false);
					tool.setStateDoorLinkRight();
				} else {
					RoomObject room = controller.getRightRoom();
					btn.setText(room == null ? autolinkText : room.toString());
					btnIdLeft.setEnabled(true);
					tool.setStateManipulate();
				}
			}
		});

		btnSelectLeft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RoomController rc = (RoomController) Manager.getCurrentShip().getController(controller.getLeftRoom());
				Manager.setSelected(rc);
			}
		});

		btnSelectRight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RoomController rc = (RoomController) Manager.getCurrentShip().getController(controller.getRightRoom());
				Manager.setSelected(rc);
			}
		});
	}

	@Override
	public void updateData() {
		RoomObject linkedRoom = null;
		String alias = controller.getAlias();
		label.setText("Door" + (alias == null || alias.trim().equals("") ? "" : " (" + alias + ")"));

		btnHorizontal.setSelection(controller.isHorizontal());

		lblIdLeft.setText((controller.isHorizontal() ? "Upper" : "Left") + " ID:");
		lblIdRight.setText((controller.isHorizontal() ? "Lower" : "Right") + " ID:");

		linkedRoom = controller.getLeftRoom();
		btnIdLeft.setText(linkedRoom == null ? autolinkText : linkedRoom.toString());
		btnIdLeft.setEnabled(true);
		btnIdLeft.setSelection(false);
		btnSelectLeft.setEnabled(linkedRoom != null);

		linkedRoom = controller.getRightRoom();
		btnIdRight.setText(linkedRoom == null ? autolinkText : linkedRoom.toString());
		btnIdRight.setEnabled(true);
		btnIdRight.setSelection(false);
		btnSelectRight.setEnabled(linkedRoom != null);

		lblIdLeft.pack();
		lblIdRight.pack();
	}

	@Override
	public void setController(AbstractController controller) {
		this.controller = (DoorController) controller;
	}

	public void reloadController() {
	}
}