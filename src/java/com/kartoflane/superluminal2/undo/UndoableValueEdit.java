package com.kartoflane.superluminal2.undo;

@SuppressWarnings("serial")
public abstract class UndoableValueEdit<T> extends ValueUndoableEdit<T> {

	protected final Object data;

	/**
	 * Constructs a new UndoableStringEdit.<br>
	 * <br>
	 * This edit's {@link #setOld(String) old} and {@link #setCurrent(String) current} values
	 * are ...
	 * 
	 * @param ac
	 *            the controller whom this edit concerns
	 */
	public UndoableValueEdit(Object ac) {
		if (ac == null)
			throw new IllegalArgumentException("Argument must not be null.");

		data = ac;
	}

	public void doUndo() {
		callback(getOld());
	}

	public void doRedo() {
		callback(getCurrent());
	}

	public abstract void callback(T arg);
}
