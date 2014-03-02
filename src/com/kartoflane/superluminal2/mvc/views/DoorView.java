package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;

import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.controllers.DoorController;

public class DoorView extends AbstractView {

	protected DoorController controller;

	public DoorView() {
		super();
		// setImage("/assets/door_v.png");
	}

	@Override
	public DoorController getController() {
		return controller;
	}

	@Override
	public void setController(Controller controller) {
		this.controller = (DoorController) controller;
	}

	public void setHorizontal(boolean horizontal) {
		if (horizontal) {
			setImage("/assets/door_h.png");
		} else {
			setImage("/assets/door_v.png");
		}
	}

	@Override
	public void paintControl(PaintEvent e) {
		if (alpha > 0) {
			paintImage(e, image, alpha);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}
