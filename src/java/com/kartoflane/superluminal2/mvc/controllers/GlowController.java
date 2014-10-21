package com.kartoflane.superluminal2.mvc.controllers;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.enums.Directions;
import com.kartoflane.superluminal2.components.interfaces.Follower;
import com.kartoflane.superluminal2.core.LayeredPainter.Layers;
import com.kartoflane.superluminal2.events.SLEvent;
import com.kartoflane.superluminal2.ftl.GlowObject;
import com.kartoflane.superluminal2.ftl.GlowSet;
import com.kartoflane.superluminal2.ftl.GlowSet.Glows;
import com.kartoflane.superluminal2.ftl.RoomObject;
import com.kartoflane.superluminal2.ftl.StationObject;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;
import com.kartoflane.superluminal2.mvc.views.GlowView;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;
import com.kartoflane.superluminal2.ui.sidebar.data.GlowDataComposite;

public class GlowController extends ObjectController {

	private StationObject station = null;

	private GlowController(StationController station, ObjectModel model, GlowView view) {
		super();
		setModel(model);
		setView(view);

		this.station = station.getGameObject();

		setSelectable(true);
		setLocModifiable(true);

		setParent(station);
		setBounded(true);
		updateBoundingArea();

		station.addListener(SLEvent.VISIBLE, this);
		station.addListener(SLEvent.DIRECTION, this);
		station.addListener(SLEvent.DISPOSE, this);
	}

	/**
	 * Creates a new object represented by the MVC system, ie.
	 * a new Controller associated with the Model and a new View object
	 */
	public static GlowController newInstance(StationController station, GlowObject object) {
		ObjectModel model = new ObjectModel(object);
		GlowView view = new GlowView();
		GlowController controller = new GlowController(station, model, view);

		controller.setVisible(false);
		controller.updateView();
		controller.setGlowSet(object.getGlowSet());
		controller.applyGlowSettings();

		return controller;
	}

	@Override
	public void setView(View view) {
		super.setView(view);
		this.view.addToPainter(Layers.GLOW);
	}

	@Override
	public GlowObject getGameObject() {
		return (GlowObject) getModel().getGameObject();
	}

	public void applyGlowSettings() {
		GlowObject glow = getGameObject();
		setDirection(glow.getDirection());

		RoomObject room = getRoom();
		if (room != null) {
			Point rel = room.getSlotLocation(station.getSlotId());
			if (rel != null) {
				Point result = new Point(0, 0);
				result.x = glow.getX() - rel.x + getWDir() / 2;
				result.y = glow.getY() - rel.y + getHDir() / 2;
				setFollowOffset(result.x, result.y);
				updateFollower();
			}
		}
	}

	/**
	 * @return width of the glow, accounting for its rotation
	 */
	public int getWDir() {
		switch (getGameObject().getDirection()) {
			case UP:
			case DOWN:
				return getW();
			case LEFT:
			case RIGHT:
				return getH();
			default:
				return 0;
		}
	}

	/**
	 * @return height of the glow, accounting for its rotation
	 */
	public int getHDir() {
		switch (getGameObject().getDirection()) {
			case UP:
			case DOWN:
				return getH();
			case LEFT:
			case RIGHT:
				return getW();
			default:
				return 0;
		}
	}

	public Point getGlowLocRelativeToRoom() {
		Point result = getFollowOffset();
		RoomObject room = getRoom();
		if (room != null) {
			Point rel = room.getSlotLocation(station.getSlotId());
			if (rel != null) {
				result.x += rel.x - getWDir() / 2;
				result.y += rel.y - getHDir() / 2;
			}
		}

		return result;
	}

	public void setGlowSet(GlowSet set) {
		if (set == null)
			throw new IllegalArgumentException("Glow set must not be null.");

		getGameObject().setGlowSet(set);
		String path = set.getImage(Glows.BLUE);
		if (path == null)
			path = "cpath:/assets/station_glow.png";
		view.setImage(path);

		Rectangle glowBounds = view.getImageBounds();
		setSize(glowBounds.width, glowBounds.height);
	}

	/**
	 * Sets the facing of the glow.
	 */
	public void setDirection(Directions dir) {
		getGameObject().setDirection(dir);

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
	}

	@Override
	public void handleEvent(SLEvent e) {
		if (e.type == SLEvent.DIRECTION)
			setDirection((Directions) e.data);
		if (e.type == SLEvent.DISPOSE) {
			dispose();
		}

		super.handleEvent(e);
	}

	@Override
	public void select() {
		super.select();
		updateView();
		redraw();
	}

	@Override
	public void deselect() {
		super.deselect();
		setMoving(false);
		updateView();
		redraw();
	}

	@Override
	public void updateBoundingArea() {
		Point stationLoc = getParent().getLocation();
		int c = ShipContainer.CELL_SIZE;
		setBoundingPoints(stationLoc.x - c / 2, stationLoc.y - c / 2,
				stationLoc.x + c / 2, stationLoc.y + c / 2);
	}

	@Override
	public DataComposite getDataComposite(Composite parent) {
		return new GlowDataComposite(parent, this);
	}

	@Override
	public void dispose() {
		if (model.isDisposed())
			return;

		super.dispose();
	}

	private RoomObject getRoom() {
		// Station objects don't know what system they're assigned to
		Follower parent = ((Follower) getParent()); // station
		SystemController system = (SystemController) parent.getParent();
		return system.getRoom();
	}
}
