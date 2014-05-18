package com.kartoflane.superluminal2.mvc.controllers;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.ftl.DoorObject;
import com.kartoflane.superluminal2.ftl.RoomObject;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;
import com.kartoflane.superluminal2.mvc.views.BaseView;
import com.kartoflane.superluminal2.mvc.views.DoorView;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;
import com.kartoflane.superluminal2.ui.sidebar.data.DoorDataComposite;

public class DoorController extends ObjectController {

	public static final int COLLISION_TOLERANCE = 3;

	protected ShipContainer container = null;

	private DoorController(ShipContainer container, ObjectModel model, DoorView view) {
		super();
		setModel(model);
		setView(view);

		this.container = container;

		setSelectable(true);
		setLocModifiable(true);
		setBounded(true);
		setCollidable(true);
		setTolerance(COLLISION_TOLERANCE);
		setParent(container.getShipController());
		setPresentedFactor(ShipContainer.CELL_SIZE);
		setSize(DoorObject.DOOR_WIDTH, DoorObject.DOOR_HEIGHT);
	}

	public static DoorController newInstance(ShipContainer container, DoorObject object) {
		ObjectModel model = new ObjectModel(object);
		DoorView view = new DoorView();
		DoorController controller = new DoorController(container, model, view);
		controller.setHorizontal(controller.getGameObject().isHorizontal());

		return controller;
	}

	public DoorObject getGameObject() {
		return (DoorObject) getModel().getGameObject();
	}

	public void setHorizontal(boolean horizontal) {
		setVisible(false);
		Point p = getPresentedLocation();
		getGameObject().setHorizontal(horizontal);
		updateBoundingArea();

		setSnapMode(horizontal ? Snapmodes.EDGE_H : Snapmodes.EDGE_V);
		setLocation(Grid.getInstance().snapToGrid(p.x * getPresentedFactor(), p.y * getPresentedFactor(), getSnapMode()));
		updateFollowOffset();

		updateView();
		setVisible(true);
	}

	public boolean isHorizontal() {
		return getGameObject().isHorizontal();
	}

	@Override
	public void setView(View view) {
		super.setView(view);
		this.view.addToPainter(Layers.DOOR);
	}

	public void setLeftRoom(RoomObject room) {
		getGameObject().setLeftRoom(room);
	}

	public void setRightRoom(RoomObject room) {
		getGameObject().setRightRoom(room);
	}

	public RoomObject getLeftRoom() {
		return getGameObject().getLeftRoom();
	}

	public RoomObject getRightRoom() {
		return getGameObject().getRightRoom();
	}

	@Override
	public int getW() {
		return isHorizontal() ? DoorObject.DOOR_WIDTH : DoorObject.DOOR_HEIGHT;
	}

	@Override
	public int getH() {
		return isHorizontal() ? DoorObject.DOOR_HEIGHT : DoorObject.DOOR_WIDTH;
	}

	@Override
	public Rectangle getBounds() {
		Rectangle b = model.getBounds();
		if (!isHorizontal()) {
			int cX = b.x + b.width / 2;
			int cY = b.y + b.height / 2;
			b.x = cX - b.height / 2;
			b.y = cY - b.width / 2;
			cX = b.width;
			b.width = b.height;
			b.height = cX;
		}
		return b;
	}

	@Override
	public Point getPresentedLocation() {
		return new Point((getX() + getH()) / getPresentedFactor(),
				(getY() + getW()) / getPresentedFactor());
	}

	@Override
	public Point getPresentedSize() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPresentedLocation(int x, int y) {
		setLocation(Grid.getInstance().snapToGrid(x * getPresentedFactor(), y * getPresentedFactor(), getSnapMode()));
	}

	@Override
	public void setPresentedSize(int w, int h) {
		throw new UnsupportedOperationException();
	}

	@Override
	public DataComposite getDataComposite(Composite parent) {
		DataComposite dc = new DoorDataComposite(parent, this);
		dc.updateData();
		return dc;
	}

	protected void updateSelectionAppearance() {
		if (selected) {
			view.setBorderColor(BaseView.SELECT_RGB);
			view.setBorderThickness(2);
		} else {
			view.setBorderColor(null);
		}
		redraw();
	}

	@Override
	public void select() {
		super.select();
		updateSelectionAppearance();
	}

	@Override
	public void deselect() {
		super.deselect();
		setMoving(false);
		updateSelectionAppearance();
	}

	@Override
	public void updateBoundingArea() {
		Point gridSize = Grid.getInstance().getSize();
		gridSize.x -= (gridSize.x % ShipContainer.CELL_SIZE) + ShipContainer.CELL_SIZE;
		gridSize.y -= (gridSize.y % ShipContainer.CELL_SIZE) + ShipContainer.CELL_SIZE;
		setBoundingPoints(getParent().getX() + (isHorizontal() ? getW() / 2 + 4 : 0),
				getParent().getY() + (isHorizontal() ? 0 : getH() / 2 + 4), gridSize.x, gridSize.y);
	}

	@Override
	public String toString() {
		DoorObject door = getGameObject();
		String result = "Door " + (door.isHorizontal() ? "H" : "V");
		if (getAlias() != null && !getAlias().equals(""))
			result += " (" + door.getAlias() + ")";
		return result;
	}

	/**
	 * Makes sure that the door is not linked to a deleted room.
	 */
	public void verifyLinks() {
		getGameObject().verifyLinks();
	}
}
