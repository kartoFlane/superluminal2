package com.kartoflane.superluminal2.mvc.controllers;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.components.Directions;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.ftl.StationObject;
import com.kartoflane.superluminal2.ftl.SystemObject.Systems;
import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;
import com.kartoflane.superluminal2.mvc.views.StationView;
import com.kartoflane.superluminal2.ui.ShipContainer;

public class StationController extends ObjectController implements Controller {

	private final ShipContainer container;
	private final Systems id;

	private StationController(ShipContainer container, SystemController system, ObjectModel model, StationView view) {
		super();
		setModel(model);
		setView(view);

		this.container = container;
		this.id = system.getSystemId();

		setSelectable(false);
		setLocModifiable(false);

		setSize(ShipContainer.CELL_SIZE, ShipContainer.CELL_SIZE);

		setSlotDirection(getGameObject().getSlotDirection());
		setSlotId(getGameObject().getSlotId());

		setParent(system);
	}

	/**
	 * Creates a new object represented by the MVC system, ie.
	 * a new Controller associated with the Model and a new View object
	 */
	public static StationController newInstance(ShipContainer container, SystemController system, StationObject object) {
		ObjectModel model = new ObjectModel(object);
		StationView view = new StationView();
		StationController controller = new StationController(container, system, model, view);

		controller.setVisible(false);
		controller.updateView();

		if (system.getSystemId() == Systems.MEDBAY || system.getSystemId() == Systems.CLONEBAY) {
			view.setImage("cpath:/assets/station_slot.png");
		} else {
			view.setImage("cpath:/assets/station_console.png");
		}

		return controller;
	}

	private ObjectModel getModel() {
		return (ObjectModel) model;
	}

	@Override
	public void setView(View view) {
		super.setView(view);
		this.view.addToPainter(Layers.STATION);
	}

	@Override
	public StationObject getGameObject() {
		return (StationObject) getModel().getGameObject();
	}

	/**
	 * Sets the slot id that is occupied by this station.<br>
	 * If settings both direction and slot id, change the direction first.
	 */
	public void setSlotId(int id) {
		getGameObject().setSlotId(id);
		updateFollowOffset();
	}

	public int getSlotId() {
		return getGameObject().getSlotId();
	}

	/**
	 * Sets the facing of the station.<br>
	 * If setting both direction and slot id, change the direction first.
	 */
	public void setSlotDirection(Directions dir) {
		if (id == Systems.MEDBAY || id == Systems.CLONEBAY) // medbay and clonebay have no direction
			dir = Directions.NONE;

		getGameObject().setSlotDirection(dir);

		switch (dir) {
			case NONE:
			case UP:
				view.setRotation(0);
				break;
			case RIGHT:
				view.setRotation(90);
				break;
			case DOWN:
				view.setRotation(180);
				break;
			case LEFT:
				view.setRotation(270);
				break;
			default:
				throw new IllegalArgumentException();
		}
		updateFollowOffset();
	}

	public Directions getSlotDirection() {
		return getGameObject().getSlotDirection();
	}

	@Override
	public void updateFollowOffset() {
		super.updateFollowOffset();
		SystemController system = container.getSystemController(id);
		if (system.isAssigned()) {
			RoomController room = (RoomController) container.getController(system.getRoom());
			Point slotLoc = room.getSlotLocation(getSlotId());
			if (slotLoc != null)
				setFollowOffset(slotLoc.x - room.getW() / 2, slotLoc.y - room.getH() / 2);
			// hide the station if the room cannot contain the slot
			setVisible(slotLoc != null && container.getAssignedSystem(system.getRoom()) == system.getSystemId());
		} else {
			setVisible(false);
		}
	}

	@Override
	public void notifySizeChanged(int w, int h) {
		updateFollowOffset();
	}

	public Systems getSystemId() {
		return id;
	}
}
