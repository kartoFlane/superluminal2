package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Point;

public class ImageView extends BaseView {

	private int w = -1;
	private int h = -1;

	@Override
	public void paintControl(PaintEvent e) {
		if (alpha > 0) {
			int tw = w == -1 ? cachedImageBounds.width : w;
			int th = h == -1 ? cachedImageBounds.height : h;
			paintImageResize(e, image, cachedImageBounds, controller.getX() - tw / 2, controller.getY() - th / 2,
					tw, th, alpha);
			paintBorder(e, borderColor, borderThickness, 255);
		}
	}

	public void setSize(int w, int h) {
		this.w = w;
		this.h = h;
	}

	public void setSize(Point p) {
		w = p.x;
		h = p.y;
	}

	public Point getSize() {
		return new Point(w, h);
	}

	@Override
	public void updateView() {
		setSize(controller.getSize());

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
