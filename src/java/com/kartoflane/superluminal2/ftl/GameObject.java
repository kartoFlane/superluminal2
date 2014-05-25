package com.kartoflane.superluminal2.ftl;

import com.kartoflane.superluminal2.components.interfaces.Deletable;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;

public abstract class GameObject implements Deletable {

	protected ObjectModel model = null;
	protected boolean deletable = true;
	protected boolean deleted = false;

	public void setModel(ObjectModel model) {
		this.model = model;
	}

	/** Pull positioning data from the model and interpret it to represent it in-game. */
	public abstract void update();

	@Override
	public void delete() {
		deleted = true;
	}

	public boolean isDeleted() {
		return deleted;
	}

	@Override
	public void restore() {
		deleted = false;
	}

	@Override
	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

	@Override
	public boolean isDeletable() {
		return deletable;
	}
}
