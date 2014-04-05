package com.kartoflane.superluminal2.ftl;

import java.io.Serializable;

import com.kartoflane.superluminal2.components.interfaces.Deletable;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;

public abstract class GameObject implements Deletable, Serializable {

	private static final long serialVersionUID = 5090191534360114085L;

	protected ObjectModel model = null;
	protected boolean deletable = true;

	public void setModel(ObjectModel model) {
		this.model = model;
	}

	@Override
	public void delete() {
	}

	@Override
	public void restore() {
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
