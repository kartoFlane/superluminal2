package com.kartoflane.superluminal2.tools;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;

import com.kartoflane.superluminal2.components.LayeredPainter;
import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.mvc.controllers.CursorController;
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
		IMAGES,
		ROOM,
		DOOR,
		WEAPON,
		STATION
	};

	protected static Layers[] selectableLayerIds;

	protected Composite compositeInstance = null;
	protected CursorController cursor = null;
	protected EditorWindow window = null;

	public Tool(EditorWindow window) {
		this.window = window;

		cursor = CursorController.getInstance();

		if (selectableLayerIds == null) {
			Layers[] allLayerIds = LayeredPainter.getInstance().getSelectionOrder();
			Layers[] ignoredLayers = { Layers.CURSOR, Layers.GRID, Layers.SYSTEM, Layers.STATION };
			selectableLayerIds = new Layers[allLayerIds.length];

			for (int i = 0; i < allLayerIds.length; i++) {
				// leave uninteresting layers as null
				if (ignoredLayers == null || !containsLayer(ignoredLayers, allLayerIds[i]))
					selectableLayerIds[i] = allLayerIds[i];
			}
		}
	}

	private boolean containsLayer(Layers[] layerIds, Layers layer) {
		for (int i = 0; i < layerIds.length; i++) {
			if (layerIds[i].equals(layer))
				return true;
		}
		return false;
	}

	public static Layers[] getSelectableLayerIds() {
		return selectableLayerIds;
	}

	/**
	 * Override this to execute actions when the tool is selected.<br>
	 * In order to actually select the tool, use {@link EditorWindow#selectTool(ToolItem)}
	 */
	public abstract void select();

	/** Override this to execute actions when the tool is deselected. */
	public abstract void deselect();

	/**
	 * Returns an instance of a composite that represents this tool.<br>
	 * If no instance is available, a new instance will be created for the supplied parent.
	 * 
	 * 
	 * @param parent
	 *            the parent that this tool's composite will be created for
	 * @return composite that represents this tool
	 */
	public Composite getToolComposite(Composite parent) {
		if (compositeInstance == null || compositeInstance.isDisposed() ||
				(compositeInstance.getParent() != parent && parent != null))
			createToolComposite(parent);
		return compositeInstance;
	}

	/** Creates a new instance of the composite that represents the tool. */
	protected abstract Composite createToolComposite(Composite parent);

	public void mouseMove(MouseEvent e) {
		cursor.mouseMove(e);
	}

	public void mouseDown(MouseEvent e) {
		cursor.mouseDown(e);
	}

	public void mouseUp(MouseEvent e) {
		cursor.mouseUp(e);
	}
}
