package com.kartoflane.superluminal2.mvc.models;

import com.kartoflane.superluminal2.components.interfaces.Alias;

public class DoorModel extends AbstractModel implements Alias {

	private static final long serialVersionUID = 6955367642714497858L;

	protected RoomModel left;
	protected RoomModel right;
	protected boolean horizontal = false;

	protected String alias = null;

	public DoorModel() {
		super();

		setLocModifiable(true);
		setSizeModifiable(false);
		setFollowActive(false);
	}

	public DoorModel(boolean horizontal) {
		this();

		setHorizontal(horizontal);
	}

	public void setLeftRoom(RoomModel left) {
		this.left = left;
	}

	public RoomModel getLeftRoom() {
		return left;
	}

	public void setRightRoom(RoomModel right) {
		this.right = right;
	}

	public RoomModel getRightRoom() {
		return right;
	}

	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
	}

	public boolean isHorizontal() {
		return horizontal;
	}

	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public void setAlias(String alias) {
		this.alias = alias;
	}
}
