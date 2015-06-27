package com.kartoflane.ftl.layout;

/**
 * A dumb representation of a single-value layout object,
 * ie. X_OFFSET, Y_OFFSET, HORIZONTAL or VERTICAL
 * 
 * @author kartoFlane
 *
 */
public class SingleValueLayoutObject extends LayoutObject {

	public SingleValueLayoutObject(LOType type, int i) {
		super(type, 1);
		values[0] = i;
	}

	public int getValue() {
		return values[0];
	}
}
