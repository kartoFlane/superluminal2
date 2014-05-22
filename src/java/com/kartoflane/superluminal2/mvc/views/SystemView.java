package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.mvc.controllers.SystemController;
import com.kartoflane.superluminal2.utils.Utils;

public class SystemView extends BaseView {

	protected Image interiorImage = null;
	protected Rectangle cachedInteriorBounds = null;
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
			// Image is icon
			// InteriorImage is the interior image (duh)
			paintBackgroundSquare(e, backgroundColor, alpha / 2);
			paintImageCorner(e, interiorImage, cachedInteriorBounds,
					controller.getX() - controller.getW() / 2,
					controller.getY() - controller.getH() / 2, alpha);
			paintImage(e, image, cachedImageBounds, alpha);
		}
	}

	private void setInteriorImage(String path) {
		if (interiorPath != null && path != null && interiorPath.equals(path))
			return; // Don't do anything if it's the same thing

		if (interiorImage != null) {
			Cache.checkInImage(this, interiorPath);
		}

		interiorPath = null;
		interiorImage = null;

		if (path != null) {
			interiorImage = Cache.checkOutImage(this, path);
			if (interiorImage != null)
				cachedInteriorBounds = interiorImage.getBounds();
			interiorPath = path;
		}
	}

	public Rectangle getInteriorBounds() {
		return interiorImage == null ? new Rectangle(0, 0, 0, 0) : Utils.copy(cachedInteriorBounds);
	}

	@Override
	public void updateView() {
		setInteriorImage(getController().getInteriorPath());
		setBackgroundColor(getController().isAvailableAtStart() ? defaultBackground : DENY_RGB);
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
