package com.kartoflane.superluminal2.mvc.controllers;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.components.enums.Directions;
import com.kartoflane.superluminal2.components.enums.Systems;
import com.kartoflane.superluminal2.core.LayeredPainter.Layers;
import com.kartoflane.superluminal2.events.SLDirectionEvent;
import com.kartoflane.superluminal2.events.SLEvent;
import com.kartoflane.superluminal2.events.SLVisibilityEvent;
import com.kartoflane.superluminal2.ftl.StationObject;
import com.kartoflane.superluminal2.ftl.SystemObject;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;
import com.kartoflane.superluminal2.mvc.views.StationView;
import com.kartoflane.superluminal2.ui.ShipContainer;

public class StationController extends ObjectController {

	private final ShipContainer container;
	private final SystemObject system;

	private StationController(ShipContainer container, SystemController system, ObjectModel model, StationView view) {
		super();
		setModel(model);
		setView(view);

		this.container = container;
		this.system = system.getGameObject();

		setSelectable(false);
		setLocModifiable(false);

		setSize(ShipContainer.CELL_SIZE, ShipContainer.CELL_SIZE);

		setSlotDirection(getGameObject().getSlotDirection());
		setSlotId(getGameObject().getSlotId());

		setParent(system);
		system.addListener(SLEvent.VISIBLE, this);
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
		updateView();
	}

	public int getSlotId() {
		return getGameObject().getSlotId();
	}

	/**
	 * Sets the facing of the station.<br>
	 * If setting both direction and slot id, change the direction first.
	 */
	public void setSlotDirection(Directions dir) {
		// Medbay and Clonebay have no direction
		if (system.getSystemId() == Systems.MEDBAY || system.getSystemId() == Systems.CLONEBAY)
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
		updateView();

		if (eventHandler != null && eventHandler.hooks(SLEvent.DIRECTION))
			eventHandler.sendEvent(new SLDirectionEvent(this, getSlotDirection()));
	}

	public Directions getSlotDirection() {
		return getGameObject().getSlotDirection();
	}

	@Override
	public void notifySizeChanged(int w, int h) {
		updateFollowOffset();
		updateView();
	}

	public SystemObject getSystem() {
		return system;
	}

	@Override
	public void updateFollowOffset() {
		super.updateFollowOffset();
		if (system.isAssigned()) {
			RoomController room = (RoomController) container.getController(system.getRoom());
			Point slotLoc = room.getSlotLocation(getSlotId());
			if (slotLoc != null)
				setFollowOffset(slotLoc.x - room.getW() / 2, slotLoc.y - room.getH() / 2);
		}
	}

	@Override
	public void updateView() {
		if (system.isAssigned()) {
			RoomController room = (RoomController) container.getController(system.getRoom());
			// hide the station if the room cannot contain the slot, or the system is not active
			setVisible(room.canContainSlotId(getSlotId()) && container.getActiveSystem(system.getRoom()) == system);
		} else {
			setVisible(false);
		}
	}

	@Override
	public void handleEvent(SLEvent e) {
		if (e instanceof SLVisibilityEvent) {
			RoomController room = (RoomController) container.getController(system.getRoom());
			if (e.source == getParent())
				setVisible((Boolean) e.data && (room == null || room.canContainSlotId(getSlotId())));
		} else {
			super.handleEvent(e);
		}
	}
}
