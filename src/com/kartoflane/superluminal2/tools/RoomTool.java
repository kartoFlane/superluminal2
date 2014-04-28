package com.kartoflane.superluminal2.tools;

import java.util.ArrayList;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.components.LayeredPainter;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ftl.RoomObject;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.OverviewWindow;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.RoomToolComposite;

public class RoomTool extends Tool {
	public static final int COLLISION_TOLERANCE = ShipContainer.CELL_SIZE / 2;

	// Room creation helper variables
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
		cursor.setSize(ShipContainer.CELL_SIZE, ShipContainer.CELL_SIZE);
		cursor.setSnapMode(Snapmodes.CELL);
		cursor.updateView();
		cursor.setVisible(false);

		CreationTool ctool = (CreationTool) Manager.getTool(Tools.CREATOR);
		ctool.getToolComposite(null).clearDataContainer();
		Composite dataC = ctool.getToolComposite(null).getDataContainer();
		createToolComposite(dataC);
		dataC.layout(true);
	}

	@Override
	public void deselect() {
		cursor.updateView();
		cursor.setVisible(false);

		CreationTool ctool = (CreationTool) Manager.getTool(Tools.CREATOR);
		ctool.getToolComposite(null).clearDataContainer();
	}

	@Override
	public RoomToolComposite getToolComposite(Composite parent) {
		return (RoomToolComposite) super.getToolComposite(parent);
	}

	@Override
	public RoomToolComposite createToolComposite(Composite parent) {
		if (parent == null)
			throw new IllegalArgumentException("Parent must not be null.");
		compositeInstance = new RoomToolComposite(parent);
		return (RoomToolComposite) compositeInstance;
	}

	public boolean isCreating() {
		return creating;
	}

	public boolean canCreate() {
		return canCreate;
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
	}

	@Override
	public void mouseDown(MouseEvent e) {
		// Check the conditions again in case the variable is outdated
		canCreate = canCreate(cursor.getDimensions());

		if (e.button == 1 && EditorWindow.getInstance().isFocusControl()) {
			// room creation begin
			if (Grid.getInstance().isLocAccessible(e.x, e.y)) {
				createOrigin = Grid.getInstance().snapToGrid(e.x, e.y, Snapmodes.CORNER_TL);

				createBounds.x = createOrigin.x;
				createBounds.y = createOrigin.y;
				createBounds.width = ShipContainer.CELL_SIZE;
				createBounds.height = ShipContainer.CELL_SIZE;

				creating = true;
				cursor.updateView();
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

				if (canCreate) {
					// if the area is clear, create the room
					ShipContainer container = Manager.getCurrentShip();
					RoomObject object = new RoomObject();
					RoomController roomController = RoomController.newInstance(container, object);

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

					container.add(roomController);

					roomController.updateFollowOffset();
					container.updateBoundingArea();

					window.canvasRedraw(createBounds);
					OverviewWindow.getInstance().update(roomController);
				} else {
					// if collided with a room, just redraw the area (cursor isn't visible)
					window.canvasRedraw(createBounds);
				}

				creating = false;
				canCreate = canCreate(cursor.getDimensions());

				cursor.updateView();
				cursor.resize(ShipContainer.CELL_SIZE, ShipContainer.CELL_SIZE);
				cursor.reposition(Grid.getInstance().snapToGrid(e.x, e.y, cursor.getSnapMode()));
				cursor.setVisible(true);
			}
		} else if (e.button == 3) {
			// room creation abort
			if (creating) {
				creating = false;
				canCreate = canCreate(cursor.getDimensions());

				cursor.updateView();
				cursor.resize(ShipContainer.CELL_SIZE, ShipContainer.CELL_SIZE);
				cursor.reposition(Grid.getInstance().snapToGrid(e.x, e.y, cursor.getSnapMode()));
				cursor.setVisible(true);
			}
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

			anchor.x = createOrigin.x + (cursor.x < createOrigin.x ? ShipContainer.CELL_SIZE : 0);
			anchor.y = createOrigin.y + (cursor.y < createOrigin.y ? ShipContainer.CELL_SIZE : 0);
			anchor = Grid.getInstance().snapToGrid(anchor.x, anchor.y, Snapmodes.CROSS);

			Rectangle oldCreateBounds = this.cursor.getBounds();

			createBounds.x = Math.min(anchor.x, cursor.x);
			createBounds.y = Math.min(anchor.y, cursor.y);
			createBounds.width = Math.max(Math.abs(anchor.x - cursor.x), ShipContainer.CELL_SIZE);
			createBounds.height = Math.max(Math.abs(anchor.y - cursor.y), ShipContainer.CELL_SIZE);

			// only redraw when we actually have to
			if (!oldCreateBounds.equals(createBounds)) {
				canCreate = canCreate(createBounds);

				this.cursor.updateView();
				this.cursor.setSize(createBounds.width, createBounds.height);
				this.cursor.reposition(createBounds.x + createBounds.width / 2, createBounds.y + createBounds.height / 2);
				window.canvasRedraw(oldCreateBounds);
			}
		} else {
			// move the cursor around to follow mouse
			if (Grid.getInstance().isLocAccessible(e.x, e.y)) {
				cursor.setVisible(!Manager.leftMouseDown);
				Point p = Grid.getInstance().snapToGrid(e.x, e.y, cursor.getSnapMode());
				// always redraw it: prevents an odd visual bug where the box sometimes doesn't register it was moved
				cursor.reposition(p.x, p.y);
			} else if (cursor.isVisible()) {
				cursor.setVisible(false);
				cursor.redraw();
			}

			canCreate = canCreate(cursor.getDimensions());
		}

		cursor.updateView();
	}

	@Override
	public void mouseEnter(MouseEvent e) {
		cursor.setVisible(!Manager.leftMouseDown);
		cursor.redraw();
	}

	@Override
	public void mouseExit(MouseEvent e) {
		cursor.setVisible(false);
		cursor.redraw();
	}

	@Override
	public void mouseHover(MouseEvent e) {
	}

	private boolean canCreate(Rectangle createBounds) {
		ShipContainer container = Manager.getCurrentShip();

		if (!container.isRoomsVisible())
			return false;

		Rectangle anchorBounds = container.getShipController().getBounds();
		if (createBounds.x <= anchorBounds.x || createBounds.y <= anchorBounds.y)
			return false;

		boolean collided = false;
		ArrayList<AbstractController> layer = LayeredPainter.getInstance().getLayerMap().get(Layers.ROOM);
		for (int i = 0; i < layer.size() && !collided; i++) {
			AbstractController c = layer.get(i);
			if (c.isVisible() && c.collides(createBounds.x + COLLISION_TOLERANCE, createBounds.y + COLLISION_TOLERANCE,
					createBounds.width - 2 * COLLISION_TOLERANCE, createBounds.height - 2 * COLLISION_TOLERANCE))
				collided = true;
		}
		return !collided;
	}
}
