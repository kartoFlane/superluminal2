package com.kartoflane.superluminal2.tools;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.sidebar.PropertiesToolComposite;

public class PropertyTool extends Tool {

	public PropertyTool(EditorWindow window) {
		super(window);
	}

	@Override
	public void select() {
		disposeSidebarContent();

		PropertiesToolComposite createC = getToolComposite(window.getSidebarWidget());
		window.setSidebarContent(createC);
	}

	@Override
	public void deselect() {
	}

	@Override
	public PropertiesToolComposite getToolComposite(Composite parent) {
		return (PropertiesToolComposite) super.getToolComposite(parent);
	}

	@Override
	public PropertiesToolComposite createToolComposite(Composite parent) {
		if (parent == null)
			throw new IllegalArgumentException("Parent must not be null.");
		compositeInstance = new PropertiesToolComposite(parent);
		return (PropertiesToolComposite) compositeInstance;
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
	}

	@Override
	public void mouseDown(MouseEvent e) {
	}

	@Override
	public void mouseUp(MouseEvent e) {
		// Returns focus to the main window when user clicks on the canvas, allowing to use hotkeys
		if (!window.isFocusControl())
			window.forceFocus();
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
