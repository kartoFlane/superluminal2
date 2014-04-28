package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;

public class StationView extends BaseView {

	public StationView() {
		setAlpha(255);
		setBorderColor(null);
		setBackgroundColor(null);
	}

	@Override
	public void paintControl(PaintEvent e) {
		if (alpha > 0) {
			paintImage(e, image, cachedImageBounds, alpha);
		}
	}

	@Override
	public void updateView() {
		// stations don't change their appearance
	}
}
