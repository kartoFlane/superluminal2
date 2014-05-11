package com.kartoflane.superluminal2.mvc.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal2.core.Cache;

/**
 * A simplistic pseudo-view used in image previews, eg. ship loader, glow selection dialog, etc.
 * 
 * @author kartoFlane
 * 
 */
public class Preview implements PaintListener {
	private Image image = null;
	private String imagePath = null;

	private boolean overrideSource = false;
	private boolean drawBackground = false;
	private Color backgroundColor = null;

	private int x = 0;
	private int y = 0;
	private int w = 0;
	private int h = 0;
	private Point sourceSize = new Point(0, 0);
	private Rectangle cachedBounds = new Rectangle(0, 0, 0, 0);

	public Preview() {
	}

	public void setBackgroundColor(RGB rgb) {
		if (backgroundColor != null)
			Cache.checkInColor(this, backgroundColor.getRGB());

		backgroundColor = null;

		if (rgb != null)
			backgroundColor = Cache.checkOutColor(this, rgb);
	}

	public void setBackgroundColor(int r, int g, int b) {
		setBackgroundColor(new RGB(r, g, b));
	}

	public void setImage(String path) {
		if (image != null)
			Cache.checkInImage(this, imagePath);

		image = null;
		imagePath = null;

		if (path != null) {
			image = Cache.checkOutImage(this, path);
			if (image != null) {
				cachedBounds = image.getBounds();
				w = cachedBounds.width;
				h = cachedBounds.height;
			}
			imagePath = path;
		}
	}

	public void setDrawBackground(boolean draw) {
		drawBackground = draw;
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setSize(int w, int h) {
		this.w = w;
		this.h = h;
	}

	public Point getSize() {
		return new Point(w, h);
	}

	public Point getImageSize() {
		return new Point(cachedBounds.width, cachedBounds.height);
	}

	public void setSourceSize(int w, int h) {
		sourceSize.x = w;
		sourceSize.y = h;
	}

	public void setOverrideSourceSize(boolean override) {
		overrideSource = override;
	}

	public boolean isOverrideSourceSize() {
		return overrideSource;
	}

	@Override
	public void paintControl(PaintEvent e) {
		if (drawBackground && backgroundColor != null) {
			Color prevColor = e.gc.getBackground();
			e.gc.setBackground(backgroundColor);
			e.gc.fillRectangle(x - w / 2, y - h / 2, w, h);
			e.gc.setBackground(prevColor);
		}
		if (image != null) {
			int sw = cachedBounds.width;
			int sh = cachedBounds.height;

			if (overrideSource) {
				sw = Math.min(sw, sourceSize.x);
				sh = Math.min(sh, sourceSize.y);
			}

			e.gc.drawImage(image, 0, 0, sw, sh, x - w / 2, y - h / 2, w, h);
		}
	}

	public void dispose() {
		if (backgroundColor != null)
			Cache.checkInColor(this, backgroundColor.getRGB());
		backgroundColor = null;
		if (imagePath != null)
			Cache.checkInImage(this, imagePath);
		image = null;
	}
}
