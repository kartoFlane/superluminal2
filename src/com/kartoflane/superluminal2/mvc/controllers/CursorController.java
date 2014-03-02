package com.kartoflane.superluminal2.mvc.controllers;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.mvc.Model;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.models.CursorModel;
import com.kartoflane.superluminal2.mvc.views.CursorView;

public class CursorController extends AbstractController {

	protected CursorModel model;
	protected CursorView view;

	private static CursorController instance = null;

	private CursorController(CursorModel model, CursorView view) {
		super();

		setModel(model);
		setView(view);

		setSelectable(false);

		instance = this;
	}

	public static CursorController newInstance() {
		if (instance != null)
			throw new IllegalStateException("CursorController already exists.");

		CursorModel model = new CursorModel();
		CursorView view = new CursorView();
		CursorController controller = new CursorController(model, view);

		return controller;
	}

	public static CursorController getInstance() {
		return instance;
	}

	@Override
	public CursorModel getModel() {
		return model;
	}

	@Override
	public void setModel(Model model) {
		if (model == null)
			throw new IllegalArgumentException("Argument is null.");
		this.model = (CursorModel) model;
	}

	@Override
	public CursorView getView() {
		return view;
	}

	@Override
	public void setView(View view) {
		if (view == null)
			throw new IllegalArgumentException("Argument is null.");

		if (this.view != null)
			this.view.dispose();
		this.view = (CursorView) view;

		view.setController(this);
		this.view.addToPainter(Layers.CURSOR);
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

	/*
	 * Override mouse events to do nothing;
	 * Cursor behaviour is implemented by tools.
	 */

	@Override
	public void mouseMove(MouseEvent e) {
		model.setMouseLocation(e.x, e.y);
	}

	@Override
	public void mouseDown(MouseEvent e) {
	}

	@Override
	public void mouseUp(MouseEvent e) {
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
	}

	@Override
	public void mouseEnter(MouseEvent e) {
	}

	@Override
	public void mouseExit(MouseEvent e) {
	}

	@Override
	public void mouseHover(MouseEvent e) {
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
