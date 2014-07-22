package com.kartoflane.superluminal2.undo;

import javax.swing.undo.CannotUndoException;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;

@SuppressWarnings("serial")
public class UndoableMoveEdit extends ValueUndoableEdit<Point> {

	private AbstractController data = null;
	private Point oldLoc;
	private Point curLoc;

	public UndoableMoveEdit(AbstractController ac) {
		if (ac == null)
			throw new IllegalArgumentException("Argument must not be null.");

		data = ac;
	}

	@Override
	public String getPresentationName() {
		return String.format("move %s", data.getClass().getSimpleName());
	}

	@Override
	public void undo() throws CannotUndoException {
		if (oldLoc == null)
			throw new IllegalStateException("Old location is null!");
		super.undo();

		// Select the controller to mimic authentic user interaction
		// Eg. ShipController.select() modifies its children's collidablity
		data.select();

		data.reposition(oldLoc);
		data.updateFollowOffset();
		data.updateView();

		// Don't deselect if it was actually selected by the user
		if (Manager.getSelected() != data)
			data.deselect();

		executeUndoCallback();
	}

	@Override
	public void redo() throws CannotUndoException {
		if (curLoc == null)
			throw new IllegalStateException("Current location is null!");
		super.redo();

		// Select the controller to mimic authentic user interaction
		// Eg. ShipController.select() modifies its children's collidablity
		data.select();

		data.reposition(curLoc);
		data.updateFollowOffset();
		data.updateView();

		// Don't deselect if it was actually selected by the user
		if (Manager.getSelected() != data)
			data.deselect();

		executeUndoCallback();
	}

	@Override
	public void setOld(Point old) {
		oldLoc = old;
	}

	@Override
	public Point getOld() {
		return oldLoc;
	}

	@Override
	public void setCurrent(Point cur) {
		curLoc = cur;
	}

	@Override
	public Point getCurrent() {
		return curLoc;
	}
}
