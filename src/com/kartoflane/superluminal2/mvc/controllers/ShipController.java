package com.kartoflane.superluminal2.mvc.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.components.interfaces.Follower;
import com.kartoflane.superluminal2.mvc.Model;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.models.DoorModel;
import com.kartoflane.superluminal2.mvc.models.MountModel;
import com.kartoflane.superluminal2.mvc.models.RoomModel;
import com.kartoflane.superluminal2.mvc.models.ShipModel;
import com.kartoflane.superluminal2.mvc.models.ShipModel.Images;
import com.kartoflane.superluminal2.mvc.models.SystemModel.Systems;
import com.kartoflane.superluminal2.mvc.views.AbstractView;
import com.kartoflane.superluminal2.mvc.views.ShipView;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.sidebar.DataComposite;
import com.kartoflane.superluminal2.ui.sidebar.ShipDataComposite;

public class ShipController extends AbstractController {

	protected ShipModel model;
	protected ShipView view;

	protected TreeSet<RoomController> roomControllers;
	protected ArrayList<DoorController> doorControllers;
	protected TreeSet<MountController> mountControllers;
	protected HashMap<Systems, SystemController> systemControllerMap;

	private ShipController(ShipModel model, ShipView view) {
		super();

		setModel(model);
		setView(view);
		view.updateLines();

		setSelectable(true);
		setSnapMode(Snapmodes.CROSS);

		systemControllerMap = new HashMap<Systems, SystemController>();
		for (Systems sys : Systems.values()) {
			systemControllerMap.put(sys, SystemController.newInstance(model.getSystem(sys)));
		}

		roomControllers = new TreeSet<RoomController>();
		doorControllers = new ArrayList<DoorController>();
		mountControllers = new TreeSet<MountController>();
	}

	public static ShipController newInstance(boolean isPlayer) {
		ShipModel model = new ShipModel(isPlayer);
		ShipView view = new ShipView();
		ShipController controller = new ShipController(model, view);

		controller.reposition(CellController.SIZE * 2, CellController.SIZE * 2);

		return controller;
	}

	public static ShipController newInstance(ShipModel model) {
		ShipView view = new ShipView();
		ShipController controller = new ShipController(model, view);

		for (RoomModel room : model.getRooms())
			controller.roomControllers.add(RoomController.newInstance(controller, room));
		for (DoorModel door : model.getDoors())
			controller.doorControllers.add(DoorController.newInstance(controller, door));
		for (MountModel mount : model.getMounts())
			controller.mountControllers.add(MountController.newInstance(controller, mount));

		controller.redraw();

		return controller;
	}

	@Override
	public ShipModel getModel() {
		return model;
	}

	@Override
	public void setModel(Model model) {
		if (model == null)
			throw new IllegalArgumentException("Argument is null.");
		this.model = (ShipModel) model;
	}

	@Override
	public ShipView getView() {
		return view;
	}

	@Override
	public void setView(View view) {
		if (view == null)
			throw new IllegalArgumentException("Argument is null.");

		if (this.view != null)
			this.view.dispose();
		this.view = (ShipView) view;

		view.setController(this);
		this.view.addToPainter(Layers.SHIP_ORIGIN);
	}

	public void setImagePath(Images imageId, String path) {
		if (imageId == null)
			throw new IllegalArgumentException("Image ID must not be null.");

		model.setImagePath(imageId, path);
		// TODO
	}

	public void add(RoomController room) {
		room.getModel().setId(getNextRoomId());
		roomControllers.add(room);
		model.getRooms().add(room.getModel());
	}

	public void add(DoorController door) {
		doorControllers.add(door);
		model.getDoors().add(door.getModel());
	}

	public void add(MountController mount) {
		mount.getModel().setId(getNextMountId());
		mountControllers.add(mount);
		model.getMounts().add(mount.getModel());
	}

	public SystemController getSystemController(Systems systemId) {
		return systemControllerMap.get(systemId);
	}

	/** @return an unmodifiable sorted set of the ship's rooms. */
	public SortedSet<RoomController> getRoomControllers() {
		return Collections.unmodifiableSortedSet(roomControllers);
	}

	/** @return an unmodifiable list of the ship's doors. */
	public List<DoorController> getDoorControllers() {
		return Collections.unmodifiableList(doorControllers);
	}

	/** @return an unmodifiable sorted set of the ship's mounts. */
	public SortedSet<MountController> getMountControllers() {
		return Collections.unmodifiableSortedSet(mountControllers);
	}

	protected int getNextRoomId() {
		int id = 0;
		for (RoomController control : roomControllers) {
			if (id == control.getModel().getId())
				id++;
		}
		return id;
	}

	protected int getNextMountId() {
		int id = 0;
		for (MountController control : mountControllers) {
			if (id == control.getModel().getId())
				id++;
		}
		return id;
	}

	@Override
	public void setSize(int w, int h) {
		return;
	}

	@Override
	public void setLocation(int x, int y) {
		model.setLocation(x, y);
		view.updateLines();
		if (followableActive)
			for (Follower fol : getFollowers()) {
				AbstractController control = (AbstractController) fol;
				boolean wasCollidable = control.isCollidable();
				control.setCollidable(false);
				control.updateFollower();
				control.updateBoundingArea();
				control.setCollidable(wasCollidable);
			}
	}

	@Override
	public void translate(int dx, int dy) {
		model.translate(dx, dy);
		view.updateLines();
		if (followableActive)
			for (Follower fol : getFollowers())
				fol.updateFollower();
	}

	protected void updateSelectionAppearance() {
		if (selected) {
			view.setBorderColor(AbstractView.SELECT_RGB);
		} else {
			view.setBorderColor(0, 0, 0);
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
		updateSelectionAppearance();
	}

	@Override
	public void dispose() {
		view.dispose();
	}

	public void recalculateBoundedArea() {
		// TODO
	}

	@Override
	public Point toPresentedLocation(int x, int y) {
		return new Point(x / CellController.SIZE, y / CellController.SIZE);
	}

	@Override
	public Point toPresentedSize(int w, int h) {
		return new Point(w / CellController.SIZE, h / CellController.SIZE);
	}

	@Override
	public Point toNormalLocation(int x, int y) {
		return new Point(x * CellController.SIZE, y * CellController.SIZE);
	}

	@Override
	public Point toNormalSize(int w, int h) {
		return new Point(w * CellController.SIZE, h * CellController.SIZE);
	}

	@Override
	public DataComposite getDataComposite(Composite parent) {
		return new ShipDataComposite(parent, this);
	}

	@Override
	public void redraw() {
		super.redraw();
		EditorWindow.getInstance().canvasRedraw(view.getHLineBounds());
		EditorWindow.getInstance().canvasRedraw(view.getVLineBounds());
	}

	@Override
	public boolean contains(int x, int y) {
		return model.contains(x, y) || view.getHLineBounds().contains(x, y) || view.getVLineBounds().contains(x, y);
	}

	@Override
	public boolean intersects(Rectangle rect) {
		return model.intersects(rect) || view.getHLineBounds().intersects(rect) || view.getVLineBounds().intersects(rect);
	}
}
