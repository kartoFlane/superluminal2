package com.kartoflane.superluminal2.mvc.models;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal2.components.interfaces.Alias;
import com.kartoflane.superluminal2.mvc.controllers.CellController;

public class RoomModel extends AbstractModel implements Alias, Comparable<RoomModel> {

	private static final long serialVersionUID = 8756539381176208915L;

	protected int id = -2;
	protected SystemModel systemModel;

	protected String alias = null;

	public RoomModel() {
		super();

		setLocModifiable(true);
		setSizeModifiable(false);
		setFollowActive(false);
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setSystem(SystemModel system) {
		systemModel = system;
	}

	public SystemModel getSystem() {
		return systemModel;
	}

	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public int compareTo(RoomModel o) {
		return id - o.id;
	}

	/** Returns the slot id of the tile at the given coordinates (relative to the canvas). */
	public int getSlotId(int x, int y) {
		Rectangle bounds = getBounds();
		if (!bounds.contains(x, y))
			return -1;

		x = (x - bounds.x) / CellController.SIZE;
		y = (y - bounds.y) / CellController.SIZE;

		return x + (y * (bounds.width / CellController.SIZE));
	}

	/**
	 * @param id
	 *            the slot id that is to be checked
	 * @return true if the room can contain station at the given slot, false otherwise.
	 */
	public boolean canContainSlotId(int slotId) {
		Rectangle bounds = getBounds();
		int w = bounds.width / CellController.SIZE;
		int h = bounds.height / CellController.SIZE;
		return w + h * w >= slotId && slotId >= 0;
	}

	public Point getSlotLocation(int slotId) {
		Point size = getSize();
		int w = size.x / CellController.SIZE;
		int h = size.y / CellController.SIZE;

		// can't contain
		if (w + (h - 1) * w < slotId || slotId < 0)
			return null;

		int x = slotId % w;
		int y = slotId / w;

		return new Point(x * CellController.SIZE + CellController.SIZE / 2 + 1, y * CellController.SIZE + CellController.SIZE / 2 + 1);
	}
}
