package com.kartoflane.superluminal2.components.enums;

public enum WeaponStats {
	/** Amount of damage dealt to the ship's hull, systems, and crew in hit room */
	DAMAGE,
	/** Amount of power bars that become locked down when a system is hit by this weapon */
	ION_DAMAGE,
	/** Amount of additional damage dealt to crew in hit rooms (amount of removed hp = [damage + pers_damage] * 15) */
	PERS_DAMAGE,
	/** Amount of additional damage dealt to systems */
	SYS_DAMAGE,
	/** Amount of shield layers ignored by the weapon */
	PIERCING,
	/** Change to cause a fire in hit rooms */
	FIRE_CHANCE,
	/** Chance to cause a hull breach in hit rooms */
	BREACH_CHANCE,
	/** Chance to stun all hostile crew in hit rooms */
	STUN_CHANCE,
	/** How many times the weapon shoots */
	SHOTS,
	/** How many power bars the weapon reserves when active */
	POWER_COST,
	/** How much the weapon costs to buy */
	SCRAP_COST,
	/** How many missiles the weapon consumes when it fires (not multiplied by SHOTS) */
	MISSILE_COST,
	/** How many seconds the weapon requires to become usable */
	COOLDOWN,
	/** Double damage on systemless rooms */
	HULL_BUST,
	/** Triggers Crystals' special ability on hit rooms */
	LOCKDOWN,
	/** For BURST type weapons, determines the area of effect of the weapon */
	RADIUS;

	public String getTagName() {
		switch (this) {
			case ION_DAMAGE:
				return "ion";
			case PERS_DAMAGE:
				return "persDamage";
			case SYS_DAMAGE:
				return "sysDamage";
			case PIERCING:
				return "sp";
			case FIRE_CHANCE:
				return "fireChance";
			case BREACH_CHANCE:
				return "breachChance";
			case STUN_CHANCE:
				return "stunChance";
			case POWER_COST:
				return "power";
			case SCRAP_COST:
				return "cost";
			case MISSILE_COST:
				return "missiles";
			case HULL_BUST:
				return "hullBust";
			default:
				return name().toLowerCase();
		}
	}

	@Override
	public String toString() {
		switch (this) {
			case ION_DAMAGE:
				return "Ion damage";
			case PERS_DAMAGE:
				return "Bio damage";
			case SYS_DAMAGE:
				return "System damage";
			case PIERCING:
				return "Shield piercing";
			case FIRE_CHANCE:
				return "Fire chance";
			case BREACH_CHANCE:
				return "Breach chance";
			case STUN_CHANCE:
				return "Stun chance";
			case POWER_COST:
				return "Power";
			case SCRAP_COST:
				return "Cost";
			case MISSILE_COST:
				return "Missiles";
			case HULL_BUST:
				return "Hull bust";
			default:
				String s = getTagName();
				s = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
				return s;
		}
	}
}
