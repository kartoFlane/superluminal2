package com.kartoflane.superluminal2.ftl;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.components.interfaces.Indexable;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;
import com.kartoflane.superluminal2.ui.ShipContainer;

public class GibObject extends ImageObject implements Indexable, Comparable<GibObject> {

	private int id = -1;

	private int directionMin = 0;
	private int directionMax = 0;
	private double velocityMin = 0;
	private double velocityMax = 0;
	private double angularMin = 0;
	private double angularMax = 0;

	private int offsetX = 0;
	private int offsetY = 0;

	/**
	 * Creates a new default gib object.
	 */
	public GibObject() {
		setDeletable(true);
	}

	public void update() {
		if (model == null)
			throw new IllegalArgumentException("Model must not be null.");

		ShipController shipC = Manager.getCurrentShip().getShipController();
		ShipObject ship = shipC.getGameObject();

		Point hullOffset = ship.getHullOffset();
		offsetX = model.getX() - shipC.getX() - hullOffset.x - (ship.getXOffset() * ShipContainer.CELL_SIZE) - model.getW() / 2;
		offsetY = model.getY() - shipC.getY() - hullOffset.y - (ship.getYOffset() * ShipContainer.CELL_SIZE) - model.getH() / 2;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
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

	public void setVelocityMin(double min) {
		velocityMin = min;
	}

	public double getVelocityMin() {
		return velocityMin;
	}

	public void setVelocityMax(double max) {
		velocityMax = max;
	}

	public double getVelocityMax() {
		return velocityMax;
	}

	public void setAngularMin(double min) {
		angularMin = min;
	}

	public double getAngularMin() {
		return angularMin;
	}

	public void setAngularMax(double max) {
		angularMax = max;
	}

	public double getAngularMax() {
		return angularMax;
	}

	@Override
	public int compareTo(GibObject o) {
		return id - o.id;
	}
}
