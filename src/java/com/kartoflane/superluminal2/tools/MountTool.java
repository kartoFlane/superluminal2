package com.kartoflane.superluminal2.tools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.enums.Directions;
import com.kartoflane.superluminal2.components.enums.Images;
import com.kartoflane.superluminal2.core.Grid;
import com.kartoflane.superluminal2.core.Grid.Snapmodes;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ftl.MountObject;
import com.kartoflane.superluminal2.mvc.controllers.MountController;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.OverviewWindow;
import com.kartoflane.superluminal2.ui.ShipContainer;
import com.kartoflane.superluminal2.ui.sidebar.MountToolComposite;
import com.kartoflane.superluminal2.undo.UndoableCreateEdit;

public class MountTool extends Tool {

	private boolean canCreate = false;
	private boolean followHull = true;
	private MountController toolMount = null;

	public MountTool(EditorWindow window) {
		super(window);
	}

	@Override
	public void select() {
		if (toolMount == null) {
			MountObject object = new MountObject();
			toolMount = MountController.newInstance(Manager.getCurrentShip(), object);
			toolMount.setSelectable(false);
			toolMount.getProp(MountController.ARROW_PROP_ID).setInheritVisibility(true);
		}

		cursor.setSnapMode(Snapmodes.FREE);
		cursor.updateView();
		cursor.setVisible(false);

		setRotated(toolMount.isRotated());
		setMirrored(toolMount.isMirrored());
		setDirection(toolMount.getDirection());
		toolMount.setParent(cursor);
		toolMount.setVisible(cursor.isVisible());

		cursor.resize(MountController.DEFAULT_WIDTH, MountController.DEFAULT_HEIGHT);

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
		toolMount.setParent(null);
		toolMount.setVisible(false);

		CreationTool ctool = (CreationTool) Manager.getTool(Tools.CREATOR);
		ctool.getToolComposite(null).clearDataContainer();
	}

	public void setRotated(boolean rot) {
		toolMount.setRotated(rot);
	}

	public boolean isRotated() {
		return toolMount.isRotated();
	}

	public void setMirrored(boolean mir) {
		toolMount.setMirrored(mir);
	}

	public boolean isMirrored() {
		return toolMount.isMirrored();
	}

	public void setDirection(Directions dir) {
		toolMount.setDirection(dir);
	}

	public Directions getDirection() {
		return toolMount.getDirection();
	}

	public void setFollowHull(boolean follow) {
		followHull = follow;
	}

	public boolean getFollowHull() {
		return followHull;
	}

	@Override
	public MountToolComposite getToolComposite(Composite parent) {
		return (MountToolComposite) super.getToolComposite(parent);
	}

	@Override
	public MountToolComposite createToolComposite(Composite parent) {
		if (parent == null)
			throw new IllegalArgumentException("Parent must not be null.");
		compositeInstance = new MountToolComposite(parent);
		return (MountToolComposite) compositeInstance;
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
	}

	@Override
	public void mouseDown(MouseEvent e) {
		// Check the conditions again in case the variable is outdated
		canCreate = canPlace();

		if (e.button == 1 && canCreate) {
			ShipContainer container = Manager.getCurrentShip();
			MountObject object = new MountObject();
			MountController mount = MountController.newInstance(container, object);
			Rectangle oldBounds = mount.getBounds();

			mount.setLocation(e.x, e.y);
			mount.setRotated(isRotated());
			mount.setMirrored(isMirrored());
			mount.setDirection(getDirection());
			if (getFollowHull())
				mount.setParent(Manager.getCurrentShip().getImageController(Images.HULL));
			else
				mount.setParent(Manager.getCurrentShip().getShipController());
			mount.updateFollowOffset();

			container.add(mount);
			container.store(mount);

			window.canvasRedraw(oldBounds);
			mount.redraw();
			OverviewWindow.staticUpdate();

			container.postEdit(new UndoableCreateEdit(mount));
		} else if (e.button == 3) {
			if ((e.stateMask & SWT.SHIFT) == SWT.SHIFT) {
				Directions dir = toolMount.getDirection();
				toolMount.setDirection(dir.nextDirection());
				toolMount.updateView();
			} else if ((e.stateMask & SWT.ALT) == SWT.ALT) {
				toolMount.setVisible(false);
				cursor.setVisible(false);
				toolMount.setMirrored(!toolMount.isMirrored());
				toolMount.updateView();
				cursor.updateView();
				toolMount.setVisible(true);
				cursor.setVisible(true);
			} else {
				toolMount.setVisible(false);
				cursor.setVisible(false);
				toolMount.setRotated(!toolMount.isRotated());
				toolMount.updateView();
				cursor.updateView();
				toolMount.setVisible(true);
				cursor.setVisible(true);
			}
			window.updateSidebarContent();
		}

		// handle cursor
		if (cursor.isVisible() && e.button == 1) {
			cursor.setVisible(false);
			toolMount.setVisible(false);
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		// handle cursor
		if (!cursor.isVisible() && Grid.getInstance().isLocAccessible(e.x, e.y)) {
			canCreate = canPlace();
			cursor.updateView();

			if (e.button == 1) {
				cursor.setVisible(true);
				toolMount.setVisible(true);
			}

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
			cursor.reposition(p.x, p.y);
			toolMount.setVisible(cursor.isVisible());
		} else if (cursor.isVisible()) {
			cursor.setVisible(false);
			toolMount.setVisible(false);
		}
	}

	@Override
	public void mouseEnter(MouseEvent e) {
		cursor.setVisible(!Manager.leftMouseDown);
		toolMount.setVisible(!Manager.leftMouseDown);
	}

	@Override
	public void mouseExit(MouseEvent e) {
		cursor.setVisible(false);
		toolMount.setVisible(false);
	}

	@Override
	public void mouseHover(MouseEvent e) {
	}

	private boolean canPlace() {
		ShipContainer container = Manager.getCurrentShip();
		return container.isMountsVisible() && container.getMountControllers().length < 8;
	}

	public boolean canCreate() {
		return canCreate;
	}
}
