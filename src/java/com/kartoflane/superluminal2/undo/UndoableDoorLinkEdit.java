package com.kartoflane.superluminal2.undo;

import com.kartoflane.superluminal2.ftl.RoomObject;
import com.kartoflane.superluminal2.mvc.controllers.DoorController;
import com.kartoflane.superluminal2.ui.EditorWindow;

@SuppressWarnings("serial")
public class UndoableDoorLinkEdit extends ValueUndoableEdit<RoomObject> {

	private final DoorController data;
	private final boolean left;

	public UndoableDoorLinkEdit(DoorController dc, boolean left) {
		if (dc == null)
			throw new IllegalArgumentException("Argument must not be null.");

		data = dc;
		this.left = left;
	}

	@Override
	public String getPresentationName() {
		return String.format("link %s", data.getClass().getSimpleName());
	}

	@Override
	public void doUndo() {
		if (left)
			data.setLeftRoom(old);
		else
			data.setRightRoom(old);
		EditorWindow.getInstance().updateSidebarContent();
	}

	@Override
	public void doRedo() {
		if (left)
			data.setLeftRoom(cur);
		else
			data.setRightRoom(cur);
		EditorWindow.getInstance().updateSidebarContent();
	}
}
