package com.kartoflane.superluminal2.core;

import java.util.ArrayList;
import java.util.HashMap;

import net.vhati.ftldat.FTLDat.FTLPack;

import com.kartoflane.superluminal2.components.ShipMetadata;
import com.kartoflane.superluminal2.ftl.WeaponObject;

public class Database {

	public enum DroneTypes {
		COMBAT, SHIP_REPAIR, BOARDER, BATTLE, REPAIR, DEFENSE, SHIELD, HACKING
	}

	public enum WeaponTypes {
		MISSILES, LASER, BEAM, BOMB, BURST
	}

	private static final Database instance = new Database();

	private FTLPack data = null;
	private FTLPack resource = null;

	private ArrayList<String> playerShipBlueprintNames = new ArrayList<String>();
	private HashMap<String, ArrayList<ShipMetadata>> shipMetadata = new HashMap<String, ArrayList<ShipMetadata>>();
	private HashMap<WeaponTypes, ArrayList<WeaponObject>> weaponTypeObjectMap = new HashMap<WeaponTypes, ArrayList<WeaponObject>>();

	// TODO
	// private HashMap<DroneTypes, ArrayList<DroneObject>> droneTypeObjectMap = new HashMap<DroneTypes, ArrayList<DroneObject>>();

	private Database() {
		playerShipBlueprintNames.add("PLAYER_SHIP_HARD");
		playerShipBlueprintNames.add("PLAYER_SHIP_HARD_2");
		playerShipBlueprintNames.add("PLAYER_SHIP_HARD_3");
		playerShipBlueprintNames.add("PLAYER_SHIP_MANTIS");
		playerShipBlueprintNames.add("PLAYER_SHIP_MANTIS_2");
		playerShipBlueprintNames.add("PLAYER_SHIP_MANTIS_3");
		playerShipBlueprintNames.add("PLAYER_SHIP_STEALTH");
		playerShipBlueprintNames.add("PLAYER_SHIP_STEALTH_2");
		playerShipBlueprintNames.add("PLAYER_SHIP_STEALTH_3");
		playerShipBlueprintNames.add("PLAYER_SHIP_CIRCLE");
		playerShipBlueprintNames.add("PLAYER_SHIP_CIRCLE_2");
		playerShipBlueprintNames.add("PLAYER_SHIP_CIRCLE_3");
		playerShipBlueprintNames.add("PLAYER_SHIP_FED");
		playerShipBlueprintNames.add("PLAYER_SHIP_FED_2");
		playerShipBlueprintNames.add("PLAYER_SHIP_FED_3");
		playerShipBlueprintNames.add("PLAYER_SHIP_JELLY");
		playerShipBlueprintNames.add("PLAYER_SHIP_JELLY_2");
		playerShipBlueprintNames.add("PLAYER_SHIP_JELLY_3");
		playerShipBlueprintNames.add("PLAYER_SHIP_ROCK");
		playerShipBlueprintNames.add("PLAYER_SHIP_ROCK_2");
		playerShipBlueprintNames.add("PLAYER_SHIP_ROCK_3");
		playerShipBlueprintNames.add("PLAYER_SHIP_ENERGY");
		playerShipBlueprintNames.add("PLAYER_SHIP_ENERGY_2");
		playerShipBlueprintNames.add("PLAYER_SHIP_ENERGY_3");
		playerShipBlueprintNames.add("PLAYER_SHIP_CRYSTAL");
		playerShipBlueprintNames.add("PLAYER_SHIP_CRYSTAL_2");
		playerShipBlueprintNames.add("PLAYER_SHIP_ANAEROBIC");
		playerShipBlueprintNames.add("PLAYER_SHIP_ANAEROBIC_2");
	}

	public static Database getInstance() {
		return instance;
	}

	public void setDataDat(FTLPack dat) {
		data = dat;
	}

	public FTLPack getDataDat() {
		return data;
	}

	public void setResourceDat(FTLPack dat) {
		resource = dat;
	}

	public FTLPack getResourceDat() {
		return resource;
	}

	public boolean isPlayerShip(String blueprintName) {
		return playerShipBlueprintNames.contains(blueprintName);
	}

	public String[] getPlayerShipBlueprintNames() {
		return playerShipBlueprintNames.toArray(new String[0]);
	}

	public void storeShipMetadata(ShipMetadata metadata) {
		ArrayList<ShipMetadata> dataList = shipMetadata.get(metadata.getBlueprintName());
		if (dataList == null) {
			dataList = new ArrayList<ShipMetadata>();
			shipMetadata.put(metadata.getBlueprintName(), dataList);
		}

		dataList.add(metadata);
	}

	public HashMap<String, ArrayList<ShipMetadata>> getShipMetadata() {
		HashMap<String, ArrayList<ShipMetadata>> copiedMap = new HashMap<String, ArrayList<ShipMetadata>>();
		copiedMap.putAll(shipMetadata);
		return copiedMap;
	}

	public void clearShipMetadata() {
		shipMetadata.clear();
	}

	public ShipMetadata[] listShipMetadata() {
		return shipMetadata.values().toArray(new ShipMetadata[0]);
	}
}
