package com.kartoflane.superluminal2.mvc.controllers;

import com.kartoflane.superluminal2.components.interfaces.Alias;
import com.kartoflane.superluminal2.ftl.GameObject;
import com.kartoflane.superluminal2.mvc.Model;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;

public abstract class ObjectController extends AbstractController implements Alias {

	public abstract GameObject getGameObject();

	public void setAlias(String alias) {
		GameObject object = getGameObject();
		if (object instanceof Alias) {
			((Alias) object).setAlias(alias);
		} else
			throw new ClassCastException("This controller's underlying game object doesn't impelment Alias interface.");
	}

	public String getAlias() {
		GameObject object = getGameObject();
		if (object instanceof Alias) {
			return ((Alias) object).getAlias();
		} else
			throw new ClassCastException("This controller's underlying game object doesn't impelment Alias interface.");
	}

	@Override
	public void setLocModifiable(boolean b) {
		model.setLocModifiable(b);
	}

	@Override
	public boolean isLocModifiable() {
		return model.isLocModifiable();
	}

	@Override
	protected ObjectModel getModel() {
		return (ObjectModel) model;
	}

	@Override
	public void setModel(Model model) {
		if (model == null)
			throw new IllegalArgumentException("Argument must not be null.");
		if (model instanceof ObjectModel == false)
			throw new IllegalArgumentException("Argument must be an ObjectModel.");
		this.model = (ObjectModel) model;
		getModel().getGameObject().setModel(getModel());
	}

	@Override
	public void delete() {
		getGameObject().delete();
		setVisible(false);
		view.removeFromPainter();
	}

	@Override
	public void restore() {
		getGameObject().restore();
		setView(view);
		setVisible(true);
	}

	public boolean isDeleted() {
		return getGameObject().isDeleted();
	}
}
