package com.kartoflane.superluminal2.ftl;

public class WeaponList extends BlueprintList<WeaponObject> {

	private static final long serialVersionUID = 4618623391139370151L;

	/**
	 * Creates the default weapon list object.
	 */
	public WeaponList() {
		super("No Weapon List");
	}

	public WeaponList(String blueprint) {
		super(blueprint);
	}
}