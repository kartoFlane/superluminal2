package com.kartoflane.superluminal2.undo;

/**
 * A base class for generic undoable edits. Subclasses have to implement the {@link #callback(Object)} method
 * in order to specify the operations that need to be executed in order to undo or redo the operation.<br>
 * <br>
 * The object passed in constructor can be accessed via {@link #getData()} (or {@link #data} field),
 * and cast to appropriate class in the callback method's body.<br>
 * <br>
 * {@link #doUndo()} and {@link #doRedo()} need not be overridden, for they execute the callback with the
 * appropriate argument.
 * 
 * @author kartoFlane
 *
 * @param <T>
 *            type of the value that this edit handles
 */
@SuppressWarnings("serial")
public abstract class UndoablePropertyEdit<T> extends ValueUndoableEdit<T>
{
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
	public UndoablePropertyEdit( Object ac )
	{
		if ( ac == null )
			throw new IllegalArgumentException( "Argument must not be null." );

		data = ac;
	}

	public Object getData()
	{
		return data;
	}

	public void doUndo()
	{
		callback( getOld() );
	}

	public void doRedo()
	{
		callback( getCurrent() );
	}

	public abstract void callback( T arg );
}
