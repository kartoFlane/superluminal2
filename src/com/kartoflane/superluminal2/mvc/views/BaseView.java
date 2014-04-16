package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;

import com.kartoflane.superluminal2.components.LayeredPainter;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.components.interfaces.Disposable;
import com.kartoflane.superluminal2.components.interfaces.Redrawable;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.mvc.Controller;
import com.kartoflane.superluminal2.mvc.Model;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.controllers.ObjectController;
import com.kartoflane.superluminal2.mvc.models.BaseModel;
import com.kartoflane.superluminal2.ui.EditorWindow;

public abstract class BaseView implements View, Disposable, Redrawable {

	public static final RGB SELECT_RGB = new RGB(0, 0, 255);
	public static final RGB HIGHLIGHT_RGB = new RGB(0, 128, 192);
	public static final RGB DENY_RGB = new RGB(230, 100, 100);
	public static final RGB ALLOW_RGB = new RGB(50, 230, 50);
	public static final RGB PIN_RGB = new RGB(192, 192, 0);

	protected AbstractController controller = null;
	protected BaseModel model = null;

	protected Layers layer = null;
	protected Transform transform = null;

	protected String imagePath = null;
	protected Image image = null;
	protected Color borderColor = null;
	protected Color backgroundColor = null;
	protected int borderThickness = 0;

	protected float rotation = 0;
	protected int alpha = 255;

	protected boolean flipX = false;
	protected boolean flipY = false;
	protected boolean visible = true;
	protected boolean highlighted = false;

	public BaseView() {
	}

	public void setController(ObjectController controller) {
		this.controller = controller;
	}

	public final Layers getLayerId() {
		return layer;
	}

	@Override
	public void setController(Controller controller) {
		this.controller = (AbstractController) controller;
	}

	@Override
	public void setModel(Model model) {
		this.model = (BaseModel) model;
	}

	@Override
	public void updateView() {
		// reset to defaults
		setFlippedX(false);
		setFlippedY(false);
		setVisible(true);
		setHighlighted(false);
		setRotation(0);
		setAlpha(255);
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public float getRotation() {
		return rotation;
	}

	public void setVisible(boolean vis) {
		visible = vis;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setAlpha(int alpha) {
		if (alpha < 0 || alpha > 255)
			throw new IllegalArgumentException("Argument is not within allowed range: " + alpha);
		this.alpha = alpha;
	}

	public int getAlpha() {
		return alpha;
	}

	public void setFlippedX(boolean flip) {
		flipX = flip;
	}

	public void setFlippedY(boolean flip) {
		flipY = flip;
	}

	public boolean isFlippedX() {
		return flipX;
	}

	public boolean isFlippedY() {
		return flipY;
	}

	public void setHighlighted(boolean high) {
		this.highlighted = high;
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setImage(String path) {
		if (imagePath != null && path != null && imagePath.equals(path))
			return; // don't do anything if it's the same thing

		if (image != null) {
			Cache.checkInImage(this, imagePath);
		}

		imagePath = null;
		image = null;

		if (path != null) {
			image = Cache.checkOutImage(this, path);
			imagePath = path;
		}
	}

	public String getImagePath() {
		return imagePath;
	}

	public Rectangle getImageBounds() {
		return image == null ? new Rectangle(0, 0, 0, 0) : image.getBounds();
	}

	public void setBorderColor(RGB rgb) {
		if (borderColor != null)
			Cache.checkInColor(this, borderColor.getRGB());

		borderColor = null;

		if (rgb != null)
			borderColor = Cache.checkOutColor(this, rgb);
	}

	public void setBorderColor(int red, int green, int blue) {
		if (borderColor != null)
			Cache.checkInColor(this, borderColor.getRGB());

		RGB rgb = new RGB(red, green, blue);
		borderColor = Cache.checkOutColor(this, rgb);
	}

	public RGB getBorderRGB() {
		return borderColor == null ? null : borderColor.getRGB();
	}

	public void setBackgroundColor(RGB rgb) {
		if (backgroundColor != null)
			Cache.checkInColor(this, backgroundColor.getRGB());

		backgroundColor = null;

		if (rgb != null)
			backgroundColor = Cache.checkOutColor(this, rgb);
	}

	public void setBackgroundColor(int red, int green, int blue) {
		if (backgroundColor != null)
			Cache.checkInColor(this, backgroundColor.getRGB());

		RGB rgb = new RGB(red, green, blue);
		backgroundColor = Cache.checkOutColor(this, rgb);
	}

	public RGB getBackgroundRGB() {
		return backgroundColor == null ? null : backgroundColor.getRGB();
	}

	public void setBorderThickness(int borderThickness) {
		this.borderThickness = borderThickness;
	}

	public int getBorderThickness() {
		return borderThickness;
	}

	@Override
	public void redraw(PaintEvent e) {
		if (visible) {
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

	public void paintControl(PaintEvent e) {
	}

	/**
	 * Paints the image without any modifications at the center of the View.
	 */
	protected void paintImage(PaintEvent e, Image image, int alpha) {
		if (image != null) {
			int prevAlpha = e.gc.getAlpha();
			e.gc.setAlpha(alpha);

			Rectangle imageRect = image.getBounds();
			e.gc.drawImage(image, model.getX() - imageRect.width / 2, model.getY() - imageRect.height / 2);

			e.gc.setAlpha(prevAlpha);
		}
	}

	/**
	 * Paints the image without any modifications, centered at the given location (relative to the canvas)
	 */
	protected void paintImage(PaintEvent e, Image image, int x, int y, int alpha) {
		if (image != null) {
			int prevAlpha = e.gc.getAlpha();
			e.gc.setAlpha(alpha);

			Rectangle imageRect = image.getBounds();
			e.gc.drawImage(image, 0, 0, imageRect.width, imageRect.height, x - imageRect.width, y - imageRect.height, imageRect.width, imageRect.height);

			e.gc.setAlpha(prevAlpha);
		}
	}

	/**
	 * Paints the image without any modifications, with the image's top left corner at the given location.
	 */
	protected void paintImageCorner(PaintEvent e, Image image, int x, int y, int alpha) {
		if (image != null) {
			int prevAlpha = e.gc.getAlpha();
			e.gc.setAlpha(alpha);

			Rectangle imageRect = image.getBounds();
			e.gc.drawImage(image, 0, 0, imageRect.width, imageRect.height, x, y, imageRect.width, imageRect.height);

			e.gc.setAlpha(prevAlpha);
		}
	}

	/**
	 * Paints the image in the given area, modifying the image to fit.
	 */
	protected void paintImage(PaintEvent e, Image image, Rectangle rect, int alpha) {
		if (image != null) {
			int prevAlpha = e.gc.getAlpha();
			e.gc.setAlpha(alpha);

			Rectangle imageRect = image.getBounds();
			e.gc.drawImage(image, 0, 0, imageRect.width, imageRect.height,
					rect.x, rect.y, rect.width, rect.height);

			e.gc.setAlpha(prevAlpha);
		}
	}

	/**
	 * Paints the image in the given area, modifying the image to fit.
	 */
	protected void paintImage(PaintEvent e, Image image, int x, int y, int w, int h, int alpha) {
		if (image != null) {
			int prevAlpha = e.gc.getAlpha();
			e.gc.setAlpha(alpha);

			Rectangle imageRect = image.getBounds();
			e.gc.drawImage(image, 0, 0, imageRect.width, imageRect.height, x, y, w, h);

			e.gc.setAlpha(prevAlpha);
		}
	}

	protected void paintBackground(PaintEvent e, Color backgroundColor, int alpha) {
		if (backgroundColor != null) {
			Color prevBgColor = e.gc.getBackground();
			int prevAlpha = e.gc.getAlpha();

			e.gc.setBackground(backgroundColor);
			e.gc.setAlpha(alpha);

			e.gc.fillRectangle(model.getX() - model.getW() / 2, model.getY() - model.getH() / 2, model.getW(), model.getH());

			e.gc.setBackground(prevBgColor);
			e.gc.setAlpha(prevAlpha);
		}
	}

	protected void paintBackground(PaintEvent e, Rectangle rect, Color backgroundColor, int alpha) {
		if (backgroundColor != null) {
			Color prevBgColor = e.gc.getBackground();
			int prevAlpha = e.gc.getAlpha();

			e.gc.setBackground(backgroundColor);
			e.gc.setAlpha(alpha);

			e.gc.fillRectangle(rect);

			e.gc.setBackground(prevBgColor);
			e.gc.setAlpha(prevAlpha);
		}
	}

	protected void paintBackground(PaintEvent e, int x, int y, int w, int h, Color backgroundColor, int alpha) {
		if (backgroundColor != null) {
			Color prevBgColor = e.gc.getBackground();
			int prevAlpha = e.gc.getAlpha();

			e.gc.setBackground(backgroundColor);
			e.gc.setAlpha(alpha);

			e.gc.fillRectangle(x, y, w, h);

			e.gc.setBackground(prevBgColor);
			e.gc.setAlpha(prevAlpha);
		}
	}

	protected void paintBorder(PaintEvent e, Color borderColor, int borderThickness, int alpha) {
		if (borderColor != null) {
			Color prevBgColor = e.gc.getBackground();
			int prevAlpha = e.gc.getAlpha();
			int prevWidth = e.gc.getLineWidth();

			e.gc.setForeground(borderColor);
			e.gc.setAlpha(alpha);
			e.gc.setLineWidth(borderThickness);

			// lines are drawn from the center, which makes the math a little funky
			e.gc.drawRectangle(model.getX() - model.getW() / 2 + borderThickness / 2,
					model.getY() - model.getH() / 2 + borderThickness / 2,
					model.getW() - 1 - borderThickness / 2, model.getH() - 1 - borderThickness / 2);

			e.gc.setForeground(prevBgColor);
			e.gc.setAlpha(prevAlpha);
			e.gc.setLineWidth(prevWidth);
		}
	}

	protected void paintBorder(PaintEvent e, Rectangle rect, Color borderColor, int borderThickness, int alpha) {
		if (borderColor != null) {
			Color prevBgColor = e.gc.getBackground();
			int prevAlpha = e.gc.getAlpha();
			int prevWidth = e.gc.getLineWidth();

			e.gc.setForeground(borderColor);
			e.gc.setAlpha(alpha);
			e.gc.setLineWidth(borderThickness);

			// lines are drawn from the center, which makes the math a little funky
			e.gc.drawRectangle(rect.x + borderThickness / 2, rect.y + borderThickness / 2,
					rect.width - 1 - borderThickness / 2, rect.height - 1 - borderThickness / 2);

			e.gc.setForeground(prevBgColor);
			e.gc.setAlpha(prevAlpha);
			e.gc.setLineWidth(prevWidth);
		}
	}

	protected void paintBorder(PaintEvent e, int x, int y, int w, int h, Color borderColor, int borderThickness, int alpha) {
		if (borderColor != null) {
			Color prevBgColor = e.gc.getBackground();
			int prevAlpha = e.gc.getAlpha();
			int prevWidth = e.gc.getLineWidth();

			e.gc.setForeground(borderColor);
			e.gc.setAlpha(alpha);
			e.gc.setLineWidth(borderThickness);

			// lines are drawn from the center, which makes the math a little funky
			e.gc.drawRectangle(x + borderThickness / 2, y + borderThickness / 2,
					w - 1 - borderThickness / 2, h - 1 - borderThickness / 2);

			e.gc.setForeground(prevBgColor);
			e.gc.setAlpha(prevAlpha);
			e.gc.setLineWidth(prevWidth);
		}
	}

	/**
	 * Registers this box with the LayeredPainter object, also remembering the layer for use during deserialization.
	 * 
	 * @param layer
	 *            Id of the layer to which the box will be added
	 */
	public void addToPainter(Layers layer) {
		LayeredPainter painter = EditorWindow.getInstance().getPainter();
		if (layer == null || !painter.getLayers().contains(layer))
			throw new IllegalArgumentException("Illegal layer.");

		this.layer = layer;
		painter.add(controller, layer);
	}

	/** Unregisters this box from the LayeredPainter object */
	public void removeFromPainter() {
		EditorWindow.getInstance().getPainter().remove(controller);
	}

	@Override
	public void dispose() {
		removeFromPainter();

		if (borderColor != null)
			Cache.checkInColor(this, borderColor.getRGB());
		if (backgroundColor != null)
			Cache.checkInColor(this, backgroundColor.getRGB());
		if (image != null)
			Cache.checkInImage(this, imagePath);

		borderColor = null;
		backgroundColor = null;
		image = null;
		imagePath = null;
	}
}
