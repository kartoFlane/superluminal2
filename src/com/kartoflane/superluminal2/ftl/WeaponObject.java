package com.kartoflane.superluminal2.ftl;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.core.Database.WeaponTypes;
import com.kartoflane.superluminal2.core.Utils;

/**
 * A class representing a weapon. Since it serves only as a data vessel
 * for MountObject's internal use, and is never drawn or displayed in
 * any way, it has no Controller or View counterparts.
 * 
 * @author kartoFlane
 * 
 */
public class WeaponObject extends GameObject implements Comparable<WeaponObject> {

	private static final long serialVersionUID = 7968532944693929010L;

	private WeaponTypes weaponType;
	private String blueprintName;
	private String title;
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
		title = "No Weapon";
		shortName = "No Weapon";
		sheetPath = "cpath:/assets/weapon.png";
		sheetSize.x = frameSize.x = 16;
		sheetSize.y = frameSize.y = 50;
		mountOffset.x = 2;
		mountOffset.y = 36;
	}

	public WeaponObject(String blueprintName) {
		if (blueprintName == null)
			throw new IllegalArgumentException("Blueprint name must not be null.");
		this.blueprintName = blueprintName;
	}

	public void setType(WeaponTypes type) {
		weaponType = type;
	}

	public WeaponTypes getType() {
		return weaponType;
	}

	public void setBlueprintName(String name) {
		if (name == null)
			throw new IllegalArgumentException("Name must not be null.");
		blueprintName = name;
	}

	public String getBlueprintName() {
		return blueprintName;
	}

	public void setTitle(String title) {
		if (title == null)
			throw new IllegalArgumentException("Title must not be null.");
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setShortName(String name) {
		if (name == null)
			throw new IllegalArgumentException("Name must not be null.");
		shortName = name;
	}

	public String getShortName() {
		return shortName;
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
			throw new IllegalArgumentException("Path must not be null.");
		sheetPath = path;
	}

	@Override
	public int compareTo(WeaponObject o) {
		return blueprintName.compareTo(o.blueprintName);
	}
}
