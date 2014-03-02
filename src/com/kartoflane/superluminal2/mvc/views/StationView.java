package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;

import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.controllers.StationController;

public class StationView extends AbstractView {

	protected StationController controller;

	@Override
	public StationController getController() {
		return controller;
	}

	@Override
	public void setController(Controller controller) {
		this.controller = (StationController) controller;
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
