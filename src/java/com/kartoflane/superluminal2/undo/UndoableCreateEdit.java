package com.kartoflane.superluminal2.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;

@SuppressWarnings("serial")
public class UndoableCreateEdit extends AbstractUndoableEdit {

	private AbstractController data = null;
	private boolean disposeOnDie = false;

	public UndoableCreateEdit(AbstractController ac) {
		if (ac == null)
			throw new IllegalArgumentException("Argument must not be null.");
		if (!ac.isDeletable())
			throw new IllegalArgumentException("The controller is not deletable.");

		data = ac;
	}

	@Override
	public String getPresentationName() {
		return String.format("create %s", data.getClass().getSimpleName());
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		// Don't call ShipContainer.delete() so's to prevent each undo of this edit from creating new edits.
		Manager.getCurrentShip().deleteNonUndoable(data);
		disposeOnDie = true;
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		Manager.getCurrentShip().restore(data);
		disposeOnDie = false;
	}

	@Override
	public void die() {
		super.die();
		if (disposeOnDie)
			Manager.getCurrentShip().dispose(data);
	}
}
