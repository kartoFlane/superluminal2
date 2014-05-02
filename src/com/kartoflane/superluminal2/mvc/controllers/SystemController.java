package com.kartoflane.superluminal2.mvc.controllers;

import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal2.components.enums.Directions;
import com.kartoflane.superluminal2.components.enums.Systems;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.ftl.RoomObject;
import com.kartoflane.superluminal2.ftl.SystemObject;
import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;
import com.kartoflane.superluminal2.mvc.views.SystemView;
import com.kartoflane.superluminal2.ui.ShipContainer;

public class SystemController extends ObjectController implements Controller {

	protected ShipContainer container = null;

	private SystemController(ShipContainer container, ObjectModel model, SystemView view) {
		super();
		setModel(model);
		setView(view);
		this.container = container;

		setSelectable(false);
		setLocModifiable(false);

		setSize(ShipContainer.CELL_SIZE, ShipContainer.CELL_SIZE);
	}

	/**
	 * Creates a new object represented by the MVC system, ie.
	 * a new Controller associated with the Model and a new View object
	 */
	public static SystemController newInstance(ShipContainer container, SystemObject object) {
		ObjectModel model = new ObjectModel(object);
		SystemView view = new SystemView();
		SystemController controller = new SystemController(container, model, view);

		controller.setFollowOffset(0, 0);
		controller.setVisible(false);
		controller.updateView();

		return controller;
	}

	protected SystemView getView() {
		return (SystemView) view;
	}

	@Override
	public SystemObject getGameObject() {
		return (SystemObject) getModel().getGameObject();
	}

	/**
	 * Sets the room to which this system is assigned.<br>
	 * This method <b>must not</b> be called directly. Use {@link #assignTo(RoomController)} instead.
	 */
	protected void setRoom(RoomObject room) {
		getGameObject().setRoom(room);
	}

	protected RoomObject getRoom() {
		return getGameObject().getRoom();
	}

	public boolean isAssigned() {
		return getGameObject().isAssigned();
	}

	public Systems getSystemId() {
		return getGameObject().getSystemId();
	}

	/**
	 * Unassigns this system from its current room.<br>
	 * This method should not be called directly.
	 * Use {@link ShipContainer#unassign(Systems)} instead.
	 */
	public void unassign() {
		getGameObject().setRoom(null);
		setParent(null);
		updateView();
	}

	/**
	 * Assigns this system to the room supplied in argument.<br>
	 * This method should not be called directly.
	 * Use {@link ShipContainer#assign(Systems, RoomController)} instead.
	 */
	public void assignTo(RoomObject room) {
		unassign();
		getGameObject().setRoom(room);
		updateView();
	}

	@Override
	public void setView(View view) {
		super.setView(view);
		// this.view.addToPainter(Layers.SYSTEM); // views are told to draw by room controllers
	}

	public void setAvailableAtStart(boolean available) {
		getGameObject().setAvailable(available);
		updateView();
	}

	public boolean isAvailableAtStart() {
		return getGameObject().isAvailable();
	}

	/**
	 * @see {@link Cache#checkOutImage(Object, String)}
	 */
	public void setInteriorPath(String interiorPath) {
		getGameObject().setInteriorPath(interiorPath);
		updateView();
		redraw();
	}

	public int getLevelCap() {
		return getGameObject().getLevelCap();
	}

	public void setLevel(int level) {
		getGameObject().setLevelStart(level);
	}

	public int getLevel() {
		return getGameObject().getLevelStart();
	}

	public void setLevelMax(int level) {
		getGameObject().setLevelMax(level);
	}

	public int getLevelMax() {
		return getGameObject().getLevelMax();
	}

	public String getInteriorPath() {
		return getGameObject().getInteriorPath();
	}

	public boolean canContainGlow() {
		return getGameObject().canContainGlow();
	}

	public boolean canContainInterior() {
		return getGameObject().canContainInterior();
	}

	public boolean canContainStation() {
		return getGameObject().canContainStation();
	}

	public Directions getDefaultStationDirection() {
		return SystemObject.getDefaultSlotDirection(getSystemId());
	}

	public int getDefaultStationSlotId() {
		return SystemObject.getDefaultSlotId(getSystemId());
	}

	@Override
	public void redraw() {
		super.redraw();
		Rectangle bounds = getView().getInteriorBounds();
		bounds.x = getX() - getW() / 2;
		bounds.y = getY() - getH() / 2;
		redraw(bounds);
	}

	@Override
	public String toString() {
		return getSystemId().toString();
	}
}
