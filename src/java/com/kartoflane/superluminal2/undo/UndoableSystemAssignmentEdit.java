package com.kartoflane.superluminal2.undo;

import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.mvc.controllers.SystemController;


@SuppressWarnings("serial")
public class UndoableSystemAssignmentEdit extends ValueUndoableEdit<RoomController>
{
	private final SystemController data;


	/**
	 * Constructs a new UndoableSystemAssignmentEdit.
	 * 
	 * @param ac
	 *            the system whom this edit concerns
	 */
	public UndoableSystemAssignmentEdit( SystemController ac )
	{
		if ( ac == null )
			throw new IllegalArgumentException( "Argument must not be null." );

		data = ac;
	}

	@Override
	public String getPresentationName()
	{
		return ( cur == null ? "unassign " : "assign " ) + data.toString();
	}

	@Override
	public void doUndo()
	{
		if ( old == null )
			Manager.getCurrentShip().unassign( data.getGameObject() );
		else
			Manager.getCurrentShip().assign( data.getGameObject(), old );
		data.updateView();
	}

	@Override
	public void doRedo()
	{
		if ( cur == null )
			Manager.getCurrentShip().unassign( data.getGameObject() );
		else
			Manager.getCurrentShip().assign( data.getGameObject(), cur );
		data.updateView();
	}
}
