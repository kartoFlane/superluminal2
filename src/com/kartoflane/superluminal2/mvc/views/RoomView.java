package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.controllers.CellController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.mvc.controllers.SystemController;
import com.kartoflane.superluminal2.mvc.models.SystemModel.Systems;

public class RoomView extends AbstractView {

	public static final RGB WALL_RGB = new RGB(0, 0, 0);
	public static final RGB FLOOR_RGB = new RGB(230, 225, 220);

	protected RoomController controller;

	private Color highlightColor = null;
	private Color gridColor = null;
	private Color denyColor = null;

	public RoomView() {
		super();
		highlightColor = Cache.checkOutColor(this, HIGHLIGHT_RGB);
		gridColor = Cache.checkOutColor(this, CellView.GRID_RGB);
		denyColor = Cache.checkOutColor(this, DENY_RGB);

		setBorderColor(WALL_RGB);
		setBackgroundColor(FLOOR_RGB);

		setBorderThickness(2);
	}

	@Override
	public void paintControl(PaintEvent e) {
		if (alpha > 0) {
			int prevAlpha = e.gc.getAlpha();
			Color prevFgColor = e.gc.getForeground();

			e.gc.setAlpha(alpha);

			paintBackground(e, backgroundColor, alpha);

			// draw fake grid lines inside of rooms
			if (getW() > CellController.SIZE || getH() > CellController.SIZE) {
				e.gc.setForeground(gridColor);
				if (getW() > CellController.SIZE)
					for (int i = 1; i < getW() / CellController.SIZE; i++)
						e.gc.drawLine(getX() - getW() / 2 + i * CellController.SIZE, getY() - getH() / 2 + borderThickness,
								getX() - getW() / 2 + i * CellController.SIZE, getY() + getH() / 2 - borderThickness);
				if (getH() > CellController.SIZE)
					for (int i = 1; i < getH() / CellController.SIZE; i++)
						e.gc.drawLine(getX() - getW() / 2 + borderThickness, getY() - getH() / 2 + i * CellController.SIZE,
								getX() + getW() / 2 - borderThickness, getY() - getH() / 2 + i * CellController.SIZE);
			}

			paintBorder(e, borderColor, borderThickness, alpha);

			// draw system
			SystemController systemC = controller.getSystemController();
			if (systemC != null && systemC.getModel().getId() != Systems.EMPTY)
				systemC.getView().redraw(e);

			paintResizeHandles(e);
			if (controller.isResizing())
				paintResizeBounds(e);

			e.gc.setAlpha(prevAlpha);
			e.gc.setForeground(prevFgColor);
		}
	}

	protected void paintResizeHandles(PaintEvent e) {
		if (controller.isSelected() && !controller.isPinned()) {
			int prevAlpha = e.gc.getAlpha();
			Color prevColor = e.gc.getBackground();

			e.gc.setAlpha(alpha / 3);
			e.gc.setBackground(highlightColor);

			for (int i = 0; i < 4; i++)
				controller.getResizeHandles()[i].paintControl(e);

			e.gc.setAlpha(prevAlpha);
			e.gc.setBackground(prevColor);
		}
	}

	protected void paintResizeBounds(PaintEvent e) {
		if (controller.isSelected()) {
			int prevAlpha = e.gc.getAlpha();
			Color prevColor = e.gc.getBackground();

			e.gc.setAlpha(alpha / 3);
			e.gc.setBackground(controller.canResize() ? highlightColor : denyColor);

			e.gc.fillRectangle(controller.getResizeBounds());

			e.gc.setAlpha(prevAlpha);
			e.gc.setBackground(prevColor);
		}
	}

	@Override
	public RoomController getController() {
		return controller;
	}

	@Override
	public void setController(Controller controller) {
		this.controller = (RoomController) controller;
	}

	@Override
	public void dispose() {
		super.dispose();

		Cache.checkInColor(this, highlightColor.getRGB());
		Cache.checkInColor(this, CellView.GRID_RGB);
		Cache.checkInColor(this, DENY_RGB);

		gridColor = null;
		highlightColor = null;
		denyColor = null;
	}
}
