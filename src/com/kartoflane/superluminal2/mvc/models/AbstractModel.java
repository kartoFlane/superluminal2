package com.kartoflane.superluminal2.mvc.models;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.components.interfaces.Boundable;
import com.kartoflane.superluminal2.components.interfaces.Collidable;
import com.kartoflane.superluminal2.components.interfaces.Followable;
import com.kartoflane.superluminal2.components.interfaces.Follower;
import com.kartoflane.superluminal2.components.interfaces.Pinnable;
import com.kartoflane.superluminal2.components.interfaces.Predicate;
import com.kartoflane.superluminal2.components.interfaces.Resizable;
import com.kartoflane.superluminal2.core.Utils;
import com.kartoflane.superluminal2.mvc.Model;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.ui.EditorWindow;

/**
 * 
 * @author kartoFlane
 * 
 */
public class AbstractModel implements Model, Resizable, Collidable, Boundable, Follower, Pinnable, Followable, Serializable {
	private static final long serialVersionUID = -3183152886974835014L;

	protected Followable parent;
	protected HashSet<Follower> followers = null;

	protected Rectangle bounds = null;
	protected Point position = null;
	protected Point size = null;
	protected Point followOffset = null;
	protected Rectangle boundingArea = null;

	protected boolean bounded = false;
	protected boolean pinned = false;
	protected boolean locModifiable = true;
	protected boolean sizeModifiable = true;
	protected boolean collidable = false;
	protected boolean followableActive = true;

	public AbstractModel() {
		bounds = new Rectangle(0, 0, 0, 0);
		position = new Point(0, 0);
		size = new Point(0, 0);
	}

	/**
	 * Set whether the box is pinned, and control its pinned appearance.
	 * Pinned boxes cannot be moved or resized.<br>
	 * False by default.
	 */
	public void setPinned(boolean pin) {
		pinned = pin;
	}

	public final boolean isPinned() {
		return pinned;
	}

	/**
	 * Sets the object's center at the given coordinates.<br>
	 * If modifying both size and location, change the size first.
	 */
	@Override
	public void setLocation(int x, int y) {
		// if the new location falls outside of the area the object is
		// bounded to, change the parameters to be as close the border as possible
		if (isBounded()) {
			int nx = x, ny = y;

			if (x <= boundingArea.x)
				nx = boundingArea.x;
			else if (x >= boundingArea.x + boundingArea.width)
				nx = boundingArea.x + boundingArea.width;
			if (y <= boundingArea.y)
				ny = boundingArea.y;
			else if (y >= boundingArea.y + boundingArea.height)
				ny = boundingArea.y + boundingArea.height;

			x = nx;
			y = ny;
		}

		Rectangle b = new Rectangle(getX() - getW() / 2, getY() - getH() / 2, getW(), getH());
		b.x = x - getW() / 2;
		b.y = y - getH() / 2;

		if (isCollidable() && collidesAs(b, this))
			return;

		position.x = x;
		position.y = y;

		if (followableActive)
			for (Follower mov : getFollowers())
				mov.updateFollower();

		recalculateBounds();
	}

	/**
	 * Sets the object's center at the given coordinates.<br>
	 * If modifying both size and location, change the size first.
	 */
	public void setLocation(Point loc) {
		setLocation(loc.x, loc.y);
	}

	@Override
	public void translate(int dx, int dy) {
		// if the new location falls outside of the area the object is
		// bounded to, change the parameters to be as close the border as possible
		if (isBounded()) {
			int nx = dx, ny = dy;

			if (position.x + dx <= boundingArea.x)
				nx = boundingArea.x - position.x;
			else if (position.x + dx >= boundingArea.x + boundingArea.width)
				nx = boundingArea.x + boundingArea.width - position.x;
			if (position.y + dy <= boundingArea.y)
				ny = boundingArea.y - position.y;
			else if (position.y + dy >= boundingArea.y + boundingArea.height)
				ny = boundingArea.y + boundingArea.height - position.y;

			dx = nx;
			dy = ny;
		}

		Rectangle b = new Rectangle(getX() - getW() / 2, getY() - getH() / 2, getW(), getH());
		b.x += dx;
		b.y += dy;

		if (isCollidable() && collidesAs(b, this))
			return;

		position.x += dx;
		position.y += dy;

		recalculateBounds();
		if (followableActive)
			for (Follower mov : getFollowers())
				mov.updateFollower();

		recalculateBounds();
	}

	public void translate(Point displacement) {
		translate(displacement.x, displacement.y);
	}

	@Override
	public Point getLocation() {
		return new Point(position.x, position.y);
	}

	@Override
	public int getX() {
		return position.x;
	}

	@Override
	public int getY() {
		return position.y;
	}

	/** @return a new point representing the box's dimensions. */
	@Override
	public Point getSize() {
		return new Point(size.x, size.y);
	}

	/** @return width of the box */
	@Override
	public int getW() {
		return size.x;
	}

	/** @return height of the box */
	@Override
	public int getH() {
		return size.y;
	}

	/**
	 * Sets the box's dimensions to the given values.<br>
	 * If modifying both size and location, change the size first.
	 */
	@Override
	public void setSize(int w, int h) {
		if (w < 0 || h < 0)
			throw new IllegalArgumentException("Room's dimensions must be non-negative integers.");

		size.x = w;
		size.y = h;

		recalculateBounds();
	}

	/**
	 * Sets the box's dimensions to the given values.<br>
	 * If modifying both size and location, change the size first.
	 */
	public void setSize(Point size) {
		setSize(size.x, size.y);
	}

	@Override
	public Rectangle getBounds() {
		return Utils.copy(bounds);
	}

	protected void recalculateBounds() {
		bounds.x = position.x - size.x / 2 - 1;
		bounds.y = position.y - size.y / 2 - 1;
		bounds.width = size.x + 2;
		bounds.height = size.y + 2;
	}

	/**
	 * Flags this box's location value as modifiable via the sidebar UI.<br>
	 * True by default.
	 */
	public void setLocModifiable(boolean b) {
		locModifiable = b;
	}

	/** Whether this box's location value can be modified via the sidebar UI. */
	public final boolean isLocModifiable() {
		return locModifiable;
	}

	/**
	 * Flags this box's size value as modifiable via the sidebar UI.<br>
	 * True by default.
	 */
	public void setSizeModifiable(boolean b) {
		sizeModifiable = b;
	}

	/** Whether this box's size value can be modified via the sidebar UI. */
	public final boolean isSizeModifiable() {
		return sizeModifiable;
	}

	/**
	 * Checks whether the Followable is fit for the role of a parent.<br>
	 * Throws appropriate exception when it is not.
	 */
	public final void checkParentConditions(Followable followable) {
		if (followable != null && !(followable instanceof Model)) // instanceof returns false for null, but we allow null argument
			throw new IllegalArgumentException("Followable passed in argument is not a Model.");
		if (followable == this)
			throw new IllegalArgumentException("Cannot be parent to itself.");
		if (followable instanceof Follower && ((Follower) followable).getParent() == this)
			throw new IllegalArgumentException("Cannot follow an object that is following the receiver.");
		if (followable != null && parent == followable)
			throw new IllegalArgumentException("Already following this object.");
	}

	@Override
	public void setParent(Followable followable) {
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
		return new Point(followOffset.x, followOffset.y);
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

	@Override
	public void updateFollower() {
		if (parent == null)
			return;

		Point offset = getFollowOffset();
		setLocation(parent.getX() + offset.x, parent.getY() + offset.y);
	}

	@Override
	public boolean contains(int x, int y) {
		return bounds.contains(x, y);
	}

	public boolean contains(Point p) {
		return contains(p.x, p.y);
	}

	@Override
	public boolean intersects(Rectangle rect) {
		return bounds.intersects(rect);
	}

	@Override
	public Set<Follower> getFollowers() {
		if (followers == null)
			followers = new HashSet<Follower>();
		return Collections.unmodifiableSet(followers);
	}

	@Override
	public boolean addFollower(Follower fol) {
		return followers.add(fol);
	}

	@Override
	public boolean removeFollower(Follower fol) {
		return followers.remove(fol);
	}

	@Override
	public int getFollowerCount() {
		return getFollowers().size();
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
		this.collidable = collidable;
	}

	@Override
	public boolean isCollidable() {
		return collidable;
	}

	@Override
	public boolean collides(int x, int y, int w, int h) {
		Rectangle rect = new Rectangle(x, y, w, h);
		return rect.intersects(getBounds());
	}

	public static boolean collidesAs(Rectangle rect, Collidable col) {
		if (col instanceof Model) {
			CollisionPredicate predicate = new CollisionPredicate(rect, (Model) col);
			AbstractController controller = null;
			Layers[] collidableLayers = { Layers.DOOR, Layers.ROOM };
			for (Layers layer : collidableLayers) {
				controller = EditorWindow.getInstance().getPainter().getControllerMatching(predicate, layer);
			}
			return controller != null;
		} else
			return false;
	}

	private static class CollisionPredicate implements Predicate<AbstractController> {
		private final Rectangle b;
		private final Model model;

		public CollisionPredicate(Rectangle b, Model model) {
			this.b = b;
			this.model = model;
		}

		@Override
		public boolean accept(AbstractController object) {
			return object.isVisible() && object.isSelectable() && object.isCollidable() &&
					object.getModel() != model &&
					object.collides(b.x, b.y, b.width, b.height);
		}
	}

	@Override
	public boolean isBounded() {
		return bounded;
	}

	@Override
	public void setBounded(boolean bound) {
		bounded = bound;
		if (bound && boundingArea == null)
			boundingArea = new Rectangle(0, 0, 0, 0);
	}

	@Override
	public Rectangle getBoundingArea() {
		if (boundingArea == null)
			boundingArea = new Rectangle(0, 0, 0, 0);
		return Utils.copy(boundingArea);
	}

	@Override
	public void setBoundingArea(int x, int y, int w, int h) {
		if (boundingArea == null)
			boundingArea = new Rectangle(0, 0, 0, 0);
		boundingArea.x = x;
		boundingArea.y = y;
		boundingArea.width = w;
		boundingArea.height = h;
	}

	public void setBoundingArea(Rectangle r) {
		setBoundingArea(r.x, r.y, r.width, r.height);
	}

	public void setBoundingArea(Point start, Point end) {
		setBoundingArea(start.x, start.y, end.x - start.x, end.y - start.y);
	}
}
