package com.kartoflane.superluminal2.undo;

import javax.swing.undo.AbstractUndoableEdit;

import com.kartoflane.superluminal2.components.interfaces.Action;

@SuppressWarnings("serial")
public abstract class ValueUndoableEdit<T> extends AbstractUndoableEdit {

	private Action undoCallback = null;
	private Action redoCallback = null;

	public abstract void setOld(T old);

	public abstract T getOld();

	public abstract void setCurrent(T cur);

	public abstract T getCurrent();

	/**
	 * Adds an action that can be executed after the undo() method is completed.
	 */
	public void setUndoCallback(Action a) {
		undoCallback = a;
	}

	protected void executeUndoCallback() {
		if (undoCallback != null)
			undoCallback.execute();
	}

	/**
	 * Adds an action that can be executed after the redo() method is completed.
	 */
	public void setRedoCallback(Action a) {
		redoCallback = a;
	}

	protected void executeRedoCallback() {
		if (redoCallback != null)
			redoCallback.execute();
	}
}
