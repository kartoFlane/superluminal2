package com.kartoflane.superluminal2.components.interfaces;

/**
 * A simple interface to allow to determine whether an object of a specific type meets
 * certain arbitrary conditions specified by the user.
 * 
 * @author kartoFlane
 * 
 * @param <T>
 *            type of the object which is to be checked
 */
public interface Predicate<T> {
	public boolean accept(T object);
}
