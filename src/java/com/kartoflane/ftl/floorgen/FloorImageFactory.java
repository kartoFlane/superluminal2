package com.kartoflane.ftl.floorgen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

/**
 * A "factory" of floor images for ships' layouts.<br>
 * <br>
 * Can generate a matching floor image for any sanely constructed room layout (ie. without
 * any airlocks in the middle of a room, etc). Does not account for doors linked to
 * non-neighbouring rooms (ie. won't draw indentations for them).<br>
 * <br>
 * Settings other than default may end up producing floor images of questionable quality.<br>
 * <br>
 * Example usage: <code>
 * <pre>
 * FloorImageFactory fif = new FloorImageFactory();
 * // optional factory configuration
 * BufferedImage floorImage = fif.generateFloorImage(layoutFile);
 * ImageIO.write(floorImage, "PNG", new File("floor_image.png"));
 * </pre>
 * </code>
 * 
 * @author kartoFlane
 *
 */
public class FloorImageFactory {

	// @formatter:off
	private static final int cellSize =				35;
	private static final Color transparentColor = 	new Color(0, 0, 0, 0);
	// @formatter:on

	private int floorMargin;
	private int borderWidth;

	private Color floorColor;
	private Color borderColor;

	/**
	 * Creates a new FloorImageFactory object with default settings.
	 */
	public FloorImageFactory() {
		floorMargin = 4;
		borderWidth = 2;
		floorColor = Color.GRAY;
		borderColor = Color.BLACK;
	}

	/*
	 * ================================
	 * Getters & setters for properties
	 */

	/**
	 * Sets the distance between the border and room walls.<br>
	 * Default: 4
	 */
	public void setFloorMargin(int margin) {
		if (margin < 0)
			throw new IllegalArgumentException("Argument must be non-negative!");
		floorMargin = margin;
	}

	public int getFloorMargin() {
		return floorMargin;
	}

	/**
	 * Sets width of the border.<br>
	 * Default: 2
	 */
	public void setBorderWidth(int border) {
		if (border < 0)
			throw new IllegalArgumentException("Argument must be non-negative!");
		borderWidth = border;
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	/**
	 * Sets color of the floor image's body. Can be null for transparent color.<br>
	 * Default: <tt>RGBA[128, 128, 128, 255]</tt>
	 */
	public void setFloorColor(Color c) {
		if (c == null)
			c = transparentColor;
		floorColor = c;
	}

	public Color getFloorColor() {
		return floorColor;
	}

	/**
	 * Sets color of the border. Can be null for transparent color.<br>
	 * Default: <tt>RGBA[0, 0, 0, 255]</tt>
	 */
	public void setBorderColor(Color c) {
		if (c == null)
			c = transparentColor;
		borderColor = c;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	/*
	 * ==============
	 * Actual methods
	 */

	/**
	 * {@link #generateFloorImage(InputStream)}
	 * 
	 * @param layoutFile
	 *            the .txt room layout file
	 * 
	 * @throws FileNotFoundException
	 *             when the file specified in the argument could not be found or accessed
	 * @throws IOException
	 *             when an IOException occurs
	 * @throws IllegalArgumentException
	 *             when the file contains syntax errors
	 */
	public BufferedImage generateFloorImage(File layoutFile) throws
			FileNotFoundException, IllegalArgumentException, IOException {
		if (layoutFile == null)
			throw new IllegalArgumentException("Argument must not be null!");

		InputStream is = null;
		BufferedImage result = null;

		try {
			is = new FileInputStream(layoutFile);
			result = generateFloorImage(is);
		} finally {
			if (is != null)
				is.close();
		}

		return result;
	}

	/**
	 * Attempts to generate a matching floor image for the specified layout.<br>
	 * <br>
	 * The returned image can be later saved using<br>
	 * {@link ImageIO ImageIO.write(image, "PNG", new File("C:/yourImageName.PNG"));}<br>
	 * <br>
	 * <b>This method does NOT close the stream.</b>
	 * 
	 * @param is
	 *            input stream for the file containing the ship's room layout
	 * @return the floor image
	 * 
	 * @throws IOException
	 *             when an IOException occurs
	 * @throws IllegalArgumentException
	 *             when the file contains syntax errors
	 */
	public BufferedImage generateFloorImage(InputStream is) throws
			IOException, IllegalArgumentException {
		if (is == null)
			throw new IllegalArgumentException("Argument must not be null!");

		Layout layout = loadLayout(is);

		final int offset = floorMargin + borderWidth;

		// TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed into integer pixels
		BufferedImage result = new BufferedImage(layout.getWidth() * cellSize + 2 * offset,
				layout.getHeight() * cellSize + 2 * offset, BufferedImage.TYPE_INT_ARGB);

		Graphics2D gc = result.createGraphics();

		// Ensure that the background is transparent
		gc.setBackground(transparentColor);
		gc.clearRect(0, 0, result.getWidth(), result.getHeight());

		// Create rectangles representing each individual tile and store them in a matrix for ease of referencing
		Graph graph = layout.getTileGraph();
		Rectangle[][] tiles = new Rectangle[layout.getWidth()][layout.getHeight()];
		for (Point v : graph) {
			if (graph.hasEdge(v, v.x + 1, v.y) && graph.hasEdge(v, v.x, v.y + 1) && graph.hasEdge(v, v.x + 1, v.y + 1))
				tiles[v.x][v.y] = new Rectangle(offset + v.x * cellSize, offset + v.y * cellSize,
						cellSize, cellSize);
		}

		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[0].length; y++) {
				Rectangle tile = tiles[x][y];

				if (tile != null) {
					// Fill the tile with color
					gc.setColor(floorColor);
					gc.fillRect(tile.x, tile.y, tile.width, tile.height);

					// Draw 'flaps' at the tile's sides, if there are no bordernig tiles
					// Also draw airlock corners

					if (getTile(tiles, x, y - 1) == null) {
						// Doesn't have a neighbouring tile above

						if (layout.hasAirlockAt(x, y, true)) {
							int cx = tile.x;
							int cy = tile.y;
							// If there's a wall, offset to prevent overlap
							if (getTile(tiles, x - 1, y - 1) != null)
								cx += floorMargin;
							// Don't draw if there's a neighbouring airlock, unless on a double dent
							if (layout.isDoubleDent(x, y) || !layout.hasAirlockAt(x - 1, y, true))
								drawTopRightCorner(gc, cx, cy);

							cx = tile.x + tile.width;
							// If there's a wall, offset to prevent overlap
							if (getTile(tiles, x + 1, y - 1) != null)
								cx -= floorMargin;
							// Don't draw if there's a neighbouring airlock, unless on a double dent
							if (layout.isDoubleDent(x + 1, y) || !layout.hasAirlockAt(x + 1, y, true))
								drawTopLeftCorner(gc, cx, cy);
						} else {
							gc.setColor(borderColor);
							// Dents & double dents need to be drawn with slight offsets to prevent overlapping
							if (layout.isDent(x, y) || layout.isDoubleDent(x, y)) {
								gc.fillRect(tile.x + floorMargin, tile.y - floorMargin - borderWidth,
										tile.width - floorMargin, floorMargin + borderWidth);
							} else {
								gc.fillRect(tile.x, tile.y - floorMargin - borderWidth,
										tile.width, floorMargin + borderWidth);
							}

							gc.setColor(floorColor);
							gc.fillRect(tile.x, tile.y - floorMargin, tile.width, floorMargin);
						}
					}
					if (getTile(tiles, x - 1, y) == null) {
						// Doesn't have a neighbouring tile to the left

						if (layout.hasAirlockAt(x, y, false)) {
							int cx = tile.x;
							int cy = tile.y;

							// Two airlocks at a dent get drawn correctly, but leave empty space between their
							// corners. Fill that area with color
							if (layout.hasAirlockAt(x - 1, y, true)) {
								gc.setColor(floorColor);
								gc.fillRect(cx - floorMargin, cy, floorMargin, floorMargin);
							}
							if (layout.hasAirlockAt(x - 1, y + 1, true)) {
								gc.setColor(floorColor);
								gc.fillRect(cx - floorMargin, cy + tile.height - floorMargin, floorMargin, floorMargin);
							}

							// If there's a wall, offset to prevent overlap
							if (getTile(tiles, x - 1, y - 1) != null)
								cy += floorMargin;
							// Don't draw if there's a neighbouring airlock, unless on a double denet
							if (layout.isDoubleDent(x, y) || !layout.hasAirlockAt(x, y - 1, false))
								drawBottomLeftCorner(gc, cx, cy);

							cy = tile.y + tile.height;
							// If there's a wall, offset to prevent overlap
							if (getTile(tiles, x - 1, y + 1) != null)
								cy -= floorMargin;
							// Don't draw if there's a neighbouring airlock, unless on a double dent
							if (layout.isDoubleDent(x, y + 1) || !layout.hasAirlockAt(x, y + 1, false))
								drawTopLeftCorner(gc, cx, cy);
						} else {
							boolean isDent = layout.isDent(x, y) || layout.isDoubleDent(x, y);
							boolean isLowerDent = layout.isDent(x, y + 1) || layout.isDoubleDent(x, y + 1);

							// Dents & double dents need to be drawn with slight offsets to prevent overlapping
							gc.setColor(borderColor);
							if (isDent && isLowerDent) {
								gc.fillRect(tile.x - floorMargin - borderWidth, tile.y + floorMargin,
										floorMargin + borderWidth, tile.height - floorMargin * 2);
							} else if (isDent) {
								gc.fillRect(tile.x - floorMargin - borderWidth, tile.y + floorMargin,
										floorMargin + borderWidth, tile.height - floorMargin);
							} else if (isLowerDent) {
								gc.fillRect(tile.x - floorMargin - borderWidth, tile.y,
										floorMargin + borderWidth, tile.height - floorMargin);
							} else {
								gc.fillRect(tile.x - floorMargin - borderWidth, tile.y,
										floorMargin + borderWidth, tile.height);
							}

							gc.setColor(floorColor);
							gc.fillRect(tile.x - floorMargin, tile.y, floorMargin, tile.height);
						}
					}
					if (getTile(tiles, x, y + 1) == null) {
						// Doesn't have a neighbouring tile below

						if (layout.hasAirlockAt(x, y + 1, true)) {
							int cx = tile.x;
							int cy = tile.y + tile.height;
							// If there's a wall, offset to prevent overlap
							if (getTile(tiles, x - 1, y + 1) != null)
								cx += floorMargin;
							// Don't draw if there's a neighbouring airlock, unless on a double dent
							if (layout.isDoubleDent(x, y + 1) || !layout.hasAirlockAt(x - 1, y + 1, true))
								drawBottomRightCorner(gc, cx, cy);

							cx = tile.x + tile.width;
							// If there's a wall, offset to prevent overlap
							if (getTile(tiles, x + 1, y + 1) != null)
								cx -= floorMargin;
							// Don't draw if there's a neighbouring airlock, unless on a double dent
							if (layout.isDoubleDent(x + 1, y + 1) || !layout.hasAirlockAt(x + 1, y + 1, true))
								drawBottomLeftCorner(gc, cx, cy);

						} else {
							gc.setColor(borderColor);
							// Dents & double dents need to be drawn with slight offsets to prevent overlapping
							if (layout.isDent(x, y + 1) || layout.isDoubleDent(x, y + 1)) {
								gc.fillRect(tile.x + floorMargin, tile.y + tile.height, tile.width - floorMargin,
										floorMargin + borderWidth);
							} else {
								gc.fillRect(tile.x, tile.y + tile.height, tile.width,
										floorMargin + borderWidth);
							}

							gc.setColor(floorColor);
							gc.fillRect(tile.x, tile.y + tile.height, tile.width, floorMargin);
						}
					}
					if (getTile(tiles, x + 1, y) == null) {
						// Doesn't have a neighbouring tile to the right

						if (layout.hasAirlockAt(x + 1, y, false)) {
							int cx = tile.x + tile.width;
							int cy = tile.y;

							// Two airlocks at a dent get drawn correctly, but leave empty space between their
							// corners. Fill that area with color
							if (layout.hasAirlockAt(x + 1, y, true)) {
								gc.setColor(floorColor);
								gc.fillRect(cx, cy, floorMargin, floorMargin);
							}
							if (layout.hasAirlockAt(x + 1, y + 1, true)) {
								gc.setColor(floorColor);
								gc.fillRect(cx, cy + tile.height - floorMargin, floorMargin, floorMargin);
							}

							// If there's a wall, offset to prevent overlap
							if (getTile(tiles, x + 1, y - 1) != null)
								cy += floorMargin;
							// Don't draw if there's a neighbouring airlock, unless on a double dent
							if (layout.isDoubleDent(x + 1, y) || !layout.hasAirlockAt(x + 1, y - 1, false))
								drawBottomRightCorner(gc, cx, cy);

							cy = tile.y + tile.height;
							// If there's a wall, offset to prevent overlap
							if (getTile(tiles, x + 1, y + 1) != null)
								cy -= floorMargin;
							// Don't draw if there's a neighbouring airlock, unless on a double dent
							if (layout.isDoubleDent(x + 1, y + 1) || !layout.hasAirlockAt(x + 1, y + 1, false))
								drawTopRightCorner(gc, cx, cy);
						} else {
							gc.setColor(borderColor);
							// Overlaps caused by dents & double dents drawn here are obscured by other tiles
							gc.fillRect(tile.x + tile.width, tile.y,
									floorMargin + borderWidth, tile.height);

							gc.setColor(floorColor);
							gc.fillRect(tile.x + tile.width, tile.y, floorMargin, tile.height);
						}
					}

					// Draw corners
					if (layout.isCorner(x, y) && graph.hasEdge(x, y, x + 1, y + 1))
						drawTopLeftCorner(gc, tile.x, tile.y);
					if (layout.isCorner(x + 1, y) && graph.hasEdge(x + 1, y, x, y + 1))
						drawTopRightCorner(gc, tile.x + tile.width, tile.y);
					if (layout.isCorner(x, y + 1) && graph.hasEdge(x, y + 1, x + 1, y))
						drawBottomLeftCorner(gc, tile.x, tile.y + tile.height);
					if (layout.isCorner(x + 1, y + 1) && graph.hasEdge(x + 1, y + 1, x, y))
						drawBottomRightCorner(gc, tile.x + tile.width, tile.y + tile.height);
				}
			}
		}

		return result;
	}

	private void drawTopLeftCorner(Graphics2D gc, int cx, int cy) {
		gc.setColor(borderColor);
		gc.fillPolygon(new int[] { cx, cx - floorMargin - borderWidth - 1, cx },
				new int[] { cy, cy, cy - floorMargin - borderWidth - 1 }, 3);
		gc.setColor(floorColor);
		gc.fillPolygon(new int[] { cx, cx - floorMargin, cx },
				new int[] { cy, cy, cy - floorMargin }, 3);
	}

	private void drawTopRightCorner(Graphics2D gc, int cx, int cy) {
		gc.setColor(borderColor);
		gc.fillPolygon(new int[] { cx, cx, cx + floorMargin + borderWidth },
				new int[] { cy, cy - floorMargin - borderWidth - 1, cy }, 3);
		gc.setColor(floorColor);
		gc.fillPolygon(new int[] { cx, cx, cx + floorMargin - 1 },
				new int[] { cy, cy - floorMargin, cy }, 3);
	}

	private void drawBottomLeftCorner(Graphics2D gc, int cx, int cy) {
		gc.setColor(borderColor);
		gc.fillPolygon(new int[] { cx, cx, cx - floorMargin - borderWidth - 1 },
				new int[] { cy, cy + floorMargin + borderWidth, cy }, 3);
		gc.setColor(floorColor);
		gc.fillPolygon(new int[] { cx, cx, cx - floorMargin },
				new int[] { cy, cy + floorMargin - 1, cy }, 3);
	}

	private void drawBottomRightCorner(Graphics2D gc, int cx, int cy) {
		gc.setColor(borderColor);
		gc.fillPolygon(new int[] { cx, cx, cx + floorMargin + borderWidth },
				new int[] { cy, cy + floorMargin + borderWidth, cy }, 3);
		gc.setColor(floorColor);
		gc.fillPolygon(new int[] { cx, cx, cx + floorMargin - 1 },
				new int[] { cy, cy + floorMargin - 1, cy }, 3);
	}

	private Layout loadLayout(InputStream is) throws FileNotFoundException, IllegalArgumentException {
		List<Rectangle> rooms = new ArrayList<Rectangle>();
		List<Door> airlocks = new ArrayList<Door>();

		Scanner sc = new Scanner(is);

		while (sc.hasNext()) {
			String line = sc.nextLine();

			if (line.trim().equals(""))
				continue;

			LayoutObjects layoutObject = null;
			try {
				layoutObject = LayoutObjects.valueOf(line);
			} catch (IllegalArgumentException e) {
				try {
					// Not a layout object -- has to be integer value
					// Check if it is, in fact, a number to verify syntax
					Integer.parseInt(line);
					continue;
				} catch (NumberFormatException ex) {
					// Not a layout object or integer value - syntax error
					// Intercept the exception to throw a more meaningful one...
					throw new IllegalArgumentException("TXT layout syntax error on line: \n" + line);
				}
			}

			switch (layoutObject) {
				case X_OFFSET:
				case Y_OFFSET:
				case HORIZONTAL:
				case VERTICAL:
				case ELLIPSE:
					// Ignore
					break;
				case ROOM:
					// Id
					sc.nextInt();
					// x, y, w, h
					rooms.add(new Rectangle(sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.nextInt()));
					break;
				case DOOR:
					// x, y
					Point p = new Point(sc.nextInt(), sc.nextInt());
					// Left Id
					int l = sc.nextInt();
					// Right Id
					int r = sc.nextInt();

					// If either ID is -1, then the door is an airlock
					if (l == -1 || r == -1) {
						// Horizontal if 0, vertical if 1
						airlocks.add(new Door(p.x, p.y, sc.nextInt() == 0));
					}
					break;
			}
		}

		return new Layout(rooms, airlocks);
	}

	private Rectangle getTile(Rectangle[][] matrix, int x, int y) {
		if (x < 0 || x >= matrix.length || y < 0 || y >= matrix[x].length)
			return null;
		return matrix[x][y];
	}
}
