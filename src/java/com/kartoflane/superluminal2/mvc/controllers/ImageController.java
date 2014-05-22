package com.kartoflane.superluminal2.mvc.controllers;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.components.Grid;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.core.Manager;
import com.kartoflane.superluminal2.ftl.ImageObject;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.models.BaseModel;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;
import com.kartoflane.superluminal2.mvc.views.ImageView;
import com.kartoflane.superluminal2.ui.sidebar.data.DataComposite;
import com.kartoflane.superluminal2.ui.sidebar.data.ImageDataComposite;

public class ImageController extends ObjectController {

	private ImageController(AbstractController parent, BaseModel model, ImageView view) {
		super();
		setModel(model);
		setView(view);

		setSelectable(true);
		setLocModifiable(true);
		setBounded(false);
		setCollidable(false);
		setParent(parent == null ? Manager.getCurrentShip().getShipController() : parent);
	}

	/**
	 * @param parent
	 *            the parent of this controller which it will follow.<br>
	 *            May be null - in this case, the controller will follow the ship controller.
	 */
	public static ImageController newInstance(AbstractController parent, ImageObject object) {
		ObjectModel model = new ObjectModel(object);
		ImageView view = new ImageView();
		ImageController controller = new ImageController(parent, model, view);

		return controller;
	}

	@Override
	public void setView(View view) {
		super.setView(view);
		this.view.addToPainter(Layers.IMAGES);
		updateView();
	}

	@Override
	public ImageObject getGameObject() {
		return (ImageObject) getModel().getGameObject();
	}

	public void setImage(String imagePath) {
		getGameObject().setImagePath(imagePath);
		view.setImage(imagePath);
		Rectangle bounds = view.getImageBounds();
		setSize(bounds.width, bounds.height);
	}

	public String getImage() {
		return getGameObject().getImagePath();
	}

	public DataComposite getDataComposite(Composite parent) {
		return new ImageDataComposite(parent, this);
	}

	public void setAlpha(int alpha) {
		view.setAlpha(alpha);
	}

	public int getAlpha() {
		return view.getAlpha();
	}

	@Override
	public void updateBoundingArea() {
		Point gridSize = Grid.getInstance().getSize();
		setBoundingPoints(0, 0, gridSize.x, gridSize.y);
	}
}
