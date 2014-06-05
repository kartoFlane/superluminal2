package com.kartoflane.superluminal2.tools;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.LayeredPainter;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.GibToolComposite;

public class GibTool extends Tool {

	private Point clickPoint = new Point(0, 0);

	public GibTool(EditorWindow window) {
		super(window);
	}

	@Override
	public void select() {
		window.disposeSidebarContent();

		GibToolComposite gibC = getToolComposite(window.getSidebarWidget());
		window.setSidebarContent(gibC);

		cursor.setSize(ShipContainer.CELL_SIZE, ShipContainer.CELL_SIZE);
		cursor.setVisible(false);
	}

	@Override
	public void deselect() {
		cursor.setVisible(false);
	}

	@Override
	public GibToolComposite getToolComposite(Composite parent) {
		return (GibToolComposite) super.getToolComposite(parent);
	}

	@Override
	public GibToolComposite createToolComposite(Composite parent) {
		if (parent == null)
			throw new IllegalArgumentException("Parent must not be null.");
		compositeInstance = new GibToolComposite(parent);
		return (GibToolComposite) compositeInstance;
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
	}

	@Override
	public void mouseDown(MouseEvent e) {
		clickPoint.x = e.x;
		clickPoint.y = e.y;

		AbstractController selected = Manager.getSelected();

		// get the controller at the mouse click pos, and if found, notify it about mouseDown event
		AbstractController controller = LayeredPainter.getInstance().getSelectableControllerAt(e.x, e.y, Layers.GIBS);
		if (controller != null)
			controller.mouseDown(e);
		else if (selected != null)
			selected.mouseDown(e);

		// handle cursor
		if (cursor.isVisible() && e.button == 1) {
			cursor.setVisible(false);
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
		controller = LayeredPainter.getInstance().getSelectableControllerAt(e.x, e.y, Layers.GIBS);
		if (controller != null && controller != Manager.getSelected()) {
			controller.mouseUp(e);
		}

		Point p = Grid.getInstance().snapToGrid(e.x, e.y, cursor.getSnapMode());
		cursor.updateView();
		cursor.reposition(p.x, p.y);
	}

	@Override
	public void mouseMove(MouseEvent e) {
		super.mouseMove(e);

		// faciliate mouseEnter event for controllers
		AbstractController controller = LayeredPainter.getInstance().getSelectableControllerAt(e.x, e.y, Layers.GIBS);
		if (controller != null) {
			// mouseEnter event also highlights the controller, so check if its not highlighted already
			if (!controller.isHighlighted() && (Manager.getSelected() == null || !Manager.getSelected().isMoving()))
				controller.mouseEnter(e);
		}

		// move the cursor around to follow mouse
		Point p = Grid.getInstance().snapToGrid(e.x, e.y, cursor.getSnapMode());
		// always redraw it - prevents an odd visual bug where the controller sometimes doesn't register that it was moved
		cursor.reposition(p.x, p.y);
		cursor.updateView();

		// faciliate mouseExit event for controllers
		for (AbstractController control : LayeredPainter.getInstance().getLayerMap().get(Layers.GIBS)) {
			if (control != null && control.isVisible()) {
				// send mouseExit event to controllers that are obscured by other controllers
				// if:
				// - control is selectable AND
				// --- control doesn't contain the mouse pointer OR
				// ----- control is not the same object as controller AND
				// ----- both control and controller contain the mouse pointer
				if (control.isSelectable() && ((!control.contains(e.x, e.y))
						|| (controller != null && control != controller && control.contains(e.x, e.y) && controller.contains(e.x, e.y))))
					control.mouseExit(e);

				// also pass on mouseMove event
				control.mouseMove(e);
			}
		}
	}

	@Override
	public void mouseEnter(MouseEvent e) {
		cursor.setVisible(!Manager.leftMouseDown);
	}

	@Override
	public void mouseExit(MouseEvent e) {
		cursor.setVisible(false);
		for (AbstractController control : LayeredPainter.getInstance().getLayerMap().get(Layers.GIBS)) {
			if (control != null && control.isVisible()) {
				control.mouseExit(e);
			}
		}
	}

	@Override
	public void mouseHover(MouseEvent e) {
	}
}
