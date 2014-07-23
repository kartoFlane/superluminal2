package com.kartoflane.superluminal2.undo;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.components.Tuple;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;

@SuppressWarnings("serial")
public class UndoableResizeEdit extends ValueUndoableEdit<Tuple<Point, Point>> {

	private AbstractController data = null;

	/**
	 * Constructs a new UndoableMoveEdit.
	 * 
	 * @param ac
	 *            the controller whom this edit concerns
	 */
	public UndoableResizeEdit(AbstractController ac) {
		if (ac == null)
			throw new IllegalArgumentException("Argument must not be null.");

		data = ac;
	}

	@Override
	public String getPresentationName() {
		return String.format("resize %s", data.getClass().getSimpleName());
	}

	@Override
	public void doUndo() {
		if (old == null)
			throw new IllegalStateException("Old size is null!");

		// Select the controller to mimic authentic user interaction
		// Eg. ShipController.select() modifies its children's collidablity
		data.select();

		data.resize(old.getValue());
		data.reposition(old.getKey());
		data.updateFollowOffset();
		data.updateView();

		// Don't deselect if it was actually selected by the user
		if (Manager.getSelected() != data)
			data.deselect();
	}

	@Override
	public void doRedo() {
		if (cur == null)
			throw new IllegalStateException("Current size is null!");

		// Select the controller to mimic authentic user interaction
		// Eg. ShipController.select() modifies its children's collidablity
		data.select();

		data.resize(cur.getValue());
		data.reposition(cur.getKey());
		data.updateFollowOffset();
		data.updateView();

		// Don't deselect if it was actually selected by the user
		if (Manager.getSelected() != data)
			data.deselect();
	}
}
