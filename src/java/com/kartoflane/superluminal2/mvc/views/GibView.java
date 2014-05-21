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
			setBackgroundColor(defaultBackground);
			setBorderThickness(2);
		} else if (controller.isHighlighted()) {
			setBorderColor(HIGHLIGHT_RGB);
			setBackgroundColor(defaultBackground);
			setBorderThickness(3);
		} else {
			setBorderColor(defaultBorder);
			setBackgroundColor(defaultBackground);
			setBorderThickness(2);
		}
	}
}
