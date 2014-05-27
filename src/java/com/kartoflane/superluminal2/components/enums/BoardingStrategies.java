package com.kartoflane.superluminal2.components.enums;

public enum BoardingStrategies {
	SABOTAGE,
	INVASION;

	@Override
	public String toString() {
		String result = super.toString().toLowerCase();
		result = result.substring(0, 1).toUpperCase() + result.substring(1);
		return result;
	}
}
