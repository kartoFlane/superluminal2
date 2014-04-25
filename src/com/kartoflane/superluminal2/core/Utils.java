package com.kartoflane.superluminal2.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.vhati.modmanager.core.FTLUtilities;
import net.vhati.modmanager.core.SloppyXMLOutputProcessor;
import net.vhati.modmanager.core.SloppyXMLParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.jdom2.Document;
import org.jdom2.input.JDOMParseException;

import com.kartoflane.superluminal2.Superluminal;

public class Utils {
	public static final Logger log = LogManager.getLogger(Utils.class);

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

	/**
	 * Corrects the bounds by including rotation. This way bounds
	 * cover the entire area of the controller, and can be reliably
	 * used to redraw it.
	 * 
	 * @param b
	 *            the rectangle representing the controller's bounds
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
		} else if (rotation % 45 == 0) {
			// TODO ?
		} else {
			// TODO perform sine/cosine calculations, or approximations of those...
			// end.x = (int) (start.x + Math.cos(rad) * distance - Math.sin(rad) * distance);
			// end.y = (int) (start.y + Math.sin(rad) * distance + Math.cos(rad) * distance);
		}
		return b;
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

	public static boolean contains(Rectangle rect, Rectangle other) {
		return rect.contains(other.x, other.y) && rect.contains(other.x + other.width, other.y + other.height);
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

	public static String readFile(File f) throws FileNotFoundException, IOException {
		DecodeResult dr = decodeText(new FileInputStream(f), f.getName());
		return dr.text;
	}

	public static Document readFileXML(File f) throws FileNotFoundException, IOException, JDOMParseException {
		String contents = readFile(f);
		return parseXML(contents);
	}

	public static String readStream(InputStream is, String label) throws IOException {
		DecodeResult dr = decodeText(is, label);
		return dr.text;
	}

	public static Document readStreamXML(InputStream is, String label) throws IOException, JDOMParseException {
		String contents = readStream(is, label);
		return parseXML(contents);
	}

	public static Document parseXML(String contents) throws JDOMParseException {
		if (contents == null)
			throw new IllegalArgumentException("Parsed string must not be null.");

		SloppyXMLParser parser = new SloppyXMLParser();

		return parser.build(contents);
	}

	/**
	 * Writes the Document to the file in XML format.<br>
	 * This method uses the {@link SloppyXMLOutputProcessor}, which
	 * omitts the root element when writing the document.
	 * 
	 * @param doc
	 *            the document to be written
	 * @param f
	 *            file in which the document will be saved
	 * @return true if operation was completed successfully, false otherwise
	 */
	public static boolean writeFileXML(Document doc, File f) throws IOException {
		if (doc == null)
			throw new IllegalArgumentException("Document must not be null.");
		if (f == null)
			throw new IllegalArgumentException("File must not be null.");
		if (f.isDirectory())
			throw new IllegalArgumentException("File must not be a directory.");

		FileWriter writer = null;

		try {
			f.createNewFile();
			writer = new FileWriter(f);
			SloppyXMLOutputProcessor.sloppyPrint(doc, writer, null);

			return true;
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	public static String trimProtocol(String input) {
		if (input.startsWith("rdat:") || input.startsWith("file:"))
			return input.substring(5);
		else if (input.startsWith("cpath:"))
			return input.substring(6);
		else
			return input;
	}

	public static String getProtocol(String input) {
		if (input.startsWith("rdat:") || input.startsWith("file:"))
			return input.substring(0, 5);
		else if (input.startsWith("cpath:"))
			return input.substring(0, 6);
		else
			return "";
	}

	public static void showErrorDialog(Shell parentShell, String message) {
		boolean dispose = false;

		if (parentShell == null) {
			parentShell = new Shell(Display.getCurrent());
			dispose = true;
		}

		MessageBox box = new MessageBox(parentShell, SWT.ICON_ERROR | SWT.OK);

		box.setText(String.format("%s - Error", Superluminal.APP_NAME));
		box.setMessage(message);

		box.open();

		if (dispose)
			parentShell.dispose();
	}

	public static void showWarningDialog(Shell parentShell, String message) {
		boolean dispose = false;

		if (parentShell == null) {
			parentShell = new Shell(Display.getCurrent());
			dispose = true;
		}

		MessageBox box = new MessageBox(parentShell, SWT.ICON_WARNING | SWT.OK);

		box.setText(String.format("%s - Warning", Superluminal.APP_NAME));
		box.setMessage(message);

		box.open();

		if (dispose)
			parentShell.dispose();
	}

	/**
	 * Modally prompts the user for the FTL resources dir.
	 * 
	 * @param parentShell
	 *            parent for the SWT dialog
	 * 
	 * @author Vhati - original method wth Swing dialogs
	 * @author kartoFlane - modified to work with SWT dialogs
	 */
	public static File promptForDatsDir(Shell parentShell) {
		File result = null;

		String message = "";
		message += "You will now be prompted to locate FTL manually.\n";
		message += "Select '(FTL dir)/resources/data.dat'.\n";
		message += "Or 'FTL.app', if you're on OSX.";

		MessageBox box = new MessageBox(parentShell, SWT.ICON_INFORMATION | SWT.OK);
		box.setText("Find FTL");
		box.setMessage(message);

		FileDialog fd = new FileDialog(parentShell, SWT.OPEN);
		fd.setText("Find data.dat or FTL.app");
		fd.setFilterExtensions(new String[] { "*.dat", "*.app" });
		fd.setFilterNames(new String[] { "FTL Data File - (FTL dir)/resources/data.dat", "FTL Application Bundle" });

		String filePath = fd.open();

		if (filePath == null) {
			// User aborted selection
			// Nothing to do here
		} else {
			File f = new File(filePath);
			if (f.getName().equals("data.dat")) {
				result = f.getParentFile();
			} else if (f.getName().endsWith(".app")) {
				File contentsPath = new File(f, "Contents");
				if (contentsPath.exists() && contentsPath.isDirectory() && new File(contentsPath, "Resources").exists())
					result = new File(contentsPath, "Resources");
				// TODO test whether this works on OSX
			}
		}

		if (result != null && FTLUtilities.isDatsDirValid(result)) {
			return result;
		}

		return null;
	}

	/**
	 * Encodes a string (throwing an exception on bad chars) to bytes in a stream.
	 * Line endings will not be normalized.
	 * 
	 * @param text
	 *            a String to encode
	 * @param encoding
	 *            the name of a Charset
	 * @param description
	 *            how error messages should refer to the string, or null
	 * 
	 * @author Vhati
	 */
	public static InputStream encodeText(String text, String encoding, String description) throws IOException {
		CharsetEncoder encoder = Charset.forName(encoding).newEncoder();

		ByteArrayOutputStream tmpData = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(tmpData, encoder);
		writer.write(text);
		writer.flush();

		InputStream result = new ByteArrayInputStream(tmpData.toByteArray());
		return result;
	}

	/**
	 * Determines text encoding for an InputStream and decodes its bytes as a string.
	 * 
	 * CR and CR-LF line endings will be normalized to LF.
	 * 
	 * @param is
	 *            a stream to read
	 * @param description
	 *            how error messages should refer to the stream, or null
	 * 
	 * @author Vhati
	 */
	public static DecodeResult decodeText(InputStream is, String description) throws IOException {
		String result = null;

		byte[] buf = new byte[4096];
		int len;
		ByteArrayOutputStream tmpData = new ByteArrayOutputStream();
		while ((len = is.read(buf)) >= 0) {
			tmpData.write(buf, 0, len);
		}
		byte[] allBytes = tmpData.toByteArray();
		tmpData.reset();

		Map<byte[], String> boms = new LinkedHashMap<byte[], String>();
		boms.put(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF }, "UTF-8");
		boms.put(new byte[] { (byte) 0xFF, (byte) 0xFE }, "UTF-16LE");
		boms.put(new byte[] { (byte) 0xFE, (byte) 0xFF }, "UTF-16BE");

		String encoding = null;
		byte[] bom = null;

		for (Map.Entry<byte[], String> entry : boms.entrySet()) {
			byte[] tmpBom = entry.getKey();
			byte[] firstBytes = Arrays.copyOfRange(allBytes, 0, tmpBom.length);
			if (Arrays.equals(tmpBom, firstBytes)) {
				encoding = entry.getValue();
				bom = tmpBom;
				break;
			}
		}

		if (encoding != null) {
			// This may throw CharacterCodingException.
			CharsetDecoder decoder = Charset.forName(encoding).newDecoder();
			ByteBuffer byteBuffer = ByteBuffer.wrap(allBytes, bom.length, allBytes.length - bom.length);
			result = decoder.decode(byteBuffer).toString();
			allBytes = null; // GC hint.
		}
		else {
			ByteBuffer byteBuffer = ByteBuffer.wrap(allBytes);

			Map<String, Exception> errorMap = new LinkedHashMap<String, Exception>();
			for (String guess : new String[] { "UTF-8", "windows-1252" }) {
				try {
					byteBuffer.rewind();
					byteBuffer.limit(allBytes.length);
					CharsetDecoder decoder = Charset.forName(guess).newDecoder();
					result = decoder.decode(byteBuffer).toString();
					encoding = guess;
					break;
				} catch (CharacterCodingException e) {
					errorMap.put(guess, e);
				}
			}
			if (encoding == null) {
				// All guesses failed!?
				String msg = String.format("Could not guess encoding for %s.", (description != null ? "\"" + description + "\"" : "a file"));
				for (Map.Entry<String, Exception> entry : errorMap.entrySet()) {
					msg += String.format("\nFailed to decode as %s: %s", entry.getKey(), entry.getValue());
				}
				throw new IOException(msg);
			}
			allBytes = null; // GC hint.
		}

		// Determine the original line endings.
		int eol = DecodeResult.EOL_NONE;
		Matcher m = Pattern.compile("(\r(?!\n))|((?<!\r)\n)|(\r\n)").matcher(result);
		if (m.find()) {
			if (m.group(3) != null)
				eol = DecodeResult.EOL_CRLF;
			else if (m.group(2) != null)
				eol = DecodeResult.EOL_LF;
			else if (m.group(1) != null)
				eol = DecodeResult.EOL_CR;
		}

		result = result.replaceAll("\r(?!\n)|\r\n", "\n");
		return new DecodeResult(result, encoding, eol, bom);
	}

	/**
	 * A holder for results from decodeText().
	 * 
	 * text - The decoded string.
	 * encoding - The encoding used.
	 * eol - A constant describing the original line endings.
	 * bom - The BOM bytes found, or null.
	 * 
	 * @author Vhati
	 */
	public static class DecodeResult {
		public static final int EOL_NONE = 0;
		public static final int EOL_CRLF = 1;
		public static final int EOL_LF = 2;
		public static final int EOL_CR = 3;

		public final String text;
		public final String encoding;
		public final int eol;
		public final byte[] bom;

		public DecodeResult(String text, String encoding, int eol, byte[] bom) {
			this.text = text;
			this.encoding = encoding;
			this.eol = eol;
			this.bom = bom;
		}

		public String getEOLName() {
			if (eol == EOL_CRLF)
				return "CR-LF";
			if (eol == EOL_LF)
				return "LF";
			if (eol == EOL_CR)
				return "CR";
			return "None";
		}
	}
}
