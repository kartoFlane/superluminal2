package com.kartoflane.superluminal2.ftl;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.core.Database;
import com.kartoflane.superluminal2.core.Database.WeaponTypes;

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

	private AnimationObject animation;

	/**
	 * Creates a default weapon object.
	 */
	public WeaponObject() {
		weaponType = null;
		blueprintName = null;
		title = "No Weapon";
		shortName = "No Weapon";
		animation = Database.DEFAULT_ANIM_OBJ;
	}

	public WeaponObject(String blueprintName) {
		setBlueprintName(blueprintName);
	}

	public void setAnimation(AnimationObject anim) {
		if (anim == null)
			throw new IllegalArgumentException(blueprintName + ": animation must not be null.");
		animation = anim;
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
			throw new IllegalArgumentException(blueprintName + ": title must not be null.");
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setShortName(String name) {
		if (name == null)
			throw new IllegalArgumentException(blueprintName + ": name must not be null.");
		shortName = name;
	}

	public String getShortName() {
		return shortName;
	}

	public String getAnimName() {
		return animation.getAnimName();
	}

	public Point getSheetSize() {
		return animation.getSheetSize();
	}

	public Point getFrameSize() {
		return animation.getFrameSize();
	}

	public Point getMountOffset() {
		return animation.getMountOffset();
	}

	public String getSheetPath() {
		return animation.getSheetPath();
	}

	@Override
	public int compareTo(WeaponObject o) {
		return blueprintName.compareTo(o.blueprintName);
	}
}
