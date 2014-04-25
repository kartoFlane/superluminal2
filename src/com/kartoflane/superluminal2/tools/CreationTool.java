package com.kartoflane.superluminal2.tools;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;

import com.kartoflane.superluminal2.ui.EditorWindow;
import com.kartoflane.superluminal2.ui.sidebar.CreationToolComposite;

/**
 * "Dummy" tool, used to create the sidebar toolkit when clicked.
 * 
 * @see {@link RoomTool}<br>
 *      {@link DoorTool}<br>
 *      {@link MountTool}<br>
 *      {@link StationTool}
 * @author kartoFlane
 * 
 */
public class CreationTool extends Tool {

	private Tools selectedSubtool = Tools.ROOM;

	public CreationTool(EditorWindow window) {
		super(window);
	}

	@Override
	public void select() {
		disposeSidebarContent();

		CreationToolComposite createC = getToolComposite(window.getSidebarWidget());
		window.setSidebarContent(createC);
		createC.updateData();
	}

	@Override
	public void deselect() {
	}

	public void setSelectedSubtool(Tools subtool) {
		selectedSubtool = subtool;
	}

	public Tools getSelectedSubtool() {
		return selectedSubtool;
	}

	public void selectSubtool(Tools subtool) {
		try {
			CreationToolComposite composite = getToolComposite(null);
			setSelectedSubtool(subtool);
			composite.updateData();
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("Sidebar widget for Creation Tool does not exist.");
		}
	}

	@Override
	public CreationToolComposite getToolComposite(Composite parent) {
		return (CreationToolComposite) super.getToolComposite(parent);
	}

	@Override
	protected CreationToolComposite createToolComposite(Composite parent) {
		if (parent == null)
			throw new IllegalArgumentException("Parent must not be null.");
		compositeInstance = new CreationToolComposite(parent);
		return (CreationToolComposite) compositeInstance;
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
