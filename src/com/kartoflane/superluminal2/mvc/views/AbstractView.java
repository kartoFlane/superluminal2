package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal2.components.AbstractDrawable;
import com.kartoflane.superluminal2.components.LayeredPainter;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.ui.EditorWindow;

public abstract class AbstractView extends AbstractDrawable implements View {

	public static final RGB SELECT_RGB = new RGB(0, 0, 255);
	public static final RGB HIGHLIGHT_RGB = new RGB(0, 128, 192);
	public static final RGB DENY_RGB = new RGB(230, 100, 100);
	public static final RGB ALLOW_RGB = new RGB(50, 230, 50);
	public static final RGB PIN_RGB = new RGB(192, 192, 0);

	private String imagePath = null;

	protected Image image = null;
	protected Color borderColor = null;
	protected Color backgroundColor = null;
	protected int borderThickness = 0;

	protected boolean highlighted = false;

	public AbstractView() {
		super();
	}

	@Override
	public void setSize(int w, int h) {
		return;
	}

	@Override
	public void setLocation(int x, int y) {
		return;
	}

	@Override
	public void translate(int dx, int dy) {
		return;
	}

	@Override
	public Point getSize() {
		return getController().getModel().getSize();
	}

	@Override
	public int getW() {
		return getController().getModel().getW();
	}

	@Override
	public int getH() {
		return getController().getModel().getH();
	}

	@Override
	public Point getLocation() {
		return getController().getModel().getLocation();
	}

	@Override
	public int getX() {
		return getController().getModel().getX();
	}

	@Override
	public int getY() {
		return getController().getModel().getY();
	}

	@Override
	public Rectangle getBounds() {
		return getController().getModel().getBounds();
	}

	@Override
	public void setHighlighted(boolean high) {
		this.highlighted = high;
	}

	@Override
	public boolean isHighlighted() {
		return highlighted;
	}

	public void setImage(String path) {
		if (image != null) {
			image.dispose();
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
	public boolean contains(int x, int y) {
		return getController().getModel().contains(x, y);
	}

	@Override
	public boolean intersects(Rectangle rect) {
		return getController().getModel().intersects(rect);
	}

	/**
	 * Paints the image without any modifications at the center of the View.
	 */
	protected void paintImage(PaintEvent e, Image image, int alpha) {
		if (image != null) {
			int prevAlpha = e.gc.getAlpha();
			e.gc.setAlpha(alpha);

			Rectangle imageRect = image.getBounds();
			e.gc.drawImage(image, getX() - imageRect.width / 2, getY() - imageRect.height / 2);

			e.gc.setAlpha(prevAlpha);
		}
	}

	/**
	 * Paints the image without any modifications at the given location (relative to the canvas)
	 */
	protected void paintImage(PaintEvent e, Image image, int x, int y, int alpha) {
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

			e.gc.fillRectangle(getX() - getW() / 2, getY() - getH() / 2, getW(), getH());

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
			e.gc.drawRectangle(getX() - getW() / 2 + borderThickness / 2, getY() - getH() / 2 + borderThickness / 2,
					getW() - 1 - borderThickness / 2, getH() - 1 - borderThickness / 2);

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
		painter.add(getController(), layer);
	}

	/** Unregisters this box from the LayeredPainter object */
	public void removeFromPainter() {
		EditorWindow.getInstance().getPainter().remove(getController());
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
