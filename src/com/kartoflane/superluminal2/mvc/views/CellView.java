package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.controllers.CellController;

public class CellView extends AbstractView {

	public static final RGB GRID_RGB = new RGB(112, 112, 112);

	protected CellController controller = null;

	private Color gridColor = null;
	private int borderThickness = 0;

	public CellView() {
		super();

		gridColor = Cache.checkOutColor(this, GRID_RGB);
	}

	@Override
	public CellController getController() {
		return controller;
	}

	@Override
	public void setController(Controller controller) {
		this.controller = (CellController) controller;
	}

	@Override
	public void paintControl(PaintEvent e) {
		if (alpha > 0) {
			int prevAlpha = e.gc.getAlpha();
			Color prevFgColor = e.gc.getForeground();

			paintBorder(e, gridColor, borderThickness, alpha);

			e.gc.setAlpha(prevAlpha);
			e.gc.setForeground(prevFgColor);
		}
	}

	@Override
	public void dispose() {
		super.dispose();

		Cache.checkInColor(this, GRID_RGB);
		gridColor = null;
	}
}
