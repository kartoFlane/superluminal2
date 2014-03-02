package com.kartoflane.superluminal2.ui.sidebar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.CellController;
import com.kartoflane.superluminal2.mvc.controllers.DoorController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.tools.ManipulationTool;

public class DoorDataComposite extends Composite implements DataComposite {

	private ManipulationTool tool = null;
	/** Determines which door's link is set during linking */
	private int id = SWT.LEFT;

	private Button btnHorizontal;
	private Button btnIdLeft;
	private Button btnIdRight;

	private DoorController controller = null;

	public DoorDataComposite(Composite parent, DoorController controller) {
		super(parent, SWT.NONE);

		tool = (ManipulationTool) Manager.getSelectedTool();
		this.controller = controller;

		setLayout(new GridLayout(2, false));

		Label label = new Label(this, SWT.NONE);
		label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));
		label.setText("Door");

		Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		btnHorizontal = new Button(this, SWT.CHECK);
		btnHorizontal.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnHorizontal.setText("Horizontal");
		btnHorizontal.setSelection(controller.getModel().isHorizontal());

		Label lblIdLeft = new Label(this, SWT.NONE);
		lblIdLeft.setText("Left ID:  ");

		btnIdLeft = new Button(this, SWT.TOGGLE);
		btnIdLeft.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnIdLeft.setText("(linked automatically)");

		Label lblIdRight = new Label(this, SWT.NONE);
		lblIdRight.setText("Right ID:  ");

		btnIdRight = new Button(this, SWT.TOGGLE);
		btnIdRight.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnIdRight.setText("(linked automatically)");

		btnHorizontal.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DoorDataComposite.this.controller.setHorizontal(btnHorizontal.getSelection());
			}
		});

		btnIdLeft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button btn = (Button) e.getSource();

				if (btn.getSelection()) {
					btn.setText("(select a room)");
					btnIdRight.setEnabled(false);
					id = SWT.LEFT;
					// tool.setState(State.DOOR_LINK); // TODO
				} else {
					RoomController room = DoorDataComposite.this.controller.getLeftRoomController();
					btn.setText(room == null ? "(linked automatically)" : room.toString());
					btnIdRight.setEnabled(true);
					// tool.setState(State.MANIPULATE); // TODO
				}
			}
		});

		btnIdRight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button btn = (Button) e.getSource();

				if (btn.getSelection()) {
					btn.setText("(select a room)");
					btnIdLeft.setEnabled(false);
					id = SWT.RIGHT;
					// tool.setState(State.DOOR_LINK);// TODO
				} else {
					RoomController room = DoorDataComposite.this.controller.getRightRoomController();
					btn.setText(room == null ? "(linked automatically)" : room.toString());
					btnIdLeft.setEnabled(true);
					// tool.setState(State.MANIPULATE);//TODO
				}
			}
		});

		updateData();
	}

	@Override
	public void updateData() {
		Point gridSize = Grid.getInstance().getSize();
		btnHorizontal.setEnabled(controller.getX() < gridSize.x - CellController.SIZE && controller.getY() < gridSize.y - CellController.SIZE);

		RoomController linkedRoom = controller.getLeftRoomController();
		btnIdLeft.setText(linkedRoom == null ? "(linked automatically)" : linkedRoom.toString());
		linkedRoom = controller.getRightRoomController();
		btnIdRight.setText(linkedRoom == null ? "(linked automatically)" : linkedRoom.toString());
	}

	public void linkDoor(RoomController room) {
		switch (id) {
			case SWT.LEFT:
				controller.setLeftRoomController(room);
				btnIdLeft.setSelection(false);
				btnIdRight.setEnabled(true);
				updateData();
				break;
			case SWT.RIGHT:
				controller.setRightRoomController(room);
				btnIdLeft.setEnabled(true);
				btnIdRight.setSelection(false);
				updateData();
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	@Override
	public DoorController getController() {
		return controller;
	}

	@Override
	public void setController(AbstractController controller) {
		this.controller = (DoorController) controller;
	}
}