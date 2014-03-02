package com.kartoflane.superluminal2.tools;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.MountController;
import com.kartoflane.superluminal2.mvc.views.AbstractView;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.OverviewWindow;

public class MountTool extends Tool {

	private static final RGB DENY_COLOR = AbstractView.DENY_RGB;
	private static final RGB ALLOW_COLOR = AbstractView.ALLOW_RGB;

	private boolean canCreate = false;

	public MountTool(EditorWindow window) {
		super(window);
	}

	@Override
	public void select() {
		cursor.getView().setImage("/assets/weapon.png");
		cursor.getView().setBackgroundColor(null);
		cursor.getView().setBorderColor(null);
		cursor.setSnapMode(Snapmodes.FREE);

		cursor.setVisible(false);
	}

	@Override
	public void deselect() {
	}

	@Override
	public MountTool getInstance() {
		return (MountTool) instance;
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
			MountController mount = MountController.newInstance(Manager.getCurrentShip());
			Rectangle oldBounds = mount.getBounds();

			mount.setLocation(e.x, e.y);

			window.canvasRedraw(oldBounds);
			window.canvasRedraw(mount.getBounds());
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
		if (!cursor.isVisible() && Grid.getInstance().isLocAccessible(e.x, e.y)) {
			if (e.button == 1)
				cursor.setVisible(true);

			Point p = Grid.getInstance().snapToGrid(e.x, e.y, cursor.getSnapMode());
			if (!p.equals(cursor.getLocation())) {
				cursor.reposition(p.x, p.y);

				canCreate = canCreate();
				cursor.getView().setBorderColor(canCreate ? ALLOW_COLOR : DENY_COLOR);
			}

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
				cursor.reposition(p.x, p.y);

				canCreate = canCreate();
				cursor.getView().setBorderColor(canCreate ? ALLOW_COLOR : DENY_COLOR);
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

	private boolean canCreate() {
		return Manager.getCurrentShip().getMountControllers().size() < 8;
	}
}
