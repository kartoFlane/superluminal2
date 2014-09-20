package com.kartoflane.superluminal2.utils;

import java.util.Arrays;
import java.util.Scanner;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

import com.kartoflane.superluminal2.components.Polygon;
import com.kartoflane.superluminal2.components.enums.OS;
import com.kartoflane.superluminal2.components.interfaces.Identifiable;
import com.kartoflane.superluminal2.components.interfaces.Indexable;

/**
 * This class contains various utility methods that didn't fit in the other util classes.
 * 
 * @author kartoFlane
 * 
 */
public class Utils {

	public static int distance(Point p1, Point p2) {
		return (int) Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
	}

	public static int distance(int x1, int y1, int x2, int y2) {
		return (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}

	public static Point center(Point p1, Point p2) {
		return center(p1.x, p1.y, p2.x, p2.y);
	}

	public static Point center(int x1, int y1, int x2, int y2) {
		return new Point((x1 + x2) / 2, (y1 + y2) / 2);
	}

	public static Point add(Point p1, Point p2) {
		return new Point(p1.x + p2.x, p1.y + p2.y);
	}

	public static Rectangle copy(Rectangle r) {
		return new Rectangle(r.x, r.y, r.width, r.height);
	}

	public static Point copy(Point p) {
		return new Point(p.x, p.y);
	}

	public static RGB copy(RGB rgb) {
		return new RGB(rgb.red, rgb.green, rgb.blue);
	}

	public static int min(int a, int b, int c) {
		return Math.min(a, Math.min(b, c));
	}

	public static int max(int a, int b, int c) {
		return Math.max(a, Math.max(b, c));
	}

	public static int limit(int min, int number, int max) {
		return Math.max(min, Math.min(number, max));
	}

	public static int sign(int x) {
		return x == 0 ? 0 : x / Math.abs(x);
	}

	/**
	 * Values returned by {@link #angle(Point, Point)} sometimes need to be filtered through this
	 * method in order to work correctly with regular Math functions...
	 * 
	 * @param angle
	 *            angle, in degrees
	 */
	public static int convertAngle(int angle) {
		return angle * (-1) + 270;
	}

	/**
	 * @see #convertAngle(int)
	 */
	public static double convertAngle(double angle) {
		return angle * (-1) + 270;
	}

	/**
	 * @return random number in the specified range, both ends inclusive
	 */
	public static int random(int min, int max) {
		return (int) Math.round(random((double) min, max));
	}

	/**
	 * @see #random(int, int)
	 */
	public static double random(double min, double max) {
		return min + Math.random() * (max - min);
	}

	/**
	 * Computes angle between the two points, in degrees.<br>
	 * 0 means north, increases counter-clockwise.
	 */
	public static double angle(Point p1, Point p2) {
		double theta = Math.atan2(p1.x - p2.x, p1.y - p2.y);
		// theta += Math.PI / 2.0;
		double angle = Math.toDegrees(theta);
		if (angle < 0) {
			angle += 360;
		}
		return angle % 360;
	}

	/**
	 * A = [0,1] = (x, y)<br>
	 * B = [2,3] = (x + w, y)<br>
	 * C = [4,5] = (x, y + h)<br>
	 * D = [6,7] = (x + w, y + h)<br>
	 * <br>
	 * The result is a Z-like shape:
	 * 
	 * <pre>
	 * A--B
	 *   /
	 *  /
	 * C--D
	 * </pre>
	 * 
	 * @return int array of length 8, containing alternating x and y coordinates,
	 *         describing the corners of the given rectangle.
	 */
	public static int[] toArray(Rectangle rect) {
		return new int[] {
				rect.x, rect.y,
				rect.x + rect.width, rect.y,
				rect.x, rect.y + rect.height,
				rect.x + rect.width, rect.y + rect.height
		};
	}

	/**
	 * The same as {@link #toArray(Rectangle)}, but 4th and 6th indices are swapped.<br>
	 * The result is a rectangle-like shape:
	 * 
	 * <pre>
	 * A--B
	 *    |
	 * D--C
	 * </pre>
	 * 
	 */
	public static int[] toArrayPolygon(Rectangle rect) {
		return new int[] {
				rect.x, rect.y,
				rect.x + rect.width, rect.y,
				rect.x + rect.width, rect.y + rect.height,
				rect.x, rect.y + rect.height
		};
	}

	/**
	 * Rotates the rectangle around its center by the given angle.
	 * 
	 * @param b
	 *            the rectangle to be rotated
	 * @param rotation
	 *            angle in degrees
	 * @return the rotated rectangle
	 */
	public static Rectangle rotate(Rectangle r, float rotation) {
		Rectangle b = copy(r);
		if (rotation % 180 == 0) {
			// no need to do anything
		} else if ((int) (rotation % 90) == 0) {
			b.x += b.width / 2 - b.height / 2;
			b.y += b.height / 2 - b.width / 2;
			int a = b.width;
			b.width = b.height;
			b.height = a;
		} else {
			Polygon p = new Polygon(toArrayPolygon(b));
			p.rotate((float) Math.toRadians(rotation));
			b = p.getBounds();
		}
		return b;
	}

	/**
	 * 
	 * @param point
	 *            the point to be rotated
	 * @param the
	 *            point around which the point will be rotated
	 * @param rad
	 *            angle in radians
	 * @return the rotated point
	 */
	public static Point rotate(Point point, int cx, int cy, float rad) {
		Point p = copy(point);
		p.x = (int) (Math.cos(rad) * (point.x - cx) - Math.sin(rad) * (point.y - cy) + cx);
		p.y = (int) (Math.sin(rad) * (point.x - cx) + Math.cos(rad) * (point.y - cy) + cy);
		return p;
	}

	public static Point rotate(Point p, Point c, float rad) {
		return rotate(p, c.x, c.y, rad);
	}

	public static Point polar(Point origin, double rad, int distance) {
		Point result = new Point(0, 0);
		result.x = origin.x + (int) Math.round(Math.cos(rad) * distance);
		result.y = origin.y + (int) Math.round(Math.sin(rad) * distance);
		return result;
	}

	/**
	 * Fixes the rectangle, so that width and height are always positive, but the
	 * resulting rectangle covers the same area.
	 * 
	 * @param r
	 *            the rectangle to be fixed (remains unchanged)
	 * @return the fixed rectangle (new instance)
	 */
	public static Rectangle fix(Rectangle r) {
		return new Rectangle(Math.min(r.x, r.x + r.width),
				Math.min(r.y, r.y + r.height),
				Math.abs(r.width), Math.abs(r.height));
	}

	/**
	 * Tests whether a rectangle contains another.
	 * 
	 * @param rect
	 *            the "larger" rectangle (that supposedly contains the "smaller" one)
	 * @param other
	 *            the "smaller" rectangle (that supposedly is contained within the "larger" one)
	 * @return true if the first rectangle contains the second, false otherwise.
	 */
	public static boolean contains(Rectangle rect, Rectangle other) {
		return rect.contains(other.x, other.y) && rect.contains(other.x + other.width, other.y + other.height);
	}

	public static int binarySearch(Identifiable[] array, String identifier, int min, int max) {
		if (min > max)
			return -1;
		int mid = (min + max) / 2;
		int result = identifier.compareTo(array[mid].getIdentifier());
		if (result > 0)
			return binarySearch(array, identifier, mid + 1, max);
		else if (result < 0)
			return binarySearch(array, identifier, min, mid - 1);
		else
			return mid;
	}

	/**
	 * Overlays the base RGB with the overlay RGB with the given opacity.<br>
	 * It doesn't work terribly well with RGB (which is an additive color model),
	 * but it's a good-enough approximation.
	 * 
	 * @param base
	 *            the base color
	 * @param overlay
	 *            the color that will be laid over base
	 * @param alpha
	 *            the opacity of the overlay color. Only values from range 0.0 - 1.0.
	 * @return the resulting color
	 */
	public static RGB tint(RGB base, RGB overlay, double alpha) {
		if (alpha < 0 || alpha > 1)
			throw new IllegalArgumentException("Alpha values must be within 0-1 range.");
		RGB tinted = new RGB(base.red, base.green, base.blue);
		tinted.red = (int) (((1 - alpha) * tinted.red + alpha * overlay.red));
		tinted.green = (int) (((1 - alpha) * tinted.green + alpha * overlay.green));
		tinted.blue = (int) (((1 - alpha) * tinted.blue + alpha * overlay.blue));

		return tinted;
	}

	/**
	 * For systems specified in the argument, wraps the string using the given settings.
	 * For all others, just returns the same string (under assumption that they wrap the text themselves)
	 * 
	 * @see #wrapOSNot(String, int, int, OS...)
	 * @see #wrap(String, int, int)
	 */
	public static String wrapOS(String msg, int wrapWidth, int wrapTolerance, OS... wrapFor) {
		OS os = OS.identifyOS();

		if (Arrays.asList(wrapFor).contains(os)) {
			return wrap(msg, wrapWidth, wrapTolerance);
		} else {
			return msg;
		}
	}

	/**
	 * Exactly the same as {@link #wrapOS(String, int, int, OS...)}, except with inverted logic,
	 * ie. for systems specified in the argument, the method <b>won't</b> wrap the string, but
	 * will for all others.
	 * 
	 * @see #wrapOS(String, int, int, OS...)
	 * @see #wrap(String, int, int)
	 */
	public static String wrapOSNot(String msg, int wrapWidth, int wrapTolerance, OS... dontWrapFor) {
		OS os = OS.identifyOS();

		if (Arrays.asList(dontWrapFor).contains(os)) {
			return msg;
		} else {
			return wrap(msg, wrapWidth, wrapTolerance);
		}
	}

	/**
	 * Attempts to wrap the supplied string with the given width.
	 * 
	 * @param msg
	 *            the message to be wrapped
	 * @param wrapWidth
	 *            the maximum width of a single row of text.
	 *            Defaults to 50 if negative number or 0.
	 * @param wrapTolerance
	 *            how many characters over the limit words can go.
	 *            Defaults to 5 if negative number.
	 * @return wrapped string
	 * 
	 * @see #wrapOS(String, int, int, OS...)
	 * @see #wrapOSNot(String, int, int, OS...)
	 */
	public static String wrap(String msg, int wrapWidth, int wrapTolerance) {
		if (msg == null)
			throw new IllegalArgumentException("Argument must not be null.");
		if (wrapWidth <= 0)
			wrapWidth = 50;
		if (wrapTolerance < 0)
			wrapTolerance = 5;
		StringBuilder buf = new StringBuilder();
		Scanner sc = new Scanner(msg);

		int currentWidth = 0;
		while (sc.hasNext()) {
			String token = sc.next();
			int newWidth = currentWidth + token.length();
			if (currentWidth >= wrapWidth) {
				// Even without the new token it's already outside -- move to new line
				buf.append("\n");
				buf.append(token);
				currentWidth = token.length();
			} else if (newWidth > wrapWidth + wrapTolerance) {
				// Falls outside of the allowed wrap width -- split
				String[] split = split(token, wrapWidth - currentWidth - 1);
				if (!split[0].isEmpty()) {
					buf.append(" ");
					buf.append(split[0]);
				}
				buf.append(split[0].isEmpty() || split[1].isEmpty() ? "\n" : "-\n");
				buf.append(split[1]);
				currentWidth = split[1].length();
			} else {
				// Fits within the wrap width -- append to current line
				if (currentWidth > 0)
					buf.append(" ");
				buf.append(token);
				currentWidth += token.length() + 1;
			}
		}

		return buf.toString();
	}

	private static String[] split(String word, int splitIndex) {
		String[] result = new String[2];
		if (splitIndex == 0) {
			result[0] = "";
			result[1] = word;
			return result;
		} else if (splitIndex == word.length()) {
			result[0] = word;
			result[1] = "";
			return result;
		}

		int i = findIndexOfClosestVowel(word, splitIndex);

		if (i < 0) {
			result[0] = word;
			result[1] = "";
		} else {
			result[0] = word.substring(0, i);
			result[1] = word.substring(i);
		}

		return result;
	}

	private static int findIndexOfClosestVowel(String word, int splitIndex) {
		char[] vowels = { 'a', 'e', 'i', 'o', 'u', 'y' };
		int result = -word.length();
		for (char vowel : vowels) {
			int indexFrom = word.indexOf(vowel, splitIndex);
			int indexTo = word.substring(0, splitIndex - 1).indexOf(vowel);
			// Determine which one is closer
			int candidate = Math.abs(splitIndex - indexFrom) < Math.abs(splitIndex - indexTo) ?
					indexFrom :
					indexTo;
			result = Math.abs(splitIndex - result) < Math.abs(splitIndex - candidate) ?
					result :
					candidate;
		}
		return result;
	}

	public static boolean contains(Object[] array, Object object) {
		for (Object o : array)
			if (o.equals(object))
				return true;
		return false;
	}

	public static int indexOf(Object[] array, Object object) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(object))
				return i;
		}
		return -1;
	}

	/**
	 * Reorders elements of the Indexable array, updating their IDs.<br>
	 * ID of an indexable elements does not necessarily correspond to its index in the array.
	 * 
	 * @param array
	 *            an array of Indexable objects
	 * @param from
	 *            index of the element that is to be moved
	 * @param to
	 *            destination index
	 */
	public static void reorder(Indexable[] array, int from, int to) {
		if (from < 0 || from >= array.length)
			throw new IndexOutOfBoundsException("" + from);
		if (to < 0 || to >= array.length)
			throw new IndexOutOfBoundsException("" + to);
		if (to == from)
			return;

		int dir = from > to ? 1 : -1;
		int oldId = array[to].getId();
		/*
		 * Update IDs of elements between the two indices
		 * 
		 * The loop needs to go on as long as
		 * (from > to && i < from) || (from < to && i > from) is true
		 * Let's say A = (from > to); B = (i < from), then the above becomes:
		 * (A && B) || (!A && !B)
		 * Hence the simplified condition is A == B
		 */
		for (int i = to; from > to == i < from; i += dir)
			array[i].setId(array[Math.max(0, i + dir)].getId());
		array[from].setId(oldId);
	}
}
