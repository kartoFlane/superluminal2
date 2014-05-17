package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;

public class GibView extends BaseView {

	@Override
	public void paintControl(PaintEvent e) {
		if (alpha > 0) {
			paintImage(e, image, cachedImageBounds, alpha);
			paintBorderSquare(e, borderColor, borderThickness, alpha);
		}
	}

	@Override
	public void updateView() {
		if (controller.isSelected()) {
			setBorderColor(controller.isPinned() ? PIN_RGB : SELECT_RGB);
			setBackgroundColor(null);
			setBorderThickness(2);
		} else if (controller.isHighlighted()) {
			setBorderColor(HIGHLIGHT_RGB);
			setBackgroundColor(null);
			setBorderThickness(3);
		} else {
			setBorderColor(null);
			setBackgroundColor(null);
			setBorderThickness(2);
		}
	}
}
