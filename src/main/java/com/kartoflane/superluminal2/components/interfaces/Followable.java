package com.kartoflane.superluminal2.components.interfaces;

import java.util.Set;

/**
 * Classes implementing this interface can be followed by classes implementing
 * the {@link Follower} interface.<br>
 * "Follow" in this case means that whenever the parent is moved, the
 * follower will move with it, at the offset specified by {@link Follower#setFollowOffset(int, int)}
 * 
 * @author kartoFlane
 * 
 */
public interface Followable extends Movable {
	/** @return a set containing all Followers of this object. */
	public Set<Follower> getFollowers();

	public boolean addFollower(Follower fol);

	public boolean removeFollower(Follower fol);

	/** @return the number of Followers currently following this object. */
	public int getFollowerCount();

	/**
	 * Sets whether the Followable is currently active, ie. notifying its followers.<br>
	 * True by default.
	 */
	public void setFollowActive(boolean active);

	/**
	 * @return whether the Followable is currently active, ie. notifying its followers.<br>
	 *         True by default.
	 */
	public boolean isFollowActive();
}
