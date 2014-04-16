package com.kartoflane.superluminal2.mvc.controllers;

import com.kartoflane.superluminal2.mvc.models.BaseModel;
import com.kartoflane.superluminal2.mvc.views.PropView;

public class PropController extends AbstractController {

	private PropController(AbstractController parent, BaseModel model, PropView view) {
		super();
		setModel(model);
		setView(view);

		setSelectable(false);
		setLocModifiable(false);
		setBounded(false);
		setCollidable(false);
		setParent(parent);
	}

	/**
	 * @param parent
	 *            the parent of this controller which it will follow
	 */
	public PropController newInstance(AbstractController parent) {
		BaseModel model = new BaseModel();
		PropView view = new PropView();
		PropController controller = new PropController(parent, model, view);

		return controller;
	}
}
