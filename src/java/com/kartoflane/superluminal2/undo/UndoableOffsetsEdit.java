package com.kartoflane.superluminal2.undo;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.components.Tuple;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;
import com.kartoflane.superluminal2.ui.ShipContainer;

/**
 * An undoable edit that represents modification of the ship's offsets (both fine and thick). <br>
 * <br>
 * This edit's old and current values are {@link Tuple} pairs of {@link Point}s, the first point in
 * the pair representing the thick offset, and the second point -- fine offset.
 * 
 * @author kartoFlane
 *
 */
@SuppressWarnings("serial")
public class UndoableOffsetsEdit extends ValueUndoableEdit<Tuple<Point, Point>> {

	private final ShipContainer data;

	/**
	 * Constructs a new UndoableOffsetsEdit.
	 * 
	 * @param ac
	 *            the controller whom this edit concerns
	 */
	public UndoableOffsetsEdit(ShipContainer container) {
		if (container == null)
			throw new IllegalArgumentException("Argument must not be null.");

		data = container;
	}

	@Override
	public String getPresentationName() {
		return "modify offset";
	}

	@Override
	public void doUndo() {
		if (old == null)
			throw new IllegalStateException("Old offsets tuple is null!");

		Point thick = old.getKey();
		Point fine = old.getValue();

		ShipController shipC = data.getShipController();
		shipC.select();

		data.setShipOffset(thick.x, thick.y);
		data.setShipFineOffset(fine.x, fine.y);

		shipC.updateProps();
		// Don't deselect if it was actually selected by the user
		if (Manager.getSelected() != shipC)
			shipC.deselect();
	}

	@Override
	public void doRedo() {
		if (cur == null)
			throw new IllegalStateException("Current offsets tuple is null!");

		Point thick = cur.getKey();
		Point fine = cur.getValue();

		ShipController shipC = data.getShipController();
		shipC.select();

		data.setShipOffset(thick.x, thick.y);
		data.setShipFineOffset(fine.x, fine.y);

		shipC.updateProps();
		// Don't deselect if it was actually selected by the user
		if (Manager.getSelected() != shipC)
			shipC.deselect();
	}
}
