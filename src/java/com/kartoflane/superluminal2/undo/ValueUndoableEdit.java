package com.kartoflane.superluminal2.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.kartoflane.superluminal2.components.interfaces.Action;

@SuppressWarnings("serial")
public class ValueUndoableEdit<T> extends AbstractUndoableEdit {

	private Action undoCallback = null;
	private Action redoCallback = null;

	protected T old;
	protected T cur;

	public void setOld(T old) {
		this.old = old;
	}

	public T getOld() {
		return old;
	}

	public void setCurrent(T cur) {
		this.cur = cur;
	}

	public T getCurrent() {
		return cur;
	}

	public void doUndo() throws CannotUndoException {
	}

	public void doRedo() throws CannotRedoException {
	}

	@Override
	public final void undo() throws CannotUndoException {
		super.undo();

		doUndo();

		if (undoCallback != null)
			undoCallback.execute();
	}

	@Override
	public final void redo() throws CannotRedoException {
		super.undo();

		doRedo();

		if (redoCallback != null)
			redoCallback.execute();
	}

	/**
	 * Adds an action that can be executed after the undo() method is completed.
	 */
	public void setUndoCallback(Action a) {
		undoCallback = a;
	}

	/**
	 * Adds an action that can be executed after the redo() method is completed.
	 */
	public void setRedoCallback(Action a) {
		redoCallback = a;
	}
}
