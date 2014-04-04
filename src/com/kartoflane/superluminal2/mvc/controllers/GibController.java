package com.kartoflane.superluminal2.mvc.controllers;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.ftl.GameObject;
import com.kartoflane.superluminal2.ftl.GibObject;
import com.kartoflane.superluminal2.mvc.ObjectModel;
import com.kartoflane.superluminal2.mvc.views.GibView;
import com.kartoflane.superluminal2.ui.ShipContainer;

public class GibController extends ObjectController {

	private GibController(ShipContainer container, ObjectModel model, GibView view) {
		super();
		setModel(model);
		setView(view);

		setSelectable(true);
		setLocModifiable(true);
	}

	@Override
	public Point getPresentedSize() {
		// TODO Auto-generated method stub
		return null;
	}

	private ObjectModel getModel() {
		return (ObjectModel) model;
	}

	@Override
	public GameObject getGameObject() {
		return (GibObject) getModel().getGameObject();
	}

}
