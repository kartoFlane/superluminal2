package com.kartoflane.superluminal2.mvc.controllers;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.Model;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.models.SystemModel;
import com.kartoflane.superluminal2.mvc.models.SystemModel.Glows;
import com.kartoflane.superluminal2.mvc.models.SystemModel.Systems;
import com.kartoflane.superluminal2.mvc.views.SystemView;

public class SystemController extends AbstractController implements Controller {

	protected SystemModel model;
	protected SystemView view;

	protected RoomController room = null;
	protected StationController station = null;

	private SystemController(SystemModel model, SystemView view) {
		super();

		setModel(model);
		setView(view);

		setSelectable(false);
	}

	/**
	 * Creates a new object represented by the MVC system, ie.
	 * a new Controller associated with the Model and a new View object
	 */
	public static SystemController newInstance(SystemModel model) {
		SystemView view = new SystemView();
		SystemController controller = new SystemController(model, view);

		if (model.canContainStation()) {
			controller.setStationController(StationController.newInstance(controller));
		}

		model.setFollowOffset(0, 0);
		view.setImage("/assets/system/" + model.toString().toLowerCase() + ".png");
		controller.setVisible(false);

		return controller;
	}

	/**
	 * Sets the room to which this system is assigned.<br>
	 * This method <b>must not</b> be called directly. Use {@link #assignTo(RoomController)} instead.
	 */
	protected void setRoom(RoomController room) {
		this.room = room;
	}

	public void assignTo(RoomController room) {
		SystemController emptyController = Manager.getCurrentShip().getSystemController(Systems.EMPTY);
		if (this.room != null) {
			// reset previous room
			this.room.setSystem(emptyController);
			this.room.getModel().setSystem(emptyController.getModel());
		}

		// update references in this controller and model
		if (emptyController != this) { // EMPTY can be assigned to multiple rooms
			setRoom(room);
			model.setRoom(room == null ? null : room.getModel());
		}

		if (room != null) {
			// reset previous system
			room.getSystemController().assignTo(null);
			// assign new system
			room.setSystem(this);
			room.getModel().setSystem(model);
		}

		setVisible(room != null);
		redraw();
	}

	public RoomController getRoom() {
		return room;
	}

	public StationController getStationController() {
		return station;
	}

	protected void setStationController(StationController station) {
		this.station = station;
	}

	@Override
	public void setModel(Model model) {
		if (model == null)
			throw new IllegalArgumentException("Argument is null.");
		this.model = (SystemModel) model;
	}

	@Override
	public SystemModel getModel() {
		return model;
	}

	@Override
	public void setView(View view) {
		if (view == null)
			throw new IllegalArgumentException("Argument is null.");

		if (this.view != null)
			this.view.dispose();
		this.view = (SystemView) view;

		view.setController(this);
		this.view.addToPainter(Layers.SYSTEM);
	}

	@Override
	public SystemView getView() {
		return view;
	}

	public void setAvailableAtStart(boolean available) {
		model.setAvailableAtStart(available);
		if (available) {
			view.setBackgroundColor(null);
		} else {
			view.setBackgroundColor(255, 0, 0);
		}
		redraw();
	}

	public void setInterior(String interiorPath) {
		model.setInteriorPath(interiorPath);
		view.setInteriorImage(interiorPath);
		redraw();
	}

	public void setGlow(Glows glowId, String glowPath) {
		model.setGlowPath(glowId, glowPath);
		// glows are not represented in the editor, nothing else to do here
	}

	@Override
	public void setSize(int w, int h) {
		throw new NotImplementedException();
	}

	@Override
	public void setLocation(int x, int y) {
		throw new NotImplementedException();
	}

	@Override
	public void translate(int dx, int dy) {
		throw new NotImplementedException();
	}

	@Override
	public void dispose() {
		view.dispose();
		if (room != null)
			assignTo(null);
	}

	@Override
	public Point toPresentedLocation(int x, int y) {
		throw new NotImplementedException();
	}

	@Override
	public Point toPresentedSize(int w, int h) {
		throw new NotImplementedException();
	}

	@Override
	public Point toNormalLocation(int x, int y) {
		throw new NotImplementedException();
	}

	@Override
	public Point toNormalSize(int w, int h) {
		throw new NotImplementedException();
	}

	@Override
	public boolean contains(int x, int y) {
		return model.contains(x, y);
	}

	@Override
	public boolean intersects(Rectangle rect) {
		return model.intersects(rect);
	}
}
