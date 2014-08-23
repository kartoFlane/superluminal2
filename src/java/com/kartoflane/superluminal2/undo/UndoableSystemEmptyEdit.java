package com.kartoflane.superluminal2.undo;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ftl.SystemObject;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.ui.ShipContainer;

@SuppressWarnings("serial")
public class UndoableSystemEmptyEdit extends AbstractUndoableEdit {

	private final RoomController data;
	private final ArrayList<SystemObject> systems;

	/**
	 * Constructs a new UndoableSystemEmptyEdit.
	 * 
	 * @param rc
	 *            the room from which the systems are removed
	 * @throws NoSystemsException
	 *             when the room has no systems assigned, and thus the edit is superfluous
	 */
	public UndoableSystemEmptyEdit(RoomController rc) throws NoSystemsException {
		if (rc == null)
			throw new IllegalArgumentException("Argument must not be null.");

		data = rc;
		systems = Manager.getCurrentShip().getAllAssignedSystems(rc.getGameObject());

		if (systems.isEmpty())
			throw new NoSystemsException();
	}

	@Override
	public String getPresentationName() {
		return String.format("unassign all from %s", data.getClass().getSimpleName());
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();

		ShipContainer container = Manager.getCurrentShip();
		for (SystemObject s : systems) {
			container.assign(s, data);
		}
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();

		ShipContainer container = Manager.getCurrentShip();
		for (SystemObject s : systems) {
			container.unassign(s);
		}
	}

	public class NoSystemsException extends Exception {
	}
}
