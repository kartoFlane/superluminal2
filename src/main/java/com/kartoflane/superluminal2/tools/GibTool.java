package com.kartoflane.superluminal2.tools;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.sidebar.GibToolComposite;

public class GibTool extends Tool {

	public GibTool(EditorWindow window) {
		super(window);
	}

	@Override
	public void select() {
		disposeSidebarContent();

		GibToolComposite gibC = getToolComposite(window.getSidebarWidget());
		window.setSidebarContent(gibC);
	}

	@Override
	public void deselect() {
	}

	@Override
	public GibToolComposite getToolComposite(Composite parent) {
		return (GibToolComposite) super.getToolComposite(parent);
	}

	@Override
	public GibToolComposite createToolComposite(Composite parent) {
		if (parent == null)
			throw new IllegalArgumentException("Parent must not be null.");
		compositeInstance = new GibToolComposite(parent);
		return (GibToolComposite) compositeInstance;
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
