package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;

import com.kartoflane.superluminal2.components.enums.Directions;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.ftl.MountObject;
import com.kartoflane.superluminal2.mvc.controllers.MountController;
import com.kartoflane.superluminal2.mvc.models.ObjectModel;

public class MountView extends BaseView {

	public static final int ARROW_WIDTH = 9;
	public static final int ARROW_HEIGHT = 45;

	private Image directionArrow = null;
	private Point weaponOffset = null;
	private Point frameSize = null;

	public MountView() {
		super();

		directionArrow = Cache.checkOutImage(this, "cpath:/assets/arrow.png");

		setBorderColor(null);
		setBackgroundColor(null);
		setBorderThickness(2);
	}

	private MountController getController() {
		return (MountController) controller;
	}

	@Override
	public void paintControl(PaintEvent e) {
		if (alpha > 0) {
			paintImageCorner(e, image, cachedImageBounds, 0, 0, frameSize.x, frameSize.y,
					controller.getX() - weaponOffset.x,
					controller.getY() - weaponOffset.y,
					frameSize.x, frameSize.y, alpha);

			paintBorder(e, controller.getX() - weaponOffset.x,
					controller.getY() - weaponOffset.y,
					frameSize.x, frameSize.y, borderColor, borderThickness, alpha);

			if (getController().getDirection() != Directions.NONE)
				paintDirectionArrow(e, directionArrow, alpha);
		}
	}

	private void paintDirectionArrow(PaintEvent e, Image image, int alpha) {
		if (image != null) {
			int prevAlpha = e.gc.getAlpha();
			e.gc.setAlpha(alpha);
			Transform prevTrans = transform;
			boolean prevAdv = e.gc.getAdvanced();

			e.gc.setAdvanced(true);
			Transform transform = new Transform(e.gc.getDevice());
			Point p = model.getLocation();
			transform.translate(p.x, p.y);
			transform.rotate(directionToRotation(getController().getDirection()));
			transform.translate(-p.x, -p.y);
			e.gc.setTransform(transform);

			e.gc.drawImage(image, model.getX() - ARROW_WIDTH / 2, model.getY() - ARROW_HEIGHT);

			transform.dispose();
			transform = null;

			e.gc.setAdvanced(prevAdv);
			e.gc.setTransform(prevTrans);
			e.gc.setAlpha(prevAlpha);
		}
	}

	public Rectangle getDirectionArrowBounds() {
		Rectangle bounds = null;
		MountController c = getController();
		switch (c.getDirection()) {
			case UP:
				bounds = new Rectangle(c.getX() - ARROW_WIDTH / 2 - 1, c.getY() - ARROW_HEIGHT - 1,
						ARROW_WIDTH + 2, ARROW_HEIGHT + 2);
				break;
			case DOWN:
				bounds = new Rectangle(c.getX() - ARROW_WIDTH / 2 - 1, c.getY() - 1,
						ARROW_WIDTH + 2, ARROW_HEIGHT + 2);
				break;
			case LEFT:
				bounds = new Rectangle(c.getX() - ARROW_HEIGHT - 1, c.getY() - ARROW_WIDTH / 2 - 1,
						ARROW_HEIGHT + 2, ARROW_WIDTH + 2);
				break;
			case RIGHT:
				bounds = new Rectangle(c.getX() - 1, c.getY() - ARROW_WIDTH / 2 - 1,
						ARROW_HEIGHT + 2, ARROW_WIDTH + 2);
				break;
			default:
				bounds = new Rectangle(0, 0, 0, 0);
				break;
		}
		return bounds;
	}

	private ObjectModel getModel() {
		return (ObjectModel) model;
	}

	private MountObject getGameObject() {
		return (MountObject) getModel().getGameObject();
	}

	@Override
	public void updateView() {
		setImage(getController().getWeapon().getSheetPath());
		weaponOffset = getGameObject().getWeapon().getMountOffset();
		frameSize = getGameObject().getWeapon().getFrameSize();

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
		setFlippedX(getGameObject().isMirrored());
	}

	private float directionToRotation(Directions dir) {
		switch (dir) {
			case UP:
				return 0;
			case RIGHT:
				return 90;
			case DOWN:
				return 180;
			case LEFT:
				return 270;
			case NONE:
				return 0;
			default:
				return 0;
		}
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}
