package com.kartoflane.superluminal2.tools;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ftl.MountObject;
import com.kartoflane.superluminal2.ftl.MountObject.Directions;
import com.kartoflane.superluminal2.mvc.controllers.MountController;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.OverviewWindow;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.MountToolComposite;

public class MountTool extends Tool {

	private boolean canCreate = false;
	private boolean rotated = false;
	private boolean mirrored = false;
	private Directions direction = Directions.UP;

	public MountTool(EditorWindow window) {
		super(window);
	}

	@Override
	public void select() {
		cursor.setSnapMode(Snapmodes.FREE);
		cursor.updateView();
		cursor.setVisible(false);
		setRotated(rotated);
		setMirrored(mirrored);
		setDirection(direction);
		cursor.setSize(MountController.DEFAULT_WIDTH, MountController.DEFAULT_HEIGHT);
	}

	@Override
	public void deselect() {
		cursor.updateView();
		cursor.setVisible(false);
	}

	@Override
	public MountTool getInstance() {
		return (MountTool) instance;
	}

	public void setRotated(boolean rot) {
		rotated = rot;
		cursor.updateView();
	}

	public boolean isRotated() {
		return rotated;
	}

	public void setMirrored(boolean mir) {
		mirrored = mir;
		cursor.updateView();
	}

	public boolean isMirrored() {
		return mirrored;
	}

	public void setDirection(Directions dir) {
		direction = dir;
		cursor.updateView();
	}

	public Directions getDirection() {
		return direction;
	}

	@Override
	public Composite getToolComposite(Composite parent) {
		return new MountToolComposite(parent);
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (e.button == 1 && canCreate) {
			ShipContainer container = Manager.getCurrentShip();
			MountObject object = new MountObject();
			MountController mount = MountController.newInstance(container, object);
			Rectangle oldBounds = mount.getBounds();

			mount.setLocation(e.x, e.y);

			window.canvasRedraw(oldBounds);
			mount.redraw();
			OverviewWindow.getInstance().update();
		}
		// handle cursor
		if (cursor.isVisible() && e.button == 1) {
			cursor.setVisible(false);
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		// handle cursor
		if (!cursor.isVisible() && Grid.getInstance().isLocAccessible(e.x, e.y)) {
			canCreate = canPlace();
			cursor.updateView();

			if (e.button == 1)
				cursor.setVisible(true);

			Point p = Grid.getInstance().snapToGrid(e.x, e.y, cursor.getSnapMode());
			if (!p.equals(cursor.getLocation())) {
				cursor.reposition(p.x, p.y);
			}
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {
		// move the cursor around to follow mouse
		if (Grid.getInstance().isLocAccessible(e.x, e.y)) {
			canCreate = canPlace();
			cursor.updateView();
			cursor.setVisible(!Manager.leftMouseDown);
			Point p = Grid.getInstance().snapToGrid(e.x, e.y, cursor.getSnapMode());
			if (!p.equals(cursor.getLocation())) {
				cursor.reposition(p.x, p.y);

			}
		} else if (cursor.isVisible()) {
			cursor.setVisible(false);
		}
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

	private boolean canPlace() {
		return Manager.getCurrentShip().getMountControllers().length < 8;
	}

	public boolean canCreate() {
		return canCreate;
	}
}
