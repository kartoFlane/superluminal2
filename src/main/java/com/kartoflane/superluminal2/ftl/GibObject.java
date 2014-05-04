package com.kartoflane.superluminal2.ftl;

import org.eclipse.swt.graphics.Point;

public class GibObject extends GameObject implements Comparable<GibObject> {

	private static final long serialVersionUID = -3100645582205851970L;

	private int id = -1;
	private int locX = 0;
	private int locY = 0;

	private int directionMin = 0;
	private int directionMax = 0;
	private float velocityMin = 0;
	private float velocityMax = 0;
	private float angularMin = 0;
	private float angularMax = 0;

	private int offsetX = 0;
	private int offsetY = 0;

	public GibObject() {
		setDeletable(true);
	}

	public void update() {
		// TODO
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	/**
	 * Location of the gib in the editor.<br>
	 * Also used to position the gib during animation.
	 */
	public void setLocation(int x, int y) {
		locX = x;
		locY = y;
	}

	/** @return location of the gib in the editor */
	public Point getLocation() {
		return new Point(locX, locY);
	}

	public int getX() {
		return locX;
	}

	public int getY() {
		return locY;
	}

	/**
	 * The gib's offset from the hull's top left corner.<br>
	 * Use this to determine gib's position during exporting, as well
	 * as when resetting positions after animating.
	 */
	public void setOffset(int x, int y) {
		offsetX = x;
		offsetY = y;
	}

	/** @return gib's offset from the hull's top left corner */
	public Point getOffset() {
		return new Point(offsetX, offsetY);
	}

	public int getOffsetX() {
		return offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public void setDirectionMin(int min) {
		directionMin = min;
	}

	public int getDirectionMin() {
		return directionMin;
	}

	public void setDirectionMax(int max) {
		directionMax = max;
	}

	public int getDirectionMax() {
		return directionMax;
	}

	public void setVelocityMin(float min) {
		velocityMin = min;
	}

	public float getVelocityMin() {
		return velocityMin;
	}

	public void setVelocityMax(float max) {
		velocityMax = max;
	}

	public float getVelocityMax() {
		return velocityMax;
	}

	public void setAngularMin(float min) {
		angularMin = min;
	}

	public float getAngularMin() {
		return angularMin;
	}

	public void setAngularMax(float max) {
		angularMax = max;
	}

	public float getAngularMax() {
		return angularMax;
	}

	@Override
	public int compareTo(GibObject o) {
		return id - o.id;
	}
}
