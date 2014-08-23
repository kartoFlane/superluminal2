package com.kartoflane.superluminal2.undo;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;
import com.kartoflane.superluminal2.ui.ShipContainer;

/**
 * An undoable edit that represents modification of the ship's thick offset by shift-dragging the origin.<br>
 * <br>
 * This edit's {@link #setOld(Point) old} and {@link #setCurrent(Point) current} values
 * are the ShipController's <b>location</b> before and after the edit has happened.
 * 
 * @author kartoFlane
 *
 */
@SuppressWarnings("serial")
public class UndoableOffsetEdit extends ValueUndoableEdit<Point> {

	private ShipContainer data = null;

	/**
	 * Constructs a new UndoableOffsetEdit.<br>
	 * <br>
	 * This edit's {@link #setOld(Point) old} and {@link #setCurrent(Point) current} values
	 * are the ShipController's <b>location</b> before and after the edit has happened.
	 * 
	 * @param ac
	 *            the controller whom this edit concerns
	 */
	public UndoableOffsetEdit(ShipContainer container) {
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
			throw new IllegalStateException("Old offset is null!");

		ShipController shipC = data.getShipController();
		// Disable Bounded state to prevent the undo from bugging out,
		// since sometimes the bounding area doesn't get updated correctlyF
		shipC.setBounded(false);
		// ShipController.select() modifies its children's collidablity
		shipC.select();
		// Temporarily prevent the object from updating its Followers
		shipC.setFollowActive(false);

		shipC.reposition(old);

		Point offset = data.findShipOffset();
		offset.x /= ShipContainer.CELL_SIZE;
		offset.y /= ShipContainer.CELL_SIZE;
		data.setShipOffset(offset.x, offset.y);
		data.updateBoundingArea();

		shipC.setBounded(true);
		shipC.setFollowActive(true);
		// Don't deselect if it was actually selected by the user
		if (Manager.getSelected() != shipC)
			shipC.deselect();
	}

	@Override
	public void doRedo() {
		if (cur == null)
			throw new IllegalStateException("Current offset is null!");

		ShipController shipC = data.getShipController();
		// Disable Bounded state to prevent the undo from bugging out,
		// since sometimes the bounding area doesn't get updated correctlyF
		shipC.setBounded(false);
		// ShipController.select() modifies its children's collidablity
		shipC.select();
		// Temporarily prevent the object from updating its Followers
		shipC.setFollowActive(false);

		shipC.reposition(cur);

		Point offset = data.findShipOffset();
		offset.x /= ShipContainer.CELL_SIZE;
		offset.y /= ShipContainer.CELL_SIZE;
		data.setShipOffset(offset.x, offset.y);
		data.updateBoundingArea();

		shipC.setBounded(true);
		shipC.setFollowActive(true);
		// Don't deselect if it was actually selected by the user
		if (Manager.getSelected() != shipC)
			shipC.deselect();
	}
}
