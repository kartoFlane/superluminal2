package com.kartoflane.superluminal2.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * A Cache class that only creates a requested object once, and then
 * redistribute the reference to that object to clients.
 * 
 * @author Vhati
 * @author kartoFlane
 * 
 */
public class Cache {
	private static final Logger log = LogManager.getLogger(Cache.class);

	private static HashMap<String, Image> cachedImageMap = new HashMap<String, Image>();
	private static HashMap<RGB, Color> cachedColorMap = new HashMap<RGB, Color>();
	private static HashMap<String, ArrayList<Object>> imageCustomerMap = new HashMap<String, ArrayList<Object>>();
	private static HashMap<RGB, ArrayList<Object>> colorCustomerMap = new HashMap<RGB, ArrayList<Object>>();

	/**
	 * Request an Image handle for the given path.<br>
	 * <br>
	 * All paths must have a protocol decraled at their beginning, like so:
	 * 
	 * <pre>
	 * <tt>file:C://example/absolute/path.txt</tt>
	 * </pre>
	 * 
	 * If a path is missing its protocol, or it is mistyped, the image will not be loaded.<br>
	 * Protocols that this method recognizes:
	 * 
	 * <pre>
	 * <tt>file:    - for use when the resource is located in the OS' filesystem,
	 *              eg. an absolute or relative path
	 * cpath:   - for use when the resource is located inside the jar
	 *              eg. cpath:/assets/image.png
	 * rdat:    - for use when the resource is located inside resource.dat
	 *              eg. rdat:img/ship/kestral_base.png</tt>
	 * </pre>
	 * 
	 * @param customer
	 *            the object that is checking out the image
	 * @param path
	 *            path to the requested resource, beginning with a protocol
	 */
	public static Image checkOutImage(Object customer, String path) {
		Image image = null;
		ArrayList<Object> customers = imageCustomerMap.get(path);

		// create a new list of customers if there isn't any
		if (customers == null) {
			customers = new ArrayList<Object>();
			imageCustomerMap.put(path, customers);
		}

		if (customer == null)
			throw new NullPointerException("Customer is null.");

		// check whether the image has already been cached
		image = cachedImageMap.get(path);
		if (image != null && image.isDisposed()) {
			cachedImageMap.remove(path);
			customers.clear();
			image = null;
		}

		if (path == null) {
			throw new NullPointerException("Path is null.");
		} else {
			String loadPath = null;
			String protocol = null;
			try {
				if (image == null) {
					InputStream is = null;
					loadPath = Utils.trimProtocol(path);
					protocol = Utils.getProtocol(path);

					// Employ "protocols" to spare the Cache from having to guess where the file is located
					if (protocol.equals("rdat:")) {
						// refers to file in resource.dat
						is = Database.getInstance().getResourceDat().getInputStream(loadPath);
					} else if (protocol.equals("cpath:")) {
						// refers to file in classpath
						is = customer.getClass().getResourceAsStream(loadPath);
					} else if (protocol.equals("file:")) {
						// refers to file in OS' filesystem
						is = new FileInputStream(new File(loadPath));
					} else {
						throw new IllegalArgumentException("Path uses unknown protocol, or doesn't have it:\n" + path);
					}

					image = new Image(Display.getCurrent(), is);
					cachedImageMap.put(path, image);
				}

				customers.add(customer);
			} catch (SWTException e) {
				log.warn(String.format("%s - resource contains invalid data.", loadPath));
			} catch (IllegalArgumentException e) {
				log.warn("", e);
			} catch (FileNotFoundException e) {
				log.warn(String.format("%s - resource could not be found.", loadPath));
			} catch (IOException e) {
				log.error("An error has occured while loading image: ", e);
			}
		}

		return image;
	}

	/**
	 * Signal the Cache that the customer is done using the image.
	 */
	public static void checkInImage(Object customer, String path) {
		ArrayList<Object> customers = imageCustomerMap.get(path);

		if (customers != null && customers.size() > 0) {
			Iterator<Object> it = customers.iterator();
			while (it.hasNext()) {
				if (it.next() == customer) {
					it.remove();
					break;
				}
			}
		}

		// no one is using the resource anymore
		if (customers == null || customers.size() == 0) {
			Image image = cachedImageMap.get(path);
			if (image != null && !image.isDisposed())
				image.dispose();
			cachedImageMap.remove(path);
		}
	}

	/**
	 * Request a Color handle for the given RGB.
	 */
	public static Color checkOutColor(Object customer, RGB rgb) {
		Color color = null;
		ArrayList<Object> customers = colorCustomerMap.get(rgb);

		// create a new list of customers if there isn't any
		if (customers == null) {
			customers = new ArrayList<Object>();
			colorCustomerMap.put(rgb, customers);
		}

		if (customer == null)
			throw new NullPointerException("Customer is null.");

		// check whether the image has already been cached
		color = cachedColorMap.get(rgb);
		if (color != null && color.isDisposed()) {
			cachedColorMap.remove(rgb);
			customers.clear();
			color = null;
		}

		if (rgb == null) {
			throw new NullPointerException("RGB is null.");
		} else {
			if (color == null) {
				color = new Color(Display.getCurrent(), rgb);
				cachedColorMap.put(rgb, color);
			}

			customers.add(customer);
		}

		return color;
	}

	/**
	 * Signal the Cache that the customer is done using the color.
	 */
	public static void checkInColor(Object customer, RGB rgb) {
		ArrayList<Object> customers = colorCustomerMap.get(rgb);

		if (customers != null && customers.size() > 0) {
			Iterator<Object> it = customers.iterator();
			while (it.hasNext()) {
				if (it.next() == customer) {
					it.remove();
					break;
				}
			}
		}

		// no one is using the resource anymore
		if (customers == null || customers.size() == 0) {
			Color color = cachedColorMap.get(rgb);
			if (color != null && !color.isDisposed())
				color.dispose();
			cachedColorMap.remove(rgb);
		}
	}

	/** Flush the Cache of any stored Image references, disposing them in the process. */
	public static void disposeImages() {
		for (Map.Entry<String, Image> entry : cachedImageMap.entrySet()) {
			if (entry.getValue() != null && !entry.getValue().isDisposed())
				entry.getValue().dispose();
		}
		cachedImageMap.clear();
		imageCustomerMap.clear();
	}

	/** Flush the Cache of any stored Color references, disposing them in the process. */
	public static void disposeColors() {
		for (Map.Entry<RGB, Color> entry : cachedColorMap.entrySet()) {
			if (entry.getValue() != null && !entry.getValue().isDisposed())
				entry.getValue().dispose();
		}
		cachedColorMap.clear();
		colorCustomerMap.clear();
	}

	/** Flush the Cache of any stored resources, disposing them in the proces. */
	public static void dispose() {
		disposeImages();
		disposeColors();
	}

}
