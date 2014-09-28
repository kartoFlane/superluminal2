package com.kartoflane.superluminal2.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.kartoflane.superluminal2.components.interfaces.MouseInputListener;
import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.utils.UIUtils;

public class ZoomWindow
		implements MouseInputListener, PaintListener, Listener {

	private static final float scaleMin = 1.0f;
	private static final float scaleMax = 6.0f;
	private static final RGB canvasRGB = new RGB(164, 164, 164);

	private static ZoomWindow instance;

	private volatile boolean oddPaintEvent = false;
	private boolean canvasRedrawn = true;

	private Control copySource = null;
	private Point sourceSize = null;
	private Point copyOrigin = new Point(0, 0);

	private int bufferW = 0;
	private int bufferH = 0;
	private Image buffer;

	private Point dragClick = null;

	private Transform transform = null;
	private float scale = 1.0f;

	private Shell shell;
	private Canvas canvas;
	private Label lblInfo;

	public ZoomWindow(Shell parent, Control source) {
		instance = this;

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.MIN | SWT.RESIZE);
		shell.setLayout(new GridLayout(1, false));
		String msg = "- Left-click and drag to move the viewport\n" +
				"- Scroll with mouse scroll to zoom in or out";

		lblInfo = new Label(shell, SWT.NONE);
		lblInfo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblInfo.setImage(Cache.checkOutImage(this, "cpath:/assets/help.png"));
		UIUtils.addTooltip(lblInfo, msg);

		canvas = new Canvas(shell, SWT.BORDER | SWT.DOUBLE_BUFFERED);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		canvas.setBackground(Cache.checkOutColor(this, canvasRGB));

		transform = new Transform(shell.getDisplay());
		transform.scale(scale, scale);

		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event e) {
				dispose();
			}
		});

		shell.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				copyOrigin.x = (int) Math.min(sourceSize.x - bufferW / scale, copyOrigin.x);
				copyOrigin.y = (int) Math.min(sourceSize.y - bufferH / scale, copyOrigin.y);

				setBufferSize(shell.getClientArea().width, canvas.getClientArea().height);
				repaint(false);
			}
		});

		canvas.addMouseListener(this);
		canvas.addMouseMoveListener(this);

		updateText();
		setCopySource(source);
		shell.setSize(300, 300);
		setBufferSize(300, 300);
	}

	public static ZoomWindow getInstance() {
		return instance;
	}

	public void setCopySource(Control source) {
		if (copySource != null) {
			copySource.removePaintListener(this);
			copySource.removeMouseMoveListener(this);
			copySource.removeMouseListener(this);

			Display display = UIUtils.getDisplay();
			display.removeFilter(SWT.MouseWheel, this);
		}

		copySource = source;

		if (copySource != null) {
			copySource.addPaintListener(this);
			copySource.addMouseMoveListener(this);
			copySource.addMouseListener(this);

			Display display = UIUtils.getDisplay();
			display.addFilter(SWT.MouseWheel, this);
			sourceSize = copySource.getSize();
		}
	}

	public void setCopyAreaLoc(int x, int y) {
		Point sourceSize = copySource.getSize();
		copyOrigin.x = (int) Math.min(sourceSize.x - bufferW / scale, x);
		copyOrigin.y = (int) Math.min(sourceSize.y - bufferH / scale, y);
		copyOrigin.x = Math.max(0, x);
		copyOrigin.y = Math.max(0, y);
	}

	public void setBufferSize(int w, int h) {
		Point sourceSize = copySource.getSize();
		bufferW = (int) Math.min(sourceSize.x, w);
		bufferH = (int) Math.min(sourceSize.y, h);

		if (buffer != null) {
			buffer.dispose();
			buffer = null;
		}
		if (bufferW <= 0 || bufferH <= 0)
			return;

		buffer = new Image(shell.getDisplay(), bufferW, bufferH);
	}

	public void open() {
		shell.open();
		shell.setMinimized(false);
		repaint(false);
	}

	public void repaint(boolean wipe) {
		if (copyOrigin == null || buffer == null || copySource == null)
			return;

		GC gc = new GC(copySource);
		gc.copyArea(buffer, copyOrigin.x, copyOrigin.y);
		gc.dispose();

		gc = new GC(canvas);
		if (wipe) {
			gc.fillRectangle(canvas.getClientArea());
			canvasRedrawn = true;
		}
		gc.setTransform(transform);
		gc.drawImage(buffer, 0, 0);

		gc.dispose();
	}

	public void dispose() {
		Cache.checkInColor(this, canvasRGB);
		Cache.checkInImage(this, "cpath:/assets/help.png");
		setCopySource(null);

		buffer.dispose();
		buffer = null;
		transform.dispose();
		transform = null;
		shell.dispose();
		shell = null;

		instance = null;
	}

	private void updateText() {
		shell.setText(String.format("Zoom Window - %.2f%%", scale * 100));
	}

	private void setScale(float newScale) {
		// Undo previous scaling
		transform.scale(1 / scale, 1 / scale);

		// Limit the scale
		newScale = Math.max(scaleMin, newScale);
		newScale = Math.min(scaleMax, newScale);

		// Sometimes when going from high magnification to lower, artifacts can appear
		Rectangle canvasArea = canvas.getClientArea();
		if ((bufferW * scale > canvasArea.width && bufferW * newScale < canvasArea.width) ||
				(bufferH * scale > canvasArea.height && bufferH * newScale < canvasArea.height))
			canvasRedrawn = false;

		scale = newScale;

		// Apply the scaling
		Point sourceSize = copySource.getSize();
		copyOrigin.x = (int) Math.min(sourceSize.x - bufferW / scale, copyOrigin.x);
		copyOrigin.y = (int) Math.min(sourceSize.y - bufferH / scale, copyOrigin.y);

		transform.scale(scale, scale);
		repaint(!canvasRedrawn);
		updateText();
	}

	private boolean isStyle(int controlStyle, int checkStyle) {
		return (controlStyle & checkStyle) == checkStyle;
	}

	/*
	 * =====================
	 * XXX: Listener methods
	 * =====================
	 */

	public void mouseMove(MouseEvent e) {
		if (dragClick != null) {
			copyOrigin.x += dragClick.x - e.x;
			copyOrigin.y += dragClick.y - e.y;

			dragClick.x = e.x;
			dragClick.y = e.y;

			Point sourceSize = copySource.getSize();
			copyOrigin.x = (int) Math.min(sourceSize.x - bufferW / scale, copyOrigin.x);
			copyOrigin.y = (int) Math.min(sourceSize.y - bufferH / scale, copyOrigin.y);
			copyOrigin.x = Math.max(0, copyOrigin.x);
			copyOrigin.y = Math.max(0, copyOrigin.y);

			repaint(false);
		}
	}

	public void mouseDoubleClick(MouseEvent e) {
	}

	public void mouseDown(MouseEvent e) {
		Object source = e.getSource();

		if ((source == canvas && e.button == 1) || e.button == 2) {
			dragClick = new Point(e.x, e.y);
		}
	}

	public void mouseUp(MouseEvent e) {
		dragClick = null;
	}

	public void mouseScrolled(MouseEvent e) {
		Object source = e.getSource();
		if (source == canvas || source == EditorWindow.getInstance().getSidebarWidget())
			setScale(scale + e.count / 5f);
	}

	public void paintControl(PaintEvent e) {
		oddPaintEvent = !oddPaintEvent;

		// If the source widget is double buffered, then we need to redraw its area before we copy from it
		// This is because the first paint event is sent when the image is painted to buffer,
		// and the second event when the canvas itself gets painted
		if (!oddPaintEvent) {
			if (isStyle(copySource.getStyle(), SWT.DOUBLE_BUFFERED)) {
				copySource.redraw(copyOrigin.x, copyOrigin.y,
						(int) (bufferW / scale), (int) (bufferH / scale), false);
			} else {
				// If it's not double buffered, then we want to redraw with every paint event...
				oddPaintEvent = true;
			}
		}

		if (oddPaintEvent)
			repaint(false);
	}

	@Override
	public void handleEvent(Event e) {
		if (e.type == SWT.MouseWheel) {
			mouseScrolled(new MouseEvent(e));
		}
	}

	public void mouseEnter(MouseEvent e) {
	}

	public void mouseExit(MouseEvent e) {
	}

	public void mouseHover(MouseEvent e) {
	}
}