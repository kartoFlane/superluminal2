package com.kartoflane.superluminal2.mvc.controllers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.components.interfaces.Alias;
import com.kartoflane.superluminal2.mvc.Model;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.models.MountModel;
import com.kartoflane.superluminal2.mvc.views.MountView;
import com.kartoflane.superluminal2.ui.sidebar.DataComposite;
import com.kartoflane.superluminal2.ui.sidebar.MountDataComposite;

public class MountController extends AbstractController implements Alias, Comparable<MountController> {

	protected MountModel model;
	protected MountView view;

	private MountController(MountModel model, MountView view) {
		super();

		setModel(model);
		setView(view);

		setSelectable(true);
	}

	public static MountController newInstance(ShipController shipController) {
		MountModel model = new MountModel(shipController.getNextMountId());
		MountView view = new MountView();
		MountController controller = new MountController(model, view);

		shipController.add(controller);

		// setId() in controller TODO

		return controller;
	}

	public static MountController newInstance(ShipController shipController, MountModel model) {
		MountView view = new MountView();
		MountController controller = new MountController(model, view);

		shipController.add(controller);

		return controller;
	}

	@Override
	public MountModel getModel() {
		return model;
	}

	@Override
	public void setModel(Model model) {
		if (model == null)
			throw new IllegalArgumentException("Argument is null.");
		this.model = (MountModel) model;
	}

	@Override
	public MountView getView() {
		return view;
	}

	@Override
	public void setView(View view) {
		if (view == null)
			throw new IllegalArgumentException("Argument is null.");

		if (this.view != null)
			this.view.dispose();
		this.view = (MountView) view;

		view.setController(this);
		this.view.addToPainter(Layers.MOUNT);
	}

	public void setRotated(boolean rotated) {
		model.setRotated(rotated);
		// TODO ?
	}

	public void setMirrored(boolean mirrored) {
		model.setMirrored(mirrored);
		// TODO ?
	}

	public void setDirection(int direction) {
		switch (direction) {
			case SWT.UP:
			case SWT.LEFT:
			case SWT.RIGHT:
			case SWT.DOWN:
			case SWT.NONE:
				model.setDirection(direction);
				break;
			default:
				throw new IllegalArgumentException("Unknown direction: " + direction);
		}
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
		return new Point(x, y);
	}

	@Override
	public Point toPresentedSize(int w, int h) {
		return new Point(w, h);
	}

	@Override
	public Point toNormalLocation(int x, int y) {
		return new Point(x, y);
	}

	@Override
	public Point toNormalSize(int w, int h) {
		return new Point(w, h);
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
	public int compareTo(MountController o) {
		return model.compareTo(o.model);
	}

	@Override
	public DataComposite getDataComposite(Composite parent) {
		return new MountDataComposite(parent, this);
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
