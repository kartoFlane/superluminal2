package com.kartoflane.superluminal2.mvc.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import com.kartoflane.superluminal2.mvc.controllers.CellController;
import com.kartoflane.superluminal2.mvc.models.SystemModel.Systems;

public class ShipModel extends AbstractModel {

	public enum Images {
		HULL, FLOOR, CLOAK, SHIELD, MINISHIP;
	}

	private static final long serialVersionUID = 64622361482385054L;

	protected boolean isPlayerShip = true;
	protected TreeSet<RoomModel> rooms;
	protected ArrayList<DoorModel> doors;
	protected TreeSet<MountModel> mounts;
	protected HashMap<Images, String> imageMap;
	protected HashMap<Systems, SystemModel> systemMap;

	public ShipModel(boolean isPlayerShip) {
		super();

		setLocModifiable(false);
		setSizeModifiable(false);
		setFollowActive(false);

		setSize(CellController.SIZE / 2, CellController.SIZE / 2);
		setPlayerShip(isPlayerShip);

		imageMap = new HashMap<Images, String>();
		for (Images image : Images.values()) {
			imageMap.put(image, null);
		}

		systemMap = new HashMap<Systems, SystemModel>();
		for (Systems sys : Systems.values()) {
			systemMap.put(sys, new SystemModel(sys));
		}

		rooms = new TreeSet<RoomModel>();
		doors = new ArrayList<DoorModel>();
		mounts = new TreeSet<MountModel>();
	}

	public TreeSet<RoomModel> getRooms() {
		return rooms;
	}

	public ArrayList<DoorModel> getDoors() {
		return doors;
	}

	public TreeSet<MountModel> getMounts() {
		return mounts;
	}

	public void setImagePath(Images imageId, String path) {
		imageMap.put(imageId, path);
	}

	public String getImagePath(Images imageId) {
		return imageMap.get(imageId);
	}

	public SystemModel getSystem(Systems systemId) {
		return systemMap.get(systemId);
	}

	public void setPlayerShip(boolean isPlayerShip) {
		this.isPlayerShip = isPlayerShip;
	}

	public boolean isPlayerShip() {
		return isPlayerShip;
	}
}
