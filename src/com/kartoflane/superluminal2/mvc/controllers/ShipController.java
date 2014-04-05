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
import com.kartoflane.superluminal2.ftl.ShipObject.Images;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;
import com.kartoflane.superluminal2.mvc.views.ShipView;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.DataComposite;
import com.kartoflane.superluminal2.ui.sidebar.ShipDataComposite;

public class ShipController extends ObjectController {

	protected ShipContainer container = null;

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

	public void setImagePath(Images imageId, String path) {
		if (imageId == null)
			throw new IllegalArgumentException("Image ID must not be null.");

		getGameObject().setImagePath(path, imageId);
		view.updateView();
		redraw();
	}

	public String getImagePath(Images imageId) {
		return getGameObject().getImagePath(imageId);
	}

	public boolean isPlayerShip() {
		return getGameObject().isPlayerShip();
	}

	@Override
	public boolean setLocation(int x, int y) {
		ArrayList<Collidable> colls = new ArrayList<Collidable>();
		if (followableActive) {
			for (Follower fol : getFollowers()) {
				Collidable c = (Collidable) fol;
				if (c.isCollidable()) {
					c.setCollidable(false);
					colls.add(c);
				}
			}
		}
		boolean result = super.setLocation(x, y);
		updateView();
		if (result) {
			for (Collidable c : colls)
				c.setCollidable(true);
		}
		colls.clear();
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

	@Override
	public void updateBoundingArea() {
		// this controller has insufficient information to update its own bounding area (needs data from ShipContainer)
		throw new NotImplementedException();
	}

	@Override
	public DataComposite getDataComposite(Composite parent) {
		return new ShipDataComposite(parent, this);
	}

	@Override
	public void redraw() {
		super.redraw();
		EditorWindow.getInstance().canvasRedraw(getView().getHLineBounds());
		EditorWindow.getInstance().canvasRedraw(getView().getVLineBounds());
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
