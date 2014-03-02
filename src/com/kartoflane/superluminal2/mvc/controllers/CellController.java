package com.kartoflane.superluminal2.mvc.controllers;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.mvc.Model;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.models.CellModel;
import com.kartoflane.superluminal2.mvc.views.CellView;

public class CellController extends AbstractController {

	/** The size of a single cell. Both width and height are equal to this value. */
	public static final int SIZE = 35;

	protected CellModel model;
	protected CellView view;

	private CellController(CellModel model, CellView view) {
		setModel(model);
		setView(view);

		setSelectable(false);
	}

	public static CellController newInstance(int i, int j) {
		CellModel model = new CellModel(i, j);
		CellView view = new CellView();
		CellController controller = new CellController(model, view);

		// 1px bigger, since cells are overlapping at borders
		controller.setSize(SIZE + 1, SIZE + 1);
		controller.setLocation(SIZE / 2 + 1 + i * SIZE, SIZE / 2 + 1 + j * SIZE);

		return controller;
	}

	@Override
	public CellModel getModel() {
		return model;
	}

	@Override
	public void setModel(Model model) {
		if (model == null)
			throw new IllegalArgumentException("Argument is null.");
		this.model = (CellModel) model;
	}

	@Override
	public CellView getView() {
		return view;
	}

	@Override
	public void setView(View view) {
		if (view == null)
			throw new IllegalArgumentException("Argument is null.");

		if (this.view != null)
			this.view.dispose();
		this.view = (CellView) view;

		view.setController(this);
		this.view.addToPainter(Layers.GRID);
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
	public boolean contains(int x, int y) {
		return model.contains(x, y);
	}

	@Override
	public boolean intersects(Rectangle rect) {
		return model.intersects(rect);
	}

	@Override
	public Point toPresentedLocation(int x, int y) {
		return new Point(x / 35, y / 35);
	}

	@Override
	public Point toPresentedSize(int w, int h) {
		return new Point(w / 35, h / 35);
	}

	@Override
	public Point toNormalLocation(int x, int y) {
		return new Point(x * 35, y * 35);
	}

	@Override
	public Point toNormalSize(int w, int h) {
		return new Point(w * 35, h * 35);
	}
}
