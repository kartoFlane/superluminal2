package com.kartoflane.superluminal2.undo;

import com.kartoflane.superluminal2.ftl.GibObject;
import com.kartoflane.superluminal2.mvc.controllers.MountController;
import com.kartoflane.superluminal2.ui.EditorWindow;

@SuppressWarnings("serial")
public class UndoableGibLinkEdit extends ValueUndoableEdit<GibObject> {

	private MountController data = null;

	public UndoableGibLinkEdit(MountController mc) {
		if (mc == null)
			throw new IllegalArgumentException("Argument must not be null.");

		data = mc;
	}

	@Override
	public String getPresentationName() {
		return String.format("link %s", data.getClass().getSimpleName());
	}

	@Override
	public void doUndo() {
		data.setGib(old);
		EditorWindow.getInstance().updateSidebarContent();
	}

	@Override
	public void doRedo() {
		data.setGib(cur);
		EditorWindow.getInstance().updateSidebarContent();
	}
}
