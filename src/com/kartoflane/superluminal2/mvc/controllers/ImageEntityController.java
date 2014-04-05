package com.kartoflane.superluminal2.mvc.controllers;

import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.mvc.models.BaseModel;
import com.kartoflane.superluminal2.mvc.views.ImageEntityView;

public class ImageEntityController extends AbstractController {

	protected String imagePath;

	private ImageEntityController(AbstractController parent, BaseModel model, ImageEntityView view) {
		super();
		setModel(model);
		setView(view);

		setSelectable(false);
		setLocModifiable(false);
		setBounded(false);
		setCollidable(false);
		setParent(parent == null ? Manager.getCurrentShip().getShipController() : parent);
	}

	/**
	 * @param parent
	 *            the parent of this controller which it will follow.<br>
	 *            May be null - in this case, the controller will follow the ship controller.
	 */
	public static ImageEntityController newInstance(AbstractController parent) {
		BaseModel model = new BaseModel();
		ImageEntityView view = new ImageEntityView();
		ImageEntityController controller = new ImageEntityController(parent, model, view);

		return controller;
	}

	public void setImage(String imagePath) {
		this.imagePath = imagePath;
		view.setImage(imagePath);
	}
}
