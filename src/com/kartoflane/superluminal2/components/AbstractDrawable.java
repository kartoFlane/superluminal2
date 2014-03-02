package com.kartoflane.superluminal2.components;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;

import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.components.interfaces.Disposable;
import com.kartoflane.superluminal2.components.interfaces.Redrawable;
import com.kartoflane.superluminal2.components.interfaces.Resizable;

/**
 * A class representing a drawable entity in the application.
 * 
 * @author kartoFlane
 * 
 */
public abstract class AbstractDrawable implements Disposable, Redrawable, Resizable {

	protected int alpha = 255;
	protected boolean visible = true;
	protected Layers layer = null;

	protected Transform transform = null;

	protected float rotation = 0;
	protected boolean flipX = false;
	protected boolean flipY = false;

	public AbstractDrawable() {
		super();
	}

	/**
	 * Set the alpha value (opacity / transparency) of this box. Values range from 0 to 255.<br>
	 * 255 by default.
	 */
	public void setAlpha(int alpha) {
		if (alpha < 0 || alpha > 255)
			throw new IllegalArgumentException("Alpha values must be within 0-255 range.");
		this.alpha = alpha;
	}

	public final int getAlpha() {
		return alpha;
	}

	@Override
	public void setVisible(boolean vis) {
		visible = vis;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	/** Set whether to flip the object in the X axis, relative to its center. */
	public void setFlippedX(boolean flip) {
		flipX = flip;
	}

	/** Set whether to flip the object in the Y axis, relative to its center. */
	public void setFlippedY(boolean flip) {
		flipY = flip;
	}

	public boolean isFlippedX() {
		return flipX;
	}

	public boolean isFlippedY() {
		return flipY;
	}

	/**
	 * Set the box's rotation, in degrees.<br>
	 * 0 by default.
	 */
	public void setRotation(float rotation) {
		this.rotation = rotation;
		recalculateBounds();
	}

	/** @return the rotation value in degrees. */
	public float getRotation() {
		return rotation;
	}

	@Override
	public void redraw(PaintEvent e) {
		if (visible) {
			boolean trans = rotation % 360 != 0 || flipX || flipY;

			if (trans) {
				e.gc.setAdvanced(true);
				transform = new Transform(e.gc.getDevice());
				Point p = getLocation();
				transform.translate(p.x, p.y);
				transform.rotate(rotation);
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

	public final Layers getLayerId() {
		return layer;
	}

	protected void recalculateBounds() {
		Rectangle bounds = getBounds();
		Point position = getLocation();
		Point size = getSize();

		if (rotation % 180 == 0) {
			bounds.x = position.x - size.x / 2 - 1;
			bounds.y = position.y - size.y / 2 - 1;
			bounds.width = size.x + 2;
			bounds.height = size.y + 2;
		} else if (rotation % 90 == 0) {
			bounds.x = position.x - size.y / 2 - 1;
			bounds.y = position.y - size.x / 2 - 1;
			bounds.width = size.y + 2;
			bounds.height = size.x + 2;
		} else {
			double rotation = Math.toRadians(this.rotation);
			// expand bounds by 1px in each direction to prevent artefacts from appearing during redrawing
			bounds.x = position.x - (int) Math.round(size.x / 2 * Math.cos(rotation) + size.y / 2 * Math.sin(rotation)) - 1;
			bounds.y = position.y - (int) Math.round(size.x / 2 * Math.sin(rotation) + size.y / 2 * Math.cos(rotation)) - 1;
			bounds.width = (int) Math.abs(Math.round(size.x * Math.cos(rotation) + size.y * Math.sin(rotation))) + 2;
			bounds.height = (int) Math.abs(Math.round(size.x * Math.sin(rotation) + size.y * Math.cos(rotation))) + 2;
		}
	}
}
