package com.kartoflane.superluminal2.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.kartoflane.superluminal2.core.Cache;
import com.kartoflane.superluminal2.utils.UIUtils;

public class ZoomWindow
		implements MouseListener, MouseMoveListener, MouseWheelListener, PaintListener {

	private static final float scaleMin = 1.0f;
	private static final float scaleMax = 6.0f;
	private static final RGB canvasRGB = new RGB(164, 164, 164);

	private static ZoomWindow instance = null;

	private Control copySource = null;
	private Point sourceSize = null;
	private Point copyOrigin = new Point(0, 0);

	private int bufferW = 0;
	private int bufferH = 0;
	private Image buffer;

	private Point dragClick = null;

	private Transform transform = null;
	private float scale = 1.0f;

	private Shell shell = null;
	private Canvas canvas;
	private Label lblInfo;

	public ZoomWindow(Shell parent, Control source) {
		instance = this;
		setCopySource(source);

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.MIN | SWT.RESIZE);
		shell.setLayout(new GridLayout(2, false));

		lblInfo = new Label(shell, SWT.NONE);
		lblInfo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1));
		lblInfo.setImage(Cache.checkOutImage(this, "cpath:/assets/help.png"));
		String msg = "- Left-click and drag to move the viewport\n" +
				"- Scroll with mouse scroll to zoom in or out";
		UIUtils.addTooltip(lblInfo, msg);

		canvas = new Canvas(shell, SWT.BORDER | SWT.DOUBLE_BUFFERED);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		canvas.setBackground(Cache.checkOutColor(this, canvasRGB));

		transform = new Transform(shell.getDisplay());
		transform.scale(scale, scale);

		shell.setSize(300, 300);
		setBufferSize(300, 300);

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
				repaint();
			}
		});

		canvas.addMouseListener(this);
		canvas.addMouseMoveListener(this);
		canvas.addMouseWheelListener(this);

		updateText();
	}

	public static ZoomWindow getInstance() {
		return instance;
	}

	public void setCopySource(Control source) {
		if (copySource != null) {
			copySource.removePaintListener(this);
		}
		copySource = source;
		if (copySource != null) {
			copySource.addPaintListener(this);
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
		repaint();
	}

	public void repaint() {
		if (copyOrigin == null || buffer == null || copySource == null)
			return;

		GC gc = new GC(copySource);
		gc.copyArea(buffer, copyOrigin.x, copyOrigin.y);
		gc.dispose();

		gc = new GC(canvas);
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
		shell.dispose();
		shell = null;
		instance = null;
	}

	private void updateText() {
		shell.setText(String.format("Zoom window - %.2f%%", scale * 100));
	}

	private void setScale(float newScale) {
		// Undo previous scaling
		transform.scale(1 / scale, 1 / scale);

		// Limit the scale
		newScale = Math.max(scaleMin, newScale);
		newScale = Math.min(scaleMax, newScale);

		scale = newScale;

		// Apply the scaling
		Point sourceSize = copySource.getSize();
		copyOrigin.x = (int) Math.min(sourceSize.x - bufferW / scale, copyOrigin.x);
		copyOrigin.y = (int) Math.min(sourceSize.y - bufferH / scale, copyOrigin.y);

		transform.scale(scale, scale);
		repaint();
		updateText();
	}

	/*
	 * =====================
	 * XXX: Listener methods
	 * =====================
	 */

	public void mouseMove(MouseEvent e) {
		if (dragClick != null) {
			copyOrigin.x += (dragClick.x - e.x) / scale;
			copyOrigin.y += (dragClick.y - e.y) / scale;

			dragClick.x = e.x;
			dragClick.y = e.y;

			Point sourceSize = copySource.getSize();
			copyOrigin.x = (int) Math.min(sourceSize.x - bufferW / scale, copyOrigin.x);
			copyOrigin.y = (int) Math.min(sourceSize.y - bufferH / scale, copyOrigin.y);
			copyOrigin.x = Math.max(0, copyOrigin.x);
			copyOrigin.y = Math.max(0, copyOrigin.y);

			repaint();
		}
	}

	public void mouseDoubleClick(MouseEvent e) {
	}

	public void mouseDown(MouseEvent e) {
		if (e.button == 1 || e.button == 2)
			dragClick = new Point(e.x, e.y);
	}

	public void mouseUp(MouseEvent e) {
		dragClick = null;
	}

	public void mouseScrolled(MouseEvent e) {
		setScale(scale + e.count / 5f);
	}

	public void paintControl(PaintEvent e) {
		// If the source widget is double buffered, then we need to redraw its area before we copy from it
		// This is because the paint event is sent before the widget itself gets painted
		if ((copySource.getStyle() & SWT.DOUBLE_BUFFERED) == SWT.DOUBLE_BUFFERED)
			copySource.redraw(copyOrigin.x, copyOrigin.y,
					(int) (bufferW / scale), (int) (bufferH / scale), false);
		repaint();
	}
}
