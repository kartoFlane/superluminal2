package com.kartoflane.superluminal2.components.enums;

public enum WeaponTypes
{
	BEAM,
	BOMB,
	BURST,
	LASER,
	MISSILES;

	public String toString()
	{
		String s = name();
		return s.substring( 0, 1 ) + s.substring( 1 ).toLowerCase();
	}
}
