package com.kartoflane.superluminal2.mvc.controllers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.EventHandler;
import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.Grid.Snapmodes;
import com.kartoflane.superluminal2.components.LayeredPainter;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.components.interfaces.Boundable;
import com.kartoflane.superluminal2.components.interfaces.Collidable;
import com.kartoflane.superluminal2.components.interfaces.Deletable;
import com.kartoflane.superluminal2.components.interfaces.Disposable;
import com.kartoflane.superluminal2.components.interfaces.Followable;
import com.kartoflane.superluminal2.components.interfaces.Follower;
import com.kartoflane.superluminal2.components.interfaces.MouseInputListener;
import com.kartoflane.superluminal2.components.interfaces.Pinnable;
import com.kartoflane.superluminal2.components.interfaces.Predicate;
import com.kartoflane.superluminal2.components.interfaces.Resizable;
import com.kartoflane.superluminal2.components.interfaces.Selectable;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.events.SLEvent;
import com.kartoflane.superluminal2.events.SLListener;
import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.Model;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.controllers.props.PropController;
import com.kartoflane.superluminal2.mvc.models.BaseModel;
import com.kartoflane.superluminal2.mvc.views.BaseView;
import com.kartoflane.superluminal2.tools.Tool.Tools;
import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;
import com.kartoflane.superluminal2.utils.Utils;

public abstract class AbstractController implements Controller, Selectable, Disposable, Deletable, Resizable, Pinnable,
		MouseInputListener, Collidable, Boundable, Follower, Followable, SLListener {

	protected Followable parent = null;
	protected Point followOffset = null;
	protected HashSet<Follower> followers = null;

	protected EventHandler eventHandler = null;
	protected HashSet<PropController> props = null;

	protected BaseModel model = null;
	protected BaseView view = null;

	protected int presentedFactor = 1;
	protected boolean selectable = true;
	protected boolean selected = false;
	protected boolean moving = false;
	protected boolean containsMouse = false;
	protected boolean followableActive = true;
	protected boolean deleted = false;

	protected Snapmodes snapmode = Snapmodes.FREE;
	protected Point clickOffset = new Point(0, 0);

	@Override
	public void setModel(Model model) {
		if (model == null)
			throw new IllegalArgumentException("Argument must not be null.");
		this.model = (BaseModel) model;
	}

	protected BaseModel getModel() {
		return model;
	}

	@Override
	public void setView(View view) {
		if (view == null)
			throw new IllegalArgumentException("Argument must not be null.");
		if (this.view != null && this.view != view)
			this.view.dispose();
		this.view = (BaseView) view;

		view.setController(this);
		view.setModel(model);
	}

	protected BaseView getView() {
		return view;
	}

	public void updateView() {
		view.updateView();
	}

	public void addToPainter(Layers layer) {
		view.addToPainter(layer);
	}

	public void addToPainterBottom(Layers layer) {
		view.addToPainterBottom(layer);
	}

	public void removeFromPainter() {
		view.removeFromPainter();
	}

	public void removeFromPainter(Layers layer) {
		view.removeFromPainter(layer);
	}

	public boolean setLocation(int x, int y) {
		// if the new location falls outside of the area the object is
		// bounded to, change the parameters to be as close the border as possible
		if (isBounded() && !isWithinBoundingArea(x, y)) {
			Point p = limitToBoundingArea(x, y);
			x = p.x;
			y = p.y;
		}

		if (isCollidable()) {
			Rectangle b = new Rectangle(x - getW() / 2, y - getH() / 2, getW(), getH());
			if (collidesAs(b, this))
				return false;
		}

		model.setLocation(x, y);

		if (isFollowActive() && followers != null) { // followers set is lazily instantiated when it's needed
			for (Follower fol : followers)
				fol.updateFollower();
		}

		if (eventHandler != null && eventHandler.hooks(SLEvent.MOVE))
			eventHandler.sendEvent(new SLEvent(SLEvent.MOVE, this, getLocation()));
		return true;
	}

	public boolean setLocation(Point p) {
		return setLocation(p.x, p.y);
	}

	@Override
	public Point getLocation() {
		return model.getLocation();
	}

	public boolean setLocationCorner(int x, int y) {
		Rectangle b = model.getBounds();
		x -= b.x;
		y -= b.y;

		return translate(x, y);
	}

	public Point getLocationCorner() {
		Rectangle b = model.getBounds();
		return new Point(b.x, b.y);
	}

	@Override
	public boolean translate(int dx, int dy) {
		// if the new location falls outside of the area the object is
		// bounded to, change the parameters to be as close the border as possible
		if (isBounded() && !isWithinBoundingArea(getX() + dx, getY() + dy)) {
			Point p = limitToBoundingArea(getX() + dx, getY() + dy);
			dx = p.x - getX();
			dy = p.y - getY();
		}

		if (isCollidable()) {
			Rectangle b = new Rectangle(getX() - getW() / 2, getY() - getH() / 2, getW(), getH());
			b.x += dx;
			b.y += dy;
			if (collidesAs(b, this))
				return false;
		}

		model.translate(dx, dy);
		if (isFollowActive() && followers != null) { // followers set is lazily instantiated when it's needed
			for (Follower fol : followers)
				fol.updateFollower();
		}

		if (eventHandler != null && eventHandler.hooks(SLEvent.MOVE))
			eventHandler.sendEvent(new SLEvent(SLEvent.MOVE, this, getLocation()));
		return true;
	}

	@Override
	public int getX() {
		return model.getX();
	}

	@Override
	public int getY() {
		return model.getY();
	}

	@Override
	public boolean setSize(int w, int h) {
		model.setSize(w, h);

		if (eventHandler != null && eventHandler.hooks(SLEvent.RESIZE))
			eventHandler.sendEvent(new SLEvent(SLEvent.RESIZE, this, getSize()));
		return true;
	}

	public boolean setSize(Point p) {
		return setSize(p.x, p.y);
	}

	@Override
	public Point getSize() {
		return model.getSize();
	}

	@Override
	public int getW() {
		return model.getW();
	}

	@Override
	public int getH() {
		return model.getH();
	}

	public Rectangle getBounds() {
		return Utils.rotate(model.getBounds(), getRotation());
	}

	/**
	 * @param rad
	 *            angle in degrees, 0 = north.
	 */
	public void setRotation(float angle) {
		view.setRotation(angle);
	}

	/**
	 * @param rad
	 *            angle in degrees
	 */
	public void rotate(float angle) {
		view.setRotation(view.getRotation() + angle);
	}

	/**
	 * @return rotation in degrees, 0 = north.
	 */
	public float getRotation() {
		return view.getRotation();
	}

	/**
	 * @return rectangle that is exactly represents the area of the controller.
	 */
	public Rectangle getDimensions() {
		return new Rectangle(getX() - getW() / 2, getY() - getH() / 2, getW(), getH());
	}

	/**
	 * Sets the location of the controller to the specified coordinates, redrawing
	 * immediately if the controller is visible.
	 */
	public void reposition(int x, int y) {
		if (isVisible()) {
			setVisible(false);
			setLocation(x, y);
			updateView();
			setVisible(true);
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

	/**
	 * Sets the size of the controller to the specified values, redrawing
	 * immediately if the controller is visible.
	 */
	public void resize(int w, int h) {
		if (isVisible()) {
			setVisible(false);
			setSize(w, h);
			updateView();
			setVisible(true);
		} else {
			setSize(w, h);
		}
	}

	/**
	 * Sets the size of the controller to the specified values, redrawing
	 * immediately if the controller is visible.
	 */
	public void resize(Point p) {
		resize(p.x, p.y);
	}

	public Point getPresentedLocation() {
		return getLocation();
	}

	public Point getPresentedSize() {
		return getSize();
	}

	public void setPresentedLocation(int x, int y) {
		reposition(x * presentedFactor, y * presentedFactor);
	}

	public void setPresentedLocation(Point p) {
		setPresentedLocation(p.x, p.y);
	}

	public void setPresentedSize(int w, int h) {
		resize(w, h);
		updateBoundingArea();
	}

	public void setPresentedSize(Point p) {
		setPresentedSize(p.x, p.y);
	}

	/**
	 * Presented factor is the number by which the controller's dimensions and location are multiplied
	 * before they are displayed in the sidebar.
	 */
	public void setPresentedFactor(int i) {
		presentedFactor = i;
	}

	/**
	 * Presented factor is the number by which the controller's dimensions and location are multiplied
	 * before they are displayed in the sidebar.
	 * 
	 * @return the presented factor of the room
	 */
	public int getPresentedFactor() {
		return presentedFactor;
	}

	/**
	 * Redraws the controller's area.
	 */
	public void redraw() {
		EditorWindow.getInstance().canvasRedraw(getBounds());
	}

	/**
	 * Convenience method to redraw the rectangle.
	 */
	public static void redraw(Rectangle r) {
		EditorWindow.getInstance().canvasRedraw(r);
	}

	/**
	 * Forwards the PaintEvent to the View.
	 */
	public void redraw(PaintEvent e) {
		view.redraw(e);
	}

	/**
	 * Allows to (un)pin the controller. Pinned controllers cannot be moved by dragging.
	 */
	@Override
	public void setPinned(boolean pin) {
		model.setPinned(pin);
		setMoving(false);
		updateView();
		redraw();
	}

	/**
	 * @return true if the controller is pinned and cannot be moved by dragging, false otherwise.
	 */
	@Override
	public boolean isPinned() {
		return model.isPinned();
	}

	/**
	 * Allows to make the controller (un)selectable.
	 */
	@Override
	public void setSelectable(boolean sel) {
		selectable = sel;
	}

	/**
	 * @return true if the controller can be selected by clicking on it, false otherwie.
	 */
	@Override
	public boolean isSelectable() {
		return selectable;
	}

	/**
	 * Sets the controller as selected. <br>
	 * Override this to execute additional actions when this controller is selected.<br>
	 * This method <b>should not</b> be called directly. Use {@link Manager#setSelected(ObjectController)} instead.
	 */
	@Override
	public void select() {
		selected = selectable;
		updateView();
		redraw();

		if (eventHandler != null && eventHandler.hooks(SLEvent.SELECT))
			eventHandler.sendEvent(new SLEvent(SLEvent.SELECT, this, this));
	}

	/**
	 * Sets the controller as deselected. <br>
	 * Override this to execute additional actions when this controller is deselected.<br>
	 * This method <b>should not</b> be called directly. Use {@link Manager#setSelected(ObjectController)} instead.
	 */
	@Override
	public void deselect() {
		selected = false;
		setMoving(false);
		updateView();
		redraw();

		if (eventHandler != null && eventHandler.hooks(SLEvent.DESELECT))
			eventHandler.sendEvent(new SLEvent(SLEvent.DESELECT, this, this));
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

		// if it passes, change the parent
		if (parent != null)
			parent.removeFollower(this);
		if (followable != null)
			followable.addFollower(this);

		parent = followable;
	}

	@Override
	public Followable getParent() {
		return parent;
	}

	@Override
	public Point getFollowOffset() {
		if (followOffset == null)
			followOffset = new Point(0, 0);
		return Utils.copy(followOffset);
	}

	@Override
	public void setFollowOffset(int x, int y) {
		if (followOffset == null)
			followOffset = new Point(x, y);
		else {
			followOffset.x = x;
			followOffset.y = y;
		}
	}

	public void setFollowOffset(Point p) {
		setFollowOffset(p.x, p.y);
	}

	@Override
	public Set<Follower> getFollowers() {
		if (followers == null)
			followers = new HashSet<Follower>();
		return Collections.unmodifiableSet(followers);
	}

	@Override
	public boolean addFollower(Follower fol) {
		if (followers == null)
			followers = new HashSet<Follower>();
		return followers.add(fol);
	}

	@Override
	public boolean removeFollower(Follower fol) {
		if (followers == null)
			followers = new HashSet<Follower>();
		return followers.remove(fol);
	}

	@Override
	public int getFollowerCount() {
		if (followers == null)
			followers = new HashSet<Follower>();
		return followers.size();
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
	public void updateFollower() {
		updateBoundingArea();
		Followable parent = getParent();
		if (parent == null)
			throw new NullPointerException("Parent is null.");
		Point offset = getFollowOffset();

		reposition(parent.getX() + offset.x, parent.getY() + offset.y);
		updateBoundingArea();
	}

	@Override
	public void setCollidable(boolean collidable) {
		model.setCollidable(collidable);
	}

	@Override
	public boolean isCollidable() {
		return model.isCollidable();
	}

	public void setMoving(boolean mov) {
		moving = mov && isSelected() && !isPinned();
	}

	public boolean isMoving() {
		return moving;
	}

	public void setVisible(boolean vis) {
		view.setVisible(vis);
		redraw();

		if (eventHandler != null)
			eventHandler.sendEvent(new SLEvent(SLEvent.VISIBLE, this, vis));
	}

	public boolean isVisible() {
		return view.isVisible();
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
	 * Sets the highlighted state of the controller.
	 */
	public void setHighlighted(boolean high) {
		view.setHighlighted(high);
		updateView();
		redraw();
	}

	public boolean isHighlighted() {
		return view.isHighlighted();
	}

	public DataComposite getDataComposite(Composite parent) {
		if (selectable)
			throw new IllegalStateException("This Controller is selectable, but doesn't define a DataComposite");
		else
			return null;
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (Manager.getSelectedToolId() == Tools.POINTER) {
			boolean contains = contains(e.x, e.y);
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
				if (!contains(e.x, e.y) && !contains(getX() + clickOffset.x, getY() + clickOffset.y) && selected)
					Manager.setSelected(null);
				setMoving(false);
			}
		}
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if (Manager.getSelectedToolId() == Tools.POINTER) {
			if (selected && moving) {
				Point p = Grid.getInstance().snapToGrid(e.x - clickOffset.x, e.y - clickOffset.y, snapmode);
				if (p.x != getX() || p.y != getY()) {
					reposition(p.x, p.y);
					updateFollowOffset();

					((DataComposite) EditorWindow.getInstance().getSidebarContent()).updateData();
				}
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

	public boolean collides(Rectangle rect) {
		return collides(rect.x, rect.y, rect.width, rect.height);
	}

	@Override
	public boolean collides(int x, int y, int w, int h) {
		Rectangle rect = new Rectangle(x + getTolerance(), y + getTolerance(),
				w - 2 * getTolerance(), h - 2 * getTolerance());
		return rect.intersects(new Rectangle(getX() - getW() / 2, getY() - getH() / 2, getW(), getH()));
	}

	@Override
	public boolean isBounded() {
		return model.isBounded();
	}

	@Override
	public void setBounded(boolean bound) {
		model.setBounded(bound);
	}

	public boolean isWithinBoundingArea(int x, int y) {
		return model.isWithinBoundingArea(x, y);
	}

	public Point limitToBoundingArea(int x, int y) {
		return model.limitToBoundingArea(x, y);
	}

	public Rectangle getBoundingArea() {
		return model.getBoundingArea();
	}

	public int getBoundingAreaX() {
		return model.getBoundingAreaX();
	}

	public int getBoundingAreaY() {
		return model.getBoundingAreaY();
	}

	public int getBoundingAreaW() {
		return model.getBoundingAreaW();
	}

	public int getBoundingAreaH() {
		return model.getBoundingAreaH();
	}

	public void setBoundingArea(int x, int y, int w, int h) {
		model.setBoundingArea(x, y, w, h);
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

	public Point[] getBoundingPoints() {
		Rectangle b = model.getBoundingArea();
		return new Point[] {
				new Point(b.x, b.y), new Point(b.x + b.width, b.y + b.height)
		};
	}

	public void updateBoundingArea() {
	}

	public void dispose() {
		if (model.isDisposed())
			return;
		if (eventHandler != null)
			eventHandler.sendEvent(new SLEvent(SLEvent.DISPOSE, this, this));

		model.dispose();
		view.dispose();

		for (PropController prop : getProps()) {
			prop.dispose();
		}

		if (eventHandler != null)
			eventHandler.dispose();
	}

	@Override
	public boolean contains(int x, int y) {
		return getBounds().contains(x, y); // Some controllers modify their bounds, so use that instead of the model's
	}

	@Override
	public boolean intersects(Rectangle rect) {
		return getBounds().intersects(rect); // Some controllers modify their bounds, so use that instead of the model's
	}

	public void setLocModifiable(boolean b) {
		model.setLocModifiable(b);
	}

	public boolean isLocModifiable() {
		return model.isLocModifiable();
	}

	@Override
	public void delete() {
		deleted = true;
		view.removeFromPainter();
		setVisible(false);

		if (eventHandler != null && eventHandler.hooks(SLEvent.DELETE))
			eventHandler.sendEvent(new SLEvent(SLEvent.DELETE, this, this));

		for (PropController prop : getProps()) {
			prop.removeFromPainter();
			prop.redraw();
		}
	}

	@Override
	public void restore() {
		deleted = false;
		setView(view);
		setVisible(true);

		if (eventHandler != null && eventHandler.hooks(SLEvent.RESTORE))
			eventHandler.sendEvent(new SLEvent(SLEvent.RESTORE, this, this));

		for (PropController prop : getProps()) {
			prop.setView(prop.getView());
			prop.redraw();
		}
	}

	public boolean isDeleted() {
		return deleted;
	}

	@Override
	public void setDeletable(boolean deletable) {
		model.setDeletable(deletable);
	}

	@Override
	public boolean isDeletable() {
		return model.isDeletable();
	}

	@Override
	public void setTolerance(int px) {
		model.setTolerance(px);
	}

	@Override
	public int getTolerance() {
		return model.getTolerance();
	}

	public void addListener(int eventType, SLListener listener) {
		if (eventHandler == null)
			eventHandler = new EventHandler();
		eventHandler.hook(eventType, listener);
	}

	public void removeListener(int eventType, SLListener listener) {
		if (eventHandler == null)
			eventHandler = new EventHandler();
		eventHandler.unhook(eventType, listener);
	}

	public void handleEvent(SLEvent e) {
		if (e.type == SLEvent.MOVE) {
			Point p = (Point) e.data;
			notifyLocationChanged(p.x, p.y);
		} else if (e.type == SLEvent.RESIZE) {
			Point p = (Point) e.data;
			notifySizeChanged(p.x, p.y);
		} else if (e.type == SLEvent.DISPOSE) {
			if (eventHandler != null && e.data != null && e.data instanceof SLListener) {
				eventHandler.unhook((SLListener) e.data);
			}
		}
	}

	public void notifyLocationChanged(int x, int y) {
	}

	public void notifySizeChanged(int w, int h) {
		resize(w, h);
	}

	public void addProp(PropController prop) {
		if (props == null)
			props = new HashSet<PropController>();
		if (getProp(prop.getIdentifier()) != null)
			throw new IllegalArgumentException(String.format("This object already owns a prop named '%s'", prop.getIdentifier()));
		props.add(prop);
		addListener(SLEvent.VISIBLE, prop);
	}

	public void removeProp(PropController prop) {
		if (props == null)
			props = new HashSet<PropController>();
		props.remove(prop);
		removeListener(SLEvent.VISIBLE, prop);
	}

	public PropController[] getProps() {
		if (props == null)
			return new PropController[0];
		return props.toArray(new PropController[0]);
	}

	/**
	 * @param id
	 *            the identifier of the sought prop
	 * @return prop with the given identifier, or null if not found
	 */
	public PropController getProp(String id) {
		if (props == null) {
			return null;
		} else {
			PropController result = null;
			Iterator<PropController> it = props.iterator();
			while (it.hasNext() && result == null) {
				PropController p = it.next();
				if (p.getIdentifier().equals(id))
					result = p;
			}

			return result;
		}
	}

	public static boolean collidesAs(Rectangle rect, AbstractController col) {
		CollisionPredicate predicate = new CollisionPredicate(rect, col);
		Layers layer = ((AbstractController) col).view.getLayerId();
		return LayeredPainter.getInstance().getControllerMatching(predicate, layer) != null;
	}

	public static class CollisionPredicate implements Predicate<AbstractController> {
		private final Rectangle b;
		private final Controller controller;

		public CollisionPredicate(Rectangle b, AbstractController controller) {
			this.b = b;
			this.controller = controller;
		}

		@Override
		public boolean accept(AbstractController control) {
			return control.isVisible() && control.isCollidable() &&
					control != controller && control.collides(b);
		}
	}

	public void updateFollowOffset() {
		if (getParent() != null) {
			Point p = getLocation();
			setFollowOffset(p.x - getParent().getX(), p.y - getParent().getY());
		}
	}
}
