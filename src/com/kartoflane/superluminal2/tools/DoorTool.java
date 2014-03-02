package com.kartoflane.superluminal2.tools;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.core.Utils;
import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.CellController;
import com.kartoflane.superluminal2.mvc.controllers.DoorController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;
import com.kartoflane.superluminal2.mvc.views.AbstractView;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.OverviewWindow;

public class DoorTool extends Tool {

	private static final RGB DENY_COLOR = AbstractView.DENY_RGB;
	private static final RGB ALLOW_COLOR = AbstractView.ALLOW_RGB;

	/** A flag indicating whether or not the door can be placed */
	private boolean canCreate = false;
	private boolean horizontal = false;

	public DoorTool(EditorWindow window) {
		super(window);
	}

	@Override
	public void select() {
		cursorView.setImage(null);
		cursorView.setBackgroundColor(null);
		cursorView.setBorderColor(ALLOW_COLOR);
		cursor.setSnapMode(Snapmodes.EDGES);

		cursor.setVisible(false);
		window.canvasRedraw(cursor.getBounds());
	}

	@Override
	public void deselect() {
		cursor.setVisible(false);
		window.canvasRedraw(cursor.getBounds());
	}

	@Override
	public DoorTool getInstance() {
		return (DoorTool) instance;
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
		if (e.button == 1 && canCreate) {
			ShipController shipController = Manager.getCurrentShip();
			DoorController door = DoorController.newInstance(shipController, horizontal);
			Rectangle oldBounds = door.getBounds();

			door.setLocation(Grid.getInstance().snapToGrid(e.x, e.y, cursor.getSnapMode()));
			door.getModel().setBounded(true);

			window.canvasRedraw(oldBounds);
			door.redraw();

			shipController.recalculateBoundedArea();
			OverviewWindow.getInstance().update();
		}
		// handle cursor
		if (cursor.isVisible() && e.button == 1) {
			cursor.setVisible(false);
			window.canvasRedraw(cursor.getBounds());
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		// handle cursor
		if (Grid.getInstance().isLocAccessible(e.x, e.y)) {
			if (e.button == 1)
				cursor.setVisible(true);

			Point p = Grid.getInstance().snapToGrid(e.x, e.y, cursor.getSnapMode());
			horizontal = p.y % CellController.SIZE == 0;
			updateSize(horizontal);
			cursor.reposition(p.x, p.y);
			canCreate = canCreate(p.x, p.y);
			cursorView.setBorderColor(canCreate ? ALLOW_COLOR : DENY_COLOR);

			window.canvasRedraw(cursor.getBounds());
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {
		// move the cursor around to follow mouse
		if (Grid.getInstance().isLocAccessible(e.x, e.y)) {
			cursor.setVisible(!Manager.leftMouseDown);

			Point p = Grid.getInstance().snapToGrid(e.x, e.y, cursor.getSnapMode());
			if (!p.equals(cursor.getLocation())) {
				horizontal = p.y % CellController.SIZE == 0;
				updateSize(horizontal);
				cursor.reposition(p.x, p.y);

				canCreate = canCreate(p.x, p.y);
				cursorView.setBorderColor(canCreate ? ALLOW_COLOR : DENY_COLOR);
			}
		} else if (cursor.isVisible()) {
			cursor.setVisible(false);
			window.canvasRedraw(cursor.getBounds());
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

	private void updateSize(boolean horizontal) {
		Rectangle oldBounds = cursor.getBounds();
		cursor.setSize(horizontal ? 34 : CellController.SIZE / 3, horizontal ? CellController.SIZE / 3 : 34);
		window.canvasRedraw(oldBounds);
	}

	private boolean canCreate(int x, int y) {
		Controller control = window.getPainter().getSelectableControllerAt(x, y);
		if (control != null && control instanceof RoomController) {
			return !Utils.contains(((AbstractController) control).getBounds(), cursor.getBounds());
		} else
			return false;
	}
}
