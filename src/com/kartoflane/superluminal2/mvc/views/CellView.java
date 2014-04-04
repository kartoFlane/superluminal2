package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.RGB;

public class CellView extends BaseView {

	public static final RGB GRID_RGB = new RGB(112, 112, 112);

	public CellView() {
		super();

		setBorderColor(GRID_RGB);
	}

	@Override
	public void paintControl(PaintEvent e) {
		if (alpha > 0) {
			paintBorder(e, borderColor, borderThickness, alpha);
		}
	}

	@Override
	public void updateView() {
	}
}
