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
public class WeaponObject {

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
	 * Creates a default weapon object, used by the Mount Tool.
	 */
	public WeaponObject() {
		weaponType = null;
		blueprintName = null;
		shortName = "No Weapon";
		sheetPath = "/assets/weapon.png";
		sheetSize.x = frameSize.x = 16;
		sheetSize.y = frameSize.y = 50;
		mountOffset.x = 2; // TODO ?
		mountOffset.y = 36; // TODO ?
	}

	public WeaponObject(WeaponTypes type) {
		weaponType = type;
		// TODO ?
	}

	public WeaponTypes getType() {
		return weaponType;
	}

	public String getBlueprintName() {
		return blueprintName;
	}

	public String getShortName() {
		return shortName;
	}

	public Point getSheetSize() {
		return Utils.copy(sheetSize);
	}

	public Point getFrameSize() {
		return Utils.copy(frameSize);
	}

	public Point getMountOffset() {
		return Utils.copy(mountOffset);
	}

	public String getSheetPath() {
		return sheetPath;
	}
}
