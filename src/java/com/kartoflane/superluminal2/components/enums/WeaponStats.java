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
	/** How long hit crewmembers will be stunned */
	STUN,
	/** The blueprint's rarity */
	RARITY,
	/** How many times the weapon shoots */
	SHOTS,
	/** How fast the weapon's projecile moves */
	SPEED,
	/** Beam length */
	LENGTH,
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
			case STUN:
				return "stun";
			default:
				return name().toLowerCase();
		}
	}

	public String formatValue(float value) {
		switch (this) {
			case DAMAGE:
			case ION_DAMAGE:
			case PERS_DAMAGE:
			case SYS_DAMAGE:
			case PIERCING:
			case POWER_COST:
			case SCRAP_COST:
			case MISSILE_COST:
			case SHOTS:
			case RADIUS:
			case SPEED:
			case LENGTH:
			case STUN:
				return "" + (int) value;
			case FIRE_CHANCE:
			case BREACH_CHANCE:
			case STUN_CHANCE:
				return "" + (((int) value) * 10) + "%";
			case HULL_BUST:
			case LOCKDOWN:
				return "" + (((int) value) == 1 ? "Yes" : "No");
			case RARITY:
				int r = (int) value;
				switch (r) {
					case 0:
						return "Unobtainable (0)";
					case 1:
						return "Common (1)";
					case 2:
						return "Uncommon (2)";
					case 3:
						return "Unusual (3)";
					case 4:
						return "Rare (4)";
					case 5:
						return "Very Rare (5)";
					default:
						throw new IllegalArgumentException("Incorrect rarity value: " + value);
				}
			default:
				return "" + value;
		}
	}

	public boolean doesApply(WeaponTypes type) {
		switch (this) {
			case PIERCING:
			case SPEED:
				return type != WeaponTypes.BOMB;
			case SHOTS:
				return type != WeaponTypes.BEAM && type != WeaponTypes.BURST;
			case LOCKDOWN:
			case MISSILE_COST:
				return type != WeaponTypes.BEAM;
			case RADIUS:
				return type == WeaponTypes.BURST;
			case LENGTH:
				return type == WeaponTypes.BEAM;
			default:
				return true;
		}
	}

	@Override
	public String toString() {
		switch (this) {
			case ION_DAMAGE:
				return "Ion Damage";
			case PERS_DAMAGE:
				return "Bio Damage";
			case SYS_DAMAGE:
				return "System Damage";
			case PIERCING:
				return "Shield Piercing";
			case FIRE_CHANCE:
				return "Fire Chance";
			case BREACH_CHANCE:
				return "Breach Chance";
			case STUN_CHANCE:
				return "Stun Chance";
			case POWER_COST:
				return "Power";
			case SCRAP_COST:
				return "Cost";
			case MISSILE_COST:
				return "Missiles";
			case HULL_BUST:
				return "Hull Bust";
			case STUN:
				return "Stun Duration";
			default:
				String s = getTagName();
				s = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
				return s;
		}
	}
}
