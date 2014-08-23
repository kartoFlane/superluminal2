package com.kartoflane.superluminal2.core;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal2.mvc.controllers.CellController;
import com.kartoflane.superluminal2.ui.ShipContainer;

/**
 * A class representing the grid, consisting of individual {@link Cell} objects.
 * 
 * Acts as a central system for positioning various entities on the canvas.
 * 
 * @author kartoFlane
 */
public class Grid {

	public enum Snapmodes {
		/** No snapping occurs. */
		FREE,
		/** Snaps to the center of the nearest grid cell. */
		CELL,
		/** Snaps to the nearest horizontal grid line. */
		LINE_H,
		/** Snaps to the nearest vertical grid line. */
		LINE_V,
		/** Snaps to the nearest intersection of grid lines (LINE_H and LINE_V). */
		CROSS,
		/** Snaps to the center of the nearest horizontal grid wall. */
		EDGE_H,
		/** Snaps to the center of the nearest vertical grid wall. */
		EDGE_V,
		/** Snaps to the center of the nearest grid wall (EDGE_H or EDGE_V). */
		EDGES,
		/** Snaps to the center of the nearest northern horizontal grid wall. */
		EDGE_TOP_H,
		/** Snaps to the center of the nearest western vertical grid wall. */
		EDGE_TOP_V,
		/** Snaps to any multiple of 17.5 (CELL or CROSS or EDGES). */
		ANY,
		/** Snaps to the top-left corner of the given cell. */
		CORNER_TL,
		/** Snaps to the top-right corner of the given cell. */
		CORNER_TR,
		/** Snaps to the bottom-left corner of the given cell. */
		CORNER_BL,
		/** Snaps to the bottom-right corner of the given cell. */
		CORNER_BR;
	}

	private static final Grid _instance = new Grid();

	private boolean drawGrid = true;
	private Rectangle bounds = null;
	private ArrayList<ArrayList<CellController>> cells = null;

	private Grid() {
		cells = new ArrayList<ArrayList<CellController>>();
		bounds = new Rectangle(0, 0, 0, 0);
	}

	public static Grid getInstance() {
		return _instance;
	}

	/**
	 * Updates the grid to the desired dimensions.
	 * 
	 * @param w
	 *            width of the grid, in grid cells
	 * @param h
	 *            height of the grid, in grid cells
	 */
	public void setCells(int w, int h) {
		updateBounds(w * ShipContainer.CELL_SIZE, h * ShipContainer.CELL_SIZE);
	}

	/**
	 * Updates the grid, instantiating new cells or hiding existing ones, as required
	 * to accomodate the new size.
	 * 
	 * @param width
	 *            new width of the grid, in pixels
	 * @param height
	 *            new height of the grid, in pixels
	 */
	public void updateBounds(int width, int height) {
		// Add one after the division so that cells fill all of the available space
		bounds.width = (width / ShipContainer.CELL_SIZE + 1) * ShipContainer.CELL_SIZE;
		bounds.height = (height / ShipContainer.CELL_SIZE + 1) * ShipContainer.CELL_SIZE;

		for (int i = 1; i <= bounds.width / ShipContainer.CELL_SIZE; i++) {
			if (cells.size() < i)
				cells.add(new ArrayList<CellController>());
			for (int j = 1; j <= bounds.height / ShipContainer.CELL_SIZE; j++)
				if (cells.get(i - 1).size() < j)
					cells.get(i - 1).add(CellController.newInstance(i - 1, j - 1));
		}

		for (ArrayList<CellController> list : cells) {
			for (CellController c : list) {
				Rectangle b = c.getBounds();
				c.setVisible(bounds.contains(b.x + 1, b.y + 1) && bounds.contains(b.x + b.width - 2, b.y + b.height - 2));
			}
		}
	}

	/**
	 * @return the rectangle describing the grid's area relative to the canvas, in pixels.
	 */
	public Rectangle getBounds() {
		return new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	/**
	 * @return size of the grid, in pixels
	 */
	public Point getSize() {
		return new Point(bounds.width, bounds.height);
	}

	public void setVisible(boolean visible) {
		drawGrid = visible;
		for (ArrayList<CellController> list : cells) {
			for (CellController c : list)
				c.redraw();
		}
	}

	public boolean isVisible() {
		return drawGrid;
	}

	/** @return a Cell at the given coordinates, or null if out of grid. */
	public CellController getCellAt(int x, int y) {
		return getCell(x / ShipContainer.CELL_SIZE, y / ShipContainer.CELL_SIZE);
	}

	/**
	 * Intended to only be used when the coordinates fall outside of the grid.
	 * If the coordinates are inside the grid, use {@link #getCellAt(int, int)} instead.
	 */
	public CellController getClosestCell(int x, int y) {
		int width = bounds.width - bounds.width % ShipContainer.CELL_SIZE - ShipContainer.CELL_SIZE;
		int height = bounds.height - bounds.height % ShipContainer.CELL_SIZE - ShipContainer.CELL_SIZE;

		x = x < width ? x / ShipContainer.CELL_SIZE : width / ShipContainer.CELL_SIZE - 1;
		y = y < height ? y / ShipContainer.CELL_SIZE : height / ShipContainer.CELL_SIZE - 1;

		return getCell(x, y);
	}

	/**
	 * @return a Cell with the given position in the grid.
	 */
	public CellController getCell(int i, int j) {
		try {
			int sx = Math.min(i, cells.size() - 1);
			int sy = Math.min(j, cells.get(sx).size() - 1);
			return cells.get(sx).get(sy);
		} catch (Exception e) {
			return cells.get(0).get(0);
		}
	}

	/**
	 * @return true if a visible {@link CellController} can be found at the given coordinates, false otherwise.
	 */
	public boolean isLocAccessible(int x, int y) {
		CellController c = getCellAt(x, y);
		return c != null && c.isVisible();
	}

	/**
	 * Aligns the given coordinates to the grid using the specified snapmode.
	 * 
	 * @param snapmode
	 *            the snapping method used to align the point to the grid
	 * 
	 * @see Snapmodes#FREE
	 * @see Snapmodes#CELL
	 * @see Snapmodes#LINE_H
	 * @see Snapmodes#LINE_V
	 * @see Snapmodes#CROSS
	 * @see Snapmodes#EDGE_H
	 * @see Snapmodes#EDGE_V
	 * @see Snapmodes#EDGES
	 * @see Snapmodes#ANY
	 * @see Snapmodes#EDGE_TOP_H
	 * @see Snapmodes#EDGE_TOP_V
	 * @see Snapmodes#CORNER_TL
	 * @see Snapmodes#CORNER_TR
	 * @see Snapmodes#CORNER_BL
	 * @see Snapmodes#CORNER_BR
	 */
	public Point snapToGrid(int x, int y, Snapmodes snapmode) {
		// Don't want values outside of the canvas area
		x = Math.min(Math.max(x, 0), bounds.width);
		y = Math.min(Math.max(y, 0), bounds.height);

		Point p = new Point(x, y);
		// Try to find a cell at the given coordinates
		CellController c = getCellAt(x, y);

		// If none is found (ie. point is outside of grid area), try to find the closest one
		if (c == null || !c.isVisible())
			c = getClosestCell(x, y);

		switch (snapmode) {
			case FREE:
				break;
			case CELL:
				p = c.getLocation();
				// Correct by 1 so that boxes appear centered
				p.x--;
				p.y--;
				break;
			case LINE_H:
				if (Math.abs(c.getBounds().y - y) < Math.abs(c.getBounds().y + ShipContainer.CELL_SIZE - y))
					p.y = c.getBounds().y;
				else
					p.y = c.getBounds().y + ShipContainer.CELL_SIZE;
				break;
			case LINE_V:
				if (Math.abs(c.getBounds().x - x) < Math.abs(c.getBounds().x + ShipContainer.CELL_SIZE - x))
					p.x = c.getBounds().x;
				else
					p.x = c.getBounds().x + ShipContainer.CELL_SIZE;
				break;
			case CROSS:
				if (Math.abs(c.getBounds().x - x) < Math.abs(c.getBounds().x + ShipContainer.CELL_SIZE - x))
					p.x = c.getBounds().x + 1;
				else
					p.x = c.getBounds().x + ShipContainer.CELL_SIZE + 1;

				if (Math.abs(c.getBounds().y - y) < Math.abs(c.getBounds().y + ShipContainer.CELL_SIZE - y))
					p.y = c.getBounds().y + 1;
				else
					p.y = c.getBounds().y + ShipContainer.CELL_SIZE + 1;
				break;
			case EDGE_H:
				p.x = c.getLocation().x - 1;

				if (Math.abs(c.getBounds().y - y) < Math.abs(c.getBounds().y + ShipContainer.CELL_SIZE - y))
					p.y = c.getBounds().y + 1;
				else
					p.y = c.getBounds().y + ShipContainer.CELL_SIZE + 1;
				break;
			case EDGE_V:
				p.y = c.getLocation().y - 1;

				if (Math.abs(c.getBounds().x - x) < Math.abs(c.getBounds().x + ShipContainer.CELL_SIZE - x))
					p.x = c.getBounds().x + 1;
				else
					p.x = c.getBounds().x + ShipContainer.CELL_SIZE + 1;
				break;
			case EDGES:
				Point a = snapToGrid(x, y, Snapmodes.EDGE_H);
				Point b = snapToGrid(x, y, Snapmodes.EDGE_V);

				if (Math.sqrt(Math.pow(a.x - x, 2) + Math.pow(a.y - y, 2)) < Math.sqrt(Math.pow(b.x - x, 2) + Math.pow(b.y - y, 2)))
					p = a;
				else
					p = b;
				break;
			case ANY:
				// Round to any multiple of 17.5 (ShipContainer.CELL_SIZE / 2.0)
				// Just subtract the remainder of division, and add 1 to correct
				p.x += -p.x % 17.5 + 1;
				p.y += -p.y % 17.5 + 1;
				break;
			case EDGE_TOP_H:
				p.x = c.getLocation().x;
				p.y = c.getBounds().y + 1;
				break;
			case EDGE_TOP_V:
				p.x = c.getBounds().x + 1;
				p.y = c.getLocation().y;
				break;
			case CORNER_TL:
				p = c.getLocation();
				p.x -= ShipContainer.CELL_SIZE / 2 + 1;
				p.y -= ShipContainer.CELL_SIZE / 2 + 1;
				break;
			case CORNER_TR:
				p = c.getLocation();
				p.x += ShipContainer.CELL_SIZE / 2 + 1;
				p.y -= ShipContainer.CELL_SIZE / 2 + 1;
				break;
			case CORNER_BL:
				p = c.getLocation();
				p.x -= ShipContainer.CELL_SIZE / 2 + 1;
				p.y += ShipContainer.CELL_SIZE / 2 + 1;
				break;
			case CORNER_BR:
				p = c.getLocation();
				p.x += ShipContainer.CELL_SIZE / 2 + 1;
				p.y += ShipContainer.CELL_SIZE / 2 + 1;
				break;

			default:
				throw new IllegalArgumentException("Invalid snap mode.");
		}

		return p;
	}

	/**
	 * {@linkplain #snapToGrid(int, int, Snapmodes)}
	 */
	public Point snapToGrid(Point p, Snapmodes snapmode) {
		return snapToGrid(p.x, p.y, snapmode);
	}

	/**
	 * Disposes of the grid, clearing all cells and lists.
	 */
	public void dispose() {
		for (ArrayList<CellController> list : cells) {
			for (CellController c : list)
				c.dispose();
			list.clear();
			list = null;
		}
		cells.clear();
		cells = null;
		System.gc();
	}
}
