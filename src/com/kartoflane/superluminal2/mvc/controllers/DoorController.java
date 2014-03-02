package com.kartoflane.superluminal2.mvc.controllers;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.components.interfaces.Alias;
import com.kartoflane.superluminal2.mvc.Model;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.models.DoorModel;
import com.kartoflane.superluminal2.mvc.views.DoorView;
import com.kartoflane.superluminal2.ui.sidebar.DataComposite;
import com.kartoflane.superluminal2.ui.sidebar.DoorDataComposite;

public class DoorController extends AbstractController implements Alias {

	protected DoorModel model;
	protected DoorView view;

	protected RoomController left = null;
	protected RoomController right = null;

	private DoorController(DoorModel model, DoorView view) {
		super();

		setModel(model);
		setView(view);

		setSelectable(true);
	}

	public static DoorController newInstance(ShipController shipController, boolean horizontal) {
		DoorModel model = new DoorModel();
		DoorView view = new DoorView();
		DoorController controller = new DoorController(model, view);
		controller.setHorizontal(horizontal);

		shipController.add(controller);

		return controller;
	}

	public static DoorController newInstance(ShipController shipController, DoorModel model) {
		DoorView view = new DoorView();
		DoorController controller = new DoorController(model, view);
		controller.setHorizontal(model.isHorizontal());

		shipController.add(controller);

		return controller;
	}

	public void setHorizontal(boolean horizontal) {
		model.setHorizontal(horizontal);
		model.setSize(horizontal ? 31 : 6, horizontal ? 6 : 31);
		view.setHorizontal(horizontal);
	}

	@Override
	public DoorModel getModel() {
		return model;
	}

	@Override
	public void setModel(Model model) {
		if (model == null)
			throw new IllegalArgumentException("Argument is null.");
		this.model = (DoorModel) model;
	}

	@Override
	public DoorView getView() {
		return view;
	}

	@Override
	public void setView(View view) {
		if (view == null)
			throw new IllegalArgumentException("Argument is null.");

		if (this.view != null)
			this.view.dispose();
		this.view = (DoorView) view;

		view.setController(this);
		this.view.addToPainter(Layers.DOOR);
	}

	public void setLeftRoomController(RoomController room) {
		model.setLeftRoom(room.getModel());
		left = room;
	}

	public void setRightRoomController(RoomController room) {
		model.setRightRoom(room.getModel());
		right = room;
	}

	public RoomController getLeftRoomController() {
		return left;
	}

	public RoomController getRightRoomController() {
		return right;
	}

	@Override
	public void setSize(int w, int h) {
		return;
	}

	@Override
	public void setLocation(int x, int y) {
		model.setLocation(x, y);
	}

	@Override
	public void translate(int dx, int dy) {
		model.translate(dx, dy);
	}

	@Override
	public void dispose() {
		view.dispose();
	}

	@Override
	public Point toPresentedLocation(int x, int y) {
		return new Point(x / 35, y / 35);
	}

	@Override
	public Point toPresentedSize(int w, int h) {
		return null;
	}

	@Override
	public Point toNormalLocation(int x, int y) {
		return new Point(x * 35, y * 35);
	}

	@Override
	public Point toNormalSize(int w, int h) {
		return null;
	}

	@Override
	public String getAlias() {
		return model.getAlias();
	}

	@Override
	public void setAlias(String alias) {
		model.setAlias(alias);
	}

	@Override
	public DataComposite getDataComposite(Composite parent) {
		return new DoorDataComposite(parent, this);
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
