package com.kartoflane.superluminal2.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;

@SuppressWarnings("serial")
public class UndoableDeleteEdit extends AbstractUndoableEdit {

	private int index;
	private AbstractController data = null;
	private boolean disposeOnDie = true;

	public UndoableDeleteEdit(AbstractController ac, int index) {
		if (ac == null)
			throw new IllegalArgumentException("Argument must not be null.");
		if (!ac.isDeletable())
			throw new IllegalArgumentException("The controller is not deletable.");

		data = ac;
		this.index = index;
	}

	@Override
	public String getPresentationName() {
		return String.format("delete %s", data.getClass().getSimpleName());
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		Manager.getCurrentShip().restore(data, index);
		disposeOnDie = false;
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		Manager.getCurrentShip().delete(data);
		disposeOnDie = true;
	}

	@Override
	public void die() {
		super.die();
		if (disposeOnDie)
			Manager.getCurrentShip().dispose(data);
	}
}
