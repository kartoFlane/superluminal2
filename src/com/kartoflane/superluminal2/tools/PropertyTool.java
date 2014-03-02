package com.kartoflane.superluminal2.tools;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.sidebar.PropertiesToolComposite;

public class PropertyTool extends Tool {

	public PropertyTool(EditorWindow window) {
		super(window);
	}

	@Override
	public void select() {
		Control c = (Control) window.getSidebarContent();
		if (c != null && !c.isDisposed())
			c.dispose();

		PropertiesToolComposite createC = getToolComposite(window.getSidebarWidget());
		window.setSidebarContent(createC);
	}

	@Override
	public void deselect() {
	}

	@Override
	public PropertyTool getInstance() {
		return (PropertyTool) instance;
	}

	@Override
	public PropertiesToolComposite getToolComposite(Composite parent) {
		return new PropertiesToolComposite(parent);
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
