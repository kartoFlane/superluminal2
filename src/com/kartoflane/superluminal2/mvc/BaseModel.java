package com.kartoflane.superluminal2.mvc;

import java.io.Serializable;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal2.components.interfaces.Boundable;
import com.kartoflane.superluminal2.components.interfaces.Collidable;
import com.kartoflane.superluminal2.components.interfaces.Deletable;
import com.kartoflane.superluminal2.components.interfaces.Disposable;
import com.kartoflane.superluminal2.components.interfaces.Movable;
import com.kartoflane.superluminal2.components.interfaces.Pinnable;
import com.kartoflane.superluminal2.components.interfaces.Resizable;
import com.kartoflane.superluminal2.core.Utils;

public class BaseModel implements Model, Movable, Resizable, Disposable, Deletable,
		Collidable, Boundable, Pinnable, Serializable {

	private static final long serialVersionUID = 4410723942852766462L;

	protected Rectangle bounds = null;
	protected Point position = null;
	protected Point size = null;
	protected Point followOffset = null;
	protected Rectangle boundingArea = null;

	protected int collisionTolerance = 0;

	protected boolean deletable = true;
	protected boolean bounded = false;
	protected boolean pinned = false;
	protected boolean collidable = false;
	protected boolean followableActive = true;
	protected boolean locModifiable = true;

	public BaseModel() {
		bounds = new Rectangle(0, 0, 0, 0);
		position = new Point(0, 0);
		size = new Point(0, 0);
	}

	@Override
	public void setPinned(boolean pin) {
		pinned = pin;
	}

	@Override
	public final boolean isPinned() {
		return pinned;
	}

	/**
	 * Sets the object's center at the given coordinates.<br>
	 * If modifying both size and location, change the size first.
	 */
	@Override
	public boolean setLocation(int x, int y) {
		position.x = x;
		position.y = y;

		recalculateBounds();
		return true;
	}

	/**
	 * Sets the object's center at the given coordinates.<br>
	 * If modifying both size and location, change the size first.
	 */
	public boolean setLocation(Point loc) {
		return setLocation(loc.x, loc.y);
	}

	@Override
	public boolean translate(int dx, int dy) {
		position.x += dx;
		position.y += dy;

		recalculateBounds();
		return true;
	}

	public boolean translate(Point displacement) {
		return translate(displacement.x, displacement.y);
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
	 * Sets the model's dimensions to the given values.<br>
	 * If modifying both size and location, change the size first.
	 */
	@Override
	public boolean setSize(int w, int h) {
		if (w < 0 || h < 0)
			throw new IllegalArgumentException("Room's dimensions must be non-negative integers.");

		size.x = w;
		size.y = h;

		recalculateBounds();
		return true;
	}

	/**
	 * Sets the model's dimensions to the given values.<br>
	 * If modifying both size and location, change the size first.
	 */
	public boolean setSize(Point size) {
		return setSize(size.x, size.y);
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

	@Override
	public void dispose() {
		// nothing to do here
	}

	@Override
	public void delete() {
		// nothing to do here
	}

	@Override
	public void restore() {
		// nothing to do here
	}

	@Override
	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

	@Override
	public boolean isDeletable() {
		return deletable;
	}

	@Override
	public void setTolerance(int px) {
		collisionTolerance = px;
	}

	@Override
	public int getTolerance() {
		return collisionTolerance;
	}

	/**
	 * Flags this model's location value as modifiable via the sidebar UI.<br>
	 * True by default.
	 */
	public void setLocModifiable(boolean b) {
		locModifiable = b;
	}

	/** @return true if this model's location value can be modified via the sidebar UI, false otherwise. */
	public final boolean isLocModifiable() {
		return locModifiable;
	}
}
