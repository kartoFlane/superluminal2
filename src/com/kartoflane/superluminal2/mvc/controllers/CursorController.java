package com.kartoflane.superluminal2.mvc.controllers;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.mvc.BaseModel;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.views.CursorView;

public class CursorController extends AbstractController {

	private static CursorController instance = null;

	private int mouseX = 0;
	private int mouseY = 0;

	private CursorController(BaseModel model, CursorView view) {
		super();
		setModel(model);
		setView(view);

		setSelectable(false);
		setBounded(false);

		instance = this;
	}

	public static CursorController newInstance() {
		if (instance != null)
			throw new IllegalStateException("Cursor already exists.");

		BaseModel model = new BaseModel();
		CursorView view = new CursorView();
		CursorController controller = new CursorController(model, view);

		return controller;
	}

	public static CursorController getInstance() {
		return instance;
	}

	@Override
	public void setView(View view) {
		super.setView(view);
		this.view.addToPainter(Layers.CURSOR);
	}

	public Point getMouseLocation() {
		return new Point(mouseX, mouseY);
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	/*
	 * Override mouse events to do nothing;
	 * Cursor behaviour is implemented by tools.
	 */

	@Override
	public void mouseMove(MouseEvent e) {
		mouseX = e.x;
		mouseY = e.y;
	}

	@Override
	public void mouseDown(MouseEvent e) {
		mouseX = e.x;
		mouseY = e.y;
	}

	@Override
	public void mouseUp(MouseEvent e) {
		mouseX = e.x;
		mouseY = e.y;
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
}
