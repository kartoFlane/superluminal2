package com.kartoflane.superluminal2.tools;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.sidebar.ImagesToolComposite;

public class ImagesTool extends Tool {

	public ImagesTool(EditorWindow window) {
		super(window);
	}

	@Override
	public void select() {
		window.disposeSidebarContent();

		ImagesToolComposite createC = getToolComposite(window.getSidebarWidget());
		window.setSidebarContent(createC);
	}

	@Override
	public void deselect() {
	}

	@Override
	public ImagesToolComposite getToolComposite(Composite parent) {
		return (ImagesToolComposite) super.getToolComposite(parent);
	}

	@Override
	public ImagesToolComposite createToolComposite(Composite parent) {
		if (parent == null)
			throw new IllegalArgumentException("Parent must not be null.");
		compositeInstance = new ImagesToolComposite(parent);
		return (ImagesToolComposite) compositeInstance;
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
