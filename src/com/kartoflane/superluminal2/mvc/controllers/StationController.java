package com.kartoflane.superluminal2.mvc.controllers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.mvc.Model;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.models.StationModel;
import com.kartoflane.superluminal2.mvc.views.StationView;

public class StationController extends AbstractController {

	protected StationModel model;
	protected StationView view;

	protected SystemController system;

	private StationController(StationModel model, StationView view) {
		super();

		setModel(model);
		setView(view);

		setSelectable(false);
	}

	/**
	 * Creates a new object represented by the MVC system, ie.
	 * a new Controller associated with the system's StationModel and a new View object
	 */
	public static StationController newInstance(SystemController system) {
		StationModel model = system.getModel().getStation();
		StationView view = new StationView();
		StationController controller = new StationController(model, view);

		controller.system = system;
		return controller;
	}

	public void setSlotId(int slot) {
		model.setSlotId(slot);
	}

	/** Valid values: SWT.UP, SWT.RIGHT, SWT.DOWN, SWT.LEFT, SWT.NONE */
	public void setSlotDirection(int dir) {
		if (dir != SWT.UP && dir != SWT.RIGHT && dir != SWT.DOWN && dir != SWT.LEFT && dir != SWT.NONE)
			throw new IllegalArgumentException("Illegal value supplied; only SWT.UP, SWT.RIGHT, SWT.DOWN, SWT.LEFT or SWT.NONE");
		model.setSlotDirection(dir);
	}

	@Override
	public StationModel getModel() {
		return model;
	}

	@Override
	public void setModel(Model model) {
		if (model == null)
			throw new IllegalArgumentException("Argument is null.");
		this.model = (StationModel) model;
	}

	@Override
	public StationView getView() {
		return view;
	}

	@Override
	public void setView(View view) {
		if (view == null)
			throw new IllegalArgumentException("Argument is null.");

		if (this.view != null)
			this.view.dispose();
		this.view = (StationView) view;

		view.setController(this);
		this.view.addToPainter(Layers.STATION);
	}

	@Override
	public void setSize(int w, int h) {
		model.setSize(w, h);
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
