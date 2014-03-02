package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;

import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.controllers.CursorController;

public class CursorView extends AbstractView {

	protected CursorController controller = null;

	private int borderThickness = 1;

	public CursorView() {
		super();
	}

	public void setBorderThickness(int borderThickness) {
		this.borderThickness = borderThickness;
	}

	public int getBorderThickness() {
		return borderThickness;
	}

	@Override
	public void paintControl(PaintEvent e) {
		if (alpha > 0) {
			paintBackground(e, backgroundColor, alpha);
			paintImage(e, image, alpha);
			paintBorder(e, borderColor, borderThickness, alpha);
		}
	}

	@Override
	public CursorController getController() {
		return controller;
	}

	@Override
	public void setController(Controller controller) {
		this.controller = (CursorController) controller;
	}

	@Override
	public void dispose() {
		super.dispose();

		if (image != null)
			Cache.checkInImage(this, getImagePath());

		backgroundColor = null;
		borderColor = null;
		image = null;
	}
}
