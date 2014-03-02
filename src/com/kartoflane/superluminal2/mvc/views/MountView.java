package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;

import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.controllers.MountController;

public class MountView extends AbstractView {

	protected MountController controller;

	@Override
	public MountController getController() {
		return controller;
	}

	@Override
	public void setController(Controller controller) {
		this.controller = (MountController) controller;
	}

	@Override
	public void paintControl(PaintEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		super.dispose();
	}
}
