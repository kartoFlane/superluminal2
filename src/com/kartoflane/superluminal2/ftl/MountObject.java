package com.kartoflane.superluminal2.ftl;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.components.enums.Directions;
import com.kartoflane.superluminal2.components.interfaces.Alias;
import com.kartoflane.superluminal2.components.interfaces.Movable;
import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.controllers.ShipController;
import com.kartoflane.superluminal2.ui.ShipContainer;

public class MountObject extends GameObject implements Alias, Movable, Comparable<MountObject> {

	private static final long serialVersionUID = 2647004106297814245L;

	private int locX = 0;
	private int locY = 0;

	private int id = -2;
	private boolean mirrored = false;
	private boolean rotated = false;
	private GibObject gib = null;
	private Directions direction = Directions.UP;

	private WeaponObject weapon = null;

	private String alias = null;

	public MountObject() {
		setDeletable(true);
		setWeapon(Database.DEFAULT_WEAPON_OBJ);
	}

	public void update() {
		ShipController shipC = Manager.getCurrentShip().getShipController();
		ShipObject ship = shipC.getGameObject();
		locX = model.getX() - shipC.getX() - ship.getXOffset() * ShipContainer.CELL_SIZE - ship.getHullOffset().x;
		locY = model.getY() - shipC.getY() - ship.getYOffset() * ShipContainer.CELL_SIZE - ship.getHullOffset().y;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setWeapon(WeaponObject weapon) {
		this.weapon = weapon;
	}

	public WeaponObject getWeapon() {
		return weapon;
	}

	@Override
	public boolean setLocation(int x, int y) {
		locX = x;
		locY = y;
		return true;
	}

	@Override
	public boolean translate(int dx, int dy) {
		locX += dx;
		locY += dy;
		return true;
	}

	@Override
	public Point getLocation() {
		return new Point(locX, locY);
	}

	@Override
	public int getX() {
		return locX;
	}

	@Override
	public int getY() {
		return locY;
	}

	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setMirrored(boolean mirrored) {
		this.mirrored = mirrored;
	}

	public boolean isMirrored() {
		return mirrored;
	}

	public void setRotated(boolean rotated) {
		this.rotated = rotated;
	}

	public boolean isRotated() {
		return rotated;
	}

	public void setGib(GibObject gib) {
		this.gib = gib;
	}

	public GibObject getGib() {
		return gib;
	}

	public void setDirection(Directions direction) {
		this.direction = direction;
	}

	public Directions getDirection() {
		return direction;
	}

	@Override
	public int compareTo(MountObject o) {
		return id - o.id;
	}
}
