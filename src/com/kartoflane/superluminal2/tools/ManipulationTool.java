package com.kartoflane.superluminal2.tools;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.RoomController;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.OverviewWindow;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.DoorDataComposite;
import com.kartoflane.superluminal2.ui.sidebar.ManipulationToolComposite;

public class ManipulationTool extends Tool {

	private enum States {
		NORMAL,
		ROOM_RESIZE,
		DOOR_LINK
	}

	private States state = States.NORMAL;
	private Point clickPoint = new Point(0, 0);

	public ManipulationTool(EditorWindow window) {
		super(window);
	}

	@Override
	public void select() {
		Control c = (Control) window.getSidebarContent();
		if (c != null && !c.isDisposed())
			c.dispose();

		ManipulationToolComposite pointerC = getToolComposite(window.getSidebarWidget());
		window.setSidebarContent(pointerC);

		OverviewWindow.getInstance().update();

		Manager.setSelected(null);

		setStateManipulate();

		cursor.setSize(ShipContainer.CELL_SIZE, ShipContainer.CELL_SIZE);
		cursor.setVisible(false);
	}

	@Override
	public void deselect() {
		Manager.setSelected(null);

		AbstractController control = window.getPainter().getSelectableControllerAt(cursor.getMouseLocation());
		if (control != null) {
			control.setHighlighted(false);
		}

		cursor.setVisible(false);
	}

	@Override
	public ManipulationTool getInstance() {
		return (ManipulationTool) instance;
	}

	@Override
	public ManipulationToolComposite getToolComposite(Composite parent) {
		return new ManipulationToolComposite(parent, true, false);
	}

	public void setStateManipulate() {
		state = States.NORMAL;
		cursor.updateView();
	}

	public boolean isStateManipulate() {
		return state == States.NORMAL;
	}

	public void setStateDoorLink() {
		state = States.DOOR_LINK;
		cursor.updateView();
	}

	public boolean isStateDoorLink() {
		return state == States.DOOR_LINK;
	}

	public void setStateRoomResize() {
		state = States.ROOM_RESIZE;
		cursor.updateView();
	}

	public boolean isStateRoomResize() {
		return state == States.ROOM_RESIZE;
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
	}

	@Override
	public void mouseDown(MouseEvent e) {
		clickPoint.x = e.x;
		clickPoint.y = e.y;

		AbstractController selected = Manager.getSelected();

		// inform the selected controller about clicks, so that it can be deselected
		// if (selected != null)
		// selected.mouseDown(e);

		if (state == States.NORMAL) {
			// get the controller at the mouse click pos, and if found, notify it about mouseDown event
			AbstractController controller = getTopmostSelectableController(e.x, e.y);
			if (controller != null)
				controller.mouseDown(e);
			else if (selected != null)
				selected.mouseDown(e);
		}
		// not using else if here, since a state change can occur in the previous step
		// TODO 'tis an ugly solution, think of something more elegant
		if (state == States.ROOM_RESIZE) {
			RoomController room = (RoomController) selected;

			cursor.updateView();
			if (e.button == 3)
				cursor.setVisible(false);
			cursor.resize(room.getSize());
			cursor.reposition(room.getLocation());

		} else if (state == States.DOOR_LINK) {
			// get the controller at the mouse click pos, and if found, notify it about mouseDown event
			AbstractController control = null;
			for (int i = selectableLayerIds.length - 1; i >= 0; i--) {
				if (selectableLayerIds[i] != null) {
					control = window.getPainter().getSelectableControllerAt(e.x, e.y, selectableLayerIds[i]);
					if (control != null && control instanceof RoomController)
						break;
				}
			}

			ManipulationToolComposite mtc = (ManipulationToolComposite) window.getSidebarContent();
			DoorDataComposite ddc = (DoorDataComposite) mtc.getDataComposite();
			RoomController roomController = control instanceof RoomController ? (RoomController) control : null;
			ddc.linkDoor(roomController);

			setStateManipulate();
		}

		// handle cursor
		if (cursor.isVisible() && e.button == 1) {
			cursor.setVisible(state == States.ROOM_RESIZE);
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		AbstractController controller = null;
		// inform the selected controller about clicks, so that it can be deselected
		// prevents a bug:
		// --controllers A and B are on the same layer, but A is "higher" than B
		// --dragging B under A, and releasing mouse button while cursor is within A's bounds, causes B to become stuck to the mouse pointer
		if (Manager.getSelected() != null)
			Manager.getSelected().mouseUp(e);

		if (state == States.NORMAL) {
			// find the topmost selectable controller and notify it about mouseUp event
			for (int i = selectableLayerIds.length - 1; i >= 0; i--) {
				if (selectableLayerIds[i] != null) {
					controller = window.getPainter().getSelectableControllerAt(e.x, e.y, selectableLayerIds[i]);
					if (controller != null && controller != Manager.getSelected()) {
						controller.mouseUp(e);
						break;
					}
				}
			}
			Point p = Grid.getInstance().snapToGrid(e.x, e.y, cursor.getSnapMode());
			cursor.updateView();
			cursor.reposition(p.x, p.y);
		} else if (state == States.ROOM_RESIZE) {
			RoomController room = (RoomController) Manager.getSelected();
			Point p = Grid.getInstance().snapToGrid(e.x, e.y, cursor.getSnapMode());
			cursor.setVisible(false);
			cursor.redraw();
			cursor.updateView();
			cursor.resize(ShipContainer.CELL_SIZE, ShipContainer.CELL_SIZE);
			cursor.reposition(p.x, p.y);

			if (!room.isResizing())
				setStateManipulate();

		} else if (state == States.DOOR_LINK) {
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {
		super.mouseMove(e);

		// faciliate mouseEnter event for controllers
		AbstractController controller = getTopmostSelectableController(e.x, e.y);
		if (controller != null) {
			// mouseEnter event also highlights the controller, so check if its not highlighted already
			if (!controller.isHighlighted() && (Manager.getSelected() == null || !Manager.getSelected().isMoving()))
				controller.mouseEnter(e);
		}

		if (state == States.NORMAL) {
			// move the cursor around to follow mouse
			Point p = Grid.getInstance().snapToGrid(e.x, e.y, cursor.getSnapMode());
			// always redraw it - prevents an odd visual bug where the controller sometimes doesn't register that it was moved
			cursor.reposition(p.x, p.y);
			cursor.updateView();

		} else if (state == States.ROOM_RESIZE) {
			RoomController room = (RoomController) Manager.getSelected();

			Point resizeAnchor = room.getResizeAnchor();

			// choose appropriate snapmode depending on the direction in which the user is resizing
			Snapmodes resizeSnapmode = null;
			if (e.x >= resizeAnchor.x && e.y >= resizeAnchor.y)
				resizeSnapmode = Snapmodes.CORNER_BR;
			else if (e.x >= resizeAnchor.x && e.y < resizeAnchor.y)
				resizeSnapmode = Snapmodes.CORNER_TR;
			else if (e.x < resizeAnchor.x && e.y >= resizeAnchor.y)
				resizeSnapmode = Snapmodes.CORNER_BL;
			else
				resizeSnapmode = Snapmodes.CORNER_TL;

			Point pointer = Grid.getInstance().snapToGrid(e.x, e.y, resizeSnapmode);
			Point anchor = new Point(0, 0);

			if (pointer.x < resizeAnchor.x)
				// if mouse is to the left of the anchor...
				anchor.x = resizeAnchor.x + (clickPoint.x > resizeAnchor.x ? ShipContainer.CELL_SIZE : 0);
			else
				// if mouse is to the right of the anchor...
				anchor.x = resizeAnchor.x + (clickPoint.x > resizeAnchor.x ? 0 : -ShipContainer.CELL_SIZE);
			if (pointer.y < resizeAnchor.y)
				// if mouse is above the anchor...
				anchor.y = resizeAnchor.y + (clickPoint.y > resizeAnchor.y ? ShipContainer.CELL_SIZE : 0);
			else
				// if mouse is below the anchor...
				anchor.y = resizeAnchor.y + (clickPoint.y > resizeAnchor.y ? 0 : -ShipContainer.CELL_SIZE);

			anchor = Grid.getInstance().snapToGrid(anchor.x, anchor.y, Snapmodes.CROSS);

			int x = Math.min(anchor.x, pointer.x);
			int y = Math.min(anchor.y, pointer.y);
			int w = Math.max(Math.abs(anchor.x - pointer.x), ShipContainer.CELL_SIZE);
			int h = Math.max(Math.abs(anchor.y - pointer.y), ShipContainer.CELL_SIZE);

			cursor.updateView();
			cursor.resize(w, h);
			cursor.reposition(x + w / 2, y + h / 2);

			if (!room.isResizing())
				setStateManipulate();

		} else if (state == States.DOOR_LINK) {
			// move the cursor around to follow mouse
			Point p = Grid.getInstance().snapToGrid(e.x, e.y, cursor.getSnapMode());
			// always redraw it - prevents an odd visual bug where the controller sometimes doesn't register that it was moved
			cursor.reposition(p.x, p.y);
			cursor.updateView();

		}

		// faciliate mouseExit event for controllers
		for (Layers layer : window.getPainter().getLayerMap().descendingKeySet()) {
			for (AbstractController control : window.getPainter().getLayerMap().get(layer)) {
				if (control != null && control.isVisible()) {
					// send mouseExit event to controllers that are obscured by other controllers
					// if:
					// - control is selectable AND
					// --- control doesn't contain the mouse pointer OR
					// ----- control is not the same object as controller AND
					// ----- both control and controller contain the mouse pointer
					if (control.isSelectable() && ((!control.getBounds().contains(e.x, e.y))
							|| (controller != null && control != controller && control.getBounds().contains(e.x, e.y) && controller.getBounds().contains(e.x, e.y))))
						control.mouseExit(e);

					// also pass on mouseMove event
					control.mouseMove(e);
				}
			}
		}
	}

	private AbstractController getTopmostSelectableController(int x, int y) {
		AbstractController controller = null;
		for (int i = selectableLayerIds.length - 1; i >= 0 && controller == null; i--) {
			if (selectableLayerIds[i] != null)
				controller = window.getPainter().getSelectableControllerAt(x, y, selectableLayerIds[i]);
		}
		return controller;
	}

	@Override
	public void mouseEnter(MouseEvent e) {
		cursor.setVisible(!Manager.leftMouseDown);
	}

	@Override
	public void mouseExit(MouseEvent e) {
		cursor.setVisible(false);
	}

	@Override
	public void mouseHover(MouseEvent e) {
	}
}
