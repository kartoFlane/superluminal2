package com.kartoflane.superluminal2.components.enums;

public enum Races {
	NO_CREW,
	HUMAN,
	ENGI,
	ENERGY,
	ROCK,
	MANTIS,
	SLUG,
	CRYSTAL,
	ANAEROBIC,
	GHOST,
	RANDOM;

	@Override
	public String toString() {
		switch (this) {
			case NO_CREW:
				return "No Crew";
			case ENERGY:
				return "Zoltan";
			case ANAEROBIC:
				return "Lanius";
			default:
				String s = name();
				s = s.substring(0, 1) + s.substring(1).toLowerCase();
				return s;
		}
	}

	/**
	 * @return an array containing all races, sans NO_CREW
	 */
	public static Races[] getRaces() {
		return new Races[] {
				HUMAN, ENGI, ENERGY, ROCK,
				MANTIS, SLUG, CRYSTAL,
				ANAEROBIC, GHOST, RANDOM
		};
	}

	/**
	 * @return an array containing all races, sans NO_CREW and RANDOM
	 */
	public static Races[] getPlayerRaces() {
		return new Races[] {
				HUMAN, ENGI, ENERGY, ROCK,
				MANTIS, SLUG, CRYSTAL,
				ANAEROBIC, GHOST
		};
	}
}
