package com.kartoflane.superluminal2.mvc.views.props;

import org.eclipse.swt.events.PaintEvent;

import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.props.TracerPropController;

public class TracerPropView extends PropView {

	private final int[] lines = new int[4];

	protected TracerPropController getController() {
		return (TracerPropController) controller;
	}

	@Override
	public void paintControl(PaintEvent e) {
		if (alpha > 0) {
			switch (shape) {
				case LINE:
					// Do nothing.
					break;
				case RECTANGLE:
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
				case LINE:
					paintLine(e, lines[0], lines[1], lines[2], lines[3], borderColor, borderThickness, alpha);
					break;
				case RECTANGLE:
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
		super.updateView();

		AbstractController ac = getController().getOrigin();
		if (ac == null) {
			setVisible(false);
			return;
		}
		lines[0] = ac.getX();
		lines[1] = ac.getY();
		ac = getController().getTarget();
		if (ac == null) {
			setVisible(false);
			return;
		}
		lines[2] = ac.getX();
		lines[3] = ac.getY();
	}
}
