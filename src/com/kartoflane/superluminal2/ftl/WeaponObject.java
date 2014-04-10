package com.kartoflane.superluminal2.ftl;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.core.Utils;

/**
 * A class representing a weapon. Since it serves only as a data vessel
 * for MountObject's internal use, and is never drawn or displayed in
 * any way, it has no Controller or View counterparts.
 * 
 * @author kartoFlane
 * 
 */
public class WeaponObject extends GameObject {

	private static final long serialVersionUID = 7968532944693929010L;

	public enum WeaponTypes {
		LASER, MISSILES, BOMB, BEAM, BURST;
	}

	private final WeaponTypes weaponType;

	private String blueprintName;
	private String shortName;
	private String sheetPath;

	private Point sheetSize = new Point(0, 0);
	private Point frameSize = new Point(0, 0);
	private Point mountOffset = new Point(0, 0);

	/**
	 * Creates a default weapon object.
	 */
	public WeaponObject() {
		weaponType = null;
		blueprintName = null;
		shortName = "No Weapon";
		sheetPath = "/assets/weapon.png";
		sheetSize.x = frameSize.x = 16;
		sheetSize.y = frameSize.y = 50;
		mountOffset.x = 2;
		mountOffset.y = 36;
	}

	public WeaponObject(WeaponTypes type) {
		if (type == null)
			throw new NullPointerException("Type must not be null.");
		weaponType = type;
		// TODO ?
	}

	public WeaponTypes getType() {
		return weaponType;
	}

	public String getBlueprintName() {
		return blueprintName;
	}

	public void setBlueprintName(String name) {
		if (name == null)
			throw new NullPointerException("Name must not be null.");
		blueprintName = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String name) {
		if (name == null)
			throw new NullPointerException("Name must not be null.");
		shortName = name;
	}

	public Point getSheetSize() {
		return Utils.copy(sheetSize);
	}

	public void setSheetSize(int x, int y) {
		sheetSize.x = x;
		sheetSize.y = y;
	}

	public void setSheetSize(Point p) {
		setSheetSize(p.x, p.y);
	}

	public Point getFrameSize() {
		return Utils.copy(frameSize);
	}

	public void setFrameSize(int x, int y) {
		frameSize.x = x;
		frameSize.y = y;
	}

	public void setFrameSize(Point p) {
		setFrameSize(p.x, p.y);
	}

	public Point getMountOffset() {
		return Utils.copy(mountOffset);
	}

	public void setMountOffset(int x, int y) {
		mountOffset.x = x;
		mountOffset.y = y;
	}

	public void setMountOffset(Point p) {
		setMountOffset(p.x, p.y);
	}

	public String getSheetPath() {
		return sheetPath;
	}

	public void setSheetPath(String path) {
		if (path == null)
			throw new NullPointerException("Path must not be null.");
		sheetPath = path;
	}
}
