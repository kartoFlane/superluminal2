package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Image;

import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.controllers.SystemController;

public class SystemView extends AbstractView {

	protected SystemController controller;

	protected Image interiorImage = null;

	public SystemView() {
	}

	@Override
	public void paintControl(PaintEvent e) {
		if (alpha > 0) {
			paintImage(e, interiorImage, getX() - getW() / 2, getY() - getH() / 2, getW(), getH(), alpha);

			paintBackground(e, backgroundColor, alpha / 4); // TODO tint RoomView's background instead?

			paintImage(e, image, alpha);
		}
	}

	public void setInteriorImage(String interiorPath) {
		if (interiorImage != null)
			Cache.checkInImage(this, controller.getModel().getInteriorPath());

		interiorImage = null;

		if (interiorPath != null)
			interiorImage = Cache.checkOutImage(this, interiorPath);
	}

	@Override
	public SystemController getController() {
		return controller;
	}

	@Override
	public void setController(Controller controller) {
		this.controller = (SystemController) controller;
	}

	@Override
	public void dispose() {
		super.dispose();

		if (interiorImage != null)
			Cache.checkInImage(this, controller.getModel().getInteriorPath());
		interiorImage = null;
	}
}
