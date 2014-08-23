package com.kartoflane.superluminal2.undo;

import com.kartoflane.superluminal2.components.interfaces.Indexable;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ui.OverviewWindow;
import com.kartoflane.superluminal2.utils.Utils;

@SuppressWarnings("serial")
public class UndoableOrderEdit extends ValueUndoableEdit<Integer> {

	private Indexable[] data = null;

	/**
	 * Constructs a new UndoableMoveEdit.
	 * 
	 * @param array
	 *            array of Indexable elements that has been reordered
	 */
	public UndoableOrderEdit(Indexable[] array) {
		if (array == null)
			throw new IllegalArgumentException("Argument must not be null.");

		data = array;
	}

	@Override
	public String getPresentationName() {
		return String.format("reorder %s", data[old].getClass().getSimpleName());
	}

	@Override
	public void doUndo() {
		if (old == null)
			throw new IllegalStateException("Old index is null!");

		Utils.reorder(data, cur, old);
		Manager.getCurrentShip().sort();
		OverviewWindow.staticUpdate();
	}

	@Override
	public void doRedo() {
		if (cur == null)
			throw new IllegalStateException("Current index is null!");

		Utils.reorder(data, old, cur);
		Manager.getCurrentShip().sort();
		OverviewWindow.staticUpdate();
	}
}
