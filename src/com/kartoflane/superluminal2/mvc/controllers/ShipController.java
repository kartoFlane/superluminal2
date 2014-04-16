package com.kartoflane.superluminal2.mvc.controllers;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.components.interfaces.Collidable;
import com.kartoflane.superluminal2.components.interfaces.Follower;
import com.kartoflane.superluminal2.ftl.ShipObject;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;
import com.kartoflane.superluminal2.mvc.views.ShipView;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;
import com.kartoflane.superluminal2.ui.sidebar.data.ShipDataComposite;

public class ShipController extends ObjectController {

	protected ShipContainer container = null;
	protected ArrayList<Collidable> collidables = new ArrayList<Collidable>();

	private ShipController(ShipContainer container, ObjectModel model, ShipView view) {
		super();
		setModel(model);
		setView(view);
		this.container = container;

		setSelectable(true);
		setLocModifiable(false);
		setBounded(true);
		Point gridSize = Grid.getInstance().getSize();
		gridSize = Grid.getInstance().snapToGrid(gridSize, Snapmodes.CROSS);
		setBoundingArea(0, 0, gridSize.x, gridSize.y);
		setSnapMode(Snapmodes.CROSS);

		setSize(ShipContainer.CELL_SIZE / 2, ShipContainer.CELL_SIZE / 2);
	}

	@Override
	public void select() {
		super.select();

		if (followableActive) {
			for (Follower fol : getFollowers()) {
				Collidable c = (Collidable) fol;
				if (c.isCollidable()) {
					c.setCollidable(false);
					collidables.add(c);
				}
			}
		}
	}

	@Override
	public void deselect() {
		super.deselect();

		for (Collidable c : collidables)
			c.setCollidable(true);
		collidables.clear();
	}

	public static ShipController newInstance(ShipContainer container, ShipObject object) {
		ObjectModel model = new ObjectModel(object);
		ShipView view = new ShipView();
		ShipController controller = new ShipController(container, model, view);

		return controller;
	}

	private ObjectModel getModel() {
		return (ObjectModel) model;
	}

	@Override
	public ShipObject getGameObject() {
		return (ShipObject) getModel().getGameObject();
	}

	private ShipView getView() {
		return (ShipView) view;
	}

	@Override
	public void setView(View view) {
		super.setView(view);
		this.view.addToPainter(Layers.SHIP_ORIGIN);
	}

	public boolean isPlayerShip() {
		return getGameObject().isPlayerShip();
	}

	@Override
	public boolean setLocation(int x, int y) {
		boolean result = super.setLocation(x, y);
		updateView();
		return result;
	}

	@Override
	public Point getPresentedLocation() {
		return new Point(getX() / ShipContainer.CELL_SIZE, getY() / ShipContainer.CELL_SIZE);
	}

	@Override
	public Point getPresentedSize() {
		throw new NotImplementedException();
	}

	/**
	 * This controller has insufficient information to update its own bounding area (needs data from ShipContainer)<br>
	 * Use {@link ShipContainer#updateBoundingArea()} instead.
	 */
	@Override
	public void updateBoundingArea() {
		throw new NotImplementedException();
	}

	@Override
	public DataComposite getDataComposite(Composite parent) {
		return new ShipDataComposite(parent, this);
	}

	@Override
	public void redraw() {
		super.redraw();
		redraw(getView().getHLineBounds());
		redraw(getView().getVLineBounds());
	}

	@Override
	public boolean contains(int x, int y) {
		return model.contains(x, y);
	}

	@Override
	public boolean intersects(Rectangle rect) {
		return model.intersects(rect) || getView().getHLineBounds().intersects(rect) || getView().getVLineBounds().intersects(rect);
	}
}
