package com.kartoflane.superluminal2.mvc.controllers;

import com.kartoflane.superluminal2.components.interfaces.Alias;
import com.kartoflane.superluminal2.ftl.GameObject;

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
}
