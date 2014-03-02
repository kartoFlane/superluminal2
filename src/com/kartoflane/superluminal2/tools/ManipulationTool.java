package com.kartoflane.superluminal2.tools;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.CellController;
import com.kartoflane.superluminal2.mvc.views.AbstractView;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.OverviewWindow;
import com.kartoflane.superluminal2.ui.sidebar.ManipulationToolComposite;

public class ManipulationTool extends Tool {

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

		resetCursor();

		OverviewWindow.getInstance().setEnabled(true);
		OverviewWindow.getInstance().update();

		Manager.setSelected(null);
	}

	@Override
	public void deselect() {
		Manager.setSelected(null);

		Controller control = window.getPainter().getSelectableControllerAt(cursor.getLocation());
		if (control != null) {
			// control.getView().setContainsMouse(false); // TODO ?
			control.getView().setHighlighted(false);
		}

		cursor.setVisible(false);
		window.canvasRedraw(cursor.getBounds());

		OverviewWindow.getInstance().setEnabled(false);
	}

	@Override
	public ManipulationTool getInstance() {
		return (ManipulationTool) instance;
	}

	@Override
	public ManipulationToolComposite getToolComposite(Composite parent) {
		return new ManipulationToolComposite(parent, true, false);
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
	}

	@Override
	public void mouseDown(MouseEvent e) {
		// inform the selected controller about clicks, so that it can be deselected
		if (Manager.getSelected() != null && !Manager.getSelected().getBounds().contains(e.x, e.y))
			Manager.getSelected().mouseDown(e);

		// get the controller at the mouse click pos, and if found, notify it about mouseDown event
		for (int i = selectableLayerIds.length - 1; i >= 0; i--) {
			if (selectableLayerIds[i] != null) {
				AbstractController control = window.getPainter().getSelectableControllerAt(e.x, e.y, selectableLayerIds[i]);
				if (control != null) {
					control.mouseDown(e);
					break;
				}
			}
		}

		/*// @formatter:off
		} else if (state == State.DOOR_LINK) {
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
			DoorDataComposite ddc = (DoorDataComposite) mtc.getData();
			RoomController roomController = control instanceof RoomController ? (RoomController) control : null;
			ddc.linkDoor(roomController);

			setState(State.MANIPULATE);
		}
		*/ // @formatter:on

		if (cursor.isVisible() && e.button == 1) {
			cursor.setVisible(false);
			window.canvasRedraw(cursor.getBounds());
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

		// find the topmost selectable controller and notify it about mouseUp event
		for (int i = selectableLayerIds.length - 1; i >= 0; i--) {
			if (selectableLayerIds[i] != null) {
				controller = window.getPainter().getSelectableControllerAt(e.x, e.y, selectableLayerIds[i]);
				if (controller != null) {
					controller.mouseUp(e);
					break;
				}
			}
		}

		if (!cursor.isVisible() && e.button == 1 && Grid.getInstance().isLocAccessible(e.x + 1, e.y + 1)) {
			cursor.setVisible(controller == null);
			Point p = Grid.getInstance().snapToGrid(e.x, e.y, cursor.getSnapMode());
			if (!p.equals(cursor.getLocation()))
				cursor.reposition(p.x, p.y);
			window.canvasRedraw(cursor.getBounds());
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {
		super.mouseMove(e);

		// faciliate mouseEnter event for controllers
		AbstractController controller = null;
		for (int i = selectableLayerIds.length - 1; i >= 0; i--) {
			if (selectableLayerIds[i] != null)
				controller = window.getPainter().getSelectableControllerAt(e.x, e.y, selectableLayerIds[i]);
			if (controller != null) {
				// mouseEnter event also highlights the controller
				if (!controller.getView().isHighlighted() && (Manager.getSelected() == null || !Manager.getSelected().isMoving()))
					controller.mouseEnter(e);
				// only one controller can be highlighted, so break the loop
				break;
			}
		}

		// move the cursor around to follow mouse
		if (Grid.getInstance().isLocAccessible(e.x, e.y) && controller == null) {
			cursor.setVisible(!Manager.leftMouseDown && controller == null);
			Point p = Grid.getInstance().snapToGrid(e.x, e.y, cursor.getSnapMode());
			// always redraw it - prevents an odd visual bug where the controller sometimes doesn't register that it was moved
			cursor.reposition(p.x, p.y);
		} else if (cursor.isVisible()) {
			cursor.setVisible(false);
			window.canvasRedraw(cursor.getBounds());
		}

		// faciliate mouseExit event for controllers
		for (Layers layer : window.getPainter().getLayerMap().descendingKeySet()) {
			for (AbstractController control : window.getPainter().getLayerMap().get(layer)) {
				if (control != null && control.getView().isVisible()) {
					// also send mouseExit event to controllers that are obscured by other controllers
					if (control.isSelectable() && ((!control.getBounds().contains(e.x, e.y))
							|| (controller != null && control != controller && control.getBounds().contains(e.x, e.y) && controller.getBounds().contains(e.x, e.y))))
						control.mouseExit(e);
					// also pass on mouseMove event
					if (window.canvasContains(e.x, e.y))
						control.mouseMove(e);
				}
			}
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

		// @formatter:off
		/*
		for (Layers layer : window.getPainter().getLayerMap().descendingKeySet()) {
			for (AbstractController controller : window.getPainter().getLayerMap().get(layer)) {
				if (controller != null)
					controller.mouseExit(e);
			}
		}
		*/
		// @formatter:on
	}

	@Override
	public void mouseHover(MouseEvent e) {
	}

	/** Changes the cursor appearance to resize */
	private void setRoomResizeCursor() { // TODO how to inform the cursor without exposing the method? change app to fully event-driven??
		cursorView.setImage(null);
		cursorView.setBackgroundColor(AbstractView.HIGHLIGHT_RGB);
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
		cursorView.setBorderColor(AbstractView.HIGHLIGHT_RGB);
		cursorView.setBorderThickness(3);
		cursor.setSnapMode(Snapmodes.CELL);

		Rectangle oldBounds = cursor.getBounds();
		cursor.setSize(CellController.SIZE, CellController.SIZE);
		cursor.setVisible(false);
		window.canvasRedraw(oldBounds);
	}
}
