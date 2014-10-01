package com.kartoflane.superluminal2.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.kartoflane.superluminal2.components.interfaces.Action;

/**
 * A base class for undoable operations that change a single value.
 * Subclasses should override {@link #doUndo()} and {@link #doRedo()} in order
 * to implement the undo/redo handling.<br>
 * <br>
 * After instantiation, use {@link #setOld(Object)} and {@link #setCurrent(Object)} to set up the values
 * for before and after the operation, respectively. <br>
 * <br>
 * Optional callbacks can be added using {@link #setUndoCallback(Action)} and {@link #setRedoCallback(Action)}<br>
 * to have the undo manager perform additional tasks once the undo/redo operation itself is completed.
 * 
 * @author kartoFlane
 *
 * @param <T>
 *            type of the value that this edit handles
 */
@SuppressWarnings("serial")
public abstract class ValueUndoableEdit<T> extends AbstractUndoableEdit {

	private Action undoCallback = null;
	private Action redoCallback = null;

	protected T old;
	protected T cur;

	/**
	 * Sets the old value for this undoable edit. The {@link #undo()} method resets to this value.
	 */
	public void setOld(T old) {
		this.old = old;
	}

	/**
	 * @return the old value for this undoable edit. The {@link #undo()} method resets to this value.
	 */
	public T getOld() {
		return old;
	}

	/**
	 * Sets the current value for this undoable edit. The {@link #redo()} method resets to this value.
	 */
	public void setCurrent(T cur) {
		this.cur = cur;
	}

	/**
	 * @return the current value for this undoable edit. The {@link #redo()} method resets to this value.
	 */
	public T getCurrent() {
		return cur;
	}

	/**
	 * Override this to undo the operation represented by this edit.<br>
	 * This method is called by {@link #undo()}.
	 */
	public void doUndo() {
	}

	/**
	 * Override this to redo the operation represented by this edit.<br>
	 * This method is called by {@link #redo()}.
	 */
	public void doRedo() {
	}

	/**
	 * Executes {@link AbstractUndoableEdit#undo() super.undo()}, then the actions defined by the subclass,
	 * and then the callback actions, if set.<br>
	 * <br>
	 * Override {@link #doUndo()} to implement the edit behaviour that would normally go here.
	 */
	@Override
	public final void undo() throws CannotUndoException {
		super.undo();

		doUndo();

		if (undoCallback != null)
			undoCallback.execute();
	}

	/**
	 * Executes {@link AbstractUndoableEdit#redo() super.redo()}, then the actions defined by the subclass,
	 * and then the callback actions, if set.<br>
	 * <br>
	 * Override {@link #doRedo()} to implement the edit behaviour that would normally go here.
	 */
	@Override
	public final void redo() throws CannotRedoException {
		super.redo();

		doRedo();

		if (redoCallback != null)
			redoCallback.execute();
	}

	/**
	 * Adds an action that will be executed after {@link #doUndo()}.<br>
	 * Can be null to remove the callback.
	 */
	public void setUndoCallback(Action a) {
		undoCallback = a;
	}

	/**
	 * Adds an action that will be executed after {@link #doRedo()}.<br>
	 * Can be null to remove the callback.
	 */
	public void setRedoCallback(Action a) {
		redoCallback = a;
	}

	public boolean isValuesEqual() {
		return old == null ? cur == null : old.equals(cur);
	}
}
