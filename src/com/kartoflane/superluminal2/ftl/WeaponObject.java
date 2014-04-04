package com.kartoflane.superluminal2.ftl;

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
		LASER, MISSILES, BOMB, BEAM
	}

	public final WeaponTypes weaponType;
	public final AnimObject anim;
	private String blueprintName;
	private String shortName;
	private String spritePath;

	public WeaponObject(WeaponTypes type, AnimObject anim) {
		weaponType = type;
		this.anim = anim;
	}
}
