package com.kartoflane.superluminal2.mvc.controllers;

import java.util.Set;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.components.interfaces.Boundable;
import com.kartoflane.superluminal2.components.interfaces.Collidable;
import com.kartoflane.superluminal2.components.interfaces.Disposable;
import com.kartoflane.superluminal2.components.interfaces.Followable;
import com.kartoflane.superluminal2.components.interfaces.Follower;
import com.kartoflane.superluminal2.components.interfaces.MouseInputListener;
import com.kartoflane.superluminal2.components.interfaces.Pinnable;
import com.kartoflane.superluminal2.components.interfaces.Presentable;
import com.kartoflane.superluminal2.components.interfaces.Resizable;
import com.kartoflane.superluminal2.components.interfaces.Selectable;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.models.AbstractModel;
import com.kartoflane.superluminal2.tools.Tool.Tools;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.sidebar.DataComposite;
import com.kartoflane.superluminal2.ui.sidebar.ManipulationToolComposite;

public abstract class AbstractController implements Controller, Selectable, Resizable, Disposable, Pinnable, MouseInputListener, Presentable, Collidable, Boundable, Follower, Followable {

	protected Followable parent = null;

	protected boolean selectable = true;
	protected boolean selected = false;
	protected boolean moving = false;
	protected boolean containsMouse = false;
	protected boolean followableActive = true;

	protected Snapmodes snapmode = Snapmodes.FREE;
	protected Point clickOffset = new Point(0, 0);

	public abstract boolean contains(int x, int y);

	public abstract boolean intersects(Rectangle rect);

	public boolean contains(Point p) {
		return contains(p.x, p.y);
	}

	private AbstractModel getModelAbstract() {
		return (AbstractModel) getModel();
	}

	public void setLocation(Point p) {
		setLocation(p.x, p.y);
	}

	@Override
	public Point getLocation() {
		return getModel().getLocation();
	}

	@Override
	public int getX() {
		return getModel().getX();
	}

	@Override
	public int getY() {
		return getModel().getY();
	}

	public void setSize(Point p) {
		setSize(p.x, p.y);
	}

	@Override
	public Point getSize() {
		return getModel().getSize();
	}

	@Override
	public int getW() {
		return getModel().getW();
	}

	@Override
	public int getH() {
		return getModel().getH();
	}

	@Override
	public Rectangle getBounds() {
		return getModel().getBounds();
	}

	/**
	 * Sets the location of the controller to the specified coordinates, redrawing
	 * immediately if the controller is visible.
	 */
	public void reposition(int x, int y) {
		if (getView().isVisible()) {
			setVisible(false);
			redraw();
			setLocation(x, y);
			setVisible(true);
			redraw();
		} else {
			setLocation(x, y);
		}
	}

	/**
	 * Sets the location of the controller to the specified location, redrawing
	 * immediately if the controller is visible.
	 */
	public void reposition(Point p) {
		reposition(p.x, p.y);
	}

	/** Redraws the controller's area. */
	public void redraw() {
		EditorWindow.getInstance().canvasRedraw(getBounds());
	}

	@Override
	public void setPinned(boolean pin) {
		getModelAbstract().setPinned(pin);
	}

	@Override
	public boolean isPinned() {
		return getModelAbstract().isPinned();
	}

	@Override
	public void setSelectable(boolean sel) {
		selectable = sel;
	}

	@Override
	public boolean isSelectable() {
		return selectable;
	}

	/**
	 * Sets the controller as selected. <br>
	 * Override this to implement the view's selected appearance.<br>
	 * This method <b>should not</b> be called directly. Use {@link Manager#setSelected(AbstractController)} instead.
	 */
	@Override
	public void select() {
		selected = selectable;
	}

	/**
	 * Sets the controller as deselected. <br>
	 * Override this to implement the view's deselected (normal) appearance.<br>
	 * This method <b>should not</b> be called directly. Use {@link Manager#setSelected(AbstractController)} instead.
	 */
	@Override
	public void deselect() {
		selected = false;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Checks whether the Followable is fit for the role of a parent.<br>
	 * Throws appropriate exception when it is not.
	 */
	public final void checkParentConditions(Followable followable) {
		if (followable != null && !(followable instanceof Controller)) // instanceof returns false for null, but we allow null argument
			throw new IllegalArgumentException("Followable passed in argument is not a Controller.");
		if (followable == this)
			throw new IllegalArgumentException("Cannot be parent to itself.");
		if (followable instanceof Follower && ((Follower) followable).getParent() == this)
			throw new IllegalArgumentException("Cannot follow an object that is following the receiver.");
		if (followable != null && parent == followable)
			throw new IllegalArgumentException("Already following this object.");
	}

	@Override
	public void setParent(Followable followable) {
		checkParentConditions(followable);
		((AbstractModel) getModel()).checkParentConditions(followable == null ? null : (Followable) ((Controller) followable).getModel());

		// if it passes, change the parent
		if (parent != null)
			parent.removeFollower(this);
		if (followable != null)
			followable.addFollower(this);

		((Follower) getModel()).setParent(followable == null ? null : (Followable) ((Controller) followable).getModel());
		parent = followable;
	}

	@Override
	public Followable getParent() {
		return ((Follower) getModel()).getParent();
	}

	@Override
	public void setFollowOffset(int x, int y) {
		((Follower) getModel()).setFollowOffset(x, y);
	}

	@Override
	public Point getFollowOffset() {
		return ((Follower) getModel()).getFollowOffset();
	}

	@Override
	public Set<Follower> getFollowers() {
		return ((Followable) getModel()).getFollowers();
	}

	@Override
	public int getFollowerCount() {
		return ((Followable) getModel()).getFollowerCount();
	}

	@Override
	public boolean addFollower(Follower fol) {
		return ((Followable) getModel()).addFollower(fol);
	}

	@Override
	public boolean removeFollower(Follower fol) {
		return ((Followable) getModel()).removeFollower(fol);
	}

	@Override
	public void updateFollower() {
		Followable parent = getParent();
		if (parent == null)
			throw new NullPointerException("Parent is null.");
		Point offset = getFollowOffset();

		reposition(parent.getX() + offset.x, parent.getY() + offset.y);
	}

	@Override
	public void setFollowActive(boolean active) {
		this.followableActive = active;
	}

	@Override
	public boolean isFollowActive() {
		return followableActive;
	}

	@Override
	public void setCollidable(boolean collidable) {
		getModelAbstract().setCollidable(collidable);
	}

	@Override
	public boolean isCollidable() {
		return getModelAbstract().isCollidable();
	}

	public void setMoving(boolean mov) {
		moving = mov && isSelected() && !isPinned();
	}

	public boolean isMoving() {
		return moving;
	}

	public void setVisible(boolean vis) {
		getView().setVisible(vis);
	}

	public boolean isVisible() {
		return getView().isVisible();
	}

	/**
	 * Sets the snapping method used to align the controller to the grid.
	 * 
	 * @see {@link Grid#snapToGrid(int, int, int)}
	 * @see {@link Snapmodes}
	 */
	public void setSnapMode(Snapmodes snapmode) {
		this.snapmode = snapmode;
	}

	/** @return the snapping method used to align the controller to the grid. */
	public Snapmodes getSnapMode() {
		return snapmode;
	}

	/**
	 * Sets the highlighted state of the controller. <br>
	 * Override to implement the view's highlighted appearance.
	 */
	public void setHighlighted(boolean high) {
		getView().setHighlighted(high);
	}

	@Override
	public DataComposite getDataComposite(Composite parent) {
		if (selectable)
			throw new IllegalStateException("This Controller is selectable, but doesn't define a DataComposite!");
		else
			return null;
	}

	public Point toPresentedLocation(Point p) {
		return toPresentedLocation(p.x, p.y);
	}

	public Point toPresentedSize(Point p) {
		return toPresentedSize(p.x, p.y);
	}

	public Point toNormalLocation(Point p) {
		return toNormalLocation(p.x, p.y);
	}

	public Point toNormalSize(Point p) {
		return toNormalSize(p.x, p.y);
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (Manager.getSelectedToolId() == Tools.POINTER) {
			boolean contains = getBounds().contains(e.x, e.y);
			if (e.button == 1) {
				clickOffset.x = e.x - getX();
				clickOffset.y = e.y - getY();

				if (contains && !selected)
					Manager.setSelected(this);
				setMoving(contains);
			}
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		if (Manager.getSelectedToolId() == Tools.POINTER) {
			if (e.button == 1) {
				Rectangle bounds = getBounds();
				if (!bounds.contains(e.x, e.y) && !bounds.contains(getX() + clickOffset.x, getY() + clickOffset.y) && selected) {
					Manager.setSelected(null);
				}
				setMoving(false);
			}
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if (Manager.getSelectedToolId() == Tools.POINTER) {
			if (selected && moving) {
				Point p = Grid.getInstance().snapToGrid(e.x - clickOffset.x, e.y - clickOffset.y, snapmode);
				if (p != null) {
					reposition(p.x, p.y);
				}

				((ManipulationToolComposite) EditorWindow.getInstance().getSidebarContent()).updateData();
			}
		}
	}

	@Override
	public void mouseEnter(MouseEvent e) {
		if (!containsMouse) {
			setHighlighted(true);
		}
		containsMouse = true;
	}

	@Override
	public void mouseExit(MouseEvent e) {
		if (containsMouse) {
			setHighlighted(false);
		}
		containsMouse = false;
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
	}

	@Override
	public void mouseHover(MouseEvent e) {
	}

	@Override
	public boolean collides(int x, int y, int w, int h) {
		Rectangle rect = new Rectangle(x, y, w, h);
		return rect.intersects(new Rectangle(getX() - getW() / 2, getY() - getH() / 2, getW(), getH()));
	}

	@Override
	public boolean isBounded() {
		return getModelAbstract().isBounded();
	}

	@Override
	public void setBounded(boolean bound) {
		getModelAbstract().setBounded(bound);
	}

	@Override
	public Rectangle getBoundingArea() {
		return getModelAbstract().getBoundingArea();
	}

	@Override
	public void setBoundingArea(int x, int y, int w, int h) {
		getModelAbstract().setBoundingArea(x, y, w, h);
	}

	public void setBoundingArea(Rectangle r) {
		setBoundingArea(r.x, r.y, r.width, r.height);
	}

	public void setBoundingPoints(int x1, int y1, int x2, int y2) {
		setBoundingArea(x1, y1, x2 - x1, y2 - y1);
	}

	public void setBoundingPoints(Point start, Point end) {
		setBoundingArea(start.x, start.y, end.x - start.x, end.y - start.y);
	}

	public void updateBoundingArea() {
	}
}
