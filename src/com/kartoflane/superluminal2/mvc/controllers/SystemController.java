package com.kartoflane.superluminal2.mvc.controllers;

import com.kartoflane.superluminal2.ftl.RoomObject;
import com.kartoflane.superluminal2.ftl.SystemObject;
import com.kartoflane.superluminal2.ftl.SystemObject.Glows;
import com.kartoflane.superluminal2.ftl.SystemObject.Systems;
import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.ObjectModel;
import com.kartoflane.superluminal2.mvc.View;
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
	}

	/**
	 * Creates a new object represented by the MVC system, ie.
	 * a new Controller associated with the Model and a new View object
	 */
	public static SystemController newInstance(ShipContainer container, SystemObject object) {
		ObjectModel model = new ObjectModel(object);
		SystemView view = new SystemView();
		SystemController controller = new SystemController(container, model, view);

		/*
		 * if (object.canContainStation()) {
		 * controller.setStationController(StationController.newInstance(controller));
		 * }
		 */

		controller.setFollowOffset(0, 0);
		controller.setVisible(false);
		controller.updateView();

		return controller;
	}

	private ObjectModel getModel() {
		return (ObjectModel) model;
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
		RoomObject room = getGameObject().getRoom();
		if (room != null) {
			room.setSystem(Systems.EMPTY);
			getGameObject().setRoom(null);
		}
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
		room.setSystem(getSystemId());
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
		redraw();
	}

	public boolean isAvailableAtStart() {
		return getGameObject().isAvailable();
	}

	public void setInteriorPath(String interiorPath) {
		getGameObject().setInteriorPath(interiorPath);
		updateView();
		redraw();
	}

	public void setGlowPath(Glows glowId, String glowPath) {
		getGameObject().setGlowPath(glowPath, glowId);
		// glows are not represented in the editor, nothing else to do here
	}

	public String getInteriorPath() {
		return getGameObject().getInteriorPath();
	}

	public String getGlowPath(Glows glowId) {
		return getGameObject().getGlowPath(glowId);
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

	public boolean canContainGlow() {
		return getGameObject().canContainGlow();
	}

	public boolean canContainInterior() {
		return getGameObject().canContainInterior();
	}

	public boolean canContainStation() {
		return getGameObject().canContainStation();
	}

	@Override
	public String toString() {
		return getSystemId().toString();
	}
}
