package com.kartoflane.superluminal2.tools;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.CellController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.mvc.controllers.StationController;
import com.kartoflane.superluminal2.mvc.views.AbstractView;
import com.kartoflane.superluminal2.ui.EditorWindow;

public class StationTool extends Tool {

	private static final RGB DENY_RGB = AbstractView.DENY_RGB;
	private static final RGB ALLOW_RGB = AbstractView.ALLOW_RGB;

	public StationTool(EditorWindow window) {
		super(window);
	}

	@Override
	public void select() {
		resetCursor();
	}

	@Override
	public void deselect() {
	}

	@Override
	public StationTool getInstance() {
		return (StationTool) instance;
	}

	@Override
	public Composite getToolComposite(Composite parent) {
		return null; // TODO
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (e.button == 1) {
			// station assignment
			RoomController roomController = (RoomController) window.getPainter().getControllerAt(e.x, e.y, Layers.ROOM);
			if (roomController != null && roomController.isVisible() && roomController.getModel().getSystem().canContainStation()) {
				StationController station = roomController.getSystemController().getStationController();
				// correction to pick the correct slot
				station.getModel().setSlotId(roomController.getModel().getSlotId(e.x - 2, e.y - 2));
				// station.updateSlot();
				window.canvasRedraw(roomController.getBounds());
			}
		} else if (e.button == 3) {
			// station removal
			RoomController roomController = (RoomController) window.getPainter().getControllerAt(e.x, e.y, Layers.ROOM);
			if (roomController != null && roomController.isVisible() && roomController.getModel().getSystem().canContainStation()) {
				StationController station = roomController.getSystemController().getStationController();
				station.setSlotId(-2);
				// station.updateSlot();
				window.canvasRedraw(roomController.getBounds());
			}
		}

		// handle cursor
		if (cursor.isVisible() && e.button == 1) {
			cursor.setVisible(false);
			window.canvasRedraw(cursor.getBounds());
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		if (e.button == 1) {
		} else if (e.button == 3) {
		}

		// handle cursor
		if (!cursor.isVisible() && Grid.getInstance().isLocAccessible(e.x + 1, e.y + 1)) {
			if (e.button == 1)
				cursor.setVisible(true);

			Point p = Grid.getInstance().snapToGrid(e.x + 1, e.y + 1, cursor.getSnapMode());
			if (!p.equals(cursor.getLocation()))
				cursor.reposition(p.x, p.y);
			window.canvasRedraw(cursor.getBounds());
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {
		// move the cursor around to follow mouse
		// (1, 1) vector correction so that the cursor shows up on the correct grid cell
		if (Grid.getInstance().isLocAccessible(e.x + 1, e.y + 1)) {
			cursor.setVisible(!Manager.leftMouseDown);
			Point p = Grid.getInstance().snapToGrid(e.x + 1, e.y + 1, cursor.getSnapMode());
			// always redraw it: prevents an odd visual bug where the box sometimes doesn't register it was moved
			cursor.reposition(p.x, p.y);
		} else if (cursor.isVisible()) {
			cursor.setVisible(false);
			window.canvasRedraw(cursor.getBounds());
		}

		RoomController roomController = (RoomController) window.getPainter().getControllerAt(e.x, e.y, Layers.ROOM);
		if (roomController != null && roomController.isVisible()) {
			if (roomController.getModel().getSystem().canContainStation() && cursorView.getBorderRGB() != ALLOW_RGB)
				cursorView.setBorderColor(ALLOW_RGB);
			else if (!roomController.getModel().getSystem().canContainStation() && cursorView.getBorderRGB() != DENY_RGB)
				cursorView.setBorderColor(DENY_RGB);
		} else if (cursorView.getBorderRGB() != DENY_RGB) {
			cursorView.setBorderColor(DENY_RGB);
		}
	}

	@Override
	public void mouseEnter(MouseEvent e) {
		cursor.setVisible(!Manager.leftMouseDown);
		window.canvasRedraw(cursor.getBounds());
	}

	@Override
	public void mouseExit(MouseEvent e) {
		cursor.setVisible(false);
		window.canvasRedraw(cursor.getBounds());
	}

	@Override
	public void mouseHover(MouseEvent e) {
	}

	private void resetCursor() {
		cursorView.setImage(null);
		cursorView.setBackgroundColor(null);
		cursorView.setBorderColor(ALLOW_RGB);
		cursorView.setAlpha(255);
		cursor.setSnapMode(Snapmodes.CELL);

		Rectangle oldBounds = cursor.getBounds();
		cursor.setSize(CellController.SIZE, CellController.SIZE);
		window.canvasRedraw(oldBounds);
	}
}