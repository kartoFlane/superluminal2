package com.kartoflane.superluminal2.tools;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;

import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.mvc.controllers.CursorController;
import com.kartoflane.superluminal2.mvc.views.CursorView;
import com.kartoflane.superluminal2.ui.EditorWindow;

/**
 * A simple class representing a tool.<br>
 * <br>
 * Used to store various properties of each tool, as an alternative to keeping
 * a haphazard assortment of variables in EditorWindow class.<br>
 * Each tool also implements its own specific handling of mouse events, as well as
 * cursor behaviour.
 * 
 * @author kartoFlane
 * 
 */
public abstract class Tool implements MouseListener, MouseMoveListener, MouseTrackListener {
	public enum Tools {
		POINTER,
		CREATOR,
		GIB,
		CONFIG,
		ROOM,
		DOOR,
		WEAPON,
		STATION
	};

	protected Tool instance = null;
	protected CursorController cursor = null;
	protected CursorView cursorView = null;
	protected EditorWindow window = null;
	protected Layers[] selectableLayerIds;

	public Tool(EditorWindow window) {
		this.window = window;
		instance = this;

		cursor = CursorController.getInstance();
		cursorView = cursor.getView();

		Layers[] allLayerIds = window.getPainter().getSelectionOrder();
		Layers[] ignoredLayers = { Layers.CURSOR, Layers.GRID, Layers.OVERLAY, Layers.SYSTEM, Layers.STATION };
		selectableLayerIds = new Layers[allLayerIds.length];

		for (int i = 0; i < allLayerIds.length; i++) {
			// leave uninteresting layers as null
			if (ignoredLayers == null || !containsLayer(ignoredLayers, allLayerIds[i]))
				selectableLayerIds[i] = allLayerIds[i];
		}
	}

	private boolean containsLayer(Layers[] layerIds, Layers layer) {
		for (int i = 0; i < layerIds.length; i++) {
			if (layerIds[i].equals(layer))
				return true;
		}
		return false;
	}

	/**
	 * Override this to execute actions when the tool is selected.<br>
	 * In order to actually select the tool, use {@link EditorWindow#selectTool(ToolItem)}
	 */
	public abstract void select();

	/** Override this to execute actions when the tool is deselected. */
	public abstract void deselect();

	public abstract Tool getInstance();

	public abstract Composite getToolComposite(Composite parent);

	public void mouseMove(MouseEvent e) {
		cursor.mouseMove(e);
	}
}
