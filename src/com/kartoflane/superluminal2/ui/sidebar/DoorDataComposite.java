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
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.DoorController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.tools.ManipulationTool;
import com.kartoflane.superluminal2.tools.ManipulationTool.States;
import com.kartoflane.superluminal2.ui.OverviewWindow;

public class DoorDataComposite extends Composite implements DataComposite {

	private static final String AUTOLINK_TXT = "Linked automatically";
	private static final String SELECT_ROOM_TXT = "Select a room";

	private ManipulationTool tool = null;
	/** Determines which door's link is being set during linking */
	private boolean leftLinking = true;

	private Button btnHorizontal;
	private Button btnIdLeft;
	private Button btnIdRight;

	private DoorController controller = null;
	private Button btnSelectLeft;
	private Button btnSelectRight;
	private Label lblIdLeft;
	private Label lblIdRight;

	public DoorDataComposite(Composite parent, DoorController control) {
		super(parent, SWT.NONE);

		tool = (ManipulationTool) Manager.getSelectedTool();
		controller = control;

		setLayout(new GridLayout(3, false));

		Label label = new Label(this, SWT.NONE);
		label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 3, 1));
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
		btnIdLeft.setText(AUTOLINK_TXT);

		btnSelectLeft = new Button(this, SWT.NONE);
		GridData gd_btnSelectLeft = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnSelectLeft.widthHint = 30;
		btnSelectLeft.setLayoutData(gd_btnSelectLeft);
		btnSelectLeft.setText(">");
		btnSelectLeft.setToolTipText("Select the linked room");

		lblIdRight = new Label(this, SWT.NONE);
		lblIdRight.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblIdRight.setText("XXXXX ID:");

		btnIdRight = new Button(this, SWT.TOGGLE);
		btnIdRight.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnIdRight.setText(AUTOLINK_TXT);

		btnSelectRight = new Button(this, SWT.NONE);
		GridData gd_btnSelectRight = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnSelectRight.widthHint = 30;
		btnSelectRight.setLayoutData(gd_btnSelectRight);
		btnSelectRight.setText(">");
		btnSelectRight.setToolTipText("Select the linked room");

		btnHorizontal.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				controller.setHorizontal(btnHorizontal.getSelection());
				OverviewWindow.getInstance().update(controller);
				updateData();
			}
		});

		btnIdLeft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button btn = (Button) e.getSource();

				if (btn.getSelection()) {
					btn.setText(SELECT_ROOM_TXT);
					btnIdRight.setEnabled(false);
					leftLinking = true;
					tool.setState(States.DOOR_LINK);
				} else {
					RoomController room = controller.getLeftRoomController();
					btn.setText(room == null ? AUTOLINK_TXT : room.toString());
					btnIdRight.setEnabled(true);
					tool.setState(States.NORMAL);
				}
			}
		});

		btnIdRight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button btn = (Button) e.getSource();

				if (btn.getSelection()) {
					btn.setText(SELECT_ROOM_TXT);
					btnIdLeft.setEnabled(false);
					leftLinking = false;
					tool.setState(States.DOOR_LINK);
				} else {
					RoomController room = controller.getRightRoomController();
					btn.setText(room == null ? AUTOLINK_TXT : room.toString());
					btnIdLeft.setEnabled(true);
					tool.setState(States.NORMAL);
				}
			}
		});

		btnSelectLeft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Manager.setSelected(controller.getLeftRoomController());
			}
		});

		btnSelectRight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Manager.setSelected(controller.getRightRoomController());
			}
		});

		// updateData();
	}

	@Override
	public void updateData() {
		controller.verifyLinkedDoors();

		btnHorizontal.setSelection(controller.isHorizontal());

		lblIdLeft.setText((controller.isHorizontal() ? "Upper" : "Left") + " ID:");
		lblIdRight.setText((controller.isHorizontal() ? "Lower" : "Right") + " ID:");

		RoomController linkedRoom = controller.getLeftRoomController();
		btnIdLeft.setText(linkedRoom == null ? AUTOLINK_TXT : linkedRoom.toString());
		btnSelectLeft.setEnabled(linkedRoom != null);
		linkedRoom = controller.getRightRoomController();
		btnIdRight.setText(linkedRoom == null ? AUTOLINK_TXT : linkedRoom.toString());
		btnSelectRight.setEnabled(linkedRoom != null);

		lblIdLeft.pack();
		lblIdRight.pack();
	}

	public void linkDoor(RoomController room) {
		if (leftLinking) {
			controller.setLeftRoomController(room);
			btnIdLeft.setSelection(false);
			btnIdRight.setEnabled(true);
			updateData();
		} else {
			controller.setRightRoomController(room);
			btnIdLeft.setEnabled(true);
			btnIdRight.setSelection(false);
			updateData();
		}
	}

	@Override
	public void setController(AbstractController controller) {
		this.controller = (DoorController) controller;
	}
}