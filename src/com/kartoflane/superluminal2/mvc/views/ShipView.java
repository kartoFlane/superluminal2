package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Line;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;

public class ShipView extends AbstractView {

	protected ShipController controller = null;

	private Color anchorColor = null;
	private Color horizontalColor = null;
	private Color verticalColor = null;

	private Line lineH = null;
	private Line lineV = null;

	public ShipView() {
		super();

		anchorColor = Cache.checkOutColor(this, new RGB(0, 255, 255));
		horizontalColor = Cache.checkOutColor(this, new RGB(255, 0, 0));
		verticalColor = Cache.checkOutColor(this, new RGB(0, 255, 0));
		setBorderColor(0, 0, 0);
		setBorderThickness(3);

		lineH = new Line(0, 0, 0, 0);
		lineV = new Line(0, 0, 0, 0);
	}

	@Override
	public ShipController getController() {
		return controller;
	}

	@Override
	public void setController(Controller controller) {
		this.controller = (ShipController) controller;
	}

	public void updateLines() {
		Point gridSize = Grid.getInstance().getSize();
		lineH.setStart(getX(), getY());
		lineH.setEnd(gridSize.x, getY());
		lineV.setStart(getX(), getY());
		lineV.setEnd(getX(), gridSize.y);
	}

	public Rectangle getHLineBounds() {
		return lineH.getBounds();
	}

	public Rectangle getVLineBounds() {
		return lineV.getBounds();
	}

	@Override
	public void paintControl(PaintEvent e) {
		if (alpha > 0) {
			Color prevFgColor = e.gc.getForeground();

			e.gc.setForeground(horizontalColor);
			lineH.paintControl(e);
			e.gc.setForeground(verticalColor);
			lineV.paintControl(e);

			paintBackground(e, anchorColor, alpha);
			paintBorder(e, borderColor, getBorderThickness(), alpha);

			e.gc.setForeground(prevFgColor);
		}
	}

	@Override
	public void dispose() {
		super.dispose();

		Cache.checkInColor(this, anchorColor.getRGB());
		Cache.checkInColor(this, horizontalColor.getRGB());
		Cache.checkInColor(this, verticalColor.getRGB());
		anchorColor = null;
		horizontalColor = null;
		verticalColor = null;
	}
}
