package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.CursorController;
import com.kartoflane.superluminal2.mvc.controllers.DoorController;
import com.kartoflane.superluminal2.mvc.controllers.MountController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.tools.DoorTool;
import com.kartoflane.superluminal2.tools.MountTool;
import com.kartoflane.superluminal2.tools.RoomTool;
import com.kartoflane.superluminal2.tools.Tool;
import com.kartoflane.superluminal2.tools.Tool.Tools;
import com.kartoflane.superluminal2.ui.EditorWindow;

public class CursorView extends BaseView {

	public CursorView() {
		super();
	}

	@Override
	public void paintControl(PaintEvent e) {
		if (alpha > 0) {
			paintBackground(e, backgroundColor, alpha);
			paintImage(e, image, alpha);
			paintBorder(e, borderColor, borderThickness, alpha);
		}
	}

	private CursorController getController() {
		return (CursorController) controller;
	}

	@Override
	public void setController(Controller controller) {
		this.controller = (AbstractController) controller;
	}

	@Override
	public void updateView() {
		Tools toolId = Manager.getSelectedToolId();
		if (toolId == Tools.POINTER)
			handlePointerToolAppearance();
		else if (toolId == Tools.ROOM)
			handleRoomToolAppearance();
		else if (toolId == Tools.DOOR)
			handleDoorToolAppearance();
		else if (toolId == Tools.WEAPON)
			handleMountToolAppearance();
		else
			; // TODO
	}

	private void handlePointerToolAppearance() {
		AbstractController selectedController = Manager.getSelected();

		if (selectedController instanceof RoomController)
			handleRoomAppearance((RoomController) selectedController);
		else if (selectedController instanceof DoorController)
			handleDoorAppearance((DoorController) selectedController);
		else if (selectedController instanceof MountController)
			handleMountAppearance((MountController) selectedController);
		// TODO else if ...
		else
			handleDefaultPointerAppearance();
	}

	private void handleRoomToolAppearance() {
		RoomTool tool = (RoomTool) Manager.getSelectedTool();

		setFlippedX(false);
		setFlippedY(false);
		setRotation(0);
		setImage(null);

		if (tool.isCreating()) {
			setBackgroundColor(tool.canCreate() ? ALLOW_RGB : DENY_RGB);
			setBorderColor(null);
			setAlpha(255 / 3);
		} else {
			setBackgroundColor(null);
			setBorderColor(tool.canCreate() ? ALLOW_RGB : DENY_RGB);
			setAlpha(255);
			setBorderThickness(3);
		}
	}

	private void handleDoorToolAppearance() {
		DoorTool tool = (DoorTool) Manager.getSelectedTool();

		setFlippedX(false);
		setFlippedY(false);
		setRotation(0);
		setImage(null);
		setBackgroundColor(null);
		setBorderColor(tool.canPlace() ? ALLOW_RGB : DENY_RGB);
		setVisible(!Manager.leftMouseDown);
	}

	private void handleMountToolAppearance() {
		MountTool tool = (MountTool) Manager.getSelectedTool();

		String path = getImagePath();
		if (path == null || !path.equals(MountView.DEFAULT_MOUNT_IMG)) {
			setImage(MountView.DEFAULT_MOUNT_IMG);
		}
		setBorderColor(tool.canCreate() ? ALLOW_RGB : DENY_RGB);
		setBackgroundColor(null);
		setAlpha(255);
		setBorderThickness(2);
		setRotation(tool.isRotated() ? 90 : 0);
		setFlippedX(tool.isMirrored());
	}

	private void handleDoorAppearance(DoorController door) {
		setFlippedX(false);
		setFlippedY(false);
		setRotation(0);
		setImage(null);

		if (door.isHighlighted()) {
			setVisible(false);
			// handled by the object
		} else if (door.isSelected()) {
			handleDefaultPointerAppearance();
		}
	}

	private void handleMountAppearance(MountController mount) {
		setFlippedX(false);
		setFlippedY(false);
		setRotation(0);
		setImage(null);

		if (mount.isHighlighted()) {
			setVisible(false);
			// handled by the object
		} else if (mount.isSelected()) {
			handleDefaultPointerAppearance();
		}
	}

	private void handleRoomAppearance(RoomController room) {
		setFlippedX(false);
		setFlippedY(false);
		setRotation(0);
		setImage(null);

		if (room.isResizing()) {
			setVisible(true);
			setBorderColor(null);
			setBackgroundColor(room.canResize() ? HIGHLIGHT_RGB : DENY_RGB);
			setAlpha(255 / 3);
		} else if (room.isHighlighted()) {
			setVisible(false);
			// handled by the object
		} else if (room.isSelected()) {
			handleDefaultPointerAppearance();
		}
	}

	private void handleDefaultPointerAppearance() {
		controller.setSnapMode(Snapmodes.CELL);

		setFlippedX(false);
		setFlippedY(false);
		setRotation(0);
		setImage(null);
		setBackgroundColor(null);
		setBorderColor(HIGHLIGHT_RGB);
		setAlpha(255);
		setBorderThickness(3);

		Point m = getController().getMouseLocation();
		Layers[] selectableLayerIds = Tool.getSelectableLayerIds();
		AbstractController anyC = null;
		for (int i = selectableLayerIds.length - 1; i >= 0 && anyC == null; i--) {
			if (selectableLayerIds[i] != null)
				anyC = EditorWindow.getInstance().getPainter().getSelectableControllerAt(m.x, m.y, selectableLayerIds[i]);
		}
		setVisible(!Manager.leftMouseDown && anyC == null && Grid.getInstance().isLocAccessible(m.x, m.y));
	}
}
