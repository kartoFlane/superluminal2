package com.kartoflane.superluminal2.components;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal2.mvc.controllers.CellController;

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

	private static Grid instance;

	private boolean visible = true;
	private Rectangle bounds = null;
	private ArrayList<ArrayList<CellController>> cells = null;

	private Grid() {
		instance = this;

		cells = new ArrayList<ArrayList<CellController>>();
		bounds = new Rectangle(0, 0, 0, 0);
	}

	public Grid(int x, int y) {
		this();

		for (int i = 0; i < x; i++) {
			cells.add(new ArrayList<CellController>());
			for (int j = 0; j < y; j++)
				cells.get(i).add(CellController.newInstance(i, j));
		}

		updateBounds(x * CellController.SIZE, y * CellController.SIZE);
	}

	public static Grid getInstance() {
		return instance;
	}

	public void updateBounds(int width, int height) {
		// add one after division as a buffer
		bounds.width = (width / CellController.SIZE + 1) * CellController.SIZE;
		bounds.height = (height / CellController.SIZE + 1) * CellController.SIZE;

		for (int i = 1; i <= bounds.width / CellController.SIZE; i++) {
			if (cells.size() < i)
				cells.add(new ArrayList<CellController>());
			for (int j = 1; j <= bounds.height / CellController.SIZE; j++)
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

	public Rectangle getBounds() {
		return new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	public Point getSize() {
		return new Point(bounds.width, bounds.height);
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		for (ArrayList<CellController> list : cells) {
			for (CellController c : list)
				c.setVisible(visible);
		}
	}

	public boolean isVisible() {
		return visible;
	}

	/** @return a Cell at the given coordinates, or null if out of grid. */
	public CellController getCellAt(int x, int y) {
		return getCell(x / CellController.SIZE, y / CellController.SIZE);
	}

	/**
	 * Intended to only be used when the coordinates fall outside of the grid.
	 * If the coordinates are inside the grid, use {@link #getCellAt(int, int)} instead.
	 */
	public CellController getClosestCell(int x, int y) {
		int width = bounds.width - bounds.width % CellController.SIZE - CellController.SIZE;
		int height = bounds.height - bounds.height % CellController.SIZE - CellController.SIZE;

		x = x < width ? x / CellController.SIZE : width / CellController.SIZE - 1;
		y = y < height ? y / CellController.SIZE : height / CellController.SIZE - 1;

		return getCell(x, y);
	}

	/**
	 * @return a Cell with the given position in the grid, or null if out of bounds.
	 */
	public CellController getCell(int i, int j) {
		try {
			return cells.get(i).get(j);
		} catch (Exception e) {
			return null;
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
		// don't want values outside of the canvas area
		x = Math.min(Math.max(x, 0), bounds.width);
		y = Math.min(Math.max(y, 0), bounds.height);

		Point p = new Point(x, y);
		CellController c = getCellAt(x, y);

		if (c == null || !c.isVisible())
			c = getClosestCell(x, y);

		switch (snapmode) {
			case FREE:
				break;
			case CELL:
				p = c.getLocation();
				// correct by 1 so that boxes appear centered
				p.x--;
				p.y--;
				break;
			case LINE_H:
				if (Math.abs(c.getBounds().y - y) < Math.abs(c.getBounds().y + CellController.SIZE - y))
					p.y = c.getBounds().y;
				else
					p.y = c.getBounds().y + CellController.SIZE;
				break;
			case LINE_V:
				if (Math.abs(c.getBounds().x - x) < Math.abs(c.getBounds().x + CellController.SIZE - x))
					p.x = c.getBounds().x;
				else
					p.x = c.getBounds().x + CellController.SIZE;
				break;
			case CROSS:
				if (Math.abs(c.getBounds().x - x) < Math.abs(c.getBounds().x + CellController.SIZE - x))
					p.x = c.getBounds().x + 1;
				else
					p.x = c.getBounds().x + CellController.SIZE + 1;

				if (Math.abs(c.getBounds().y - y) < Math.abs(c.getBounds().y + CellController.SIZE - y))
					p.y = c.getBounds().y + 1;
				else
					p.y = c.getBounds().y + CellController.SIZE + 1;
				break;
			case EDGE_H:
				p.x = c.getLocation().x - 1;

				if (Math.abs(c.getBounds().y - y) < Math.abs(c.getBounds().y + CellController.SIZE - y))
					p.y = c.getBounds().y + 1;
				else
					p.y = c.getBounds().y + CellController.SIZE + 1;
				break;
			case EDGE_V:
				p.y = c.getLocation().y - 1;

				if (Math.abs(c.getBounds().x - x) < Math.abs(c.getBounds().x + CellController.SIZE - x))
					p.x = c.getBounds().x + 1;
				else
					p.x = c.getBounds().x + CellController.SIZE + 1;
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
				// round to any multiple of 17.5 (CellController.SIZE / 2.0)
				// just subtract the remainder of division, and add 1 to correct
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
				p.x -= CellController.SIZE / 2 + 1;
				p.y -= CellController.SIZE / 2 + 1;
				break;
			case CORNER_TR:
				p = c.getLocation();
				p.x += CellController.SIZE / 2 + 1;
				p.y -= CellController.SIZE / 2 + 1;
				break;
			case CORNER_BL:
				p = c.getLocation();
				p.x -= CellController.SIZE / 2 + 1;
				p.y += CellController.SIZE / 2 + 1;
				break;
			case CORNER_BR:
				p = c.getLocation();
				p.x += CellController.SIZE / 2 + 1;
				p.y += CellController.SIZE / 2 + 1;
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