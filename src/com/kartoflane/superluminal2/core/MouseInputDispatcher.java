package com.kartoflane.superluminal2.core;

import org.eclipse.swt.events.MouseEvent;

import com.kartoflane.superluminal2.components.interfaces.MouseInputListener;
import com.kartoflane.superluminal2.tools.Tool;

public class MouseInputDispatcher implements MouseInputListener {

	private static MouseInputDispatcher instance = null;
	private Tool currentTool = null;

	public MouseInputDispatcher() {
		instance = this;
	}

	public void setCurrentTool(Tool tool) {
		currentTool = tool;
	}

	public static MouseInputDispatcher getInstance() {
		return instance;
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (e.button == 1)
			Manager.leftMouseDown = true;
		if (e.button == 3)
			Manager.rightMouseDown = true;

		if (currentTool != null)
			currentTool.mouseDown(e);
	}

	@Override
	public void mouseUp(MouseEvent e) {
		if (e.button == 1)
			Manager.leftMouseDown = false;
		if (e.button == 3)
			Manager.rightMouseDown = false;

		if (currentTool != null)
			currentTool.mouseUp(e);
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if (currentTool != null)
			currentTool.mouseMove(e);
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		if (currentTool != null)
			currentTool.mouseDoubleClick(e);
	}

	@Override
	public void mouseEnter(MouseEvent e) {
		if (currentTool != null)
			currentTool.mouseEnter(e);
	}

	@Override
	public void mouseExit(MouseEvent e) {
		if (currentTool != null)
			currentTool.mouseExit(e);
	}

	@Override
	public void mouseHover(MouseEvent e) {
		if (currentTool != null)
			currentTool.mouseHover(e);
	}
}
