package com.kartoflane.superluminal2.tools;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.Directions;
import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ftl.SystemObject.Systems;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.mvc.controllers.StationController;
import com.kartoflane.superluminal2.mvc.controllers.SystemController;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.StationToolComposite;

public class StationTool extends Tool {

	private enum States {
		PLACEMENT, DIRECTION, REMOVAL
	}

	private States state = States.PLACEMENT;
	private boolean canPlace = false;
	private Directions direction = Directions.UP;

	public StationTool(EditorWindow window) {
		super(window);
	}

	@Override
	public StationTool getInstance() {
		return (StationTool) instance;
	}

	@Override
	public void select() {
		cursor.updateView();
		cursor.setSnapMode(Snapmodes.CELL);
		cursor.resize(ShipContainer.CELL_SIZE, ShipContainer.CELL_SIZE);
		cursor.setVisible(false);
	}

	@Override
	public void deselect() {
	}

	public void setDirection(Directions dir) {
		direction = dir;
	}

	public Directions getDirection() {
		return direction;
	}

	public void setStatePlacement() {
		state = States.PLACEMENT;
	}

	public boolean isStatePlacement() {
		return state == States.PLACEMENT;
	}

	public void setStateDirection() {
		state = States.DIRECTION;
	}

	public boolean isStateDirection() {
		return state == States.DIRECTION;
	}

	public void setStateRemoval() {
		state = States.REMOVAL;
	}

	public boolean isStateRemoval() {
		return state == States.REMOVAL;
	}

	@Override
	public Composite getToolComposite(Composite parent) {
		return new StationToolComposite(parent);
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (canPlace && e.button == 1) {
			if (state == States.PLACEMENT) {
				if (Manager.modShift) {
					// direction change
					ShipContainer container = Manager.getCurrentShip();
					RoomController roomC = (RoomController) window.getPainter().getControllerAt(e.x, e.y, Layers.ROOM);
					if (roomC != null) {
						SystemController system = container.getSystemController(roomC.getSystemId());
						if (system.canContainStation()) {
							StationController station = (StationController) container.getController(system.getGameObject().getStation());
							Directions dir = station.getSlotDirection();
							changeDirection(e.x, e.y, getNextDirection(dir));
						}
						roomC.redraw();
					}
				} else {
					// station placement
					ShipContainer container = Manager.getCurrentShip();
					RoomController roomC = (RoomController) window.getPainter().getControllerAt(e.x, e.y, Layers.ROOM);
					if (roomC != null) {
						SystemController system = container.getSystemController(roomC.getSystemId());
						if (system.canContainStation()) {
							StationController station = (StationController) container.getController(system.getGameObject().getStation());
							int id = roomC.getSlotId(cursor.getX(), cursor.getY());
							// station.setSlotDirection(direction);
							station.setSlotId(id);
							station.updateFollowOffset();
							station.updateFollower();
							station.setVisible(true);
						}
						roomC.redraw();
					}
				}
			} else if (state == States.DIRECTION) {
				changeDirection(e.x, e.y, direction);
			} else if (state == States.REMOVAL) {
				removeStation(e.x, e.y);
			}
		} else if (canPlace && e.button == 3) {
			if (state == States.PLACEMENT) {
				removeStation(e.x, e.y);
			}
		}

		// handle cursor
		if (cursor.isVisible() && e.button == 1) {
			cursor.setVisible(false);
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		// handle cursor
		if (e.button == 1) {
			cursor.reposition(Grid.getInstance().snapToGrid(e.x, e.y, cursor.getSnapMode()));
			cursor.setVisible(true);
		}
	}

	private void removeStation(int x, int y) {
		ShipContainer container = Manager.getCurrentShip();
		RoomController roomC = (RoomController) window.getPainter().getControllerAt(x, y, Layers.ROOM);
		if (roomC != null) {
			SystemController system = container.getSystemController(roomC.getSystemId());
			if (system.canContainStation()) {
				StationController station = (StationController) container.getController(system.getGameObject().getStation());
				station.setSlotId(-2);
				station.setVisible(false);
			}
			roomC.redraw();
		}
	}

	private void changeDirection(int x, int y, Directions dir) {
		ShipContainer container = Manager.getCurrentShip();
		RoomController roomC = (RoomController) window.getPainter().getControllerAt(x, y, Layers.ROOM);
		if (roomC != null) {
			SystemController system = container.getSystemController(roomC.getSystemId());
			if (system.canContainStation()) {
				StationController station = (StationController) container.getController(system.getGameObject().getStation());
				station.setSlotDirection(dir);
				station.updateFollowOffset();
				station.updateFollower();
			}
			roomC.redraw();
		}
	}

	private Directions getNextDirection(Directions dir) {
		switch (dir) {
			case UP:
				return Directions.RIGHT;
			case RIGHT:
				return Directions.DOWN;
			case DOWN:
				return Directions.LEFT;
			case LEFT:
				return Directions.UP;
			case NONE:
				return null;
			default:
				return null;
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {
		ShipContainer container = Manager.getCurrentShip();
		Point p = Grid.getInstance().snapToGrid(e.x, e.y, cursor.getSnapMode());
		RoomController roomC = (RoomController) window.getPainter().getControllerAt(p.x, p.y, Layers.ROOM);
		if (roomC != null) {
			SystemController system = container.getSystemController(roomC.getSystemId());
			Systems id = system.getSystemId();
			canPlace = system.canContainStation() && roomC.getDimensions().contains(p.x, p.y);
			canPlace &= !(isStateDirection() && (id == Systems.MEDBAY || id == Systems.CLONEBAY));
		} else {
			canPlace = false;
		}

		// move the cursor around to follow mouse
		cursor.updateView();
		if (p.x != cursor.getX() || p.y != cursor.getY()) {
			cursor.reposition(p.x, p.y);
			if (!cursor.isVisible())
				cursor.setVisible(!Manager.leftMouseDown);
		}
		cursor.redraw();
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

	public boolean canPlace() {
		return canPlace;
	}

	@Override
	public void mouseHover(MouseEvent e) {
	}
}
