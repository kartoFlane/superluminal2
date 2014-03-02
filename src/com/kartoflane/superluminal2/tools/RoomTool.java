package com.kartoflane.superluminal2.tools;

import java.util.ArrayList;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.CellController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;
import com.kartoflane.superluminal2.mvc.views.AbstractView;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.OverviewWindow;
import com.kartoflane.superluminal2.ui.sidebar.RoomToolComposite;

public class RoomTool extends Tool {
	public static final int COLLISION_TOLERANCE = CellController.SIZE / 2;

	private static final RGB DENY_RGB = AbstractView.DENY_RGB;
	private static final RGB ALLOW_RGB = AbstractView.ALLOW_RGB;

	// room creation helper variables
	private Point createOrigin;
	private Rectangle createBounds;
	private boolean canCreate = false;
	/** A flag indicating whether or not the user is in the process of creating a new room */
	private boolean creating = false;

	public RoomTool(EditorWindow window) {
		super(window);

		createBounds = new Rectangle(0, 0, 0, 0);
	}

	@Override
	public void select() {
		resetCursor();

		cursor.setVisible(false);
		window.canvasRedraw(cursor.getBounds());
	}

	@Override
	public void deselect() {
		cursor.setVisible(false);
		window.canvasRedraw(cursor.getBounds());
	}

	@Override
	public RoomTool getInstance() {
		return (RoomTool) instance;
	}

	@Override
	public Composite getToolComposite(Composite parent) {
		return new RoomToolComposite(parent);
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (e.button == 1 && EditorWindow.getInstance().isFocusControl()) {
			// room creation begin
			if (Grid.getInstance().isLocAccessible(e.x + 1, e.y + 1)) {
				setCreationCursor();

				createOrigin = Grid.getInstance().snapToGrid(e.x + 1, e.y + 1, Snapmodes.CORNER_TL);

				createBounds.x = createOrigin.x;
				createBounds.y = createOrigin.y;
				createBounds.width = CellController.SIZE;
				createBounds.height = CellController.SIZE;

				creating = true;
				mouseMove(e);
			}
		} else if (e.button == 3) {
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		if (e.button == 1) {
			if (creating) {
				// room creation finalize
				createBounds = cursor.getBounds();
				resetCursor();
				cursor.setVisible(false);

				if (canCreate) {
					// if the area is clear, create the room
					ShipController shipController = Manager.getCurrentShip();

					RoomController roomController = RoomController.newInstance(shipController);

					// round the size to match grid cell size (bounds are usually off by a pixel or two as an error margin)
					Point p = Grid.getInstance().snapToGrid(createBounds.width, createBounds.height, Snapmodes.CROSS);
					createBounds.width = p.x;
					createBounds.height = p.y;
					p = Grid.getInstance().snapToGrid(createBounds.x, createBounds.y, Snapmodes.CROSS);
					createBounds.x = p.x;
					createBounds.y = p.y;

					p = Grid.getInstance().snapToGrid(createBounds.x + createBounds.width / 2,
							createBounds.y + createBounds.height / 2, roomController.getSnapMode());
					roomController.setSize(createBounds.width, createBounds.height);
					roomController.setLocation(p.x, p.y);

					p = roomController.getLocation();
					roomController.setFollowOffset(p.x - roomController.getParent().getX(), p.y - roomController.getParent().getY());

					shipController.recalculateBoundedArea();

					window.canvasRedraw(createBounds);
					OverviewWindow.getInstance().update(roomController);
				} else {
					// if collided with a room, just redraw the area (createBox isn't visible anymore)
					window.canvasRedraw(createBounds);
				}

				creating = false;
			}
		} else if (e.button == 3) {
			// room creation abort
			if (creating) {
				createBounds = cursor.getBounds();
				resetCursor();
				cursor.setVisible(false);
				window.canvasRedraw(createBounds);

				creating = false;
			}
		}

		// handle cursor
		if (!cursor.isVisible() && Grid.getInstance().isLocAccessible(e.x + 1, e.y + 1)) {
			if (e.button == 1)
				cursor.setVisible(true);

			Rectangle anchorBounds = Manager.getCurrentShip().getBounds();
			boolean result = cursor.getX() > anchorBounds.x && cursor.getY() > anchorBounds.y;
			if (result)
				result = window.getPainter().getControllerAt(e.x + 1, e.y + 1, Layers.ROOM) == null;

			if (result && cursorView.getBorderRGB() != ALLOW_RGB)
				cursorView.setBorderColor(ALLOW_RGB);
			else if (!result && cursorView.getBorderRGB() != DENY_RGB)
				cursorView.setBorderColor(DENY_RGB);

			Point p = Grid.getInstance().snapToGrid(e.x, e.y, cursor.getSnapMode());
			if (!p.equals(cursor.getLocation()))
				cursor.reposition(p.x, p.y);
			window.canvasRedraw(cursor.getBounds());
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if (creating) {
			// update room creation rectangle
			if (!Grid.getInstance().isLocAccessible(e.x, e.y))
				return;

			Snapmodes snapmode = (e.x >= createOrigin.x && e.y >= createOrigin.y ? Snapmodes.CORNER_BR :
					(e.x >= createOrigin.x && e.y < createOrigin.y ? Snapmodes.CORNER_TR :
							(e.x < createOrigin.x && e.y >= createOrigin.y ? Snapmodes.CORNER_BL :
									Snapmodes.CORNER_TL)));

			Point cursor = Grid.getInstance().snapToGrid(e.x + 1, e.y + 1, snapmode);
			Point anchor = new Point(0, 0);

			anchor.x = createOrigin.x + (cursor.x < createOrigin.x ? CellController.SIZE : 0);
			anchor.y = createOrigin.y + (cursor.y < createOrigin.y ? CellController.SIZE : 0);
			anchor = Grid.getInstance().snapToGrid(anchor.x, anchor.y, Snapmodes.CROSS);

			Rectangle oldCreateBounds = this.cursor.getBounds();

			createBounds.x = Math.min(anchor.x, cursor.x);
			createBounds.y = Math.min(anchor.y, cursor.y);
			createBounds.width = Math.max(Math.abs(anchor.x - cursor.x), CellController.SIZE);
			createBounds.height = Math.max(Math.abs(anchor.y - cursor.y), CellController.SIZE);

			// only redraw when we actually have to
			if (!oldCreateBounds.equals(createBounds)) {
				canCreate = canCreate();

				this.cursorView.setBackgroundColor(canCreate ? ALLOW_RGB : DENY_RGB);
				this.cursor.setSize(createBounds.width, createBounds.height);
				this.cursor.setLocation(createBounds.x + createBounds.width / 2, createBounds.y + createBounds.height / 2);

				window.canvasRedraw(createBounds);
				window.canvasRedraw(oldCreateBounds);
			}
		} else {
			// move the cursor around to follow mouse
			if (Grid.getInstance().isLocAccessible(e.x + 1, e.y + 1)) {
				cursor.setVisible(!Manager.leftMouseDown);
				Point p = Grid.getInstance().snapToGrid(e.x, e.y, cursor.getSnapMode());
				// always redraw it: prevents an odd visual bug where the box sometimes doesn't register it was moved
				cursor.reposition(p.x, p.y);
			} else if (cursor.isVisible()) {
				cursor.setVisible(false);
				window.canvasRedraw(cursor.getBounds());
			}

			// check whether the cursor is beyond the anchor, and color it accordingly
			Rectangle anchorBounds = Manager.getCurrentShip().getBounds();
			boolean result = cursor.getX() > anchorBounds.x && cursor.getY() > anchorBounds.y;
			if (result)
				result = window.getPainter().getControllerAt(e.x, e.y, Layers.ROOM) == null;

			if (result && cursorView.getBorderRGB() != ALLOW_RGB)
				cursorView.setBorderColor(ALLOW_RGB);
			else if (!result && cursorView.getBorderRGB() != DENY_RGB)
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

	private boolean canCreate() {
		Rectangle anchorBounds = Manager.getCurrentShip().getBounds();
		if (createBounds.x <= anchorBounds.x || createBounds.y <= anchorBounds.y)
			return false;

		boolean collided = false;
		ArrayList<AbstractController> layer = window.getPainter().getLayerMap().get(Layers.ROOM);
		for (int i = 0; i < layer.size() && !collided; i++) {
			AbstractController c = layer.get(i);
			if (c.isVisible() && c.collides(createBounds.x + COLLISION_TOLERANCE, createBounds.y + COLLISION_TOLERANCE,
					createBounds.width - 2 * COLLISION_TOLERANCE, createBounds.height - 2 * COLLISION_TOLERANCE))
				collided = true;
		}
		return !collided;
	}

	/** Changes the cursor appearance to creation. */
	private void setCreationCursor() {
		cursorView.setImage(null);
		cursorView.setBackgroundColor(ALLOW_RGB);
		cursorView.setBorderColor(null);
		cursorView.setAlpha(255 / 3);

		cursor.setSnapMode(Snapmodes.CELL);
		cursor.setSize(CellController.SIZE, CellController.SIZE);
		cursor.reposition(Grid.getInstance().snapToGrid(cursor.getLocation(), cursor.getSnapMode()));
	}

	/** Resets the cursor appearance to the default state for this tool. */
	private void resetCursor() {
		cursorView.setImage(null);
		cursorView.setBackgroundColor(null);
		cursorView.setBorderColor(AbstractView.ALLOW_RGB);
		cursorView.setBorderThickness(3);
		cursorView.setAlpha(255);

		cursor.setSnapMode(Snapmodes.CELL);
		cursor.setSize(CellController.SIZE, CellController.SIZE);
		cursor.reposition(Grid.getInstance().snapToGrid(cursor.getLocation(), cursor.getSnapMode()));
	}
}
