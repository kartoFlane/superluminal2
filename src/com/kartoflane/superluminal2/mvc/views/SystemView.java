package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.mvc.controllers.SystemController;

public class SystemView extends BaseView {

	protected Image interiorImage = null;
	protected String interiorPath = null;

	public SystemView() {
		setAlpha(255);
	}

	private SystemController getController() {
		return (SystemController) controller;
	}

	@Override
	public void paintControl(PaintEvent e) {
		if (alpha > 0) {
			// image is icon
			// interiorImage is the interior image (duh)
			paintImageCorner(e, interiorImage, controller.getX() - controller.getW() / 2,
					controller.getY() - controller.getH() / 2, alpha);
			paintImage(e, image, alpha);
		}
	}

	private void setInteriorImage(String path) {
		if (interiorPath != null && path != null && interiorPath.equals(path))
			return; // don't do anything if it's the same thing

		if (interiorImage != null) {
			Cache.checkInImage(this, interiorPath);
		}

		interiorPath = null;
		interiorImage = null;

		if (path != null) {
			interiorImage = Cache.checkOutImage(this, path);
			interiorPath = path;
		}
	}

	public Rectangle getInteriorBounds() {
		return interiorImage == null ? new Rectangle(0, 0, 0, 0) : interiorImage.getBounds();
	}

	@Override
	public void updateView() {
		setImage("cpath:/assets/system/" + getController().toString().toLowerCase() + ".png");
		setInteriorImage(getController().getInteriorPath());
		setVisible(getController().isAssigned());
	}

	@Override
	public void dispose() {
		super.dispose();

		if (interiorImage != null)
			Cache.checkInImage(this, interiorPath);
		interiorImage = null;
	}
}
