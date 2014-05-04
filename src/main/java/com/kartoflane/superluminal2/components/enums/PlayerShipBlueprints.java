package com.kartoflane.superluminal2.components.enums;

public enum PlayerShipBlueprints {
	HARD, HARD_2, HARD_3,
	MANTIS, MANTIS_2, MANTIS_3,
	STEALTH, STEALTH_2, STEALTH_3,
	CIRCLE, CIRCLE_2, CIRCLE_3,
	FED, FED_2, FED_3,
	JELLY, JELLY_2, JELLY_3,
	ROCK, ROCK_2, ROCK_3,
	ENERGY, ENERGY_2, ENERGY_3,
	CRYSTAL, CRYSTAL_2,
	ANAEROBIC, ANAEROBIC_2;

	@Override
	public String toString() {
		return "PLAYER_SHIP_" + name();
	}
}