package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;

import com.kartoflane.superluminal2.ftl.DoorObject;
import com.kartoflane.superluminal2.mvc.ObjectModel;

public class DoorView extends BaseView {

	public DoorView() {
		super();

		setBorderColor(null);
		setBackgroundColor(null);
		setBorderThickness(2);
	}

	private ObjectModel getModel() {
		return (ObjectModel) model;
	}

	private DoorObject getGameObject() {
		return (DoorObject) getModel().getGameObject();
	}

	@Override
	public void paintControl(PaintEvent e) {
		if (alpha > 0) {
			paintImage(e, image, alpha);
			paintBorder(e, borderColor, getBorderThickness(), alpha);
		}
	}

	@Override
	public void updateView() {
		if (getGameObject().isHorizontal())
			setImage("/assets/door_h.png");
		else
			setImage("/assets/door_v.png");

		if (controller.isSelected()) {
			setBorderColor(controller.isPinned() ? PIN_RGB : SELECT_RGB);
			setBackgroundColor(HIGHLIGHT_RGB);
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

	@Override
	public void dispose() {
		super.dispose();
	}
}
