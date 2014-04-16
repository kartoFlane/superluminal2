package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Transform;

import com.kartoflane.superluminal2.components.Grid;

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
	public void redraw(PaintEvent e) {
		if (visible && Grid.getInstance().isVisible()) {
			boolean trans = getRotation() % 360 != 0 || flipX || flipY;
			if (trans) {
				e.gc.setAdvanced(true);
				transform = new Transform(e.gc.getDevice());
				Point p = model.getLocation();
				transform.translate(p.x, p.y);
				transform.rotate(getRotation());
				transform.scale(flipX ? -1 : 1, flipY ? -1 : 1);
				transform.translate(-p.x, -p.y);

				e.gc.setTransform(transform);
			}

			paintControl(e);

			if (trans) {
				transform.dispose();
				transform = null;

				e.gc.setAdvanced(false);
			}
		}
	}

	@Override
	public void updateView() {
	}
}
