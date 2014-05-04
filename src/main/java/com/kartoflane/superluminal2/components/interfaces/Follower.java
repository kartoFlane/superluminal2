package com.kartoflane.superluminal2.components.interfaces;

import org.eclipse.swt.graphics.Point;

/**
 * Classes implementing this interface can follow classes implementing
 * the {@link Followable} interface.<br>
 * "Follow" in this case means that whenever the parent is moved, the
 * follower will move with it, at the offset specified by {@link #setFollowOffset(int, int)}
 * 
 * @author kartoFlane
 * 
 */
public interface Follower {
	/**
	 * Sets the parent of this Follower to the Followable specified in argument.
	 * 
	 * @param followable
	 *            the parent this follower will attach to
	 */
	public void setParent(Followable followable);

	/** Returns the parent of this Follower */
	public Followable getParent();

	/** Returns the offset at which this follower follows its parent. */
	public Point getFollowOffset();

	/** Sets the offset at which this follower follows its parent. */
	public void setFollowOffset(int x, int y);

	/** Updates this follower when its parent has been moved. */
	public void updateFollower();
}
