package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;

import com.kartoflane.superluminal2.ftl.MountObject;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;

public class GibView extends BaseView {

	@Override
	public void paintControl(PaintEvent e) {
		if (alpha > 0) {
			paintImage(e, image, alpha);
			paintBorder(e, borderColor, borderThickness, alpha);
		}
	}

	private ObjectModel getModel() {
		return (ObjectModel) model;
	}

	private MountObject getGameObject() {
		return (MountObject) getModel().getGameObject();
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

		setRotation(getGameObject().isRotated() ? 90 : 0);
		setFlippedY(getGameObject().isMirrored());
	}
}
