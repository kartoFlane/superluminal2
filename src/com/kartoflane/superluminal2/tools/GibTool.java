package com.kartoflane.superluminal2.tools;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kartoflane.superluminal2.ui.EditorWindow;

public class GibTool extends Tool {

	public GibTool(EditorWindow window) {
		super(window);
	}

	@Override
	public void select() {
		Control c = (Control) window.getSidebarContent();
		if (c != null && !c.isDisposed())
			c.dispose();

		// PointerComposite pointerC = new PointerComposite(window.getSidebarWidget(), true, false);
		// window.setSidebarContent(pointerC);
	}

	@Override
	public void deselect() {
	}

	@Override
	public GibTool getInstance() {
		return (GibTool) instance;
	}

	@Override
	public Composite getToolComposite(Composite parent) {
		return null; // TODO
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
	}

	@Override
	public void mouseDown(MouseEvent e) {
	}

	@Override
	public void mouseUp(MouseEvent e) {
	}

	@Override
	public void mouseMove(MouseEvent e) {
	}

	@Override
	public void mouseEnter(MouseEvent e) {
	}

	@Override
	public void mouseExit(MouseEvent e) {
	}

	@Override
	public void mouseHover(MouseEvent e) {
	}
}
