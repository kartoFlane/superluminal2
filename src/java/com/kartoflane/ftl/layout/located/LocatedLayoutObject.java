package com.kartoflane.ftl.layout.located;

/**
 * A simple interface for layout objects to become aware of their
 * position in the file.
 * 
 * @author kartoFlane
 *
 */
public interface LocatedLayoutObject
{
	/**
	 * @return the line number at which the object is located.
	 */
	public int getLine();
}
