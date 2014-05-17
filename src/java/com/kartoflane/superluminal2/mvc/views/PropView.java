package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;

import com.kartoflane.superluminal2.components.enums.Shapes;
import com.kartoflane.superluminal2.mvc.controllers.PropController;

public class PropView extends BaseView {

	private Shapes shape = Shapes.SQUARE;

	public PropView() {
		super();
	}

	private PropController getController() {
		return (PropController) controller;
	}

	@Override
	public void paintControl(PaintEvent e) {
		if (alpha > 0) {
			switch (shape) {
				case SQUARE:
					paintBackgroundSquare(e, backgroundColor, alpha);
					break;
				case OVAL:
					paintBackgroundOval(e, backgroundColor, alpha);
					break;
				case POLYGON:
					paintBackgroundPolygon(e, getController().getPolygon().toArray(), backgroundColor, alpha);
					break;
			}
			paintImage(e, image, cachedImageBounds, alpha);

			switch (shape) {
				case SQUARE:
					paintBorderSquare(e, borderColor, borderThickness, alpha);
					break;
				case OVAL:
					paintBorderOval(e, borderColor, borderThickness, alpha);
					break;
				case POLYGON:
					paintBorderPolygon(e, getController().getPolygon().toArray(), borderColor, borderThickness, alpha);
					break;
			}
		}
	}

	@Override
	public void updateView() {
		shape = getController().getShape();
		if (controller.isSelected()) {
			setBorderColor(controller.isPinned() ? PIN_RGB : SELECT_RGB);
		} else if (controller.isHighlighted()) {
			setBorderColor(HIGHLIGHT_RGB);
		} else {
			setBorderColor(0, 0, 0);
		}
	}
}
