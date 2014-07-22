package com.kartoflane.superluminal2.mvc.models;

import com.kartoflane.superluminal2.ftl.GameObject;

public class ObjectModel extends BaseModel {

	protected GameObject gameObject = null;

	private ObjectModel() {
		super();
	}

	public ObjectModel(GameObject gameObject) {
		this();
		setGameObject(gameObject);
	}

	public void setGameObject(GameObject o) {
		gameObject = o;
	}

	public GameObject getGameObject() {
		if (gameObject == null)
			throw new IllegalStateException("Model is disposed.");
		return gameObject;
	}

	@Override
	public void dispose() {
		super.dispose();
		gameObject.setModel(null);
		gameObject = null;
	}

	@Override
	public void delete() {
		if (gameObject == null)
			throw new IllegalStateException("Model is disposed.");
		gameObject.delete();
	}

	@Override
	public void restore() {
		if (gameObject == null)
			throw new IllegalStateException("Model is disposed.");
		gameObject.restore();
	}

	@Override
	public void setDeletable(boolean deletable) {
		if (gameObject == null)
			throw new IllegalStateException("Model is disposed.");
		gameObject.setDeletable(deletable);
	}

	@Override
	public boolean isDeletable() {
		if (gameObject == null)
			throw new IllegalStateException("Model is disposed.");
		return gameObject.isDeletable();
	}
}
