package com.kartoflane.superluminal2.ftl;

import java.util.HashMap;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.components.enums.WeaponStats;
import com.kartoflane.superluminal2.components.enums.WeaponTypes;
import com.kartoflane.superluminal2.components.interfaces.Identifiable;
import com.kartoflane.superluminal2.core.Database;

/**
 * A class representing a weapon. Since it serves only as a data vessel
 * for MountObject's internal use, and is never drawn or displayed in
 * any way, it has no Controller or View counterparts.
 * 
 * @author kartoFlane
 * 
 */
public class WeaponObject extends GameObject implements Comparable<WeaponObject>, Identifiable {

	private static final long serialVersionUID = 7968532944693929010L;

	private final String blueprintName;
	private WeaponTypes weaponType;
	private String title = "";
	private String shortName = "";
	private String description = "";

	private HashMap<WeaponStats, Float> statMap = null;

	private AnimationObject animation;

	/**
	 * Creates a default weapon object.
	 */
	public WeaponObject() {
		weaponType = null;
		blueprintName = "Default Weapon";
		title = "<No Weapon>";
		shortName = "<No Weapon>";
		animation = Database.DEFAULT_ANIM_OBJ;
	}

	public WeaponObject(String blueprint) {
		blueprintName = blueprint;
	}

	@Override
	public String getIdentifier() {
		return blueprintName;
	}

	public void update() {
		// Nothing to do here
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

	public void setDescription(String desc) {
		if (desc == null)
			throw new IllegalArgumentException(blueprintName + ": description must not be null.");
		description = desc;
	}

	public String getDescription() {
		return description;
	}

	public void setStat(WeaponStats stat, float value) {
		if (stat == null)
			throw new IllegalArgumentException("Stat type must not be null.");
		if (statMap == null)
			initStatMap();
		statMap.put(stat, value);
	}

	public float getStat(WeaponStats stat) {
		if (stat == null)
			throw new IllegalArgumentException("Stat type must not be null.");
		if (statMap == null)
			initStatMap();
		return statMap.get(stat);
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

	private void initStatMap() {
		statMap = new HashMap<WeaponStats, Float>();
		for (WeaponStats stat : WeaponStats.values())
			statMap.put(stat, 0f);
	}

	@Override
	public String toString() {
		return shortName;
	}

	@Override
	public int compareTo(WeaponObject o) {
		return blueprintName.compareTo(o.blueprintName);
	}
}
