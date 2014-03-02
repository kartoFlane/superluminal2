package com.kartoflane.superluminal2.mvc.models;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.kartoflane.superluminal2.components.interfaces.Followable;
import com.kartoflane.superluminal2.components.interfaces.Follower;
import com.kartoflane.superluminal2.mvc.Model;

/**
 * A Model that cannot exist on its own, and depends on another Model.
 * 
 * @author kartoFlane
 * 
 */
public abstract class AbstractDependentModel implements Model, Follower {
	protected Point followOffset = null;

	/**
	 * Sets the Model that this Model depends on.
	 * 
	 * @param model
	 *            the Model that this Model depends on.
	 *            Used as source for this Model's location, size and bounds.
	 */
	protected abstract void setDependency(Model model);

	/**
	 * @return the Model that this Model depends on. Used as source for this Model's location, size and bounds.
	 */
	protected abstract Model getDependency();

	@Override
	public void setSize(int w, int h) {
		throw new NotImplementedException();
	}

	@Override
	public void setLocation(int x, int y) {
		throw new NotImplementedException();
	}

	@Override
	public void translate(int dx, int dy) {
		throw new NotImplementedException();
	}

	@Override
	public Point getLocation() {
		Model model = getDependency();
		return model == null ? new Point(0, 0) : model.getLocation();
	}

	@Override
	public int getX() {
		Model model = getDependency();
		return model == null ? 0 : model.getX() + getFollowOffset().x;
	}

	@Override
	public int getY() {
		Model model = getDependency();
		return model == null ? 0 : model.getY() + getFollowOffset().y;
	}

	@Override
	public Point getSize() {
		Model model = getDependency();
		return model == null ? new Point(0, 0) : model.getSize();
	}

	@Override
	public int getW() {
		Model model = getDependency();
		return model == null ? 0 : model.getW();
	}

	@Override
	public int getH() {
		Model model = getDependency();
		return model == null ? 0 : model.getH();
	}

	@Override
	public Rectangle getBounds() {
		Model model = getDependency();
		return model == null ? new Rectangle(0, 0, 0, 0) : model.getBounds();
	}

	public void setLocModifiable(boolean b) {
		throw new NotImplementedException();
	}

	public final boolean isLocModifiable() {
		return getDependency().isLocModifiable();
	}

	public void setSizeModifiable(boolean b) {
		throw new NotImplementedException();
	}

	public final boolean isSizeModifiable() {
		return getDependency().isSizeModifiable();
	}

	public boolean contains(int x, int y) {
		Model model = getDependency();
		return model != null && model.contains(x, y);
	}

	public boolean contains(Point p) {
		return contains(p.x, p.y);
	}

	public boolean intersects(Rectangle rect) {
		Model model = getDependency();
		return model != null && model.intersects(rect);
	}

	@Override
	public void setParent(Followable followable) {
		throw new NotImplementedException();
	}

	@Override
	public Followable getParent() {
		throw new NotImplementedException();
	}

	@Override
	public void updateFollower() {
		throw new NotImplementedException();
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
}
