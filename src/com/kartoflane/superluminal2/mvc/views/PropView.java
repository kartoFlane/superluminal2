package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;

public class PropView extends BaseView {

	public PropView() {
		super();
	}

	@Override
	public void paintControl(PaintEvent e) {
		if (alpha > 0) {
			paintBackground(e, backgroundColor, alpha);
			paintImage(e, image, alpha);
			paintBorder(e, borderColor, borderThickness, alpha);
		}
	}
}
