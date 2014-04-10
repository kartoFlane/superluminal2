package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal2.components.Triangle;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.core.Utils;
import com.kartoflane.superluminal2.ftl.RoomObject;
import com.kartoflane.superluminal2.mvc.controllers.SystemController;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;
import com.kartoflane.superluminal2.ui.ShipContainer;

public class RoomView extends BaseView {

	public static final int RESIZE_HANDLE_LENGTH = 19;
	public static final RGB WALL_RGB = new RGB(0, 0, 0);
	public static final RGB FLOOR_RGB = new RGB(230, 225, 220);

	private Color gridColor = null;
	private Color handleColor = null;
	private Triangle[] resizeHandles = null;

	public RoomView() {
		super();

		gridColor = Cache.checkOutColor(this, CellView.GRID_RGB);
		handleColor = Cache.checkOutColor(this, HIGHLIGHT_RGB);

		setBorderColor(WALL_RGB);
		setBackgroundColor(FLOOR_RGB);
		setBorderThickness(2);

		resizeHandles = new Triangle[4];
		for (int i = 0; i < 4; i++)
			resizeHandles[i] = new Triangle();
	}

	private ObjectModel getModel() {
		return (ObjectModel) model;
	}

	private RoomObject getGameObject() {
		return (RoomObject) getModel().getGameObject();
	}

	/**
	 * Visibility for the controller, to allow to check whether a triangle was clicked.
	 * 
	 * @return array of length 4, containing triangles representing the resize handles for the room.
	 */
	public Triangle[] getResizeHandles() {
		return resizeHandles;
	}

	@Override
	public void paintControl(PaintEvent e) {
		if (alpha > 0) {

			paintBackground(e, backgroundColor, alpha);

			// draw fake grid lines inside of rooms
			if (controller.getW() > ShipContainer.CELL_SIZE || controller.getH() > ShipContainer.CELL_SIZE) {
				Color prevFgColor = e.gc.getForeground();
				e.gc.setForeground(gridColor);
				paintFakeGridLines(e);
				e.gc.setForeground(prevFgColor);
			}

			// draw system
			SystemController systemC = Manager.getCurrentShip().getSystemController(getGameObject().getSystem());
			systemC.redraw(e);

			paintBorder(e, borderColor, borderThickness, alpha);

			paintResizeHandles(e);
		}
	}

	private void paintFakeGridLines(PaintEvent e) {
		if (controller.getW() > ShipContainer.CELL_SIZE)
			for (int i = 1; i < controller.getW() / ShipContainer.CELL_SIZE; i++)
				e.gc.drawLine(controller.getX() - controller.getW() / 2 + i * ShipContainer.CELL_SIZE,
						controller.getY() - controller.getH() / 2 + borderThickness,
						controller.getX() - controller.getW() / 2 + i * ShipContainer.CELL_SIZE,
						controller.getY() + controller.getH() / 2 - borderThickness);
		if (controller.getH() > ShipContainer.CELL_SIZE)
			for (int i = 1; i < controller.getH() / ShipContainer.CELL_SIZE; i++)
				e.gc.drawLine(controller.getX() - controller.getW() / 2 + borderThickness,
						controller.getY() - controller.getH() / 2 + i * ShipContainer.CELL_SIZE,
						controller.getX() + controller.getW() / 2 - borderThickness,
						controller.getY() - controller.getH() / 2 + i * ShipContainer.CELL_SIZE);
	}

	private void paintResizeHandles(PaintEvent e) {
		if (controller.isSelected() && !controller.isPinned()) {
			int prevAlpha = e.gc.getAlpha();
			Color prevColor = e.gc.getBackground();

			e.gc.setAlpha(alpha / 3);
			e.gc.setBackground(handleColor);

			for (int i = 0; i < 4; i++)
				resizeHandles[i].paintControl(e);

			e.gc.setAlpha(prevAlpha);
			e.gc.setBackground(prevColor);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		Cache.checkInColor(this, CellView.GRID_RGB);
		Cache.checkInColor(this, HIGHLIGHT_RGB);
		handleColor = null;
		gridColor = null;
	}

	@Override
	public void updateView() {
		if (controller.isSelected()) {
			setBorderColor(controller.isPinned() ? PIN_RGB : SELECT_RGB);
			setBackgroundColor(Utils.tint(FLOOR_RGB, borderColor.getRGB(), 0.33));
			setBorderThickness(2);
		} else if (controller.isHighlighted()) {
			setBorderColor(HIGHLIGHT_RGB);
			setBackgroundColor(FLOOR_RGB);
			setBorderThickness(3);
		} else {
			setBorderColor(WALL_RGB);
			setBackgroundColor(FLOOR_RGB);
			setBorderThickness(2);
		}

		updateResizeHandles();
	}

	private void updateResizeHandles() {
		Rectangle bounds = controller.getBounds();
		// redefine triangles for resize handles
		// top left corner
		int x = bounds.x + 1;
		int y = bounds.y + 1;
		resizeHandles[0].set(x, y, x + RESIZE_HANDLE_LENGTH, y, x, y + RESIZE_HANDLE_LENGTH);

		// top right corner
		x = bounds.x + controller.getW() + borderThickness - 1;
		resizeHandles[1].set(x, y, x - RESIZE_HANDLE_LENGTH, y, x, y + RESIZE_HANDLE_LENGTH);

		// bottom right corner
		y = bounds.y + controller.getH() + borderThickness - 1;
		resizeHandles[3].set(x, y, x - RESIZE_HANDLE_LENGTH, y, x, y - RESIZE_HANDLE_LENGTH);
		// yes, this is supposed to stay out of order -- this way we can access the triangle
		// on the opposite corner via resizeHandles[3 - id]

		// bottom left corner
		x = bounds.x + 1;
		resizeHandles[2].set(x, y, x + RESIZE_HANDLE_LENGTH, y, x, y - RESIZE_HANDLE_LENGTH);
	}
}
