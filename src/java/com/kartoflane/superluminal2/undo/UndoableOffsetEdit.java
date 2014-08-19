package com.kartoflane.superluminal2.undo;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.mvc.controllers.ShipController;
import com.kartoflane.superluminal2.ui.ShipContainer;

@SuppressWarnings("serial")
public class UndoableOffsetEdit extends ValueUndoableEdit<Point> {

	private ShipContainer data = null;

	/**
	 * Constructs a new UndoableOffsetEdit.
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
		return String.format("modify offset");
	}

	@Override
	public void doUndo() {
		if (old == null)
			throw new IllegalStateException("Old offset is null!");

		ShipController shipC = data.getShipController();
		shipC.setFollowActive(false);
		shipC.reposition(old);

		Point offset = data.findShipOffset();
		offset.x /= ShipContainer.CELL_SIZE;
		offset.y /= ShipContainer.CELL_SIZE;
		data.setShipOffset(offset.x, offset.y);
		data.updateBoundingArea();

		shipC.setFollowActive(true);
	}

	@Override
	public void doRedo() {
		if (cur == null)
			throw new IllegalStateException("Current offset is null!");

		ShipController shipC = data.getShipController();
		shipC.setFollowActive(false);
		shipC.reposition(cur);

		Point offset = data.findShipOffset();
		offset.x /= ShipContainer.CELL_SIZE;
		offset.y /= ShipContainer.CELL_SIZE;
		data.setShipOffset(offset.x, offset.y);
		data.updateBoundingArea();

		shipC.setFollowActive(true);
	}
}
