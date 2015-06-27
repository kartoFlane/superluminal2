package com.kartoflane.ftl.layout;

/**
 * A dumb representation of an ellipse as a layout object.
 * 
 * @author kartoFlane
 *
 */
public class EllipseLayoutObject extends LayoutObject {

	/**
	 * @param maj
	 *            half of the ellipse's width
	 * @param min
	 *            half of the ellipse's height
	 */
	public EllipseLayoutObject(int maj, int min, int x, int y) {
		super(LOType.ELLIPSE, 4);
		values[0] = maj;
		values[1] = min;
		values[2] = x;
		values[3] = y;
	}

	/**
	 * @return half of the ellipse's width
	 */
	public int getMajorAxis() {
		return values[0];
	}

	/**
	 * @return half of the ellipse's height
	 */
	public int getMinorAxis() {
		return values[1];
	}

	public int getX() {
		return values[2];
	}

	public int getY() {
		return values[3];
	}
}
